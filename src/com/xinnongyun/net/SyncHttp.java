package com.xinnongyun.net;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
@SuppressWarnings("deprecation")
public class SyncHttp
{
	
	/**
	 * 通过GET方式发送请求
	 * @param url URL地址
	 * @param params 参数
	 * @return 
	 * @throws Exception
	 */
	public String httpGet(String url, String params) throws Exception
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

	/**
	 * 通过POST方式发送请求 获取短信
	 * @param url URL地址
	 * @param params 参数
	 * @return
	 * @throws Exception
	 */
	public String httpPost(String url, List<Parameter> params) throws Exception
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
	 * 通过delete方式发送请求
	 * @param url URL地址
	 * @param params 参数
	 * @return 
	 * @throws Exception
	 */
	public String httpdelete(String url, String params) throws Exception
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
		HttpDelete httpdelete = new HttpDelete(url);
		try
		{
			HttpResponse httpResponse = httpClient.execute(httpdelete);
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
	
	/**
	 * 通过put方式发送请求,修改用户信息
	 * @param url URL地址
	 * @param params 参数
	 * @return 
	 * @throws Exception
	 */
	public String httpPut(String url,  HashMap<String, String> params,String tokenAuth) throws Exception
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
		HttpPut httpPut = new HttpPut(url);
		httpPut.setHeader("Authorization", tokenAuth);
		if (params.size()>=0)
		{
			
			MultipartEntity reqEntity = new MultipartEntity();
			if(params.keySet().contains("password"))
			{
				reqEntity.addPart("password", new StringBody(params.get("password"),Charset.forName("UTF-8")));
				reqEntity.addPart("username", new StringBody(params.get("username"),Charset.forName("UTF-8")));
				reqEntity.addPart("new_password", new StringBody(params.get("new_password"),Charset.forName("UTF-8")));
			}
			else
			{
		        // reqEntity.addPart("username", new StringBody(params.get("username"),Charset.forName("UTF-8")));
				if(params.keySet().contains("nickname"))
				{
		            reqEntity.addPart("nickname", new StringBody(params.get("nickname"),Charset.forName("UTF-8")));
				}
				if(params.keySet().contains("avatar"))
				{
					reqEntity.addPart("avatar", new FileBody(new File(params.get("avatar"))));
				}
			}
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
	
	/**
	 * 通过put方式发送请求,修改用户密码信息
	 * @param url URL地址
	 * @param params 参数
	 * @return 
	 * @throws Exception
	 */
	public String httpPutSetNewPwd(String url,  HashMap<String, String> params) throws Exception
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
		HttpPut httpPut = new HttpPut(url);
		if (params.size()>=0)
		{
			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("code", new StringBody(params.get("code"),Charset.forName("UTF-8")));
			reqEntity.addPart("username", new StringBody(params.get("username"),Charset.forName("UTF-8")));
			reqEntity.addPart("new_password", new StringBody(params.get("new_password"),Charset.forName("UTF-8")));
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
