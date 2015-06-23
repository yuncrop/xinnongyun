package com.xinnongyun.net;

import java.util.ArrayList;
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
public class NetConnectionSuggestion {

	/**
	 * 注册
	 * @param url
	 * @param para  用户名和密码
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void PostNetConnection(final String url, final String para,final String token,
			final SuggestionSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(
						Void... params) {
					try {
						SyncHttpSuggestion syncHttp = new SyncHttpSuggestion();
						try {
							 List<Parameter> data = new ArrayList<Parameter>();
							 for(String s : para.split("&"))
							 {
								 data.add(new Parameter(s.split("=")[0], s.split("=")[1]));
							 }
							String retStr = syncHttp.httpPost(url,token,
									data);
							HashMap<String, Object> map = JsonUtil.getHashMap(retStr);
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
						SyncHttpSuggestion syncHttp = new SyncHttpSuggestion();
						try {
							 List<Parameter> data = new ArrayList<Parameter>();
							 for(String s : para.split("&"))
							 {
								 data.add(new Parameter(s.split("=")[0], s.split("=")[1]));
							 }
							String retStr = syncHttp.httpPost(url,token,
									data);
							HashMap<String, Object> map = JsonUtil.getHashMap(retStr);
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
	 * 提交注册返回接口
	 * @author sm
	 *
	 */
	public static interface SuggestionSuccessCallBack {
		void onSuccess(HashMap<String, Object> result);
	}
}
