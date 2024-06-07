package com.ruoyi.pay.sqb.service;

import com.alibaba.fastjson2.JSONObject;

public interface ISqbPayService {
    public void callback(JSONObject param);
}
