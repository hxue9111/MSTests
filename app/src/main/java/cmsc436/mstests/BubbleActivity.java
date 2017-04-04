package cmsc436.mstests;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import static android.R.attr.data;

public class BubbleActivity extends Activity {

    final static String SCORE = "SCORE";
    final static String DATA = "DATA";

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

}
