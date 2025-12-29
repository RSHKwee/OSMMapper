package kwee.osmmapper.lib;

public class Const {
  public static final double c_LongLatUndefined = -500.0;
  public static final int c_ZoomUndefined = -1;

  /**
   * Compare double's
   * 
   * @param a Double 1
   * @param b Double 2
   * @return True: doubles are equal
   */
  public static boolean compareDouble(double a, double b) {
    boolean bstat = false;
    bstat = Double.valueOf(a).equals(Double.valueOf(b));
    return bstat;
  }
}
