package com.example.josh.waypointcompass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;

public class MainActivity extends AppCompatActivity
{
    private boolean GridRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);//Connects the button
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()//When the button is clicked the function gets called.
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                GridRef = isChecked;//when the button is clicked the input type would be a grid reference so GridRef becomes true
            }
        });
    }

    public void sendCoords(View view)//Is called when the confirm button is pressed
    {
        coordinateCheck c = new coordinateCheck();

        TextView textView = (TextView) findViewById(R.id.errorMessage);//Setting the

        Intent intent = new Intent(this, Display.class);
        EditText editText = (EditText) findViewById(R.id.enter_long_lat);

        String message = editText.getText().toString().toUpperCase().replace(" ","");//Getting the text from the text field
        if (GridRef)//If the input type is a OS Grid Reference
        {
            if (c.OSCheck(message))//If the input is a legitimate OS Grid Reference
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
            message = c.coordFormat(message);//Putting the coordinate in the correct format
            if (c.coordCheck(message))//If the coordinates are legitimate coordinates
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