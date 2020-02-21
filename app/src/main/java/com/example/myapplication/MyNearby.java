package com.example.myapplication;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.gioUtil.Floor;
import com.example.myapplication.gioUtil.Mina;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MyNearby {

    private static final String TAG  = "NEARBY";
    private static final String TAG1 = "PAYLOAD";

    private ConnectionsClient connectionsClient;
    private static final Strategy STRATEGY = Strategy.P2P_STAR;
    private static final String SERVICE_ID = "it.unive.dais.nearby.apps.SERVICE_ID";

    private String deviceA = "EV3MCLOVIN";
    private String deviceB = "GroundStation";
    private String deviceBEndpointId;

    private String chiave, id;
    public String MyStop = id+"STOP";
    public String AllStop = "0STOP";
    public String MyResume = id+"START";
    public String AllResume = "0START";
    public String coordinata = "Coordinate obiettivo";
    public String coordinateRecupero = "Coordinate recupero";
    public String KEY = "abcdefgh";

    public static List<String> listCoordMines = new ArrayList<>();
    public static List<String> MyMex = new ArrayList<>();
    public static Queue<String> MyMotionStop = new ConcurrentLinkedQueue<>();
    //public static List<Pair> mineList = new ArrayList<>();

    /**********************************************************************************************/

    public MyNearby(ConnectionsClient cc, String id){
        connectionsClient = cc;
        this.id = id;
    }

    /** Step 1: Advertise and Discover ************************************************************/

    public void startDiscovery() {
        Log.e(TAG,"discovery");
        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(STRATEGY).build();
        connectionsClient.startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions);
    }

    /** Step 2: Manage Connection *****************************************************************/

    // callbacks for finding other devices
    public final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {

                @Override
                public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo info){
                    Log.e(TAG, "onEndpointFound: endpoint found");
                    connectionsClient.requestConnection(deviceA, endpointId, connectionLifecycleCallback);
                }

                @Override
                public void onEndpointLost(@NonNull String endpointId) {
                    Log.e(TAG, "onEndpointLost: endpoint lost");
                }
            };

    // callbacks for connections to other devices
    public final ConnectionLifecycleCallback connectionLifecycleCallback =

            new ConnectionLifecycleCallback() {

                @Override
                public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
                    Log.e(TAG,"onConnectionInitiated: accepting connection");
                    connectionsClient.acceptConnection(endpointId, payloadCallback);
                    deviceB = connectionInfo.getEndpointName();
                }

                @Override
                public void onConnectionResult(@NonNull String endpointId,@NonNull ConnectionResolution result) {
                    if (result.getStatus().isSuccess()) {
                        Log.e(TAG,"onConnectionResult: connection succesful");
                        connectionsClient.stopDiscovery();
                        connectionsClient.stopAdvertising();
                        deviceBEndpointId = endpointId;
                    }
                    else {
                        Log.e(TAG, "onConnectionResult: connection failed");
                    }
                }

                @Override
                public void onDisconnected(@NonNull String endpointId) {
                    Log.e(TAG, "onDisconnected: disconnected from the opponent");
                }
            };

    /** Step 3: Exchange Data *********************************************************************/

    public void convert(@NonNull Payload payload){
        String s = new String(payload.asBytes());
        String[] s2 = s.split(":");
        String[] s3 = s2[1].split(";");
        String x = s3[0];
        String y = s3[1];
        listCoordMines.add(x);
        listCoordMines.add(y);
    }

    public String convert2(@NonNull Payload payload){
        String x = new String(payload.asBytes());
        return x;
    }

    public String decrypt(Payload payload, String psw) throws Exception{
        byte[] bytes = payload.asBytes();
        SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), "DES");
        Cipher c = Cipher.getInstance("DES/ECB/ISO10126Padding");
        c.init(c.DECRYPT_MODE, key);
        byte[] plaintext = c.doFinal(bytes);
        String s = new String(plaintext);
        return s;
    }

    public Payload encrypt(String data, String psw) throws Exception{
        byte[] bytes = data.getBytes();
        SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), "DES");
        Cipher c = Cipher.getInstance("DES/ECB/ISO10126Padding");
        c.init(c.ENCRYPT_MODE, key);
        byte[] ciphertext = c.doFinal(bytes);
        Payload p = Payload.fromBytes(ciphertext);
        return p;
    }

    public void sendMyPayLoad(@NonNull String x) {
        MyMex.add(x);
        byte[] coordBytes = x.getBytes();
        Payload coordPayload = Payload.fromBytes(coordBytes);
        connectionsClient.sendPayload(deviceBEndpointId,coordPayload);
    }

    Long MyT;
    public void sendMyCryptoPayLoad(String x, String psw) throws Exception {
        Calendar calendar = Calendar.getInstance();
        Long t = calendar.getTimeInMillis();
        MyT = t;
        x = x+t.toString()+";";
        MyMex.add(x);
        Payload enc = encrypt(x,psw);
        connectionsClient.sendPayload(deviceBEndpointId,enc);
    }


    public void sendMyPayload2(@NonNull String x, String psw) throws Exception{
        if (x.contains("Benvenuto") || x.contains("Coordinate recupero")){
            Log.e(TAG1,"invio plaintext");
            sendMyPayLoad(x);
        }
        if (x.contains("Operazione in corso") || x.contains("Operazione annullata") || x.contains("Operazione completata")){
            Log.e(TAG1, "invio ciphertext");
            sendMyCryptoPayLoad(x,psw);
        }
    }

    public Long MyTime(){
        return MyT;
    }

    public void updateVal(String id){
        MyStop = id+"STOP";
        MyResume = id+"START";
    }

    public final PayloadCallback payloadCallback =
            new PayloadCallback() {

                @Override
                public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
                    Log.e(TAG1, "inizio trasferimento da "+endpointId + " a robot "+id);

                    String s = convert2(payload);
                    updateVal(id);

                    if (!MyMex.contains(s)) {

                        if (s.equals(MyStop)) {
                            Log.e(TAG1,s);
                            MyMotionStop.add(s);
                        }
                        else {
                            if (s.equals(AllStop)){
                                Log.e(TAG1,s);
                                MyMotionStop.add(s);
                            }
                            else {
                                if (s.equals(MyResume)) {
                                    Log.e(TAG1, s);
                                    MyMotionStop.add(s);
                                }
                                else {
                                    if (s.equals(AllResume)){
                                        Log.e(TAG1, s);
                                        MyMotionStop.add(s);
                                    }
                                    else {
                                        String[] z = s.split("S");
                                        if ((s.contains("STOP") || s.contains("START")) && z[0] != id) {
                                            Log.e(TAG1, "MotionStop per altri robot");
                                        }
                                        else {
                                            String ss[] = s.split(":");
                                            if (ss[0].equals(coordinata)) {
                                                convert(payload);
                                            }
                                            else {
                                                if (ss[0].equals(coordinateRecupero)) {
                                                    convert(payload);
                                                }
                                                else {
                                                    //if (s.equals("Benvenuto sono Pippo")){
                                                    if (s.contains("Benvenuto")){

                                                    }
                                                    else {
                                                        if (!MyMex.contains(s)) {
                                                            Log.e(TAG1, "testo cifrato: " + s);
                                                            String dec2 = null;
                                                            try {
                                                                dec2 = decrypt(payload, chiave);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                            Log.e(TAG1, "testo decifrato: " + dec2);

                                                            String[] s2 = dec2.split(":");
                                                            String[] s3 = s2[1].split(";");
                                                            String x = s3[0];
                                                            String y = s3[1];
                                                            String t = s3[2]; //salvo in timestamp

                                                            Log.e(TAG1,x+";"+y+";"+t);

                                                            /*if (dec2.contains("Operazione in corso")){
                                                                Floor.OnFloorPosition posMina = new Floor.OnFloorPosition(Integer.valueOf(String.valueOf(x)), Integer.valueOf(String.valueOf(y)));
                                                                Mina m2 = new Mina(posMina);
                                                                Long t2 = Long.parseLong(t);
                                                                Pair p = new Pair(m2,t2);
                                                                mineList.add(p);
                                                            }*/

                                                            if (!listCoordMines.contains(x))
                                                                listCoordMines.add(x);

                                                            if (!listCoordMines.contains(y))
                                                                listCoordMines.add(y);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        Log.e(TAG1,"MyMex ritornato");
                    }

                    Log.e("MotionStop ====>",MyMotionStop.toString());
                    Log.e("Mine ==========>",listCoordMines.toString());

                }

                @Override
                public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate update) {
                    if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) { //entire payload has been received
                        //Log.e(TAG1, "trasferimento completato da "+endpointId);
                    }
                    else {
                        if (update.getStatus() == PayloadTransferUpdate.Status.FAILURE) {
                            Log.e(TAG1, "trasferimento fallito da "+endpointId);
                        }
                    }
                }
            };

    public List<String> getMines(){
        return listCoordMines;
    }

    public List<Floor.OnFloorPosition> getMines2(){
        return galfList;
    }

    /*public List<Pair> getMines3(){
        return mineList;
    }*/

    List<Floor.OnFloorPosition> galfList;

    public List<Floor.OnFloorPosition> convertList(List<String> l){
        galfList = new ArrayList<>();
        for(int i=0;i<l.size();i+=2){
            Floor.OnFloorPosition pos = new Floor.OnFloorPosition(new Integer(String.valueOf(l.get(i))),new Integer(String.valueOf(l.get(i+1))));
            galfList.add(pos);
        }
        return galfList;
    }

}
