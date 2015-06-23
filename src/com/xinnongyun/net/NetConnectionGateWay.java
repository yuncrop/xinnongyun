package com.xinnongyun.net;

import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Build;

import com.xinnongyun.json.JsonUtil;

/**
 * 异步通信类   网关
 * @author sm
 *
 */
public class NetConnectionGateWay {

	
	/**
	 * 获取系统默认物种列表
	 * @param url
	 * @param para 
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void GateWayGetNetConnection(final String url,final String para,final String tokenAuth,
			final GateWaySuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(
						Void... params) {
					try {
						SyncHttpGateWay syncHttp = new SyncHttpGateWay();
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
						SyncHttpGateWay syncHttp = new SyncHttpGateWay();
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
	
	public static interface GateWaySuccessCallBack {
		void onSuccess(HashMap<String, Object> result);
	}
	
}
