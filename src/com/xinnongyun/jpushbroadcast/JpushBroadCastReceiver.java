package com.xinnongyun.jpushbroadcast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.LruCache;
import android.widget.RemoteViews;
import cn.jpush.android.api.JPushInterface;

import com.xinnongyun.activity.MainActivity;
import com.xinnongyun.activity.MainBrocastActivity;
import com.xinnongyun.activity.R;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.json.JsonUtil;
import com.xinnongyun.net.NetConnectionCamera;
import com.xinnongyun.net.NetConnectionCamera.CameraSuccessCallBack;
import com.xinnongyun.net.NetConnectionFarming;
import com.xinnongyun.net.NetConnectionFarming.FarmingSuccessCallBackGetAll;
import com.xinnongyun.net.NetConnectionJournal;
import com.xinnongyun.net.NetConnectionJournal.JournalSuccessCallBack;
import com.xinnongyun.net.NetConnectionWeatherStation;
import com.xinnongyun.net.NetConnectionWeatherStation.WeatherSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.BeingDaoDBManager;
import com.xinnongyun.sqlite.BlowerDaoDBManager;
import com.xinnongyun.sqlite.CameraDaoDBManager;
import com.xinnongyun.sqlite.CollectDaoDBManager;
import com.xinnongyun.sqlite.ControlCenterDaoDBManager;
import com.xinnongyun.sqlite.FarmDaoManager;
import com.xinnongyun.sqlite.FarmingDaoDBManager;
import com.xinnongyun.sqlite.JournalDaoDBManager;
import com.xinnongyun.sqlite.NoticeDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.sqlite.WeatherStationDaoDBManager;

public class JpushBroadCastReceiver extends BroadcastReceiver {

	public static MyNotificationLruCache notificationLruCache = MyNotificationLruCache.getMyNotificationLrucache();
	
	@Override
	public void onReceive(final Context cxt, Intent intent) {
		
		if (MySharePreference.getValueFromKey(cxt,
				MySharePreference.LOGINSTATE).equals(
				MySharePreference.NOTSTATE)) {
			return;
		}
		
		Bundle bundle = intent.getExtras();
		String result = null;
		if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			result =  bundle.getString(JPushInterface.EXTRA_MESSAGE);
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			result = bundle.getString(JPushInterface.EXTRA_ALERT);
		}
		if(result!=null && result.length()>0)
		{ //  System.out.println(result);
			try {
				result = new String(result.getBytes(),"utf-8");
				} catch (UnsupportedEncodingException e1) {
			}
			
		    System.out.println("Jpush:" + result);
			final HashMap<String, Object> noticeMap = JsonUtil.getHashMap(result);
			if(noticeMap!= null && noticeMap.keySet().size()>0)
			{
				new Thread(
						new Runnable() {
							@Override
							public void run() {
								
							try {
								
								//TODO 移动风口中
								if(noticeMap.get("subject").toString().equals("blower/moving") && noticeMap.get("signal").toString().equals("updated"))
								{
									HashMap<String, Object> inMap = JsonUtil.getHashMap(noticeMap.get("context").toString());
									String succeedStr = inMap.get("blower").toString();
									if(succeedStr.length()>2)
									{
										succeedStr = succeedStr.replace("[", "").replace("]", "");
										String ids[] = succeedStr.split(",");
										for(String id : ids)
										{
											NetConnectionCamera.CameraGetNetConnection("http://m.nnong.com/v2/api-automator/blower/" + id + "/", null, null, 
													new CameraSuccessCallBack() {
														@Override
														public void onSuccess(final HashMap<String, Object> result) {
															BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(cxt);
															blowerDaoDBManager.updateBlowerById(result);
														}
											});
										}
									}
									return;
								}
								
								//TODO 移动风口结果
								if(noticeMap.get("subject").toString().equals("blower/move") && noticeMap.get("signal").toString().equals("updated"))
								{
									HashMap<String, Object> inMap = JsonUtil.getHashMap(noticeMap.get("context").toString());
									String succeedStr = inMap.get("succeed").toString();
									if(succeedStr.length()>2)
									{
										succeedStr = succeedStr.replace("[", "").replace("]", "");
										String ids[] = succeedStr.split(",");
										for(String id : ids)
										{
											NetConnectionCamera.CameraGetNetConnection("http://m.nnong.com/v2/api-automator/blower/" + id + "/", null, null, 
													new CameraSuccessCallBack() {
														@Override
														public void onSuccess(final HashMap<String, Object> result) {
															BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(cxt);
															blowerDaoDBManager.updateBlowerById(result);
														}
														
											});
										}
										
										try {
											NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
											noticeDaoDBManager.insertNotice(noticeMap);
											Intent noticeIntent = new Intent();
											noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
											noticeIntent.putExtra("currentpage", 2);
											noticeIntent.setClass(cxt, MainBrocastActivity.class);
											showActivityNotifi(cxt,inMap.get("success_alert").toString(),noticeIntent);
										} catch (Exception e) {
											
										}
									}
									String failedStr = inMap.get("failed").toString();
									if(failedStr.length()>2)
									{
										failedStr = failedStr.replace("[", "").replace("]", "");
										
										failedStr = failedStr.replace("[", "").replace("]", "");
										String ids[] = failedStr.split(",");
										for(String id : ids)
										{
											NetConnectionCamera.CameraGetNetConnection("http://m.nnong.com/v2/api-automator/blower/" + id + "/", null, null, 
													new CameraSuccessCallBack() {
														@Override
														public void onSuccess(final HashMap<String, Object> result) {
															BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(cxt);
															blowerDaoDBManager.updateBlowerById(result);
														}
														
											});
										}
										try {
											NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
											noticeDaoDBManager.insertNotice(noticeMap);
											Intent noticeIntent = new Intent();
											noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
											noticeIntent.putExtra("currentpage", 2);
											noticeIntent.setClass(cxt, MainBrocastActivity.class);
											showActivityNotifi(cxt,inMap.get("failure_alert").toString(),noticeIntent);
										} catch (Exception e) {
											
										}
									}
									
									return;
								}
								
								
								
								
								//TODO 修改放风机
								if(noticeMap.get("subject").toString().equals("blower/conf") && noticeMap.get("signal").toString().equals("updated"))
								{
									HashMap<String, Object> inMap = JsonUtil.getHashMap(noticeMap.get("context").toString());
									String succeedStr = inMap.get("succeed").toString();
									if(succeedStr.length()>2)
									{
										succeedStr = succeedStr.replace("[", "").replace("]", "");
										String ids[] = succeedStr.split(",");
										for(String id : ids)
										{
											NetConnectionCamera.CameraGetNetConnection("http://m.nnong.com/v2/api-automator/blower/" + id + "/", null, null, 
													new CameraSuccessCallBack() {
														@Override
														public void onSuccess(final HashMap<String, Object> result) {
															BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(cxt);
															blowerDaoDBManager.updateBlowerById(result);
														}
														
											});
										}
										
										try {
											NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
											noticeDaoDBManager.insertNotice(noticeMap);
											Intent noticeIntent = new Intent();
											noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
											noticeIntent.putExtra("currentpage", 2);
											noticeIntent.setClass(cxt, MainBrocastActivity.class);
											showActivityNotifi(cxt,inMap.get("success_alert").toString(),noticeIntent);
										} catch (Exception e) {
										}
									}
									String failedStr = inMap.get("failed").toString();
									if(failedStr.length()>2)
									{
										failedStr = failedStr.replace("[", "").replace("]", "");
										
										failedStr = failedStr.replace("[", "").replace("]", "");
										String ids[] = failedStr.split(",");
										for(String id : ids)
										{
											NetConnectionCamera.CameraGetNetConnection("http://m.nnong.com/v2/api-automator/blower/" + id + "/", null, null, 
													new CameraSuccessCallBack() {
														@Override
														public void onSuccess(final HashMap<String, Object> result) {
															BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(cxt);
															blowerDaoDBManager.updateBlowerById(result);
														}
														
											});
										}
										try {
											NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
											noticeDaoDBManager.insertNotice(noticeMap);
											Intent noticeIntent = new Intent();
											noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
											noticeIntent.putExtra("currentpage", 2);
											noticeIntent.setClass(cxt, MainBrocastActivity.class);
											showActivityNotifi(cxt,inMap.get("failure_alert").toString(),noticeIntent);
										} catch (Exception e) {
										}
									}
									
									return;
								}
								
								
								//TODO 风机工作异常
								if(noticeMap.get("subject").toString().equals("cc/exception") && noticeMap.get("signal").toString().equals("created"))
								{
								    try {
										NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
										noticeDaoDBManager.insertNotice(noticeMap);
										Intent noticeIntent = new Intent();
										noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
										noticeIntent.putExtra("currentpage", 2);
										noticeIntent.setClass(cxt, MainBrocastActivity.class);
										showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
									} catch (Exception e) {
									}
									return;
								}
								
								
								//TODO 同步中控下放风机数据
								if(noticeMap.get("subject").toString().equals("cc") && noticeMap.get("signal").toString().equals("refresh"))
								{
									final String ccId = JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString();
									//获取中控下所有放风机
									NetConnectionFarming.FarmingGetAllNetConnection(
											NetUrls.getAllBlowerFromControl(ccId), null, null, 
											new FarmingSuccessCallBackGetAll() {
												@Override
												public void onSuccess(List<HashMap<String, Object>> result1) {
													if(result1!=null && result1.size()>0)
													{
														BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(cxt);
														for(HashMap<String, Object> map : result1)
														{
															HashMap<String, String> bMap = blowerDaoDBManager.getBlowerById(map.get("id").toString());
															if(bMap!=null && bMap.size()>2)
															{
																blowerDaoDBManager.updateBlowerById(map);
															}
															else
															{
																List<HashMap<String, Object>> res = new ArrayList<HashMap<String,Object>>();
																res.add(map);
																blowerDaoDBManager.insertAllControlCenter(res );
															}
														}
													}
												}
											}
											);
								    
									return;
								}
								
								
								
								
								
								
								
								//TODO 中控离线
								if(noticeMap.get("subject").toString().equals("cc/offline") && noticeMap.get("signal").toString().equals("created"))
								{
								    try {
										NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
										noticeDaoDBManager.insertNotice(noticeMap);
										Intent noticeIntent = new Intent();
										noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
										noticeIntent.putExtra("currentpage", 2);
										noticeIntent.setClass(cxt, MainBrocastActivity.class);
										showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
									} catch (Exception e) {
									}
									return;
								}
								
								//TODO 卸载中控
								if(noticeMap.get("subject").toString().equals("cc") && noticeMap.get("signal").toString().equals("deleted"))
								{
									if(!MySharePreference.getValueFromKey(cxt, MySharePreference.ACCOUNTID).equals(noticeMap.get("operator").toString()))
									{
										
										try {
											ControlCenterDaoDBManager ccDao = new ControlCenterDaoDBManager(cxt);
											ccDao.deleteControlCenterById(JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString());		
											NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
											noticeDaoDBManager.insertNotice(noticeMap);
											Intent noticeIntent = new Intent();
											noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
											noticeIntent.putExtra("currentpage", 2);
											noticeIntent.setClass(cxt, MainBrocastActivity.class);
											showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
										} catch (Exception e) {
										}
										
									}
									return;
								}
								
								//TODO 修改中控
								if(noticeMap.get("subject").toString().equals("cc") && noticeMap.get("signal").toString().equals("updated"))
								{
									if(!MySharePreference.getValueFromKey(cxt, MySharePreference.ACCOUNTID).equals(noticeMap.get("operator").toString()))
									{
										NetConnectionCamera.CameraGetNetConnection("http://m.nnong.com/v2/api-automator/cc/" + 
												JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString()+ "/", null, null, 
												new CameraSuccessCallBack() {
													@Override
													public void onSuccess(final HashMap<String, Object> result) {
														if (result != null && result.keySet().contains("id")) {
															ControlCenterDaoDBManager ccDao = new ControlCenterDaoDBManager(cxt);
															ccDao.updateControlCenterById(result);
															try {
																NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
																noticeDaoDBManager.insertNotice(noticeMap);
																Intent noticeIntent = new Intent();
																noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
																noticeIntent.putExtra("currentpage", 2);
																noticeIntent.setClass(cxt, MainBrocastActivity.class);
																showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
															} catch (Exception e) {
															}
														}
													}
									       });
									}
									return;
								}
										
								
								//TODO 安装中控
								if(noticeMap.get("subject").toString().equals("cc") && noticeMap.get("signal").toString().equals("added"))
								{
									if(!MySharePreference.getValueFromKey(cxt, MySharePreference.ACCOUNTID).equals(noticeMap.get("operator").toString()))
									{
										
										NetConnectionCamera.CameraGetNetConnection("http://m.nnong.com/v2/api-automator/cc/" + 
												JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString()+ "/", null, null, 
												new CameraSuccessCallBack() {
													@Override
													public void onSuccess(final HashMap<String, Object> result) {
														if (result != null && result.keySet().contains("id")) {
															ControlCenterDaoDBManager ccDao = new ControlCenterDaoDBManager(cxt);
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
																				BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(cxt);
																				blowerDaoDBManager.insertAllControlCenter(result1);
																			}
																		}
																	}
																	);
															try {
																NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
																
																Intent noticeIntent = new Intent();
																noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
																noticeDaoDBManager.insertNotice(noticeMap);
																noticeIntent.putExtra("currentpage", 2);
																noticeIntent.setClass(cxt, MainBrocastActivity.class);
																showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
															} catch (Exception e) {
															}
															
														}
													}
										});
									}
									return;
								}
								
								
								
								
								//TODO 农场摄像头下线
								if(noticeMap.get("subject").toString().equals("camera") && noticeMap.get("signal").toString().equals("offline"))
								{
									if(!MySharePreference.getValueFromKey(cxt, MySharePreference.ACCOUNTID).equals(noticeMap.get("operator").toString()))
									{			
										NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
										noticeDaoDBManager.insertNotice(noticeMap);
										Intent noticeIntent = new Intent();
										noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
										noticeIntent.putExtra("currentpage", 2);
										noticeIntent.setClass(cxt, MainBrocastActivity.class);
										showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
									}
									return;
								}
								
								//TODO 农场删除摄像头
								if(noticeMap.get("subject").toString().equals("camera") && noticeMap.get("signal").toString().equals("deleted"))
								{
									if(!MySharePreference.getValueFromKey(cxt, MySharePreference.ACCOUNTID).equals(noticeMap.get("operator").toString()))
									{
										CameraDaoDBManager stationDaoDBManager = new CameraDaoDBManager(cxt);
										stationDaoDBManager.deleteCmareaById(JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString());				
										NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
										noticeDaoDBManager.insertNotice(noticeMap);
										Intent noticeIntent = new Intent();
										noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
										noticeIntent.putExtra("currentpage", 2);
										noticeIntent.setClass(cxt, MainBrocastActivity.class);
										showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
									}
									return;
								}
								
								
								//TODO 农场修改摄像头
								if(noticeMap.get("subject").toString().equals("camera") && noticeMap.get("signal").toString().equals("updated"))
								{
									if(!MySharePreference.getValueFromKey(cxt, MySharePreference.ACCOUNTID).equals(noticeMap.get("operator").toString()))
									{
										final CameraDaoDBManager stationDaoDBManager = new CameraDaoDBManager(cxt);
										HashMap<String, String> cameraMap = stationDaoDBManager.getCameraInfoById(
												JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString()
												);
										if(cameraMap!=null && cameraMap.keySet().size()>0)
										{
											NetConnectionCamera.CameraGetNetConnection(
													NetUrls.URL_CAMREA_MODIFY_ADD(JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString())
													, null, null, 
													new CameraSuccessCallBack() {
														@Override
														public void onSuccess(HashMap<String, Object> result) {
															if(result!=null && !result.keySet().contains("status_code"))
															{
																stationDaoDBManager.updateCameraById(result,1);
																NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
																noticeDaoDBManager.insertNotice(noticeMap);
																Intent noticeIntent = new Intent();
																noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
																noticeIntent.putExtra("currentpage", 2);
																noticeIntent.setClass(cxt, MainBrocastActivity.class);
																showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
															}
														}
													}
													);
										}
									}
									return;
								}
								
								
								//TODO 农场添加摄像头
								if(noticeMap.get("subject").toString().equals("camera") && noticeMap.get("signal").toString().equals("added"))
								{
									final CameraDaoDBManager stationDaoDBManager = new CameraDaoDBManager(cxt);
									HashMap<String, String> cameraMap = stationDaoDBManager.getCameraInfoById(
											JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString()
											);
									if(cameraMap==null || cameraMap.keySet().size()==0)
									{
										NetConnectionCamera.CameraGetNetConnection(
												NetUrls.URL_CAMREA_MODIFY_ADD(JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString())
												, null, null, 
												new CameraSuccessCallBack() {
													@Override
													public void onSuccess(HashMap<String, Object> result) {
														if(result!=null && !result.keySet().contains("status_code"))
														{
															List<HashMap<String, Object>> listDate = new ArrayList<HashMap<String, Object>>();
															listDate.add(result);
															stationDaoDBManager.insertAllCameras(listDate);
															NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
															noticeDaoDBManager.insertNotice(noticeMap);
															Intent noticeIntent = new Intent();
															noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
															noticeIntent.putExtra("currentpage", 2);
															noticeIntent.setClass(cxt, MainBrocastActivity.class);
															showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
														}
													}
												}
												);
									}
									return;
								}
								
								
								
								
								//TODO 农场添加环境小站
								if(noticeMap.get("subject").toString().equals("weather_station") && noticeMap.get("signal").toString().equals("added"))
								{
									if(!MySharePreference.getValueFromKey(cxt, MySharePreference.ACCOUNTID).equals(noticeMap.get("operator").toString()))
									{
										final WeatherStationDaoDBManager stationDaoDBManager = new WeatherStationDaoDBManager(cxt);
										List<HashMap<String, String>> stations = stationDaoDBManager.getAllWeatherStation();
										if(stations==null || stations.size()==0)
										{
											NetConnectionWeatherStation.WeatherGetNetConnection("http://m.nnong.com/v2/api-weather-station/"+
													JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString()+"/", null, null, 
													new WeatherSuccessCallBack() {
														@Override
														public void onSuccess(HashMap<String, Object> result) {
															if(result!=null && !result.keySet().contains("status_code"))
															{
																List<HashMap<String, Object>> listDate = new ArrayList<HashMap<String, Object>>();
																listDate.add(result);
																stationDaoDBManager.insertAllWeatherStation(listDate);
															}
														}
													}
													);
											NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
											noticeDaoDBManager.insertNotice(noticeMap);
											Intent noticeIntent = new Intent();
											noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
											noticeIntent.putExtra("currentpage", 2);
											noticeIntent.setClass(cxt, MainBrocastActivity.class);
											showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
										}
									}
									return;
								}
								
								
								//TODO 修改环境小站
								if(noticeMap.get("subject").toString().equals("weather_station") && noticeMap.get("signal").toString().equals("updated"))
								{
									if(!MySharePreference.getValueFromKey(cxt, MySharePreference.ACCOUNTID).equals(noticeMap.get("operator").toString()))
									{
										final WeatherStationDaoDBManager stationDaoDBManager = new WeatherStationDaoDBManager(cxt);
										List<HashMap<String, String>> stations = stationDaoDBManager.getAllWeatherStation();
										if(stations!=null && stations.size()>0)
										{
											NetConnectionWeatherStation.WeatherGetNetConnection("http://m.nnong.com/v2/api-weather-station/"+
													JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString()+"/", null, null, 
													new WeatherSuccessCallBack() {
														@Override
														public void onSuccess(HashMap<String, Object> result) {
															if(result!=null && !result.keySet().contains("status_code"))
															{
																stationDaoDBManager.updateWeatherStationById(result);
															}
														}
													}
													);
											NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
											noticeDaoDBManager.insertNotice(noticeMap);
											Intent noticeIntent = new Intent();
											noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
											noticeIntent.putExtra("currentpage", 2);
											noticeIntent.setClass(cxt, MainBrocastActivity.class);
											showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
										}
									}
									return;
								}
								
								//TODO 删除环境小站
								if(noticeMap.get("subject").toString().equals("weather_station") && noticeMap.get("signal").toString().equals("deleted"))
								{
									if(!MySharePreference.getValueFromKey(cxt, MySharePreference.ACCOUNTID).equals(noticeMap.get("operator").toString()))
									{
										final WeatherStationDaoDBManager stationDaoDBManager = new WeatherStationDaoDBManager(cxt);
										List<HashMap<String, String>> stations = stationDaoDBManager.getAllWeatherStation();
										if(stations!=null && stations.size()>0)
										{
											stationDaoDBManager.deleteWeatherStationById(JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString());
											NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
											noticeDaoDBManager.insertNotice(noticeMap);
											Intent noticeIntent = new Intent();
											//noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
											//noticeIntent.putExtra("currentpage", 2);
											//noticeIntent.setClass(cxt, MainBrocastActivity.class);
											showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
										}
									}
									return;
								}
								
								//TODO 环境小站电源报警
								if(noticeMap.get("subject").toString().equals("weather_alarm") && noticeMap.get("signal").toString().equals("created"))
								{
									NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
									noticeDaoDBManager.insertNotice(noticeMap);
									Intent noticeIntent = new Intent();
									noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
									noticeIntent.putExtra("currentpage", 2);
									noticeIntent.setClass(cxt, MainBrocastActivity.class);
									showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
									return;
								}
								
								//TODO 环境小站电离线报警
								if(noticeMap.get("subject").toString().equals("weather_offline") && noticeMap.get("signal").toString().equals("created"))
								{
									NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
									noticeDaoDBManager.insertNotice(noticeMap);
									Intent noticeIntent = new Intent();
									noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
									noticeIntent.putExtra("currentpage", 2);
									noticeIntent.setClass(cxt, MainBrocastActivity.class);
									showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
									return;
								}
								
								
								
								
								
								//TODO 农场有人发日记
								if(noticeMap.get("subject").toString().equals("journal") && noticeMap.get("signal").toString().equals("created"))
								{
									if(!MySharePreference.getValueFromKey(cxt, MySharePreference.ACCOUNTID).equals(noticeMap.get("operator").toString()))
									{
										NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
										noticeDaoDBManager.insertNotice(noticeMap);
										Intent noticeIntent = new Intent();
										noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
										noticeIntent.putExtra("currentpage", 2);
										noticeIntent.setClass(cxt, MainBrocastActivity.class);
										showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
									}
									final JournalDaoDBManager journalDaoDBManager = new JournalDaoDBManager(cxt);
									HashMap<String, String> map = journalDaoDBManager.getJournalVideoInfoById(JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString());
									if(map==null || map.keySet().size() ==0)
									{
										NetConnectionJournal.JournalGetOneNetConnection(NetUrls.get_URL_JOURNALVIDEO_BYID(JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString()),
												null, null,
												new JournalSuccessCallBack() {
													@Override
													public void onSuccess(HashMap<String, Object> result) {
														if(result!=null && !result.keySet().contains("status_code"))
														{
															journalDaoDBManager.insertUploadVideo(result);
														}
													}
												});
									}
									//系统通知   任务
									return;
								}
								
								
								//TODO 某视频被播放了xx次
								if(noticeMap.get("subject").toString().equals("journal") && noticeMap.get("signal").toString().equals("view"))
								{
									NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
									noticeDaoDBManager.insertNotice(noticeMap);
									Intent noticeIntent = new Intent();
									noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
									noticeIntent.putExtra("currentpage", 2);
									noticeIntent.setClass(cxt, MainBrocastActivity.class);
									showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
									final JournalDaoDBManager journalDaoDBManager = new JournalDaoDBManager(cxt);
									HashMap<String, String> map = journalDaoDBManager.getJournalVideoInfoById(JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString());
									if(map==null || map.keySet().size() ==0)
									{
										NetConnectionJournal.JournalGetOneNetConnection(NetUrls.get_URL_JOURNALVIDEO_BYID(JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString()),
												null, null,
												new JournalSuccessCallBack() {
													@Override
													public void onSuccess(HashMap<String, Object> result) {
														if(result!=null && !result.keySet().contains("status_code"))
														{
															journalDaoDBManager.insertUploadVideo(result);
														}
													}
												});
									}
									else
									{
										NetConnectionJournal.JournalGetOneNetConnection(NetUrls.get_URL_JOURNALVIDEO_BYID(JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString()),
												null, null,
												new JournalSuccessCallBack() {
													@Override
													public void onSuccess(HashMap<String, Object> result) {
														if(result!=null && !result.keySet().contains("status_code"))
														{
															journalDaoDBManager.updateJOurnalById(result);
														}
													}
												});
									}
									//系统通知   任务
									return;
								}
								
								//TODO 某视频被赞、评论、删除
								if(noticeMap.get("subject").toString().equals("journal") && noticeMap.get("signal").toString().equals("like"))
								{
									final JournalDaoDBManager journalDaoDBManager = new JournalDaoDBManager(cxt);
									HashMap<String, String> map = journalDaoDBManager.getJournalVideoInfoById(JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString());
									if(map==null || map.keySet().size() ==0)
									{
										NetConnectionJournal.JournalGetOneNetConnection(NetUrls.get_URL_JOURNALVIDEO_BYID(JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString()),
												null, null,
												new JournalSuccessCallBack() {
													@Override
													public void onSuccess(HashMap<String, Object> result) {
														if(result!=null && !result.keySet().contains("status_code"))
														{
															journalDaoDBManager.insertUploadVideo(result);
															if(!MySharePreference.getValueFromKey(cxt, MySharePreference.ACCOUNTID).equals(noticeMap.get("operator").toString()))
															{
																NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
																noticeDaoDBManager.insertNotice(noticeMap);
															}
														}
													}
												});
									}
									else
									{
										NetConnectionJournal.JournalGetOneNetConnection(NetUrls.get_URL_JOURNALVIDEO_BYID(JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString()),
												null, null,
												new JournalSuccessCallBack() {
													@Override
													public void onSuccess(HashMap<String, Object> result) {
														if(result!=null && !result.keySet().contains("status_code"))
														{
															journalDaoDBManager.updateJOurnalById(result);
															if(!MySharePreference.getValueFromKey(cxt, MySharePreference.ACCOUNTID).equals(noticeMap.get("operator").toString()))
															{
																NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
																noticeDaoDBManager.insertNotice(noticeMap);
															}
														}
													}
												});
									}
									//系统通知   任务
									return;
								}
								
								//TODO 某视频被评论
								if(noticeMap.get("subject").toString().equals("journal") && noticeMap.get("signal").toString().equals("comment"))
								{
									final JournalDaoDBManager journalDaoDBManager = new JournalDaoDBManager(cxt);
									HashMap<String, String> map = journalDaoDBManager.getJournalVideoInfoById(JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString());
									if(map==null || map.keySet().size() ==0)
									{
										NetConnectionJournal.JournalGetOneNetConnection(NetUrls.get_URL_JOURNALVIDEO_BYID(JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString()),
												null, null,
												new JournalSuccessCallBack() {
													@Override
													public void onSuccess(HashMap<String, Object> result) {
														if(result!=null && !result.keySet().contains("status_code"))
														{
															journalDaoDBManager.insertUploadVideo(result);
															if(!MySharePreference.getValueFromKey(cxt, MySharePreference.ACCOUNTID).equals(noticeMap.get("operator").toString()))
															{
																NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
																noticeDaoDBManager.insertNotice(noticeMap);
															}
														}
													}
												});
									}
									else
									{
										NetConnectionJournal.JournalGetOneNetConnection(NetUrls.get_URL_JOURNALVIDEO_BYID(JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString()),
												null, null,
												new JournalSuccessCallBack() {
													@Override
													public void onSuccess(HashMap<String, Object> result) {
														if(result!=null && !result.keySet().contains("status_code"))
														{
															journalDaoDBManager.updateJOurnalById(result);
															if(!MySharePreference.getValueFromKey(cxt, MySharePreference.ACCOUNTID).equals(noticeMap.get("operator").toString()))
															{
																NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
																noticeDaoDBManager.insertNotice(noticeMap);
															}
														}
													}
												});
									}
									//系统通知   任务
									return;
								}
								
								//TODO 某视频被删除
								if(noticeMap.get("subject").toString().equals("journal") && noticeMap.get("signal").toString().equals("deleted"))
								{
									if(!MySharePreference.getValueFromKey(cxt, MySharePreference.ACCOUNTID).equals(noticeMap.get("operator").toString()))
									{
										NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
										noticeDaoDBManager.insertNotice(noticeMap);
										Intent noticeIntent = new Intent();
										noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
										noticeIntent.putExtra("currentpage", 2);
										noticeIntent.setClass(cxt, MainBrocastActivity.class);
										showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
									}
									JournalDaoDBManager journalDaoDBManager = new JournalDaoDBManager(cxt);
									journalDaoDBManager.deleteJournalById(JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString());
									//系统通知   任务
									return;
								}
								
								
								//TODO 最后一篇农场日记超过10天
								if(noticeMap.get("subject").toString().equals("journal") && noticeMap.get("signal").toString().equals("tip"))
								{
									NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
									noticeDaoDBManager.insertNotice(noticeMap);
									Intent noticeIntent = new Intent();
									noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
									noticeIntent.putExtra("currentpage", 2);
									noticeIntent.setClass(cxt, MainBrocastActivity.class);
									showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
									//系统通知   任务
									return;
								}
								
								//TODO 新版本
								if(noticeMap.get("subject").toString().equals("version") && noticeMap.get("signal").toString().equals("created"))
								{
									NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
									noticeDaoDBManager.insertNotice(noticeMap);
									Intent noticeIntent = new Intent();
									noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
									noticeIntent.putExtra("currentpage", 2);
									noticeIntent.setClass(cxt, MainBrocastActivity.class);
									showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
									//系统通知   任务
									return;
								}
								
								
								//TODO 系统通知   浏览器
								if(noticeMap.get("subject").toString().equals("notice") && noticeMap.get("signal").toString().equals("created"))
								{
									NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
									noticeDaoDBManager.insertNotice(noticeMap);
									Intent noticeIntent = new Intent();
									noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
									noticeIntent.putExtra("currentpage", 2);
									noticeIntent.setClass(cxt, MainBrocastActivity.class);
									showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
									//系统通知   任务
									return;
								}
								
								
								//TODO 电量低
								if(noticeMap.get("subject").toString().equals("alarm") && noticeMap.get("signal").toString().equals("power"))
								{
									NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
									noticeDaoDBManager.insertNotice(noticeMap);
									Intent noticeIntent = new Intent();
									noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
									noticeIntent.putExtra("currentpage", 2);
									noticeIntent.setClass(cxt, MainBrocastActivity.class);
									showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
									//通知 任务
									return;
								}
								
								
								//TODO 某项指标超过报警值
								if(noticeMap.get("subject").toString().equals("alarm") && noticeMap.get("signal").toString().equals("created"))
								{
									NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
									noticeDaoDBManager.insertNotice(noticeMap);
									
									Intent noticeIntent = new Intent();
									noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
									noticeIntent.putExtra("currentpage", 2);
									noticeIntent.setClass(cxt, MainBrocastActivity.class);
									showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
									
									//通知 任务
									return;
								}
								
								//TODO 意见反馈
								if(noticeMap.get("subject").toString().equals("feedback") && noticeMap.get("signal").toString().equals("requested"))
								{
									NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
									noticeDaoDBManager.insertNotice(noticeMap);
									
									Intent noticeIntent = new Intent();
									noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
									noticeIntent.putExtra("currentpage", 2);
									noticeIntent.setClass(cxt, MainBrocastActivity.class);
									showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
									
									//任务
									return;
								}
								
								//TODO 新注册用户 头像
								if(noticeMap.get("subject").toString().equals("user") && noticeMap.get("signal").toString().equals("edit-profile"))
								{
									// 任务
									NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
									noticeDaoDBManager.insertNotice(noticeMap);
									return;
								}
								//TODO 新注册用户 设置农场
								if(noticeMap.get("subject").toString().equals("user") && noticeMap.get("signal").toString().equals("add-farm"))
								{
									//任务
									NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
									noticeDaoDBManager.insertNotice(noticeMap);
									return;
								}
								
								//-----------------------自己是否显示通知
								if(!MySharePreference.getValueFromKey(cxt, MySharePreference.ACCOUNTID).equals(noticeMap.get("operator").toString()))
								{
									NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
									noticeDaoDBManager.insertNotice(noticeMap);
									Intent noticeIntent = new Intent();
									noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
									noticeIntent.putExtra("currentpage", 2);
									noticeIntent.setClass(cxt, MainBrocastActivity.class);
									showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
								}
								else
								{
//									NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
//									noticeDaoDBManager.insertNotice(noticeMap);
//									Intent noticeIntent = new Intent();
//									noticeIntent.putExtra(TablesColumns.TABLENOTICE_ID+"notice", noticeMap.get(TablesColumns.TABLENOTICE_ID).toString());
//									noticeIntent.putExtra("currentpage", 2);
//									noticeIntent.setClass(cxt, MainBrocastActivity.class);
//									showActivityNotifi(cxt,noticeMap.get("alert").toString(),noticeIntent);
								}
								
								
								//TODO 修改种养记录报警信息
								if(noticeMap.get("subject").toString().equals("farming") && noticeMap.get("signal").toString().equals("updated-alarm"))
								{
									FarmingDaoDBManager farmingDaoDBManager = new FarmingDaoDBManager(cxt);
									farmingDaoDBManager.updateFarmingById(JsonUtil.getHashMap(noticeMap.get("context").toString()));
									//系统通知   任务
									return;
								}
								
								
								//TODO 修改种养纪录 成熟时间
								if(noticeMap.get("subject").toString().equals("farming") && noticeMap.get("signal").toString().equals("updated-mature"))
								{
								
									FarmingDaoDBManager farmingDaoDBManager = new FarmingDaoDBManager(cxt);
									farmingDaoDBManager.updateFarmingById(JsonUtil.getHashMap(noticeMap.get("context").toString()));
									
									
									//系统通知   任务
									return;
								}
								
								//TODO 新建种养纪录
								if(noticeMap.get("subject").toString().equals("farming") && noticeMap.get("signal").toString().equals("created"))
								{
									FarmingDaoDBManager farmingDaoDBManager = new FarmingDaoDBManager(cxt);
									if(null == farmingDaoDBManager.getFarmingInfoById(
											JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEFARMING_ID).toString()))
									farmingDaoDBManager.insertNewFarming(JsonUtil.getHashMap(noticeMap.get("context").toString()));
									
									
									//系统通知   任务
									return;
								}
								
								
								
								
								//TODO 删除采集器
								if(noticeMap.get("subject").toString().equals("collector") && noticeMap.get("signal").toString().equals("deleted"))
								{
									
									CollectDaoDBManager collectDaoDBManager = new CollectDaoDBManager(cxt);
									collectDaoDBManager.deleteCollectById(JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLECOLLECTOR_ID).toString());
									FarmingDaoDBManager farmingDao = new FarmingDaoDBManager(cxt);
									farmingDao.deleteCollectNewFarming(JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLECOLLECTOR_ID).toString());
									
									
									//系统通知   任务
									return;
								}
								
								//TODO 修改采集器
								if(noticeMap.get("subject").toString().equals("collector") && noticeMap.get("signal").toString().equals("updated"))
								{
									CollectDaoDBManager collectDaoDBManager = new CollectDaoDBManager(cxt);
									collectDaoDBManager.updateCollectById(JsonUtil.getHashMap(noticeMap.get("context").toString()));
									//系统通知   任务
									return;
								}
								
								//TODO 添加采集器
								if(noticeMap.get("subject").toString().equals("collector") && noticeMap.get("signal").toString().equals("added"))
								{
									CollectDaoDBManager collectDaoDBManager = new CollectDaoDBManager(cxt);
									HashMap<String, String> cMap = collectDaoDBManager.getCollectById(
												JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLECOLLECTOR_ID).toString());
									if(null ==cMap || cMap.keySet().size()==0)
									{
										List<HashMap<String, Object>> listMap = new ArrayList<HashMap<String,Object>>();
										listMap.add(JsonUtil.getHashMap(noticeMap.get("context").toString()));
										collectDaoDBManager.insertAllCollect(listMap);
									}
									//系统通知   任务
									return;
								}
								
								
								//TODO 新建新品种
								if(noticeMap.get("subject").toString().equals("being") && noticeMap.get("signal").toString().equals("created"))
								{
									BeingDaoDBManager beingDaoDBManager = new BeingDaoDBManager(cxt);
									HashMap<String, String> cMap = beingDaoDBManager.getBeingById(JsonUtil.getHashMap(noticeMap.get("context").toString()).get(TablesColumns.TABLEBEING_ID).toString());
									if(cMap==null || cMap.keySet().size() ==0)
									{
										List<HashMap<String, Object>> listMap = new ArrayList<HashMap<String,Object>>();
										listMap.add(JsonUtil.getHashMap(noticeMap.get("context").toString()));
										beingDaoDBManager.insertAllBeing(listMap);
									}
									
									//系统通知   任务
									return;
								}
								
								
								//TODO 退出农场
								if(noticeMap.get("subject").toString().equals("farm") && noticeMap.get("signal").toString().equals("departed"))
								{
									//系统通知   任务
									return;
								}
								
								//TODO 加入农场
								if(noticeMap.get("subject").toString().equals("farm") && noticeMap.get("signal").toString().equals("joined"))
								{
									//系统通知   任务
									return;
								}
								
								//TODO 删除农场
								if(noticeMap.get("subject").toString().equals("farm") && noticeMap.get("signal").toString().equals("deleted"))
								{
									if(!MySharePreference.getValueFromKey(cxt, MySharePreference.ACCOUNTID).equals(noticeMap.get("operator").toString()))
									{
										MySharePreference.clearFarmInfo(cxt);
										FarmDaoManager farmDao = new FarmDaoManager(cxt);
										farmDao.clearFarmTable(0);
									}
									
									//系统通知   任务
									return;
								}
								
								//TODO 修改农场
								if(noticeMap.get("subject").toString().equals("farm") && noticeMap.get("signal").toString().equals("updated"))
								{
									if(!MySharePreference.getValueFromKey(cxt, MySharePreference.ACCOUNTID).equals(noticeMap.get("operator").toString()))
									{
										MySharePreference.saveFarmInfo(
												cxt, JsonUtil.getHashMap(noticeMap.get("context").toString()));
										
										Intent intent2 = new Intent();
										intent2.setAction(MainActivity.farmConfigbroastCast);
										cxt.sendBroadcast(intent2);
										//系统通知   任务
									}
									
									return;
								}
								
								
								//TODO 新建农场
								if(noticeMap.get("subject").toString().equals("farm") && noticeMap.get("signal").toString().equals("created"))
								{
									if(!MySharePreference.getValueFromKey(cxt, MySharePreference.ACCOUNTID).equals(noticeMap.get("operator").toString()))
									{
										NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(cxt);
										noticeDaoDBManager.insertNotice(noticeMap);
									}
									// 任务
									
									return;
								}
							} catch (Exception e) {
							}
							}
						}
						).start();
				
				
				
				
				
			}
		}
	}
	
	
	
	
	public void showActivityNotifi(Context ctx,String contentText,Intent intent) {
		int notifyNum = (UUID.randomUUID().toString()).hashCode();
		intent.putExtra("notify_id", notifyNum);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				ctx);
		builder.setContentText(contentText).setSmallIcon(R.drawable.ic_launcher);;
		builder.setTicker("新农云消息");
		RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.notifi_simple);
		views.setTextViewText(R.id.notifi_simple_tv, contentText);
		// 必须有一个发通知的东西 NotificationManager
		PendingIntent pendingIntent = PendingIntent.getActivity(ctx, notifyNum, intent , PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.notify_activity_ll, pendingIntent);
		builder.setContent(views);
		NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = builder.build();
		notification.defaults=Notification.DEFAULT_SOUND;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			builder.setAutoCancel(true);
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			 notification.contentView = views;
			 notification.contentIntent = pendingIntent;
		} else {
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			 notification.contentView = views;
			 notification.contentIntent = pendingIntent;
		}
		notification.icon = R.drawable.ic_launcher;
		manager.notify(notifyNum, notification);
		
		notificationLruCache.putImageCache(notifyNum+"", notifyNum);
		//AppShortCutUtil.addNumShortCut(ctx, LoginActivity.class, true, notificationLruCache.lruCache.size()+"", true);
		
	}
	
	
	
	
//	public void showActivityNotifi(Context ctx,String contentText,Intent intent) {
//		int notifyNum = (UUID.randomUUID().toString()).hashCode();
//		intent.putExtra("notify_id", notifyNum);
//		NotificationCompat.Builder builder = new NotificationCompat.Builder(
//				ctx);
//		builder.setContentText(contentText).setSmallIcon(R.drawable.ic_launcher);;
//		builder.setTicker("新农云消息");
//		RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.notifi_simple);
//		views.setTextViewText(R.id.notifi_simple_tv, contentText);
//		// 必须有一个发通知的东西 NotificationManager
//		PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 2, intent , PendingIntent.FLAG_ONE_SHOT);
//		views.setOnClickPendingIntent(R.id.notify_activity_ll, pendingIntent);
//		builder.setContent(views);
//		NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
//		Notification notification = builder.build();
//		notification.defaults=Notification.DEFAULT_SOUND;
//		notification.flags |= Notification.FLAG_AUTO_CANCEL;
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//			builder.setAutoCancel(true);
//		} else {
//			notification.flags = Notification.FLAG_AUTO_CANCEL;
//			 notification.contentView = views;
//			 notification.contentIntent = pendingIntent;
//		}
//		notification.icon = R.drawable.ic_launcher;
//		manager.notify(notifyNum, notification);
//		//notificationLruCache.context = ctx;
//		//notificationLruCache.putImageCache(notifyNum+"", notifyNum);
//	}
//	
//	
//	
//	
//	public void showSimpleNotifi(Context ctx,String contentText) {
//		int notifyNum = (UUID.randomUUID().toString()).hashCode();
//		NotificationCompat.Builder builder = new NotificationCompat.Builder(
//				ctx);
//		builder.setContentText(contentText).setSmallIcon(R.drawable.ic_launcher);;
//		builder.setTicker("新农云消息");
//		RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.notifi_simple);
//		builder.setContent(views);
//		views.setTextViewText(R.id.notifi_simple_tv, contentText);
//		// 必须有一个发通知的东西 NotificationManager
//		NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
//		Notification notification = builder.build();
//		notification.defaults=Notification.DEFAULT_SOUND;
//		notification.flags |= Notification.FLAG_AUTO_CANCEL;
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//			builder.setAutoCancel(true);
//		} else {
//			Intent intent = new Intent();
//			PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent, 0);
//			notification.contentIntent = contentIntent;
//			
//			notification.contentView = views;
//		}
//		notification.icon = R.drawable.ic_launcher;
//		manager.notify(notifyNum, notification);
//		//notificationLruCache.context = ctx;
//		//notificationLruCache.putImageCache(notifyNum+"", notifyNum);
//	}
//	
//	
	
	
	/**
	 * 判断程序是否打开
	 * @param context
	 * @param PackageName
	 * @return
	 */
	public static boolean isServiceStarted(Context context, String PackageName) {
		boolean isStarted = false;
		try {
			ActivityManager mActivityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			int intGetTastCounter = 1000;
			List<ActivityManager.RunningServiceInfo> mRunningService = mActivityManager
					.getRunningServices(intGetTastCounter);
			for (ActivityManager.RunningServiceInfo amService : mRunningService) {
				if (0 == amService.service.getPackageName().compareTo(
						PackageName)) {
					isStarted = true;
					break;
				}
			}
		} catch (SecurityException e) {
			return false;
		}
		return isStarted;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class MyNotificationLruCache {
		public Context context;
		private  static MyNotificationLruCache  notificationLruCache; 
		public static MyNotificationLruCache getMyNotificationLrucache()
		{
			if(notificationLruCache == null)
			{
				notificationLruCache= new MyNotificationLruCache();
			}
			return notificationLruCache;
		}
		public  LruCache<Object, Integer> lruCache;
		private MyNotificationLruCache() {
			lruCache = new LruCache<Object, Integer>(1000000) {

				// 返回缓存中每个bitmap的大小
				// key用来区分每张图片
				// value存储的每张图片
				@Override
				protected int sizeOf(Object key, Integer value) {
					return 1;
				}

				// 从内存中移除一个图片
				// evicted : true代表图片被从内存中移除
				// key : 区分每张图片的标识
				// oldValue : 从内存中移除的旧的图片
				// newValue : 加载到内存的新图片
				@Override
				protected void entryRemoved(boolean evicted, Object key,
						Integer oldValue, Integer newValue) {
				}
			};
		}

		// 存
		public void putImageCache(Object key, Integer bitmap) {
			lruCache.put(key, bitmap);
		}
		//移除
		public void reMoveKey(Object key)
		{
			lruCache.remove(key);
		}
		// 清空
		public void clearCache() {
			// 清空缓存中所有的内容
			lruCache.evictAll();
		}

	}
	
	
	
}
