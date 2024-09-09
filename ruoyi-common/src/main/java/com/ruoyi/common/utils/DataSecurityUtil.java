package com.ruoyi.common.utils;

import com.ruoyi.common.context.dataSecurity.DataSecurityContextHolder;

public class DataSecurityUtil {

   public static void closeDataSecurity() {
      DataSecurityContextHolder.clearCache();
   }

   public static void startDataSecurity() {
      DataSecurityContextHolder.startDataSecurity();
   }
}
