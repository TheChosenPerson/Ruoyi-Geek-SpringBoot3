package com.ruoyi.common.context.dataSecurity;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.enums.SqlType;
import com.ruoyi.common.model.JoinTableModel;
import com.ruoyi.common.model.WhereModel;

public class DataSecurityContextHolder {
   private static final ThreadLocal<JSONObject> DATA_SECURITY_SQL_CONTEXT_HOLDER = new ThreadLocal<>();

   public static void startDataSecurity() {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("isSecurity", Boolean.TRUE);
      jsonObject.put(SqlType.WHERE.getSqlType(), new JSONArray());
      jsonObject.put(SqlType.JOIN.getSqlType(), new JSONArray());
      DATA_SECURITY_SQL_CONTEXT_HOLDER.set(jsonObject);
   }

   public static void addWhereParam(WhereModel whereModel) {
      DATA_SECURITY_SQL_CONTEXT_HOLDER.get().getJSONArray(SqlType.WHERE.getSqlType()).add(whereModel);
   }

   public static void clearCache() {
      DATA_SECURITY_SQL_CONTEXT_HOLDER.remove();
   }

   public static boolean isSecurity() {

      return DATA_SECURITY_SQL_CONTEXT_HOLDER.get() != null
            && DATA_SECURITY_SQL_CONTEXT_HOLDER.get().getBooleanValue("isSecurity");
   }

   public static JSONArray getWhere() {
      return DATA_SECURITY_SQL_CONTEXT_HOLDER.get().getJSONArray(SqlType.WHERE.getSqlType());
   }

   public static void addJoinTable(JoinTableModel joinTableModel) {
      DATA_SECURITY_SQL_CONTEXT_HOLDER.get().getJSONArray(SqlType.JOIN.getSqlType()).add(joinTableModel);
   }

   public static JSONArray getJoinTables() {
      return DATA_SECURITY_SQL_CONTEXT_HOLDER.get().getJSONArray(SqlType.JOIN.getSqlType());
   }
}
