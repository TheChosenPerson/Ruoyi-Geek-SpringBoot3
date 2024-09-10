package com.ruoyi.mybatisinterceptor.util;


import com.ruoyi.mybatisinterceptor.context.dataSecurity.SqlContextHolder;

public class DataSecurityUtil {

   public static void closeDataSecurity() {
      SqlContextHolder.clearCache();
   }

   public static void startDataSecurity() {
      SqlContextHolder.startDataSecurity();
   }
}
