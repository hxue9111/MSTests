package cmsc436.mstests;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Huang on 4/24/2017.
 */

public class LocationService extends Service {
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 1;
    private ArrayList<LatLng> points = new ArrayList<>();
    private long start_time;
    private class ServiceLocationListener implements LocationListener {
        Location mLastLocation;
        public ServiceLocationListener(String provider)
        {
            mLastLocation = new Location(provider);
        }
        @Override
        public void onLocationChanged(Location location)
        {
            System.out.println("Lat : " + location.getLatitude());
            System.out.println("Long : " + location.getLongitude());
            if(mLastLocation.getLongitude() != location.getLongitude() || mLastLocation.getLatitude() != location.getLatitude()) {
                points.add(new LatLng(location.getLatitude(), location.getLongitude()));
                mLastLocation.set(location);
            }
        }
        @Override
        public void onProviderDisabled(String provider)
        {
        }
        @Override
        public void onProviderEnabled(String provider)
        {
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
        }
    }
    LocationListener mLocationListener = new ServiceLocationListener(LocationManager.GPS_PROVIDER);
    LocationListener nwLocationListener = new ServiceLocationListener(LocationManager.NETWORK_PROVIDER);
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();

    public long getStartTime(){
        return start_time;
    }
    public ArrayList<LatLng> getPoints() {
        return new ArrayList<>(points);
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        start_time = System.currentTimeMillis();
        return mBinder;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
    @Override
    public void onCreate()
    {
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListener);
        } catch (java.lang.SecurityException ex) {
        } catch (IllegalArgumentException ex) {
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListener);
        } catch (java.lang.SecurityException ex) {
        } catch (IllegalArgumentException ex) {
        }
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        try{
            mLocationManager.removeUpdates(mLocationListener);
            mLocationManager.removeUpdates(nwLocationListener);
        }catch(SecurityException e) {

        }
    }
    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
