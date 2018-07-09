package com.example.josh.waypointcompass;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import static android.content.ContentValues.TAG;

public class Display extends AppCompatActivity implements SensorEventListener
{
    private TextView text;
    private ImageView arrow;
    private ImageView compass;

    private LocationManager locationManager;
    private LocationListener listener;
    private SensorManager sensorManager;

    private float a_current = 0;
    private float c_current = 0;
    private double bearing=0;

    private double userElevation = 0;
    private double destinationElevation = 0;
    private boolean gotUserElevation = false;
    private boolean everGotUserElevation = false;
    private boolean gotDestinationElevation = false;
    private boolean gettingDestination = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        final double [] coord = new double [2];

        text = findViewById(R.id.textView);
        text.setText("Searching for GPS signal");
        compass = findViewById(R.id.compass);
        arrow = findViewById(R.id.arrow);

        Intent intent = getIntent();
        String longlat = intent.getStringExtra("coords");
        longlat = longlat.replaceAll(" ","");
        String [] longilati = longlat.split(",");
        coord[0] = Double.parseDouble(longilati[0]);
        coord[1] = Double.parseDouble(longilati[1]);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);



        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location)
            {

                RelativeLayout relativeLayout = findViewById(R.id.rl);
                double distance = findDistanceTo(location.getLatitude(), location.getLongitude(), coord[0], coord[1]);

                if (distance <= 0.01) relativeLayout.setBackgroundColor(Color.GREEN);
                else if (distance < 0.1) relativeLayout.setBackgroundColor(Color.YELLOW);
                else relativeLayout.setBackgroundColor(Color.WHITE);

                bearing = findBearingTo(location.getLatitude(), location.getLongitude(), coord[0], coord[1]);

                text.setText("Distance: " + distance + "km\n Bearing: " +(int) bearing+"°");

                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (!(networkInfo!=null&&networkInfo.isConnected())) Toast.makeText(Display.this, "No connection available", Toast.LENGTH_LONG).show();
                else {
                    if (!gotDestinationElevation) {
                        gettingDestination = true;
                        new altitudeFinder().execute(coord[0], coord[1]);
                    }

                    if (gotDestinationElevation && !gotUserElevation) {
                        gettingDestination = false;
                        new altitudeFinder().execute(location.getLatitude(),location.getLongitude());
                    }

                }

                if (gotDestinationElevation && everGotUserElevation) {
                    text.append("\n Altitude gain: " + (int) round(destinationElevation - userElevation, 0) + "m");
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 10);
            }
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, listener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 10);
                    }
                    return;
                }
                locationManager.requestLocationUpdates("gps", 5000, 0, listener);
                break;
            default:
                break;
        }
    }

///////////Compass///////////
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        float degree = Math.round(sensorEvent.values[0]);

        RotateAnimation rc = new RotateAnimation(
                c_current,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rc.setDuration(210);
        rc.setFillAfter(true);
        compass.startAnimation(rc);
        c_current = -degree;

        float rotation = (-degree)+(float)bearing;
        RotateAnimation ra = new RotateAnimation(
                a_current,
                rotation,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, (2.5f/4.3f));
        ra.setDuration(210);
        ra.setFillAfter(true);
        arrow.startAnimation(ra);
        a_current = rotation;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
///////////Compass end///////////

    //inputs: GPS lat, GPS long, WayPoint lat and WayPoint long
    public int findBearingTo(double y1, double x1, double y2, double x2)
    {
        y1 = Math.toRadians(y1);
        x1 = Math.toRadians(x1);
        y2 = Math.toRadians(y2);
        x2 = Math.toRadians(x2);
        double dx = x2 - x1;
        double y = Math.sin(dx)*Math.cos(y2);
        double x = Math.cos(y1)*Math.sin(y2)-Math.sin(y1)*Math.cos(y2)*Math.cos(dx);
        double brng;
        if (Math.toDegrees(y2) == 90) brng = 0;
        else if (Math.toDegrees(y2) == -90) brng = 180;
        else if (y2>y1)
        {
            if (x2>x1) brng = Math.toDegrees(Math.atan(y/x));
            else brng = 360 + Math.toDegrees(Math.atan(y/x));
        }
        else if (y2<y1) brng = 180 + Math.toDegrees(Math.atan(y/x));
        else//y2==y1
        {
            if (x2==x1) brng = 0;
            else brng = 360+Math.toDegrees(Math.atan(y/x));
        }
        return (int) Math.round(brng)%360;
    }

    //Inputs: GPS long, GPS lat, WP long and WP lat
    public double findDistanceTo(double a1, double b1, double a2, double b2) {
        double r = 6371.0;//earth's radius

        a1 = Math.toRadians(a1);//all geometrical calculations require the angles in radians
        a2 = Math.toRadians(a2);
        b1 = Math.toRadians(b1);
        b2 = Math.toRadians(b2);

        double sina1 = Math.sin(a1);
        double sina2 = Math.sin(a2);
        double cosa1 = Math.cos(a1);
        double cosa2 = Math.cos(a2);

        double db = Math.abs(b1 - b2);
        double cosdb = Math.cos(db);

        double angle = Math.acos(sina1 * sina2 + cosa1 * cosa2 * cosdb);
        double distance = angle * r;
        if (distance<=0.001) distance = 0;
        else if (distance<=1) distance = round(distance,3);
        else if (distance<=10) distance = round(distance,2);
        else if (distance<=100) distance = round(distance,1);
        else distance = round(distance,0);
        return distance;
    }
    public double round (double num, int dp)
    {
        num = num*Math.pow(10, dp);
        num = Math.round(num);
        return num/Math.pow(10, dp);
    }

    private class altitudeFinder extends AsyncTask<Double, Void, Double>
    {
        @Override
        protected Double doInBackground(Double... doubles) {
            String elevation="";

            try
            {
                URL alt = new URL("https://maps.googleapis.com/maps/api/elevation/json?locations="+ doubles[0]+","+doubles[1]);
                alt.openConnection();
                InputStream str = alt.openStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(str));
                String inputLine;
                OUT:
                while ((inputLine = in.readLine()) != null)
                {
                    if (inputLine.contains("elevation"))
                    {
                        for (int i = 23;;i++) {
                            if (inputLine.charAt(i) != ',')
                                elevation = elevation + "" + inputLine.charAt(i);
                            else break OUT;
                        }
                    }
                }

                double e = round(Double.parseDouble(elevation),1);

                if (gettingDestination){
                    destinationElevation = e;
                    gotDestinationElevation = true;
                }
                else {
                    userElevation = e;
                    everGotUserElevation = true;
                    gotUserElevation = true;
                }
                Log.d(TAG, Double.toString(e));
                return e;
            }
            catch (MalformedURLException e) {}
            catch (IOException e) {}
            catch (NumberFormatException e) {
                Log.e(TAG, "NOT FOUND ELEVATION");
            }
            return 0.0;
        }
    }
}