package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import it.unive.dais.legodroid.lib.EV3;
import it.unive.dais.legodroid.lib.comm.BluetoothConnection;
import it.unive.dais.legodroid.lib.gioUtil.Floor;
import it.unive.dais.legodroid.lib.gioUtil.SensorMaster;
import it.unive.dais.legodroid.lib.gioUtil.TachoMaster;
import it.unive.dais.legodroid.lib.gioUtil.Test;
import it.unive.dais.legodroid.lib.plugs.TachoMotor;
import it.unive.dais.legodroid.lib.plugs.UltrasonicSensor;
import it.unive.dais.legodroid.lib.util.Prelude;

public class FirstTryActivity extends AppCompatActivity {

    TachoMotor motorA , motorD , motorC;
    TachoMaster tachoMaster ;
    Floor floor;
    EV3 ev3 ;
    UltrasonicSensor ultra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_try);
        //EV3 ev3 =getIntent().getExtras().getSerializable("EV3MCLOVIN"); /**TODO**/

       /** Button startButtonFirst= findViewById(R.id.startButtonFirst);
        Button stopButtonFirst = findViewById(R.id.stopButtonFirst);
        Button connectButton = findViewById(R.id.connect);
        MANCANO I BOTTONI SU FILE XML
        **/

        try {
            BluetoothConnection.BluetoothChannel ch = new BluetoothConnection("EV3MCLOVIN").connect(); // replace with your own brick name
            ev3=new EV3(ch);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("FIRST TRY", "CANNOT connect to the fuckin lego");
        }

        /**startButtonFirst.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::ev3Task2)));
        stopButtonFirst.setOnClickListener(v->ev3.cancel());**/

    }

    public void ev3Task(EV3.Api api){
        motorA=api.getTachoMotor(EV3.OutputPort.A);
        motorD=api.getTachoMotor(EV3.OutputPort.D);
        motorC=api.getTachoMotor(EV3.OutputPort.C);

        tachoMaster = new TachoMaster(motorD,motorA,motorC);

        int giri=0;

        floor = new Floor(3, 3, 29.5f ,29.5f);
        try{
            while(giri<8) {
                while ((floor.getNextPosition().getRaw()>=0 &&floor.getNextPosition().getRaw() < floor.getWidth() )
                        && (floor.getNextPosition().getCol() < floor.getHeight() && floor.getNextPosition().getCol()>=0)) {
                    Log.e("FLOOR" , "NEXT POSITION raw = "+floor.getNextPosition().getRaw()+" col = "+floor.getNextPosition().getCol());

                    tachoMaster.resetMovementMotorsPosition();
                    tachoMaster.moveStepstraight(20, 0, 630);
                    floor.updateBotPosition();
                    // tachoMaster.getMotorsPosition();
                }
                //  floor.getNextPosition().setOnFloorPosition(floor.getActualPosition().getRaw(),floor.getActualPosition().getCol());
                Floor.TurnDirection turnDirection = floor.chooseNextDirection();
                floor.updateNextPosition();
                tachoMaster.resetMovementMotorsPosition();
                Log.e("FLOOR000000000000000" , "NEXT POSITION raw = "+floor.getNextPosition().getRaw()+" col = "+floor.getNextPosition().getCol());
                tachoMaster.getMotorsPosition();
                switch(turnDirection){
                    case TURN_LEFT:tachoMaster.turnNinetyLeft(20);break;
                    case TURN_RIGHT:tachoMaster.turnNinetyRight(20);break;
                }
                giri++;
            }
        }
        catch(IOException | ExecutionException | InterruptedException e){
            Log.e("FIRST TRY : ","PROBLEMI COL MOVIMENTO");
            e.printStackTrace();
        }


    }

    public void ev3Task2(EV3.Api api) {
        motorA = api.getTachoMotor(EV3.OutputPort.A);
        motorD = api.getTachoMotor(EV3.OutputPort.D);
        motorC = api.getTachoMotor(EV3.OutputPort.C);
        ultra = api.getUltrasonicSensor(EV3.InputPort._1);

        SensorMaster sensorMaster = new SensorMaster(ultra);

        tachoMaster = new TachoMaster(motorD, motorA, motorC);

        int mine = 1;

        floor = new Floor(3, 3, 29.5f, 29.5f);

        Test test = new Test(tachoMaster , floor  , sensorMaster);
        try {
            test.PrimaProva2(mine);
        } catch (InterruptedException | IOException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}