package com.example.myapplication.gioUtil;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import it.unive.dais.legodroid.lib.plugs.GyroSensor;
import it.unive.dais.legodroid.lib.plugs.UltrasonicSensor;

public class SensorMaster {

    private UltrasonicSensor ultra;
    private GyroSensor gyro;



    public SensorMaster(UltrasonicSensor ultra , GyroSensor gyro){
        this.ultra = ultra;
        this.gyro = gyro;
    }

    public boolean objectInProximity() throws IOException, ExecutionException, InterruptedException {
        Float f =ultra.getDistance().get();
        if(f<6)
            return true;
        else return false;
    }

    public float getGyroAngle () throws IOException, ExecutionException, InterruptedException {
        float f = gyro.getAngle().get();
        return f;
    }
}
