package cmsc436.mstests;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class SymbolTestActivity extends Activity {
    double start_time,end_time;
    Button start_button, done_button ;
    TextView prompt, result;
    NumberPicker np;
    ImageView symbol ;
    ImageView symbol_list;
    EditText input;
    int n = 0;
    Random r;
    ArrayList<Integer> numbers;
    int count = 0;
    double[] trials = new double[9];
    String[] trials_check = new String[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symbol_test);

        //generate random number 1-10
        numbers = new ArrayList<Integer>();
        for (int i = 1; i < 10; ++i) {numbers.add(i); }
        Collections.shuffle(numbers);

        start_button = (Button) findViewById(R.id.symbol_test_start);
        done_button = (Button) findViewById(R.id.done_button);
        prompt = (TextView) findViewById(R.id.symbol_prompt);
        symbol = (ImageView) findViewById(R.id.symbol);
        symbol_list = (ImageView) findViewById(R.id.symbol_list);
        symbol_list.setImageResource(R.drawable.symbol_list);
        result = (TextView) findViewById(R.id.result);

        prompt.setText("Please take a look at the chart above then enter the correct number that corresponds to the image appear below and press Done! after you select");
        np = (NumberPicker) findViewById(R.id.numberPicker);
        np.setMaxValue(9);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);

//        String str = "";
//        if( numbers.isEmpty()) {
//            prompt.setText("EMPTY");
//        }else {
//
//            for (int i : numbers) {
//                str = str + (String.valueOf(i));
//            }
//
//            prompt.setText(str);
//        }

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prompt.setVisibility(View.INVISIBLE);
                init();
            }
        });

    }
    public void init() {
        start_button.setVisibility(View.INVISIBLE);
        if(count < 9) {
            doTest();
        }
        else {
            np.setVisibility(View.INVISIBLE);
            done_button.setVisibility(View.INVISIBLE);
            resultView();
        }
//        symbol.setImageResource(R.drawable.num1);
//        n = np.getValue();
//        prompt.setText(String.valueOf(n));
//        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
//                //Display the newly selected number from picker
//                prompt.setText("Selected Number : " + newVal);
//            }
//        });
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

        getSymbol(numbers.get(count));
        start_time = System.currentTimeMillis();
        done_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                double final_time = 0;
                if (np.getValue() == numbers.get(count)) {
                    end_time = System.currentTimeMillis();
                    final_time = ( end_time -start_time)/1000;
                    trials[count] = final_time;
                    trials_check[count] = "Correct";
                }
                else {
                    trials[count] = final_time;
                    trials_check[count] = "Wrong";
                }
                count++;
                init();

            }
        });

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

}
