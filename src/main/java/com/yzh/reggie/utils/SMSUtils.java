package com.yzh.reggie.utils;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import org.json.JSONException;
import java.io.IOException;


public class SMSUtils {
	public static void sendMessage(String[] args) {
		int appid = 1313131;
		String appkey = "这里输入步骤4的appkey";
		String[] phoneNumbers = {"这里输入手机号","第二个手机号"}; //手机号可以添很多。
		int templateId = 313131313;
		String smsSign = "这里添你申请的签名，注意不是ID，是签名，中文。";
		try {
			String[] params = {"","123456"};  //第一个参数传递{1}位置想要的内容，第二个传递{2}的内容，以此类推。具体看步骤5
			SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
			SmsSingleSenderResult result = ssender.sendWithParam("86", phoneNumbers[0],
					templateId, params, smsSign, "", "");
			System.out.println(result);
		} catch (HTTPException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}


