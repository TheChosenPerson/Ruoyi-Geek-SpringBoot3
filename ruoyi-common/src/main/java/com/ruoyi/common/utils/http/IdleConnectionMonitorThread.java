package com.ruoyi.common.utils.http;

import java.util.concurrent.TimeUnit;

import org.apache.http.conn.HttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 连接池清理
 * 
 * @author ruoyi
 */
public class IdleConnectionMonitorThread extends Thread
{
    private static final Logger log = LoggerFactory.getLogger(IdleConnectionMonitorThread.class);

    private final HttpClientConnectionManager connMgr;

    private volatile boolean shutdown;

    public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr)
    {
        super();
        this.shutdown = false;
        this.connMgr = connMgr;
    }

    @Override
    public void run()
    {
        while (!shutdown)
        {
            try
            {
                synchronized (this)
                {
                    // 每5秒检查一次关闭连接
                    wait(HttpConf.KEEP_ALIVE_TIMEOUT / 4);
                    // 关闭失效的连接
                    connMgr.closeExpiredConnections();
                    // 可选的, 关闭20秒内不活动的连接
                    connMgr.closeIdleConnections(HttpConf.KEEP_ALIVE_TIMEOUT, TimeUnit.MILLISECONDS);
                    // log.debug("关闭失效的连接");
                }
            }
            catch (Exception e)
            {
                log.error("关闭失效连接异常", e);
            }
        }
    }

    public void shutdown()
    {
        shutdown = true;
        if (connMgr != null)
        {
            try
            {
                connMgr.shutdown();
            }
            catch (Exception e)
            {
                log.error("连接池异常", e);
            }
        }
        synchronized (this)
        {
            notifyAll();
        }
    }
}