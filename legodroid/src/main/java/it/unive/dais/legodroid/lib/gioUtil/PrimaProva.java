package it.unive.dais.legodroid.lib.gioUtil;

import android.service.quicksettings.Tile;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PrimaProva {

    TachoMaster tachoMaster ;
    Floor floor ;
    SensorMaster sensorMaster;
    int tileDim ;   /**ASSUMO CHE SIANO QUADRATE **/

    /** in questo array vengono salvate le posizioni chiave(quelle ottenute tramite choose next direction)
     * Serviranno per fare il percorso inverso non appena trovata una mina se nessun altro percorso risulti
     * privo di ostacoli (altre mine)**/

    List<Floor.OnFloorPosition> botMoves;

    /****************************/

    public PrimaProva(TachoMaster tachoMaster , Floor floor , SensorMaster sensorMaster){
        this.tachoMaster=tachoMaster;
        this.floor=floor;
        this.sensorMaster=sensorMaster;
        botMoves=new ArrayList<>();
        tileDim = Math.round(floor.getTileWidth()*20+20); /**è la dimensione in step per i motori**/
    }

    public void findMine() throws InterruptedException, ExecutionException, IOException, Floor.AllPositionVisited {

        boolean motors_going=false;
        boolean nowhereToGo=true;

        botMoves.add(floor.getStartPosition()); /**aggiungo posizione di partenza --> ultima posizione dell'inverso**/

        Floor.OnFloorPosition newPosition = new Floor.OnFloorPosition(-1,-1); //TODO


        while (!sensorMaster.objectInProximity()) {

            if (nowhereToGo) {

                Log.d("PRIMA PROVA 3 : ", "ACTUALPOSITION :  ROW : " + floor.getActualPosition().getRow() + " COL : " + floor.getActualPosition().getCol());

                tachoMaster.resetMovementMotorsPosition();


                newPosition = floor.chooseNextPosition();


                Log.d("PRIMA PROVA 3 : ", "POSIZIONE SCELTA -----> RIGA : " + newPosition.getRow() + " COLONNA : " + newPosition.getCol());


                Floor.Direction d = floor.changeBotDirection(newPosition);

                Floor.TurnDirection turn = floor.turnDirection(d);


                tachoMaster.turnBot(10, 183, turn);

                nowhereToGo = false;
            }

            if (!motors_going && floor.getActualPosition().compareTo(newPosition) != 0) {
                Log.d("PRIMA PROVA 3 : ", "MOTORS GOING ");

                tachoMaster.resetMovementMotorsPosition();
                tachoMaster.moveStraight(20);
                motors_going = true;

            }
            if (tachoMaster.getMotorsCount() > tileDim && motors_going) {
                Log.e("PRIMA PROVA 3 : ", "UPDATING POSITION");
                floor.updateBotPosition();
                floor.updateNextPosition();

                Log.e("PRIMA PROVA 3 : ", "ACTUALPOSITION :  ROW : " + floor.getActualPosition().getRow() + " COL : " + floor.getActualPosition().getCol());

                if (floor.getActualPosition().compareTo(newPosition) == 0) {
                    Log.e("PRIMA PROVA 3 : ", "STOPPING MOTORS");
                    tachoMaster.stopMotors();
                    motors_going = false;
                    nowhereToGo = true;
                    tachoMaster.countAdjustment(20, Math.round(tachoMaster.getMotorsCount()), tileDim);
                    tachoMaster.resetMovementMotorsPosition();
                    botMoves.add(new Floor.OnFloorPosition(floor.getActualPosition().getRow(), floor.getActualPosition().getCol()));
                    Log.e("PRIMA PROVA 3 : ", "POSITION ADDED : " + botMoves.get(botMoves.size() - 1).getRow() + botMoves.get(botMoves.size() - 1).getCol());
                }
                tachoMaster.resetMovementMotorsPosition();

            }

        }   /**CHIUSURA while(!sensorMaster.objectInProximity())**/

    }

    public Floor.OnFloorPosition takeMine() throws InterruptedException, ExecutionException, IOException {

        tachoMaster.takeMine(-20,3000);
        tachoMaster.countAdjustment(20,Math.round(tachoMaster.getMotorsCount()),630 ); //TODO
        floor.updateBotPosition();
        floor.updateNextPosition();
        tachoMaster.takeMine(-20,2000);

        Log.e("PRIMA PROVA 3 : ", "ACTUALPOSITION :  ROW : "+floor.getActualPosition().getRow()+" COL : "+floor.getActualPosition().getCol());


        tachoMaster.resetMovementMotorsPosition();

        return floor.getActualPosition();
    }

    public void goToStartPosition() throws InterruptedException, ExecutionException, IOException {
        boolean motors_going=false;
        boolean nowhereToGo=true;
        int i = botMoves.size()-1;
        Floor.OnFloorPosition newPosition = new Floor.OnFloorPosition(-1,-1); //TODO
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
            if(tachoMaster.getMotorsCount()>tileDim && motors_going){  /** dovrei avere (grandezza della piastrella *20)+20*/
                floor.updateBotPosition();
                floor.updateNextPosition();
                if(floor.getActualPosition().compareTo(newPosition)==0) {
                    tachoMaster.stopMotors();
                    motors_going=false;
                    nowhereToGo=true;
                    tachoMaster.countAdjustment(20, Math.round(tachoMaster.getMotorsCount()), tileDim);
                    i--;
                }

                tachoMaster.resetMovementMotorsPosition();

                Log.e("PRIMA PROVA 3 : ", "ACTUALPOSITION :  ROW : "+floor.getActualPosition().getRow()+" COL : "+floor.getActualPosition().getCol());

            }
            /***TOGLIERE LA MOSSA**/
        }

    }

    public void releaseMine() throws InterruptedException, ExecutionException, IOException {
        tachoMaster.countAdjustment(20,Math.round(tachoMaster.getMotorsCount()),tileDim/2);
        tachoMaster.releaseMine(20,3000);
        tachoMaster.countAdjustment(-20,tileDim/2,Math.round(tachoMaster.getMotorsCount()));
    }
}
