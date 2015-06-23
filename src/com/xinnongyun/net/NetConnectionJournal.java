package com.xinnongyun.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;
import android.os.Build;

import com.xinnongyun.json.JsonUtil;
import com.xinnongyun.net.NetConnectionCollector.CollectorSuccessCallBack;

/**
 * 异步通信类   服务器视频处理
 * @author sm
 *
 */
public class NetConnectionJournal {
	
	/**
	 *  获取Token
	 * @param url  http://www.nnong.com/api-journal/
	 * @param para
	 * @param tokenAuth
	 * @param successCallBack
	 */
	public static void PostNetConnection(final String url, final String para,final String tokenAuth,
			final JournalSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(
						Void... params) {
					try {
						SyncHttpJournal syncHttp = new SyncHttpJournal();
						try {
							 List<Parameter> data = new ArrayList<Parameter>();
							 try {
								for(String s : para.split("&"))
								 {
									 data.add(new Parameter(s.split("=")[0], s.split("=")[1]));
								 }
							} catch (Exception e) {
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("status_code", "444");
								return map;
							}
							String farmInfo = syncHttp.httpPost(url, tokenAuth, data);
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
						SyncHttpJournal syncHttp = new SyncHttpJournal();
						try {
							 List<Parameter> data = new ArrayList<Parameter>();
							 try {
								for(String s : para.split("&"))
								 {
									 data.add(new Parameter(s.split("=")[0], s.split("=")[1]));
								 }
							} catch (Exception e) {
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("status_code", "444");
								return map;
							}
							String farmInfo = syncHttp.httpPost(url, tokenAuth, data);
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

	
	
	
	
	/**
	 * 删除视频
	 * @param url
	 * @param para 
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void JournalDeleteNetConnection(final String url,final String para,final String tokenAuth,
			final CollectorSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(
						Void... params) {
					try {
						SyncHttpJournal syncHttp = new SyncHttpJournal();
						try {
							
							String farmInfo = syncHttp.httpdelete(url,tokenAuth);
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
						SyncHttpJournal syncHttp = new SyncHttpJournal();
						try {
							
							String farmInfo = syncHttp.httpdelete(url,tokenAuth);
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
	
	
	/**
	 * 获取农场视频列表
	 * @param url
	 * @param para 
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void JournalGetNetConnection(final String url,final String para,final String tokenAuth,
			final JournalListSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, List<HashMap<String, Object>>>() {
				@Override
				protected List<HashMap<String, Object>> doInBackground(
						Void... params) {
					try {
						SyncHttpJournal syncHttp = new SyncHttpJournal();
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
						SyncHttpJournal syncHttp = new SyncHttpJournal();
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
	 * 获取农场某条视频
	 * @param url
	 * @param para 
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void JournalGetOneNetConnection(final String url,final String para,final String tokenAuth,
			final JournalSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(
						Void... params) {
					try {
						SyncHttpJournal syncHttp = new SyncHttpJournal();
						try {
							String beingInfo = syncHttp.httpGet(url,para,tokenAuth);
							HashMap<String, Object> map = JsonUtil.getHashMap(beingInfo);
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
						SyncHttpJournal syncHttp = new SyncHttpJournal();
						try {
							String beingInfo = syncHttp.httpGet(url,para,tokenAuth);
							HashMap<String, Object> map = JsonUtil.getHashMap(beingInfo);
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
	
	
	public static interface JournalListSuccessCallBack
	{
		void onSuccess(List<HashMap<String, Object>> result);
	}
	
	
	public static interface JournalSuccessCallBack {
		void onSuccess(HashMap<String, Object> result);
	}
}
