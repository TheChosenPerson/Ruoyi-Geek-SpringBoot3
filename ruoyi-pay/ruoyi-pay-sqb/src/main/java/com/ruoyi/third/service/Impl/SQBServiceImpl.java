package org.jeecg.modules.sqb.service.impl;


import org.json.JSONObject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.net.URLDecoder;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
@PropertySource("classpath:sqbpay.properties")
@ConfigurationProperties(prefix = "sqbpay")
@Data
public class SQBServiceImpl {
    private String apiDomain;
    private String terminalSn;
    private String terminalKey;
    private String appId;
    private String vendorSn;
    private String vendorKey;
    private final static String CHARSET_UTF8 = "utf8";

    /**
     * 计算字符串的MD5值
     * 
     * @param signStr:签名字符串
     * @return
     */
    public String getSign(String signStr) {
        try {
            String md5 = MD5Util.encryptMd5(signStr);
            return md5;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 终端激活
     * 
     * @param code:激活码
     * @return {terminal_sn:"$终端号",terminal_key:"$终端密钥"}
     */
    public JSONObject activate(String code, String deviceId, String clientSn, String name) {
        String url = apiDomain + "/terminal/activate";
        JSONObject params = new JSONObject();
        try {
            params.put("app_id", appId); // app_id，必填
            params.put("code", code); // 激活码，必填
            params.put("device_id", deviceId); // 客户方收银终端序列号，需保证同一app_id下唯一，必填。为方便识别，建议格式为“品牌名+门店编号+‘POS’+POS编号“
            params.put("client_sn", clientSn); // 客户方终端编号，一般客户方或系统给收银终端的编号，必填
            params.put("name", name); // 客户方终端名称，必填

            String sign = getSign(params.toString() + vendorKey);
            System.out.println(params.toString() + vendorKey);
            String result = HttpUtil.httpPost(url, params.toString(), sign, vendorSn);
            JSONObject retObj = new JSONObject(result);
            String resCode = retObj.get("result_code").toString();
            if (!resCode.equals("200"))
                return null;
            String responseStr = retObj.get("biz_response").toString();
            JSONObject terminal = new JSONObject(responseStr);
            if (terminal.get("terminal_sn") == null || terminal.get("terminal_key") == null)
                return null;
            return terminal;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 终端签到
     * 
     * @return {terminal_sn:"$终端号",terminal_key:"$终端密钥"}
     */
    public JSONObject checkin() {
        String url = apiDomain + "/terminal/checkin";
        JSONObject params = new JSONObject();
        try {
            params.put("terminal_sn", terminalSn);
            params.put("device_id", "HUISUAN001POS01");
            params.put("os_info", "Mac OS");
            params.put("sdk_version", "Java SDK v1.0");
            String sign = getSign(params.toString() + terminalKey);
            String result = HttpUtil.httpPost(url, params.toString(), sign, terminalSn);
            JSONObject retObj = new JSONObject(result);
            String resCode = retObj.get("result_code").toString();
            if (!resCode.equals("200"))
                return null;
            String responseStr = retObj.get("biz_response").toString();
            JSONObject terminal = new JSONObject(responseStr);
            if (terminal.get("terminal_sn") == null || terminal.get("terminal_key") == null)
                return null;
            return terminal;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 退款
     * 
     * @return
     */
    public String refund(SageOrder orderPay) {
        String url = apiDomain + "/upay/v2/refund";
        JSONObject params = new JSONObject();
        try {
            params.put("terminal_sn", terminalSn); // 收钱吧终端ID
            params.put("client_sn", orderPay.getSn()); // 商户系统订单号,必须在商户系统内唯一；且长度不超过64字节
            params.put("refund_amount", orderPay.getTotalAmount()); // 退款金额
            params.put("refund_request_no", "2"); // 商户退款所需序列号,表明是第几次退款
            params.put("operator", "kay"); // 门店操作员

            String sign = getSign(params.toString() + terminalKey);
            String result = HttpUtil.httpPost(url, params.toString(), sign, terminalSn);

            return result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 查询
     * 
     * @return
     */
    public String query(SageOrder orderPay) {
        String url = apiDomain + "/upay/v2/query";
        JSONObject params = new JSONObject();
        try {
            params.put("terminal_sn", terminalSn); // 终端号
            params.put("client_sn", orderPay.getSn()); // 商户系统订单号,必须在商户系统内唯一；且长度不超过64字节
            System.out.println(params.toString() + terminalKey);
            String sign = getSign(params.toString() + terminalKey);
            String result = HttpUtil.httpPost(url, params.toString(), sign, terminalSn);
            JSONObject retObj = new JSONObject(result);
            String resCode = retObj.get("result_code").toString();
            if (!resCode.equals("200"))
                return null;
            String responseStr = retObj.get("biz_response").toString();
            return responseStr;
        } catch (Exception e) {
            return null;
        }
    }

    public String payUrl(SageOrder orderPay) throws UnsupportedEncodingException {
        String param = "" +
                "client_sn=" + orderPay.getSn() +
                "&operator=" + orderPay.getOperator() +
                "&return_url=" + "https://www.shouqianba.com/" +
                "&subject=" + orderPay.getSubject() +
                "&terminal_sn=" + terminalSn +
                "&total_amount=" + orderPay.getTotalAmount();
        String urlParam = "" +
                "client_sn=" + orderPay.getSn() +
                "&operator=" + URLEncoder.encode(orderPay.getOperator(), "UTF-8") +
                "&return_url=" + "https://www.shouqianba.com/" +
                "&subject=" + URLEncoder.encode(orderPay.getSubject(), "UTF-8") +
                "&terminal_sn=" + terminalSn +
                "&total_amount=" + orderPay.getTotalAmount();
        String sign = getSign(param + "&key=" + terminalKey);
        return "https://qr.shouqianba.com/gateway?" + urlParam + "&sign=" + sign.toUpperCase();
    }

    /**
     * 预下单
     * 
     * @return
     */
    public String precreate(SageOrder orderPay) {
        String url = apiDomain + "/upay/v2/precreate";
        JSONObject params = new JSONObject();
        try {
            params.put("terminal_sn", terminalSn); // 收钱吧终端ID
            params.put("client_sn", orderPay.getSn()); // 商户系统订单号,必须在商户系统内唯一；且长度不超过32字节
            params.put("total_amount", orderPay.getTotalAmount()); // 交易总金额
            params.put("payway", orderPay.getPayway()); // 支付方式
            params.put("subject", orderPay.getSubject()); // 交易简介
            params.put("operator", orderPay.getOperator()); // 门店操作员

            String sign = getSign(params.toString() + terminalKey);
            String result = HttpUtil.httpPost(url, params.toString(), sign, terminalSn);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 自动撤单
     * 
     * @param terminal_sn:终端号
     * @param terminal_key:终端密钥
     * @return
     */
    public String cancel(String terminal_sn, String terminal_key) {
        String url = apiDomain + "/upay/v2/cancel";
        JSONObject params = new JSONObject();
        try {
            params.put("terminal_sn", terminal_sn); // 终端号
            params.put("sn", "7892259488292938"); // 收钱吧系统内部唯一订单号
            params.put("client_sn", "18348290098298292838"); // 商户系统订单号,必须在商户系统内唯一；且长度不超过64字节

            String sign = getSign(params.toString() + terminal_key);
            String result = HttpUtil.httpPost(url, params.toString(), sign, terminal_sn);

            return result;
        } catch (Exception e) {
            return null;
        }
    }

}
