package cmsc436.mstests;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.util.Log;
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
import cmsc436.mstests.Sheets.Sheets;


public class VibrationTestActivity extends Activity implements Sheets.Host{
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
    private Sheets sheet;

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
        sendToSheets(yes, Sheets.TestType.VIBRATION);
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

    private void sendToSheets(long scores, Sheets.TestType type) {
        // Send data to sheets
        sheet = new Sheets(this, this, getString(R.string.app_name));


        float[] result = {(float) scores};


//        sheet.writeData(type, getString(R.string.patientID), result);
        sheet.writeTrials(type, getString(R.string.patientID), result) ;


    }

    @Override
    public int getRequestCode(Sheets.Action action) {
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
