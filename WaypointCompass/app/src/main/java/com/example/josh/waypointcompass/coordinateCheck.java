package com.example.josh.waypointcompass;

public class coordinateCheck {

    public boolean OSCheck (String x)//An OS Grid Reference has to be in a certain format, this checks if a string is in that format
    {
        String [] a = x.split("");//Splits the string into an array which each element being a letter, the first element is a blank charcter
        if (a.length!=9)return false;//An OS Grid Reference will be 8 characters long, the blank element makes it 9
        else if (!(a[1].equals("H")||a[1].equals("N")||a[1].equals("S")||a[1].equals("O")||a[1].equals("T"))) return false;//the 1st character must be on these letters
        else if (a[2].equals("I")) return false;//The second character can't be I
        else if (!numberCheck(a)) return false;//All the rest must numbers
        return true;
    }

    private boolean numberCheck (String [] a)
    {
        for (int i = 3;i<a.length;i++) {
            try
            {
                Double.parseDouble(a[i]);
            }
            catch (NumberFormatException e)//If the string isn't a number
            {
                return false;
            }
        }
        return true;
    }
/*
    This function formats a string to be in a format understood by the display activity.
    This means that you can have the input in terms of degrees East or West and North or
    South and it will convert it in to a positive or negative latitude and longitude.
    The East-West and North-South can be either way round on input and will be corrected.
 */
    public String coordFormat (String coord)
    {
        if ((coord.indexOf("S")<coord.indexOf(",")&&coord.contains("S"))||(coord.indexOf("N")<coord.indexOf(",")&&coord.contains("N")))//Checks if the North-South measurement comes first
        {
            if (coord.contains("S")) coord = "-"+coord.replace("S","");//South is negative in the latitude system so this makes the North-South measurement negative if it's South

            if (coord.contains("W"))//West is negative so if it contains W then the East-West value needs to be negative
            {
                coord = coord.replace("W","");
                String [] a = coord.split("");//Splitting the string into the letters of the input string
                int x = coord.indexOf(",");//Finding the location where the coordinates split between the North-South and East-West
                coord = "";
                for (int i = 0;i<x+2;i++) coord = coord+a[i];//Rebuilding the first half of the string until just before the East-West values
                coord = coord+"-";//Adding the negative just before the East-West value
                for (int i = x+2;i<a.length;i++) coord = coord + a[i];//Rebuilding the rest of the string
            }
        }
        else//If the East-West values comes before the North-South value
        {
            if (coord.contains("W")) coord = "-"+coord.replace("W","");

            if (coord.contains("S"))
            {
                coord = coord.replace("S","");
                String [] a = coord.split("");
                int x = coord.indexOf(",");
                coord = "-";
                for (int i = x+2;i<a.length;i++)//Making the string so that the latitude is first then the longitude
                {
                    coord = coord+a[i];
                }
                coord = coord+",";
                for (int i = 0;i<x+1;i++)//Then putting the first half of the string after the negative
                {
                    coord = coord + a[i];
                }
            }
            else if (coord.contains("N"))
            {
                coord = coord.replace("N", "");//Removing the North and East characters
                coord = coord.replace("E", "");
                String [] a = coord.split("");//Making the string so that the latitude is first then the longitude
                int x = coord.indexOf(",");
                coord = "";
                for (int i = x+2;i<a.length;i++) coord = coord+a[i];
                coord = coord +",";
                for (int i = 0;i<x+1;i++) coord = coord + a[i];
            }
        }
        coord = coord.replace("N", "");//Removing the North and East characters
        coord = coord.replace("E", "");
        return coord;
    }

    public boolean coordCheck (String coord)//The longitude and latitude must be checked to be in range
    {
        String [] c = coord.split(",");
        if (c.length != 2 ) return false;
        try
        {
            double a = Double.parseDouble(c[0]);
            double b = Double.parseDouble(c[1]);
            if (Math.abs(a)>90) return false;//The latitude must be between -90 and 90 so the modulus must be less than 90
            if (Math.abs(b)>180) return false;//The longitude must be between -180 and 180 so the modulus must be less than 180
        }
        catch (NumberFormatException e)//If the string isn't a number
        {
            return false;
        }
        return true;
    }
}