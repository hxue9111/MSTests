package cmsc436.mstests;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.hardware.SensorManager;
import android.app.Activity;
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


public class LevelTestActivity extends Activity implements SensorEventListener {
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    float x, y, z;
    private TextView currentX, currentY, currentZ, display_score;
    private LevelView visual;
    Button level_test_button ;
    ImageView img;
    long last_timestamp = 0;
    double score ;
    TextView timer;
    double leftHandScore = -1;
    double rightHandScore = -1;

    public float mPosX;
    public float mPosY;
    private float mVelX;
    private float mVelY;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_test);


        display_score = (TextView)findViewById(R.id.score);
        visual = (LevelView)findViewById(R.id.visual);
        visual.setVisibility(View.INVISIBLE);

        setStartView();


    }

    public void setStartView() {
        score = 0;

        visual.updateDrawing(0,0,0);
        visual.setVisibility(View.INVISIBLE);
        level_test_button = (Button)findViewById(R.id.level_test_start);
        level_test_button.setVisibility(View.VISIBLE);

        level_test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                level_test_button.setVisibility(View.INVISIBLE);
                timer = (TextView) findViewById(R.id.time_remaining);
                timer.setVisibility(View.VISIBLE);

                new CountDownTimer(3000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        timer.setText("Starting Test in: " + millisUntilFinished / 1000);

                    }
                    public void onFinish() {
                        levelTestStart();
                    }
                }.start();

            }
        });


    }
    public void levelTestStart() {
        visual.setVisibility(View.VISIBLE);
        timer = (TextView) findViewById(R.id.time_remaining);
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("Seconds Remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                if (leftHandScore == -1) {
                    leftHandScore = score;
                    timer.setVisibility(View.INVISIBLE);

                    setStartView();
                } else {
                    rightHandScore = score;
                    setResultsView();
                }

            }
        }.start();


        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);



    }

    public void setResultsView() {
        senSensorManager.unregisterListener(this, senAccelerometer);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle resultData = new Bundle();
        resultData.putDouble("leftHandScore", leftHandScore);
        resultData.putDouble("rightHandScore", rightHandScore);
        LevelTestResultsFragment testResults = new LevelTestResultsFragment();
        testResults.setArguments(resultData);

        fragmentTransaction.replace(R.id.activity_level_test, testResults, "level test results");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            currentX = (TextView)findViewById(R.id.display_x);
            currentY = (TextView)findViewById(R.id.display_y);
            currentZ = (TextView)findViewById(R.id.display_z);

//            currentX.setText("Current X : " + Float.toString(x));
//            currentY.setText("Current Y : " + Float.toString(y));
//            currentZ.setText("Current Z : " + Float.toString(z));
//            display_score.setText("current score : " + score);


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

