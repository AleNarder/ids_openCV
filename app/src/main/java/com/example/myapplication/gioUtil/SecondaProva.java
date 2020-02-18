package com.example.myapplication.gioUtil;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.myapplication.FirstTryActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

import static android.content.Context.SENSOR_SERVICE;

public class SecondaProva {

    TachoMaster tachoMaster ;
    FloorMaster floorMaster ;
    SensorMaster sensorMaster;
    int tileDim ;   /**ASSUMO CHE SIANO QUADRATE **/
    Context context;
    private SensorManager sensorManager;
    private Sensor smartphone_gyro;
    private Float angle_checker;


    /** in questo array vengono salvate le posizioni chiave(quelle ottenute tramite choose next direction)
     * Serviranno per fare il percorso inverso non appena trovata una mina se nessun altro percorso risulti
     * privo di ostacoli (altre mine)**/

    List<Floor.OnFloorPosition> botMoves;

    /**TODO:
     * come ottengo questa lista?-->mi arrivano tutte le posizioni prima di partire o arrivano man mano?? in teoria prima di partire
     * la devo ordinare in base ai colori?--> quale mina devo prendere per prima?
     * so già i colori delle mine? Se si la lista deve avere un altro tipo
     * bisogna aggiornare floor e dirgli dove stanno le mine!!!!!!!!!!!!!!
     * **/

    FirstTryActivity.MyCameraListener cameraListener;

    List<Floor.OnFloorPosition> minePosition; /**lista posizioni mine da raccogliere**/

    //TODO aggiungere il camera listener + sistemare algoritmo con gli adjust. aggiustare anche appena dopo aver girato?
    public SecondaProva(Context context , TachoMaster tachoMaster , FloorMaster floorMaster , SensorMaster sensorMaster , List<Floor.OnFloorPosition> minePosition , FirstTryActivity.MyCameraListener cameraListener){
        this.tachoMaster=tachoMaster;
        this.floorMaster=floorMaster;
        this.sensorMaster=sensorMaster;
        this.cameraListener = cameraListener;
        botMoves=new ArrayList<>();
        tileDim = Math.round(floorMaster.getFloor().getTileWidth()*20+20); /**è la dimensione in step per i motori**/
        this.minePosition=minePosition;

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

    public void findMine() throws InterruptedException, ExecutionException, IOException, N_ReachableException {
        boolean motors_going=false;
        boolean noWhereToGo=true;
        boolean noDestination=true;
        boolean noMiddleDestination=true;
        botMoves.clear();
        botMoves.add(floorMaster.getFloor().getStartPosition()); /**aggiungo posizione di partenza --> ultima posizione dell'inverso**/
        Floor.OnFloorPosition newPosition = new Floor.OnFloorPosition(-1,-1);
        Floor.OnFloorPosition destination = new Floor.OnFloorPosition(-1,-1);
        Floor.OnFloorPosition middleDestination = new Floor.OnFloorPosition(-1,-1);

        while(!sensorMaster.objectInProximity()) {
            if (noDestination) {
                //TODO sto if potrebbe non servire se alla fine prendo una mina alla volta, posso farlo direttamente quando inizio la funzione
                destination = minePosition.remove(0);  //TODO o peek o poll dipende se è meglio non toglierla subito e lasciarla finchè non ho la sicurezza di avere preso la mina
                noDestination = false;
            }
            if(noMiddleDestination) {
                floorMaster.getFloor().setCheckedForAll(false);
                middleDestination.setOnFloorPosition(-1, -1);
                floorMaster.destinationMeth(floorMaster.getFloor().getActualPosition(), destination, middleDestination);
                Log.e("PRIMA PROVA 3 : ", "MIDDLE :  ROW : " + middleDestination.getRow() + " COL : " + middleDestination.getCol());

                noMiddleDestination = false;
                if(middleDestination.compareTo(new Floor.OnFloorPosition(-1,-1))==0)
                    throw new N_ReachableException();
            }
            if (noWhereToGo) {
                Log.e("SECONDA PROVA :","NOWHERE");
                newPosition = floorMaster.chooseNextPosition(middleDestination);
                noWhereToGo = false;
                tachoMaster.resetMovementMotorsPosition();
                Floor.Direction d = floorMaster.changeBotDirection(newPosition);

                Floor.TurnDirection turn = floorMaster.turnDirectionDispatch(d);

                floorMaster.getFloor().setDestination(newPosition,true);

                //tachoMaster.turnBot(10, 163, turn);
                tachoMaster.turnBot(10, turn, sensorMaster, 20.0);
            }

            if(motors_going){
                tachoMaster.moduleSpeed(15,cameraListener.getInclination());
            }

            if (!motors_going && floorMaster.getFloor().getActualPosition().compareTo(newPosition) != 0) {
                Log.d("PRIMA PROVA 3 : ", "MOTORS GOING ");

                tachoMaster.resetMovementMotorsPosition();
                tachoMaster.moveStraight(15);
                tachoMaster.moduleSpeed(15,cameraListener.getInclination());
                motors_going = true;

            }

            if (tachoMaster.getMotorsCount() > tileDim && motors_going) {
                Log.e("PRIMA PROVA 3 : ", "UPDATING POSITION");
                floorMaster.updateBotPosition();
                floorMaster.updateNextPosition();

                Log.e("PRIMA PROVA 3 : ", "ACTUALPOSITION :  ROW : " + floorMaster.getFloor().getActualPosition().getRow() + " COL : " + floorMaster.getFloor().getActualPosition().getCol());

                if (floorMaster.getFloor().getActualPosition().compareTo(newPosition) == 0) {
                    if(floorMaster.getFloor().getActualPosition().compareTo((middleDestination))==0) {
                        noMiddleDestination = true;
                        floorMaster.getFloor().setDestinationForAll(false);
                    }
                    Log.e("PRIMA PROVA 3 : ", "STOPPING MOTORS");
                    tachoMaster.stopMotors();
                    motors_going = false;
                    noWhereToGo = true;
                    Thread.sleep(1000);
                    //tachoMaster.countAdjustment(20, Math.round(tachoMaster.getMotorsCount()), tileDim);
                    tachoMaster.resetMovementMotorsPosition();

                    botMoves.add(new Floor.OnFloorPosition(floorMaster.getFloor().getActualPosition().getRow(), floorMaster.getFloor().getActualPosition().getCol()));
                    Log.e("PRIMA PROVA 3 : ", "POSITION ADDED : " + botMoves.get(botMoves.size() - 1).getRow() + botMoves.get(botMoves.size() - 1).getCol());
                }
                tachoMaster.resetMovementMotorsPosition();
            }
        }

        tachoMaster.stopMotors();

    }



    public Mina takeMine() throws InterruptedException, ExecutionException, IOException {

        // tachoMaster.turnBot(10, Floor.TurnDirection.U_INVERSION,sensorMaster);
        // tachoMaster.countAdjustment(20,Math.round(tachoMaster.getMotorsCount()),630 ); //TODO
        //tachoMaster.releaseMine(20,3000);
        tachoMaster.countAdjustment(15,Math.round(tachoMaster.getMotorsCount()),630 ); //TODO
        tachoMaster.takeMine(30,2000);
        tachoMaster.releaseMine(-30,3000);
        //tachoMaster.takeMine(-20,3000);
        //tachoMaster.countAdjustment(20,Math.round(tachoMaster.getMotorsCount()),630 ); //TODO
        floorMaster.updateBotPosition();
        floorMaster.updateNextPosition();
        //tachoMaster.takeMine(-20,2000);
       // tachoMaster.turnBot(10, Floor.TurnDirection.U_INVERSION,sensorMaster,20.0);

        Log.e("PRIMA PROVA 3 : ", "ACTUALPOSITION :  ROW : "+floorMaster.getFloor().getActualPosition().getRow()+" COL : "+floorMaster.getFloor().getActualPosition().getCol());


        tachoMaster.resetMovementMotorsPosition();

        int x = floorMaster.getFloor().getActualPosition().getRow();
        int y = floorMaster.getFloor().getActualPosition().getCol();
        Floor.OnFloorPosition minePos = new Floor.OnFloorPosition(x,y);
        floorMaster.getFloor().setMine(minePos,false);
        return  new Mina(minePos,cameraListener.getColor());
    }


    public void goToStartPosition() throws InterruptedException, ExecutionException, IOException {
        boolean motors_going=false;
        boolean nowhereToGo=true;
        int i = botMoves.size()-1;
        Floor.OnFloorPosition newPosition = new Floor.OnFloorPosition(-1,-1); //TODO
        Log.e("SECONDA PROVA 3 : ", "ARRAY : "+botMoves.get(botMoves.size()-1).getRow()+botMoves.get(botMoves.size()-1).getCol());
        while(i>=0) {
            if (nowhereToGo){

                newPosition = botMoves.get(i);
                Floor.Direction d = floorMaster.changeBotDirection(botMoves.get(i));
                Floor.TurnDirection turn = floorMaster.turnDirectionDispatch(d);

                //tachoMaster.turnBot(10,163,turn);
                tachoMaster.turnBot(10,turn,sensorMaster,cameraListener.getInclination());





                Log.e("PRIMA PROVA 3 : ", "POSIZIONE SCELTA PERCORSO INVERSO-----> RIGA : "+botMoves.get(i).getRow()+" COLONNA : "+ botMoves.get(i).getCol());

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
                    //tachoMaster.countAdjustment(20, Math.round(tachoMaster.getMotorsCount()), tileDim);
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

    public static class N_ReachableException extends Exception{}
}
