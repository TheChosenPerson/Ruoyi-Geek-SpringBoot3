package com.ruoyi.pay.alipay.service;

import java.util.Map;

public interface IAliPayService {
    public void callback(Map<String, String> params);
}
