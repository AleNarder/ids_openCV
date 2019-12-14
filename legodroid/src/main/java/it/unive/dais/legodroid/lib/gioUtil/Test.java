package it.unive.dais.legodroid.lib.gioUtil;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Test {

    private TachoMaster tachoMaster;
    private Floor floor;
    private SensorMaster sensorMaster;

    public Test(TachoMaster tachoMaster , Floor floor , SensorMaster sensorMaster){
        this.sensorMaster=sensorMaster;
        this.tachoMaster=tachoMaster;
        this.floor=floor;
    }

    public void PrimaProva(int mine) throws InterruptedException, ExecutionException, IOException {

        while(mine>0){
            while(!sensorMaster.objectInProximity()){
                if((floor.getNextPosition().getRow()>=0 &&floor.getNextPosition().getRow() < floor.getWidth() )
                        && (floor.getNextPosition().getCol() < floor.getHeight() && floor.getNextPosition().getCol()>=0) &&
                        (floor.getNextPosition().getCol()==floor.getPrevPosition().getCol()+1 || floor.getBotDirection()== Floor.Direction.VERTICAL_UP ||
                                floor.getBotDirection()== Floor.Direction.VERTICAL_DOWN)){
                    tachoMaster.resetMovementMotorsPosition();
                    tachoMaster.moveStepstraight(20,0,630);
                    floor.updateBotPosition();
                    /*salvo posizioni*/
                }
                else{
                    Floor.TurnDirection turnDirection = floor.chooseNDPrimaProva(); /*TODO*/
                    floor.updateNextPosition();
                    tachoMaster.resetMovementMotorsPosition();
                    tachoMaster.getMotorsPosition();
                    switch(turnDirection){
                        case TURN_LEFT:tachoMaster.turnNinetyLeft(20,183);break;
                        case TURN_RIGHT:tachoMaster.turnNinetyRight(20,183);break;
                    }
                }
            }
            // tachoMaster.takeMine();
            /*gira 180 fai percorso inverso*/
            // tachoMaster.releaseMine();
            mine--;
            /*gira 180 e riprendi ad andare*/
        }
    }

    public void PrimaProva2 (int mine) throws InterruptedException, ExecutionException, IOException {
        boolean motors_going = false;
        while(!sensorMaster.objectInProximity()){
            if(!motors_going){
                tachoMaster.resetMovementMotorsPosition();
                tachoMaster.moveStraight(20);
                motors_going=true;
            }
            if(tachoMaster.getMotorsCount()>630){
                Log.e("HEIII","ZIO KEN");
                floor.updateBotPosition();
                if((floor.getNextPosition().getRow()<0 || floor.getNextPosition().getRow() >= floor.getWidth() )
                        || (floor.getNextPosition().getCol() >= floor.getHeight() || floor.getNextPosition().getCol()<0) ){
                    tachoMaster.stopMotors();
                    tachoMaster.turnNinetyRight(20,183);
                    tachoMaster.turnNinetyRight(20,183);
                    tachoMaster.turnNinetyRight(20,183);
                    if(tachoMaster.getMotorsCount()>630){
                        int step =Math.round(tachoMaster.getMotorsCount())-630;
                        // tachoMaster.moveStepstraight(-20,0, step);
                    }
                }
                tachoMaster.resetMovementMotorsPosition();
            }
        }
        tachoMaster.stopMotors();
        tachoMaster.takeMine(-20,3000);
        tachoMaster.turnNinetyRight(20,183);
        tachoMaster.turnNinetyRight(20,183);
        tachoMaster.moveStepstraight(20,0,630);

    }

}
