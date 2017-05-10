package cmsc436.mstests;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Vibrator;
import org.w3c.dom.Text;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import java.util.ArrayList;

import static android.os.SystemClock.uptimeMillis;

public class VibrationTestActivity extends Activity {
    private static final int NUM_OF_TRIAL = 10;
    String start_txt= "Prest start to begin the vibration test";
    TextView textView;
    Button yes_btn, no_btn;
    Vibrator v;
    Hashtable<long[],String> check = new Hashtable<>();
    int rand, rand2;
    long[] pattern;
    int yes =0, no =0, counter = 0;
    long startTime, totalTime, prevTime ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibration_test);

        textView = (TextView) findViewById(R.id.promp);
        yes_btn = (Button) findViewById(R.id.yes_btn);
        no_btn = (Button) findViewById(R.id.no_btn);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setStartView();
    }

    // Make the text take up the whole screen
    private void setStartView() {
        textView.setText("Tab start to begin vibration test.");
        yes_btn.setVisibility(View.VISIBLE);
        no_btn.setVisibility(View.INVISIBLE);
        yes_btn.setText("CLICK TO START TEST");
        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            new CountDownTimer(4000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    textView.setText("Test starting in: " + millisUntilFinished / 1000);
                    yes_btn.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFinish() {
                    startGame();
                }
            }.start();

            }
    });
    }

    private void startGame() {
        counter =0;
        startTime = uptimeMillis();
        prevTime = startTime;
        textView.setText("If you can feel the vibration, please tap yes / blue color button after each trial");
        no_btn.setVisibility(View.VISIBLE);
        yes_btn.setVisibility(View.VISIBLE);
        yes_btn.setBackgroundColor(Color.BLUE);
        no_btn.setBackgroundColor(Color.RED);
        yes_btn.setText("YES");
        no_btn.setText("NO");
        capture();

    }

    private void capture() {
        if(counter < NUM_OF_TRIAL) {
            changeVibrator();
            yes_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    yes++;
                    counter++;
                    capture();
                }
            });
            no_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    no++;
                    counter++;
                    capture();
                }
            });

        }
        else
            setResultView();
    }

    private void setResultView() {
        String str ="";
        yes_btn.setVisibility(View.INVISIBLE);
        no_btn.setVisibility(View.INVISIBLE);
        str = "Result: " + yes +"/10";
        sendToSheets(yes, Sheets.UpdateType.VIBRATION.ordinal());
        textView.setText(str);
    }

    private void changeVibrator() {
        textView.setText("Trial: " +counter);
        Random r = new Random();
        rand = r.nextInt(1000);
        rand2 = r.nextInt(1000);
        pattern = new long[]{0,rand,rand2,rand2,rand};
        v.vibrate(pattern, -1);
    }

    private void sendToSheets(int scores, int sheet) {
        // Send data to sheets
        Intent sheets = new Intent(this, Sheets.class);
//
        float temp = 1011;

        sheets.putExtra(Sheets.EXTRA_VALUE, temp);
        sheets.putExtra(Sheets.EXTRA_USER, getString(R.string.patientID));
        sheets.putExtra(Sheets.EXTRA_TYPE, sheet);

        startActivity(sheets);
    }
}
