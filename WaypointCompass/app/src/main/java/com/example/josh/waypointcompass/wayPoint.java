package com.example.josh.waypointcompass;

import java.io.Serializable;

public class wayPoint implements Serializable
{
    public String name;
    public double longitude;
    public double latitude;
    public String routeName ="";
    public int routePosition;
}
