package com.ruoyi.pay.wx.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pay.domain.PayOrder;
import com.ruoyi.pay.service.IPayOrderService;
import com.ruoyi.pay.wx.config.WxPayConfig;
import com.wechat.pay.java.core.exception.ValidationException;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import com.wechat.pay.java.service.wexinpayscoreparking.model.Transaction;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zlh
 */
@Slf4j
@RestController
@ConditionalOnProperty(prefix = "pay.wechat", name = "enabled", havingValue = "true")
@RequestMapping("/wxPay")
public class WxPayController extends BaseController {
    @Autowired
    private WxPayConfig wxPayAppConfig;
    @Autowired
    private IPayOrderService payOrderService;

    @Autowired
    private NativePayService nativePayService;
    @Autowired
    private NotificationParser notificationParser;

    @Operation(summary = "微信支付")
    @Parameters({
            @Parameter(name = "orderNumber", description = "订单号", required = true)
    })
    @GetMapping("/pay/{orderNumber}")
    public AjaxResult pay(@PathVariable String orderNumber) throws Exception {
        PayOrder aliPay = payOrderService.selectPayOrderByOrderNumber(orderNumber);
        String amountStr = aliPay.getActualAmount();
        double amountDouble = Double.parseDouble(amountStr);
        int totalAmountInt = (int) (amountDouble * 100);
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(totalAmountInt);
        request.setAmount(amount);
        request.setAppid(wxPayAppConfig.getAppId());
        request.setMchid(wxPayAppConfig.getWxchantId());
        request.setDescription(aliPay.getOrderContent());
        request.setNotifyUrl(wxPayAppConfig.getNotifyUrl());
        request.setOutTradeNo(aliPay.getOrderNumber());
        PrepayResponse response = nativePayService.prepay(request);
        return AjaxResult.success(response.getCodeUrl());
    }

    @Anonymous
    @Operation(summary = "微信支付查询订单")
    @PostMapping("/notify")
    public AjaxResult WxPayList(HttpServletRequest servletRequest, HttpServletResponse response)
            throws Exception {
        log.info("=========微信异步回调========");

        String timeStamp = servletRequest.getHeader("Wechatpay-Timestamp");
        String nonce = servletRequest.getHeader("Wechatpay-Nonce");
        String signature = servletRequest.getHeader("Wechatpay-Signature");
        String certSn = servletRequest.getHeader("Wechatpay-Serial");

        try {
            String requestBody = StreamUtils.copyToString(servletRequest.getInputStream(), StandardCharsets.UTF_8);
            RequestParam requestParam = new RequestParam.Builder()
                    .serialNumber(certSn)
                    .nonce(nonce)
                    .signature(signature)
                    .timestamp(timeStamp)
                    .body(requestBody)
                    .build();

            try {
                Transaction transaction = notificationParser.parse(requestParam, Transaction.class);
                String orderNumber = transaction.getOutTradeNo();
                payOrderService.updateStatus(orderNumber, "已支付");
                return success();
            } catch (ValidationException e) {
                return error();
            }
        } catch (IOException e) {
            log.error("Error reading request body", e);
            throw new RuntimeException("Error reading request body", e);
        }
    }

}