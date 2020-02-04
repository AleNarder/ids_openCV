package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.nearby.Nearby;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import it.unive.dais.legodroid.lib.EV3;
import it.unive.dais.legodroid.lib.comm.BluetoothConnection;
import it.unive.dais.legodroid.lib.gioUtil.Floor;
import it.unive.dais.legodroid.lib.gioUtil.FloorMaster;
import it.unive.dais.legodroid.lib.gioUtil.PrimaProva;
import it.unive.dais.legodroid.lib.gioUtil.SensorMaster;
import it.unive.dais.legodroid.lib.gioUtil.TachoMaster;
import it.unive.dais.legodroid.lib.gioUtil.Test;
import it.unive.dais.legodroid.lib.plugs.GyroSensor;
import it.unive.dais.legodroid.lib.plugs.LightSensor;
import it.unive.dais.legodroid.lib.plugs.TachoMotor;
import it.unive.dais.legodroid.lib.plugs.UltrasonicSensor;
import it.unive.dais.legodroid.lib.util.Prelude;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FirstTryActivity extends AppCompatActivity {

    /**** CIAO FISTA**/
    /*** NEARBY VAR**************************************************************************************************************/

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


    public String packageName = FirstTryActivity.PACKAGE_NAME;


    /*******************************************************************************************************************************/

    TachoMotor motorA, motorD, motorC;
    TachoMaster tachoMaster;
    FloorMaster floorMaster;
    Floor floor;
    EV3 ev3;
    UltrasonicSensor ultra;
    GyroSensor gyro;

    SensorMaster sensorMaster;

    LinearLayout ll;

    int cnt = 0, x = -1, y = -1;
//    EditText et1, et2;

    Integer n, m;
    public static String PACKAGE_NAME;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_try);
        //EV3 ev3 =getIntent().getExtras().getSerializable("EV3MCLOVIN"); /**TODO**/

        Button startButtonFirst = findViewById(R.id.startButtonFirst);
        Button stopButtonFirst = findViewById(R.id.stopButtonFirst);
        Button discButton = findViewById(R.id.discoveryButton);

        et1 = findViewById(R.id.editText1);
        et2 = findViewById(R.id.editText2);

        PACKAGE_NAME = getApplicationContext().getPackageName();

        connectionsClient = Nearby.getConnectionsClient(this);

        if (connectionsClient != null)
            Toast.makeText(this, "ERRORE", Toast.LENGTH_SHORT).show();


        discButton.setOnClickListener(v -> startDiscovery());
        //nearby.startDiscovery();

        /*try {
            BluetoothConnection.BluetoothChannel ch = new BluetoothConnection("EV3MCLOVIN").connect(); // replace with your own brick name
            ev3 = new EV3(ch);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("FIRST TRY", "CANNOT connect to the fuckin lego");
        }

        startButtonFirst.setOnClickListener(v -> {
            setContentView(R.layout.activity_map);

            Button stopButton2 = findViewById(R.id.stopButton2);
            stopButton2.setOnClickListener(v2 -> ev3.cancel());

            ll = findViewById(R.id.linearlayout0);

            String s1 = et1.getText().toString();
            String s2 = et2.getText().toString();
            n = new Integer(s1);
            m = new Integer(s2);

            creaMap(n, m, x, y);

            Prelude.trap(() -> ev3.run(this::ev3Task3));



        });


        stopButtonFirst.setOnClickListener(v -> ev3.cancel());
        */
    }



    /**************************************************************************************************************************/
    public void creaMap(int n, int m, int x, int y) {
        for (int i = 0; i < n; i++)
            addButton(i, m, x, y);
    }

    public void addButton(int n, int m, int x, int y) {
        LinearLayout ll2 = new LinearLayout(this);
        ll2.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams ll_params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll2.setLayoutParams(ll_params);
        ll.addView(ll2);

        for (int i = 0; i < m; i++) {
            Button btn = new Button(this);

            LinearLayout.LayoutParams b_params = new LinearLayout.LayoutParams(0, 100, 1);
            btn.setLayoutParams(b_params);
            ll2.addView(btn);
            btn.setId(cnt);
            cnt++;

            if (n == x && i == y)
                btn.setBackgroundColor(Color.RED);

            final int x2 = i;
            btn.setOnClickListener(v -> Toast.makeText(FirstTryActivity.this, "[" + n + "," + x2 + "]", Toast.LENGTH_LONG).show());
        }
    }

    public void setColorButton(int x, int y, Button btn) {
        int cnt = 0;
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                cnt++;
            }
        }
        //btn.findViewById(R.id.cnt);
        btn.setBackgroundColor(Color.RED);
    }

    /**************************************************************************************************************************/
    /** Step 1: Advertise and Discover ******************************************************************************************************/

    public void startAdvertising() {
        Log.e(TAG,"advertising");
        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(STRATEGY).build();
        connectionsClient.startAdvertising(deviceA, packageName, connectionLifecycleCallback, advertisingOptions);
    }

    public void startDiscovery() {
        Log.e(TAG,"discovery");
        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(STRATEGY).build();
        connectionsClient.startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions); //per comunicare con GroundStaion

       // connectionsClient.startDiscovery(packageName, endpointDiscoveryCallback, discoveryOptions); //per comunicare con altri
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
                        listCoordMines2.add(s);
                    }
                    else {
                        if (s.equals("0STOP")){
                            Log.e(TAG,"b");
                            listCoordMines2.add(s);
                        }
                        else {
                            if (s.equals("1STOP")){
                                Log.e(TAG,"c");
                                listCoordMines2.add(s);
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
                                listCoordMines2.add(x);
                                listCoordMines2.add(y);
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




    public List<String> getMines(){
        return listCoordMines2;
    }


    /******************************************************************************************************/

    public void ev3Task5(EV3.Api api) {
        motorA = api.getTachoMotor(EV3.OutputPort.A);
        motorD = api.getTachoMotor(EV3.OutputPort.D);
        motorC = api.getTachoMotor(EV3.OutputPort.C);
        ultra = api.getUltrasonicSensor(EV3.InputPort._1);
        gyro = api.getGyroSensor(EV3.InputPort._3);

        sensorMaster = new SensorMaster(ultra, gyro);

        tachoMaster = new TachoMaster(motorD, motorA, motorC);
        boolean motorrs_going = false;

        try {
            while (!sensorMaster.objectInProximity()) {
                if (!motorrs_going) {
                    motorA.setSpeed(20);
                    motorD.setSpeed(20);
                    motorA.start();
                    motorD.start();
                    motorrs_going = true;
                }
            }
            motorD.stop();
            motorA.stop();
            motorC.setSpeed(-20);
            motorC.start();
            Thread.sleep(4000);
            motorC.stop();
            motorrs_going = false;
            motorC.setSpeed(0);
            motorC.start();
            motorD.setStepSpeed(20, 0, 500, 0, true);
            motorA.setStepSpeed(20, 0, 500, 0, true);
            motorA.waitCompletion();
            motorD.waitCompletion();
            motorD.setStepSpeed(20, 0, 500, 0, true);
            motorA.setStepSpeed(-20, 0, 500, 0, true);
            Thread.sleep(5000);
            motorC.stop();


        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                motorD.stop();
                motorA.stop();
                motorC.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void gyroTest(EV3.Api api) {
        motorA = api.getTachoMotor(EV3.OutputPort.A);
        motorD = api.getTachoMotor(EV3.OutputPort.D);
        motorC = api.getTachoMotor(EV3.OutputPort.C);
        ultra = api.getUltrasonicSensor(EV3.InputPort._1);
        gyro = api.getGyroSensor(EV3.InputPort._3);

        sensorMaster = new SensorMaster(ultra, gyro);

        tachoMaster = new TachoMaster(motorD, motorA, motorC);

        float init_angle, final_angle;

        boolean motors_going = false;
        try {
            while (!ev3.isCancelled()) {
                init_angle = sensorMaster.getGyroAngle();
                while ((sensorMaster.getGyroAngle() - init_angle) < 82) {
                    if (!motors_going) {
                        motorA.setSpeed(10);
                        motorD.setSpeed(-10);
                        motorD.start();
                        motorA.start();
                        motors_going = true;
                    }
                }
                Prelude.trap(() -> {
                    motorA.stop();
                    motorD.stop();
                });
                motors_going = false;
                Log.e("SENSOR MASTER = ", "ANGOLO = " + sensorMaster.getGyroAngle());
                Log.e("SENSOR MASTER = ", "DIFFERENZA ANGOLI = " + Math.round(sensorMaster.getGyroAngle() - init_angle));
                Thread.sleep(5000);
            }
        } catch (Exception e) {
        } finally {
            Prelude.trap(() -> {
                motorA.stop();
                motorD.stop();
            });
        }
    }

    private void ev3Task3(EV3.Api api) {
        motorA = api.getTachoMotor(EV3.OutputPort.A);
        motorD = api.getTachoMotor(EV3.OutputPort.D);
        motorC = api.getTachoMotor(EV3.OutputPort.C);
        ultra = api.getUltrasonicSensor(EV3.InputPort._1);
        gyro = api.getGyroSensor(EV3.InputPort._3);            //TODO: testare se i motori resattano il loro contatore cancellando EV3 task

        sensorMaster = new SensorMaster(ultra, gyro);

        tachoMaster = new TachoMaster(motorA, motorD, motorC);

        floor = new Floor(n,m, 29.5f ,29.5f); //TODO: variabile globale??? utile se devo salvare lo stato del robot sul campo

        floorMaster = new FloorMaster(floor);

        List<Floor.OnFloorPosition> minePosition = new ArrayList<>(); //TODO /*da mettere globale??*/

        PrimaProva test = new PrimaProva(getApplicationContext(), tachoMaster , floorMaster  , sensorMaster);
        int mine = 3 ;
        try {
            while (!ev3.isCancelled() && mine>0 ) {
                test.findMine();
                Floor.OnFloorPosition pos = test.takeMine() ;

                x = pos.getRow();
                y = pos.getCol();

                runOnUiThread(() -> {
                    Log.e("====>", "aggiormo mappa");
                    ll.removeAllViews();
                    creaMap(floor.getWidth(),floor.getHeight(),x,y);
                });

                test.goToStartPosition();
                test.releaseMine();
                mine--;

            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FloorMaster.AllPositionVisited allPositionVisited) {
            allPositionVisited.printStackTrace();
            Log.d("PRIMA PROVA : " , "Tutto il campo è stato visitato");
        } finally {
            Prelude.trap(()->tachoMaster.stopMotors());
        }

    }
}
