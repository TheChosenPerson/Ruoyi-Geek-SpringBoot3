package com.ruoyi.common.service.cache;

import java.util.Set;

import org.springframework.cache.Cache;

public interface CacheKeys {

    public Set<String> getCachekeys(final Cache cache);
}
