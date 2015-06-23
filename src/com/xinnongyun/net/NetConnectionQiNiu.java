package com.xinnongyun.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.os.AsyncTask;
import android.os.Build;
import com.xinnongyun.json.JsonUtil;

//NetConnectionQiNiu.PostNetConnection(
//		NetUrls.URL_QINIU_TOKEN
//		, "key=2015-02-15T10:46:03.avi", 
//		"Token "+MySharePreference.getValueFromKey(LoginActivity.this, MySharePreference.ACCOUNTTOKEN), null);
/**
 * 异步通信类   七牛
 * @author sm
 *
 */
public class NetConnectionQiNiu {
	
	/**
	 *  获取Token
	 * @param url  http://www.nnong.com/api-journal/token/
	 * @param para  key=2015-02-15T10:46:03.avi -H "Authorization: Token e3e2ac24aa4a6f1fc97180d45808d1dd898e807d"
	 * @param tokenAuth
	 * @param successCallBack
	 */
	public static void PostNetConnection(final String url, final String para,final String tokenAuth,
			final QiNiuSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(
						Void... params) {
					try {
						SyncHttpQiNiu syncHttp = new SyncHttpQiNiu();
						try {
							 List<Parameter> data = new ArrayList<Parameter>();
							 for(String s : para.split("&"))
							 {
								 data.add(new Parameter(s.split("=")[0], s.split("=")[1]));
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
						SyncHttpQiNiu syncHttp = new SyncHttpQiNiu();
						try {
							 List<Parameter> data = new ArrayList<Parameter>();
							 for(String s : para.split("&"))
							 {
								 data.add(new Parameter(s.split("=")[0], s.split("=")[1]));
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

	public static interface QiNiuSuccessCallBack {
		void onSuccess(HashMap<String, Object> result);
	}
}
