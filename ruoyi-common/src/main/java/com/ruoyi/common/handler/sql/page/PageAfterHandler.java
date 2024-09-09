package com.ruoyi.common.handler.sql.page;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ruoyi.common.annotation.sql.MybatisHandlerOrder;
import com.ruoyi.common.context.page.PageContextHolder;
import com.ruoyi.common.context.page.model.TableInfo;
import com.ruoyi.common.handler.sql.MybatisAfterHandler;

@MybatisHandlerOrder(1)
@Component
public class PageAfterHandler implements MybatisAfterHandler {

   @Override
   public Object handleObject(Object object) throws Throwable {
      if (PageContextHolder.isPage()) {
         if (object instanceof List) {
            TableInfo tableInfo = new TableInfo<>((List) object);
            tableInfo.setTotal(PageContextHolder.getTotal());
            PageContextHolder.clear();
            return tableInfo;
         }
         return object;
      }
      return object;
   }

}
