package com.xinnongyun.net;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
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
@SuppressWarnings("deprecation")
public class SyncHttpFarm {

	public static final String IMAGE_BG = "bg";
	public static final String IMAGE_LOGO = "logo";

	/**
	 * 通过POST方式发送请求
	 * 
	 * @param url
	 *            URL地址
	 * @param params
	 *            参数
	 * @param params
	 *            文件
	 * @return
	 * @throws Exception
	 */
	public String httpPost(String url, Map<String, String> map,String tokenAuth) throws Exception{
		HttpClient httpclient = new DefaultHttpClient();
		// 设置通信协议版本
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpPost httppost = new HttpPost(url);
		httppost.setHeader("Authorization", tokenAuth);
		if (map.size()>=0)
		{
			MultipartEntity reqEntity = new MultipartEntity();
			if(map.containsKey("name"))
	        reqEntity.addPart("name", new StringBody(map.get("name"),Charset.forName("UTF-8")));
			if(map.containsKey("location"))
	        reqEntity.addPart("location", new StringBody(map.get("location"),Charset.forName("UTF-8")));
			if(map.containsKey("lng"))
	        reqEntity.addPart("lng", new StringBody(map.get("lng"),Charset.forName("UTF-8")));
			if(map.containsKey("lat"))
	        reqEntity.addPart("lat", new StringBody(map.get("lat"),Charset.forName("UTF-8")));
			if(map.containsKey("description"))
	        reqEntity.addPart("description", new StringBody(map.get("description"),Charset.forName("UTF-8")));
			if(map.containsKey("logo"))
	        reqEntity.addPart("logo", new FileBody(new File(map.get("logo"))));
			if(map.containsKey("bg"))
	        reqEntity.addPart("bg", new FileBody(new File(map.get("bg"))));
			//设置httpPost请求参数
			httppost.setEntity(reqEntity);
		}

		HttpResponse response;
		try {
			response = httpclient.execute(httppost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_CREATED || statusCode == HttpStatus.SC_OK) // SC_OK = 201
			{
				// 获得返回结果
				return EntityUtils.toString(response.getEntity(),"UTF-8");
			}
			else
			{
				return "{status_code:"+statusCode+"}";
			}
		} catch (ClientProtocolException e1) {
			return null;
		} catch (IOException e1) {
			return null;
		}
	}
	
	/**
	 * 把Parameter类型集合转换成NameValuePair类型集合
	 * @param params 参数集合
	 * @return
	 */
	@SuppressWarnings("unused")
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
	 * 修改农场 信息 通过put方式发送请求
	 * @param url URL地址
	 * @param params 参数
	 * @return 
	 * @throws Exception
	 */
	public String httpPut(String url, Map<String, String> map,String tokenAuth) throws Exception
	{
		String response = null; //返回信息
	
		int timeoutConnection = YunTongXun.httpclienttime;  
		int timeoutSocket = YunTongXun.httpclienttime;  
		HttpParams httpParameters = new BasicHttpParams();// Set the timeout in milliseconds until a connection is established.  
	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);// Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data.  
	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);  
	    
		// 构造HttpClient的实例
		HttpClient httpClient = new DefaultHttpClient(httpParameters);  
		
		// 创建GET方法的实例
		HttpPut httpPut = new HttpPut(url);
		httpPut.setHeader("Authorization", tokenAuth);
		if (map.size()>=0)
		{
			MultipartEntity reqEntity = new MultipartEntity();
			if(map.keySet().contains("name"))
	        reqEntity.addPart("name", new StringBody(map.get("name"),Charset.forName("UTF-8")));
			if(map.keySet().contains("location"))
	        reqEntity.addPart("location", new StringBody(map.get("location"),Charset.forName("UTF-8")));
			if(map.keySet().contains("lng"))
	        reqEntity.addPart("lng", new StringBody(map.get("lng"),Charset.forName("UTF-8")));
			if(map.keySet().contains("lat"))
	        reqEntity.addPart("lat", new StringBody(map.get("lat"),Charset.forName("UTF-8")));
			if(map.keySet().contains("description"))
	        reqEntity.addPart("description", new StringBody(map.get("description"),Charset.forName("UTF-8")));
			if(map.keySet().contains("logo"))
	        reqEntity.addPart("logo", new FileBody(new File(map.get("logo"))));
			if(map.keySet().contains("bg"))
	        reqEntity.addPart("bg", new FileBody(new File(map.get("bg"))));
			//设置httpPost请求参数
	        httpPut.setEntity(reqEntity);
		}
		try
		{
			HttpResponse httpResponse = httpClient.execute(httpPut);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) //SC_OK = 200
			{
				// 获得返回结果
				response = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
			}
			else
			{
			//	response = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
				return "{status_code:"+statusCode+"}";
			}
		} catch (Exception e)
		{
			return null;
		} 
		return response;
	}

	
	
	
	/**
	 * 通过put方式发送请求  退出农场
	 * @param url URL地址
	 * @param params 参数  user_serial, password, farm_serial
	 * @return 
	 * @throws Exception
	 */
	public String httpdelete(String url, String tokenAuth) throws Exception
	{
		//拼接请求URL
		int timeoutConnection = YunTongXun.httpclienttime;  
		int timeoutSocket = YunTongXun.httpclienttime;  
		HttpParams httpParameters = new BasicHttpParams();// Set the timeout in milliseconds until a connection is established.  
	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);// Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data.  
	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);  
	    
		// 构造HttpClient的实例
		HttpClient httpClient = new DefaultHttpClient(httpParameters);  
		// 创建GET方法的实例
		HttpPut httpexit = new HttpPut(url);
		httpexit.setHeader("Authorization", tokenAuth);
		try
		{
			HttpResponse httpResponse = httpClient.execute(httpexit);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			return "{status_code:"+statusCode+"}";
			
		} catch (Exception e)
		{
			return null;
		} 
	}
	
	
	/**
	 * 通过delete方式发送请求  删除农场
	 * @param url URL地址
	 * @param params 参数  user_serial, password, farm_serial
	 * @return 
	 * @throws Exception
	 */
	public String httpdeleteFarm(String url, String tokenAuth) throws Exception
	{
		//拼接请求URL
		int timeoutConnection = YunTongXun.httpclienttime;  
		int timeoutSocket = YunTongXun.httpclienttime;  
		HttpParams httpParameters = new BasicHttpParams();// Set the timeout in milliseconds until a connection is established.  
	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);// Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data.  
	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);  
	    
		// 构造HttpClient的实例
		HttpClient httpClient = new DefaultHttpClient(httpParameters);  
		// 创建GET方法的实例
		HttpDelete httpexit = new HttpDelete(url);
		httpexit.setHeader("Authorization", tokenAuth);
		try
		{
			HttpResponse httpResponse = httpClient.execute(httpexit);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			return "{status_code:"+statusCode+"}";
			
		} catch (Exception e)
		{
			return null;
		} 
	}
	
	
	/**
	 * 通过GET方式发送请求  获取农场信息
	 * @param url URL地址
	 * @param params 参数
	 * @return 
	 * @throws Exception
	 */
	public String httpGet(String url, String params, String authToken) throws Exception
	{
		String response = null; //返回信息
		//拼接请求URL
		if (null!=params&&!params.equals(""))
		{
			url += "?" + params;
		}
		
		int timeoutConnection = YunTongXun.httpclienttime;  
		int timeoutSocket = YunTongXun.httpclienttime;
		
		HttpParams httpParameters = new BasicHttpParams();// Set the timeout in milliseconds until a connection is established.
	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);// Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data.  
	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);  
	    
		// 构造HttpClient的实例
		HttpClient httpClient = new DefaultHttpClient(httpParameters);  
		// 创建GET方法的实例
		HttpGet httpGet = new HttpGet(url);
		if(authToken!=null)
		httpGet.setHeader("Authorization", authToken);
		try
		{
			HttpResponse httpResponse = httpClient.execute(httpGet);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) //SC_OK = 200
			{
				// 获得返回结果
				response = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
			}
			else
			{
				return "{status_code:"+statusCode+"}";
			}
		} catch (Exception e)
		{
			return null;
		} 
		return response;
	}
	
	
	
	
	
	
	
	
	
}
