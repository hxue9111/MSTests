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
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class LevelTestActivity extends Activity implements SensorEventListener {
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    float x, y, z;
    private TextView currentX, currentY, currentZ;
    Button level_test_button ;
    TextView text_prompt;
    ImageView img;
    String left_hand_test_label = "Press the button to hold your phone on left hand";
    String right_hand_test_label = "Press the button to hold your phone on right hand";
    String start_label = "Push to start test";
    double score;
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
        level_test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCountdownView();
            }
        });

    }
    //Start countdown
    private void setCountdownView() {
        level_test_button.setVisibility(View.GONE);
        new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                text_prompt.setText("Test starting in: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                levelTestStart();
            }
        }.start();
    }

    public void resultVIew() {
        text_prompt.setText("Your score is : " + Math.round(score));
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void levelTestStart() {
        visual.setVisibility(View.VISIBLE);
        level_test_button.setVisibility(View.INVISIBLE);
//        ImageView img = (ImageView)findViewById(R.id.target);
//        img.setVisibility(View.VISIBLE);


        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                text_prompt.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                visual.setVisibility(View.INVISIBLE);
                text_prompt.setVisibility(View.INVISIBLE);
                resultVIew();
                unregistered(senSensorManager);
            }

        };
    }

    public void unregistered(SensorManager sensor) {
        sensor.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    /*
    public void displayCurrentValues() {

        currentX = (TextView)findViewById(R.id.display_x);
        currentY = (TextView)findViewById(R.id.display_y);
        currentZ = (TextView)findViewById(R.id.display_z);

        currentX.setText("Current X : " + Float.toString(x));
        currentY.setText("Current Y : " + Float.toString(y));
        currentZ.setText("Current Z : " + Float.toString(z));

    }*/


    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
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
            if(score(x,y,z)){
                visual.updateDrawing(x,y,z);
            }

        }
    }

    public boolean score (float x, float y, float z) {

        final int SENSITIVITY = 1;
        double displacement = Math.abs(x) + Math.abs(y) ;

        if (displacement > SENSITIVITY) {
            score += displacement;
            return true;
        }

        return false;
    }
}




