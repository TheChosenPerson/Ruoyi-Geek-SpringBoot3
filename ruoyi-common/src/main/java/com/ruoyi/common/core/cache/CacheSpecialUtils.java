package com.ruoyi.common.core.cache;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.Cache;

public interface CacheSpecialUtils {
    public Set<String> getKeys(Cache cache);

    default public <T> void set(String string, T value, long timeout, TimeUnit unit){}

    default public <T> void set(String string, T value){};
}
