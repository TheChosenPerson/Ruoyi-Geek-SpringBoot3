package sql.page;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ruoyi.common.annotation.sql.MybatisHandlerOrder;
import context.page.PageContextHolder;
import context.page.model.TableInfo;
import sql.MybatisAfterHandler;

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
