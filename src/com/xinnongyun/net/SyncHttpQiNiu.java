package com.xinnongyun.net;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.xinnongyun.config.YunTongXun;



/**
 * http通信类
 * @author sm
 *
 */
public class SyncHttpQiNiu
{
	/**
	 * 通过POST方式发送请求
	 * @param url URL地址
	 * @param params 参数
	 * @return
	 * @throws Exception
	 */
	public String httpPost(String url,String token, List<Parameter> params) throws Exception
	{
		int timeoutConnection = YunTongXun.httpclienttime;  
		int timeoutSocket = YunTongXun.httpclienttime;  
		HttpParams httpParameters = new BasicHttpParams();// Set the timeout in milliseconds until a connection is established.  
	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);// Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data.  
	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);  
		// 构造HttpClient的实例
		HttpClient httpClient = new DefaultHttpClient(httpParameters);  
		HttpPost httpPost = new HttpPost(url);
		if(token!=null)
		{
			httpPost.setHeader("Authorization", token);
		}
		if (params.size()>=0)
		{
			//设置httpPost请求参数
			httpPost.setEntity(new UrlEncodedFormEntity(buildNameValuePair(params),HTTP.UTF_8));
		}
		//使用execute方法发送HTTP Post请求，并返回HttpResponse对象
		HttpResponse httpResponse = httpClient.execute(httpPost);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) // SC_OK = 200
		{
			// 获得返回结果
			return EntityUtils.toString(httpResponse.getEntity(),
					"utf-8");
		} else {
			return "{status_code:" + EntityUtils.toString(httpResponse.getEntity(),
					"utf-8") + "}";
		}
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
	
}
