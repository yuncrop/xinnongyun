package com.xinnongyun.net;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.xinnongyun.config.YunTongXun;

/**
 * http通信类 种养活动
 * 
 * @author sm
 * 
 */
public class SyncHttpFarming {

	/**
	 * 通过GET方式发送请求  获取种养记录
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
		if (para != null) {
			url += "?" + para;
		}
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

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
	
	
	/**
	 * 通过POST方式发送请求  新建种养记录
	 * @param url URL地址
	 * @param params 参数
	 * @return
	 * @throws Exception
	 */
	public String httpPost(String url, List<Parameter> params, String authToken) throws Exception
	{
		String response = null;
		int timeoutConnection = YunTongXun.httpclienttime;  
		int timeoutSocket = YunTongXun.httpclienttime;  
		HttpParams httpParameters = new BasicHttpParams();// Set the timeout in milliseconds until a connection is established.  
	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);// Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data.  
	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);  
		// 构造HttpClient的实例
		HttpClient httpClient = new DefaultHttpClient(httpParameters);  
		HttpPost httpPost = new HttpPost(url);
		if (params.size()>=0)
		{
			//设置httpPost请求参数
			httpPost.setEntity(new UrlEncodedFormEntity(buildNameValuePair(params),HTTP.UTF_8));
		}
		if (authToken != null) {
			httpPost.setHeader("Authorization", authToken);
		}
		//使用execute方法发送HTTP Post请求，并返回HttpResponse对象
		HttpResponse httpResponse = httpClient.execute(httpPost);
		
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		if(statusCode==HttpStatus.SC_CREATED || statusCode == HttpStatus.SC_OK)
		{
			
			//获得返回结果
    		response = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
		}
		else
		{ 
			return "{status_code:"+statusCode+"}";
		}
		return response;
	}
	
	/**
	 * 把Parameter类型集合转换成NameValuePair类型集合
	 * @param params 参数集合
	 * @return
	 */
	private List<BasicNameValuePair> buildNameValuePair(List<Parameter> params)
	{
		List<BasicNameValuePair> result = new ArrayList<BasicNameValuePair>();
		for (Parameter param : params)
		{
			BasicNameValuePair pair = new BasicNameValuePair(param.getName(), param.getValue());
			result.add(pair);
		}
		return result;
	}
	
	
	
	
	/**
	 * 通过put方式发送请求,修改信息
	 * @param url URL地址
	 * @param params 参数
	 * @return 
	 * @throws Exception
	 */
	public String httpPut(String url,  List<Parameter> params,String authToken) throws Exception
	{
		String response = null;
		int timeoutConnection = YunTongXun.httpclienttime;  
		int timeoutSocket = YunTongXun.httpclienttime;  
		HttpParams httpParameters = new BasicHttpParams();// Set the timeout in milliseconds until a connection is established.  
	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);// Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data.  
	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);  
		// 构造HttpClient的实例
		HttpClient httpClient = new DefaultHttpClient(httpParameters);  
		HttpPut httpPut = new HttpPut(url);
		if (params.size()>=0)
		{
			//设置httpPost请求参数
			httpPut.setEntity(new UrlEncodedFormEntity(buildNameValuePair(params),HTTP.UTF_8));
		}
		if (authToken != null) {
			httpPut.setHeader("Authorization", authToken);
		}
		//使用execute方法发送HTTP Post请求，并返回HttpResponse对象
		HttpResponse httpResponse = httpClient.execute(httpPut);
		
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		if(statusCode==HttpStatus.SC_CREATED || statusCode == HttpStatus.SC_OK)
		{
			//获得返回结果
    		response = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
		}
		else
		{ 
			return "{status_code:"+statusCode+"}";
		}
		return response;
	}
	
	

}
