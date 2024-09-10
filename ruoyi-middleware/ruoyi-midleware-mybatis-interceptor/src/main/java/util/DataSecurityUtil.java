package util;


import context.dataSecurity.SqlContextHolder;

public class DataSecurityUtil {

   public static void closeDataSecurity() {
      SqlContextHolder.clearCache();
   }

   public static void startDataSecurity() {
      SqlContextHolder.startDataSecurity();
   }
}
