package com.example.josh.waypointcompass;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BearingTest {

  @Test
  public void test(){
    Display display = new Display();

    assertEquals(display.findBearingTo(0,0,1,0),0);
    assertEquals(display.findBearingTo(0,0,1,1),45);
    assertEquals(display.findBearingTo(0,0,0,1),90);
    assertEquals(display.findBearingTo(0,0,-1,1),135);
    assertEquals(display.findBearingTo(0,0,-1,0),180);
    assertEquals(display.findBearingTo(0,0,-1,-1),225);
    assertEquals(display.findBearingTo(0,0,0,-1),270);
    assertEquals(display.findBearingTo(0,0,1,-1),315);
  
    assertEquals(display.findBearingTo(0,180,1,180),0);
    assertEquals(display.findBearingTo(0,180,1,-179),45);
    assertEquals(display.findBearingTo(0,180,0,-179),90);
    assertEquals(display.findBearingTo(0,180,-1,-179),135);
    assertEquals(display.findBearingTo(0,180,-1,180),180);
    assertEquals(display.findBearingTo(0,180,-1,179),225);
    assertEquals(display.findBearingTo(0,180,0,179),270);
    assertEquals(display.findBearingTo(0,180,1,179),315);
  
  
    assertEquals(display.findBearingTo(51,1,27,87),75);
    assertEquals(display.findBearingTo(51,1,27,-87),286);
    assertEquals(display.findBearingTo(51,1,-27,-87),251);
    assertEquals(display.findBearingTo(-51,1,27,-87),289);
  
  
    assertEquals(display.findBearingTo(-1,179,1,-179),45);
    assertEquals(display.findBearingTo(1,179,-1,-179),135);
    assertEquals(display.findBearingTo(-1,-179,1,179),315);

  
    assertEquals(display.findBearingTo(-10,0,-8,60),93);
    assertEquals(display.findBearingTo(-10,41,-8,3),270);
    assertEquals(display.findBearingTo(-10,100,-8,3),261);
    assertEquals(display.findBearingTo(-20,-100,-8,3),102);
    assertEquals(display.findBearingTo(-10,0,-8,0),0);
    assertEquals(display.findBearingTo(-10,0,-11,0),180);
    assertEquals(display.findBearingTo(-10,0,-8,34),89);
    assertEquals(display.findBearingTo(10,0,8,34),91);
    
  }
}
