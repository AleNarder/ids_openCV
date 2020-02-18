package com.example.myapplication.gioUtil;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.SurfaceView;

import com.example.myapplication.FirstTryActivity;
import com.example.myapplication.LineFinder;
import com.example.myapplication.R;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.SENSOR_SERVICE;

public class PrimaProva {

    TachoMaster tachoMaster ;
    FloorMaster floorMaster ;
    SensorMaster sensorMaster;
    int tileDim ;   /**ASSUMO CHE SIANO QUADRATE **/
    Context context;
    private SensorManager sensorManager;
    private Sensor smartphone_gyro;
    private Float angle_checker;

    double inclination;


    /** in questo array vengono salvate le posizioni chiave(quelle ottenute tramite choose next direction)
     * Serviranno per fare il percorso inverso non appena trovata una mina se nessun altro percorso risulti
     * privo di ostacoli (altre mine)**/

    List<Floor.OnFloorPosition> botMoves;

    private CameraBridgeViewBase mOpenCvCameraView;
    private FirstTryActivity.MyCameraListener cameraListener;
    /****************************/

    public PrimaProva(Context context , TachoMaster tachoMaster , FloorMaster floorMaster , SensorMaster sensorMaster , FirstTryActivity.MyCameraListener cameraListener){
        this.tachoMaster=tachoMaster;
        this.floorMaster=floorMaster;
        this.sensorMaster=sensorMaster;
       // this.act = act
        this.cameraListener = cameraListener;
        botMoves=new ArrayList<>();
        tileDim = Math.round(floorMaster.getFloor().getTileWidth()*20+20); /**è la dimensione in step per i motori**/

        this.context=context;
        sensorManager = (SensorManager)context.getSystemService(SENSOR_SERVICE);
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

    public void findMine() throws InterruptedException, ExecutionException, IOException, FloorMaster.AllPositionVisited {


        boolean motors_going=false;
        boolean nowhereToGo=true;

        botMoves.clear();
        botMoves.add(floorMaster.getFloor().getStartPosition()); /**aggiungo posizione di partenza --> ultima posizione dell'inverso**/

        Floor.OnFloorPosition newPosition = new Floor.OnFloorPosition(-1,-1); //TODO


        while (!sensorMaster.objectInProximity()) {

            if (nowhereToGo) {

                Log.d("PRIMA PROVA 3 : ", "ACTUALPOSITION :  ROW : " + floorMaster.getFloor().getActualPosition().getRow() + " COL : " + floorMaster.getFloor().getActualPosition().getCol());

                tachoMaster.resetMovementMotorsPosition();


                newPosition = floorMaster.chooseNextPosition();

                if(newPosition==null)
                    break;


                Log.d("PRIMA PROVA 3 : ", "POSIZIONE SCELTA -----> RIGA : " + newPosition.getRow() + " COLONNA : " + newPosition.getCol());


                Floor.Direction d = floorMaster.changeBotDirection(newPosition);

                Floor.TurnDirection turn = floorMaster.turnDirectionDispatch(d);


                //tachoMaster.turnBot(10, 163, turn);
                tachoMaster.turnBot(10,turn,sensorMaster,cameraListener.getInclination());

                nowhereToGo = false;
            }

            if(motors_going){
                tachoMaster.moduleSpeed(15,cameraListener.getInclination());
            }

            if (!motors_going && floorMaster.getFloor().getActualPosition().compareTo(newPosition) != 0) {
                Log.d("PRIMA PROVA 3 : ", "MOTORS GOING ");
                //tachoMaster.stopMotors();
                tachoMaster.resetMovementMotorsPosition();
                tachoMaster.moveStraight(15);
                tachoMaster.moduleSpeed(15,cameraListener.getInclination());
                motors_going = true;

            }
            if (tachoMaster.getMotorsCount() > tileDim && motors_going) {
                Log.e("PRIMA PROVA 3 : ", "UPDATING POSITION");
                floorMaster.updateBotPosition();
                floorMaster.updateNextPosition();
               // tachoMaster.resetMovementMotorsPosition();
                Log.e("PRIMA PROVA 3 : ", "ACTUALPOSITION :  ROW : " + floorMaster.getFloor().getActualPosition().getRow() + " COL : " + floorMaster.getFloor().getActualPosition().getCol());

                if (floorMaster.getFloor().getActualPosition().compareTo(newPosition) == 0) {
                    Log.e("PRIMA PROVA 3 : ", "STOPPING MOTORS");
                    tachoMaster.stopMotors();
                    motors_going = false;
                    nowhereToGo = true;
                    Thread.sleep(500);
                    //tachoMaster.countAdjustment(15, Math.round(tachoMaster.getMotorsCount()), tileDim);
                    //tachoMaster.resetMovementMotorsPosition();
                    botMoves.add(new Floor.OnFloorPosition(floorMaster.getFloor().getActualPosition().getRow(), floorMaster.getFloor().getActualPosition().getCol()));
                    Log.e("PRIMA PROVA 3 : ", "POSITION ADDED : " + botMoves.get(botMoves.size() - 1).getRow() + botMoves.get(botMoves.size() - 1).getCol());
                }
                tachoMaster.resetMovementMotorsPosition();

            }
           // abbassare risoluzione togliere colori alzare contrasto e in caso abbassare threshold

        }   /**CHIUSURA while(!sensorMaster.objectPressing())**/
        tachoMaster.stopMotors();
    }

    public Mina takeMine() throws InterruptedException, ExecutionException, IOException {

       // tachoMaster.turnBot(10, Floor.TurnDirection.U_INVERSION,sensorMaster);
       // tachoMaster.countAdjustment(20,Math.round(tachoMaster.getMotorsCount()),630 ); //TODO
        //tachoMaster.releaseMine(20,3000);
        tachoMaster.countAdjustment(20,Math.round(tachoMaster.getMotorsCount()),630 ); //TODO
        tachoMaster.takeMine(30,2000);
        tachoMaster.releaseMine(-30,3000);

        //tachoMaster.takeMine(-20,3000);
        tachoMaster.countAdjustment(20,Math.round(tachoMaster.getMotorsCount()),630 ); //TODO
        floorMaster.updateBotPosition();
        floorMaster.updateNextPosition();
        //tachoMaster.takeMine(-20,2000);
        //tachoMaster.turnBot(10, Floor.TurnDirection.U_INVERSION,sensorMaster);

        Log.e("PRIMA PROVA 3 : ", "ACTUALPOSITION :  ROW : "+floorMaster.getFloor().getActualPosition().getRow()+" COL : "+floorMaster.getFloor().getActualPosition().getCol());


        tachoMaster.resetMovementMotorsPosition();

        Log.e("TAKE MINE : " , "coordinate : "+floorMaster.getFloor().getActualPosition().getRow()+" "+floorMaster.getFloor().getActualPosition().getCol());
        int x = floorMaster.getFloor().getActualPosition().getRow();
        int y = floorMaster.getFloor().getActualPosition().getCol();
        Floor.OnFloorPosition minePos = new Floor.OnFloorPosition(x,y);
        return  new Mina(minePos,cameraListener.getColor());
    }

    public void goToStartPosition() throws InterruptedException, ExecutionException, IOException {
        boolean motors_going=false;
        boolean nowhereToGo=true;
        boolean optimalRoad=false;
        int i = botMoves.size()-1;
        Floor.OnFloorPosition newPosition = new Floor.OnFloorPosition(-1,-1); //TODO
        Log.e("PRIMA PROVA 3 : ", "ARRAY : "+botMoves.get(botMoves.size()-1).getRow()+botMoves.get(botMoves.size()-1).getCol());
        while(i>=0 && floorMaster.getFloor().getActualPosition().compareTo(floorMaster.getFloor().getStartPosition())!=0) {
            if (nowhereToGo){

                if((newPosition=floorMaster.chooseNextPositionInv(floorMaster.getFloor().getStartPosition()))!=null) {
                    Log.e("PRIMA PROVA","percorso ottimale");
                    optimalRoad = true;
                }
                if(!optimalRoad) {
                    newPosition = botMoves.get(i);
                    Log.e("PRIMA PROVA","percorso inverso");
                }

                Floor.Direction d = floorMaster.changeBotDirection(newPosition);
                Floor.TurnDirection turn = floorMaster.turnDirectionDispatch(d);

                tachoMaster.turnBot(10,turn,sensorMaster,cameraListener.getInclination());





                Log.e("PRIMA PROVA 3 : ", "POSIZIONE SCELTA PERCORSO INVERSO-----> RIGA : "+newPosition.getRow()+" COLONNA : "+ newPosition.getCol());

                nowhereToGo=false;
            }

            if(motors_going){
                tachoMaster.moduleSpeed(15,cameraListener.getInclination());
            }
            if(!motors_going && floorMaster.getFloor().getActualPosition().compareTo(newPosition)!=0){
                tachoMaster.resetMovementMotorsPosition();
                tachoMaster.moveStraight(15);
                tachoMaster.moduleSpeed(15,cameraListener.getInclination());
                motors_going=true;
            }
            if(tachoMaster.getMotorsCount()>tileDim && motors_going){  /** dovrei avere (grandezza della piastrella *20)+20*/
                floorMaster.updateBotPosition();
                floorMaster.updateNextPosition();
                if(floorMaster.getFloor().getActualPosition().compareTo(newPosition)==0) {
                    tachoMaster.stopMotors();
                    motors_going=false;
                    nowhereToGo=true;
                   // tachoMaster.countAdjustment(15, Math.round(tachoMaster.getMotorsCount()), tileDim);
                    i--;
                }

                tachoMaster.resetMovementMotorsPosition();

                Log.e("PRIMA PROVA 3 : ", "ACTUALPOSITION :  ROW : "+floorMaster.getFloor().getActualPosition().getRow()+" COL : "+floorMaster.getFloor().getActualPosition().getCol());

            }
            /***TOGLIERE LA MOSSA**/
        }

    }

    public void releaseMine() throws InterruptedException, ExecutionException, IOException {

        Floor.Direction prevD = floorMaster.getFloor().getBotDirection();
        Floor.Direction d = floorMaster.getFloor().safeDirection(floorMaster.floor.getStartPosition());
        Floor.TurnDirection turn = floorMaster.turnDirectionDispatch(d);
        tachoMaster.turnBot(10,turn,sensorMaster,cameraListener.getInclination());

        tachoMaster.resetMovementMotorsPosition();
        tachoMaster.countAdjustment(15,Math.round(tachoMaster.getMotorsCount()),tileDim/2);
        tachoMaster.releaseMine(30,5000);
        tachoMaster.resetMovementMotorsPosition();
        tachoMaster.countAdjustment(-15,Math.round(tachoMaster.getMotorsCount()),tileDim/2);

        turn = floorMaster.turnDirectionDispatch(prevD);
        tachoMaster.turnBot(10,turn,sensorMaster,cameraListener.getInclination());

    }


}
