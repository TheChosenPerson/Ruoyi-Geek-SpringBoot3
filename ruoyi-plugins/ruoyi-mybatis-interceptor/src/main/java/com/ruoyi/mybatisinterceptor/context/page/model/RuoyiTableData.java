package com.ruoyi.mybatisinterceptor.context.page.model;

import java.util.List;

public class RuoyiTableData {
   private Long total;
   private List<?> data;

   public Long getTotal() {
      return total;
   }

   public void setTotal(Long total) {
      this.total = total;
   }

   public List<?> getData() {
      return data;
   }

   public void setData(List<?> data) {
      this.data = data;
   }

}
