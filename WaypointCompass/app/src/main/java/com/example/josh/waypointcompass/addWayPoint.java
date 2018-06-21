package com.example.josh.waypointcompass;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.location.Location;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;

public class addWayPoint extends AppCompatActivity {
    private boolean GridRef;
    private boolean update = false;

    private db database = new db(this);
    private wayPoint oldWayPoint;

    private EditText nameEditor;
    private EditText coordEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_way_point);

        nameEditor = (EditText) findViewById(R.id.nameEditor);
        coordEditor = (EditText) findViewById(R.id.coordEditor);

        Intent i = getIntent();
        oldWayPoint =(wayPoint) i.getSerializableExtra("wp");

        if (oldWayPoint != null) {
            update = true;
            nameEditor.setText(oldWayPoint.name);
            coordEditor.setText(oldWayPoint.latitude+", "+oldWayPoint.longitude);
        }

        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GridRef = isChecked;
            }
        });

    }

    public void done (View view) {
        coordinateCheck c = new coordinateCheck();
        String name = nameEditor.getText().toString();
        String coord = coordEditor.getText().toString().toUpperCase();
        TextView errorMessage = (TextView) findViewById(R.id.errorMessage);
        boolean valid = false;

        wayPoint newWayPoint = new wayPoint();

        if (!name.equals(""))  newWayPoint.name = name;

        if (GridRef)
        {
            if (c.OSCheck(coord))
            {
                OSRef r = new OSRef(coord);
                LatLng l = r.toLatLng();

                newWayPoint.latitude = l.getLat();
                newWayPoint.longitude= l.getLng();
                valid = true;
            }
            else errorMessage.setText("\nPlease enter a valid OS Grid Reference"
                    + "\nIn the form: RRXXXYYY"
                    + "\nWhere RR is the map reference(2 letters in top right of map)"
                    + "\nXXX is the eastings"
                    + "\nYYY is the northings");

        }
        else
        {
            coord = c.coordFormat(coord);
            if (c.coordCheck(coord))
            {
                String [] str= coord.split(",");

                newWayPoint.latitude =Double.parseDouble(str[0]);
                newWayPoint.longitude=Double.parseDouble(str[1]);

                valid = true;
            }
            else errorMessage.setText("\nPlease enter a valid coordinate"
                    + "\nIn the form latitude, longitude");
        }

        if (valid)
        {
            if (update)
            {
                if (!database.updateWayPoint(newWayPoint, oldWayPoint.name)) errorMessage.setText("Invalid name.");
                else finish();

            }
            else
            {
                if (database.addWayPoint(newWayPoint))finish();
                else errorMessage.setText("Invalid name");
            }
        }
    }

    public void useCurrentLocation(View view){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 10);
            }
            return;
        }
        Location location = locationManager.getLastKnownLocation("gps");

        coordEditor.setText(location.getLatitude() + ", " + location.getLongitude());
    }
}