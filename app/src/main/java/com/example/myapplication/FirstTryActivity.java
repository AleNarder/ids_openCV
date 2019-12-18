package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
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

    TachoMotor motorA , motorD , motorC;
    TachoMaster tachoMaster ;
    Floor floor;
    EV3 ev3 ;
    UltrasonicSensor ultra;
    GyroSensor gyro;

    SensorMaster sensorMaster ;

    LinearLayout ll;

    int cnt=0,x,y;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_try);
        //EV3 ev3 =getIntent().getExtras().getSerializable("EV3MCLOVIN"); /**TODO**/

        Button startButtonFirst= findViewById(R.id.startButtonFirst);
        Button stopButtonFirst = findViewById(R.id.stopButtonFirst);

        try {
            BluetoothConnection.BluetoothChannel ch = new BluetoothConnection("EV3MCLOVIN").connect(); // replace with your own brick name
            ev3=new EV3(ch);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("FIRST TRY", "CANNOT connect to the fuckin lego");
        }

        startButtonFirst.setOnClickListener(v -> {
            setContentView(R.layout.activity_map);
            ll = findViewById(R.id.linearlayout0);
            creaMap(10,10,x,y);
            //ll.removeAllViews();
            //Prelude.trap(() -> ev3.run(this::ev3Task3));
        });
        stopButtonFirst.setOnClickListener(v->ev3.cancel());

    }

    /**************************************************************************************************************************/
    public void creaMap(int n, int m, int x, int y){
        for(int i=0;i<n;i++)
            addButton(i,m,x,y);
    }

    public void addButton(int n, int m, int x, int y){
        LinearLayout ll2 = new LinearLayout(this);
        ll2.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams ll_params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll2.setLayoutParams(ll_params);
        ll.addView(ll2);

        for(int i=0;i<m;i++){
            Button btn = new Button(this);

            LinearLayout.LayoutParams b_params = new LinearLayout.LayoutParams(0, 100,1);
            btn.setLayoutParams(b_params);
            ll2.addView(btn);
            btn.setId(cnt);
            cnt++;

            /** set color */
            if (n==x && i==y)
                btn.setBackgroundColor(Color.RED);

            final int x2 = i;
            btn.setOnClickListener(v-> Toast.makeText(FirstTryActivity.this, "["+n+","+x2+"]", Toast.LENGTH_LONG).show());
        }
    }

    public void setColorButton(int x, int y, Button btn){
        int cnt=0;
        for(int i=0;i<x;i++){
            for(int j=0;j<y;j++){
                cnt++;
            }
        }
        //btn.findViewById(R.id.cnt);
        btn.setBackgroundColor(Color.RED);
    }
    /**************************************************************************************************************************/


    private void threadTest(EV3.Api api){
        Thread a = new Thread(){
            @Override
            public void run(){

            }
        };
        a.start();
    }

    private void gyroTest(EV3.Api api){
        motorA = api.getTachoMotor(EV3.OutputPort.A);
        motorD = api.getTachoMotor(EV3.OutputPort.D);
        motorC = api.getTachoMotor(EV3.OutputPort.C);
        ultra = api.getUltrasonicSensor(EV3.InputPort._1);
        gyro = api.getGyroSensor(EV3.InputPort._3);

        sensorMaster = new SensorMaster(ultra, gyro);

        tachoMaster = new TachoMaster(motorD, motorA, motorC);

        float init_angle , final_angle;

        boolean motors_going=false;
        try{
            while(!ev3.isCancelled()){
                init_angle = sensorMaster.getGyroAngle();
                while((sensorMaster.getGyroAngle()-init_angle)<82) {
                    if (!motors_going) {
                        motorA.setSpeed(10);
                        motorD.setSpeed(-10);
                        motorD.start();
                        motorA.start();
                        motors_going = true;
                    }
                }
                Prelude.trap(()->{motorA.stop(); motorD.stop();});
                motors_going=false;
                Log.e("SENSOR MASTER = " , "ANGOLO = "+sensorMaster.getGyroAngle());
                Log.e("SENSOR MASTER = ", "DIFFERENZA ANGOLI = "+Math.round(sensorMaster.getGyroAngle()-init_angle));
                Thread.sleep(5000);
            }
        }
        catch(Exception e){}
        finally{
            Prelude.trap(()->{motorA.stop(); motorD.stop();});
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

        floor = new Floor(3, 3, 29.5f ,29.5f);

        List<Floor.OnFloorPosition> minePosition = new ArrayList<>(); //TODO /*da mettere globale??*/

        PrimaProva test = new PrimaProva(getApplicationContext(), tachoMaster , floor  , sensorMaster);
        int mine = 3 ;
        try {
            while (!ev3.isCancelled() && mine>0 ) {
                test.findMine();
                Floor.OnFloorPosition pos = test.takeMine() ;
                x = pos.getRow();
                y=pos.getCol();
                ll.removeAllViews();
                creaMap(floor.getWidth(),floor.getHeight(),x,y);
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
            } catch (Floor.AllPositionVisited allPositionVisited) {
                allPositionVisited.printStackTrace();
                Log.d("PRIMA PROVA : " , "Tutto il campo è stato visitato");
        } finally {
            Prelude.trap(()->tachoMaster.stopMotors());
        }

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

        sensorMaster = new SensorMaster(ultra, gyro);

        tachoMaster = new TachoMaster(motorD, motorA, motorC);

        int mine = 1;

        floor = new Floor(3, 3, 29.5f, 29.5f);

        Test test = new Test(tachoMaster , floor  , sensorMaster);
        try {
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
                    try {
                        newPosition = floor.chooseNextPosition(); /*se ho riga o colonna adiacenti da controllare vado fino in fondo
                        altrimenti mi faccio restituire una nuova colonna a cui andare o riga*/
                    } catch (Floor.AllPositionVisited allPositionVisited) {
                        allPositionVisited.printStackTrace();
                    }


                    Log.e("PRIMA PROVA 3 : ", "POSIZIONE SCELTA -----> RIGA : "+newPosition.getRow()+" COLONNA : "+newPosition.getCol());


                    /**************/
                    Floor.Direction d = floor.changeBotDirection(newPosition);
                    /**qua deve girarsi a dovere*/
                    Floor.TurnDirection turn = floor.turnDirection(d);

                    Log.e("PRIMA PROVA 3 : ", "TURN DIRECTION "+turn);
                    tachoMaster.turnBot(10,183,turn);


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


                    tachoMaster.turnBot(10,183,turn);




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
