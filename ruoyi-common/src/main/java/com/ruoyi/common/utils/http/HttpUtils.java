package com.ruoyi.common.utils.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.StringUtils;

/**
 * 通用http发送方法
 * 
 * @author ruoyi
 */
public class HttpUtils
{
    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    public static RequestConfig requestConfig;

    private static CloseableHttpClient httpClient;

    private static PoolingHttpClientConnectionManager connMgr;

    private static IdleConnectionMonitorThread idleThread;

    static
    {
        HttpUtils.initClient();
    }

    /**
     * 向指定 URL 发送GET方法的请求
     *
     * @param url 发送请求的 URL
     * @return 所代表远程资源的响应结果
     */
    public static String sendGet(String url)
    {
        return sendGet(url, StringUtils.EMPTY);
    }

    /**
     * 向指定 URL 发送GET方法的请求
     *
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param)
    {
        return sendGet(url, param, Constants.UTF8);
    }

    /**
     * 向指定 URL 发送GET方法的请求
     *
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @param contentType 编码类型
     * @return 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param, String contentType)
    {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try
        {
            String urlNameString = StringUtils.isNotBlank(param) ? url + "?" + param : url;
            log.info("sendGet - {}", urlNameString);
            URI uri = new URI(urlNameString);
            URL realUrl = uri.toURL();
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), contentType));
            String line;
            while ((line = in.readLine()) != null)
            {
                result.append(line);
            }
            log.info("recv - {}", result);
        }
        catch (ConnectException e)
        {
            log.error("调用HttpUtils.sendGet ConnectException, url=" + url + ",param=" + param, e);
        }
        catch (SocketTimeoutException e)
        {
            log.error("调用HttpUtils.sendGet SocketTimeoutException, url=" + url + ",param=" + param, e);
        }
        catch (IOException e)
        {
            log.error("调用HttpUtils.sendGet IOException, url=" + url + ",param=" + param, e);
        }
        catch (Exception e)
        {
            log.error("调用HttpsUtil.sendGet Exception, url=" + url + ",param=" + param, e);
        }
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch (Exception ex)
            {
                log.error("调用in.close Exception, url=" + url + ",param=" + param, ex);
            }
        }
        return result.toString();
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param)
    {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try
        {
            log.info("sendPost - {}", url);
            URI uri = new URI(url);
            URL realUrl = uri.toURL();
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("contentType", "utf-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null)
            {
                result.append(line);
            }
            log.info("recv - {}", result);
        }
        catch (ConnectException e)
        {
            log.error("调用HttpUtils.sendPost ConnectException, url=" + url + ",param=" + param, e);
        }
        catch (SocketTimeoutException e)
        {
            log.error("调用HttpUtils.sendPost SocketTimeoutException, url=" + url + ",param=" + param, e);
        }
        catch (IOException e)
        {
            log.error("调用HttpUtils.sendPost IOException, url=" + url + ",param=" + param, e);
        }
        catch (Exception e)
        {
            log.error("调用HttpsUtil.sendPost Exception, url=" + url + ",param=" + param, e);
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException ex)
            {
                log.error("调用in.close Exception, url=" + url + ",param=" + param, ex);
            }
        }
        return result.toString();
    }

    public static String sendSSLPost(String url, String param)
    {
        StringBuilder result = new StringBuilder();
        String urlNameString = url + "?" + param;
        try
        {
            log.info("sendSSLPost - {}", urlNameString);
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());
            URI uri = new URI(urlNameString);
            URL console = uri.toURL();
            HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("contentType", "utf-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String ret = "";
            while ((ret = br.readLine()) != null)
            {
                if (ret != null && !ret.trim().equals(""))
                {
                    result.append(new String(ret.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                }
            }
            log.info("recv - {}", result);
            conn.disconnect();
            br.close();
        }
        catch (ConnectException e)
        {
            log.error("调用HttpUtils.sendSSLPost ConnectException, url=" + url + ",param=" + param, e);
        }
        catch (SocketTimeoutException e)
        {
            log.error("调用HttpUtils.sendSSLPost SocketTimeoutException, url=" + url + ",param=" + param, e);
        }
        catch (IOException e)
        {
            log.error("调用HttpUtils.sendSSLPost IOException, url=" + url + ",param=" + param, e);
        }
        catch (Exception e)
        {
            log.error("调用HttpsUtil.sendSSLPost Exception, url=" + url + ",param=" + param, e);
        }
        return result.toString();
    }

    private static class TrustAnyTrustManager implements X509TrustManager
    {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
        {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
        {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers()
        {
            return new X509Certificate[] {};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier
    {
        @Override
        public boolean verify(String hostname, SSLSession session)
        {
            return true;
        }
    }

    /**
     * 获取httpClient
     * 
     * @return
     */
    public static CloseableHttpClient getHttpClient()
    {
        if (httpClient != null)
        {
            return httpClient;
        }
        else
        {
            return HttpClients.createDefault();
        }
    }

    /**
     * 创建连接池管理器
     * 
     * @return
     */
    private static PoolingHttpClientConnectionManager createConnectionManager()
    {

        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager();
        // 将最大连接数增加到
        connMgr.setMaxTotal(HttpConf.MAX_TOTAL_CONN);
        // 将每个路由基础的连接增加到
        connMgr.setDefaultMaxPerRoute(HttpConf.MAX_ROUTE_CONN);

        return connMgr;
    }

    /**
     * 根据当前配置创建HTTP请求配置参数。
     * 
     * @return 返回HTTP请求配置。
     */
    private static RequestConfig createRequestConfig()
    {
        Builder builder = RequestConfig.custom();
        builder.setConnectionRequestTimeout(StringUtils.nvl(HttpConf.WAIT_TIMEOUT, 10000));
        builder.setConnectTimeout(StringUtils.nvl(HttpConf.CONNECT_TIMEOUT, 10000));
        builder.setSocketTimeout(StringUtils.nvl(HttpConf.SO_TIMEOUT, 60000));
        return builder.build();
    }

    /**
     * 创建默认的HTTPS客户端，信任所有的证书。
     * 
     * @return 返回HTTPS客户端，如果创建失败，返回HTTP客户端。
     */
    private static CloseableHttpClient createHttpClient(HttpClientConnectionManager connMgr)
    {
        try
        {
            final SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy()
            {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException
                {
                    // 信任所有
                    return true;
                }
            }).build();
            final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);

            // 重试机制
            HttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(HttpConf.RETRY_COUNT, true);
            ConnectionKeepAliveStrategy connectionKeepAliveStrategy = new ConnectionKeepAliveStrategy()
            {
                @Override
                public long getKeepAliveDuration(HttpResponse httpResponse, HttpContext httpContext)
                {
                    return HttpConf.KEEP_ALIVE_TIMEOUT; // tomcat默认keepAliveTimeout为20s
                }
            };
            httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).setConnectionManager(connMgr)
                    .setDefaultRequestConfig(requestConfig).setRetryHandler(retryHandler)
                    .setKeepAliveStrategy(connectionKeepAliveStrategy).build();
        }
        catch (Exception e)
        {
            log.error("Create http client failed", e);
            httpClient = HttpClients.createDefault();
        }

        return httpClient;
    }

    /**
     * 初始化 只需调用一次
     */
    public synchronized static CloseableHttpClient initClient()
    {
        if (httpClient == null)
        {
            connMgr = createConnectionManager();
            requestConfig = createRequestConfig();
            // 初始化httpClient连接池
            httpClient = createHttpClient(connMgr);
            // 清理连接池
            idleThread = new IdleConnectionMonitorThread(connMgr);
            idleThread.start();
        }

        return httpClient;
    }

    /**
     * 关闭HTTP客户端。
     * 
     * @param httpClient HTTP客户端。
     */
    public synchronized static void shutdown()
    {
        try
        {
            if (idleThread != null)
            {
                idleThread.shutdown();
                idleThread = null;
            }
        }
        catch (Exception e)
        {
            log.error("httpclient connection manager close", e);
        }

        try
        {
            if (httpClient != null)
            {
                httpClient.close();
                httpClient = null;
            }
        }
        catch (IOException e)
        {
            log.error("httpclient close", e);
        }
    }

    /**
     * 请求上游 GET提交
     * 
     * @param uri
     * @throws IOException
     */
    public static String getCall(final String uri) throws Exception
    {

        return getCall(uri, null, Constants.UTF8);
    }

    /**
     * 请求上游 GET提交
     * 
     * @param uri
     * @param contentType
     * @throws IOException
     */
    public static String getCall(final String uri, String contentType) throws Exception
    {

        return getCall(uri, contentType, Constants.UTF8);
    }

    /**
     * 请求上游 GET提交
     * 
     * @param uri
     * @param contentType
     * @param charsetName
     * @throws IOException
     */
    public static String getCall(final String uri, String contentType, String charsetName) throws Exception
    {

        final String url = uri;
        final HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        if (!StringUtils.isEmpty(contentType))
        {
            httpGet.addHeader("Content-Type", contentType);
        }
        final CloseableHttpResponse httpRsp = getHttpClient().execute(httpGet);
        try
        {
            if (httpRsp.getStatusLine().getStatusCode() == HttpStatus.SC_OK
                    || httpRsp.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN)
            {
                final HttpEntity entity = httpRsp.getEntity();
                final String rspText = EntityUtils.toString(entity, charsetName);
                EntityUtils.consume(entity);
                return rspText;
            }
            else
            {
                throw new IOException("HTTP StatusCode=" + httpRsp.getStatusLine().getStatusCode());
            }
        }
        finally
        {
            try
            {
                httpRsp.close();
            }
            catch (Exception e)
            {
                log.error("关闭httpRsp异常", e);
            }
        }
    }

    /**
     * 请求上游 POST提交
     * 
     * @param uri
     * @param paramsMap
     * @throws IOException
     */
    public static String postCall(final String uri, Map<String, Object> paramsMap) throws Exception
    {
        return postCall(uri, null, paramsMap, Constants.UTF8);
    }

    /**
     * 请求上游 POST提交
     * 
     * @param uri
     * @param contentType
     * @param paramsMap
     * @throws IOException
     */
    public static String postCall(final String uri, String contentType, Map<String, Object> paramsMap) throws Exception
    {

        return postCall(uri, contentType, paramsMap, Constants.UTF8);
    }

    /**
     * 请求上游 POST提交
     * 
     * @param uri
     * @param contentType
     * @param paramsMap
     * @param charsetName
     * @throws IOException
     */
    public static String postCall(final String uri, String contentType, Map<String, Object> paramsMap,
            String charsetName) throws Exception
    {

        final String url = uri;
        final HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        if (!StringUtils.isEmpty(contentType))
        {
            httpPost.addHeader("Content-Type", contentType);
        }
        // 添加参数
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        if (paramsMap != null)
        {
            for (Map.Entry<String, Object> entry : paramsMap.entrySet())
            {
                list.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
            }
        }
        httpPost.setEntity(new UrlEncodedFormEntity(list, charsetName));

        final CloseableHttpResponse httpRsp = getHttpClient().execute(httpPost);

        try
        {
            if (httpRsp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                final HttpEntity entity = httpRsp.getEntity();
                final String rspText = EntityUtils.toString(entity, charsetName);
                EntityUtils.consume(entity);
                return rspText;
            }
            else
            {
                throw new IOException("HTTP StatusCode=" + httpRsp.getStatusLine().getStatusCode());
            }
        }
        finally
        {
            try
            {
                httpRsp.close();
            }
            catch (Exception e)
            {
                log.error("关闭httpRsp异常", e);
            }
        }
    }

    /**
     * 请求上游 POST提交
     * 
     * @param uri
     * @param param
     * @throws IOException
     */
    public static String postCall(final String uri, String param) throws Exception
    {

        return postCall(uri, null, param, Constants.UTF8);
    }

    /**
     * 请求上游 POST提交
     * 
     * @param uri
     * @param contentType
     * @param param
     * @throws IOException
     */
    public static String postCall(final String uri, String contentType, String param) throws Exception
    {

        return postCall(uri, contentType, param, Constants.UTF8);
    }

    /**
     * 请求上游 POST提交
     * 
     * @param uri
     * @param contentType
     * @param param
     * @param charsetName
     * @throws IOException
     */
    public static String postCall(final String uri, String contentType, String param, String charsetName)
            throws Exception
    {

        final String url = uri;
        final HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        if (!StringUtils.isEmpty(contentType))
        {
            httpPost.addHeader("Content-Type", contentType);
        }
        else
        {
            httpPost.addHeader("Content-Type", "application/json");
        }
        // 添加参数
        StringEntity paramEntity = new StringEntity(param, charsetName);
        httpPost.setEntity(paramEntity);

        final CloseableHttpResponse httpRsp = getHttpClient().execute(httpPost);

        try
        {
            if (httpRsp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                final HttpEntity entity = httpRsp.getEntity();
                final String rspText = EntityUtils.toString(entity, charsetName);
                EntityUtils.consume(entity);
                return rspText;
            }
            else
            {
                throw new IOException("HTTP StatusCode=" + httpRsp.getStatusLine().getStatusCode());
            }
        }
        finally
        {
            try
            {
                httpRsp.close();
            }
            catch (Exception e)
            {
                log.error("关闭httpRsp异常", e);
            }
        }
    }

    /**
     * 判断HTTP异常是否为读取超时。
     * 
     * @param e 异常对象。
     * @return 如果是读取引起的异常（而非连接），则返回true；否则返回false。
     */
    public static boolean isReadTimeout(final Throwable e)
    {
        return (!isCausedBy(e, ConnectTimeoutException.class) && isCausedBy(e, SocketTimeoutException.class));
    }

    /**
     * 检测异常e被触发的原因是不是因为异常cause。检测被封装的异常。
     * 
     * @param e 捕获的异常。
     * @param cause 异常触发原因。
     * @return 如果异常e是由cause类异常触发，则返回true；否则返回false。
     */
    public static boolean isCausedBy(final Throwable e, final Class<? extends Throwable> cause)
    {
        if (cause.isAssignableFrom(e.getClass()))
        {
            return true;
        }
        else
        {
            Throwable t = e.getCause();
            while (t != null && t != e)
            {
                if (cause.isAssignableFrom(t.getClass()))
                {
                    return true;
                }
                t = t.getCause();
            }
            return false;
        }
    }
}