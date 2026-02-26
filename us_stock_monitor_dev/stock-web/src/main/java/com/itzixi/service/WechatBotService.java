 package com.itzixi.service;

import com.itzixi.entity.USStockMsg;

import java.util.List;

 /**
  * @ClassName TelegramBotService
  * @Author duqy
  * @Version 1.0
  * @Description TelegramBotService
  **/
 public interface WechatBotService {

     public void sendMessage(String text);

     public void sendMessage(List<USStockMsg> msgList) throws Exception;

 }
