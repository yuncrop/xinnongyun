package com.xinnongyun.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;
import android.os.Build;

import com.xinnongyun.json.JsonUtil;

/**
 * 异步通信类   摄像头
 * @author sm
 *
 */
public class NetConnectionCamera {

	/**
	 * 获取  某一条
	 * @param url
	 * @param para 
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void CameraGetNetConnection(final String url,final String para,final String tokenAuth, 
			final CameraSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(
						Void... params) {
					try {
						SyncHttpCamera syncHttp = new SyncHttpCamera();
						try {
							String farmInfo = syncHttp.httpGet(url,para,tokenAuth);
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
						SyncHttpCamera syncHttp = new SyncHttpCamera();
						try {
							String farmInfo = syncHttp.httpGet(url,para,tokenAuth);
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
	 * 农场所有摄像头
	 * @param url
	 * @param para 
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void CameraGetAllNetConnection(final String url,final String para,final String tokenAuth, 
			final CameraAllSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, List<HashMap<String, Object>>>() {
				@Override
				protected List<HashMap<String, Object>> doInBackground(
						Void... params) {
					try {
						SyncHttpCamera syncHttp = new SyncHttpCamera();
						try {
							String farmInfo = syncHttp.httpGet(url,para,tokenAuth);
							List<HashMap<String, Object>> map = JsonUtil.getListHashMapFromJsonArrayString(farmInfo);
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
						SyncHttpCamera syncHttp = new SyncHttpCamera();
						try {
							String farmInfo = syncHttp.httpGet(url,para,tokenAuth);
							List<HashMap<String, Object>> map = JsonUtil.getListHashMapFromJsonArrayString(farmInfo);
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
	 * 更新摄像头信息
	 * @param url
	 * @param para 
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void CameraPutNetConnection(final String url,final String para,final String tokenAuth,
			final CameraSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(
						Void... params) {
					try {
						SyncHttpCamera syncHttp = new SyncHttpCamera();
						try {
							List<Parameter> data = new ArrayList<Parameter>();
							if(para.length()>0)
							 for(String s : para.split("&"))
							 {
								 data.add(new Parameter(s.split("=")[0], s.split("=")[1]));
							 }
							String farmInfo = syncHttp.httpPut(url,data,tokenAuth);
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
						SyncHttpCamera syncHttp = new SyncHttpCamera();
						try {
							List<Parameter> data = new ArrayList<Parameter>();
							if(para.length()>0)
							 for(String s : para.split("&"))
							 {
								 data.add(new Parameter(s.split("=")[0], s.split("=")[1]));
							 }
							String farmInfo = syncHttp.httpPut(url,data,tokenAuth);
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
	 * 删除摄像头信息
	 * @param url
	 * @param para 
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void CameraDeleteNetConnection(final String url,final String para,final String tokenAuth,
			final CameraSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(
						Void... params) {
					try {
						SyncHttpCamera syncHttp = new SyncHttpCamera();
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
						SyncHttpCamera syncHttp = new SyncHttpCamera();
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
	 * 添加摄像头
	 * @param url
	 * @param para
	 * @param map 文件 
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void CameraPostNetConnection(final String url, final String para,final String tokenAuth,
			final CameraSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(
						Void... params) {
					try {
						SyncHttpCamera syncHttp = new SyncHttpCamera();
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
						SyncHttpCamera syncHttp = new SyncHttpCamera();
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
	
	
	
	
	
	
	
	public static interface CameraAllSuccessCallBack {
		void onSuccess(List<HashMap<String, Object>> result);
	}
	
	public static interface CameraSuccessCallBack {
		void onSuccess(HashMap<String, Object> result);
	}
	
}
