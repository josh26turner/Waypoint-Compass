package com.example.josh.waypointcompass;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class dbListing extends AppCompatActivity {
    private db database = new db(this);
    private List<wayPoint> wayPointList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_listing);

        updateWayPointTable();
    }

    public void updateTable(View v)//When the refresh button is pressed this function is called
    {
        updateWayPointTable();
    }

    private void updateWayPointTable ()
    {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.wayPointTable);//Connecting to the table
        tableLayout.removeAllViews();

        wayPointList = database.getAllWayPoints();//Getting all the way points saved in the database

        for (int i = 0;i<wayPointList.size();i++)
        {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            final int finalI = i;

            TextView textView = new TextView(this);
            textView.setTextColor(Color.BLACK);
            textView.setText(wayPointList.get(i).name+": "+
                    round(wayPointList.get(i).latitude,2)+", "+
                    round(wayPointList.get(i).longitude,2));
            row.addView(textView);

            TableRow buttons = new TableRow(this);

            final Button update = new Button(this);
            update.setText("UPDATE");
            update.setTextSize(14);
            update.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v)
                {
                    updateWP(wayPointList.get(finalI));
                }
            });
            buttons.addView(update);

            final Button delete = new Button(this);
            delete.setText("DELETE");
            delete.setTextSize(14);
            delete.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v)
                {
                    database.delete(wayPointList.get(finalI).name);
                    updateWayPointTable();
                }
            });
            buttons.addView(delete);

            final Button select = new Button(this);
            select.setText("SELECT");
            select.setTextSize(14);
            select.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v)
                {
                    select(wayPointList.get(finalI).latitude,wayPointList.get(finalI).longitude);
                }
            });
            buttons.addView(select);

            tableLayout.addView(row);
            tableLayout.addView(buttons);
        }
    }

    public void select (double lat, double lon)
    {
        Intent display = new Intent(this, Display.class);
        display.putExtra("coords",lat+","+lon);
        startActivity(display);
    }

    private void updateWP(wayPoint wp)
    {
        Intent i = new Intent(this,addWayPoint.class);
        i.putExtra("wp",wp);
        startActivity(i);
    }

    public void addWP(View view)
    {
        Intent i = new Intent(this, addWayPoint.class);
        i.putExtra(null,"wp");
        startActivity(i);
    }

    private double round (double num, int dp)
    {
        num = num*Math.pow(10, dp);
        num = Math.round(num);
        return num/Math.pow(10, dp);
    }
}