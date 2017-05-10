package cmsc436.mstests;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;

import edu.umd.cmsc436.sheets.Sheets;

public class TapTestActivity extends Activity implements Sheets.Host {
    int leftHandTaps = -1, rightHandTaps = -1;
    int taps;
    int count = 0;
    int[] lefthand = new int[5];
    int[] righthand = new int[5];

    String left_hand_test_label = "Press the button to start left hand test";
    String right_hand_test_label = "Press the button to start right hand test";
    String start_label = "Push to start test";

    Button tap_test_button;
    TextView text_prompt;

    private Sheets sheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_test);

        tap_test_button = (Button) findViewById(R.id.tap_location);
        text_prompt = (TextView) findViewById(R.id.prompt);

        setStartView(left_hand_test_label);
    }

    // Make the text take up the whole screen
    private void setStartView(String hand_test_name) {
        if (count < 5) {

            text_prompt.setText(hand_test_name);
            tap_test_button.setVisibility(View.VISIBLE);
            tap_test_button.setText(start_label);
            tap_test_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setCountdownView();
                }
            });
        } else {
            setResultView();
        }
    }

    //Start countdown
    private void setCountdownView() {
        tap_test_button.setVisibility(View.GONE);
        new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                text_prompt.setText("Test starting in: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                setTapView();
            }
        }.start();
    }

    // Make the timer and button sized appropriately to capture taps
    private void setTapView() {
        tap_test_button.setVisibility(View.VISIBLE);
        captureTaps();
    }

    //Show results
    private void setResultView() {
        tap_test_button.setVisibility(View.INVISIBLE);
        text_prompt.setText("Results:\nTaps with left hand: \n" + Arrays.toString(lefthand) + "\nTaps with right hand: \n" + Arrays.toString(righthand));
        sendToSheets(lefthand, Sheets.TestType.LH_TAP);
        sendToSheets(righthand, Sheets.TestType.RH_TAP);
    }

    // Enable button to count taps
    private int captureTaps() {
        taps = 0;
        text_prompt.setText("Tap here");
        tap_test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taps++;
            }
        });
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                text_prompt.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                text_prompt.setText("Done! taps: " + leftHandTaps);
                tap_test_button.setVisibility(View.GONE);
                if (leftHandTaps == -1) {
                    leftHandTaps = taps;
                    lefthand[count] = taps;

                    setStartView(right_hand_test_label);
                } else if (rightHandTaps == -1) {
                    rightHandTaps = taps;
                    righthand[count] = taps;
                    count++;
                    leftHandTaps = -1;
                    rightHandTaps = -1;
                    setStartView(left_hand_test_label);
                }
            }
        }.start();
        return taps;
    }

    private void sendToSheets(int[] scores, Sheets.TestType type) {
//        // Send data to sheets
//        Intent sheets = new Intent(this, Sheets.class);
//
        sheet = new Sheets(this, this, getString(R.string.app_name), getString(R.string.class_sheet), getString(R.string.private_sheet));

        float avg = 0;
        float fScores[] = new float[scores.length];
        for (int i = 0; i < 5; i++) {
            avg += scores[i];
            fScores[i] = scores[i];
        }
        avg = avg / 5;

        sheet.writeData(type, getString(R.string.patientID), avg);
        sheet.writeTrials(type, getString(R.string.patientID), fScores);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tap_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
