package com.itzixi.utils;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @ClassName DingTalkGroupMessageUtils
 * @Author duqy
 * @Version 1.0
 * @Description DingTalkGroupMessageUtils
 **/
public class DingTalkGroupMessageUtils {

//    https://oapi.dingtalk.com/robot/send?access_token=

    public static final String CUSTOM_ROBOT_TOKEN = "76b9d5cae72d7ab0546bca2a6f97ef1c27f2cbd39e6ea5765643dafa19292235";

    public static final String USER_ID= "<you need @ group user's userId>";

    public static final String SECRET = "SEC4989c402942af6c9b1b7e0be5ba57b45b639c02a446f98b868acd5783d752f48";

    public static void sendTextMessage() {
        try {
            Long timestamp = System.currentTimeMillis();
            System.out.println(timestamp);
            String secret = SECRET;
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");
            System.out.println(sign);

            //sign字段和timestamp字段必须拼接到请求URL上，否则会出现 310000 的错误信息
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?sign="+sign+"&timestamp="+timestamp);
            OapiRobotSendRequest req = new OapiRobotSendRequest();
            /**
             * 发送文本消息
             */
            //定义文本内容
            OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
            text.setContent("hello duqy，这是java代码调用的钉钉机器人所发送的消息~~~");
            //定义 @ 对象
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            at.setAtUserIds(Arrays.asList(USER_ID));
            //设置消息类型
            req.setMsgtype("text");
            req.setText(text);
            req.setAt(at);
            OapiRobotSendResponse rsp = client.execute(req, CUSTOM_ROBOT_TOKEN);
            System.out.println(rsp.getBody());
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new DingTalkGroupMessageUtils().sendTextMessage();
    }

}
