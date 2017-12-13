package ht.msc.mybatis.plugin;

public final class EqualsUtil
{
  public static boolean areEqual(boolean aThis, boolean aThat)
  {
    return aThis == aThat;
  }

  public static boolean areEqual(char aThis, char aThat) {
    return aThis == aThat;
  }

  public static boolean areEqual(long aThis, long aThat)
  {
    return aThis == aThat;
  }

  public static boolean areEqual(float aThis, float aThat) {
    return Float.floatToIntBits(aThis) == Float.floatToIntBits(aThat);
  }

  public static boolean areEqual(double aThis, double aThat) {
    return Double.doubleToLongBits(aThis) == Double.doubleToLongBits(aThat);
  }

  public static boolean areEqual(Object aThis, Object aThat)
  {
    return aThis == null ? false : aThat == null ? true : aThis.equals(aThat);
  }
}