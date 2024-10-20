package com.ruoyi.framework.datasource;

import java.util.Objects;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据源切换处理
 * 
 * @author ruoyi
 */
public class DynamicDataSourceContextHolder {
    public static final Logger log = LoggerFactory.getLogger(DynamicDataSourceContextHolder.class);

    /**
     * 使用ThreadLocal维护变量，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
     * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
     */
    private static final ThreadLocal<Stack<String>> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 设置数据源的变量
     */
    public static void setDataSourceType(String dsType) {
        log.info("切换到{}数据源", dsType);
        Stack<String> stack = CONTEXT_HOLDER.get();
        if (Objects.isNull(stack)) {
            stack = new Stack<>();
            CONTEXT_HOLDER.set(stack);
        }
        stack.push(dsType);
    }

    /**
     * 获得数据源的变量
     */
    public static String getDataSourceType() {
        Stack<String> stack = CONTEXT_HOLDER.get();
        if (Objects.isNull(stack)) {
            return null;
        } else {
            return stack.peek();
        }
    }

    /**
     * 清空数据源变量
     */
    public static void clearDataSourceType() {
        Stack<String> stack = CONTEXT_HOLDER.get();
        if (Objects.nonNull(stack) && !stack.isEmpty()) {
            stack.pop();
            if (stack.isEmpty()) {
                CONTEXT_HOLDER.remove();
            }
        }
    }
}
