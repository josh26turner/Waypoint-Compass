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
        updateRouteTable();
    }

    public void updateTable(View v)//When the refresh button is pressed this function is called
    {
        updateWayPointTable();
        updateRouteTable();
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
                    round(wayPointList.get(i).longitude,2)+":"+
                    wayPointList.get(i).routePosition);
            row.addView(textView);

            final Button update = new Button(this);
            update.setText("UPDATE");
            update.setTextSize(14);
            update.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v)
                {
                    updateWP(wayPointList.get(finalI));
                }
            });
            row.addView(update);

            final Button delete = new Button(this);
            delete.setText("DELETE");
            delete.setTextSize(14);
            delete.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v)
                {
                    database.delete(wayPointList.get(finalI).name);
                    updateWayPointTable();
                    updateRouteTable();
                }
            });
            row.addView(delete);

            final Button select = new Button(this);
            select.setText("SELECT");
            select.setTextSize(14);
            select.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v)
                {
                    select(wayPointList.get(finalI).latitude,wayPointList.get(finalI).longitude);
                }
            });
            row.addView(select);

            tableLayout.addView(row);
        }
    }

    public void updateRouteTable()
    {

        TableLayout tableLayout = (TableLayout) findViewById(R.id.routesTable);
        tableLayout.removeAllViews();

        final List<String> routeNames = new ArrayList<>();
        for (int i = 0;i<wayPointList.size();i++)
        {
            if (!(routeNames.contains(wayPointList.get(i).routeName)))
            {
                routeNames.add(wayPointList.get(i).routeName);
            }
        }

        for (int i = 0;i<routeNames.size();i++)
        {
            if (!routeNames.get(i).equals("")&&!routeNames.get(i).equals(null))
            {
                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                final int finalI = i;

                TextView textView = new TextView(this);
                textView.setTextColor(Color.BLACK);
                textView.setText(routeNames.get(i));
                row.addView(textView);

                final Button select = new Button(this);
                select.setText("SELECT");
                select.setTextSize(14);
                select.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        selectRoute(routeNames.get(finalI));
                    }
                });
                row.addView(select);

                tableLayout.addView(row);
            }
        }
    }

    public void select (double lat, double lon)
    {
        Intent display = new Intent(this, Display.class);
        display.putExtra("coords",lat+","+lon);
        startActivity(display);
    }

    private void selectRoute(String routeName)
    {
        List<wayPoint> wayPointList = database.getWayPoints(routeName);
        double [] coords = new double[wayPointList.size()*2];
        for (int i = 0;i<wayPointList.size();i+=2)
        {
            coords[i] = wayPointList.get(i).latitude;
            coords[i+1] = wayPointList.get(i).longitude;
        }
        Intent i = new Intent(this, Display.class);
        i.putExtra("route",true);
        i.putExtra("routeWayPoints",coords);
        startActivity(i);
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