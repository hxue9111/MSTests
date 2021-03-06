package cmsc436.mstests;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import edu.umd.cmsc436.sheets.Sheets;

public class SpiralTestActivity extends Activity implements Sheets.Host{
    private Sheets sheet;
    Date date;
    Button spiral_test_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spiral_test);

        spiral_test_button = (Button)findViewById(R.id.spiral_test_button);

        spiral_test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpSpiralView();
            }
        });
    }


    private void setUpSpiralView() {
        TextView text_prompt = (TextView)findViewById(R.id.spiral_test_instructions);
        text_prompt.setVisibility(View.GONE);

        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);


        final DrawingView canvas = new DrawingView(this, mPaint);
        canvas.setBackgroundResource(R.drawable.ic_spiral);
        RelativeLayout rel = (RelativeLayout) findViewById(R.id.activity_spiral_test);
        rel.addView(canvas);

        spiral_test_button = (Button) findViewById(R.id.spiral_test_button);
        spiral_test_button.setText("Done");

        spiral_test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvas.saveDrawing();
                sendToSheets(canvas, Sheets.TestType.LH_SPIRAL);
                sendToSheets(canvas, Sheets.TestType.RH_SPIRAL);
                finish();
            }
        });


    }

    private void sendToSheets(DrawingView v, edu.umd.cmsc436.sheets.Sheets.TestType type) {
        // Send data to sheets
        sheet = new edu.umd.cmsc436.sheets.Sheets(this, this, getString(R.string.app_name), getString(R.string.class_sheet), getString(R.string.private_sheet));


//        float[] result = {(float) scores};
        date = new Date();

//        sheet.writeData(type, getString(R.string.patientID), result);
//        sheet.writeTrials(type, getString(R.string.patientID), result) ;

        String pathFilename = date.toString() + ": Path, " + type.toString();
        sheet.uploadToDrive(getString(R.string.img_folder), pathFilename, v.getDrawingCache());
    }

    public class DrawingView extends View {

        public int width;
        public  int height;
        private Bitmap mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;
        private Paint mPaint;

        private DrawingView(Context c) {
            super(c);
            context=c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);
        }

        public DrawingView(Context c, Paint mPaint) {
            this(c);
            this.mPaint = mPaint;
        }

        private void saveDrawing() {
            this.setDrawingCacheEnabled(true);
            this.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            Bitmap bitmap = this.getDrawingCache();
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            File file = new File(path+"/image.png");
            FileOutputStream ostream;
            try {
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
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath( mPath,  mPaint);
            canvas.drawPath( circlePath,  circlePaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath,  mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }

    @Override
    public int getRequestCode(edu.umd.cmsc436.sheets.Sheets.Action action) {
        switch (action) {
            case REQUEST_ACCOUNT_NAME:
                return 1001;
            case REQUEST_AUTHORIZATION:
                return 1002;
            case REQUEST_PERMISSIONS:
                return 1003;
            case REQUEST_PLAY_SERVICES:
                return 1004;
            default:
                return -1;
        }
    }

    @Override
    public void notifyFinished(Exception e) {
        if (e != null) {
            throw new RuntimeException(e);
        }
        Log.i(getClass().getSimpleName(), "Done");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        this.sheet.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.sheet.onActivityResult(requestCode, resultCode, data);
    }
}
