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
    private TextView currentX, currentY, currentZ;
    Button level_test_button ;
    ImageView img;
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

    public void displayCleanValues() {



    }
    public void displayCurrentValues() {

        currentX = (TextView)findViewById(R.id.display_x);
        currentY = (TextView)findViewById(R.id.display_y);
        currentZ = (TextView)findViewById(R.id.display_z);

        currentX.setText("Current X : " + Float.toString(x));
        currentY.setText("Current Y : " + Float.toString(y));
        currentZ.setText("Current Z : " + Float.toString(z));

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
        }
    }
    public class Particle {
        /* coefficient of restitution */
        private static final float COR = 0.7f;

        public float mPosX;
        public float mPosY;
        private float mVelX;
        private float mVelY;

        public void updatePosition(float sx, float sy, float sz, long timestamp) {
            float dt = (System.nanoTime() - timestamp) / 1000000000.0f;
            mVelX += -sx * dt;
            mVelY += -sy * dt;

            mPosX += mVelX * dt;
            mPosY += mVelY * dt;
        }

        public void resolveCollisionWithBounds(float mHorizontalBound, float mVerticalBound) {
            if (mPosX > mHorizontalBound) {
                mPosX = mHorizontalBound;
                mVelX = -mVelX * COR;
            } else if (mPosX < -mHorizontalBound) {
                mPosX = -mHorizontalBound;
                mVelX = -mVelX * COR;
            }
            if (mPosY > mVerticalBound) {
                mPosY = mVerticalBound;
                mVelY = -mVelY * COR;
            } else if (mPosY < -mVerticalBound) {
                mPosY = -mVerticalBound;
                mVelY = -mVelY * COR;
            }
        }
    }


}

