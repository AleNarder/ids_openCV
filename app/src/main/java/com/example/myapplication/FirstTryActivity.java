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


    private EditText et1, et2, et5, et6 , et7;
    private TextView tv3;
    private Button startButtonFirst, stopButtonFirst;
    public Integer posX, posY;

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


        et1 = findViewById(R.id.editText1);
        et2 = findViewById(R.id.editText2);
        et5 = findViewById(R.id.editText5);
        et6 = findViewById(R.id.editText6);
        et7 = findViewById(R.id.editText7);
        tv3 = findViewById(R.id.statoRobot);

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
                if(color==null)
                    color="green";
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
                        case "green":
                            btn.setBackgroundColor(Color.GREEN);
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

    /**********************************************************************************************************************************/

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
