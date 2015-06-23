package com.xinnongyun.net;

import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;
import android.os.Build;

import com.xinnongyun.json.JsonUtil;

/**
 * 异步通信类   物种
 * @author sm
 *
 */
public class NetConnectionBeing {

	
	/**
	 * 获取系统默认物种列表
	 * @param url
	 * @param para 
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void BeingGetNetConnection(final String url,final String para,final String tokenAuth,
			final BeingSuccessCallBackGetAll successCallBack) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, List<HashMap<String, Object>>>() {
				@Override
				protected List<HashMap<String, Object>> doInBackground(
						Void... params) {
					try {
						SyncHttpBeing syncHttp = new SyncHttpBeing();
						try {
							String beingInfo = syncHttp.httpGet(url,para,tokenAuth);
							List<HashMap<String, Object>> map = JsonUtil.getListHashMapFromJsonArrayString(beingInfo);
							if(map!=null)
							{
								return map;
							}
							return null;
						} catch (Exception e) {
							return null;
						}
					} catch (Exception e) {
						return null;
					}
				}

				@Override
				protected void onPostExecute(List<HashMap<String, Object>> result) {
					
						if (successCallBack != null) {
							successCallBack.onSuccess(result);
					}
				}
			}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else
		{
			new AsyncTask<Void, Void, List<HashMap<String, Object>>>() {
				@Override
				protected List<HashMap<String, Object>> doInBackground(
						Void... params) {
					try {
						SyncHttpBeing syncHttp = new SyncHttpBeing();
						try {
							String beingInfo = syncHttp.httpGet(url,para,tokenAuth);
							List<HashMap<String, Object>> map = JsonUtil.getListHashMapFromJsonArrayString(beingInfo);
							if(map!=null)
							{
								return map;
							}
							return null;
						} catch (Exception e) {
							return null;
						}
					} catch (Exception e) {
						return null;
					}
				}

				@Override
				protected void onPostExecute(List<HashMap<String, Object>> result) {
					
						if (successCallBack != null) {
							successCallBack.onSuccess(result);
					}
				}
			}.execute();
		}
		

	}

	
	/**
	 * 新建作物
	 * @param url
	 * @param para name=番茄&   icon= e/ru/Downloads/icon.png&    kingdom=1&    farm={id}' -H 'Authorization: Token b23d1ff85398571932801bde0ae3f73e3e5e595c'
	 * @param map 文件 
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void BeingPostNetConnection(final String url, final String para,final String tokenAuth,
			final BeingSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(
						Void... params) {
					try {
						SyncHttpBeing syncHttp = new SyncHttpBeing();
						try {
							 HashMap<String, String> data = new HashMap<String, String>();
							 for(String s : para.split("&"))
							 {
								 data.put(s.split("=")[0], s.split("=")[1]);
							 }
							String farmInfo = syncHttp.httpPost(url, data, tokenAuth);
							HashMap<String, Object> map = JsonUtil.getHashMap(farmInfo);
							if(map!=null)
							{
									return map;
							}
							return null;
						} catch (Exception e) {
							return null;
						}
					} catch (Exception e) {
						return null;
					}
				}

				@Override
				protected void onPostExecute(HashMap<String, Object> result) {
						if (successCallBack != null) {
							successCallBack.onSuccess(result);
					}
				}
			}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else
		{
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(
						Void... params) {
					try {
						SyncHttpBeing syncHttp = new SyncHttpBeing();
						try {
							 HashMap<String, String> data = new HashMap<String, String>();
							 for(String s : para.split("&"))
							 {
								 data.put(s.split("=")[0], s.split("=")[1]);
							 }
							String farmInfo = syncHttp.httpPost(url, data, tokenAuth);
							HashMap<String, Object> map = JsonUtil.getHashMap(farmInfo);
							if(map!=null)
							{
									return map;
							}
							return null;
						} catch (Exception e) {
							return null;
						}
					} catch (Exception e) {
						return null;
					}
				}

				@Override
				protected void onPostExecute(HashMap<String, Object> result) {
						if (successCallBack != null) {
							successCallBack.onSuccess(result);
					}
				}
			}.execute();
		}
		
	}

	
	public static interface BeingSuccessCallBack {
		void onSuccess(HashMap<String, Object> result);
	}
	
	public static interface BeingSuccessCallBackGetAll {
		void onSuccess(List<HashMap<String, Object>> result);
	}
	
}
