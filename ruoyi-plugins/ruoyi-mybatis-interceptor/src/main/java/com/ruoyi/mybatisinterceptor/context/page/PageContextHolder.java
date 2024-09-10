package com.ruoyi.mybatisinterceptor.context.page;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.mybatisinterceptor.context.page.model.PageInfo;

public class PageContextHolder {
   private static final ThreadLocal<JSONObject> PAGE_CONTEXT_HOLDER = new ThreadLocal<>();

   private static final String PAGE_FLAG = "isPage";

   private static final String PAGE_INFO = "pageInfo";

   private static final String TOTAL = "total";

   public static void startPage() {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put(PAGE_FLAG, Boolean.TRUE);
      PAGE_CONTEXT_HOLDER.set(jsonObject);
   }

   public static void setPageInfo() {
      PAGE_CONTEXT_HOLDER.get().put(PAGE_INFO, PageInfo.defaultPageInfo());
   }

   public static PageInfo getPageInfo() {
      return (PageInfo) PAGE_CONTEXT_HOLDER.get().get(PAGE_INFO);
   }

   public static void clear() {
      PAGE_CONTEXT_HOLDER.remove();
   }

   public static boolean isPage() {
      return PAGE_CONTEXT_HOLDER.get() != null && PAGE_CONTEXT_HOLDER.get().getBooleanValue(PAGE_FLAG);
   }

   public static void setTotal(Long total) {
      PAGE_CONTEXT_HOLDER.get().put(TOTAL, total);
   }

   public static Long getTotal() {
      return PAGE_CONTEXT_HOLDER.get().getLong(TOTAL);
   }
}
