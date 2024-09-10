package com.ruoyi.mybatisinterceptor.context.page.model;

import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.utils.ServletUtils;

public class PageInfo {

   private Long pageNumber;

   private Long pageSize;

   /**
    * 当前记录起始索引
    */
   public static final String PAGE_NUM = "pageNum";

   /**
    * 每页显示记录数
    */
   public static final String PAGE_SIZE = "pageSize";

   /**
    * 排序列
    */
   public static final String ORDER_BY_COLUMN = "orderByColumn";

   /**
    * 排序的方向 "desc" 或者 "asc".
    */
   public static final String IS_ASC = "isAsc";

   /**
    * 分页参数合理化
    */
   public static final String REASONABLE = "reasonable";

   public Long getPageNumber() {
      return pageNumber;
   }

   public void setPageNumber(Long pageNumber) {
      this.pageNumber = pageNumber;
   }

   public Long getPageSize() {
      return pageSize;
   }

   public void setPageSize(Long pageSize) {
      this.pageSize = pageSize;
   }

   public static PageInfo defaultPageInfo() {
      PageInfo pageInfo = new PageInfo();
      pageInfo.setPageNumber(Long.valueOf(Convert.toInt(ServletUtils.getParameter(PAGE_NUM), 1)));
      pageInfo.setPageSize(Long.valueOf(Convert.toInt(ServletUtils.getParameter(PAGE_SIZE), 10)));
      return pageInfo;
   }

   public Long getOffeset() {
      return (pageNumber.longValue() - 1L) * pageSize;
   }
}
