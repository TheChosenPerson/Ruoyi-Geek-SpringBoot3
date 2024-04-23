package com.ruoyi.common.utils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.ehcache.core.EhcacheBase;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.ehcache.impl.internal.store.heap.OnHeapStore;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.jcache.JCacheCache;
import org.springframework.cache.transaction.TransactionAwareCacheDecorator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import com.ruoyi.common.utils.spring.SpringUtils;

public class CacheUtils {
    /**
     * 使用redis时对redis进行单独特殊操作需要使用
     *
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> RedisTemplate<K, V> getRedisTemplate() {
        return SpringUtils.getBean("redisTemplate");
    }

    /**
     * 获取CacheManager
     *
     * @return
     */
    public static CacheManager getCacheManager() {
        return SpringUtils.getBean(CacheManager.class);
    }

    /**
     * 根据cacheName从CacheManager中获取cache
     *
     * @param cacheName
     * @return
     */
    public static Cache getCache(String cacheName) {
        return getCacheManager().getCache(cacheName);
    }

    /**
     * 获取缓存的所有key值(由于springcache不支持获取所有key,只能根据cache类型来单独获取)
     *
     * @param cacheName
     * @return
     */
    @SuppressWarnings(value = { "unchecked", "rawtypes" })
    public static Set<String> getkeys(String cacheName) {
        Cache cache = getCacheManager().getCache(cacheName);
        Set<String> keyset = new HashSet<>();
        if (cache instanceof JCacheCache) {
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

        } else if (cache instanceof TransactionAwareCacheDecorator) {
            Set<Object> keysets = getRedisTemplate().keys(cache.getName() + "*");
            for (Object s : keysets) {
                keyset.add(StringUtils.replace(s.toString(), cache.getName() + ":", ""));
            }
        }
        return keyset;
    }

    /**
     * 根据cacheName,key缓存数据
     *
     * @param cacheName
     * @param key
     * @param value
     * @param <T>
     */
    public static <T> void put(String cacheName, String key, T value) {
        put(cacheName, key, value, 0, null);
    }

    /**
     * 如果没有则进行缓存,根据cacheName,key缓存数据
     *
     * @param cacheName
     * @param key
     * @param value
     * @param <T>
     */
    public static <T> void putIfAbsent(String cacheName, String key, T value) {
        if (ObjectUtils.isEmpty(get(cacheName, key))) {
            put(cacheName, key, value, 0, null);
        }
    }

    /**
     * 根据cacheName,key和缓存过期时间进行缓存数据,使用各种不同缓存可以单独进行操作
     *
     * @param cacheName
     * @param key
     * @param value
     * @param timeout
     * @param unit
     * @param <T>
     */
    public static <T> void put(String cacheName, String key, T value, long timeout, TimeUnit unit) {
        Cache cache = getCacheManager().getCache(cacheName);
        if (cache instanceof JCacheCache) {
            JCacheCache ehcache = (JCacheCache) cache;
            ehcache.put(key, value);
        } else if (cache instanceof TransactionAwareCacheDecorator) {
            if (timeout != 0 && unit != null) {
                getRedisTemplate().opsForValue().set(cacheName + ":" + key, value, timeout, unit);
            } else {
                getRedisTemplate().opsForValue().set(cacheName + ":" + key, value);
            }
        } else {
            cache.put(key, value);
        }
    }

    /**
     * 获取数据
     *
     * @param cacheName
     * @param key
     * @return
     */
    public static Cache.ValueWrapper get(String cacheName, String key) {
        return getCacheManager().getCache(cacheName).get(key);
    }

    /**
     * 根据类型获取数据
     *
     * @param cacheName
     * @param key
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T get(String cacheName, String key, @Nullable Class<T> type) {
        return getCacheManager().getCache(cacheName).get(key, type);
    }

    /**
     * 移除缓存数据
     *
     * @param cacheName
     * @param key
     */
    public static void remove(String cacheName, String key) {
        getCacheManager().getCache(cacheName).evict(key);
    }

    /**
     * 如果存在则移除缓存数据
     *
     * @param cacheName
     * @param key
     * @return
     */
    public static boolean removeIfPresent(String cacheName, String key) {
        remove(cacheName, key);
        return false;
    }

    /**
     * 清除缓存名称为cacheName的所有缓存数据
     *
     * @param cacheName
     */
    public static void clear(String cacheName) {
        getCacheManager().getCache(cacheName).clear();
    }
}
