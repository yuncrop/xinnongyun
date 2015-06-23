package com.xinnongyun.net;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.sqlite.TablesColumns;

/**
 * http通信类
 * 
 * @author sm
 * 
 */
@SuppressWarnings("deprecation")
public class SyncHttpBeing {

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

	
	
	
	
	
	
	
	
	/**
	 * 通过post方式发送请求  新建作物
	 * @param url URL地址
	 * @param params 参数
	 * @return 
	 * @throws Exception
	 */
	public String httpPost(String url,  HashMap<String, String> params,String authToken) throws Exception
	{
		String response = null; //返回信息
		//拼接请求URL
		int timeoutConnection = YunTongXun.httpclienttime;  
		int timeoutSocket = YunTongXun.httpclienttime;  
		HttpParams httpParameters = new BasicHttpParams();// Set the timeout in milliseconds until a connection is established. 
	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);// Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data.  
	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);  
	    
		// 构造HttpClient的实例
		HttpClient httpClient = new DefaultHttpClient(httpParameters);
		
		// 创建GET方法的实例
		HttpPost httpPost = new HttpPost(url);
		if (authToken != null) {
			httpPost.setHeader("Authorization", authToken);
		}
		if (params.size()>=0)
		{
			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart(TablesColumns.TABLEBEING_NAME, new StringBody(params.get(TablesColumns.TABLEBEING_NAME),Charset.forName("UTF-8")));
			reqEntity.addPart(TablesColumns.TABLEBEING_FARM, new StringBody(params.get(TablesColumns.TABLEBEING_FARM),Charset.forName("UTF-8")));
			reqEntity.addPart(TablesColumns.TABLEBEING_KINGDOM, new StringBody(params.get(TablesColumns.TABLEBEING_KINGDOM),Charset.forName("UTF-8")));
			if(params.keySet().contains(TablesColumns.TABLEBEING_IMAGE))
			{
				reqEntity.addPart(TablesColumns.TABLEBEING_IMAGE, new FileBody(new File(params.get(TablesColumns.TABLEBEING_IMAGE))));
			}
			
			//设置httpPost请求参数
			httpPost.setEntity(reqEntity);
		}
		try
		{
			HttpResponse httpResponse = httpClient.execute(httpPost);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) //SC_OK = 200
			{
				// 获得返回结果
				response = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
			}
			else
			{
				response = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
				return "{status_code:"+statusCode+"}";
			}
		} catch (Exception e)
		{
			return null;
		} 
		return response;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
