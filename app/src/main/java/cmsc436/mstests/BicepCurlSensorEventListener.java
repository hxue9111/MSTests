package cmsc436.mstests;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.ArrayList;

/**
 * Created by katelinmontgomery on 3/27/17.
 */

public class BicepCurlSensorEventListener implements SensorEventListener {
    BicepCurlManager manager;
    Long timeSinceLastCurl;
    public ArrayList<Long> curlTimes = new ArrayList<Long>();
    public BicepLegTestActivity.CurlCallback cb;

    public BicepCurlSensorEventListener(BicepLegTestActivity.CurlCallback callback) {
        this.cb = callback;
        manager = new BicepCurlManager() {
            @Override
            protected void onRepIncrease() {
                System.out.println("rep increase");
                timeCurl();
                cb.onCurl(curlTimes);
            }
        };
    }

    public void init(){
        resetTimer();
    }

    public void timeCurl(){
        Long currTime = System.currentTimeMillis();
        curlTimes.add(currTime - timeSinceLastCurl);
        resetTimer();
    }

    public void resetTimer(){
        timeSinceLastCurl = System.currentTimeMillis();
    }

    @Override
    public void onAccuracyChanged(Sensor s, int i){
    }

    @Override
    public void onSensorChanged(SensorEvent s){
        if(s.sensor.getType() == Sensor.TYPE_GRAVITY){
            manager.updateEvent(s);
        }
    }
}
