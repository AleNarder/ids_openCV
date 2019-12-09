package it.unive.dais.legodroid.lib.gioUtil;

import android.hardware.SensorEventListener;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import it.unive.dais.legodroid.lib.plugs.UltrasonicSensor;

public class SensorMaster {

    private UltrasonicSensor ultra;


    private SensorEventListener sensorListener;

    public SensorMaster(UltrasonicSensor ultra){
        this.ultra = ultra;

    }

    public boolean objectInProximity() throws IOException, ExecutionException, InterruptedException {
        Float f =ultra.getDistance().get();
        if(f<10)
            return true;
        else return false;
    }
}
