package com.ruoyi.pay.wx.service;

import com.wechat.pay.java.service.wexinpayscoreparking.model.Transaction;

public interface IWxPayService {
    public void callback(Transaction transaction);
}
