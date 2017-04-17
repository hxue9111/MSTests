package cmsc436.mstests;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WalkingTestActivity extends Activity implements SensorEventListener, StepListener {

    private TextView textView;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;
    private TextView TvSteps;
    private Button BtnStart;
    private Stopwatch stopwatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_test);


            // Get an instance of the SensorManager
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            simpleStepDetector = new StepDetector();
            simpleStepDetector.registerListener(this);

            TvSteps = (TextView) findViewById(R.id.tv_steps);
            BtnStart = (Button) findViewById(R.id.btn_start);



            BtnStart.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    numSteps = 0;
                    BtnStart.setVisibility(View.GONE);
                    registerAccel();


                }
            });




        }


        public void registerAccel() {
            TvSteps.setText("Please walk until you hear a notification sound.");
            sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
            stopwatch = new Stopwatch();
            stopwatch.start();
        }

        public void unregisterAccel() {
            sensorManager.unregisterListener(this);
            stopwatch.stop();
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                simpleStepDetector.updateAccel(
                        event.timestamp, event.values[0], event.values[1], event.values[2]);
            }
        }

        @Override
        public void step(long timeNs) {
            numSteps++;
            //TvSteps.setText(TEXT_NUM_STEPS + numSteps);

            if (numSteps >= 25) {
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                unregisterAccel();
                float time = stopwatch.getElapsedTime()/1000;

                TvSteps.setText("Average Time Per Step:\n" + time/25 + " seconds");

            }

        }
}
