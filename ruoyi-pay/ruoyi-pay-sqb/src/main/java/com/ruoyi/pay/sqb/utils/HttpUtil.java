package com.ruoyi.pay.sqb.utils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

public class HttpUtil {
    public static String httpPostWithoutException(String url, String string,String sign,String sn) {
        String xmlRes = "{}";
        try {
            xmlRes = httpPost(url, string,sign,sn);
        } catch (UnrecoverableKeyException e) {

        } catch (NoSuchAlgorithmException e) {

        } catch (KeyStoreException e) {

        } catch (KeyManagementException e) {

        }
        return xmlRes;
    }
    /**
     * http POST 请求
     * @param  url:请求地址
     * @param  body: body实体字符串
     * @param  sign:签名
     * @param  sn: 序列号
     * @return
     */
    public static String httpPost(String url, String body,String sign,String sn) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String xmlRes = "{}";
        HttpClient client = createSSLClientDefault();
        HttpPost httpost = new HttpPost(url);
        try {
            System.out.println("Request string: " + body);
            //所有请求的body都需采用UTF-8编码
            StringEntity entity = new StringEntity(body,"UTF-8");//
            entity.setContentType("application/json");
            httpost.setEntity(entity);

            //支付平台所有的API仅支持JSON格式的请求调用，HTTP请求头Content-Type设为application/json
            httpost.addHeader("Content-Type","application/json");

            //支付平台所有的API调用都需要签名验证,签名首部: Authorization: sn + " " + sign
            httpost.addHeader("Authorization",sn + " " + sign);
            System.out.println("Authorization" + sn + " " + sign);
            HttpResponse response = client.execute(httpost);

            //所有响应也采用UTF-8编码
            xmlRes = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println("Response string: " + xmlRes);
        } catch (ClientProtocolException e) {

        } catch (IOException e) {

        }
        return xmlRes;
    }

    public static CloseableHttpClient createSSLClientDefault() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                //信任所有
                public boolean isTrusted(X509Certificate[] chain,
                                         String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException e) {

        } catch (NoSuchAlgorithmException e) {

        } catch (KeyStoreException e) {

        }
        return HttpClients.createDefault();
    }

    public static String doGet(String url,String parameter)
    {
        String uriAPI =url+"?"+parameter ; //"http://XXXXX?str=I+am+get+String";
        String result= "";
        HttpClient client = createSSLClientDefault();
        HttpGet httpRequst = new HttpGet(uriAPI);
        try {

            HttpResponse httpResponse = client.execute(httpRequst);//其中HttpGet是HttpUriRequst的子类
            if(httpResponse.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity httpEntity = httpResponse.getEntity();
                result = EntityUtils.toString(httpEntity);//取出应答字符串
                // 一般来说都要删除多余的字符
                result.replaceAll("\r", "");//去掉返回结果中的"\r"字符，否则会在结果字符串后面显示一个小方格
            }
            else
                httpRequst.abort();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result = e.getMessage().toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result = e.getMessage().toString();
        }
        return result;
    }

}
