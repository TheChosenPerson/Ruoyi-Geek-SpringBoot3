package com.ruoyi.pay.sqb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.pay.domain.PayOrder;
import com.ruoyi.pay.service.IPayOrderService;
import com.ruoyi.pay.sqb.service.Impl.SQBServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "sqb支付")
@RestController
@RequestMapping("/pay/sql")
public class SQBController extends BaseController {
    @Autowired
    private SQBServiceImpl sqbServiceImpl;
    @Autowired
    private IPayOrderService payOrderServicer;

    @Operation(summary = "获取支付url")
    @Parameters(value = {
            @Parameter(name = "orderNumber", description = "订单号", required = true)
    })
    @PostMapping("/payUrl")
    @Anonymous
    public R<String> payUrl(@RequestParam("orderNumber") String orderNumber) throws Exception {
        PayOrder payOrder = payOrderServicer.selectPayOrderByOrderNumber(orderNumber);
        String url = sqbServiceImpl.payUrl(payOrder);
        return R.ok(url);
    }

    @Operation(summary = "查询支付状态")
    @Parameters(value = {
        @Parameter(name = "orderNumber", description = "订单号", required = true)
})
    @PostMapping("/query")
    @Anonymous
    public AjaxResult query(@RequestParam("orderNumber") String orderNumber) throws Exception {
        PayOrder payOrder = payOrderServicer.selectPayOrderByOrderNumber(orderNumber);
        return success(sqbServiceImpl.query(payOrder));
    }

}
