package cmsc436.mstests;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        Button tap_test_button = (Button)findViewById(R.id.tap_test_start);
        Button spiral_test_button = (Button)findViewById(R.id.spiral_test_start);
        Button level_test_button = (Button)findViewById(R.id.level_test_start);
        Button bicep_leg_test_button = (Button)findViewById(R.id.bicep_leg_test_start);
        Button bubble_test_button = (Button)findViewById((R.id.start_bubble_button));

        final Intent start_tap_test = new Intent(this, TapTestActivity.class);
        final Intent start_spiral_test = new Intent(this, SpiralTestActivity.class);
        final Intent start_level_test = new Intent(this, LevelTestActivity.class);
        final Intent start_bicep_leg_test = new Intent(this, BicepLegTestActivity.class);
        final Intent start_bubble_test = new Intent(this, BubbleActivity.class);

        tap_test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(start_tap_test);
            }
        });

        spiral_test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(start_spiral_test);
            }
        });

        level_test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(start_level_test);
            }
        });

        bicep_leg_test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(start_bicep_leg_test);
            }
        });

        bubble_test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(start_bubble_test);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
