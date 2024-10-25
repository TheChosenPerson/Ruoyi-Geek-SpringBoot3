package com.ruoyi.mybatisinterceptor.handler.page;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ruoyi.mybatisinterceptor.annotation.MybatisHandlerOrder;
import com.ruoyi.mybatisinterceptor.context.page.PageContextHolder;
import com.ruoyi.mybatisinterceptor.context.page.model.TableInfo;
import com.ruoyi.mybatisinterceptor.handler.MybatisAfterHandler;

@MybatisHandlerOrder(1)
@Component
public class PageAfterHandler implements MybatisAfterHandler {

   @Override
   public Object handleObject(Object object) throws Throwable {
      if (PageContextHolder.isPage()) {
         if (object instanceof List) {
            TableInfo<Object> tableInfo = new TableInfo<Object>((List<?>) object);
            tableInfo.setTotal(PageContextHolder.getTotal());
            PageContextHolder.clear();
            return tableInfo;
         }
         return object;
      }
      return object;
   }

}
