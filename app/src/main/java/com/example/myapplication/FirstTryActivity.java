package com.example.myapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private Sensor smartphone_gyro;

    private SensorManager sensorManager;

    private Float angle_checker;

    SensorMaster sensorMaster ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_try);
        //EV3 ev3 =getIntent().getExtras().getSerializable("EV3MCLOVIN"); /**TODO**/

        Button startButtonFirst= findViewById(R.id.startButtonFirst);
        Button stopButtonFirst = findViewById(R.id.stopButtonFirst);
        Button connectButton = findViewById(R.id.connect);

        try {
            BluetoothConnection.BluetoothChannel ch = new BluetoothConnection("EV3MCLOVIN").connect(); // replace with your own brick name
            ev3=new EV3(ch);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("FIRST TRY", "CANNOT connect to the fuckin lego");
        }

        startButtonFirst.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::ev3Task2)));
        stopButtonFirst.setOnClickListener(v->ev3.cancel());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        smartphone_gyro = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        SensorEventListener sensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                angle_checker = sensorEvent.values[0];
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorManager.registerListener(sensorListener, smartphone_gyro , sensorManager.SENSOR_DELAY_FASTEST);

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
                while ((floor.getNextPosition().getRow()>=0 &&floor.getNextPosition().getRow() < floor.getWidth() )
                        && (floor.getNextPosition().getCol() < floor.getHeight() && floor.getNextPosition().getCol()>=0)) {
                    Log.e("FLOOR" , "NEXT POSITION raw = "+floor.getNextPosition().getRow()+" col = "+floor.getNextPosition().getCol());

                    tachoMaster.resetMovementMotorsPosition();
                    tachoMaster.moveStepstraight(20, 0, 630);
                    floor.updateBotPosition();
                    // tachoMaster.getMotorsPosition();
                }
                //  floor.getNextPosition().setOnFloorPosition(floor.getActualPosition().getRaw(),floor.getActualPosition().getCol());
                Floor.TurnDirection turnDirection = floor.chooseNextDirection();
                floor.updateNextPosition();
                tachoMaster.resetMovementMotorsPosition();
                Log.e("FLOOR000000000000000" , "NEXT POSITION raw = "+floor.getNextPosition().getRow()+" col = "+floor.getNextPosition().getCol());
                tachoMaster.getMotorsPosition();
                switch(turnDirection){
                    case TURN_LEFT:tachoMaster.turnNinetyLeft(20,173);break;
                    case TURN_RIGHT:tachoMaster.turnNinetyRight(20,173);break;
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

        sensorMaster = new SensorMaster(ultra);

        tachoMaster = new TachoMaster(motorD, motorA, motorC);

        int mine = 1;

        floor = new Floor(3, 3, 29.5f, 29.5f);

        Test test = new Test(tachoMaster , floor  , sensorMaster);
        try {
            /*Schifoso di merda*/
            PrimaProva3(mine);
        } catch (InterruptedException | IOException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    public void PrimaProva3(int mine) throws InterruptedException, ExecutionException, IOException {
        boolean motors_going=false;
        boolean nowhereToGo=true;
        float initAngle , finalAngle;
        List<Floor.OnFloorPosition> botMoves = new ArrayList<>();
        Floor.OnFloorPosition newPosition = new Floor.OnFloorPosition(-1,-1); //TODO
        Thread.sleep(3000);
        while(mine>0){

            botMoves.clear();
            botMoves.add(floor.getStartPosition());

            while(!sensorMaster.objectInProximity()){
                /**devo anche salvarmi gli spostamenti che fa prima di trovare una pallina**/
                if(nowhereToGo){

                    boolean okAngle=false;

                    Log.e("PRIMA PROVA 3 : ", "ACTUALPOSITION :  ROW : "+floor.getActualPosition().getRow()+" COL : "+floor.getActualPosition().getCol());

                    tachoMaster.resetMovementMotorsPosition();



                    /***questo può stare in una ausiliaria ..... qua deve scegliere il punto dove andare che varia da dovè o solo per riga o solo per colonna*/
                    /**qui devo settare new Position*/
                    newPosition = floor.chooseNextPosition(); /*se ho riga o colonna adiacenti da controllare vado fino in fondo
                    altrimenti mi faccio restituire una nuova colonna a cui andare o riga*/


                    Log.e("PRIMA PROVA 3 : ", "POSIZIONE SCELTA -----> RIGA : "+newPosition.getRow()+" COLONNA : "+newPosition.getCol());


                    /**************/
                    Floor.Direction d = floor.changeBotDirection(newPosition);
                    /**qua deve girarsi a dovere*/
                    Floor.TurnDirection turn = floor.turnDirection(d);

                    Log.e("PRIMA PROVA 3 : ", "TURN DIRECTION "+turn);

                    initAngle=angle_checker;
                    tachoMaster.turnBot(10,183,turn);
                    finalAngle=angle_checker;
                    int ok=0;
                    while(!okAngle && ok<10){
                        okAngle=tachoMaster.adjustTurn(turn,initAngle,finalAngle);
                        Thread.sleep(1000);
                        finalAngle=angle_checker;
                        ok++;
                    }


                    nowhereToGo=false;
                }

                if(!motors_going && floor.getActualPosition().compareTo(newPosition)!=0){
                    Log.e("PRIMA PROVA 3 : ", "MOTORS GOING ");
                    tachoMaster.resetMovementMotorsPosition();
                    tachoMaster.moveStraight(20);
                    motors_going=true;
                }
                if(tachoMaster.getMotorsCount()>630 && motors_going){  /** dovrei avere (grandezza della piastrella *20)+20*/
                    Log.e("PRIMA PROVA 3 : ", "UPDATING POSITION");
                    floor.updateBotPosition();
                    floor.updateNextPosition();

                    Log.e("PRIMA PROVA 3 : ", "ACTUALPOSITION :  ROW : "+floor.getActualPosition().getRow()+" COL : "+floor.getActualPosition().getCol());

                    if(floor.getActualPosition().compareTo(newPosition)==0) {
                        Log.e("PRIMA PROVA 3 : ", "STOPPING MOTORS");
                        tachoMaster.stopMotors();
                        motors_going = false;
                        nowhereToGo = true;
                        tachoMaster.countAdjustment(20, Math.round(tachoMaster.getMotorsCount()), 630);
                        tachoMaster.resetMovementMotorsPosition();
                        botMoves.add(new Floor.OnFloorPosition(floor.getActualPosition().getRow(), floor.getActualPosition().getCol()));
                        Log.e("PRIMA PROVA 3 : ", "POSITION ADDED : " + botMoves.get(botMoves.size() - 1).getRow() + botMoves.get(botMoves.size() - 1).getCol());
                    }
                    tachoMaster.resetMovementMotorsPosition();

                    /**PROBLEMA SE MI TROVO GIA' NELLA POSIZIONE SCELTA**/

                }

            }   /**CHIUSURA while(!sensorMaster.objectInProximity())**/
            tachoMaster.stopMotors();
            nowhereToGo=true;
            motors_going=false;

            /**raccolgo pallina*/

            tachoMaster.takeMine(-20,3000);
            tachoMaster.countAdjustment(20,Math.round(tachoMaster.getMotorsCount()),630 ); //TODO
            floor.updateBotPosition();
            floor.updateNextPosition();
            tachoMaster.takeMine(-20,2000);

            Log.e("PRIMA PROVA 3 : ", "ACTUALPOSITION :  ROW : "+floor.getActualPosition().getRow()+" COL : "+floor.getActualPosition().getCol());


            tachoMaster.resetMovementMotorsPosition();




            /**faccio percorso inverso*/
            int i = botMoves.size()-1;
            Log.e("PRIMA PROVA 3 : ", "ARRAY : "+botMoves.get(botMoves.size()-1).getRow()+botMoves.get(botMoves.size()-1).getCol());
            while(i>=0) {
                if (nowhereToGo){
                    boolean okAngle=false;
                    newPosition = botMoves.get(i);
                    Floor.Direction d = floor.changeBotDirection(botMoves.get(i));
                    Floor.TurnDirection turn = floor.turnDirection(d);

                    initAngle=angle_checker;
                    tachoMaster.turnBot(10,183,turn);
                    finalAngle=angle_checker;
                    int ok=0;
                    while(!okAngle && ok<10){
                        okAngle=tachoMaster.adjustTurn(turn,initAngle,finalAngle);
                        Thread.sleep(1000);
                        finalAngle=angle_checker;
                        ok++;
                    }



                    Log.e("PRIMA PROVA 3 : ", "POSIZIONE SCELTA PERCORSO INVERSO-----> RIGA : "+botMoves.get(i).getRow()+" COLONNA : "+ botMoves.get(i).getCol());

                    nowhereToGo=false;
                }

                if(!motors_going && floor.getActualPosition().compareTo(newPosition)!=0){
                    tachoMaster.resetMovementMotorsPosition();
                    tachoMaster.moveStraight(20);
                    motors_going=true;
                }
                if(tachoMaster.getMotorsCount()>630 && motors_going){  /** dovrei avere (grandezza della piastrella *20)+20*/
                    floor.updateBotPosition();
                    floor.updateNextPosition();
                    if(floor.getActualPosition().compareTo(newPosition)==0) {
                        tachoMaster.stopMotors();
                        motors_going=false;
                        nowhereToGo=true;
                        tachoMaster.countAdjustment(20, Math.round(tachoMaster.getMotorsCount()), 630);
                        i--;
                    }

                    tachoMaster.resetMovementMotorsPosition();

                    Log.e("PRIMA PROVA 3 : ", "ACTUALPOSITION :  ROW : "+floor.getActualPosition().getRow()+" COL : "+floor.getActualPosition().getCol());

                }
                /***TOGLIERE LA MOSSA**/
            }

            /**rilascio pallina*/



            /**TODO: VEDERE SE LA DIREZIONE E' CORRETTA AFFINCHE' LA MINA VENGA RILASCIATA NELLA ZONA ADIBITA*/

            tachoMaster.countAdjustment(20,Math.round(tachoMaster.getMotorsCount()),315);
            tachoMaster.releaseMine(20,3000);
            tachoMaster.countAdjustment(-20,315,Math.round(tachoMaster.getMotorsCount()));
            mine--;
        }
    }
}
