package com.xinnongyun.activity;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xinnongyun.config.NetUrls;
import com.xinnongyun.json.JsonUtil;
import com.xinnongyun.net.NetConnectionLoginZhuCe;
import com.xinnongyun.net.NetConnectionLoginZhuCe.ZhuceSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.utils.DialogManager;
import com.xinnongyun.utils.InputTools;
import com.xinnongyun.utils.StringUtil;

/**
 * 注册界面
 * 
 * @author sm
 * 
 */
public class ZhuCeActivity extends Activity {

	/**
	 * 验证码获取、验证、注册
	 */
	private Button submit;
	private TextView zhuce_get_yanzhengma;
	private ImageView zhuce_return;
	private EditText phoneNumber, yanZhengMa, pwd;

	private TextView zhuceHead;

	// 1获取验证码 2时间计算
	private int type = 1;
	private int time = 120;
	private boolean isTimeContinue = false;

	// 1注册 2找回密码
	private int work = 1;

	private Handler changeHandleNumber = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (time == 1) {
				time = 120;
				isTimeContinue = false;
				type = 1;
				zhuce_get_yanzhengma.setText(R.string.zhuce_btn_cxhqyzm);
			} else {
				zhuce_get_yanzhengma.setText(time + getResources().getString(R.string.yanzhengma_timeleft));
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏ActionBar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		         WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		setContentView(R.layout.activity_login_zhuce);
		Intent i = getIntent();
		if (i != null) {
			work = i.getIntExtra("work", 0);
		}
		initView();
		if (work == 2) {
			zhuceHead.setText(R.string.resetPwd);
			submit.setText(R.string.zhuce_btn_resetPwd);
		}
		ExitApplication.getInstance().addActivity(this);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		zhuceHead = (TextView) findViewById(R.id.zhuceHead_tv);
		zhuce_return = (ImageView) findViewById(R.id.zhuce_return);
		zhuce_return.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		zhuce_get_yanzhengma = (TextView) findViewById(R.id.zhuce_get_yanzhengma);
		zhuce_get_yanzhengma.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (type == 1) {
					getYanZhemgMa();
				}
			}
		});
		submit = (Button) findViewById(R.id.zhuce_btn_submit);
		phoneNumber = (EditText) findViewById(R.id.zhuce_et_sjhm);
		yanZhengMa = (EditText) findViewById(R.id.zhuce_et_yzm);
		pwd = (EditText) findViewById(R.id.zhuce_et_pwd);

		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				InputTools.HideKeyboard(phoneNumber);
				InputTools.HideKeyboard(yanZhengMa);
				InputTools.HideKeyboard(pwd);
			
				if (!StringUtil.checkEditTextIsEmpty(phoneNumber)
						&& !StringUtil.checkEditTextIsEmpty(pwd)
						&& !StringUtil.checkEditTextIsEmpty(yanZhengMa)) {
					if (StringUtil.isMobileNO(phoneNumber.getText().toString()
							.trim())
							&& StringUtil.checkPwd(pwd.getText()
									.toString().trim())
							&& yanZhengMa.getText().toString().trim().length() == 4) {
						// TODO 注册
						submit.setClickable(false);
						if(work == 1)
						{
							submit.setText(R.string.zhuce_btn_newaccounting);
						}
						else if(work == 2)
						{
							submit.setText(R.string.zhuce_btn_resetPwding);
						}
						if (work == 1) {
							String para = NetUrls.URL_ZHUCE_USERNAME + "="
									+ phoneNumber.getText().toString().trim()
									+ "&" + NetUrls.URL_LOGIN_ZHUCE_PASSWORD
									+ "=" + pwd.getText().toString().trim()
									+ "&" + "code="
									+ yanZhengMa.getText().toString().trim();
							NetConnectionLoginZhuCe.ZhucePostNetConnection(
									NetUrls.URL_LOGIN_ZHUCE, para,
									new ZhuceSuccessCallBack() {
										@Override
										public void onSuccess(
												HashMap<String, Object> result) {
											submit.setClickable(true);
											submit.setText(R.string.zhuce_btn_newaccount);
											if (result != null) {
												
												if (result.keySet().contains(
														"status_code")) {
													if (result
															.get("status_code")
															.toString()
															.equals(ErrorMarkToast.conflict)
															|| result
																	.get("status_code")
																	.toString()
																	.equals(ErrorMarkToast.yanzhengmaLate)) {
														submit.setText(R.string.zhuce_btn_hqyzm);
														time = 120;
														isTimeContinue = false;
														type = 1;
														zhuce_get_yanzhengma
																.setText(R.string.zhuce_btn_cxhqyzm);
													}
													
													if (result.keySet().contains(
															"status_code")) {
														if (result
																.get("status_code")
																.toString()
																.equals(ErrorMarkToast.unauthorized))
														{
															DialogManager.showDialogSimple(ZhuCeActivity.this, R.string.zhuce_yzmerror);
															return;
														}
													}
													
													ErrorMarkToast
															.showCallBackToast(
																	ZhuCeActivity.this,
																	result);

													return;
												}
												MySharePreference
														.save(ZhuCeActivity.this,
																MySharePreference.LOGINSTATE,
																phoneNumber
																		.getText()
																		.toString()
																		.trim());
												result.putAll(JsonUtil
														.getHashMap(result.get(
																"profile")
																.toString()));
												result.put(
														"key",
														JsonUtil.getOneValueFromJsonString(
																result.get(
																		"auth_token")
																		.toString(),
																"key"));
												result.put("password", pwd
														.getText().toString()
														.trim());
												MySharePreference
														.saveAccountZhuceInfo(
																ZhuCeActivity.this,
																result);
												MySharePreference.clearFarmInfo(ZhuCeActivity.this);
												// TODO 注册成功 设置昵称或者登录
												Intent intent = new Intent();
												intent.setClass(ZhuCeActivity.this, NickNameActivity.class);
												startActivity(intent);
												
												finish();
//												showDialogTwoButton(ZhuCeActivity.this,R.string.zhuce_setnickname,R.string.zhuce_nick_dialog_isnickname);
												isTimeContinue = false;
											} else {
												showToast(R.string.interneterror);
											}
										}
									});
							return;
						}
						// TODO 保存密码
						if (work == 2) {
							String para = NetUrls.URL_ZHUCE_USERNAME + "="
									+ phoneNumber.getText().toString().trim()
									+ "&" + "new_password" + "="
									+ pwd.getText().toString().trim() + "&"
									+ "code="
									+ yanZhengMa.getText().toString().trim();
							NetConnectionLoginZhuCe
									.ModifyPutSetNewPwdNetConnection(
											NetUrls.FINDPWD, para,
											new ZhuceSuccessCallBack() {
												@Override
												public void onSuccess(
														HashMap<String, Object> result) {
													submit.setClickable(true);
													submit.setText(R.string.zhuce_btn_resetPwd);
													if (result != null) {
														if (result
																.keySet()
																.contains(
																		"status_code")) {
															
															time = 120;
															isTimeContinue = false;
															type = 1;
															zhuce_get_yanzhengma
																	.setText(R.string.zhuce_btn_cxhqyzm);
															
															
																if (result
																		.get("status_code")
																		.toString()
																		.equals(ErrorMarkToast.unauthorized))
																{
																	DialogManager.showDialogSimple(ZhuCeActivity.this, R.string.zhuce_yzmerror);
																	return;
																}
															
															
																ErrorMarkToast
																.showCallBackToast(
																		ZhuCeActivity.this,
																		result);
															
															
															return;
														}
														result.putAll(JsonUtil
																.getHashMap(result
																		.get("profile")
																		.toString()));
														result.put(
																"key",
																JsonUtil.getOneValueFromJsonString(
																		result.get(
																				"auth_token")
																				.toString(),
																		"key"));
														result.put(
																"password",
																pwd.getText()
																		.toString()
																		.trim());
														MySharePreference
																.saveAccountLoginInfo(
																		ZhuCeActivity.this,
																		result);
														showDialog(
																ZhuCeActivity.this,
																R.string.resetPwd,
																R.string.zhuce_btn_resetPwd_sueecss);
														isTimeContinue = false;
													} else {
														showToast(R.string.interneterror);
													}
												}
											});
						}
					} else {
						if(work == 1)
						{
							submit.setText(R.string.zhuce_btn_newaccount);
						}
						else if(work == 2)
						{
							submit.setText(R.string.zhuce_btn_resetPwd);
						}
						DialogManager.showDialogSimple(ZhuCeActivity.this,
								//R.string.zhuce_error,
								R.string.zhuce_submit_farmart_error);
					}
				} else {
					if(work == 1)
					{
						submit.setText(R.string.zhuce_btn_newaccount);
					}
					else if(work == 2)
					{
						submit.setText(R.string.zhuce_btn_resetPwd);
					}
					DialogManager.showDialogSimple(ZhuCeActivity.this,
							//R.string.zhuce_error,
							R.string.zhuce_yzmempty);
				}
			}
		});
	}
//
//	/**
//	 * 注册成功 设置昵称或者登录
//	 */
//	private void showDialogTwoButton(Context context, int headStringId,
//			int contentStirngId) {
//		LoginActivity.addFarm(ZhuCeActivity.this);
//		final AlertDialog dlg = new AlertDialog.Builder(context).create();
//		dlg.setCancelable(false);
//		dlg.show();
//		Window window = dlg.getWindow();
//		// *** 主要就是在这里实现这种效果的.
//		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
//		window.setContentView(R.layout.tishi_show_dialog_twobtn);
//		((TextView) window.findViewById(R.id.dialog_head))
//				.setText(headStringId);
//		((TextView) window.findViewById(R.id.dialog_content))
//				.setText(contentStirngId);
//		// 为确认按钮添加事件,执行退出应用操作
//		Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
//		ok.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(ZhuCeActivity.this, NickNameActivity.class);
//				startActivity(intent);
//				dlg.dismiss();
//				finish();
//			}
//		});
//		Button cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
//		cancel.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				dlg.dismiss();
//				LoginActivity.firstOpenApp(ZhuCeActivity.this);
//				finish();
//			}
//		});
//
//	}

	private void showDialog(Context context, int headStringId,
			int contentStirngId) {
		final AlertDialog dlg = new AlertDialog.Builder(context).create();
		dlg.setCancelable(false);
		dlg.show();
		Window window = dlg.getWindow();
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.tishi_show_dialog);
		
		((TextView) window.findViewById(R.id.dialog_content))
				.setText(contentStirngId);
		// 为确认按钮添加事件,执行退出应用操作
		Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dlg.dismiss();
				finish();
			}
		});
	}

	/**
	 * 开始计时
	 */
	private void openTimeCount() {
		isTimeContinue = true;
		time = 120;
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (isTimeContinue && time > 0) {
					changeHandleNumber.sendEmptyMessage(0);
					SystemClock.sleep(1000);
					time -= 1;
				}
			}
		}).start();
	}

	/**
	 * 获取验证码
	 */
	private void getYanZhemgMa() {
		// 获取验证码
		if (!StringUtil.checkEditTextIsEmpty(phoneNumber)) {
			if (StringUtil.isMobileNO(phoneNumber.getText().toString().trim())) {
				String para = NetUrls.URL_ZHUCE_GETYAZHENGMA_PHONE + "="
						+ phoneNumber.getText().toString().trim();
				zhuce_get_yanzhengma.setClickable(false);
				NetConnectionLoginZhuCe.ZhuceYanZhengNetConnection(
						NetUrls.URL_ZHUCE_GETYAZHENGMA, para,
						new ZhuceSuccessCallBack() {
							@Override
							public void onSuccess(HashMap<String, Object> result) {
								zhuce_get_yanzhengma.setClickable(true);
								// 获取返回码失败
								if (result == null) {
									zhuce_get_yanzhengma
											.setText(R.string.zhuce_btn_cxhqyzm);
									showToast(R.string.interneterror);
								} else {
									if (result.get("status_code").equals(
											"000000")) {
										// 获取验证码成功
										type = 2;
										openTimeCount();
									} else {
										type = 1;
										// ErrorMarkToast
										// .showCallBackToast(
										// ZhuCeActivity.this,
										// result);
										DialogManager.showDialogSimple(
												ZhuCeActivity.this,
												//R.string.zhuce_error,
												R.string.zhuce_getyzmfail);
										zhuce_get_yanzhengma
												.setText(R.string.zhuce_btn_cxhqyzm);

									}
								}
							}
						});
			} else {
				DialogManager.showDialogSimple(ZhuCeActivity.this,
						//R.string.zhuce_error,
						R.string.zhuce_phoneform);
			}
		} else {
			DialogManager.showDialogSimple(ZhuCeActivity.this,
					//R.string.zhuce_error,
					R.string.zhuce_phoneempty);
		}
	}

	@Override
	protected void onDestroy() {
		isTimeContinue = false;
		super.onDestroy();
	}

	/**
	 * Toast提示框
	 * 
	 * @param id
	 */
	private void showToast(int id) {
		Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
	}

}
