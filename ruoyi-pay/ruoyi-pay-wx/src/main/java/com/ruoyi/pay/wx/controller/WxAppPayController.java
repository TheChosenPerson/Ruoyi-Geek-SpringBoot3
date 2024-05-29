package com.ruoyi.pay.wx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pay.domain.PayOrder;
import com.ruoyi.pay.service.IPayOrderService;
import com.ruoyi.pay.wx.config.WxPayAppConfig;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;

import io.swagger.v3.oas.annotations.Operation;

/**
 * @author zlh
 */
@RestController
@RequestMapping("/wxPay")
public class WxAppPayController extends BaseController {
    @Autowired
    private WxPayAppConfig wxPayAppConfig;
    @Autowired
    private IPayOrderService payOrderService;
    @Anonymous
    @Operation(summary = "微信支付")
    @GetMapping("/pay/{orderNumber}")
    public AjaxResult pay(@PathVariable String orderNumber) {
        // 使用自动更新平台证书的RSA配置
        // 一个商户号只能初始化一个配置，否则会因为重复的下载任务报错
        Config config =
                new RSAAutoCertificateConfig.Builder()
                        .merchantId(wxPayAppConfig.getWxchantId())
                        .privateKeyFromPath(wxPayAppConfig.getWxcertPath())
                        .merchantSerialNumber(wxPayAppConfig.getWxchantSerialNumber())
                        .apiV3Key(wxPayAppConfig.getWxapiV3Key())
                        .build();
        // 构建service
        NativePayService service = new NativePayService.Builder().config(config).build();
        // request.setXxx(val)设置所需参数，具体参数可见Request定义
        PayOrder aliPay = payOrderService.selectPayOrderByOrderNumber(orderNumber);
        String amountStr = aliPay.getTotalAmount();
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
        // 调用下单方法，得到应答
        PrepayResponse response = service.prepay(request);
        // 使用微信扫描 code_url 对应的二维码，即可体验Native支付
        return AjaxResult.success(response.getCodeUrl());
    }

//    @Anonymous
//    @Operation(summary = "微信支付查询订单")
//    @GetMapping("/notify")
//    public AjaxResult WxPayList() {
//        System.out.println("=========支付宝异步回调========");
//        // 构造 RequestParam
//        RequestParam requestParam = new RequestParam.Builder()
//                .serialNumber("wechatPaySerial")
//                .nonce("wechatpayNonce")
//                .signature("wechatSignature")
//                .timestamp("wechatTimestamp")
//                .body("requestBody")
//                .build();
//
//// 如果已经初始化了 RSAAutoCertificateConfig，可直接使用
//// 没有的话，则构造一个
//        NotificationConfig config = new RSAAutoCertificateConfig.Builder()
//                .merchantId(merchantId)
//                .privateKeyFromPath(privateKeyPath)
//                .merchantSerialNumber(merchantSerialNumber)
//                .apiV3Key(apiV3Key)
//                .build();
//
//// 初始化 NotificationParser
//        NotificationParser parser = new NotificationParser(config);
//
//        try {
//            // 以支付通知回调为例，验签、解密并转换成 Transaction
//            Transaction transaction = parser.parse(requestParam, Transaction.class);
//        } catch (ValidationException e) {
//            // 签名验证失败，返回 401 UNAUTHORIZED 状态码
//            logger.error("sign verification failed", e);
//            return AjaxResult.success(HttpStatus.UNAUTHORIZED);
//        }
//// 处理成功，返回 200 OK 状态码
//        return AjaxResult.success(HttpStatus.OK);
//    }

}