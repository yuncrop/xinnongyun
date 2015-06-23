package com.xinnongyun.net;

import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;
import android.os.Build;

import com.xinnongyun.json.JsonUtil;

/**
 * 异步通信类   土壤
 * @author sm
 *
 */
public class NetConnectionSoil {

	
	/**
	 * 获取系统默认土壤列表
	 * @param url
	 * @param para 
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void CollectorGetNetConnection(final String url,final String para,final String tokenAuth,
			final SoilSuccessCallBackGetAll successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, List<HashMap<String, Object>>>() {
				@Override
				protected List<HashMap<String, Object>> doInBackground(
						Void... params) {
					try {
						SyncHttpSoil syncHttp = new SyncHttpSoil();
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
						SyncHttpSoil syncHttp = new SyncHttpSoil();
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
	
	public static interface SoilSuccessCallBackGetAll {
		void onSuccess(List<HashMap<String, Object>> result);
	}
	
}
