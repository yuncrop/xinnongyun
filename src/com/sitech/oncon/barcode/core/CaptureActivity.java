package com.sitech.oncon.barcode.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.common.HybridBinarizer;
import com.sitech.oncon.barcode.camera.CameraManager;
import com.sitech.oncon.barcode.executor.ResultHandler;
import com.videogo.constant.IntentConsts;
import com.videogo.openapi.EzvizAPI;
import com.videogo.openapi.bean.resp.CameraInfo;
import com.videogo.widget.SimpleRealPlayActivity;
import com.xinnongyun.activity.BlowerDetailInfoSetting;
import com.xinnongyun.activity.BoxActivity;
import com.xinnongyun.activity.ErrorMarkToast;
import com.xinnongyun.activity.FarmConfigActivity;
import com.xinnongyun.activity.LoginActivity;
import com.xinnongyun.activity.MainActivity;
import com.xinnongyun.activity.R;
import com.xinnongyun.activity.WeatherStationActivity;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.net.NetConnectionCamera;
import com.xinnongyun.net.NetConnectionFarming;
import com.xinnongyun.net.NetConnectionCamera.CameraSuccessCallBack;
import com.xinnongyun.net.NetConnectionCollector;
import com.xinnongyun.net.NetConnectionCollector.CollectorSuccessCallBack;
import com.xinnongyun.net.NetConnectionFarm;
import com.xinnongyun.net.NetConnectionFarm.FarmSuccessCallBack;
import com.xinnongyun.net.NetConnectionFarming.FarmingSuccessCallBackGetAll;
import com.xinnongyun.net.NetConnectionWeatherStation;
import com.xinnongyun.net.NetConnectionWeatherStation.WeatherSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.BlowerDaoDBManager;
import com.xinnongyun.sqlite.BlowerTimeLineDaoDBManager;
import com.xinnongyun.sqlite.CameraDaoDBManager;
import com.xinnongyun.sqlite.CollectDaoDBManager;
import com.xinnongyun.sqlite.ControlCenterDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.sqlite.WeatherStationDaoDBManager;
import com.xinnongyun.utils.DialogManager;
import com.xinnongyun.utils.StringUtil;

@SuppressWarnings("deprecation")
public final class CaptureActivity extends Activity implements
		SurfaceHolder.Callback {
	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private TextView statusView;
	private ImageView common_title_TV_left;
	private TextView common_title_TV_right;
	private Result lastResult;
	private boolean hasSurface;
	private IntentSource source;
	private Collection<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private final int from_photo = 010;
	static final int PARSE_BARCODE_SUC = 3035;
	static final int PARSE_BARCODE_FAIL = 3036;
	String photoPath;
	ProgressDialog mProgress;

	public static String device_serial = "";
	enum IntentSource {
		ZXING_LINK, NONE
	}

	Handler barHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PARSE_BARCODE_SUC:
				showDialog((String) msg.obj);
				break;
			case PARSE_BARCODE_FAIL:
				if (mProgress != null && mProgress.isShowing()) {
					mProgress.dismiss();
				}
				DialogManager.showDialogSimple(CaptureActivity.this,
						R.string.barcode_msg_fail);
				break;
			}
			super.handleMessage(msg);
		}

	};

	ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏ActionBar
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		setContentView(R.layout.capture);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		cameraManager = new CameraManager(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);
		statusView = (TextView) findViewById(R.id.status_view);
		if (getIntent().getStringExtra(YunTongXun.CAPTURE_KEY).equals(
				YunTongXun.CAPTURE_ADDFARM)) {
			statusView.setText(R.string.msg_add_status);
		}
		common_title_TV_left = (ImageView) findViewById(R.id.common_title_TV_left);
		common_title_TV_right = (TextView) findViewById(R.id.common_title_TV_right);
		setListeners();
	}

	
	//TODO  扫描二维码结果
	public void showDialog(final String msg) {

		if (mProgress != null && mProgress.isShowing()) {
			mProgress.dismiss();
		}
		
		if((msg.startsWith(NetUrls.URL) || msg.startsWith(NetUrls.URL2)) && StringUtil.getFileNameFromUrl(msg).startsWith("A0"))
		{
			ControlCenterDaoDBManager centerDaoDBManager = new ControlCenterDaoDBManager(CaptureActivity.this);
			HashMap<String, String> cMap = centerDaoDBManager.getControlCenterBySerial(StringUtil.getFileNameFromUrl(msg));
			if(cMap!=null && cMap.size() >0)
			{
				BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(this);
				List<HashMap<String, String>> bMaps = blowerDaoDBManager.getAllBlowersByCCId(cMap.get(TablesColumns.TABLECONTROLCENTER_ID).toString());
				String ccBlowerIds = "";
				BlowerTimeLineDaoDBManager blowerTimeLineDaoDBManager = new BlowerTimeLineDaoDBManager(this);
				for(HashMap<String, String> map : bMaps)
				{
					HashMap<String, String>  bTMap = blowerTimeLineDaoDBManager.getCollectLastUpdateTime(map.get(TablesColumns.TABLEBLOWER_ID).toString());
					if(bTMap!=null && bTMap.size()>2)
					{
						ccBlowerIds += map.get(TablesColumns.TABLEBLOWER_ID) + " ";
					}
				}
				Intent intent = new Intent();
				intent.setClass(this, BlowerDetailInfoSetting.class);
				intent.putExtra("id", cMap.get(TablesColumns.TABLECONTROLCENTER_ID).toString());
				intent.putExtra("ccName", cMap.get(TablesColumns.TABLECONTROLCENTER_NAME).toString());
				intent.putExtra("ccBlowerIds", ccBlowerIds.trim());
				intent.putExtra("serial", cMap.get(TablesColumns.TABLECONTROLCENTER_SERIAL).toString());
				startActivity(intent);
				finish();
			}
			else
			{
				NetConnectionCamera.CameraGetNetConnection("http://m.nnong.com/v2/api-automator/cc/?serial=" +StringUtil.getFileNameFromUrl(msg), null, null, 
						new CameraSuccessCallBack() {
							@Override
							public void onSuccess(final HashMap<String, Object> result) {
								if (result != null) {
									if (result.keySet().contains("status_code") || !result.keySet().contains("produced_at")) {
										Toast.makeText(CaptureActivity.this, "网络加载失败", Toast.LENGTH_SHORT).show();
										finish();
										return;
									}
									if(result.get("farm")==null || result.get("farm").toString().contains("null"))
									{
										final AlertDialog dlg = new AlertDialog.Builder(
												CaptureActivity.this)
												.create();
										dlg.setCancelable(false);
										dlg.show();
										Window window = dlg.getWindow();
										window.setContentView(R.layout.tishi_show_dialog_twobtn);
										((TextView) window
												.findViewById(R.id.dialog_content))
												.setText(R.string.add_collect_to_farm);
										Button ok = (Button) window
												.findViewById(R.id.dialog_btn_submit);
										ok.setOnClickListener(new View.OnClickListener() {
											public void onClick(View v) {
												dlg.dismiss();
												// 加入农场
												NetConnectionCamera.CameraPostNetConnection("http://m.nnong.com/v2/api-automator/cc/" + result.get("id").toString() + "/mount/",
														"farm=" + MySharePreference.getValueFromKey(CaptureActivity.this, MySharePreference.FARMID),
														"Token "+MySharePreference.getValueFromKey(CaptureActivity.this, MySharePreference.ACCOUNTTOKEN), 
														new CameraSuccessCallBack() {
															@Override
															public void onSuccess(HashMap<String, Object> result) {
																if(result!=null)
																{
																	if(result.keySet().size()>5)
																	{
																		ControlCenterDaoDBManager ccDao = new ControlCenterDaoDBManager(CaptureActivity.this);
																		ArrayList<HashMap<String, Object>> listCam = new ArrayList<HashMap<String, Object>>();
																		listCam.add(result);
																		ccDao.insertAllControlCenter(listCam);
																		//获取中控下所有放风机
																		NetConnectionFarming.FarmingGetAllNetConnection(NetUrls.getAllBlowerFromControl(result.get("id").toString()), null, null, 
																				new FarmingSuccessCallBackGetAll() {
																					@Override
																					public void onSuccess(List<HashMap<String, Object>> result1) {
																						if(result1!=null && result1.size()>0)
																						{
																							BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(CaptureActivity.this);
																							blowerDaoDBManager.insertAllControlCenter(result1);
																						}
																					}
																				}
																				);
																		Toast.makeText(CaptureActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
																		finish();
																	}
																	else
																	{
																		Toast.makeText(CaptureActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
																		finish();
																	}
																}else
																{
																	Toast.makeText(CaptureActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
																	finish();
																}
															}
														});
											}
										});
										Button cancel = (Button) window
												.findViewById(R.id.dialog_btn_cancel);
										cancel.setOnClickListener(new View.OnClickListener() {
											public void onClick(View v) {
												dlg.dismiss();
												restartPreviewAfterDelay(0L);
												return;
											}
										});
										
									}
									else
									{
										if (!result
												.get("farm")
												.toString()
												.equals(MySharePreference
														.getValueFromKey(
																CaptureActivity.this,
																MySharePreference.FARMID))) {

											// 提示用户连接usb
											final AlertDialog dlg = new AlertDialog.Builder(
													CaptureActivity.this)
													.create();
											dlg.show();
											Window window = dlg.getWindow();
											// *** 主要就是在这里实现这种效果的.
											// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
											window.setContentView(R.layout.tishi_show_dialog);
											((TextView) window
													.findViewById(R.id.dialog_content))
													.setText(R.string.farmconfig_collect_notinyoufarm);
											// 为确认按钮添加事件,执行退出应用操作
											Button ok = (Button) window
													.findViewById(R.id.dialog_btn_submit);
											ok.setOnClickListener(new View.OnClickListener() {
												public void onClick(View v) {
													dlg.dismiss();
													restartPreviewAfterDelay(0L);
												}
											});
										}
									}
								}
							}
						}
						);
			}
			return;
		}
		
		
		if(msg.contains("www.ys7.com"))
		{
			try {
				String cameraSerial = msg.replace("http://", "").replace("www.ys7.com", "").substring(0, 9);
				device_serial = cameraSerial;
				CameraDaoDBManager cameraDaoDBManager = new CameraDaoDBManager(this);
				HashMap<String, String> cMap = cameraDaoDBManager.getCameraInfoBySerial(cameraSerial);
				if(cMap!=null && cMap.keySet().size()>0)
				{
					 CameraInfo cameraInfo = new CameraInfo();
					 cameraInfo.setCameraId(cMap.get(TablesColumns.TABLECAMERA_ID).toString());
					 cameraInfo.setCameraName(cMap.get(TablesColumns.TABLECAMERA_NAME).toString());
					 //cameraInfo.setCameraNo(1);
					 cameraInfo.setDeviceId(cMap.get(TablesColumns.TABLECAMERA_DEVICEID).toString());
					 cameraInfo.setIsEncrypt(0);
					 cameraInfo.setIsShared(0);
					 cameraInfo.setPicUrl(cMap.get(TablesColumns.TABLECAMERA_COVER_URL1).toString());
					 cameraInfo.setStatus(0);
					 Intent intent = new Intent(this, SimpleRealPlayActivity.class);
					 intent.putExtra("share_url", cMap.get(TablesColumns.TABLECAMERA_SHARE_URL).toString());
					 intent.putExtra("isPublish", cMap.get(TablesColumns.TABLECAMERA_PUBLISH).toString());
		             intent.putExtra(IntentConsts.EXTRA_CAMERA_INFO, cameraInfo);
		             startActivity(intent);
		             finish();
		             return;
				}else
				{
					
					NetConnectionCamera.CameraGetNetConnection("http://m.nnong.com/v2/api-camera/camera/?device_serial=" +cameraSerial, null, null, 
							new CameraSuccessCallBack() {
								@Override
								public void onSuccess(final HashMap<String, Object> result) {
									if (result != null) {
										if (result.keySet().contains("status_code") || result.keySet().size()<5) {
											EzvizAPI.getInstance().gotoAddDevicePage(device_serial, "1.0");
											finish();
											return;
										}
										if(result.get("farm")==null || result.get("farm").toString().contains("null"))
										{
											final AlertDialog dlg = new AlertDialog.Builder(
													CaptureActivity.this)
													.create();
											dlg.setCancelable(false);
											dlg.show();
											Window window = dlg.getWindow();
											window.setContentView(R.layout.tishi_show_dialog_twobtn);
											((TextView) window
													.findViewById(R.id.dialog_content))
													.setText(R.string.add_collect_to_farm);
											Button ok = (Button) window
													.findViewById(R.id.dialog_btn_submit);
											ok.setOnClickListener(new View.OnClickListener() {
												public void onClick(View v) {
													dlg.dismiss();
													// 摄像头加入农场
													NetConnectionCamera.CameraPutNetConnection("http://m.nnong.com/v2/api-camera/" + result.get("id").toString() + "/mount/",
															"farm=" + MySharePreference.getValueFromKey(CaptureActivity.this, MySharePreference.FARMID),
															"Token "+MySharePreference.getValueFromKey(CaptureActivity.this, MySharePreference.ACCOUNTTOKEN), 
															new CameraSuccessCallBack() {
																@Override
																public void onSuccess(HashMap<String, Object> result) {
																	if(result!=null)
																	{
																		if(result.keySet().size()>5)
																		{
																			CameraDaoDBManager cameraDaoDBManager = new CameraDaoDBManager(CaptureActivity.this);
																			ArrayList<HashMap<String, Object>> listCam = new ArrayList<HashMap<String, Object>>();
																			listCam.add(result);
																			cameraDaoDBManager.insertAllCameras(listCam);
																			Toast.makeText(CaptureActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
																			finish();
																		}
																		else
																		{
																			Toast.makeText(CaptureActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
																			finish();
																		}
																	}else
																	{
																		Toast.makeText(CaptureActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
																		finish();
																	}
																}
															});
												}
											});
											Button cancel = (Button) window
													.findViewById(R.id.dialog_btn_cancel);
											cancel.setOnClickListener(new View.OnClickListener() {
												public void onClick(View v) {
													dlg.dismiss();
													restartPreviewAfterDelay(0L);
													return;
												}
											});
											
										}
										else
										{
											if (!result
													.get("farm")
													.toString()
													.equals(MySharePreference
															.getValueFromKey(
																	CaptureActivity.this,
																	MySharePreference.FARMID))) {

												// 提示用户连接usb
												final AlertDialog dlg = new AlertDialog.Builder(
														CaptureActivity.this)
														.create();
												dlg.show();
												Window window = dlg.getWindow();
												// *** 主要就是在这里实现这种效果的.
												// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
												window.setContentView(R.layout.tishi_show_dialog);
												((TextView) window
														.findViewById(R.id.dialog_content))
														.setText(R.string.farmconfig_collect_notinyoufarm);
												// 为确认按钮添加事件,执行退出应用操作
												Button ok = (Button) window
														.findViewById(R.id.dialog_btn_submit);
												ok.setOnClickListener(new View.OnClickListener() {
													public void onClick(View v) {
														dlg.dismiss();
														restartPreviewAfterDelay(0L);
													}
												});
											}
										}
									}
									else
									{
										EzvizAPI.getInstance().gotoAddDevicePage(device_serial, "1.0");
										finish();
										return;
									}
								}
							}
							);
				}
			} catch (Exception e) {
				
			}
			return;
		}
		
		
		
		if (msg.startsWith(NetUrls.URL) || msg.startsWith(NetUrls.URL2)) {
			if(StringUtil
					.getFileNameFromUrl(msg).startsWith("D0"))
			{
				if (!MySharePreference.getValueFromKey(CaptureActivity.this,
						MySharePreference.FARMID)
						.equals(MySharePreference.NOTSTATE))
				{
					 WeatherStationDaoDBManager stationDaoDBManager = new WeatherStationDaoDBManager(CaptureActivity.this);
					 List<HashMap<String, String>> stations = stationDaoDBManager.getAllWeatherStation();
					 if(stations!=null && stations.size()!=0)
					 {
						 if(msg.contains(stations.get(0).get("serial").toString()))
						 {
							 Intent intentWeather = new Intent();
							 intentWeather.setClass(CaptureActivity.this, WeatherStationActivity.class);
							 startActivity(intentWeather);
							 finish();
						 }else
						 {
							 Toast.makeText(CaptureActivity.this, "每个农场只能有一个环境小站", Toast.LENGTH_SHORT).show();
							 finish();
						 }
						 return;
					 }	
				}
				
				
				NetConnectionWeatherStation.WeatherGetNetConnection("http://m.nnong.com/v2/api-weather-station/sensor/?serial="+StringUtil
						.getFileNameFromUrl(msg), null, null,
						new WeatherSuccessCallBack() {
							@Override
							public void onSuccess(final HashMap<String, Object> result) {
								if (result != null) {
									if (result.keySet().contains("status_code")) {
										ErrorMarkToast.showCallBackToast(
												CaptureActivity.this, result);
										finish();
										return;
									}
									if(result.get("farm")==null || result.get("farm").toString().contains("null"))
									{
										if (!MySharePreference.getValueFromKey(CaptureActivity.this,
												MySharePreference.FARMID)
												.equals(MySharePreference.NOTSTATE))
										{
											// 加入农场
											final AlertDialog dlg = new AlertDialog.Builder(
													CaptureActivity.this)
													.create();
											dlg.setCancelable(false);
											dlg.show();
											Window window = dlg.getWindow();
											window.setContentView(R.layout.tishi_show_dialog_twobtn);
											((TextView) window
													.findViewById(R.id.dialog_content))
													.setText(R.string.add_collect_to_farm);
											Button ok = (Button) window
													.findViewById(R.id.dialog_btn_submit);
											ok.setOnClickListener(new View.OnClickListener() {
												public void onClick(View v) {
													
													NetConnectionWeatherStation.WeatherPutNetConnection("http://m.nnong.com/v2/api-weather-station/"+ result.get("id").toString()+"/mount/",
															"farm="+MySharePreference.getValueFromKey(CaptureActivity.this,MySharePreference.FARMID),
															"Token " + MySharePreference.getValueFromKey(CaptureActivity.this,MySharePreference.ACCOUNTTOKEN),
															new WeatherSuccessCallBack()
													        {
																@Override
																public void onSuccess(
																		HashMap<String, Object> result) {
																	dlg.dismiss();
																	if (result != null) {
																		if (result.keySet().contains("status_code")) {
																			ErrorMarkToast.showCallBackToast(
																					CaptureActivity.this, result);
																			finish();
																			return;
																		}
																		else
																		{
																			//将小站加入自己农场
																			WeatherStationDaoDBManager weaDao = new WeatherStationDaoDBManager(CaptureActivity.this);
																			List<HashMap<String, Object>> listDate = new ArrayList<HashMap<String, Object>>();
																			listDate.add(result);
																			weaDao.insertAllWeatherStation(listDate);
																			Intent intentWeather = new Intent();
																			 intentWeather.setClass(CaptureActivity.this, WeatherStationActivity.class);
																			 startActivity(intentWeather);
																			 finish();
																		}
																	}
																	else
																	{
																		Toast.makeText(CaptureActivity.this,
																				R.string.interneterror,
																				Toast.LENGTH_SHORT).show();
																	}
																}
														
													        });
													
												}
											});
											Button cancel = (Button) window
													.findViewById(R.id.dialog_btn_cancel);
											cancel.setOnClickListener(new View.OnClickListener() {
												public void onClick(View v) {
													dlg.dismiss();
													restartPreviewAfterDelay(0L);
													return;
												}
											});
										}
										else
										{
											// 新建农场
											final AlertDialog dlg = new AlertDialog.Builder(
													CaptureActivity.this)
													.create();
											dlg.setCancelable(false);
											dlg.show();
											Window window = dlg.getWindow();
											window.setContentView(R.layout.tishi_show_dialog_twobtn);
											((TextView) window
													.findViewById(R.id.dialog_content))
													.setText(R.string.new_collect_to_farm);
											Button ok = (Button) window
													.findViewById(R.id.dialog_btn_submit);
											ok.setOnClickListener(new View.OnClickListener() {
												public void onClick(View v) {
													dlg.dismiss();
													Intent intent = new Intent();
													intent.setClass(
															CaptureActivity.this,
															FarmConfigActivity.class);
													startActivity(intent);
													finish();
												}
											});
											Button cancel = (Button) window
													.findViewById(R.id.dialog_btn_cancel);
											cancel.setOnClickListener(new View.OnClickListener() {
												public void onClick(View v) {
													dlg.dismiss();
													finish();
													return;
												}
											});
										}
									}else
									{
										if (!MySharePreference.getValueFromKey(CaptureActivity.this,MySharePreference.FARMID).equals(MySharePreference.NOTSTATE) &&
											!MySharePreference.getValueFromKey(CaptureActivity.this,MySharePreference.FARMID).equals(result.get("farm").toString()))
										{
											final AlertDialog dlg = new AlertDialog.Builder(
													CaptureActivity.this)
													.create();
											dlg.show();
											Window window = dlg.getWindow();
											// *** 主要就是在这里实现这种效果的.
											// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
											window.setContentView(R.layout.tishi_show_dialog);
											((TextView) window
													.findViewById(R.id.dialog_content))
													.setText(R.string.farmconfig_collect_notinyoufarm);
											// 为确认按钮添加事件,执行退出应用操作
											Button ok = (Button) window
													.findViewById(R.id.dialog_btn_submit);
											ok.setOnClickListener(new View.OnClickListener() {
												public void onClick(View v) {
													dlg.dismiss();
													restartPreviewAfterDelay(0L);
												}
											});
										}
									}
									
									
									
								}
								else {
									Toast.makeText(CaptureActivity.this,
											R.string.interneterror,
											Toast.LENGTH_SHORT).show();
								}
							}
						}
						);
				   return;
			}
			
			// 自己有农场
			if (!MySharePreference.getValueFromKey(CaptureActivity.this,
					MySharePreference.FARMID)
					.equals(MySharePreference.NOTSTATE)) {
				CollectDaoDBManager dao = new CollectDaoDBManager(
						CaptureActivity.this);
				List<HashMap<String, String>> datas = dao.getAllCollect();
				if (datas != null && datas.size() > 0) {
					for (HashMap<String, String> map : datas) {
						if (map.containsValue(msg)) {
							Intent intent = new Intent();
							intent.setClass(CaptureActivity.this,
									BoxActivity.class);
							Iterable<String> keys = map.keySet();
							intent.putExtra("type", "ModifyFarm");
							for (String name : keys) {
								intent.putExtra(name, map.get(name).toString());
							}
							startActivity(intent);
							finish();
							return;
						}
					}
				}
				// 查询本地是否存在盒子信息
			}

			// 如果本地没有 则获取盒子网络信息
			NetConnectionCollector.CollectorGetNetConnection(
					NetUrls.URL_COLLECTOR_GET(StringUtil
							.getFileNameFromUrl(msg)),
					null,
					"Token "
							+ MySharePreference.getValueFromKey(
									CaptureActivity.this,
									MySharePreference.ACCOUNTTOKEN),
					new CollectorSuccessCallBack() {
						@Override
						public void onSuccess(
								final HashMap<String, Object> result) {
							if (result != null) {
								if (result.keySet().contains("status_code")) {
									ErrorMarkToast.showCallBackToast(
											CaptureActivity.this, result);
									finish();
									return;
								}

								// 如果盒子没有网关
								if (result.get("gateway") == null
										|| (result.get("gateway") != null && result
												.get("gateway").toString()
												.equals("null"))) {
									// 提示用户连接usb
									final AlertDialog dlg = new AlertDialog.Builder(
											CaptureActivity.this).create();
									dlg.show();
									Window window = dlg.getWindow();
									// *** 主要就是在这里实现这种效果的.
									// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
									window.setContentView(R.layout.tishi_show_dialog);
									((TextView) window
											.findViewById(R.id.dialog_content))
											.setText(R.string.farmconfig_collect_notconnectusb);
									// 为确认按钮添加事件,执行退出应用操作
									Button ok = (Button) window
											.findViewById(R.id.dialog_btn_submit);
									ok.setOnClickListener(new View.OnClickListener() {
										public void onClick(View v) {
											dlg.dismiss();
											finish();
										}
									});
									return;
								}
								// 有网关
								else {
									// 盒子不属于农场
									if (result.get("farm") == null
											|| (result.get("farm") != null && result
													.get("farm").toString()
													.equals("null"))) {
										// 本地有农场
										if (!MySharePreference
												.getValueFromKey(
														CaptureActivity.this,
														MySharePreference.FARMID)
												.equals(MySharePreference.NOTSTATE)) {
											// 加入农场
											final AlertDialog dlg = new AlertDialog.Builder(
													CaptureActivity.this)
													.create();
											dlg.setCancelable(false);
											dlg.show();
											Window window = dlg.getWindow();
											window.setContentView(R.layout.tishi_show_dialog_twobtn);
											((TextView) window
													.findViewById(R.id.dialog_content))
													.setText(R.string.add_collect_to_farm);
											Button ok = (Button) window
													.findViewById(R.id.dialog_btn_submit);
											ok.setOnClickListener(new View.OnClickListener() {
												public void onClick(View v) {
													dlg.dismiss();
													Intent intent = new Intent();
													intent.setClass(
															CaptureActivity.this,
															BoxActivity.class);
													Iterable<String> keys = result
															.keySet();
													intent.putExtra("type",
															"addFarm");
													for (String name : keys) {
														intent.putExtra(
																name,
																result.get(name)
																		.toString());
													}
													startActivity(intent);
													finish();
												}
											});
											Button cancel = (Button) window
													.findViewById(R.id.dialog_btn_cancel);
											cancel.setOnClickListener(new View.OnClickListener() {
												public void onClick(View v) {
													dlg.dismiss();
													restartPreviewAfterDelay(0L);
													return;
												}
											});
										} else {
											// 新建农场
											final AlertDialog dlg = new AlertDialog.Builder(
													CaptureActivity.this)
													.create();
											dlg.setCancelable(false);
											dlg.show();
											Window window = dlg.getWindow();
											window.setContentView(R.layout.tishi_show_dialog_twobtn);
											((TextView) window
													.findViewById(R.id.dialog_content))
													.setText(R.string.new_collect_to_farm);
											Button ok = (Button) window
													.findViewById(R.id.dialog_btn_submit);
											ok.setOnClickListener(new View.OnClickListener() {
												public void onClick(View v) {
													dlg.dismiss();
													Intent intent = new Intent();
													intent.setClass(
															CaptureActivity.this,
															FarmConfigActivity.class);
													startActivity(intent);
													finish();
												}
											});
											Button cancel = (Button) window
													.findViewById(R.id.dialog_btn_cancel);
											cancel.setOnClickListener(new View.OnClickListener() {
												public void onClick(View v) {
													dlg.dismiss();
													finish();
													return;
												}
											});

										}
									}
									// 属于农场
									else {
										// 本地有农场
										if (!MySharePreference
												.getValueFromKey(
														CaptureActivity.this,
														MySharePreference.FARMID)
												.equals(MySharePreference.NOTSTATE)) {
											// 不是你一个农场
											if (!result
													.get("farm")
													.toString()
													.equals(MySharePreference
															.getValueFromKey(
																	CaptureActivity.this,
																	MySharePreference.FARMID))) {

												// 提示用户连接usb
												final AlertDialog dlg = new AlertDialog.Builder(
														CaptureActivity.this)
														.create();
												dlg.show();
												Window window = dlg.getWindow();
												// *** 主要就是在这里实现这种效果的.
												// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
												window.setContentView(R.layout.tishi_show_dialog);
												((TextView) window
														.findViewById(R.id.dialog_content))
														.setText(R.string.farmconfig_collect_notinyoufarm);
												// 为确认按钮添加事件,执行退出应用操作
												Button ok = (Button) window
														.findViewById(R.id.dialog_btn_submit);
												ok.setOnClickListener(new View.OnClickListener() {
													public void onClick(View v) {
														dlg.dismiss();
														restartPreviewAfterDelay(0L);
													}
												});
											}
											// 是你一个农场
											if (result
													.get("farm")
													.toString()
													.equals(MySharePreference
															.getValueFromKey(
																	CaptureActivity.this,
																	MySharePreference.FARMID))) {
												CollectDaoDBManager dao = new CollectDaoDBManager(
														CaptureActivity.this);
												List<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
												listData.add(result);
												dao.insertAllCollect(listData);
												Intent intent = new Intent();
												intent.setClass(
														CaptureActivity.this,
														BoxActivity.class);
												Iterable<String> keys = result
														.keySet();
												intent.putExtra("type",
														"ModifyFarm");
												for (String name : keys) {
													intent.putExtra(name,
															result.get(name)
																	.toString());
												}
												startActivity(intent);
												finish();
												return;
											}
										} else {
											// 加入该农场
											final AlertDialog dlg = new AlertDialog.Builder(
													CaptureActivity.this)
													.create();
											dlg.setCancelable(false);
											dlg.show();
											Window window = dlg.getWindow();
											window.setContentView(R.layout.tishi_show_dialog_twobtn);
											((TextView) window
													.findViewById(R.id.dialog_content))
													.setText(R.string.farmconfig_collect_joinfarm);
											Button ok = (Button) window
													.findViewById(R.id.dialog_btn_submit);
											ok.setOnClickListener(new View.OnClickListener() {
												public void onClick(View v) {
													// 加载农场信息
													NetConnectionFarm
															.FarmPutNetConnection(
																	NetUrls.URL_FARM_CREATE
																			+ result.get(TablesColumns.TABLECOLLOCTOR_FARM)
																			+ "/join/",
																	null,
																	"Token "
																			+ MySharePreference
																					.getValueFromKey(
																							CaptureActivity.this,
																							MySharePreference.ACCOUNTTOKEN),
																	new FarmSuccessCallBack() {
																		@Override
																		public void onSuccess(
																				HashMap<String, Object> addresult) {
																			if (addresult != null) {
																				if (addresult
																						.keySet()
																						.contains(
																								"status_code")) {
																					ErrorMarkToast
																							.showCallBackToast(
																									CaptureActivity.this,
																									addresult);
																					restartPreviewAfterDelay(0L);
																					return;
																				}

																				MySharePreference
																						.saveFarmInfo(
																								CaptureActivity.this,
																								addresult);
																				Intent intent2 = new Intent();
																				intent2.setAction(MainActivity.farmConfigbroastCast);
																				sendBroadcast(intent2);
																				LoginActivity
																						.addFarm(CaptureActivity.this);
																				setResult(10);
																			}
																		}
																	});

													dlg.dismiss();
													finish();
												}
											});
											Button cancel = (Button) window
													.findViewById(R.id.dialog_btn_cancel);
											cancel.setOnClickListener(new View.OnClickListener() {
												public void onClick(View v) {
													dlg.dismiss();
													restartPreviewAfterDelay(0L);
													return;
												}
											});
										}
									}
								}

							} else {
								Toast.makeText(CaptureActivity.this,
										R.string.interneterror,
										Toast.LENGTH_SHORT).show();
							}
						}
					}

			);

		} else {
			final AlertDialog dlg = new AlertDialog.Builder(
					CaptureActivity.this).create();
			dlg.setCancelable(false);
			dlg.show();
			Window window = dlg.getWindow();
			window.setContentView(R.layout.tishi_show_dialog_twobtn);
			((TextView) window.findViewById(R.id.dialog_content))
					.setText(R.string.barcode_one_dimen_success);
			// 为确认按钮添加事件,执行退出应用操作
			Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
			ok.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					
					dlg.dismiss();

				}
			});
			Button cancel = (Button) window
					.findViewById(R.id.dialog_btn_cancel);
			cancel.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					dlg.dismiss();
					finish();
				}
			});

		}

	}

	

	public void setListeners() {
		common_title_TV_left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CaptureActivity.this.finish();
			}
		});
		common_title_TV_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StringUtils.showPictures(CaptureActivity.this, from_photo);
			}
		});
	}

	public String parsLocalPic(String path) {
		String parseOk = null;
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF8");
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 先获取原大小
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false; // 获取新的大小
		// 缩放比
		int be = (int) (options.outHeight / (float) 400);
		if (be <= 0)
			be = 1;
		options.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(path, options);
	
	
		 Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),  
                 Config.RGB_565);  
         // int brightness = progress - 127;  
         float contrast = 4;  
         ColorMatrix cMatrix = new ColorMatrix();  
         cMatrix.set(new float[] { contrast, 0, 0, 0, 0, 0,  
                 contrast, 0, 0, 0,// 改变对比度  
                 0, 0, contrast, 0, 0, 0, 0, 0, 1, 0 });  

         Paint paint = new Paint();  
         paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));  

         Canvas canvas = new Canvas(bmp);  
         // 在Canvas上绘制一个已经存在的Bitmap。这样，dstBitmap就和srcBitmap一摸一样了  
         canvas.drawBitmap(bitmap, 0, 0, paint);  
		
		RGBLuminanceSource source = new RGBLuminanceSource(bitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader2 = new QRCodeReader();
		Result result;
		try {
			result = reader2.decode(bitmap1, hints);
			parseOk = result.getText();

		} catch (NotFoundException e) {
			parseOk = null;
		} catch (ChecksumException e) {
			parseOk = null;
		} catch (FormatException e) {
			parseOk = null;
		}
		return parseOk;

	}

	public  Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();
		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

	@SuppressWarnings("null")
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			try {
				mProgress = new ProgressDialog(CaptureActivity.this);
				mProgress.setMessage("正在扫描...");
				mProgress.setCanceledOnTouchOutside(false);
				mProgress.show();
				if (requestCode == from_photo) {
					if (resultCode == RESULT_OK) {
						Cursor cursor = null;
						try {
							cursor = getContentResolver().query(data.getData(),
									null, null, null, null);
							if (cursor.moveToFirst()) {
								photoPath = cursor.getString(cursor
										.getColumnIndex(MediaStore.Images.Media.DATA));
							}
							
						} catch (Exception e) { 
							Uri contentUri = data.getData();  
						    String wholeID = DocumentsContract.getDocumentId(contentUri);
						    String id = wholeID.split(":")[1];
						    String[] column = { MediaStore.Images.Media.DATA };
						    String sel = MediaStore.Images.Media._ID + "=?";
						    cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
						            sel, new String[] { id }, null);
						    int columnIndex = cursor.getColumnIndex(column[0]);
						    if (cursor.moveToFirst()) {
						    	photoPath = cursor.getString(columnIndex);
						    }
						    cursor.close();
						}finally{
							if(cursor == null)
							{
								cursor.close();
							}
						}
						new Thread(new Runnable() {
							@Override
							public void run() {
								Looper.prepare();
								String result = parsLocalPic(photoPath);
								if (result != null) {
									Message m = Message.obtain();
									m.what = PARSE_BARCODE_SUC;
									m.obj = result;
									barHandler.sendMessage(m);
								} else {
									Message m = Message.obtain();
									m.what = PARSE_BARCODE_FAIL;
									m.obj = "扫描失败！";
									barHandler.sendMessage(m);
								}
								Looper.loop();
							}
						}).start();
					}
				}
			} catch (Exception e) {
				Message m = Message.obtain();
				m.what = PARSE_BARCODE_FAIL;
				m.obj = "扫描失败！";
				barHandler.sendMessage(m);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		handler = null;
		lastResult = null;
		resetStatusView();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		inactivityTimer.onResume();
		source = IntentSource.NONE;

		decodeFormats = null;
	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		if (mProgress != null) {
			mProgress.dismiss();
		}
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK)
					&& lastResult != null) {
				restartPreviewAfterDelay(0L);
				return true;
			}
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			cameraManager.setTorch(false);
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			cameraManager.setTorch(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 这里初始化界面，调用初始化相机
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	private static ParsedResult parseResult(Result rawResult) {
		return ResultParser.parseResult(rawResult);
	}

	/** 解析二维码 */
	public void handleDecode(Result rawResult, Bitmap barcode) {
		inactivityTimer.onActivity();
		lastResult = rawResult;
		ResultHandler resultHandler = new ResultHandler(parseResult(rawResult));
		if (barcode == null) {
		} else {
			showDialog(resultHandler.getDisplayContents().toString());
		}
	}

	// 初始化照相机，CaptureActivityHandler解码
	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats,
						characterSet, cameraManager);
			}
		} catch (IOException ioe) {
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			displayFrameworkBugMessageAndExit();
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		DialogManager.showDialogSimple(CaptureActivity.this,
				R.string.msg_camera_framework_bug);
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (delayMS == 1) {
			return;
		}

		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
		resetStatusView();
	}

	// 初始化信息
	private void resetStatusView() {
		statusView.setVisibility(View.VISIBLE);
		viewfinderView.setVisibility(View.VISIBLE);
		lastResult = null;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}
}
