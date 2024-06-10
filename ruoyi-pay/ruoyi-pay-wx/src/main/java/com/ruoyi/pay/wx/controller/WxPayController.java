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
import com.ruoyi.common.core.domain.R;
import com.ruoyi.pay.domain.PayOrder;
import com.ruoyi.pay.service.IPayOrderService;
import com.ruoyi.pay.wx.config.WxPayConfig;
import com.ruoyi.pay.wx.service.IWxPayService;
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

/**
 * @author zlh
 */
@RestController
@ConditionalOnProperty(prefix = "pay.wechat", name = "enabled", havingValue = "true")
@RequestMapping("/pay/wechat")
public class WxPayController extends BaseController {
    @Autowired
    private WxPayConfig wxPayAppConfig;

    @Autowired(required = false)
    private IWxPayService wxPayService;

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
    @GetMapping("/url/{orderNumber}")
    public R<String> url(@PathVariable(name = "orderNumber") String orderNumber) throws Exception {
        PayOrder aliPay = payOrderService.selectPayOrderByOrderNumber(orderNumber);
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(Integer.parseInt(aliPay.getActualAmount()));
        request.setAmount(amount);
        request.setAppid(wxPayAppConfig.getAppId());
        request.setMchid(wxPayAppConfig.getWxchantId());
        request.setDescription(aliPay.getOrderContent());
        request.setNotifyUrl(wxPayAppConfig.getNotifyUrl());
        request.setOutTradeNo(aliPay.getOrderNumber());
        PrepayResponse response = nativePayService.prepay(request);
        return R.ok(response.getCodeUrl());
    }

    @Anonymous
    @Operation(summary = "微信支付查询订单")
    @PostMapping("/notify")
    public AjaxResult notify(HttpServletRequest servletRequest, HttpServletResponse response)
            throws Exception {
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
                if (wxPayService != null) {
                    wxPayService.callback(transaction);
                }
                return success();
            } catch (ValidationException e) {
                return error();
            }
        } catch (IOException e) {
            return error(e.getMessage());
        }
    }

}