package com.xinnongyun.activity;

import java.util.HashMap;

import android.content.Context;
import android.widget.Toast;

/**
 * 提示Toast
 * 
 * @author sm
 * 
 */
public class ErrorMarkToast {
	
	private static final String stateCode = "status_code";
	public static final String unauthorized = "401";
	private static final String forbidden = "403";
	//409: 该手机已使用
	public static final String conflict = "409";
	//410: 验证码过期
	public static final String yanzhengmaLate = "410";
	//400: 参数错误
	private static final String urlParaError = "400";
	private static final String notfound = "404";
	private static final String serverError = "500";
	//url路径错误
	public static final String urlError = "1000";
	public static final String updateError = "2000";

	// 接收短信的手机号码为空
	private static final String smsPhoneNumEmpty = "112300";

	// 短信正文为空
	private static final String smsPhoneContentEmpty = "112301";
	//
	// 群发短信已暂停
	private static final String smsPhoneMuchSend = "112302";
	// 应用未开通短信功能
	private static final String smsPhoneCannotOpen = "112303";
	// 112304
	// 短信内容的编码转换有误
	private static final String smsPhoneCharCodeError = "112304";
	// 112305
	// 应用未上线，短信接收号码外呼受限
	private static final String smsPhoneCannotUse = "112305";
	//
	// 接收模板短信的手机号码为空
	private static final String smsPhoneTemplePhoneEmpty = "112306";
	// 模板短信模板ID为空
	private static final String smsPhoneTempleIDEmpty = "112307";
	// 模板短信模板data参数为空
	private static final String smsPhoneTempleContentEmpty = "112308";
	// 模板短信内容的编码转换有误
	private static final String smsPhoneTempleCharCodeError = "112309";
	// 应用未上线，模板短信接收号码外呼受限
	private static final String smsPhoneTempleNotRights = "112310";
	// 短信模板不存在
	private static final String smsPhoneTempleNotexits = "112311";

	/**
	 * 返回信息错误提示
	 * 
	 * @param context
	 */
	public static void showCallBackToast(Context context,
			HashMap<String, Object> map) {
		if (map.get(stateCode).toString().equals(urlParaError)) {
			Toast.makeText(context, "信息不存在或已被使用！", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (map.get(stateCode).toString().equals(yanzhengmaLate)) {
			Toast.makeText(context, "验证码过期,请重新获取！", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (map.get(stateCode).toString().equals(updateError)) {
			Toast.makeText(context, "版本更新失败,请稍后更新！", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (map.get(stateCode).toString().equals(urlError)) {
			Toast.makeText(context, "下载路径错误,请稍后更新！", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		//短信
		if (map.get(stateCode).toString().equals(smsPhoneMuchSend)) {
			Toast.makeText(context, "手机应用群发短信服务已暂停！", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (map.get(stateCode).toString().equals(smsPhoneTemplePhoneEmpty)) {
			Toast.makeText(context, "手机号码为空！", Toast.LENGTH_SHORT).show();
			return;
		}
		if (map.get(stateCode).toString().equals(smsPhoneTempleIDEmpty)) {
			Toast.makeText(context, "手机模板短信内容为空！", Toast.LENGTH_SHORT).show();
			return;
		}
		if (map.get(stateCode).toString().equals(smsPhoneTempleContentEmpty)) {
			Toast.makeText(context, "手机模板短信内容的编码转换有误！", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (map.get(stateCode).toString().equals(smsPhoneTempleCharCodeError)) {
			Toast.makeText(context, "手机模板短信内容的编码转换有误！", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (map.get(stateCode).toString().equals(smsPhoneTempleNotRights)) {
			Toast.makeText(context, "手机应用未上线，模板短信接收号码外呼受限！", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (map.get(stateCode).toString().equals(smsPhoneTempleNotexits)) {
			Toast.makeText(context, "手机短信模板不存在！", Toast.LENGTH_SHORT).show();
			return;
		}
		if (map.get(stateCode).toString().equals(smsPhoneCannotUse)) {
			Toast.makeText(context, "手机应用未上线，短信接收号码外呼受限！", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (map.get(stateCode).toString().equals(smsPhoneCharCodeError)) {
			Toast.makeText(context, "手机短信内容的编码转换有误！", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (map.get(stateCode).toString().equals(smsPhoneCannotOpen)) {
			Toast.makeText(context, "手机应用未开通短信功能！", Toast.LENGTH_SHORT).show();
			return;
		}
		if (map.get(stateCode).toString().equals(smsPhoneContentEmpty)) {
			Toast.makeText(context, "手机号码为空！", Toast.LENGTH_SHORT).show();
			return;
		}
		if (map.get(stateCode).toString().equals(smsPhoneContentEmpty)) {
			Toast.makeText(context, "手机号码为空！", Toast.LENGTH_SHORT).show();
			return;
		}
		if (map.get(stateCode).toString().equals(smsPhoneNumEmpty)) {
			Toast.makeText(context, "手机短信正文为空！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (map.get(stateCode).toString().equals(unauthorized)) {
			Toast.makeText(context, "您没有登录，请先登录！", Toast.LENGTH_SHORT).show();
			return;
		}
		if (map.get(stateCode).toString().equals(forbidden)) {
			Toast.makeText(context, "请求参数出现错误，请确认重试！", Toast.LENGTH_SHORT).show();
			//Toast.makeText(context, "本地数据出现异常,请重新登录！", Toast.LENGTH_SHORT).show();
			return;
		}

		if (map.get(stateCode).toString().equals(conflict)) {
			Toast.makeText(context, "该号码已被注册！", Toast.LENGTH_SHORT).show();
			return;
		}

		if (map.get(stateCode).toString().equals(notfound)) {
			Toast.makeText(context, "您当前操作的信息已被删除或不存在！", Toast.LENGTH_SHORT).show();
			return;
		}
		if (map.get(stateCode).toString().equals(serverError)) {
			Toast.makeText(context, "服务器出现异常，请稍后重试！", Toast.LENGTH_SHORT).show();
			return;
		}
		Toast.makeText(context, "服务器出现异常，请稍后重试！", Toast.LENGTH_SHORT).show();
	}

}
