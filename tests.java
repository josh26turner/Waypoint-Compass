public class Main {

  public static void main(String[] args) {
    test();
  }
  
  private static int findBearingTo(double y1, double x1, double y2, double x2)
  {
    y1 = Math.toRadians(y1);
    x1 = Math.toRadians(x1);
    y2 = Math.toRadians(y2);
    x2 = Math.toRadians(x2);
    double dx = x2 - x1;
    double y = Math.sin(dx)*Math.cos(y2);
    double x = Math.cos(y1)*Math.sin(y2) - Math.sin(y1)*Math.cos(y2)*Math.cos(dx);
    double bearing;
    
    if (y > 0) {
      if (x > 0) bearing = Math.toDegrees(Math.atan(y / x));
      else if (x > 0) bearing = 180 - Math.toDegrees(Math.atan(-y/x));
      else bearing = (180 + Math.toDegrees(Math.atan(y/x))) % 180;
    }
    else if (y < 0){
      if (x > 0) bearing = -Math.toDegrees(Math.atan(-y/x));
      else if (x < 0) bearing = Math.toDegrees(Math.atan(y/x))-180;
      else bearing = 270;
    }
    else {
      if (x >= 0) bearing = 0;
      else bearing = 180;
    }
    
    return (int) (Math.round(bearing)+360)%360;
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
    error(1, findBearingTo(0,0,1,0),0);
    error(2, findBearingTo(0,0,1,1),45);
    error(3, findBearingTo(0,0,0,1),90);
    error(4, findBearingTo(0,0,-1,1),135);
    error(5, findBearingTo(0,0,-1,0),180);
    error(6, findBearingTo(0,0,-1,-1),225);
    error(7, findBearingTo(0,0,0,-1),270);
    error(8, findBearingTo(0,0,1,-1),315);
  
    error(9, findBearingTo(0,180,1,180),0);
    error(10, findBearingTo(0,180,1,-179),45);
    error(11, findBearingTo(0,180,0,-179),90);
    error(12, findBearingTo(0,180,-1,-179),135);
    error(13, findBearingTo(0,180,-1,180),180);
    error(14, findBearingTo(0,180,-1,179),225);
    error(15, findBearingTo(0,180,0,179),270);
    error(16, findBearingTo(0,180,1,179),315);
  
  
    error(17, findBearingTo(51,1,27,87),75);
    error(18, findBearingTo(51,1,27,-87),286);
    error(19, findBearingTo(51,1,-27,-87),251);
    error(20, findBearingTo(-51,1,27,-87),289);
  
  
    error(21, findBearingTo(-1,179,1,-179),45);
    error(22, findBearingTo(1,179,-1,-179),135);
    error(23, findBearingTo(-1,-179,1,179),315);

  
    error(24, findBearingTo(-10,0,-8,60),93);
    error(25, findBearingTo(-10,41,-8,3),270);
    error(26, findBearingTo(-10,100,-8,3),261);
    error(27, findBearingTo(-20,-100,-8,3),102);
    error(28, findBearingTo(-10,0,-8,0),0);
    error(29, findBearingTo(-10,0,-11,0),180);
    error(30, findBearingTo(-10,0,-8,34),89);
    error(31, findBearingTo(10,0,8,34),91);
    
  }
}
