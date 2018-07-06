package com.example.josh.waypointcompass;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

    @Override
    public void onResume(){
        super.onResume();

        updateWayPointTable();
    }

    private void updateWayPointTable ()
    {
        TableLayout tableLayout = findViewById(R.id.wayPointTable);//Connecting to the table
        tableLayout.removeAllViews();

        wayPointList = database.getAllWayPoints();//Getting all the way points saved in the database

        for (int i = 0;i<wayPointList.size();i++)
        {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            row.setLayoutParams(lp);

            final int finalI = i;

            TextView textView = new TextView(this);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(24);
            textView.setText(wayPointList.get(i).name);
            row.addView(textView);

            final ImageButton update = new ImageButton(this);
            update.setImageResource(R.drawable.ic_update);
            update.setBackground(null);
            update.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v)
                {
                    updateWP(wayPointList.get(finalI));
                }
            });
            row.addView(update);

            final ImageButton select = new ImageButton(this);
            select.setImageResource(R.drawable.ic_navigation_24dp);
            select.setBackground(null);
            select.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v)
                {
                    select(wayPointList.get(finalI).latitude,wayPointList.get(finalI).longitude);
                }
            });
            row.addView(select);

            final ImageButton delete = new ImageButton(this);
            delete.setImageResource(R.drawable.ic_delete);
            delete.setBackground(null);
            delete.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v)
                {
                    database.delete(wayPointList.get(finalI).name);
                    updateWayPointTable();
                }
            });
            row.addView(delete);

            tableLayout.addView(row);
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