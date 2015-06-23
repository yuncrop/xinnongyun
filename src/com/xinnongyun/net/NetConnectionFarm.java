package com.xinnongyun.net;

import java.util.HashMap;
import java.util.List;
import android.os.AsyncTask;
import android.os.Build;

import com.xinnongyun.json.JsonUtil;

/**
 * 异步通信类
 * @author sm
 *
 */
public class NetConnectionFarm {

	/**
	 * 新建农场
	 * @param url
	 * @param para user_serial, password, farm_name, (lat, lng, location), (bg), (logo), (description)
	 * @param map 文件 
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void FarmPostNetConnection(final String url, final String para,final String tokenAuth,
			final FarmSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(
						Void... params) {
					try {
						SyncHttpFarm syncHttp = new SyncHttpFarm();
						try {
							 HashMap<String, String> data = new HashMap<String, String>();
							 if(para.length()>0)
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
						SyncHttpFarm syncHttp = new SyncHttpFarm();
						try {
							 HashMap<String, String> data = new HashMap<String, String>();
							 if(para.length()>0)
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

	
	/**
	 * 修改农场
	 * @param url
	 * @param para user_serial, password, farm_serial, (new_name), (new_lat, new_lng, new_location), (new_bg), (new_logo), (new_description)
	 * @param map 文件 
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void FarmPutNetConnection(final String url, final String para,final String tokenAuth,
			final FarmSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(
						Void... params) {
					try {
						SyncHttpFarm syncHttp = new SyncHttpFarm();
						try {
							HashMap<String, String> data = new HashMap<String, String>();
							if(para!=null)
							 for(String s : para.split("&"))
							 {
								 data.put(s.split("=")[0], s.split("=")[1]);
							 }
							String farmInfo = syncHttp.httpPut(url, data, tokenAuth);
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
						SyncHttpFarm syncHttp = new SyncHttpFarm();
						try {
							HashMap<String, String> data = new HashMap<String, String>();
							if(para!=null)
							 for(String s : para.split("&"))
							 {
								 data.put(s.split("=")[0], s.split("=")[1]);
							 }
							String farmInfo = syncHttp.httpPut(url, data, tokenAuth);
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
	 * 退出删除  农场
	 * @param url
	 * @param para user_serial, password, farm_serial
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void FarmExitNetConnection(final String url, final String tokenAuth,
			final FarmSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(
						Void... params) {
					try {
						SyncHttpFarm syncHttp = new SyncHttpFarm();
						try {
							String farmInfo = syncHttp.httpdelete(url, tokenAuth);
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
						SyncHttpFarm syncHttp = new SyncHttpFarm();
						try {
							String farmInfo = syncHttp.httpdelete(url, tokenAuth);
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
	 * 删除  农场
	 * @param url
	 * @param para user_serial, password, farm_serial
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void FarmDeleteNetConnection(final String url, final String tokenAuth,
			final FarmSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(
						Void... params) {
					try {
						SyncHttpFarm syncHttp = new SyncHttpFarm();
						try {
							String farmInfo = syncHttp.httpdeleteFarm(url, tokenAuth);
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
						SyncHttpFarm syncHttp = new SyncHttpFarm();
						try {
							String farmInfo = syncHttp.httpdeleteFarm(url, tokenAuth);
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
	 * 获取  农场
	 * @param url
	 * @param para user_serial, password, farm_serial
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void FarmGetNetConnection(final String url,final String para, final String tokenAuth,
			final FarmSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(
						Void... params) {
					try {
						SyncHttpFarm syncHttp = new SyncHttpFarm();
						try {
							String farmInfo = syncHttp.httpGet(url, para, tokenAuth);
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
						SyncHttpFarm syncHttp = new SyncHttpFarm();
						try {
							String farmInfo = syncHttp.httpGet(url, para, tokenAuth);
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
	 * 获取 自己  农场
	 * @param url
	 * @param para user_serial, password, farm_serial
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void FarmGetMineNetConnection(final String url,final String para, final String tokenAuth,
			final FarmSuccessCallBackList successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, List<HashMap<String, Object>>>() {
				@Override
				protected List<HashMap<String, Object>> doInBackground(
						Void... params) {
					try {
						SyncHttpFarm syncHttp = new SyncHttpFarm();
						try {
							String farmInfo = syncHttp.httpGet(url, para, tokenAuth);
							List<HashMap<String, Object>> map = JsonUtil.getListHashMapFromJsonArrayString(farmInfo);
							if(map!=null)
							{
								if(map.size() == 0)
								{
									map.add(null);
								}
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
						SyncHttpFarm syncHttp = new SyncHttpFarm();
						try {
							String farmInfo = syncHttp.httpGet(url, para, tokenAuth);
							List<HashMap<String, Object>> map = JsonUtil.getListHashMapFromJsonArrayString(farmInfo);
							if(map!=null)
							{
								if(map.size() == 0)
								{
									map.add(null);
								}
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
	 * 获取 单个  农场
	 * @param url
	 * @param para user_serial, password, farm_serial
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void FarmGetNetOneConnection(final String url,final String para, final String tokenAuth,
			final FarmSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(
						Void... params) {
					try {
						SyncHttpFarm syncHttp = new SyncHttpFarm();
						try {
							String farmInfo = syncHttp.httpGet(url, para, tokenAuth);
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
						SyncHttpFarm syncHttp = new SyncHttpFarm();
						try {
							String farmInfo = syncHttp.httpGet(url, para, tokenAuth);
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
	public static interface FarmSuccessCallBackList {
		void onSuccess(List<HashMap<String, Object>> result);
	}
	
	public static interface FarmSuccessCallBack {
		void onSuccess(HashMap<String, Object> result);
	}
	
}
