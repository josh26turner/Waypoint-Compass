package com.example.josh.waypointcompass;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.support.v7.widget.Toolbar;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;

public class MainActivity extends AppCompatActivity
{
    private boolean GridRef;
    private com.example.josh.waypointcompass.MainActivity thisClass = this;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        drawerLayout.closeDrawers();
                        Intent i = new Intent(thisClass,dbListing.class);
                        startActivity(i);

                        return true;
                    }
                }
        );

        ToggleButton toggle = findViewById(R.id.toggleButton);//Connects the button
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()//When the button is clicked the function gets called.
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                GridRef = isChecked;//when the button is clicked the input type would be a grid reference so GridRef becomes true
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendCoords(View view)//Is called when the confirm button is pressed
    {
        coordinateCheck coordinateCheck = new coordinateCheck();

        TextView textView = findViewById(R.id.errorMessage);

        Intent intent = new Intent(this, Display.class);
        EditText editText = findViewById(R.id.enter_long_lat);

        String message = editText.getText().toString().toUpperCase().replace(" ","");//Getting the text from the text field
        if (GridRef)//If the input type is a OS Grid Reference
        {
            if (coordinateCheck.OSCheck(message))//If the input is a legitimate OS Grid Reference
            {
                OSRef r = new OSRef(message);
                LatLng l = r.toLatLng();//Converting the grid reference to a latitude and longitude
                message = l.getLat() + "," + l.getLng();//Putting the latitude and longitude in the format which is required for display activity
                intent.putExtra("coords", message);//Adding the coordinates to the bundle being sent to the display activity
                startActivity(intent);//starting the display activity and sending the bundle
            }
            else textView.setText("\nPlease enter a valid OS Grid Reference"//If the text doesn't pass the check then this message is displayed to the user
                    + "\nIn the form: RRXXXYYY"
                    + "\nWhere RR is the map reference(2 letters in top right of map)"
                    + "\nXXX is the eastings"
                    + "\nYYY is the northings");
        }
        else//If the input ype is a longitude and latitude coordinate
        {
            message = coordinateCheck.coordFormat(message);//Putting the coordinate in the correct format
            if (coordinateCheck.coordCheck(message))//If the coordinates are legitimate coordinates
            {
                intent.putExtra("coords", message);//Adding the coordinates to the bundle being sent to the display activity
                startActivity(intent);//starting the display activity and sending the bundle
            }
            else textView.setText("\nPlease enter a valid coordinate\nIn the form latitude, longitude");//If the text doesn't pass the check this message is displayed
        }
    }

    public void list (View v)//If the list button is pressed this function is called
    {
        Intent i = new Intent(this,dbListing.class);
        startActivity(i);//Starting the activity which lists the contents of the database
    }
}