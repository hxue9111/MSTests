package cmsc436.mstests;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.WindowManager;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cmsc436.mstests.Sheets.Sheets;


public class SymbolTestActivity extends Activity implements Sheets.Host {
    private Sheets sheet;
    double start_time,end_time;
    Button start_button ;
    Button speech_recog;
    TextView prompt, result;
    ImageView symbol ;
    ImageView symbol_list;
    ImageView img1,img2,img3,img4,img5,img6,img7,img8,img9;
    EditText input;
    int currSymbol = 0, numSymbolCorrect = 0;
    ArrayList<Double> correctAnswerTimes = new ArrayList<>();
    ArrayList<Double> trials = new ArrayList<>();
    ArrayList<String> trials_check = new ArrayList<>();
    List<Integer> imgList = Arrays.asList(R.drawable.num1, R.drawable.num2, R.drawable.num3, R.drawable.num4,R.drawable.num5, R.drawable.num6, R.drawable.num7, R.drawable.num8, R.drawable.num9 );
    List<ImageView> symbolorder;
    ArrayList<Integer> numbers;
    HashMap<Integer,ArrayList<Double>> symbolTimes;
    TextView trialresults ;
    int learnabiltiy = 0;
    double totalAverage = 0;
    protected static final int RESULT_SPEECH = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symbol_test);

        //generate random number 1-10
        numbers = new ArrayList<Integer>();
        for(int i=1; i<10; i++){
            numbers.add(i);
        }

        img1 = (ImageView) findViewById(R.id.imageView1);
        img2 = (ImageView) findViewById(R.id.imageView2);
        img3 = (ImageView) findViewById(R.id.imageView3);
        img4 = (ImageView) findViewById(R.id.imageView4);
        img5 = (ImageView) findViewById(R.id.imageView5);
        img6 = (ImageView) findViewById(R.id.imageView6);
        img7 = (ImageView) findViewById(R.id.imageView7);
        img8 = (ImageView) findViewById(R.id.imageView8);
        img9 = (ImageView) findViewById(R.id.imageView9);

        symbolorder = Arrays.asList(img1,img2,img3,img4,img5,img6,img7,img8,img9);
        Collections.shuffle(numbers);

        for(int i=0; i<9; i++){
            imgList.set(i, numToImglist(numbers.get(i)));
        }
        for(int i=0; i<9; i++) {
            symbolorder.get(i).setImageResource(imgList.get(i));
        }

        symbolTimes = new HashMap<>();
        for(int i=1; i<10; i++) {
            symbolTimes.put(i, new ArrayList<Double>());
        }

        speech_recog = (Button) findViewById(R.id.speech);
        start_button = (Button) findViewById(R.id.symbol_test_start);
        prompt = (TextView) findViewById(R.id.symbol_prompt);
        symbol = (ImageView) findViewById(R.id.symbol);
        symbol_list = (ImageView) findViewById(R.id.symbol_list);
        symbol_list.setImageResource(R.drawable.symbol_list2);
        result = (TextView) findViewById(R.id.result);
        result.setVisibility(View.INVISIBLE);
        input = (EditText) findViewById(R.id.answer);
        prompt.setText("Please take a look at the chart above then enter the correct number that corresponds to the image appear below and press Done! after you select");
        trialresults = (TextView) findViewById(R.id.temp2);

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                prompt.setVisibility(View.INVISIBLE);
                new CountDownTimer(10000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        prompt.setText("Seconds remaining: " + millisUntilFinished / 1000);
                    }

                    @Override
                    public void onFinish() {
                        prompt.setVisibility(View.INVISIBLE);
                        resultView();
                    }
                }.start();
                init();
            }
        });

    }
    public void init() {

        start_button.setVisibility(View.INVISIBLE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,1);
        doTest();
    }
    public void resultView() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        input.setVisibility(View.INVISIBLE);
        speech_recog.setVisibility(View.INVISIBLE);
        result.setVisibility(View.VISIBLE);
        result.setMovementMethod(new ScrollingMovementMethod());
        calcAverage();
        String str = "";
        String str2 = calcAverage();
        trialresults.setText(str2);
        symbol.setVisibility(View.INVISIBLE);
//        for(int i=1; i<=symbolTimes.keySet().size(); i++) {
//            str += "symbol "+ i + " -> " + symbolTimes.get(i).toString() + "\n";
//        }
        for(int i=0; i<trials.size(); i++) {

            str = str + "Trial "+ (i+1) + " : " + trials.get(i) + " / " + trials_check.get(i) + " \n";
        }
        result.setText(str.replace("\\n",System.lineSeparator()));

        sendToSheets(Sheets.TestType.SYMBOL, totalAverage, numSymbolCorrect);
        sendToSheets_str(Sheets.TestType.SYMBOL_CORRECT);
    }

    private void sendToSheets(Sheets.TestType type, double result, int correctSymbols) {
        //sheet.writeData(sheetType, getString(R.string.patientID), (float)result);
        sheet = new Sheets(this, this, getString(R.string.app_name));

        float[] temp = new float[trials.size()];
        double[] temp2 = new double[trials.size()];
        for(int i=0; i<temp2.length; i++) {
            temp2[i] = trials.get(i);
        }
        for(int i=0; i<temp.length; i++) {
            temp[i] = (float) temp2[i];
        }
        float[] centralSheetData = {(float) result, (float) correctSymbols, (float) learnabiltiy};
        sheet.writeData(type,getString(R.string.patientID), centralSheetData) ;
        sheet.writeTrials(type, getString(R.string.patientID), temp);
//        sheet.writeTrials(type, getString(R.string.patientID), (float)correctSymbols);
    }
    private void sendToSheets_str(Sheets.TestType type) {
        sheet = new Sheets(this, this, getString(R.string.app_name));
        float[] test = new float[trials_check.size()];
        for(int i=0; i<trials_check.size(); i++){
            if(trials_check.get(i).equals("CORRECT")) {
                test[i] = 1;
            }
            else {
                test[i] = 0;
            }
        }
        sheet.writeTrials(type, getString(R.string.patientID), test);

    }

        private String calcAverage() {
        double firstHalfAvg = 0, secondHalfAvg =0;
        int fasterAnswer = 0;
        for(ArrayList<Double> list: symbolTimes.values()) {
            for(double d : list) {
                totalAverage += d ;
            }

            if(list.size() > 1 ) {
                for (int i = 0; i < list.size() / 2; i++) {
                    firstHalfAvg += list.get(i);
                }
                firstHalfAvg /= list.size() / 2;
                for (int j = list.size(); j < list.size(); j++) {
                    secondHalfAvg += list.get(j);
                }
                secondHalfAvg /= (list.size() - list.size() / 2);

                if (secondHalfAvg < firstHalfAvg) {
                    fasterAnswer++;
                }
            }
        }
        totalAverage /= numSymbolCorrect;
        totalAverage = (double)Math.round(totalAverage * 100000d) / 100000d;

        String resultprompt = "Number of correct answer : " + numSymbolCorrect +".\n"+ "Average Time: " + totalAverage + "s\n" + "Learn Ability (Scale 0-9): ";
        learnabiltiy = fasterAnswer;
        return resultprompt + fasterAnswer;
    }
    public void doTest() {

        speech_recog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(),
                            "SPEECH NOT SUPPORTED",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        changeSymbol();

        symbol.setImageResource(imgList.get(currSymbol));
        start_time = System.currentTimeMillis();
        input.setOnKeyListener(new TextView.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    return checkAnswer();
                } else {
                    return false;
                }
            }
        });
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    return checkAnswer();
                } else {
                    return false;
                }
            }
        });

    }

    private void changeSymbol() {
        Random r = new Random();
        int rand;
        do {    // Make sure we don't get the last one again
            rand = r.nextInt(9);
        }while(rand == currSymbol);
        currSymbol = rand;
    }
    public boolean checkAnswer() {

        double final_time = 0;
        end_time = System.currentTimeMillis();
        final_time = (end_time - start_time) / 1000;

        if( input.getText().toString().equals("")) {
            trials.add(final_time);
            trials_check.add("WRONG");
        }
        else {
            int n = Integer.parseInt(input.getText().toString());
            if (n == currSymbol+1) {
                correctAnswerTimes.add(final_time);
                trials.add(final_time);
                trials_check.add("CORRECT");
                numSymbolCorrect++;
                symbolTimes.get(n).add(final_time);
            }
            else {
                trials.add(final_time);
                trials_check.add("WRONG");

            }
        }
        input.setText("");
        init();

        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


                    //result.setText(text.get(0));
                    String txt = text.get(0);
                    Log.d("myTag", "Cool");
                    switch (txt) {
                        case "one" :
                            input.setText("1");
                            break;
                        case "two":
                            input.setText("2");
                            break;
                        case "three" :
                            input.setText("3");
                            break;
                        case "4":
                            input.setText("4");
                            break;
                        case "5" :
                            input.setText("5");
                            break;
                        case "six":
                            input.setText("6");
                            break;
                        case "7" :
                            input.setText("7");
                            break;
                        case "8":
                            input.setText("8");
                            break;
                        case "9" :
                            input.setText("9");
                            break;

                    }
                    input.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_NUMPAD_ENTER, 0));

                }
                break;
            }

        }
    }
    public void getSymbol(int n) {
        switch (n) {
            case 1:
                symbol.setImageResource(R.drawable.num1);break;
            case 2:
                symbol.setImageResource(R.drawable.num2);break;
            case 3:
                symbol.setImageResource(R.drawable.num3);break;
            case 4:
                symbol.setImageResource(R.drawable.num4);break;
            case 5:
                symbol.setImageResource(R.drawable.num5);break;
            case 6:
                symbol.setImageResource(R.drawable.num6);break;
            case 7:
                symbol.setImageResource(R.drawable.num7);break;
            case 8:
                symbol.setImageResource(R.drawable.num8);break;
            case 9:
                symbol.setImageResource(R.drawable.num9);break;
            default:
                return ;
        }
    }

    private Integer numToImglist(int n) {
        switch (n) {
            case 1:
                return R.drawable.num1;
            case 2:
                return R.drawable.num2;
            case 3:
                return R.drawable.num3;
            case 4:
                return R.drawable.num4;
            case 5:
                return R.drawable.num5;
            case 6:
                return R.drawable.num6;
            case 7:
                return R.drawable.num7;
            case 8:
                return R.drawable.num8;
            case 9:
                return R.drawable.num9;
            default:
                return 0;
        }

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

}