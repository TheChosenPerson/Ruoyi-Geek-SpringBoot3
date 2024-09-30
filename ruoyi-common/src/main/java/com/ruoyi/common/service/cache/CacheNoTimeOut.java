package com.ruoyi.common.service.cache;

public interface CacheNoTimeOut {

    public <T> void setCacheObject(final String cacheName,final String key, final T value);

}
