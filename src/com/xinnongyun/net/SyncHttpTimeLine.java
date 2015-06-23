package com.xinnongyun.net;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.xinnongyun.config.YunTongXun;

/**
 * http通信类
 * 
 * @author sm
 * 
 */
public class SyncHttpTimeLine {

	/**
	 * 通过GET方式发送请求
	 * 
	 * @param url
	 *            URL地址
	 * @param params
	 *            参数
	 * @return
	 * @throws Exception
	 */
	public String httpGet(String url, String para, String tokenAuth)
			throws Exception {
		String response = null; // 返回信息
		// 拼接请求URL

		int timeoutConnection = YunTongXun.httpclienttime;
		int timeoutSocket = YunTongXun.httpclienttime;

		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		if (para != null) {
			url += "?" + para;
		}
		// 构造HttpClient的实例
		HttpClient httpClient = new DefaultHttpClient(httpParameters);
		// 创建GET方法的实例
		HttpGet httpGet = new HttpGet(url);
		if (tokenAuth != null) {
			httpGet.setHeader("Authorization", tokenAuth);
		}
		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) // SC_OK = 200
			{
				// 获得返回结果
				response = EntityUtils.toString(httpResponse.getEntity(),
						"utf-8");
			} else {
				return "{status_code:" + statusCode + "}";
			}
		} catch (Exception e) {
			return null;
		}
		return response;
	}

}
