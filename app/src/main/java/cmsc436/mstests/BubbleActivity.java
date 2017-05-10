package cmsc436.mstests;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import static android.R.attr.data;
import edu.umd.cmsc436.sheets.Sheets;

public class BubbleActivity extends Activity implements Sheets.Host{

    final static String SCORE = "SCORE";
    final static String DATA = "DATA";
    private Sheets sheet;
    BubbleView.OnBubbleUpdateListener bubbleUpdateListener;
    private static long num_sec = 0;
    int counter = 0;
    TextView timeView;
    Button startButton;
    BubbleView bubble1;

    long left_click = -1, right_click = -1;
    String left_hand_test_label = "Press the button to use your hand and pop the bubble";
    String right_hand_test_label = "Press the button to use your right hand to pop the bubble";
    String start_label = "Push to start test";
    boolean lefthand = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);

        timeView = (TextView) findViewById(R.id.timeView);
        startButton = (Button) findViewById(R.id.start_bubble_button);
        bubble1 = (BubbleView) findViewById(R.id.bubble1);
        bubble1.setOnBubbleUpdateListener(new BubbleView.OnBubbleUpdateListener() {
            @Override
            public void onBubbleUpdate() {
                timeView.setText("Counter: "+ bubble1.getCurrentCounter());

            }

            @Override
            public void onDone() {
                startButton.setVisibility(View.VISIBLE);
                startButton.setText("Done");
                startButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       /* if (left_click == -1) {
                            left_click = bubble1.getAverageTime();
                            setStartView(right_hand_test_label);
                        } else if (right_click == -1) {
                            right_click = bubble1.getAverageTime();
                            long score = (left_click + right_click)/2;
                           //Intent intent = new Intent();

                           // intent.putExtra(SCORE, score);
                           // intent.putExtra(DATA, data);
                           // setResult(RESULT_OK, intent);
                           */
                        Intent intent = new Intent();
                        intent.putExtra(SCORE,bubble1.getAverageTime());
                        startButton.setVisibility(View.GONE);
                        timeView.setText("AverageTime: " + bubble1.getAverageTime());
                        sendToSheets(bubble1.getAverageTime(), Sheets.TestType.LH_POP);
                        setResult(RESULT_OK,intent);
                            finish();
                        //}
                    }
        });

    }
    });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               setStartView(left_hand_test_label);

            }
        });

    }

    private void setStartView(String hand_test_name) {
        timeView.setText(hand_test_name);
        startButton.setVisibility(View.VISIBLE);
        startButton.setText(start_label);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCountdownView();
            }
        });
    }

    private void sendToSheets(long scores, Sheets.TestType type) {
        // Send data to sheets
        sheet = new Sheets(this, this, getString(R.string.app_name), getString(R.string.class_sheet), getString(R.string.private_sheet));


        float[] result = {(float) scores};


//        sheet.writeData(type, getString(R.string.patientID), result);
        sheet.writeTrials(type, getString(R.string.patientID), result) ;


    }

    //Start countdown
    private void setCountdownView() {
        startButton.setVisibility(View.GONE);
        new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                timeView.setText("Test starting in: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                bubble1.startGame();
            }
        }.start();
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
