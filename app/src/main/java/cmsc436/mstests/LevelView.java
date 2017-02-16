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
        ballX = centerX;
        ballY = centerY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(centerX, centerY, Math.min(width/4, height/4), targetPaint);

        canvas.drawCircle(ballX, ballY, 10, ballPaint);
    }

    public void updateDrawing(float x, float y, float z) {
        ballX += x;
        invalidate();
    }
}
