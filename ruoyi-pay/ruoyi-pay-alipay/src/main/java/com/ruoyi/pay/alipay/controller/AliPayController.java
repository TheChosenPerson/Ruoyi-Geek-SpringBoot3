package com.ruoyi.pay.alipay.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.pay.alipay.config.AliPayConfig;
import com.ruoyi.pay.domain.PayOrder;
import com.ruoyi.pay.mapper.PayOrderMapper;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/alipay")
public class AliPayController {

    @Autowired
    private PayOrderMapper payOrderMapper;

    @Autowired
    private AliPayConfig aliPayConfig;

    @GetMapping("/init")
    public String init() {
        aliPayConfig.init();
        return "success";
    }

    @Anonymous
    @GetMapping("/pay")
    public String pay(PayOrder payOrder) {
        AlipayTradePagePayResponse response;
        try {
            // 发起API调用（以创建当面付收款二维码为例）
            response = Factory.Payment.Page()
                    .pay(payOrder.getOrderContent(), payOrder.getOrderNumber(), payOrder.getTotalAmount(), "");
        } catch (Exception e) {
            System.err.println("调用遭遇异常，原因：" + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
        return response.getBody();
    }

    @PostMapping("/notify") // 注意这里必须是POST接口
    public String payNotify(HttpServletRequest request) throws Exception {
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            System.out.println("=========支付宝异步回调========");

            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
                // System.out.println(name + " = " + request.getParameter(name));
            }

            String orderNumber = params.get("out_trade_no");
            // 支付宝验签
            if (Factory.Payment.Common().verifyNotify(params)) {
                // 验签通过
                System.out.println("交易名称: " + params.get("subject"));
                System.out.println("交易状态: " + params.get("trade_status"));
                System.out.println("支付宝交易凭证号: " + params.get("trade_no"));
                System.out.println("商户订单号: " + params.get("out_trade_no"));
                System.out.println("交易金额: " + params.get("total_amount"));
                System.out.println("买家在支付宝唯一id: " + params.get("buyer_id"));
                System.out.println("买家付款时间: " + params.get("gmt_payment"));
                System.out.println("买家付款金额: " + params.get("buyer_pay_amount"));

                PayOrder payOrder = payOrderMapper.selectPayOrderByOrderNumber(orderNumber);
                payOrder.setOrderStatus("已支付");
                payOrderMapper.updatePayOrder(payOrder);
            }
        }
        return "success";
    }
}
