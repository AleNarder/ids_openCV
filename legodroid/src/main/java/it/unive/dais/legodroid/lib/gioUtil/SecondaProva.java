package it.unive.dais.legodroid.lib.gioUtil;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static android.content.Context.SENSOR_SERVICE;

public class SecondaProva {

    TachoMaster tachoMaster ;
    Floor floor ;
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

    Queue<Floor.OnFloorPosition> minePosition; /**lista posizioni mine da raccogliere**/

    public SecondaProva(Context context , TachoMaster tachoMaster , Floor floor , SensorMaster sensorMaster , Queue<Floor.OnFloorPosition> minePosition){
        this.tachoMaster=tachoMaster;
        this.floor=floor;
        this.sensorMaster=sensorMaster;
        botMoves=new ArrayList<>();
        tileDim = Math.round(floor.getTileWidth()*20+20); /**è la dimensione in step per i motori**/
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

    public void findMine(){
        boolean motors_going=false;
        Floor.OnFloorPosition destination= minePosition.poll();

        while(floor.getNextPosition().compareTo(destination)!=0){
            /**
             * se c'è una strada seguo quella (devo vedere come)
             * altrimenti mi sposto o di una colonna o di una riga**/
        }

    }
}
