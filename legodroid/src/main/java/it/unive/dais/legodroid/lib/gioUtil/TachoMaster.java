package it.unive.dais.legodroid.lib.gioUtil;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import it.unive.dais.legodroid.lib.plugs.TachoMotor;

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
        leftMotor.waitUntilReady();
        rightMotor.waitUntilReady();

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

    public void turn(int speed , TachoMotor m1 , TachoMotor m2) throws IOException, ExecutionException, InterruptedException {
        m1.setStepSpeed(speed,0,225,0,true);
        m2.setStepSpeed(-speed,0,225,0,true);
        m1.waitCompletion();
        m2.waitCompletion();
        m1.waitUntilReady();
        m2.waitUntilReady();

    }

    public void turnNinetyLeft(int speed) throws IOException, ExecutionException, InterruptedException {
        turn(speed,rightMotor,leftMotor);
    }
    public void turnNinetyRight(int speed) throws IOException, ExecutionException, InterruptedException {
        turn(speed,leftMotor,rightMotor);

    }

    public float getMotorsCount() throws IOException, ExecutionException, InterruptedException {
        return Math.max(rightMotor.getPosition().get(),leftMotor.getPosition().get());
    }

    public void armDown(int speed , int step) throws IOException, ExecutionException, InterruptedException {
        armMotor.setTimeSpeed(speed,0,step,0,true);
        armMotor.waitCompletion();
    }
    public void takeMine(int speed , int step) throws IOException, ExecutionException, InterruptedException {
        armDown(speed,step);
    }
}