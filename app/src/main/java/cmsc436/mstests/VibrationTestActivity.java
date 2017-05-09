package cmsc436.mstests;

import android.app.Activity;
import android.content.Context;
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

public class VibrationTestActivity extends Activity {
    String start_txt= "Prest start to begin the vibration test";
    TextView textView;
    Button yes_btn, no_btn;
    Vibrator v;
    Hashtable<long[],String> check = new Hashtable<>();
    int rand, rand2;
    long[] pattern;
    int yes =0, no =0;
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
        textView.setText("Tab start to begin vibration test. If you can feel the vibration, " +
                "please tap yes / blue color button");
        yes_btn.setVisibility(View.VISIBLE);
        no_btn.setVisibility(View.INVISIBLE);
        yes_btn.setText("CLICK TO START TEST");
        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            new CountDownTimer(90000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    textView.setText("Seconds remaining: " + millisUntilFinished / 1000);
                    setClickView();
                }

                @Override
                public void onFinish() {
                    //textView.setVisibility(View.INVISIBLE);
                    textView.setText("Vibration: " + yes + "\n" + "No vibration: " + no);
                }
            }.start();

        }
    });

    }

    private void setClickView() {
        no_btn.setVisibility(View.VISIBLE);
        yes_btn.setText("YES");
        no_btn.setText("NO");

        for(int i =0; i <=10; i++) {
            changeVibrator();
            yes_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    check.put(pattern,"yes");
                    yes++;
                }
            });
            no_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    check.put(pattern,"no");
                    no++;
                }
            });
        }
        setResultView();

    }

    private void setResultView() {
        String str ="";
        str = "Vibration: " + yes + "\n" + "No vibration: " + no;
        textView.setText(str);
    }

    private void changeVibrator() {
        Random r = new Random();
        rand = r.nextInt(1000);
        rand2 = r.nextInt(1000);
        pattern = new long[]{0,rand,rand2,rand2,rand};
        v.vibrate(pattern, -1);
    }

}
