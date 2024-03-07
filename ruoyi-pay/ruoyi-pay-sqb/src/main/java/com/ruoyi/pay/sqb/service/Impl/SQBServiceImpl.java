package com.ruoyi.pay.sqb.service.Impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.sign.Md5Utils;
import com.ruoyi.pay.domain.PayOrder;
import com.ruoyi.pay.sqb.constant.SqbConstant;
import com.ruoyi.pay.sqb.utils.HttpUtil;

@Service
public class SQBServiceImpl {
    @Autowired
    private SqbConstant sqbConstant;

    /**
     * 计算字符串的MD5值
     * 
     * @param signStr:签名字符串
     * @return
     */
    private String getSign(String signStr) {
        try {
            String md5 = Md5Utils.encryptMd5(signStr);
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
        String url = sqbConstant.getApiDomain() + "/terminal/activate";
        JSONObject params = new JSONObject();
        try {
            params.put("app_id", sqbConstant.getAppId()); // app_id，必填
            params.put("code", code); // 激活码，必填
            params.put("device_id", deviceId); // 客户方收银终端序列号，需保证同一app_id下唯一，必填。为方便识别，建议格式为“品牌名+门店编号+‘POS’+POS编号“
            params.put("client_sn", clientSn); // 客户方终端编号，一般客户方或系统给收银终端的编号，必填
            params.put("name", name); // 客户方终端名称，必填

            String sign = getSign(params.toString() + sqbConstant.getVendorKey());
            String result = HttpUtil.httpPost(url, params.toString(), sign, sqbConstant.getVendorSn());
            JSONObject retObj = JSON.parseObject(result);
            String resCode = retObj.get("result_code").toString();
            if (!resCode.equals("200"))
                return null;
            String responseStr = retObj.get("biz_response").toString();
            JSONObject terminal = JSON.parseObject(responseStr);
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
        String url = sqbConstant.getApiDomain() + "/terminal/checkin";
        JSONObject params = new JSONObject();
        try {
            params.put("terminal_sn", sqbConstant.getTerminalSn());
            params.put("device_id", "HUISUAN001POS01");
            params.put("os_info", "Mac OS");
            params.put("sdk_version", "Java SDK v1.0");
            String sign = getSign(params.toString() + sqbConstant.getTerminalKey());
            String result = HttpUtil.httpPost(url, params.toString(), sign, sqbConstant.getTerminalSn());
            JSONObject retObj = JSON.parseObject(result);
            String resCode = retObj.get("result_code").toString();
            if (!resCode.equals("200"))
                return null;
            String responseStr = retObj.get("biz_response").toString();
            JSONObject terminal = JSON.parseObject(responseStr);
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
    public String refund(PayOrder payOrder) {
        String url = sqbConstant.getApiDomain() + "/upay/v2/refund";
        JSONObject params = new JSONObject();
        try {
            params.put("terminal_sn", sqbConstant.getTerminalSn()); // 收钱吧终端ID
            params.put("client_sn", payOrder.getOrderNumber()); // 商户系统订单号,必须在商户系统内唯一；且长度不超过64字节
            params.put("refund_amount", payOrder.getTotalAmount()); // 退款金额
            params.put("refund_request_no", "1"); // 商户退款所需序列号,表明是第几次退款
            params.put("operator", "kay"); // 门店操作员

            String sign = getSign(params.toString() + sqbConstant.getTerminalKey());
            String result = HttpUtil.httpPost(url, params.toString(), sign, sqbConstant.getTerminalSn());

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

    public JSONObject query(PayOrder payOrder) {
        String url = sqbConstant.getApiDomain() + "/upay/v2/query";
        JSONObject params = new JSONObject();
        try {
            params.put("terminal_sn", sqbConstant.getTerminalSn()); // 终端号
            params.put("client_sn", payOrder.getOrderNumber()); // 商户系统订单号,必须在商户系统内唯一；且长度不超过64字节
            System.out.println(params.toString() + sqbConstant.getTerminalKey());
            String sign = getSign(params.toString() + sqbConstant.getTerminalKey());
            String result = HttpUtil.httpPost(url, params.toString(), sign, sqbConstant.getTerminalSn());
            JSONObject retObj = JSON.parseObject(result);
            String resCode = retObj.get("result_code").toString();
            if (!resCode.equals("200"))
                return null;
            String responseStr = retObj.get("biz_response").toString();
            return JSONObject.parseObject(responseStr);
        } catch (Exception e) {
            return null;
        }
    }

    public String payUrl(PayOrder payOrder) throws UnsupportedEncodingException {
        if(payOrder.getRemark() == null){
            payOrder.setRemark("支付");
        }
        String param = "" +
                "client_sn=" + payOrder.getOrderNumber() +
                "&operator=" + "admin" +
                "&return_url=" + "https://www.shouqianba.com/" +
                "&subject=" + payOrder.getRemark() +
                "&terminal_sn=" + sqbConstant.getTerminalSn() +
                "&total_amount=" + payOrder.getTotalAmount();
        String urlParam = "" +
                "client_sn=" + payOrder.getOrderNumber() +
                "&operator=" + URLEncoder.encode("admin", "UTF-8") +
                "&return_url=" + "https://www.shouqianba.com/" +
                "&subject=" + URLEncoder.encode(payOrder.getRemark(), "UTF-8") +
                "&terminal_sn=" + sqbConstant.getTerminalSn() +
                "&total_amount=" + payOrder.getTotalAmount();
        String sign = getSign(param + "&key=" + sqbConstant.getTerminalKey());
        return "https://qr.shouqianba.com/gateway?" + urlParam + "&sign=" + sign.toUpperCase();
    }

    /**
     * 预下单
     * 
     * @return
     */
    public String precreate(PayOrder payOrder,String sn, String payway) {
        String url = sqbConstant.getApiDomain() + "/upay/v2/precreate";
        JSONObject params = new JSONObject();
        try {
            params.put("terminal_sn", sqbConstant.getTerminalSn()); // 收钱吧终端ID
            params.put("client_sn", payOrder.getOrderNumber()); // 商户系统订单号,必须在商户系统内唯一；且长度不超过32字节
            params.put("total_amount", payOrder.getTotalAmount()); // 交易总金额
            params.put("payway", payway); // 支付方式
            params.put("subject", "无简介"); // 交易简介
            params.put("operator", "admin"); // 门店操作员

            String sign = getSign(params.toString() + sqbConstant.getTerminalKey());
            String result = HttpUtil.httpPost(url, params.toString(), sign, sqbConstant.getTerminalSn());
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
        String url = sqbConstant.getApiDomain() + "/upay/v2/cancel";
        JSONObject params = new JSONObject();
        try {
            params.put("terminal_sn", terminal_sn); // 终端号
            params.put("client_sn", "18348290098298292838"); // 商户系统订单号,必须在商户系统内唯一；且长度不超过64字节

            String sign = getSign(params.toString() + terminal_key);
            String result = HttpUtil.httpPost(url, params.toString(), sign, terminal_sn);

            return result;
        } catch (Exception e) {
            return null;
        }
    }

}
