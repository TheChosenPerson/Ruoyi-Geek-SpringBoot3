package com.ruoyi.framework.config;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

import org.ehcache.core.EhcacheBase;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.ehcache.impl.internal.store.heap.OnHeapStore;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCache;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ruoyi.common.core.cache.CacheSpecialUtils;

@Configuration
@EnableCaching
@ConditionalOnProperty(prefix = "spring.cache", name = { "type" }, havingValue = "jcache", matchIfMissing = false)
public class Ehcache3Config {

    @Bean
    @SuppressWarnings(value = { "unchecked", "rawtypes" })
    public CacheSpecialUtils cacheGetKets() {
        return new CacheSpecialUtils() {
            @Override
            public Set<String> getKeys(Cache cache) {
                Set<String> keyset = new HashSet<>();
                try {
                    JCacheCache jehcache = (JCacheCache) cache;
                    // org.ehcache.jsr107.Eh107Cache 不公开
                    Object nativeCache = jehcache.getNativeCache();
                    Class<?> nativeCacheClass = nativeCache.getClass();
                    Field ehCacheField = nativeCacheClass.getDeclaredField("ehCache");
                    ehCacheField.setAccessible(true);
                    EhcacheBase ehcache = (EhcacheBase) ehCacheField.get(nativeCache);
                    Field storeField = EhcacheBase.class.getDeclaredField("store");
                    storeField.setAccessible(true);
                    OnHeapStore store = (OnHeapStore) storeField.get(ehcache);
                    Field mapField = OnHeapStore.class.getDeclaredField("map");
                    mapField.setAccessible(true);
                    // org.ehcache.impl.internal.store.heap.Backend 不公开
                    Object map = mapField.get(store);
                    Class<?> mapClass = map.getClass();
                    Field realMapField = mapClass.getDeclaredField("realMap");
                    realMapField.setAccessible(true);
                    ConcurrentHashMap<String, ?> realMap = (ConcurrentHashMap<String, ?>) realMapField.get(map);
                    keyset = realMap.keySet();
                } catch (Exception e) {
                }
                return keyset;
            }
        };
    }

    @Bean
    public JCacheCacheManager ehcacheManager() {
        EhcacheCachingProvider cachingProvider = (EhcacheCachingProvider) Caching.getCachingProvider();

        CacheManager cacheManager = (CacheManager) cachingProvider.getCacheManager();
        MutableConfiguration<String, Object> mutableConfiguration = new MutableConfiguration<>();
        mutableConfiguration.setTypes(String.class, Object.class);
        mutableConfiguration.setStoreByValue(false); // 默认值为 true，可根据需求调整
        mutableConfiguration.setManagementEnabled(true); // 启用管理功能（可选）
        mutableConfiguration.setStatisticsEnabled(true); // 启用统计功能（可选）
        // 设置缓存过期策略（以 timeToIdle 为例，根据实际需求调整）
        mutableConfiguration.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.HOURS, 1)));

        cacheManager.createCache("temp_cache", mutableConfiguration);
        cacheManager.createCache("eternal_cache", mutableConfiguration);
        cacheManager.createCache("sys_dict", mutableConfiguration);
        cacheManager.createCache("sys_config", mutableConfiguration);
        cacheManager.createCache("repeat_submit", mutableConfiguration);
        cacheManager.createCache("captcha_codes", mutableConfiguration);
        cacheManager.createCache("login_tokens", mutableConfiguration);
        cacheManager.createCache("rate_limit", mutableConfiguration);
        cacheManager.createCache("pwd_err_cnt", mutableConfiguration);

        JCacheCacheManager jCacheCacheManager = new JCacheCacheManager(cacheManager);

        return jCacheCacheManager;

    }
}
