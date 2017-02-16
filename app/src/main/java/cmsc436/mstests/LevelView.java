package cmsc436.mstests;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class LevelView extends View {
    Paint targetPaint, ballPaint;
    int height, width, centerX, centerY, ballX, ballY; // View should be square
    float offset_x, offset_y;


    public LevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        targetPaint = new Paint();
        ballPaint = new Paint();

        ballPaint.setStyle(Paint.Style.FILL);
        width = getWidth();
        height = getHeight();
        centerX = width/2;
        centerY = height/2;
        ballX = getWidth()/2;
        ballY = getHeight()/2;
        setBackgroundColor(Color.GREEN);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(centerX, centerY, Math.min(width/4, height/4), targetPaint);

        canvas.drawCircle(getWidth()/2 - offset_x, getHeight()/2 + offset_y, 40, ballPaint);
    }

    public void updateDrawing(float x, float y, float z) {
        offset_x += x;
        offset_y += y;

        if( offset_x > 0 ) {
            offset_x = Math.min(getWidth()/2, offset_x);
        }
        else{
            offset_x = Math.max(-getWidth()/2, offset_x);
        }
        if ( offset_y > 0 ) {
            offset_y = Math.min(getHeight()/2, offset_y);
        }
        else{

            offset_y = Math.max(-getHeight()/2, offset_y);
        }



        invalidate();
    }
}
