package cmsc436.mstests;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import edu.umd.cmsc436.sheets.Sheets;

public class BicepLegTestActivity extends Activity implements Sheets.Host{
    private Sheets sheet;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    TextView tv;
    Button start_button;
    Button done_button;
    String right_arm = "Start Right Arm Bicep Curls";
    String left_arm = "Start Left Arm Bicep Curls";
    String right_leg = "Start Right Leg Curls";
    String left_leg = "Start Left Leg Curls";

    ArrayList<Long> right_arm_list = new ArrayList<Long>();
    ArrayList<Long> left_arm_list = new ArrayList<Long>();
    ArrayList<Long> right_leg_list = new ArrayList<Long>();
    ArrayList<Long> left_leg_list = new ArrayList<Long>();


    double ra_avg;
    double la_avg;
    double rl_avg;
    double ll_avg;

    BicepCurlSensorEventListener evl;

    String currLimb = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bicep_leg_test);
        tv = (TextView) findViewById(R.id.text_view);
        start_button = (Button) findViewById(R.id.start_btn);
        done_button = (Button) findViewById(R.id.done_btn);
        done_button.setVisibility(View.GONE);
        //tv.setText("0 Curls");
        System.out.println("activity started");
        setRightArmView(right_arm);
        //init();
    }

    public void init(){
        evl = new BicepCurlSensorEventListener(new CurlCallback(tv));
        evl.init();
        start_button.setVisibility(View.GONE);
        done_button.setVisibility(View.VISIBLE);
        done_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("done clicked");
                System.out.println("currLimb" + currLimb);
                if(currLimb.equals("right_arm")){
                    ra_avg = average(evl.curlTimes)/1000;
                    setLeftArmView(left_arm);
                }else if(currLimb.equals("left_arm")){
                    la_avg = average(evl.curlTimes)/1000;
                    setRightLegView(right_leg);
                }else if(currLimb.equals("right_leg")){
                    rl_avg = average(evl.curlTimes)/1000;
                    setLeftLegView(left_leg);
                }else if(currLimb.equals("left_leg")){
                    ll_avg = average(evl.curlTimes)/1000;
                    setResultsView();
                }
                evl.curlTimes.clear();
            }
        });
        tv.setText("0 Curls");
        //evl.curlTimes.size(); // number reps
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        senSensorManager.registerListener(evl, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public class CurlCallback{
        TextView tv;
        public CurlCallback(TextView tv){
            this.tv = tv;
        }

        public void onCurl(ArrayList<Long> curlTimes){
            int rep = curlTimes.size();
            tv.setText(rep + " Curls");

//            if(currLimb.equals("right_arm")){
//                right_arm_list = curlTimes;
//            }else if(currLimb.equals("left_arm")){
//                left_arm_list = curlTimes;
//            }else if(currLimb.equals("right_leg")){
//                right_leg_list = curlTimes;
//            }else if(currLimb.equals("left_leg")){
//                left_leg_list = curlTimes;
//            }
            //once rep = 10 can move to the next task, get array and store it, compute the averages and such
            if(rep == 10){
                if(currLimb.equals("right_arm")){
                    ra_avg = average(evl.curlTimes)/1000.0;
                    setLeftArmView(left_arm);
                }else if(currLimb.equals("left_arm")){
                    la_avg = average(evl.curlTimes)/1000.0;
                    setRightLegView(right_leg);
                }else if(currLimb.equals("right_leg")){
                    rl_avg = average(evl.curlTimes)/1000.0;
                    setLeftLegView(left_leg);
                }else if(currLimb.equals("left_leg")){
                    ll_avg = average(evl.curlTimes)/1000.0;
                    setResultsView();
                }
                curlTimes.clear();
            }
        }
    }

    // Make the text take up the whole screen
    private void setRightArmView(String right_arm) {
        tv.setText(right_arm);
        start_button.setVisibility(View.VISIBLE);
        done_button.setVisibility(View.GONE);
        currLimb = "right_arm";
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });
    }

    private void setLeftArmView(String left_arm) {
        tv.setText(left_arm);
        start_button.setVisibility(View.VISIBLE);
        done_button.setVisibility(View.GONE);
        currLimb = "left_arm";
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });
    }

    private void setRightLegView(String right_leg) {
        tv.setText(right_leg);
        start_button.setVisibility(View.VISIBLE);
        done_button.setVisibility(View.GONE);
        currLimb = "right_leg";
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });
    }

    private void setLeftLegView(String left_leg) {
        tv.setText(left_leg);
        start_button.setVisibility(View.VISIBLE);
        done_button.setVisibility(View.GONE);
        currLimb = "left_leg";
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });
    }
    private void setResultsView(){
        start_button.setVisibility(View.GONE);
        done_button.setVisibility(View.GONE);
        sendToSheets(la_avg, Sheets.TestType.LH_CURL);
        sendToSheets(ra_avg, Sheets.TestType.RH_CURL);
        sendToSheets(ll_avg, Sheets.TestType.LF_CURL);
        sendToSheets(rl_avg, Sheets.TestType.RF_CURL);
        tv.setText("Average Time Per Curl Results\n\n" +
                "Right Arm: " + ra_avg + " seconds" +
                "\nLeft Arm: " + la_avg + " seconds" +
                "\nRight Leg: " + rl_avg + " seconds" +
                "\nLeft Leg: " + ll_avg + " seconds");
    }

    private void sendToSheets(double scores, Sheets.TestType type) {
        // Send data to sheets
        sheet = new Sheets(this, this, getString(R.string.app_name), getString(R.string.class_sheet), getString(R.string.private_sheet));


        float[] result = {(float) scores};


//        sheet.writeData(type, getString(R.string.patientID), result);
        sheet.writeTrials(type, getString(R.string.patientID), result) ;


    }
    private double average(ArrayList<Long> arr){
        double l = 0;
        if(arr.size() == 0){
            return l;
        }else{
            double sum = 0;
            for(int i = 0; i < arr.size(); i++){
                sum += arr.get(i);
            }
            l = sum/arr.size();
            return l;
        }

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
