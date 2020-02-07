package com.example.myapplication.gioUtil;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import it.unive.dais.legodroid.lib.plugs.TachoMotor;

import static com.example.myapplication.gioUtil.Floor.TurnDirection.TURN_LEFT;
import static com.example.myapplication.gioUtil.Floor.TurnDirection.TURN_RIGHT;
import static com.example.myapplication.gioUtil.Floor.TurnDirection.U_INVERSION;

public class TachoMaster {
    @Nullable
    private TachoMotor leftMotor , rightMotor, armMotor ;

    public TachoMaster(TachoMotor rightMotor , TachoMotor leftMotor , TachoMotor armMotor){
        this.rightMotor = rightMotor;
        this.leftMotor = leftMotor;
        this.armMotor = armMotor;
    }

    private static void waitMotor(TachoMotor m) throws IOException, ExecutionException, InterruptedException {
        m.waitCompletion();
        m.waitUntilReady();
    }
    private static void startMotor(TachoMotor m) throws IOException {
        m.start();
    }
    private static void stopMotor(TachoMotor m) throws IOException {
        m.stop();
    }
    public void moveStraight(int speed) throws IOException, ExecutionException, InterruptedException {

        rightMotor.setSpeed(speed);
        //waitMotor(rightMotor);

        leftMotor.setSpeed(speed);
        //waitMotor(leftMotor);

        startMotor(rightMotor);
        startMotor(leftMotor);
    }

    private  static void turnBystep(int speed , int step1 , int step2 , int step3 , boolean brake , TachoMotor m) throws IOException, ExecutionException, InterruptedException{
        m.setStepSpeed(speed,step1,step2,step3,brake);
        waitMotor(m);
    }
    public void moveLeftByStep(int speed , int step1 , int step2 , int step3 , boolean brake) throws IOException, ExecutionException, InterruptedException {
        turnBystep(speed,step1,step2,step3,brake,leftMotor);
    }

    public void moveRightByStep (int speed , int step1 , int step2 , int step3 , boolean brake) throws IOException, ExecutionException, InterruptedException {
        turnBystep(speed,step1,step2,step3,brake,rightMotor);
    }

    private void moveBack(int speed) throws InterruptedException, ExecutionException, IOException {
        moveStraight(-speed);
    }

    /** TODO**/
    /*public void moveStraightByStep(int speed , int step1 , int step2 , int step3 , boolean brake) throws IOException, ExecutionException, InterruptedException {

        rightMotor.setStepSpeed(speed, step1, step2 ,step3 , brake);
        waitMotor(rightMotor);
        leftMotor.setStepSpeed(speed, step1, step2, step3, brake);
        waitMotor((leftMotor));

    }*/

    public void moveLeft(int speed){
        //TODO
    }

    public void moveStepstraight(int speed , int step1,int step2) throws IOException, ExecutionException, InterruptedException {
        rightMotor.setStepSpeed(speed,step1,step2,0,true);
        leftMotor.setStepSpeed(speed,step1,step2,0,true);
        rightMotor.waitCompletion();
        leftMotor.waitCompletion();
       // leftMotor.waitUntilReady();
        //rightMotor.waitUntilReady();
        //stopMotors();

    }

    public void resetMovementMotorsPosition() throws IOException {
        rightMotor.clearCount();
        leftMotor.clearCount();
    }

    public void resetArmMotorPosition() throws IOException {
        armMotor.clearCount();
    }

    public void stopMotors() throws IOException {
        rightMotor.stop();
        leftMotor.stop();
    }

    public void getMotorsPosition() throws IOException, ExecutionException, InterruptedException {
        Log.e("TACHOMASTER", "RIGHTMOTORPOSITION : "+rightMotor.getPosition().get());
        Log.e("TACHOMASTER", "LEFTMOTORPOSITION : "+leftMotor.getPosition().get());


    }

    public void turn(int speed , TachoMotor m1 , TachoMotor m2,int step) throws IOException, ExecutionException, InterruptedException {
        m1.setStepSpeed(speed,step,0,0,true);
        m2.setStepSpeed(-speed,step,0,0,true);
        m1.waitCompletion();
        m2.waitCompletion();
       // m1.waitUntilReady();
       // m2.waitUntilReady();

    }

    public void turnNinetyLeft(int speed , int step) throws IOException, ExecutionException, InterruptedException {
        turn(speed,rightMotor,leftMotor,step);
    }
    public void turnNinetyRight(int speed , int step) throws IOException, ExecutionException, InterruptedException {
        turn(speed,leftMotor,rightMotor,step);

    }
    public void turnNinetyRight(int speed , SensorMaster sensorMaster) throws IOException, ExecutionException, InterruptedException {
        boolean motors_going = false;
        float tmp;
        float init_angle=sensorMaster.getGyroAngle();
       // Log.e("TACHOMASTER : " , "INIT ANGLE = "+init_angle);
        while(Math.abs((tmp=sensorMaster.getGyroAngle())-init_angle)<80){
           // Log.e("TACHOMASTER : " , "ANGLE VARIATION = "+tmp);
            if(!motors_going){
                rightMotor.setSpeed(-speed);
                leftMotor.setSpeed(speed);
                rightMotor.start();
                leftMotor.start();
                motors_going=true;
            }

        }
        //Log.e("SENSOR MASTER = ", "DIFFERENZA ANGOLI = "+Math.round(sensorMaster.getGyroAngle()-init_angle));

        rightMotor.stop();
        leftMotor.stop();

    }
    public void turnNinetyLeft(int speed, SensorMaster sensorMaster) throws InterruptedException, ExecutionException, IOException {
        boolean motors_going = false;
        float tmp;
        float init_angle=sensorMaster.getGyroAngle();
       // Log.e("TACHOMASTER : " , "INIT ANGLE = "+init_angle);
        while(Math.abs(init_angle-(tmp=sensorMaster.getGyroAngle()))<80){ //TODO
           // Log.e("TACHOMASTER : " , "ANGLE VARIATION = "+tmp);
            if(!motors_going){
                rightMotor.setSpeed(speed);
                leftMotor.setSpeed(-speed);
                leftMotor.start();
                rightMotor.start();
                motors_going=true;
            }

        }
       // Log.e("SENSOR MASTER = ", "DIFFERENZA ANGOLI = "+Math.round(init_angle-sensorMaster.getGyroAngle()));
        rightMotor.stop();
        leftMotor.stop();
    }
    public void uInversionTurn(int speed , int step) throws InterruptedException, ExecutionException, IOException {
        turnNinetyRight(speed,step);
        turnNinetyRight(speed,step);
    }

    public void uInversionTurn(int speed , SensorMaster sensorMaster) throws InterruptedException, ExecutionException, IOException {
        turnNinetyRight(speed, sensorMaster);
        turnNinetyRight(speed,sensorMaster);
    }

    public float getMotorsCount() throws IOException, ExecutionException, InterruptedException {
        return Math.max(rightMotor.getPosition().get(),leftMotor.getPosition().get());
    }

    public void armDown(int speed , int step) throws IOException {
        armMotor.setTimeSpeed(speed,0,step,0,true);
        armMotor.waitCompletion();
        armMotor.stop();
    }
    public void armDown(int speed) throws IOException, ExecutionException, InterruptedException {
        //armMotor.setTimeSpeed(speed,0,step,0,true);
       // armMotor.waitCompletion();
       // armMotor.stop();
        armMotor.setSpeed(speed);
        armMotor.start();
    }
    public void takeMine(int speed , int step) throws IOException, ExecutionException, InterruptedException {
        armDown(speed,step);
    }

    public void releaseMine(int speed,int step) throws InterruptedException, ExecutionException, IOException {
        armDown(-speed,step);
    }


    public void turnBot(int speed , int step, Floor.TurnDirection turn) throws InterruptedException, ExecutionException, IOException {
        switch(turn){
            case TURN_RIGHT:
                turnNinetyRight(speed,step);
                break;
            case TURN_LEFT:
                turnNinetyLeft(speed,step);
                break;
            case U_INVERSION:
                uInversionTurn(speed,step);
                break;
        }
        Thread.sleep(1000);
    }

    public void turnBot(int speed , Floor.TurnDirection turn , SensorMaster sensorMaster , double inclination) throws InterruptedException, ExecutionException, IOException {
        //Thread.sleep(2000);
        switch(turn){
            case TURN_RIGHT:
                turnNinetyRight(speed,sensorMaster);
                break;
            case TURN_LEFT:
                turnNinetyLeft(speed,sensorMaster);
                break;
            case U_INVERSION:
                uInversionTurn(speed,sensorMaster);
                break;
        }


        /*if(inclination!=Double.NaN){

            if(inclination<89.7) {
                //  Log.e("TACHOMASTER", "Turn left inclination");
                turn(10,rightMotor,leftMotor,5);
            }
            else
            if(inclination>90.7) {
                //  Log.e("TACHOMASTER", "Turn right inclination");
                turn(10,leftMotor,rightMotor,5);


            }

        }*/

        Thread.sleep(1500);



    }

    public void countAdjustment(int speed , int actualStep , int finalStep) throws InterruptedException, ExecutionException, IOException {
        if(finalStep-actualStep>0)
            moveStepstraight(speed,0,finalStep-actualStep);
        else
            moveStepstraight(-speed,0,actualStep-finalStep);
    }

    public boolean adjustTurn( Floor.TurnDirection turn, float initAngle, float finalAngle , int stepNinety) throws InterruptedException, ExecutionException, IOException {
        boolean okAngle=false;
        Float diff;
        Log.e("TACHOMASTE :","NON MODIFICATI   ------- ANGOLO INIZIALE = "+initAngle+" ANGOLO FINALE = "+finalAngle);

        if(turn!=U_INVERSION) {
            if (initAngle <= 360 && initAngle >= 270 && finalAngle >= 0 && finalAngle <= 90)
                initAngle = initAngle - 360;
            if (finalAngle <= 360 && finalAngle >= 270 && initAngle >= 0 && initAngle <= 90)
                finalAngle = finalAngle - 360;
        }

        Log.e("TACHOMASTE :","ANGOLO INIZIALE = "+initAngle+" ANGOLO FINALE = "+finalAngle);
        switch(turn){
            case NO_TURN :okAngle=true;
            case TURN_LEFT:

                if((diff=Math.abs(finalAngle-initAngle))<75) {
                    turnBot(10, Math.round(stepNinety/90*(90-diff)), TURN_LEFT);
                }
                else
                if(Math.abs(finalAngle-initAngle)>85) {
                    turnBot(10, Math.round(stepNinety/90*(diff-90)), TURN_RIGHT);
                }
                else okAngle=true;

            case TURN_RIGHT:


                if((diff=Math.abs(finalAngle-initAngle))<75) {
                    turnBot(10,Math.round(stepNinety/90) , TURN_RIGHT);
                    Log.e("TACHOMASTE :","CONTINUO DESTRA");

                }
                else
                if((diff=Math.abs(finalAngle-initAngle))>85) {
                    turnBot(10, Math.round(stepNinety/90), TURN_LEFT);
                    Log.e("TACHOMASTE :","VADO SINISTRA");

                }
                else okAngle=true;


            case U_INVERSION:
                if((diff=Math.abs(finalAngle-initAngle))<165) {
                    turnBot(10, Math.round(stepNinety/90*(180-diff)), TURN_RIGHT);
                }
                else
                if((diff=Math.abs(finalAngle-initAngle))>175) {
                    turnBot(10, Math.round(stepNinety/90*(diff-180)), TURN_LEFT);
                }
                else okAngle=true;

        }
        return okAngle;
    }

    public void inclinationAdjustment(int i, double inclination) throws InterruptedException, ExecutionException, IOException {
        Log.e("TACHOMASTER","inclinazione="+inclination);
        if(inclination!=Double.NaN){
            if(inclination<88.0) {
                Log.e("TACHOMASTER", "Turn left inclination");
                turn(10, rightMotor, leftMotor, 5);
            }
            else
                if(inclination>92) {
                    Log.e("TACHOMASTER", "Turn right inclination");

                    turn(10, leftMotor, rightMotor, 5);
                }

        }
    }

    public void moduleSpeed(int speed, double inclination) throws IOException {
        Log.e("TACHOMASTER","inclinazione="+inclination);
        if(inclination!=Double.NaN){
            if(inclination<89.5) {
              //  Log.e("TACHOMASTER", "Turn left inclination");
                rightMotor.setSpeed(20+1);
                leftMotor.setSpeed(20);
            }
            else
            if(inclination>90.5) {
              //  Log.e("TACHOMASTER", "Turn right inclination");
                rightMotor.setSpeed(20);
                leftMotor.setSpeed(20+1);

            }

        }
    }
}
