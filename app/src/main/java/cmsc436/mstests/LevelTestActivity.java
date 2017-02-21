package cmsc436.mstests;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.SensorManager;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class LevelTestActivity extends Activity {
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    float x, y, z;
    private TextView currentX, currentY, currentZ;
    Button level_test_button ;
    TextView text_prompt;
    ImageView img;
    String left_hand_test_label = "Hold your phone on left hand as steady as possible ";
    String right_hand_test_label = "Hold your phone on right hand as steady as possible ";
    String start_label = "Push to start test";
    double score, left_hand_score = -1 , right_hand_score = -1;
    private LevelView visual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_test);

        visual = (LevelView)findViewById(R.id.visual);
        visual.setVisibility(View.INVISIBLE);



        level_test_button = (Button)findViewById(R.id.level_test_start);
        text_prompt = (TextView)findViewById(R.id.level_test_prompt);
        setStartView(left_hand_test_label);

    }

    // Make the text take up the whole screen
    private void setStartView(String hand_test_name) {
        text_prompt.setText(hand_test_name);
        level_test_button.setVisibility(View.VISIBLE);
        level_test_button.setText(start_label);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(listener, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        level_test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCountdownView();
            }
        });

    }


    //countdown until test begins
    private void setCountdownView() {
        level_test_button.setVisibility(View.GONE);
        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
                text_prompt.setText("Test Starting in: " + millisUntilFinished / 1000);
            }

            public void onFinish() {

                setTimerView();
            }
        }.start();
    }
    //Start timer
    private double setTimerView() {
        score = 0;
        visual.setVisibility(View.VISIBLE);
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                text_prompt.setText("Second Remaining " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                visual.setVisibility(View.INVISIBLE);

                if (left_hand_score == -1) {
                    left_hand_score  = score;
                    senSensorManager.unregisterListener(listener);
                    setStartView(right_hand_test_label);
                } else if (right_hand_score == -1){
                    right_hand_score = score ;
                    senSensorManager.unregisterListener(listener);
                    resultView();
                }

            }
        }.start();
        return score;
    }

    //set result
    public void resultView() {
       // text_prompt.setText("Your score is : " + Math.round(score));
        text_prompt.setText("Results:\n Left hand: " + Math.round(left_hand_score) +
                "\nRight hand: " + Math.round(right_hand_score));
    }


    private final SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }


        @TargetApi(Build.VERSION_CODES.CUPCAKE)
        public void onSensorChanged(SensorEvent event) {
            Sensor mySensor = event.sensor;

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

            /*currentX = (TextView)findViewById(R.id.display_x);
            currentY = (TextView)findViewById(R.id.display_y);
            currentZ = (TextView)findViewById(R.id.display_z);

            currentX.setText("Current X : " + Float.toString(x));
            currentY.setText("Current Y : " + Float.toString(y));
            currentZ.setText("Current Z : " + Float.toString(z));
*/
                if (score(x, y, z)) {
                    visual.updateDrawing(x, y, z);
                }

            }
        }

        public boolean score(float x, float y, float z) {
            final int SENSITIVITY = 1;
            double displacement = Math.abs(x) + Math.abs(y);

            if (displacement > SENSITIVITY) {
                score += displacement;
                return true;
            }

            return false;
        }
        };
}




