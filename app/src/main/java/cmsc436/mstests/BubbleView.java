package cmsc436.mstests;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static android.os.SystemClock.uptimeMillis;

/**
 * Created by vinhnguyen on 4/2/17.
 */

public class BubbleView extends View{
    private static final int NUM_OF_TRIAL = 10;
    private int hand = 1;

    Paint paint;
    boolean start = false;
    long startTime, totalTime, prevTime ;

    static int r =0, g =0, b =0, x, y;
    int counter = 0;
    int radius , width , height;
    Random random;

    OnBubbleUpdateListener listener;

    public BubbleView(Context context) {
        super(context);
        init(); 
    }

    public BubbleView(Context context, AttributeSet attr) {
        super(context,attr);
        init(); 
    }

    public BubbleView(Context context, AttributeSet attr, int defStyle) {
        super(context,attr,defStyle);
        init(); 
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        random = new Random();
        this.listener = null;
    }

    public void reset() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        random = new Random();
        counter = 1;
        invalidate();
    }

    private void setDimension(int h, int w) {
        this.height = h;
        this.width = w;
        x = this.width /2;
        y = this.height /2;
        radius = width /6;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (counter == 1) {
            setDimension(getHeight(),getWidth());
        }

        paint.setColor(Color.RED);
        canvas.drawCircle(x,y,radius,paint);
        moveBubble();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if(start) {
            if(counter < NUM_OF_TRIAL) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    long currTime = event.getEventTime();

                    long diffTime = currTime - prevTime;
                    totalTime += diffTime;
                    setVisibility(View.INVISIBLE);

                    counter++;

                    if(listener != null)
                        listener.onBubbleUpdate();

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            //making bubble appear after being popped
                            setVisibility(View.VISIBLE);

                            //setting prev time after bubble appears
                            prevTime = uptimeMillis();
                        }
                    }, getRandomTime(1000,2000));

                }
            }
            else {

                //for testing purposes, feel free to remove
                Toast.makeText(getContext(), "Average time:" + getAverageTime(), Toast.LENGTH_LONG).show();
                System.out.println("Average time: " + getAverageTime());

                listener.onDone();
            }
        }
        return super.onTouchEvent(event);
    }

    public void moveBubble() {
        x = (int) (Math.random() * (width - (2* radius))) + radius;
        y = (int) (Math.random() * (height - (2*radius))) + radius;
    }

    public void startGame() {
        start = true;
        counter =0;
        startTime = uptimeMillis();
        prevTime = startTime;
    }

    public int getCurrentCounter() { return counter;}

    public long getAverageTime() {
        return (totalTime / (NUM_OF_TRIAL));}

    public int getRandomTime(int min, int max) {
        return random.nextInt((max-min) +1) +min;
    }

    public interface OnBubbleUpdateListener  {
        void onBubbleUpdate();
        void onDone();
    }

    public void setOnBubbleUpdateListener(OnBubbleUpdateListener l) {
        listener = l;
    }
}
