package context.dataSecurity;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.enums.SqlType;
import com.ruoyi.common.model.JoinTableModel;
import com.ruoyi.common.model.WhereModel;

public class SqlContextHolder {
   private static final ThreadLocal<JSONObject> SQL_CONTEXT_HOLDER = new ThreadLocal<>();

   public static void startDataSecurity() {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("isSecurity", Boolean.TRUE);
      jsonObject.put(SqlType.WHERE.getSqlType(), new JSONArray());
      jsonObject.put(SqlType.JOIN.getSqlType(), new JSONArray());
      SQL_CONTEXT_HOLDER.set(jsonObject);
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
}
