package com.example.myapplication;

import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Nearby {
    private static final String TAG  = "ProvaCryptoNearby";
    private static final String TAG1 = "PAYLOAD";

    private static final Strategy STRATEGY = Strategy.P2P_STAR;
    private ConnectionsClient connectionsClient;

    private String deviceA = "EV3MCLOVIN";
    private String deviceB = "GroundStation";
    private String deviceBEndpointId;

    private Button findButton, sendButton, stopButton, MinesButton, discButton, adButton;

    public static List<String> listCoordMines2 = new ArrayList<>();

    String PayloadSent;

    EditText et1, et2;
    String coordinata, chiave;

    TextView tv1;

    /** PLAINTEXT */
    private String welcome = "Benvenuto sono "+deviceA;
    private String target = "Coordinate obiettivo:Coordinata_X;Coordinata_Y";

    /** CIPHERTEXT */
    private String takingMine = "Operazione in corso:x;y;";
    private String abortMine  = "Operazione annullata:x;y;";
    private String takeMine   = "Operazione completata:x;y;";

    private static final String SERVICE_ID = "it.unive.dais.nearby.apps.SERVICE_ID";

    private String KEY = "abcdefgh";


    //public String packageName = FirstTryActivity.PACKAGE_NAME;



    public Nearby(ConnectionsClient cc){
        connectionsClient = cc;
    }
    /** Step 1: Advertise and Discover ******************************************************************************************************/

    public void startAdvertising() {
        Log.e(TAG,"advertising");
        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(STRATEGY).build();
        //connectionsClient.startAdvertising(deviceA, packageName, connectionLifecycleCallback, advertisingOptions);
    }

    public void startDiscovery() {
        Log.e(TAG,"discovery");
        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(STRATEGY).build();
        connectionsClient.startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions); //per comunicare con GroundStaion

        //connectionsClient.startDiscovery(packageName, endpointDiscoveryCallback, discoveryOptions); //per comunicare con altri
    }

    /** Step 2: Manage Connection ***********************************************************************************************************/

    // callbacks for finding other devices
    public final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {

                @Override
                public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo info){
                    Log.e(TAG, "onEndpointFound: endpoint found");
                    Log.e(TAG, "endpointID: "+endpointId);

                    //inserire controllo per membri della squadre

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
                    //connectionsClient.acceptConnection(endpointId, CryptoPayloadCallback);
                    deviceB = connectionInfo.getEndpointName();
                }

                @Override
                public void onConnectionResult(@NonNull String endpointId,@NonNull ConnectionResolution result) {
                    if (result.getStatus().isSuccess()) {
                        //Toast.makeText(MainActivity.this,"CONNESSO",Toast.LENGTH_SHORT).show();
                        Log.e(TAG,"onConnectionResult: connection succesful");
                        connectionsClient.stopDiscovery();
                        connectionsClient.stopAdvertising();
                        deviceBEndpointId = endpointId;
                    }
                    else {
                        //Toast.makeText(MainActivity.this,"NON CONNESSO",Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onConnectionResult: connection failed");
                    }
                }

                @Override
                public void onDisconnected(@NonNull String endpointId) {
                    //Toast.makeText(MainActivity.this,"DISCONNESSO",Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onDisconnected: disconnected from the opponent");
                }
            };
    /** Step 3: Exchange Data ***************************************************************************************************************/

    /**
     * DeviceA -> sendMyPayload
     * DeviceB -> payloadCallback (callbacks for receiving payloads)
     */

    /** COMUNICAZIONE PASSIVA */

    public void sendMyPayLoad(@NonNull String x) {
        byte[] coordBytes = x.getBytes(UTF_8);
        Payload coordPayload = Payload.fromBytes(coordBytes);
        connectionsClient.sendPayload(deviceBEndpointId,coordPayload);
    }

    public final PayloadCallback payloadCallback =
            new PayloadCallback() {

                @Override
                public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
                    Log.e(TAG1, "inizio trasferimento da "+endpointId);

                    /** caso base */
                    convert(payload);
                    Log.e("====>",listCoordMines2.toString());

                    /** test */

                    /*String s = convert2(payload);

                    if (s.equals("Benvenuto sono Pippo")){
                        Log.e(TAG,"a");
                        listCoordMines.add(s);
                    }
                    else {
                        if (s.equals("0STOP")){
                            Log.e(TAG,"b");
                            listCoordMines.add(s);
                        }
                        else {
                            if (s.equals("1STOP")){
                                Log.e(TAG,"c");
                                listCoordMines.add(s);
                            }
                            if (s.equals("Coordinate recupero:3;6;")){
                                Log.e(TAG,"d");
                                convert(payload);
                            }
                            else {
                                Log.e(TAG,"testo cifrato: "+s);

                                String dec = null;
                                try {
                                    dec = decrypt(s,"abcdefgh");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Log.e(TAG,"testo decifrato: "+dec);

                                String[] s2 = dec.split(";");
                                String x = s2[0];
                                String y = s2[1];
                                listCoordMines.add(x);
                                listCoordMines.add(y);
                            }
                        }
                    }*/

                }

                @Override
                public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate update) {
                    if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) { //entire payload has been received
                        Log.e(TAG1, "trasferimento completato da "+endpointId);
                    }
                    else {
                        if (update.getStatus() == PayloadTransferUpdate.Status.FAILURE) {
                            Log.e(TAG1, "trasferimento fallito da "+endpointId);
                        }
                    }
                }
            };

    public void convert(@NonNull Payload payload){
        PayloadSent = new String(payload.asBytes(),UTF_8);
        String[] s = PayloadSent.split(":");
        String[] s2 = s[1].split(";");
        String x = s2[0];
        String y = s2[1];
        listCoordMines2.add(x);
        listCoordMines2.add(y);
    }

    public String convert2(@NonNull Payload payload){
        String x = new String(payload.asBytes(),UTF_8);
        return x;
    }

    /** COMUNICAZIONE ATTIVA */

    public void sendMyCryptoPayLoad(String x, String psw) throws Exception {
        String enc = encrypt(x,psw);
        //String enc = encrypt(x,"aaaaaaaa");
        //Toast.makeText(MainActivity.this, enc, Toast.LENGTH_SHORT).show();
        byte[] coordBytes = enc.getBytes(UTF_8);
        Payload coordPayload = Payload.fromBytes(coordBytes);
        connectionsClient.sendPayload(deviceBEndpointId,coordPayload);

        //timestamp
        //Calendar calendar = Calendar.getInstance();
        //Long time_long = calendar.getTimeInMillis();
    }

    public final PayloadCallback CryptoPayloadCallback =
            new PayloadCallback() {

                @Override
                public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
                    Log.e(TAG1, "inizio trasferimento");
                    PayloadSent = new String(payload.asBytes(),UTF_8);
                    Log.e(TAG,"testo cifrato = "+PayloadSent);
                    String dec = null;
                    try {
                        dec = decrypt(PayloadSent,"aaaaaaaa");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String[] s = dec.split(":");
                    String[] s2 = s[1].split(";");
                    String x = s2[0];
                    String y = s2[1];
                    listCoordMines2.add(x);
                    listCoordMines2.add(y);
                    Log.e("====>",listCoordMines2.toString());
                }

                @Override
                public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate update) {
                    if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) { //entire payload has been received
                        Log.e(TAG1, "trasferimento completato");
                    }
                    else {
                        if (update.getStatus() == PayloadTransferUpdate.Status.FAILURE) {
                            Log.e(TAG1, "trasferimento fallito");
                        }
                    }
                }
            };

    public String decrypt(String outputString, String psw) throws Exception{

        /*DESKeySpec keySpec = new DESKeySpec(psw.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(keySpec);
        Cipher c = Cipher.getInstance("DES/ECB/ISO10126Padding");
        c.init(c.DECRYPT_MODE, key);*/

        SecretKeySpec key = new SecretKeySpec("abcdefgh".getBytes(), "DES");
        Cipher c = Cipher.getInstance("DES/ECB/ISO10126Padding");
        c.init(c.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.decode(outputString, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodedValue);
        String decryptValue = new String(decValue);
        return decryptValue;
    }

    public String encrypt(String data, String psw) throws Exception{
        //DESKeySpec keySpec = new DESKeySpec("aaaaaaaa".getBytes());
        DESKeySpec keySpec = new DESKeySpec(psw.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(keySpec);

        Cipher c = Cipher.getInstance("DES/ECB/ISO10126Padding");
        c.init(c.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;
    }


}
