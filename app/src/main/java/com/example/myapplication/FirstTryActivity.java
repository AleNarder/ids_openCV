package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.gioUtil.Mina;
import com.google.android.gms.nearby.Nearby;
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

public class FirstTryActivity extends AppCompatActivity {

    /*** NEARBY VAR **************************************************************************************************************/

    private static final String TAG  = "NEARBY";
    private static final String TAG1 = "PAYLOAD";

    private static final Strategy STRATEGY = Strategy.P2P_STAR;
    private ConnectionsClient connectionsClient;

    private String deviceA = "EV3MCLOVIN";
    private String deviceB = "GroundStation";
    private String deviceBEndpointId;
    private EditText et1, et2, et3, et4, et5, et6 , et7;
    private TextView tv1, tv2, tv3;
    private String chiave, id;
    private Button startButtonFirst, stopButtonFirst, discButton, disconnectButton, showMex, sendButton;
    public Integer posX, posY;

    public static List<String> listCoordMines = new ArrayList<>();
    public static List<String> MyMex = new ArrayList<>();
    public static Queue<String> MyMotionStop = new ConcurrentLinkedQueue<>();

    private static final String SERVICE_ID = "it.unive.dais.nearby.apps.SERVICE_ID";

    /** PLAINTEXT */
    private String welcome = "Benvenuto sono "+deviceA;

    /** CIPHERTEXT */
    public String takingMine = "Operazione in corso";
    public String abortMine  = "Operazione annullata";
    public String takeMine   = "Operazione completata";

    private String KEY = "abcdefgh";

    public String dec;

    public String MyStop = id+"STOP";
    public String AllStop = "0STOP";
    public String MyResume = id+"START";
    public String AllResume = "0START";
    public String coordinata = "Coordinate obiettivo";
    public String coordinateRecupero = "Coordinate recupero";

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
    Floor.Direction startDirection;
    int cnt = 0, x = -1, y = -1 , nMine;

    /** dimensione campo */
    Integer n=3, m=3;

    private CameraBridgeViewBase mOpenCvCameraView;

    ArrayList<Mina> mineList = new ArrayList<>();

    /*******************************************************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_try);

        startButtonFirst = findViewById(R.id.startButtonFirst);
        stopButtonFirst = findViewById(R.id.stopButtonFirst);
        discButton = findViewById(R.id.discoveryButton);
        disconnectButton = findViewById(R.id.disconnectButton);
        //showMex = findViewById(R.id.button5);
        sendButton = findViewById(R.id.sendButton);

        et1 = findViewById(R.id.editText1);
        et2 = findViewById(R.id.editText2);
        et3 = findViewById(R.id.key);
        et4 = findViewById(R.id.idRobot);
        et5 = findViewById(R.id.editText5);
        et6 = findViewById(R.id.editText6);
        et7 = findViewById(R.id.editText7);

        tv1 = findViewById(R.id.statoConnesione);
        //tv2 = findViewById(R.id.messages);
        tv3 = findViewById(R.id.statoRobot);

        connectionsClient = Nearby.getConnectionsClient(this);

        if (connectionsClient == null)
            Toast.makeText(this, "ERRORE", Toast.LENGTH_SHORT).show();

        discButton.setOnClickListener(v -> {
            chiave = et3.getText().toString();
            id = et4.getText().toString();
            startDiscovery();
        });

        /*showMex.setOnClickListener(v -> {
            tv2.setText(listCoordMines.toString());
        });*/

        disconnectButton.setOnClickListener(v -> {
            connectionsClient.stopDiscovery();
            connectionsClient.stopAdvertising();
            connectionsClient.stopAllEndpoints();
            tv1.setText("non connesso");
            tv1.setTextColor(Color.RED);
        });

        sendButton.setOnClickListener(v -> {
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


        /*mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setMaxFrameSize(640, 480);*/

        try {
            BluetoothConnection.BluetoothChannel ch = new BluetoothConnection("EV3MCLOVIN").connect();
            ev3 = new EV3(ch);
            tv3.setText("connesso a EV3MCLOVIN");
            tv3.setTextColor(Color.GREEN);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "NON CONNESSO A EV3", Toast.LENGTH_SHORT).show();
            Log.e("FIRST TRY", "CANNOT connect to the fuckin lego");
            tv3.setText("non connesso a EV3MCLOVIN");
            tv3.setTextColor(Color.RED);
        }

        startButtonFirst.setOnClickListener(v -> {


            /*Button stopButton2 = findViewById(R.id.stopButton2);
            stopButton2.setOnClickListener(v2 -> ev3.cancel());
            //findViewById(R.id.linearlayout0);*/
            //floor = new Floor(n,m, 29.5f ,29.5f);
            nMine = new Integer(String.valueOf(et7.getText()));
            String s1 = et1.getText().toString();
            String s2 = et2.getText().toString();
            posX = new Integer(String.valueOf(et5.getText()));
            posY = new Integer(String.valueOf(et6.getText()));

            n = new Integer(s1);
            m = new Integer(s2);

            Log.e("=============>", posX.toString()+" "+posY.toString()+" "+startDirection);

            floor = new Floor(n,m , 30.0f, 30.0f, posX,posY,startDirection);
            setContentView(R.layout.activity_camera);
            mOpenCvCameraView = findViewById(R.id.OpenCvView);
            mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
            mOpenCvCameraView.setMaxFrameSize(320, 240);
            cameraListener = new MyCameraListener();
            mOpenCvCameraView.setCvCameraViewListener(cameraListener);
            mOpenCvCameraView.enableView();

            Log.e("FIRST :","Camera active");


            //creaMap(n, m, x, y);

            Prelude.trap(() -> ev3.run(this::ev3Task3));

        });

        stopButtonFirst.setOnClickListener(v -> ev3.cancel());

        Spinner spin = findViewById(R.id.spinner);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(FirstTryActivity.this,parent.getSelectedItem().toString(),Toast.LENGTH_SHORT).show();
                String s = parent.getSelectedItem().toString();
                switch(s){
                    case "R+":
                        startDirection= Floor.Direction.VERTICAL_UP;
                        break;
                    case "R-":
                        startDirection= Floor.Direction.VERTICAL_DOWN;
                        break;
                    case "C+":
                        startDirection= Floor.Direction.HORIZONTAL_UP;
                        break;
                    case "C-":
                        startDirection= Floor.Direction.HORIZONTAL_DOWN;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    /**************************************************************************************************************************/

    public void creaMap(ArrayList<Mina> l, int n, int m){
        for(int i=0;i<n;i++){
            addButton(l, i, m);
        }
    }

    public void addButton(ArrayList<Mina> l, int n, int m){
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

            for(int j=0;j<l.size();j++) {

                int x = l.get(j).getPosition().getRow();
                int y = l.get(j).getPosition().getCol();
                String color = l.get(j).getColor();

                if (n == x && i == y) {
                    Log.e("========>","COLORO MINA");
                    switch (color) {
                        case "red":
                            btn.setBackgroundColor(Color.RED);
                            break;
                        case "yellow":
                            btn.setBackgroundColor(Color.YELLOW);
                            break;
                        case "blue":
                            btn.setBackgroundColor(Color.BLUE);
                            break;
                    }

                }
            }

            final int x2 = i;
            btn.setOnClickListener(v -> {
                Toast.makeText(FirstTryActivity.this, "["+n+","+x2+"]", Toast.LENGTH_LONG).show();
            });
        }
    }

    /***************************************************************************************************************************************
     *** NEARBY
     **************************************************************************************************************************************/

    /** Step 1: Advertise and Discover */

    public void startDiscovery() {
        Log.e(TAG,"discovery");
        tv1.setText("discovery...");
        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(STRATEGY).build();
        connectionsClient.startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions);
    }

    /** Step 2: Manage Connection */

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

                        //sendMyPayLoad(welcome);
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

    /** Step 3: Exchange Data */

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

    public void sendMyCryptoPayLoad(String x, String psw) throws Exception {
        Calendar calendar = Calendar.getInstance();
        Long t = calendar.getTimeInMillis();
        x = x+t.toString()+";";
        MyMex.add(x);
        Payload enc = encrypt(x,KEY);
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
                                ev3.cancel();
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
                                        Prelude.trap(() -> ev3.run(FirstTryActivity.this::ev3Task3));
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
                                                    if (s.equals("Benvenuto sono Pippo")){

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

                                                            Log.e(TAG1,x+" "+y);

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
                    Log.e("Mine ====>", listCoordMines.toString());

                }

                //chiamato quando invio qualcosa
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

    /**********************************************************************************************************************************/

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
        //floor = new Floor(n,m, 29.5f ,29.5f); //TODO: variabile globale??? utile se devo salvare lo stato del robot sul campo

        floorMaster = new FloorMaster(floor);

        List<Floor.OnFloorPosition> minePosition = new ArrayList<>(); //TODO /*da mettere globale??*/

        PrimaProva test = new PrimaProva(getApplicationContext(), tachoMaster , floorMaster  , sensorMaster ,  cameraListener);
        int mine = nMine ;
        try {
            while (!ev3.isCancelled() && mine>0 ) {

                test.findMine();
                Mina minn = test.takeMine() ;

                mineList.add(minn);

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

            Log.e("TAKE MINE 2 : " , "coordinate : "+floorMaster.getFloor().getActualPosition().getRow()+" "+floorMaster.getFloor().getActualPosition().getCol());

            runOnUiThread(() -> {
                Log.e("LISTA MINE:", mineList.toString());
                setContentView(R.layout.activity_map);
                ll = findViewById(R.id.linearlayout0);
                creaMap(mineList,n,m);
            });
        }

    }


    public static class MyCameraListener implements CameraBridgeViewBase.CvCameraViewListener2{
        double inclination = Double.NaN;
        double prev_inclination=Double.NaN;
        String color;
        public double getInclination(){return inclination;}
        public String getColor(){return color;}
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

            BallFinder ballFinder = new BallFinder(frame, true);
            ballFinder.setViewRatio(0.2f);
            ballFinder.setOrientation("landscape");
            ArrayList<Ball> f = ballFinder.findBalls();

            for (Ball b : f) {
               /* Log.e("ball", String.valueOf(b.center.x));
                Log.e("ball", String.valueOf(b.center.y));
                Log.e("ball", String.valueOf(b.radius));
                Log.e("ball", b.color);*/
                color = b.color;
            }
            return frame;
        }
    }
}
