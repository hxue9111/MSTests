package cmsc436.mstests;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import edu.umd.cmsc436.sheets.Sheets;

public class PathTimeTestActivity extends Activity
        implements OnMapReadyCallback, Sheets.Host {
    Button start_stop;
    TextView prompt;
    GoogleMap map;
    MapFragment mapFragment;
    boolean mIsBound;
    private LocationService mBoundService;
    Polyline mapLine;
    private Sheets sheet;

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
        final Timer T=new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mIsBound) {
                            long score = getTimeElapsed();
                            prompt.setText("Current travel time: " + score + " seconds");
                            updateLine();
                        }
                    }
                });
            }
        }, 1000, 1000);
        start_stop.setText("End test");
        start_stop.setEnabled(true);
        start_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_stop.setVisibility(View.GONE);
                T.cancel();
                if (mIsBound) {
                    long score = getTimeElapsed();
                    double dist = Math.round(getDistanceTraveled()*100)/100;
                    prompt.setText(dist +" meters traveled in: " + score + " seconds");
                    map.snapshot(new GoogleMap.SnapshotReadyCallback() {
                        @Override
                        public void onSnapshotReady(Bitmap bitmap) {
                            saveMap(bitmap);
                        }
                    });
                    doUnbindService();
                }
            }
        });
        drawPathOnMap();
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
        mapFragment.getView().setVisibility(View.VISIBLE);

        try {
            map.setMyLocationEnabled(true);

            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);

            Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if(location != null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(),location.getLongitude()), 20));
            }
        } catch(SecurityException e) {

        }

        ArrayList<LatLng> pts = new ArrayList<>();
        PolylineOptions lineOpts = new PolylineOptions();
        lineOpts.addAll(pts);
        lineOpts.width(10);
        lineOpts.color(Color.RED);
        mapLine = map.addPolyline(lineOpts);
        if (!pts.isEmpty()) {
            map.moveCamera(CameraUpdateFactory.newLatLng(pts.get(0)));
        }
    }
    public void updateLine(){
        if(mapLine != null) {
            if (mapLine.getPoints().size() != mBoundService.getPoints().size()) {
                mapLine.setPoints(mBoundService.getPoints());


                try{
                    LocationManager locationManager = (LocationManager)
                            getSystemService(Context.LOCATION_SERVICE);

                    Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                    if(location != null) {
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(location.getLatitude(),location.getLongitude()), 20));
                    }

                }catch(SecurityException e){

                }
            }

        }
    }
    public long getTimeElapsed(){
        if (mIsBound){
            return (System.currentTimeMillis()-mBoundService.getStartTime())/1000;
        }
        else return 0;
    }
    public double getDistanceTraveled(){
        if (mIsBound){
            return mBoundService.getTravelDistance();
        }
        else return 0;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
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
    private void saveMap(Bitmap map) {
        if (ContextCompat.checkSelfPermission(PathTimeTestActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(PathTimeTestActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(PathTimeTestActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        final String FILENAME = "outdoorwalkingpath";
        final String DATE_FORMAT_NOW = "yyyy-MM-dd_HH:mm:ss";

        String date = new SimpleDateFormat(DATE_FORMAT_NOW).format(new Date());

        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS),FILENAME + date +".png");
        FileOutputStream ostream;
        try {
            System.out.println(file.getAbsoluteFile());
            file.createNewFile();
            ostream = new FileOutputStream(file);
            map.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            ostream.flush();
            ostream.close();
            MediaScannerConnection.scanFile(this, new String[] {file.getAbsolutePath().toString()}, null, null);
            Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.sheet.onActivityResult(requestCode, resultCode, data);
    }
}
