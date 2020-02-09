package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
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
import java.util.Calendar;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import it.unive.dais.legodroid.lib.EV3;

import com.example.myapplication.gioUtil.Floor;
import com.example.myapplication.gioUtil.FloorMaster;
import com.example.myapplication.gioUtil.PrimaProva;
import com.example.myapplication.gioUtil.SensorMaster;
import com.example.myapplication.gioUtil.TachoMaster;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import it.unive.dais.legodroid.lib.comm.BluetoothConnection;
import it.unive.dais.legodroid.lib.plugs.GyroSensor;
import it.unive.dais.legodroid.lib.plugs.TachoMotor;
import it.unive.dais.legodroid.lib.plugs.UltrasonicSensor;
import it.unive.dais.legodroid.lib.util.Prelude;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FirstTryActivity extends AppCompatActivity {

    /*** NEARBY VAR**************************************************************************************************************/

    /*private static final String TAG  = "ProvaCryptoNearby";
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


    private String welcome = "Benvenuto sono "+deviceA;
    private String target = "Coordinate obiettivo:Coordinata_X;Coordinata_Y";

        private String takingMine = "Operazione in corso:x;y;";
    private String abortMine  = "Operazione annullata:x;y;";
    private String takeMine   = "Operazione completata:x;y;";

    private static final String SERVICE_ID = "it.unive.dais.nearby.apps.SERVICE_ID";

    private String KEY = "abcdefgh";


    public String packageName = FirstTryActivity.PACKAGE_NAME;*/


    private static final String TAG  = "NEARBY";
    private static final String TAG1 = "PAYLOAD";

    private static final Strategy STRATEGY = Strategy.P2P_STAR;
    private ConnectionsClient connectionsClient;

    private String deviceA = "EV3MCLOVIN";
    private String deviceB = "GroundStation";
    private String deviceBEndpointId;
    private EditText et1, et2, et3, et4;
    private TextView tv1, tv2;
    private String PayloadSent, chiave, id;

    public static List<String> listCoordMines2 = new ArrayList<>();
    public static List<String> MyMex = new ArrayList<>();
    public static Queue<String> MyMotionStop = new ConcurrentLinkedQueue<>();

    private static final String SERVICE_ID = "it.unive.dais.nearby.apps.SERVICE_ID";

    public static String PACKAGE_NAME;

    public String packageName = FirstTryActivity.PACKAGE_NAME;

    /** PLAINTEXT */
    private String welcome = "Benvenuto sono "+deviceA;
    private String target = "Coordinate obiettivo:Coordinata_X;Coordinata_Y";

    /** CIPHERTEXT */
    private String takingMine = "Operazione in corso:x;y;";
    private String abortMine  = "Operazione annullata:x;y;";
    private String takeMine   = "Operazione completata:x;y;";

    private String KEY = "abcdefgh";

    MyCameraListener cameraListener;



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

    Integer n=7, m=7;
    //public static String PACKAGE_NAME;

    private CameraBridgeViewBase mOpenCvCameraView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_try);

        Button startButtonFirst = findViewById(R.id.startButtonFirst);
        Button stopButtonFirst = findViewById(R.id.stopButtonFirst);
        Button discButton = findViewById(R.id.discoveryButton);
        Button disconnectButton = findViewById(R.id.disconnectButton);
        Button showMex = findViewById(R.id.button5);
        Button sendButton = findViewById(R.id.sendButton);

        et1 = findViewById(R.id.editText1);
        et2 = findViewById(R.id.editText2);
        et3 = findViewById(R.id.key);
        et4 = findViewById(R.id.idRobot);

        tv1 = findViewById(R.id.statoConnesione);
        tv2 = findViewById(R.id.messages);

        //PACKAGE_NAME = getApplicationContext().getPackageName(); //non mi serve più

        connectionsClient = Nearby.getConnectionsClient(this);

        if (connectionsClient == null)
            Toast.makeText(this, "ERRORE", Toast.LENGTH_SHORT).show();


        discButton.setOnClickListener(v -> {
            chiave = et3.getText().toString();
            id = et4.getText().toString();
            Log.e(TAG, "sono il robot: "+id);
            Log.e(TAG, "chiave: " +chiave);
            startDiscovery();
        });

        showMex.setOnClickListener(v -> tv2.setText(listCoordMines2.toString()));

        disconnectButton.setOnClickListener(v -> {
            connectionsClient.stopDiscovery();
            connectionsClient.stopAdvertising();
            connectionsClient.stopAllEndpoints();
            tv1.setText("non connesso");
            tv1.setTextColor(Color.RED);
        });

        sendButton.setOnClickListener(v ->
        {
            sendMyPayLoad(welcome);
            /*try{
               sendMyCryptoPayLoad("Operazione in corso:4;8;", chiave);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        });




        if (!OpenCVLoader.initDebug()) {
            Log.e("AndroidIngSwOpenCV", "Unable to load OpenCV");
        } else {
            Log.d("AndroidIngSwOpenCV", "OpenCV loaded");
        }


/*
        PACKAGE_NAME = getApplicationContext().getPackageName();

        connectionsClient = Nearby.getConnectionsClient(this);

        if (connectionsClient != null)
            Toast.makeText(this, "ERRORE", Toast.LENGTH_SHORT).show();


        discButton.setOnClickListener(v -> startDiscovery());*/
        //nearby.startDiscovery();
        mOpenCvCameraView = findViewById(R.id.OpenCvView);
        /*mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setMaxFrameSize(640, 480);*/
        try {
            BluetoothConnection.BluetoothChannel ch = new BluetoothConnection("EV3MCLOVIN").connect(); // replace with your own brick name
            ev3 = new EV3(ch);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("FIRST TRY", "CANNOT connect to the fuckin lego");
        }
        startButtonFirst.setOnClickListener(v -> {
            /*setContentView(R.layout.activity_map);

            Button stopButton2 = findViewById(R.id.stopButton2);
            stopButton2.setOnClickListener(v2 -> ev3.cancel());

            ll = findViewById(R.id.linearlayout0);*/
            mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
            mOpenCvCameraView.setMaxFrameSize(320, 240);
            cameraListener = new MyCameraListener();
            mOpenCvCameraView.setCvCameraViewListener(cameraListener);

            mOpenCvCameraView.enableView();

            Log.e("FIRST :","Camera active");

            String s1 = et1.getText().toString();
            String s2 = et2.getText().toString();
            //n = new Integer(s1);
           // m = new Integer(s2);

            //creaMap(n, m, x, y);

            Prelude.trap(() -> ev3.run(this::ev3Task3));



        });


        stopButtonFirst.setOnClickListener(v -> ev3.cancel());





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
        tv1.setText("discovery...");
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
                    Log.e(TAG, "onEndpointFound: endpoint found: "+endpointId);
                    tv1.setText("endpoint found: "+endpointId);
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
                        Log.e(TAG,"onConnectionResult: connection succesful");
                        tv1.setText("connesso a "+ endpointId);
                        tv1.setTextColor(Color.GREEN);
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
        MyMex.add(x) ;
        byte[] coordBytes = x.getBytes(UTF_8);
        Payload coordPayload = Payload.fromBytes(coordBytes);
        connectionsClient.sendPayload(deviceBEndpointId,coordPayload);
    }

    public String MyStop = id+"STOP";
    public String AllStop = "0STOP";
    public String MyResume = id+"START";
    public String AllResume = "0START";
    public String coordinata = "Coordinate obiettivo";

    public String dec = null;

    public void updateVal(String id){
        MyStop = id+"STOP";
        MyResume = id+"START";
    }

    public final PayloadCallback payloadCallback =
            new PayloadCallback() {

                @Override
                public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
                    Log.e(TAG1, "inizio trasferimento da "+endpointId + "a robot "+id);

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
                                        String ss[] = s.split(":");
                                        if (ss[0].equals(coordinata)) {
                                            convert(payload);
                                        }
                                        else{
                                            Log.e(TAG1, "testo cifrato: " + s);

                                            try {
                                                dec = decrypt(payload, chiave);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            Log.e(TAG, "testo decifrato: " + dec);

                                            String[] s2 = dec.split(":");
                                            String[] s3 = s2[1].split(";");
                                            String x = s3[0];
                                            String y = s3[1];

                                            listCoordMines2.add(x);
                                            listCoordMines2.add(y);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        Log.e(TAG1,"MyMex ritornato");
                    }

                    /** caso base */
                    //convert(payload);
                    //Log.e("====>",listCoordMines.toString());

                    /** test */


                    /*if (!MyMex.contains(s)) {

                        if (s.equals("Benvenuto sono Pippo")) {
                            Log.e(TAG, "a");
                            listCoordMines.add(s);
                        }
                        else {
                            if (s.equals("0STOP")) {
                                Log.e(TAG, "b");
                                listCoordMines.add(s);
                            }
                            else {
                                if (s.equals("1STOP")) {
                                    Log.e(TAG, "c");
                                    listCoordMines.add(s);
                                }
                                else {
                                    if (s.equals("2STOP")) {
                                        Log.e(TAG, "qua");
                                        listCoordMines.add(s);
                                    }
                                    else {
                                        if (s.equals("Coordinate recupero:3;6;")) {
                                            Log.e(TAG, "d");
                                            convert(payload);
                                        }
                                        else {
                                            Log.e(TAG, "testo cifrato: " + s);

                                            dec = null;
                                            try {
                                                dec = decrypt(payload, "abcdefgh");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            Log.e(TAG, "testo decifrato: " + dec);

                                            String[] s2 = dec.split(":");
                                            String[] s3 = s2[1].split(";");
                                            String x = s3[0];
                                            String y = s3[1];

                                            listCoordMines.add(x);
                                            listCoordMines.add(y);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else {
                        Log.e(TAG1,"MyMex ritornato");
                    }*/

                    Log.e("MotionStop ====>",MyMotionStop.toString());
                    Log.e("Mine ====>",listCoordMines2.toString());

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
        Calendar calendar = Calendar.getInstance();
        Long t = calendar.getTimeInMillis();
        //x += t;
        MyMex.add(x);
        String enc = encrypt(x,psw);
        //String enc = encrypt(x,"aaaaaaaa");
        //Toast.makeText(MainActivity.this, enc, Toast.LENGTH_SHORT).show();
        byte[] coordBytes = enc.getBytes(UTF_8);
        Payload coordPayload = Payload.fromBytes(coordBytes);
        connectionsClient.sendPayload(deviceBEndpointId,coordPayload);
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
                        dec = decrypt(payload,"aaaaaaaa");
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

    public String decrypt(Payload payload, String psw) throws Exception{
        byte[] bytes = payload.asBytes();
        SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), "DES");
        Cipher c = Cipher.getInstance("DES/ECB/ISO10126Padding");
        c.init(c.DECRYPT_MODE, key);
        byte[] plaintext = c.doFinal(bytes);
        String s = new String(plaintext);
        return s;
    }

    public String encrypt(String data, String psw) throws Exception{
        SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), "DES");
        Cipher c = Cipher.getInstance("DES/ECB/ISO10126Padding");
        c.init(c.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes(UTF_8));
        String encryptedValue = new String(encVal, UTF_8);
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

        PrimaProva test = new PrimaProva(getApplicationContext(), tachoMaster , floorMaster  , sensorMaster ,  cameraListener);
        int mine = 3 ;
        try {
            while (!ev3.isCancelled() && mine>0 ) {
                test.findMine();
                Floor.OnFloorPosition pos = test.takeMine() ;

                x = pos.getRow();
                y = pos.getCol();

                /*runOnUiThread(() -> {
                    Log.e("====>", "aggiormo mappa");
                    ll.removeAllViews();
                    creaMap(floor.getWidth(),floor.getHeight(),x,y);
                });*/

                test.goToStartPosition();
                test.releaseMine();
                mine--;

            }
        }
        catch(Exception e ) {
            Log.e("BOH:","errore");
            e.printStackTrace();
        }
       /* catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FloorMaster.AllPositionVisited allPositionVisited) {
            allPositionVisited.printStackTrace();
            Log.d("PRIMA PROVA : " , "Tutto il campo è stato visitato");
        } */finally {
            Prelude.trap(()->tachoMaster.stopMotors());
        }

    }


    public static class MyCameraListener implements CameraBridgeViewBase.CvCameraViewListener2{
        double inclination = Double.NaN;
        double prev_inclination=Double.NaN;
        public double getInclination(){return inclination;}
        @Override
        public void onCameraViewStarted(int width, int height) {

        }

        @Override
        public void onCameraViewStopped() {

        }

        @Override
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
            Mat frame = inputFrame.rgba();
            LineFinder lineFinder = new LineFinder(frame, true);
            lineFinder.setThreshold(120, 240);
            lineFinder.setOrientation("landscape");
            if(Double.isNaN(inclination) && !Double.isNaN(prev_inclination))
                inclination = prev_inclination;
            else {
                inclination = lineFinder.findLine();
                prev_inclination = inclination;
            }

            if (!Double.isNaN(inclination)) {
               // Log.e("Line inclination", String.valueOf(inclination));
            }
            return frame;
        }
    }
}
