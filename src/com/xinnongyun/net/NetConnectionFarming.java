package com.xinnongyun.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;
import android.os.Build;

import com.xinnongyun.json.JsonUtil;

/**
 * 异步通信类 种养活动
 * 
 * @author sm
 * 
 */
public class NetConnectionFarming {

	/**
	 * 获取当前农场的种养活动
	 * 
	 * @param url
	 * @param para
	 * @param successCallBack
	 *            返回结果 需自己实现
	 */
	public static void FarmingGetAllNetConnection(final String url,
			final String para, final String tokenAuth,
			final FarmingSuccessCallBackGetAll successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, List<HashMap<String, Object>>>() {
				@Override
				protected List<HashMap<String, Object>> doInBackground(
						Void... params) {
					try {
						SyncHttpFarming syncHttp = new SyncHttpFarming();
						try {
							String beingInfo = syncHttp.httpGet(url, para,
									tokenAuth);
							List<HashMap<String, Object>> map = JsonUtil
									.getListHashMapFromJsonArrayString(beingInfo);
							if (map != null) {
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
						SyncHttpFarming syncHttp = new SyncHttpFarming();
						try {
							String beingInfo = syncHttp.httpGet(url, para,
									tokenAuth);
							List<HashMap<String, Object>> map = JsonUtil
									.getListHashMapFromJsonArrayString(beingInfo);
							if (map != null) {
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
	 * 新种
	 * 
	 * @param url
	 * @param para
	 * @param successCallBack
	 *            返回结果 需自己实现
	 */
	public static void FarmingPostNetConnection(final String url,
			final String para, final String tokenAuth,
			final FarmingSuccessCallBack successCallBack) {
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(Void... params) {
					try {
						SyncHttpFarming syncHttp = new SyncHttpFarming();
						try {
							List<Parameter> data = new ArrayList<Parameter>();
							for (String s : para.split("&")) {
								data.add(new Parameter(s.split("=")[0], s
										.split("=")[1]));
							}
							String beingInfo = syncHttp.httpPost(url, data,
									tokenAuth);
							HashMap<String, Object> map = JsonUtil
									.getHashMap(beingInfo);
							if (map != null) {
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
				protected HashMap<String, Object> doInBackground(Void... params) {
					try {
						SyncHttpFarming syncHttp = new SyncHttpFarming();
						try {
							List<Parameter> data = new ArrayList<Parameter>();
							for (String s : para.split("&")) {
								data.add(new Parameter(s.split("=")[0], s
										.split("=")[1]));
							}
							String beingInfo = syncHttp.httpPost(url, data,
									tokenAuth);
							HashMap<String, Object> map = JsonUtil
									.getHashMap(beingInfo);
							if (map != null) {
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
	 * 修改记录
	 * 
	 * @param url
	 * @param para
	 * @param successCallBack
	 *            返回结果 需自己实现
	 */
	public static void FarmingPutNetConnection(final String url,
			final String para, final String tokenAuth,
			final FarmingSuccessCallBack successCallBack) {
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(Void... params) {
					try {
						SyncHttpFarming syncHttp = new SyncHttpFarming();
						try {
							List<Parameter> data = new ArrayList<Parameter>();
							for (String s : para.split("&")) {
								data.add(new Parameter(s.split("=")[0], s
										.split("=")[1]));
							}
							String beingInfo = syncHttp.httpPut(url, data,
									tokenAuth);
							HashMap<String, Object> map = JsonUtil
									.getHashMap(beingInfo);
							if (map != null) {
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
				protected HashMap<String, Object> doInBackground(Void... params) {
					try {
						SyncHttpFarming syncHttp = new SyncHttpFarming();
						try {
							List<Parameter> data = new ArrayList<Parameter>();
							for (String s : para.split("&")) {
								data.add(new Parameter(s.split("=")[0], s
										.split("=")[1]));
							}
							String beingInfo = syncHttp.httpPut(url, data,
									tokenAuth);
							HashMap<String, Object> map = JsonUtil
									.getHashMap(beingInfo);
							if (map != null) {
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
	
	public static interface FarmingSuccessCallBack {
		void onSuccess(HashMap<String, Object> result);
	}

	public static interface FarmingSuccessCallBackGetAll {
		void onSuccess(List<HashMap<String, Object>> result);
	}

}
