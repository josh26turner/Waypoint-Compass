package com.example.josh.waypointcompass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class db extends SQLiteOpenHelper
{
    public db(Context context)
    {
        super(context, "Locations", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

        String wpCreate = "CREATE TABLE IF NOT EXISTS wayPoints " +
                "(" +
                "ID             INT         PRIMARY KEY, " +
                "name           VARCHAR(30) UNIQUE NOT NULL, " +
                "longitude      DOUBLE      NOT NULL, " +
                "latitude       DOUBLE      NOT NULL, " +
                "route          VARCHAR(30)," +
                "routePosition  INT" +
                ")";

        db.execSQL(wpCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (oldVersion!=newVersion)
        {
            db.execSQL("DROP IF EXISTS wayPoints");
            db.execSQL("DROP IF EXISTS routes");
            onCreate(db);
        }
    }

    public boolean addWayPoint(wayPoint wayPoint)
    {
        boolean added = false;
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put("name",wayPoint.name);
        values.put("longitude",wayPoint.longitude);
        values.put("latitude",wayPoint.latitude);
        values.put("route",wayPoint.routeName);
        values.put("routePosition",wayPoint.routePosition);

        try
        {
            if (db.insert("wayPoints",null, values)==-1) return false;
            db.setTransactionSuccessful();
            added = true;
        }
        catch (Exception e)
        {
            Log.d(TAG,"Error while trying to add waypoint");
        }finally
        {
            db.endTransaction();
        }
        return added;
    }


    public List<wayPoint> getAllWayPoints()
    {
        List<wayPoint> wayPoints = new ArrayList<>();

        String selectQuery = "SELECT * FROM wayPoints ORDER BY UPPER(name) ASC";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        try
        {
            if (cursor.moveToFirst())
            {
                do
                {
                    wayPoint newWayPoint = new wayPoint();
                    newWayPoint.name = cursor.getString(cursor.getColumnIndex("name"));
                    newWayPoint.latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                    newWayPoint.longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                    newWayPoint.routeName = cursor.getString(cursor.getColumnIndex("route"));
                    newWayPoint.routePosition = cursor.getInt(cursor.getColumnIndex("routePosition"));

                    wayPoints.add(newWayPoint);
                }while (cursor.moveToNext());
            }
        }
        catch (Exception e)
        {
            Log.d(TAG,"Error while trying to get waypoints from database");
        }
        finally
        {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }
        return wayPoints;
    }
    public List<wayPoint> getWayPoints(String routeName)
    {
        List<wayPoint> wayPoints = new ArrayList<>();

        String selectQuery = "SELECT * FROM wayPoints WHERE route == ? ORDER BY routePosition ASC";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,new String[]{String.valueOf(routeName)});

        try
        {
            if (cursor.moveToFirst())
            {
                do
                {
                    wayPoint newWayPoint = new wayPoint();
                    newWayPoint.name = cursor.getString(cursor.getColumnIndex("name"));
                    newWayPoint.latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                    newWayPoint.longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                    newWayPoint.routePosition = cursor.getInt(cursor.getColumnIndex("routePosition"));

                    wayPoints.add(newWayPoint);
                }while (cursor.moveToNext());
            }
        }
        catch (Exception e)
        {
            Log.d(TAG,"Error while trying to get waypoints from database");
        }
        finally
        {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }
        return wayPoints;
    }

    public boolean updateWayPoint (wayPoint wayPoint,String name)
    {
        boolean added = false;
        if (wayPoint.name.equals(name))
        {
            if (delete(name)) if (addWayPoint(wayPoint)) added = true;
        }
        else
        {

            String selectQuery = "SELECT * FROM wayPoints WHERE name = ?";
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery,new String[]{String.valueOf(wayPoint.name)});
            if (cursor.getCount()==0)
            {
                delete(name);
                addWayPoint(wayPoint);
                added = true;
            }
        }
        return added;
    }

    public boolean delete (String name)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        boolean del = false;
        try
        {
            db.delete("wayPoints","name = ?",new String[]{String.valueOf(name)});
            db.setTransactionSuccessful();
            del = true;
        }
        catch (Exception e)
        {
            Log.d(TAG,"Error while deleting record");
            e.printStackTrace();
        }
        finally
        {
            db.endTransaction();
        }
        return del;
    }
}