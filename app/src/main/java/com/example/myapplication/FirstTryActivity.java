package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

public class FirstTryActivity extends AppCompatActivity {

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
    EditText et1, et2;

    Integer n, m;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_try);
        //EV3 ev3 =getIntent().getExtras().getSerializable("EV3MCLOVIN"); /**TODO**/

        Button startButtonFirst = findViewById(R.id.startButtonFirst);
        Button stopButtonFirst = findViewById(R.id.stopButtonFirst);

        et1 = findViewById(R.id.editText1);
        et2 = findViewById(R.id.editText2);

        try {
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
        gyro = api.getGyroSensor(EV3.InputPort._3);

        sensorMaster = new SensorMaster(ultra, gyro);

        tachoMaster = new TachoMaster(motorA, motorD, motorC);

        floor = new Floor(n,m, 29.5f ,29.5f);

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
