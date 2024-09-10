package com.ruoyi.mybatisinterceptor.util;

import java.util.List;

import com.ruoyi.mybatisinterceptor.context.page.PageContextHolder;
import com.ruoyi.mybatisinterceptor.context.page.model.RuoyiTableData;
import com.ruoyi.mybatisinterceptor.context.page.model.TableInfo;

public class PageUtils {
    public static <E> RuoyiTableData toTableInfo(List<E> list) {
        if (list instanceof TableInfo) {
            TableInfo<E> tableInfo = (TableInfo<E>) list;
            RuoyiTableData ruoyiTableData = new RuoyiTableData();
            ruoyiTableData.setData(list);
            ruoyiTableData.setTotal(tableInfo.getTotal());
            return ruoyiTableData;
        }
        RuoyiTableData ruoyiTableData = new RuoyiTableData();
        ruoyiTableData.setData(list);
        ruoyiTableData.setTotal(-1L);
        return ruoyiTableData;

    }

    public static void ruoyiStartPage() {
        PageContextHolder.startPage();
        PageContextHolder.setPageInfo();
    }

}
