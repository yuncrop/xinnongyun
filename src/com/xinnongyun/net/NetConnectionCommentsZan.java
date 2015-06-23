package com.xinnongyun.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;
import android.os.Build;

import com.xinnongyun.json.JsonUtil;

/**
 * 异步通信类   赞 评论  播放次数
 * @author sm
 *
 */
public class NetConnectionCommentsZan {

	/**
	 * 赞  或 评论  或 播放次数
	 * @param url
	 * @param para 
	 * @param successCallBack  返回结果 需自己实现
	 */
	public static void PostNetConnection(final String url,final String para,final String tokenAuth,
			final JournalComments_ZanSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(
						Void... params) {
					try {
						SyncHttpJournalCommentsZan syncHttp = new SyncHttpJournalCommentsZan();
						try {
							List<Parameter> data = new ArrayList<Parameter>();
							if(para.length()>0)
							 for(String s : para.split("&"))
							 {
								 data.add(new Parameter(s.split("=")[0], s.split("=")[1]));
							 }
							String farmInfo = syncHttp.httpPost(url,data,tokenAuth);
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
						SyncHttpJournalCommentsZan syncHttp = new SyncHttpJournalCommentsZan();
						try {
							List<Parameter> data = new ArrayList<Parameter>();
							if(para.length()>0)
							 for(String s : para.split("&"))
							 {
								 data.add(new Parameter(s.split("=")[0], s.split("=")[1]));
							 }
							String farmInfo = syncHttp.httpPost(url,data,tokenAuth);
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
	
	
	public static interface JournalComments_ZanSuccessCallBack {
		void onSuccess(HashMap<String, Object> result);
	}
	
}
