package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myapplication.gioUtil.Floor;
import com.example.myapplication.gioUtil.FloorMaster;
import com.example.myapplication.gioUtil.Mappa;
import com.example.myapplication.gioUtil.Mina;
import com.example.myapplication.gioUtil.SensorMaster;
import com.example.myapplication.gioUtil.TachoMaster;
import com.example.myapplication.gioUtil.TerzaProva;
import com.google.android.gms.nearby.connection.ConnectionsClient;

import org.opencv.android.CameraBridgeViewBase;

import java.util.ArrayList;
import java.util.List;

import it.unive.dais.legodroid.lib.EV3;
import it.unive.dais.legodroid.lib.comm.BluetoothConnection;
import it.unive.dais.legodroid.lib.plugs.GyroSensor;
import it.unive.dais.legodroid.lib.plugs.TachoMotor;
import it.unive.dais.legodroid.lib.plugs.UltrasonicSensor;
import it.unive.dais.legodroid.lib.util.Prelude;

public class ThirdTryActivity extends AppCompatActivity {

    /**********************************************************************************************/

    public ConnectionsClient cc;
    public MyNearby nearby;
    public Button discoveryButton, disconnectButton, startButtonFirst;
    public EditText et1, et2, et3, et4, et5, et6;
    public Spinner spin,spin2;
    public String id, chiave;
    public EV3 ev3;

    public TachoMaster tachoMaster;
    public SensorMaster sensorMaster;
    public FloorMaster floorMaster;
    public TachoMotor motorA, motorD, motorC;
    public UltrasonicSensor ultra;
    public GyroSensor gyro;
    public Floor floor;
    public Floor.Direction startDirection;

    private int n,m; //dimensione campo
    private int posX,posY;
    private int mine, score;

    private CameraBridgeViewBase mOpenCvCameraView;
    private FirstTryActivity.MyCameraListener cameraListener;

    public TerzaProva test;
    public List<Floor.OnFloorPosition> posList;
    public ArrayList<Mina> mineList = new ArrayList<>();
    public Floor.Coordinate_System axisSystem;

    /**********************************************************************************************/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_try);

        cc = com.google.android.gms.nearby.Nearby.getConnectionsClient(this);
        nearby = new MyNearby(cc,id);

        startButtonFirst = findViewById(R.id.startButtonFirst);
        discoveryButton  = findViewById(R.id.discoveryButton);
        disconnectButton = findViewById(R.id.disconnectButton);

        et1 = findViewById(R.id.editText1);
        et2 = findViewById(R.id.editText2);
        et3 = findViewById(R.id.key);
        et4 = findViewById(R.id.idRobot);
        et5 = findViewById(R.id.editText5);
        et6 = findViewById(R.id.editText6);

        spin = findViewById(R.id.spinner);
        spin2 = findViewById(R.id.spinner2);

        try {
            BluetoothConnection.BluetoothChannel ch = new BluetoothConnection("EV3MCLOVIN").connect();
            ev3 = new EV3(ch);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        discoveryButton.setOnClickListener(v -> {
            chiave = et3.getText().toString();
            id = et4.getText().toString();
            nearby.startDiscovery();
        });

        disconnectButton.setOnClickListener(v -> {
            cc.stopDiscovery();
            cc.stopAdvertising();
            cc.stopAllEndpoints();
        });

        startButtonFirst.setOnClickListener(v -> {
            nearby.sendMyPayLoad("Benvenuto sono EV3MCLOVIN");

            n = Integer.valueOf(String.valueOf(et1.getText()));
            m = Integer.valueOf(String.valueOf(et2.getText()));
            posX = Integer.valueOf(String.valueOf(et5.getText()));
            posY = Integer.valueOf(String.valueOf(et6.getText()));

            posList = nearby.convertList(nearby.getMines());

            floor = new Floor(n,m ,30.0f,30.0f,posX,posY,startDirection,posList,axisSystem);
            setContentView(R.layout.activity_camera);
            mOpenCvCameraView = findViewById(R.id.OpenCvView);
            mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
            mOpenCvCameraView.setMaxFrameSize(320, 240);
            cameraListener = new FirstTryActivity.MyCameraListener();
            mOpenCvCameraView.setCvCameraViewListener(cameraListener);
            mOpenCvCameraView.enableView();

            Log.e("FIRST :","Camera active");

            Prelude.trap(() -> ev3.run(this::ev3Task));


        });

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = parent.getSelectedItem().toString();
                switch(s){
                    case "R+":
                        startDirection = Floor.Direction.VERTICAL_UP;
                        break;
                    case "R-":
                        startDirection = Floor.Direction.VERTICAL_DOWN;
                        break;
                    case "C+":
                        startDirection = Floor.Direction.HORIZONTAL_UP;
                        break;
                    case "C-":
                        startDirection = Floor.Direction.HORIZONTAL_DOWN;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = parent.getSelectedItem().toString();
                switch(s){
                    case "Y/X":
                        axisSystem = Floor.Coordinate_System.CARTESIAN;
                        break;
                    case "X/Y":
                        axisSystem = Floor.Coordinate_System.MATRIX;

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /**********************************************************************************************/

    private void ev3Task(EV3.Api api) {

        motorA = api.getTachoMotor(EV3.OutputPort.A);
        motorD = api.getTachoMotor(EV3.OutputPort.D);
        motorC = api.getTachoMotor(EV3.OutputPort.C);
        ultra = api.getUltrasonicSensor(EV3.InputPort._1);
        gyro = api.getGyroSensor(EV3.InputPort._3);

        sensorMaster = new SensorMaster(ultra, gyro);
        tachoMaster = new TachoMaster(motorA, motorD, motorC);
        floorMaster = new FloorMaster(floor);

        test = new TerzaProva(getApplicationContext(),tachoMaster,floorMaster,sensorMaster,posList,cameraListener,nearby);
        mine = posList.size();

        try {
            while (!ev3.isCancelled() && mine>0 ) {
                test.findMine();
                Mina m = test.takeMine();
                score = getScore(m);
                mineList.add(m);
                test.goToStartPosition();
                test.releaseMine();
                mine--;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            Prelude.trap(() -> tachoMaster.stopMotors());

            runOnUiThread(() -> {
                setContentView(R.layout.activity_map);
                LinearLayout ll1 = findViewById(R.id.linearlayout0);
                //LinearLayout ll2 = findViewById(R.id.ll_score);
                //TextView tv = findViewById(R.id.tv_score);
                Mappa map = new Mappa(this,ll1);
                map.creaMap(mineList, n, m, score);
            });
        }
    }

    /**********************************************************************************************/

    public int getScore(Mina m){
        String c = m.getColor();
        if (c.equals("red"))
            return 1;
        if (c.equals("blue"))
            return 2;
        if (c.equals("yellow"))
            return 3;
        else
            return 0;
    }
}
