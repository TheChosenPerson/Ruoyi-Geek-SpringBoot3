package com.ruoyi.pay.sqb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pay.domain.PayOrder;
import com.ruoyi.pay.service.IPayOrderService;
import com.ruoyi.pay.sqb.service.Impl.SQBServiceImpl;

@RestController
@RequestMapping("/pay/sql")
public class SQBController extends BaseController {
    @Autowired
    private SQBServiceImpl sqbServiceImpl;
    @Autowired
    private IPayOrderService payOrderServicer;

    @PostMapping("/payUrl")
    @Anonymous
    public AjaxResult payUrl(@RequestParam("id") String orderNumber) throws Exception {
        PayOrder payOrder = payOrderServicer.selectPayOrderByOrderNumber(orderNumber);
        String url = sqbServiceImpl.payUrl(payOrder);
        AjaxResult ajaxResult = new AjaxResult(200, url, "操作成功");
        return ajaxResult;
    }

    @PostMapping("/query")
    @Anonymous
    public AjaxResult query(@RequestParam("id") String orderNumber) throws Exception {
        PayOrder payOrder = payOrderServicer.selectPayOrderByOrderNumber(orderNumber);
        return success(sqbServiceImpl.query(payOrder));
    }

}
