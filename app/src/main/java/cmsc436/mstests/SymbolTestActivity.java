package cmsc436.mstests;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SymbolTestActivity extends Activity {
    double start_time,end_time;
    Button start_button ;
    Button speech_recog;
    TextView prompt, result;
    ImageView symbol ;
    ImageView symbol_list;
    ImageView img1,img2,img3,img4,img5,img6,img7,img8,img9;
    EditText input;
    int n = 0;
    Random r;
    int count = 0;
    double[] trials = new double[9];
    String[] trials_check = new String[9];
    List<Integer> imgList = Arrays.asList(R.drawable.num1, R.drawable.num2, R.drawable.num3, R.drawable.num4,R.drawable.num5, R.drawable.num6, R.drawable.num7, R.drawable.num8, R.drawable.num9 );
    List<ImageView> symbolorder;
    ArrayList<Integer> numbers , shuffle;

    protected static final int RESULT_SPEECH = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symbol_test);

        //generate random number 1-10
        numbers = new ArrayList<Integer>(9);
        for(int i=1; i<10; i++){
            numbers.add(i);
        }

        shuffle = new ArrayList<Integer>(9);
        for(int i=1; i<10; i++){
            shuffle.add(i);
        }
        Collections.shuffle(shuffle);

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
//
//        for (int i: numbers) {numbers.add(i); }
//        Collections.shuffle(numbers);
//        shuffle(numbers);

        speech_recog = (Button) findViewById(R.id.speech);
        start_button = (Button) findViewById(R.id.symbol_test_start);
        prompt = (TextView) findViewById(R.id.symbol_prompt);
        symbol = (ImageView) findViewById(R.id.symbol);
        symbol_list = (ImageView) findViewById(R.id.symbol_list);
        symbol_list.setImageResource(R.drawable.symbol_list2);
        result = (TextView) findViewById(R.id.result);
        input = (EditText) findViewById(R.id.answer);
        prompt.setText("Please take a look at the chart above then enter the correct number that corresponds to the image appear below and press Done! after you select");

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prompt.setVisibility(View.INVISIBLE);

                init();
            }
        });

    }
    public void init() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,1);

        start_button.setVisibility(View.INVISIBLE);
        if(count < 9) {
            doTest();
        }
        else {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            input.setVisibility(View.INVISIBLE);
            speech_recog.setVisibility(View.INVISIBLE);
            resultView();
        }
    }
    public void resultView() {
        String str = "";
        symbol.setVisibility(View.INVISIBLE);
        for(int i=0; i<9; i++) {
            str = str + "Trial "+ (i+1) + " : " + trials[i] + " / " + trials_check[i] + " \n";
        }
        result.setText(str.replace("\\n",System.lineSeparator()));
    }
    public void doTest() {

        speech_recog.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try{
                    startActivityForResult(intent, RESULT_SPEECH);
                }catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(),
                            "SPEECH NOT SUPPORTED",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        symbol.setImageResource(imgList.get(shuffle.get(count)-1));
//        getSymbol(shuffle.get(count));
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

    public boolean checkAnswer() {

        double final_time = 0;
        if( input.getText().toString().equals("")) {
            trials_check[count] = "Wrong";
        }
        else {
            int n = Integer.parseInt(input.getText().toString());
            if (n == shuffle.get(count)) {
                trials_check[count] = "Correct";
            } else {

                trials_check[count] = "Wrong";
            }
        }
        end_time = System.currentTimeMillis();
        final_time = (end_time - start_time) / 1000;
        trials[count] = final_time;

        count++;
        input.setText("");
        init();

        return true;
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
}