package com.xinnongyun.net;

import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Build;

import com.xinnongyun.json.JsonUtil;

/**
 * 异步通信类   版本更新
 * @author sm
 *
 */
public class NetConnectionVersion {


	/**
	 * 版本更新
	 * @param url
	 * @param para 
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void VersionGetNetConnection(final String url,
			final VersionSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(
						Void... params) {
					try {
						SyncHttpVersion syncHttp = new SyncHttpVersion();
						try {
							String farmInfo = syncHttp.httpGet(url);
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
						SyncHttpVersion syncHttp = new SyncHttpVersion();
						try {
							String farmInfo = syncHttp.httpGet(url);
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
	
	
	
	public static interface VersionSuccessCallBack {
		void onSuccess(HashMap<String, Object> result);
	}
	
}
