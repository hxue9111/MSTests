package cmsc436.mstests;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Path;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PathTimeTestActivity extends Activity
        implements OnMapReadyCallback {
    Button start_stop;
    TextView prompt;
    GoogleMap map;
    MapFragment mapFragment;
    boolean mIsBound;
    private LocationService mBoundService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((LocationService.LocalBinder) service).getService();

        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_time_test);
        start_stop = (Button) findViewById(R.id.start_stop);
        prompt = (TextView) findViewById(R.id.prompt);
        requestPermissions();
        startScreen();
        initMap();
    }

    private void initMap(){
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getView().setVisibility(View.INVISIBLE);
    }
    private void startScreen(){
        if (ContextCompat.checkSelfPermission(PathTimeTestActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            prompt.setVisibility(View.VISIBLE);
            prompt.setText("Directions: Start tracking by pressing the start button. When you have finished walking, press the button again to finish test.");
            start_stop.setText("Start test");
            start_stop.setVisibility(View.VISIBLE);
            start_stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    start_stop.setEnabled(false);
                    new CountDownTimer(4000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            start_stop.setText("Test starting in: " + millisUntilFinished / 1000);
                        }

                        public void onFinish() {
                            initializeTest();
                        }
                    }.start();
                }
            });
        } else {
            prompt.setText("Need location permissions to proceed.");
        }

    }

    private void initializeTest() {
        doBindService();
        prompt.setVisibility(View.VISIBLE);
        prompt.setText("Currently tracking path.");
        start_stop.setText("End test");
        start_stop.setEnabled(true);
        start_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_stop.setVisibility(View.GONE);
                if (mIsBound) {
                    long score = score();
                    prompt.setText("Time taken to travel path: " + score + " seconds");
                    drawPathOnMap();
                    mapFragment.getView().setVisibility(View.VISIBLE);
                    doUnbindService();
                }
            }
        });
    }
    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(PathTimeTestActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(PathTimeTestActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
    }

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(this,
                LocationService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {

            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    public void drawPathOnMap() {
        try {
            map.setMyLocationEnabled(true);


            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);

            Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(),location.getLongitude()), 20));
        } catch(SecurityException e) {

        }

        ArrayList<LatLng> pts = mBoundService.getPoints();
        PolylineOptions lineOpts = new PolylineOptions();
        lineOpts.addAll(pts);
        lineOpts.width(10);
        lineOpts.color(Color.RED);
        map.addPolyline(lineOpts);
        if (!pts.isEmpty()) {
            map.moveCamera(CameraUpdateFactory.newLatLng(pts.get(0)));
        }

    }
    public long score(){
        return (System.currentTimeMillis()-mBoundService.getStartTime())/1000;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScreen();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
