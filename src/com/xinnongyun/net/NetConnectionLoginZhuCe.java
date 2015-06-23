package com.xinnongyun.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;
import android.os.Build;

import com.xinnongyun.json.JsonUtil;

/**
 * 异步通信类
 * 
 * @author sm
 * 
 */
public class NetConnectionLoginZhuCe {

	/**
	 * 注册
	 * 
	 * @param url
	 * @param para
	 *            用户名和密码
	 * @param successCallBack
	 *            返回结果 需自己实现
	 */
	public static void ZhucePostNetConnection(final String url,
			final String para, final ZhuceSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(Void... params) {
					try {
						SyncHttp syncHttp = new SyncHttp();
						try {
							List<Parameter> data = new ArrayList<Parameter>();
							for (String s : para.split("&")) {
								data.add(new Parameter(s.split("=")[0], s
										.split("=")[1]));
							}
							String retStr = syncHttp.httpPost(url, data);
							HashMap<String, Object> map = JsonUtil
									.getHashMap(retStr);
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

		} else {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(Void... params) {
					try {
						SyncHttp syncHttp = new SyncHttp();
						try {
							List<Parameter> data = new ArrayList<Parameter>();
							for (String s : para.split("&")) {
								data.add(new Parameter(s.split("=")[0], s
										.split("=")[1]));
							}
							String retStr = syncHttp.httpPost(url, data);
							HashMap<String, Object> map = JsonUtil
									.getHashMap(retStr);
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
	 * 注册获取验证码
	 * 
	 * @param url
	 * @param para
	 *            手机号码
	 * @param ZhuceYZMSuccessCallBack
	 *            返回结果 需自己实现
	 */
	public static void ZhuceYanZhengNetConnection(final String url,
			final String para, final ZhuceSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(Void... params) {
					try {
						SyncHttp syncHttp = new SyncHttp();
						try {
							List<Parameter> data = new ArrayList<Parameter>();
							for (String s : para.split("&")) {
								data.add(new Parameter(s.split("=")[0], s
										.split("=")[1]));
							}

							String retStr = syncHttp.httpPost(url, data);
							HashMap<String, Object> responseJson = JsonUtil
									.getHashMap(retStr);
							if (responseJson != null) {
								return responseJson;
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
		} else {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(Void... params) {
					try {
						SyncHttp syncHttp = new SyncHttp();
						try {
							List<Parameter> data = new ArrayList<Parameter>();
							for (String s : para.split("&")) {
								data.add(new Parameter(s.split("=")[0], s
										.split("=")[1]));
							}

							String retStr = syncHttp.httpPost(url, data);
							HashMap<String, Object> responseJson = JsonUtil
									.getHashMap(retStr);
							if (responseJson != null) {
								return responseJson;
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
	 * 修改信息
	 * 
	 * @param url
	 * @param para
	 *            user_serial, password, (new_nickname), (new_avatar),
	 *            (new_password)
	 * @param successCallBack
	 *            返回结果 需自己实现
	 */
	public static void ModifyPutNetConnection(final String url,
			final String para, final String headerToken,
			final ZhuceSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(Void... params) {
					try {
						SyncHttp syncHttp = new SyncHttp();

						try {
							HashMap<String, String> data = new HashMap<String, String>();
							for (String s : para.split("&")) {
								data.put(s.split("=")[0], s.split("=")[1]);
							}
							String retStr = syncHttp.httpPut(url, data,
									headerToken);
							HashMap<String, Object> map = JsonUtil
									.getHashMap(retStr);
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
		} else {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(Void... params) {
					try {
						SyncHttp syncHttp = new SyncHttp();

						try {
							HashMap<String, String> data = new HashMap<String, String>();
							for (String s : para.split("&")) {
								data.put(s.split("=")[0], s.split("=")[1]);
							}
							String retStr = syncHttp.httpPut(url, data,
									headerToken);
							HashMap<String, Object> map = JsonUtil
									.getHashMap(retStr);
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
	 * 通过验证码设置新密码 找回密码
	 * 
	 * @param url
	 * @param para
	 *            user_serial, password, (new_nickname), (new_avatar),
	 *            (new_password)
	 * @param successCallBack
	 *            返回结果 需自己实现
	 */
	public static void ModifyPutSetNewPwdNetConnection(final String url,
			final String para, final ZhuceSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(Void... params) {
					try {
						SyncHttp syncHttp = new SyncHttp();

						try {
							HashMap<String, String> data = new HashMap<String, String>();
							for (String s : para.split("&")) {
								data.put(s.split("=")[0], s.split("=")[1]);
							}
							String retStr = syncHttp
									.httpPutSetNewPwd(url, data);
							HashMap<String, Object> map = JsonUtil
									.getHashMap(retStr);
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
		} else {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(Void... params) {
					try {
						SyncHttp syncHttp = new SyncHttp();

						try {
							HashMap<String, String> data = new HashMap<String, String>();
							for (String s : para.split("&")) {
								data.put(s.split("=")[0], s.split("=")[1]);
							}
							String retStr = syncHttp
									.httpPutSetNewPwd(url, data);
							HashMap<String, Object> map = JsonUtil
									.getHashMap(retStr);
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
	 * 登录
	 * 
	 * @param url
	 * @param para
	 *            手机号码 密码
	 * @param SuccessCallBack
	 *            返回结果 需自己实现
	 */
	public static void LoginNetConnection(final String url, final String para,
			final ZhuceSuccessCallBack successCallBack) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(Void... params) {
					try {
						SyncHttp syncHttp = new SyncHttp();
						try {
							List<Parameter> data = new ArrayList<Parameter>();
							for (String s : para.split("&")) {
								data.add(new Parameter(s.split("=")[0], s
										.split("=")[1]));
							}
							String retStr = syncHttp.httpPost(url, data);

							if (retStr != null) {
								HashMap<String, Object> map = JsonUtil
										.getHashMap(retStr);
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
		} else {
			new AsyncTask<Void, Void, HashMap<String, Object>>() {
				@Override
				protected HashMap<String, Object> doInBackground(Void... params) {
					try {
						SyncHttp syncHttp = new SyncHttp();
						try {
							List<Parameter> data = new ArrayList<Parameter>();
							for (String s : para.split("&")) {
								data.add(new Parameter(s.split("=")[0], s
										.split("=")[1]));
							}
							String retStr = syncHttp.httpPost(url, data);

							if (retStr != null) {
								HashMap<String, Object> map = JsonUtil
										.getHashMap(retStr);
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
	 * 
	 * @author sm
	 * 
	 */
	public static interface ZhuceSuccessCallBack {
		void onSuccess(HashMap<String, Object> result);
	}
}
