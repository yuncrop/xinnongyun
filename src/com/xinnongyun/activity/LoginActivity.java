package com.xinnongyun.activity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xinnongyun.config.NetUrls;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.json.JsonUtil;
import com.xinnongyun.net.NetConnectionBeing;
import com.xinnongyun.net.NetConnectionBeing.BeingSuccessCallBackGetAll;
import com.xinnongyun.net.NetConnectionCamera;
import com.xinnongyun.net.NetConnectionCamera.CameraAllSuccessCallBack;
import com.xinnongyun.net.NetConnectionCollector;
import com.xinnongyun.net.NetConnectionCollector.CollectorAllSuccessCallBack;
import com.xinnongyun.net.NetConnectionFarm;
import com.xinnongyun.net.NetConnectionFarm.FarmSuccessCallBackList;
import com.xinnongyun.net.NetConnectionFarming;
import com.xinnongyun.net.NetConnectionFarming.FarmingSuccessCallBackGetAll;
import com.xinnongyun.net.NetConnectionJournal;
import com.xinnongyun.net.NetConnectionJournal.JournalListSuccessCallBack;
import com.xinnongyun.net.NetConnectionLoginZhuCe;
import com.xinnongyun.net.NetConnectionLoginZhuCe.ZhuceSuccessCallBack;
import com.xinnongyun.net.NetConnectionNotice;
import com.xinnongyun.net.NetConnectionNotice.NoticeAllSuccessCallBack;
import com.xinnongyun.net.NetConnectionSoil;
import com.xinnongyun.net.NetConnectionSoil.SoilSuccessCallBackGetAll;
import com.xinnongyun.net.NetConnectionWeatherStation;
import com.xinnongyun.net.NetConnectionWeatherStation.WeatherAllSuccessCallBack;
import com.xinnongyun.service.BootReceiver;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.BeingDaoDBManager;
import com.xinnongyun.sqlite.BlowerDaoDBManager;
import com.xinnongyun.sqlite.CameraDaoDBManager;
import com.xinnongyun.sqlite.CollectDaoDBManager;
import com.xinnongyun.sqlite.ControlCenterDaoDBManager;
import com.xinnongyun.sqlite.DBHelper;
import com.xinnongyun.sqlite.FarmDaoManager;
import com.xinnongyun.sqlite.FarmingDaoDBManager;
import com.xinnongyun.sqlite.JournalDaoDBManager;
import com.xinnongyun.sqlite.NoticeDaoDBManager;
import com.xinnongyun.sqlite.SoilDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.sqlite.WeatherStationDaoDBManager;
import com.xinnongyun.utils.ContactsUtils;
import com.xinnongyun.utils.DialogManager;
import com.xinnongyun.utils.StringUtil;

/**
 * 登录
 * 
 * @author sm
 * 
 */
public class LoginActivity extends Activity {

	private EditText phoneNumber, pwd;
	private Button login, findPwd, zhuCe;
	private static boolean isLoadInfo= false;
	
	private Handler pageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.arg1 == 1)
			{
				if (!MySharePreference.getValueFromKey(LoginActivity.this,
						MySharePreference.LOGINSTATE).equals(
						MySharePreference.NOTSTATE)) {
					firstOpenApp(LoginActivity.this);
					finish();
				}
				else
				{
			    //未登录  清空数据
				isLoadInfo = true;
//				FarmDaoManager farmDao = new FarmDaoManager(
//							LoginActivity.this);
//				farmDao.clearFarmTable();
				setContentView(R.layout.acticity_login);
				initView();
				}
			}
		}
	};
	private static int notifyid;
	private static String notice_id = null;

	
	
	final CountDownLatch signal = new CountDownLatch(1);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//EzvizAPI.getInstance().gotoLoginPage(false);
		// 
		DBHelper.getInstance(LoginActivity.this);
		
//		try {
////            GetCameraInfoList getCameraInfoList = new GetCameraInfoList();
////            getCameraInfoList.setPageSize(10);
////            getCameraInfoList.setPageStart(0);
////           
////            List<CameraInfo> result = EzvizAPI.getInstance().getCameraInfoList(getCameraInfoList);
////            CameraInfo cameraInfo =result.get(0);
////           System.out.println(cameraInfo.getCameraId()+"---camreaId");
////           System.out.println(cameraInfo.getCameraName()+"---CameraName");
////           System.out.println(cameraInfo.getCameraNo()+"---getCameraNo");
////           System.out.println(cameraInfo.getDeviceId()+"---getDeviceId");
////           System.out.println(cameraInfo.getIsEncrypt()+"---getIsEncrypt");
////           System.out.println(cameraInfo.getIsShared()+"---getIsShared");
////           System.out.println(cameraInfo.getPicUrl()+"---getPicUrl");
////           System.out.println(cameraInfo.getStatus()+"---getIsShared");
//			
//			
//			
//			 CameraInfo cameraInfo = new CameraInfo();
//			 cameraInfo.setCameraId("c84aad37ddd44a689e4b02c811c14d0e");
//			 cameraInfo.setCameraName("sdsd");
//			 //cameraInfo.setCameraNo(1);
//			 cameraInfo.setDeviceId("9f4d1c5498f24c12a7870b56070a07b8516157507");
//			 cameraInfo.setIsEncrypt(0);
//			 cameraInfo.setIsShared(0);
//			 cameraInfo.setPicUrl("https://i.ys7.com/assets/imgs/public/companyDevice.jpeg");
//			 cameraInfo.setStatus(0);
//			
//			
//			
//            Intent intent = new Intent(this, SimpleRealPlayActivity.class);
//            //Intent intent = new Intent(CameraListActivity.this, SimpleRealPlayActivity.class);
//            intent.putExtra(IntentConsts.EXTRA_CAMERA_INFO, cameraInfo);
//            startActivity(intent);
//            //System.out.println(result.size()+"");
//        } catch (Exception e) {
//        }
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏ActionBar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		         WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		Intent getIntentFrom = getIntent();
		if(getIntentFrom!=null)
		{
			notifyid = getIntent().getIntExtra("notify_id", 0);
			notice_id = getIntentFrom.getStringExtra(TablesColumns.TABLENOTICE_ID+"notice");
		}
		
		setContentView(R.layout.activity_splash);
		TextView textViewYear = (TextView) findViewById(R.id.textView_splash_year);
		textViewYear.setText(textViewYear.getText().toString() + Calendar.getInstance().get(Calendar.YEAR));
		new Thread(new Runnable() {

			@Override
			public void run() {
				SystemClock.sleep(YunTongXun.splashTime);
				Message msg = pageHandler.obtainMessage();
				msg.arg1 = 1;
				pageHandler.sendMessage(msg);
			}
		}).start();
		ExitApplication.getInstance().addActivity(this);
	}
	
	/**
	 * 初始化界面控件
	 */
	private void initView() {
		phoneNumber = (EditText) findViewById(R.id.login_et_sjhm);
		pwd = (EditText) findViewById(R.id.login_et_pwd);
		login = (Button) findViewById(R.id.login_btn_login);
		findPwd = (Button) findViewById(R.id.login_btn_zhmm);
		zhuCe = (Button) findViewById(R.id.login_btn_zc);
		// 登录
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!StringUtil.checkEditTextIsEmpty(phoneNumber)
						&& !StringUtil.checkEditTextIsEmpty(pwd)) {
					if (StringUtil.isMobileNO(phoneNumber.getText().toString()
							.trim())
							&& StringUtil.checkPwd(pwd.getText().toString()
									.trim())) {
						
						
						
						//判断外部存储卡是否存在  
		                if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  
		                    Toast.makeText(getApplicationContext(), "SD存储卡不存在！,无法使用", Toast.LENGTH_LONG).show();    
		                    return;  
		                }  
						
						String para = NetUrls.URL_LOGIN_ZHUCE_USERNAME + "="
								+ phoneNumber.getText().toString().trim() + "&"
								+ NetUrls.URL_LOGIN_ZHUCE_PASSWORD + "="
								+ pwd.getText().toString().trim();
						para += "&os_version="+Build.VERSION.SDK_INT+"&app_version=2";
						login.setClickable(false);
						findPwd.setClickable(false);
						zhuCe.setClickable(false);
						login.setText(R.string.login_logining);
						NetConnectionLoginZhuCe.LoginNetConnection(
								NetUrls.URL_LOGIN, para,
								new ZhuceSuccessCallBack() {
									@Override
									public void onSuccess(
											HashMap<String, Object> result) {
										if (result == null) {
											login.setClickable(true);
											findPwd.setClickable(true);
											zhuCe.setClickable(true);
											login.setText(R.string.login_login);
											MySharePreference
													.clearOneState(
															LoginActivity.this,
															MySharePreference.LOGINSTATE);
											Toast.makeText(LoginActivity.this,
													R.string.interneterror,
													Toast.LENGTH_SHORT).show();
										} else {
											// 登录成功
											if (result.keySet()
													.contains("user")) {
												// 登陆过 并且是同一个用户
//												if (MySharePreference.getValueFromKey(LoginActivity.this,MySharePreference.ACCOUNTID)
//														.equals(result.get(MySharePreference.ACCOUNTID).toString())) {
//													String jsonUser = result.get("user").toString();
//													result.putAll(JsonUtil.getHashMap(JsonUtil.getOneValueFromJsonString(jsonUser,"profile")));
//													result.putAll(JsonUtil.getHashMap(jsonUser));
//													result.put("password", pwd.getText().toString().trim());
//													result.put("key",JsonUtil.getHashMap(JsonUtil.getOneValueFromJsonString(jsonUser,"auth_token")).get("key").toString());
//													MySharePreference.saveAccountLoginInfo(LoginActivity.this,result);
//													MySharePreference.save(LoginActivity.this,MySharePreference.LOGINSTATE,phoneNumber.getText().toString().trim());
//													firstOpenApp(LoginActivity.this);
//													finish();
//													return;
//												} else {
													String jsonUser = result.get("user").toString();
													result.putAll(JsonUtil.getHashMap(JsonUtil.getOneValueFromJsonString(jsonUser,"profile")));
													result.putAll(JsonUtil.getHashMap(jsonUser));
													result.put("password", pwd.getText().toString().trim());
													result.put("key",JsonUtil.getHashMap(JsonUtil.getOneValueFromJsonString(jsonUser,"auth_token")).get("key").toString());
													MySharePreference.saveAccountLoginInfo(LoginActivity.this,result);
													MySharePreference.save(LoginActivity.this,MySharePreference.LOGINSTATE,phoneNumber.getText().toString().trim());
													MySharePreference.clearFarmInfo(LoginActivity.this);
													new Thread(new Runnable() {
														
														@Override
														public void run() {
															new ContactsUtils(LoginActivity.this, "Token " + MySharePreference.getValueFromKey(LoginActivity.this, MySharePreference.ACCOUNTTOKEN));
														}
													}).start();
													NetConnectionFarm.FarmGetMineNetConnection(NetUrls.URL_FARMMANAGER_GETMINE,null,"Token "
																			+ MySharePreference.getValueFromKey(LoginActivity.this,MySharePreference.ACCOUNTTOKEN),
																	new FarmSuccessCallBackList() {
																		@Override
																		public void onSuccess(
																				List<HashMap<String, Object>> result) {
																			login.setClickable(true);
																			findPwd.setClickable(true);
																			zhuCe.setClickable(true);
																			login.setText(R.string.login_login);
																			if (result != null) {
																				
																				if (result.get(0) != null) {
																					MySharePreference.saveFarmInfo(LoginActivity.this,result.get(0));
																					
																				} 
																				firstOpenApp(LoginActivity.this);
																				finish();
																				return;
																			}
																			MySharePreference
																					.clearOneState(
																							LoginActivity.this,
																							MySharePreference.LOGINSTATE);
																			// 登录失败
																			DialogManager
																					.showDialogSimple(
																							LoginActivity.this,
																							//R.string.login_fail,
																							R.string.login_fail);
																		}
																	});
													return;
//												}

											}
											login.setClickable(true);
											findPwd.setClickable(true);
											zhuCe.setClickable(true);
											login.setText(R.string.login_login);
											DialogManager.showDialogSimple(
													LoginActivity.this,
													//R.string.login_fail,
													R.string.login_fail);
											return;

										}
									}
								});
					} else {
						login.setClickable(true);
						findPwd.setClickable(true);
						zhuCe.setClickable(true);
						login.setText(R.string.login_login);
						DialogManager.showDialogSimple(LoginActivity.this,
								//R.string.login_fail_farmat,
								R.string.login_phonepwdform);
					}
				} else {
					login.setClickable(true);
					findPwd.setClickable(true);
					zhuCe.setClickable(true);
					login.setText(R.string.login_login);
					DialogManager.showDialogSimple(LoginActivity.this,
							//R.string.login_fail_farmat,
							R.string.login_phonepwdempty);
				}
			}
		});

		zhuCe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putExtra("work", 1);
				intent.setClass(LoginActivity.this, ZhuCeActivity.class);
				startActivity(intent);
			}
		});

		findPwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putExtra("work", 2);
				intent.setClass(LoginActivity.this, ZhuCeActivity.class);
				startActivity(intent);
			}
		});
	}

	/**
	 * 首次打开app
	 */
	public static void firstOpenApp(final Context context)
	{
		if(isLoadInfo)
		{
			FarmDaoManager farmDao = new FarmDaoManager(
					context);
		    farmDao.clearFarmTable(1);
			new Thread(new Runnable() {
				@Override
				public void run() {
					LoginActivity.addFarm(context);
				}
			}).start();
		}
		else
		{
			BootReceiver.openService(context);
		}
		
		if(!MySharePreference.getValueFromKey(context,
				MySharePreference.ISFIRSTOPEN)
				.equals(MySharePreference.NOTSTATE))
		{
			Intent intent = new Intent();
			if(notifyid!=0 && notice_id!=null)
			{
				intent.putExtra("notify_id", notifyid);
				intent.putExtra(TablesColumns.TABLENOTICE_ID+"notice",notice_id);
				intent.putExtra("currentpage", 2);
			}
			intent.setClass(
					context,
					MainActivity.class);
			context.startActivity(intent);
		}
		else
		{
			Intent intent = new Intent();
			intent.setClass(context, ShowActivity.class);
			context.startActivity(intent);
		}
		
	}
	
	
	// 加入农场 TODO
	public static void addFarm(final Context context) {
		if (!MySharePreference.getValueFromKey(context,
				MySharePreference.LOGINSTATE)
				.equals(MySharePreference.NOTSTATE)) {
			// 获取默认作物列表
			final BeingDaoDBManager manager = new BeingDaoDBManager(context);
			if (manager.getAllBeing(null).size() == 0) {
				NetConnectionBeing.BeingGetNetConnection(NetUrls.URL_BEINGS,
						null, null, new BeingSuccessCallBackGetAll() {
							@Override
							public void onSuccess(final List<HashMap<String, Object>> result) {
								if (result != null) {
									// 获取所有作物信息并保存
									new Thread(
											new Runnable() {
												@Override
												public void run() {
													manager.insertAllBeing(result);
												}
											}
											).start();
									
								}
							}
						});
			}
			// 获取默认土壤列表
			final SoilDaoDBManager dao = new SoilDaoDBManager(context);
			if (dao.getAllSoil().size() == 0) {
				NetConnectionSoil.CollectorGetNetConnection(NetUrls.URL_SOIL,
						null, null, new SoilSuccessCallBackGetAll() {
							@Override
							public void onSuccess(final List<HashMap<String, Object>> result) {
								if (result != null) {
									new Thread(
											new Runnable() {
												@Override
												public void run() {
													// 获取所有土壤信息并保存
													dao.insertAllSoil(result);
												}
											}
											).start();
								}
							}
						});
			}
			if (!MySharePreference.getValueFromKey(context,
					MySharePreference.FARMID)
					.equals(MySharePreference.NOTSTATE)) {
				// 自定义作物列表
				NetConnectionBeing.BeingGetNetConnection(
						NetUrls.URL_BEINGS_FARM,
						TablesColumns.TABLEBEING_FARM
								+ "="
								+ MySharePreference.getValueFromKey(context,
										MySharePreference.FARMID), null,
						new BeingSuccessCallBackGetAll() {

							@Override
							public void onSuccess(final List<HashMap<String, Object>> result) {
								if (result != null) {
									new Thread(
											new Runnable() {
												@Override
												public void run() {
													manager.insertAllBeing(result);
												}
											}
											).start();
									
								}
							}
						});
				// 获取农场所有盒子
				final CollectDaoDBManager collects = new CollectDaoDBManager(
						context);
				if (collects.getAllCollect().size() == 0) {
					NetConnectionCollector.CollectorGetAllNetConnection(
							NetUrls.URL_FARM_COLLECTS,
							TablesColumns.TABLECOLLOCTOR_FARM
									+ "="
									+ MySharePreference.getValueFromKey(
											context, MySharePreference.FARMID),
							"Token "
									+ MySharePreference.getValueFromKey(
											context,
											MySharePreference.ACCOUNTTOKEN),
							new CollectorAllSuccessCallBack() {

								@Override
								public void onSuccess(final List<HashMap<String, Object>> result) {
									if(result!=null)
									{
										new Thread(
												new Runnable() {
													@Override
													public void run() {
														collects.insertAllCollect(result);
													}
												}
												).start();
										
									}
									BootReceiver.openService(context);
								}
							});
					
					//获取农场视频日志
					final JournalDaoDBManager daoDBManager = new JournalDaoDBManager(context);
					if(daoDBManager.getAllJournalVideo().size() == 0)
					{
						NetConnectionJournal.JournalGetNetConnection(
								NetUrls.URL_VIDEO_CREATE,
								"farm="+MySharePreference.getValueFromKey(context, MySharePreference.FARMID),
								null,
								new JournalListSuccessCallBack() {
									
									@Override
									public void onSuccess(final List<HashMap<String, Object>> result) {
										if(result!=null)
										{
											new Thread(
													new Runnable() {
														@Override
														public void run() {
															daoDBManager.insertAllJournal(result);
														}
													}
													).start();
										}
									}
								});
					}
				}
				
				
				//获取环境小站列表
				final WeatherStationDaoDBManager weatherDbManager = new WeatherStationDaoDBManager(context);
				if(weatherDbManager.getAllWeatherStation().size()==0)
				{
					NetConnectionWeatherStation.WeatherGetAllNetConnection(
							NetUrls.URL_WEATHER_STATIONS(MySharePreference.getValueFromKey(context,MySharePreference.FARMID)),
							null, null, 
							new WeatherAllSuccessCallBack() {
								@Override
								public void onSuccess(final List<HashMap<String, Object>> result) {
									if (result != null) {
										new Thread(
												new Runnable() {
													@Override
													public void run() {
														weatherDbManager.insertAllWeatherStation(result);
													}
												}
												).start();
									}
								}
							}
							);
				}
				
				
				final CameraDaoDBManager cameraDao = new CameraDaoDBManager(context);
				if(cameraDao.getAllCameraList().size()==0)
				{
					NetConnectionCamera.CameraGetAllNetConnection(
							NetUrls.URI_CAMREAList(MySharePreference.getValueFromKey(context,MySharePreference.FARMID)), null, null, 
							new CameraAllSuccessCallBack() {
								@Override
								public void onSuccess(final List<HashMap<String, Object>> result) {
									if(result!=null)
									{
										new Thread(
												new Runnable() {
													@Override
													public void run() {
														cameraDao.insertAllCameras(result);
													}
												}
												).start();
									}
								}
							}
							);
				}
				
				// 获取农场种养记录
				final FarmingDaoDBManager farmingDao = new FarmingDaoDBManager(
						context);
				if (farmingDao.getAllFarming().size() == 0)
					NetConnectionFarming.FarmingGetAllNetConnection(
							NetUrls.URL_FARMING_GETALL(MySharePreference
									.getValueFromKey(context,
											MySharePreference.FARMID)),
							null,
							"Token "
									+ MySharePreference.getValueFromKey(
											context,
											MySharePreference.ACCOUNTTOKEN),
							new FarmingSuccessCallBackGetAll() {

								@Override
								public void onSuccess(final 
										List<HashMap<String, Object>> result) {
									if (result != null) {
										new Thread(
												new Runnable() {
													@Override
													public void run() {
														farmingDao.insertAllFarming(result);
													}
												}
												).start();
									}
								}
							});
				
			}
			
			//获取所有中控
			final ControlCenterDaoDBManager centerDaoDBManager = new ControlCenterDaoDBManager(context);
			if(centerDaoDBManager.getAllControlCenters().size() == 0)
			{
				NetConnectionFarming.FarmingGetAllNetConnection(
						NetUrls.getFarmAllControlsInfo(MySharePreference.getValueFromKey(context,MySharePreference.FARMID)), null, null,
						new FarmingSuccessCallBackGetAll() {
							
							@Override
							public void onSuccess(final List<HashMap<String, Object>> result) {
								if (result != null) {
									new Thread(
											new Runnable() {
												@Override
												public void run() {
													if(result.size()>0)
													{
														for(HashMap<String, Object> cMap : result)
														{
															//获取中控下所有放风机
															NetConnectionFarming.FarmingGetAllNetConnection(NetUrls.getAllBlowerFromControl(cMap.get("id").toString()), null, null, 
																	new FarmingSuccessCallBackGetAll() {
																		@Override
																		public void onSuccess(List<HashMap<String, Object>> result1) {
																			if(result1!=null && result1.size()>0)
																			{
																				BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(context);
																				blowerDaoDBManager.insertAllControlCenter(result1);
																			}
																		}
																	}
																	);
														}
														centerDaoDBManager.insertAllControlCenter(result);
													}
												}
											}
											).start();
								}
							}
						});
			}
			
			
			
			
			// 获取农场通知记录
			final NoticeDaoDBManager noticeDao = new NoticeDaoDBManager(
					context);
				NetConnectionNotice.NoticeGetAllNetConnection(
						NetUrls.URL_NOTICE_GETALL,
						null,
						"Token "
								+ MySharePreference.getValueFromKey(
										context,
										MySharePreference.ACCOUNTTOKEN),
						new NoticeAllSuccessCallBack() {
							@Override
							public void onSuccess(final List<HashMap<String, Object>> result) {
								if(result!=null)
								{
									new Thread(
											new Runnable() {
												@Override
												public void run() {
													noticeDao.insertAllNotice(result);
												}
											}
											).start();
								}
							}
						});
		}
	}

}
