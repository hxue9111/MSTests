package cmsc436.mstests;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BalanceTestActivity extends Activity implements SensorEventListener {

    private SensorManager sensorMan;
    private Sensor mSensor;
    private Vibrator v;

    private int score = 0;
    private double movementScaling = 50;
    private float buffer = 0.1f;
    private float[] initGravity;
    private float[] mGravity;

    TextView x,y,z,xview,yview,prompt;
    Button start_btn;
    BalanceTestDrawingView dv;

    float xpos = 0, ypos = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_test);


        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = sensorMan.getDefaultSensor(Sensor.TYPE_GRAVITY);
        v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

        prompt = (TextView)findViewById(R.id.prompt);
        start_btn = (Button)findViewById(R.id.start_balance_button);
        x = (TextView)findViewById(R.id.x);
        y = (TextView) findViewById(R.id.y);
        z = (TextView) findViewById(R.id.z);
        xview = (TextView) findViewById(R.id.xpos);
        yview = (TextView) findViewById(R.id.ypos);

        hideDebug();
        startScreen();
    }
    private void startScreen() {
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_btn.setVisibility(View.GONE);
                new CountDownTimer(4000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        prompt.setText("Test starting in: " + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        initializeTest();
                    }
                }.start();
            }
        });
    }
    private void initializeTest() {
        v.vibrate(5000);
        score = 0;
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(new Color().parseColor("#FF6B6B"));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(6);

        dv = new BalanceTestDrawingView(this, mPaint);
        FrameLayout fl = (FrameLayout) findViewById(R.id.canvas);
        fl.setVisibility(View.VISIBLE);
        fl.addView(dv);
        dv.setBackgroundColor(new Color().parseColor("#4ECDC4"));


        sensorMan.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        timeTest();
    }

    private void hideDebug() {
        x.setVisibility(View.GONE);
        y.setVisibility(View.GONE);
        z.setVisibility(View.GONE);
        xview.setVisibility(View.GONE);
        yview.setVisibility(View.GONE);
    }
    private void scoreScreen() {
        v.vibrate(5000);
        prompt.setText("Score(lower is better): " + score);
        dv.saveDrawing();
        dv.setVisibility(View.GONE);
    }
    private void timeTest() {
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                prompt.setText("Time Remaining: "+millisUntilFinished / 1000);
            }

            public void onFinish() {
                scoreScreen();
            }
        }.start();
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorMan.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mGravity = event.values.clone();

        if (initGravity == null) {
            initializeTracking();
        }
        double x = mGravity[0];
        double y = mGravity[1];
        double z = mGravity[2];
        this.x.setText("X acel: " + x);
        this.y.setText("Y acel: " + y);
        this.z.setText("Z acel: " + z);
        detectMovement();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // required method
    }

    private void initializeTracking() {
        initGravity = mGravity.clone();
    }

    private void detectMovement() {
        float delta;
        if (initGravity == null) {
            return;
        }
        delta = mGravity[0] - initGravity[0];
        if (Math.abs(delta) > buffer) {
            xpos -= delta*movementScaling;
            xview.setText("X-position: "+ xpos);
            initGravity[0] = mGravity[0];

            dv.update(xpos, ypos);
        }
        delta = mGravity[1] - initGravity[1];
        if (Math.abs(delta) > buffer) {
            ypos += delta*movementScaling;
            yview.setText("Y-position: "+ ypos);
            initGravity[1] = mGravity[1];

            dv.update(xpos, ypos);
        }
    }

    public class BalanceTestDrawingView extends View {

        private Path mPath;
        Context context;
        private Paint mPaint, centerPaint;
        private float xcen, ycen, centerRadius = 10;

        private BalanceTestDrawingView(Context c) {
            super(c);
            context=c;

            centerPaint = new Paint();

            centerPaint.setAntiAlias(true);
            centerPaint.setDither(true);
            centerPaint.setColor(new Color().parseColor("#292F36"));
            centerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            centerPaint.setStrokeJoin(Paint.Join.ROUND);
            centerPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        public BalanceTestDrawingView(Context c, Paint mPaint) {
            this(c);
            this.mPaint = mPaint;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            xcen = w/2;
            ycen = h/2;
        }

        private void saveDrawing() {
            if (ContextCompat.checkSelfPermission(BalanceTestActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(BalanceTestActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(BalanceTestActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            0);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }

            final String FILENAME = "balancetest";
            final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

            this.setDrawingCacheEnabled(true);
            this.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            Bitmap bitmap = this.getDrawingCache();

            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            String date = new SimpleDateFormat(DATE_FORMAT_NOW).format(new Date());

            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES),FILENAME + date +".png");
            FileOutputStream ostream;
            try {
                System.out.println(file.getAbsoluteFile());
                file.createNewFile();
                ostream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                ostream.flush();
                ostream.close();
                Toast.makeText(getApplicationContext(), "image saved", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (mPath != null) {
                canvas.drawPath( mPath,  mPaint);
            }

            canvas.drawCircle(xcen, ycen, centerRadius, centerPaint);
        }

        private void update(float x, float y) {
            if (mPath == null) {
                mPath = new Path();
                mPath.moveTo(xcen, ycen);
            }
            x += xcen;
            y += ycen;
            mPath.lineTo(x, y);
            mPath.moveTo(x, y);
            invalidate();
            score += Math.abs(x) + Math.abs(y);
        }


    }

}