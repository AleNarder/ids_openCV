package it.unive.dais.legodroid.lib.gioUtil;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import it.unive.dais.legodroid.lib.plugs.UltrasonicSensor;

public class SensorMaster {

    private UltrasonicSensor ultra;

    public SensorMaster(UltrasonicSensor ultra){
        this.ultra = ultra;
    }

    public boolean objectInProximity() throws IOException, ExecutionException, InterruptedException {
        Float f =ultra.getDistance().get();
        //   Log.e("SENSOR MASTER : ","DISTANZA = "+f);
        if(f<9)
            return false;
        else return false;
    }
}
