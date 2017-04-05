package cmsc436.mstests;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class TapTestActivity extends Activity {
    int leftHandTaps = -1, rightHandTaps = -1, taps;

    String left_hand_test_label = "Press the button to start left hand test";
    String right_hand_test_label = "Press the button to start right hand test";
    String start_label = "Push to start test";

    Button tap_test_button;
    TextView text_prompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_test);

        tap_test_button = (Button)findViewById(R.id.tap_location);
        text_prompt = (TextView)findViewById(R.id.prompt);

        setStartView(left_hand_test_label);
    }

    // Make the text take up the whole screen
    private void setStartView(String hand_test_name) {
        text_prompt.setText(hand_test_name);
        tap_test_button.setVisibility(View.VISIBLE);
        tap_test_button.setText(start_label);
        tap_test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCountdownView();
            }
        });
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
        text_prompt.setText("Results:\nTaps with left hand: " + leftHandTaps + "\nTaps with right hand: " + rightHandTaps);
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
                    sendToSheets(leftHandTaps, Sheets.UpdateType.LH_TAP.ordinal());

                    setStartView(right_hand_test_label);
                } else if (rightHandTaps == -1){
                    rightHandTaps = taps;
                    sendToSheets(rightHandTaps, Sheets.UpdateType.RH_TAP.ordinal());

                    setResultView();
                }
            }
        }.start();
        return taps;
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
}
