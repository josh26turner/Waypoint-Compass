package com.example.josh.waypointcompass;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class Main {

  public static void main(String[] args) {
    test();
  }
  
  private static void error (int i, int num1, int num2)
  {
    if (num1!=num2){
      System.out.println("Failed at test "+i+" got "+num1+" should have been "+num2+" difference is "+(num2-num1));
    }
    else {
      System.out.println("Test "+i+" passed");
    }
  }
  
  private static void test(){
    error(1, Display.findBearingTo(0,0,1,0),0);
    error(2, Display.findBearingTo(0,0,1,1),45);
    error(3, Display.findBearingTo(0,0,0,1),90);
    error(4, Display.findBearingTo(0,0,-1,1),135);
    error(5, Display.findBearingTo(0,0,-1,0),180);
    error(6, Display.findBearingTo(0,0,-1,-1),225);
    error(7, Display.findBearingTo(0,0,0,-1),270);
    error(8, Display.findBearingTo(0,0,1,-1),315);
  
    error(9, Display.findBearingTo(0,180,1,180),0);
    error(10, Display.findBearingTo(0,180,1,-179),45);
    error(11, Display.findBearingTo(0,180,0,-179),90);
    error(12, Display.findBearingTo(0,180,-1,-179),135);
    error(13, Display.findBearingTo(0,180,-1,180),180);
    error(14, Display.findBearingTo(0,180,-1,179),225);
    error(15, Display.findBearingTo(0,180,0,179),270);
    error(16, Display.findBearingTo(0,180,1,179),315);
  
  
    error(17, Display.findBearingTo(51,1,27,87),75);
    error(18, Display.findBearingTo(51,1,27,-87),286);
    error(19, Display.findBearingTo(51,1,-27,-87),251);
    error(20, Display.findBearingTo(-51,1,27,-87),289);
  
  
    error(21, Display.findBearingTo(-1,179,1,-179),45);
    error(22, Display.findBearingTo(1,179,-1,-179),135);
    error(23, Display.findBearingTo(-1,-179,1,179),315);

  
    error(24, Display.findBearingTo(-10,0,-8,60),93);
    error(25, Display.findBearingTo(-10,41,-8,3),270);
    error(26, Display.findBearingTo(-10,100,-8,3),261);
    error(27, Display.findBearingTo(-20,-100,-8,3),102);
    error(28, Display.findBearingTo(-10,0,-8,0),0);
    error(29, Display.findBearingTo(-10,0,-11,0),180);
    error(30, Display.findBearingTo(-10,0,-8,34),89);
    error(31, Display.findBearingTo(10,0,8,34),91);
    
  }
}
