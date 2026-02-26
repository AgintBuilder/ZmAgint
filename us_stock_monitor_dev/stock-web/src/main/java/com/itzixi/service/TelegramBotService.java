package com.itzixi.service;

import com.itzixi.entity.USStockMsg;

import java.util.List;

/**
 * @ClassName TelegramBotService
 * @Author duqy
 * @Version 1.0
 * @Description TelegramBotService
 **/
public interface TelegramBotService {

    /**
     * @Description: 用于发送单个消息（测试）
     * @Author duqy
     * @param text
     */
    public void sendMessage(String text);

    /**
     * @Description: 发送消息
     * @Author duqy
     * @param msgList
     */
    public void sendMessage(List<USStockMsg> msgList) throws Exception;

}
