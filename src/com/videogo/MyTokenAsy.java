package com.videogo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.videogo.openapi.EzvizAPI;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.json.JsonUtil;
import com.xinnongyun.sharepreference.MySharePreference;


public class MyTokenAsy extends AsyncTask<Void,Void,Void> {

	
	public Context context;
	
	
	 protected Map<String, Object> paramsInit(String method, Map<String, Object> paramsMap,String yingyunshi_token_time) {
		    if(yingyunshi_token_time == null)
		    {
		    	yingyunshi_token_time = System.currentTimeMillis() / 1000 +"";
		    }
	        Map<String, Object> map = new HashMap<String, Object>();
	        StringBuilder paramString = new StringBuilder();
	        List<String> paramList = new ArrayList<String>();
	        for (Iterator<String> it = paramsMap.keySet().iterator(); it.hasNext();) {
	            String key1 = it.next();
	            String param = key1 + ":" + paramsMap.get(key1);
	            paramList.add(param);
	        }
	        String[] params = paramList.toArray(new String[paramList.size()]);
	        Arrays.sort(params);
	        for (String param : params) {
	            paramString.append(param).append(",");
	        }
	        paramString.append("method").append(":").append(method).append(",");
	        paramString.append("time").append(":").append(yingyunshi_token_time).append(",");
	        paramString.append("secret").append(":").append(YunTongXun.YINGYUNSHI_SECRET_KEY);
	        System.out.println(paramString.toString().trim());
	        //String sign = DigestUtils.md5Hex(paramString.toString().trim());
	        String sign = new String(Hex.encodeHex(DigestUtils.md5(paramString.toString().trim())));
	        System.out.println("sign" + sign);
	        Map<String, Object> systemMap = new HashMap<String, Object>();
	        systemMap.put("ver", "1.0");
	        systemMap.put("sign", sign);
	        systemMap.put("key", YunTongXun.YINGYUNSHI_APP_KEY);
	        systemMap.put("time", yingyunshi_token_time);

	        map.put("system", systemMap);
	        map.put("method", method);
	        map.put("params", paramsMap);
	        map.put("id", "123456");
	        return map;
	}
	
	 
	 protected void doPost(Map<String, Object> map) {
	        String json = JSON.toJSONString(map);
	        ProtocolSocketFactory fcty = new MySecureProtocolSocketFactory();
	        Protocol.registerProtocol("https", new Protocol("https", fcty, 443));
	        HttpClient client = new HttpClient();
	        // 使用POST方法
	        /** 开发者需换成"https://open.ys7.com:443/api/method" */
	        PostMethod method = new PostMethod("https://open.ys7.com:443/api/method");
	        try {
	            RequestEntity entity = new StringRequestEntity(json, "application/json", "UTF-8");
	            method.setRequestEntity(entity);
	            client.executeMethod(method);

	            InputStream inputStream = method.getResponseBodyAsStream();
	            String restult = IOUtils.toString(inputStream);
	            System.out.println(restult);
	            try {
	            	String yingyunshi_token = JsonUtil.getHashMap(JsonUtil.getHashMap(JsonUtil.getHashMap(restult).get("result").toString()).get("data").toString()).get("accessToken").toString();
					EzvizAPI.getInstance().setAccessToken(yingyunshi_token);
					MySharePreference.save(context, "tokenTime", (System.currentTimeMillis() / 1000) +"");
					MySharePreference.save(context, "yingyunshi_token", yingyunshi_token);
					
				} catch (Exception e) {
					
				}
	        } catch (Exception e) {
	        } finally {
	            // 释放连接
	            method.releaseConnection();
	        }
	    }
	 
	@Override
	protected Void doInBackground(Void... params) {
		
//		HashMap<String, Object> timeMap = new HashMap<String, Object>();
//		timeMap.put("id", "123456");
//		timeMap.put("appKey", YunTongXun.YINGYUNSHI_APP_KEY);
//		String json = JSON.toJSONString(timeMap);
		List<NameValuePair> pairsList = new ArrayList<NameValuePair>();
	    pairsList.add(new NameValuePair("appKey", YunTongXun.YINGYUNSHI_APP_KEY));

	    
	    ProtocolSocketFactory fcty = new MySecureProtocolSocketFactory();
        Protocol.registerProtocol("https", new Protocol("https", fcty, 443));
        HttpClient httpClient = new HttpClient();

        PostMethod postMethod = new PostMethod("https://open.ys7.com:443/api/" + "time/get");
        postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        NameValuePair[] valuePairs = pairsList.toArray(new NameValuePair[0]);
        postMethod.setRequestBody(valuePairs);
        try {
            httpClient.executeMethod(postMethod);
            InputStream inputStream = postMethod.getResponseBodyAsStream();
            String returnReult = IOUtils.toString(inputStream);
            System.out.println(returnReult);
            try {
            	String yingyunshi_token_time = JsonUtil.getHashMap(JsonUtil.getHashMap(JsonUtil.getHashMap(returnReult).get("result").toString()).get("data").toString()).get("serverTime").toString();
            	Map<String, Object> paramsMap = new HashMap<String, Object>();
                paramsMap.put("userId", "123456");
                paramsMap.put("phone", "13801377827");
                Map<String, Object> map = paramsInit("token/accessToken/get", paramsMap,yingyunshi_token_time);
        		doPost(map);
            } catch (Exception e) {
            	Map<String, Object> paramsMap = new HashMap<String, Object>();
                paramsMap.put("userId", "123456");
                paramsMap.put("phone", "13801377827");
                Map<String, Object> map = paramsInit("token/accessToken/get", paramsMap,null);
        		doPost(map);
			}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            postMethod.releaseConnection();
        }
//	    sendHttpRequest(pairsList, "time/get");
//        ProtocolSocketFactory fcty = new MySecureProtocolSocketFactory();
//        Protocol.registerProtocol("https", new Protocol("https", fcty, 443));
//        HttpClient client = new HttpClient();
//        // 使用POST方法
//        PostMethod method = new PostMethod("https://open.ys7.com/api/time/get");
//        try {
//            RequestEntity entity = new StringRequestEntity(json, "application/json", "UTF-8");
//            method.setRequestEntity(entity);
//            client.executeMethod(method);
//
//            InputStream inputStream = method.getResponseBodyAsStream();
//            String restult = IOUtils.toString(inputStream);
//        	
//            System.out.println(restult);
//            try {
//            	String yingyunshi_token_time = JsonUtil.getHashMap(JsonUtil.getHashMap(JsonUtil.getHashMap(restult).get("result").toString()).get("data").toString()).get("serverTime").toString();
//            	Map<String, Object> paramsMap = new HashMap<String, Object>();
//                paramsMap.put("userId", "123456");
//                paramsMap.put("phone", "13801377827");
//                Map<String, Object> map = paramsInit("token/accessToken/get", paramsMap,yingyunshi_token_time);
//        		doPost(map);
//            } catch (Exception e) {
//            	Map<String, Object> paramsMap = new HashMap<String, Object>();
//                paramsMap.put("userId", "123456");
//                paramsMap.put("phone", "13801377827");
//                Map<String, Object> map = paramsInit("token/accessToken/get", paramsMap,null);
//        		doPost(map);
//			}
//        } catch (Exception e) {
//        } finally {
//            // 释放连接
//           method.releaseConnection();
//        }
		
		
		
		
		return null;
	}

	
	
	
//	private String postTime () throws Exception
//	{
//		String response = null; //返回信息
//		//拼接请求URL
//		int timeoutConnection = YunTongXun.httpclienttime;  
//		int timeoutSocket = YunTongXun.httpclienttime;  
//		HttpParams httpParameters = new BasicHttpParams();// Set the timeout in milliseconds until a connection is established. 
//	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);// Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data.  
//	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);  
//	    
//		// 构造HttpClient的实例
//	    org.apache.http.client.HttpClient httpClient = new DefaultHttpClient(httpParameters);
//		
//		// 创建GET方法的实例
//		HttpPost httpPost = new HttpPost("https://open.ys7.com/api/time/get");
//		MultipartEntity reqEntity = new MultipartEntity();
//		reqEntity.addPart("id", new StringBody("123456",Charset.forName("UTF-8")));
//		reqEntity.addPart("appKey", new StringBody(YunTongXun.YINGYUNSHI_APP_KEY,Charset.forName("UTF-8")));
//		//设置httpPost请求参数
//		httpPost.setEntity(reqEntity);
//		try
//		{
//			HttpResponse httpResponse = httpClient.execute(httpPost);
//			int statusCode = httpResponse.getStatusLine().getStatusCode();
//			if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) //SC_OK = 200
//			{
//				// 获得返回结果
//				response = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
//			}
//			else
//			{
//				response = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
//				return "{status_code:"+statusCode+"}";
//			}
//		} catch (Exception e)
//		{
//			return null;
//		} 
//		return response;
//	}
//	
	
	
	
	
	
	
	
}