package cmsc436.mstests;

import android.content.Context;
import android.hardware.SensorManager;
import android.app.Activity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

    public float mPosX;
    public float mPosY;
    private float mVelX;
    private float mVelY;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_test);

        level_test_button = (Button)findViewById(R.id.level_test_start);

        level_test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                levelTestStart();
            }
        });
        display_score = (TextView)findViewById(R.id.score);
        visual = (LevelView)findViewById(R.id.visual);

    }
    public void levelTestStart() {
        level_test_button.setVisibility(View.INVISIBLE);
//        ImageView img = (ImageView)findViewById(R.id.target);
//        img.setVisibility(View.VISIBLE);


        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

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

            currentX.setText("Current X : " + Float.toString(x));
            currentY.setText("Current Y : " + Float.toString(y));
            currentZ.setText("Current Z : " + Float.toString(z));
            display_score.setText("current score : " + score);


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

