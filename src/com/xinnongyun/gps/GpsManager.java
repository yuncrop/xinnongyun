package com.xinnongyun.gps;

import java.util.HashMap;

import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.xinnongyun.activity.ErrorMarkToast;
import com.xinnongyun.activity.R;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.json.JsonUtil;
import com.xinnongyun.net.NetConnectionFarm;
import com.xinnongyun.net.NetConnectionFarm.FarmSuccessCallBack;
import com.xinnongyun.net.SyncHttpVersion;
import com.xinnongyun.sharepreference.MySharePreference;

/**
 * 经纬度获取 反地理编码
 * 
 * @author sm
 * 
 */
public class GpsManager implements OnGetGeoCoderResultListener {

	private GeoCoder mSearch = null;
	// 定位相关
	private LocationClient mLocClient;
	private static GpsManager gpsManager;
	private TextView editText;
	private MyLocationListenner myListener = new MyLocationListenner();
	private int type = 0;

	/**
	 * 设置将要显示GPS数据的控件
	 * 
	 * @param editText
	 * @param 经纬度反地理编码 1---经纬度2
	 */
	public void setGpsView(TextView editText, int type) {
		this.editText = editText;
		this.type = type;
		intialInfo();
	}

	private GpsManager() {

	}

	/**
	 * 停止百度位置监听
	 */
	public void destory() {
		if (mLocClient != null)
			mLocClient.stop();
		if(mSearch!=null)
		{
		   mSearch.destroy();
		}
		if(gpsManager!=null)
		{
			gpsManager =null;
		}
	}

	/**
	 * 初始化信息
	 */
	public void intialInfo() {
		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		// 定位初始化
		mLocClient = new LocationClient(editText.getContext());
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");//返回的定位结果包含地址信息
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
		option.setPriority(LocationClientOption.GpsFirst);
		option.setProdName("xinnongyun");
		option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
		option.disableCache(true);//禁止启用缓存定位
		//option.setPoiNumber(5);	//最多返回POI个数	
		//option.setPoiDistance(1000); //poi查询距离		
		mLocClient.setLocOption(option);
		mLocClient.registerLocationListener(myListener);
		mLocClient.start();
	}

	/**
	 * 获取GPS实例 单例
	 * 
	 * @return
	 */
	public static GpsManager getInstance() {
		if (gpsManager == null) {
			gpsManager = new GpsManager();
		}
		return gpsManager;
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			editText.setText("抱歉,定位失败未能找到结果");
			editText.setTag("lng=0.0&lat=0.0");
			mLocClient.stop();
			return;
		}
		editText.setText(result.getAddress());
		editText.setEnabled(true);
		if(type==1)
		changeFarmInfoImage(initialURLparaAddress());
	}

	/**
	 * 定位SDK监听函数
	 */
	class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			mLocClient.stop();
			if (location == null) {
				editText.setTag("lng=0.0&lat=0.0");
				editText.setText("抱歉,定位失败未能找到结果");
				return;
			}
			
			final LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			if (type == 2) {
				editText.setTag("lng=" + ll.longitude + "&lat=" + ll.latitude);
				editText.setEnabled(true);
				return;
			}
			if (type != 2) {
				// 反Geo搜索
				editText.setTag("lng=" + ll.longitude + "&lat=" + ll.latitude);
				//if(!mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll)))
				{
					new AsyncTask<Void, Void, HashMap<String, Object>>() {
						@Override
						protected HashMap<String, Object> doInBackground(
								Void... params) {
							try {
								SyncHttpVersion syncHttp = new SyncHttpVersion();
								try {
									String locationUrl = "http://api.map.baidu.com/geocoder/v2/?" +
										"ak=vrH5pWiTbrI6AFVnLjbo1E6u&callback=renderReverse&" +
										"location=" 
										+ ll.latitude
										+ ","
										+ ll.longitude
										+ "&output=json&pois=1&" +
										"mcode=08:C0:F5:68:56:E2:AE:A9:D9:1B:15:37:BE:78:76:2A:E6:7C:6A:FB;com.xinnongyun.activity";
									String farmInfo = syncHttp.httpGet(locationUrl);
									HashMap<String, Object> map = JsonUtil.getHashMap(farmInfo.replace("renderReverse&&renderReverse(", "").replace(")", ""));
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
							if(result!=null && result.get("status").toString().equals("0"))
							{
								try {
									HashMap<String,Object> resultMap = JsonUtil.getHashMap(result.get("result").toString());
									editText.setText(resultMap.get("formatted_address").toString());
									if(type==1)
										changeFarmInfoImage(initialURLparaAddress());
								} catch (Exception e) {
									
								}
							}
							
						}
					}.execute();
				}
		
			}
			
		}

		
		@Override
		public void onReceivePoi(BDLocation arg0) {
		}
	}
	
	private void changeFarmInfoImage(String para)
	{
		NetConnectionFarm.FarmPutNetConnection(
				NetUrls.URL_MODIFY_FARM(MySharePreference
						.getValueFromKey(editText.getContext(),
								MySharePreference.FARMID)),
				para,
				"Token "
						+ MySharePreference.getValueFromKey(
								editText.getContext(),
								MySharePreference.ACCOUNTTOKEN),
				new FarmSuccessCallBack() {
					@Override
					public void onSuccess(HashMap<String, Object> map) {
						if (map != null) {
							if (map.keySet().contains("status_code")) {
								ErrorMarkToast.showCallBackToast(
										editText.getContext(), map);
								return;
							}
							// TODO保存农场信息到本地
							MySharePreference.saveFarmInfo(
									editText.getContext(), map);
						} else {
							showToast(R.string.interneterror);
						}
					}
				});
	}
	
	private void showToast(int id) {
		Toast.makeText(editText.getContext(), id, Toast.LENGTH_SHORT).show();
	}
	
	private String initialURLparaAddress()
	{
		
		String para = "name=" + MySharePreference.getValueFromKey(editText.getContext(), MySharePreference.FARMNAME);
		para += "&location=" + editText.getText().toString();
		para += "&" + editText.getTag().toString();
		return para;
	}
}
