package com.xinnongyun.net;

import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;
import android.os.Build;

import com.xinnongyun.json.JsonUtil;

/**
 * 异步通信类   K线
 * @author sm
 *
 */
public class NetConnectionKLine {

	/**
	 * 获取盒子K线
	 * @param url
	 * @param para 
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void KLineGetNetConnection(final String url,final String para,final String tokenAuth,
			final KLineSuccessCallBackGetAll successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, List<HashMap<String, Object>>>() {
				@Override
				protected List<HashMap<String, Object>> doInBackground(
						Void... params) {
					try {
						SyncHttpKLine syncHttp = new SyncHttpKLine();
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
		}else
		{
			new AsyncTask<Void, Void, List<HashMap<String, Object>>>() {
				@Override
				protected List<HashMap<String, Object>> doInBackground(
						Void... params) {
					try {
						SyncHttpKLine syncHttp = new SyncHttpKLine();
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
	
	public static interface KLineSuccessCallBackGetAll {
		void onSuccess(List<HashMap<String, Object>> result);
	}
	
}
