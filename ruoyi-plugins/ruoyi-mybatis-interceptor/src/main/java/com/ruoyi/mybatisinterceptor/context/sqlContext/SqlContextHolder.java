package com.ruoyi.mybatisinterceptor.context.sqlContext;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.mybatisinterceptor.enums.SqlType;
import com.ruoyi.mybatisinterceptor.model.JoinTableModel;
import com.ruoyi.mybatisinterceptor.model.WhereModel;

public class SqlContextHolder {
   private static final ThreadLocal<JSONObject> SQL_CONTEXT_HOLDER = new ThreadLocal<>();

   public static void startDataSecurity() {
      SQL_CONTEXT_HOLDER.get().put("isSecurity", Boolean.TRUE);
   }

   public static void startLogicSelect() {
      SQL_CONTEXT_HOLDER.get().put("isLogic", Boolean.TRUE);
   }

   public static void addWhereParam(WhereModel whereModel) {
      SQL_CONTEXT_HOLDER.get().getJSONArray(SqlType.WHERE.getSqlType()).add(whereModel);
   }

   public static void clearCache() {
      SQL_CONTEXT_HOLDER.remove();
   }

   public static boolean isSecurity() {
      return SQL_CONTEXT_HOLDER.get() != null
            && SQL_CONTEXT_HOLDER.get().getBooleanValue("isSecurity");
   }

   public static JSONArray getWhere() {
      return SQL_CONTEXT_HOLDER.get().getJSONArray(SqlType.WHERE.getSqlType());
   }

   public static void addJoinTable(JoinTableModel joinTableModel) {
      SQL_CONTEXT_HOLDER.get().getJSONArray(SqlType.JOIN.getSqlType()).add(joinTableModel);
   }

   public static JSONArray getJoinTables() {
      return SQL_CONTEXT_HOLDER.get().getJSONArray(SqlType.JOIN.getSqlType());
   }

   public static void startInterceptor() {
      JSONObject jsonObject = SQL_CONTEXT_HOLDER.get();
      if (jsonObject != null) {
         return;
      }
      JSONObject object = new JSONObject();
      object.put(SqlType.JOIN.getSqlType(), new JSONArray());
      object.put(SqlType.WHERE.getSqlType(), new JSONArray());
      SQL_CONTEXT_HOLDER.set(object);
   }
}
