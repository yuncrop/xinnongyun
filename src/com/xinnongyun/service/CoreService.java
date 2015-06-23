package com.xinnongyun.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.xinnongyun.config.NetUrls;
import com.xinnongyun.net.NetConnectionTimeLine;
import com.xinnongyun.net.NetConnectionTimeLine.TimeLineSuccessCallBackGetAll;
import com.xinnongyun.net.NetConnectionWeatherStation;
import com.xinnongyun.net.NetConnectionWeatherStation.WeatherSuccessCallBack;
import com.xinnongyun.sqlite.BlowerDaoDBManager;
import com.xinnongyun.sqlite.BlowerTimeLineDaoDBManager;
import com.xinnongyun.sqlite.CollectDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.sqlite.TimeLineDaoDBManager;
import com.xinnongyun.sqlite.WeatherStationDaoDBManager;
import com.xinnongyun.utils.StringUtil;

/**
 * 实时下载数据
 * 
 * @author sm
 * 
 */
public class CoreService extends Service {
	
	public CoreService()
	{}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				getAllCollect();
			}
		}).start();
		
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	/**
	 * 获取盒子Id 并更新时间线 K线表
	 */
	public void getAllCollect()
	{
		try {
			
			CollectDaoDBManager collectDao = new CollectDaoDBManager(this);
			List<HashMap<String, String>> datas = collectDao.getAllCollect();
			if(datas!=null && datas.size() > 0)
			{
				for(HashMap<String, String> map : datas)
				{
					getTimeLine(map.get(TablesColumns.TABLECOLLECTOR_ID).trim());
					//getKLine(map.get(TablesColumns.TABLECOLLECTOR_ID).trim());
				}
			}
			WeatherStationDaoDBManager weatherStationDaoDBManager = new WeatherStationDaoDBManager(this);
			List<HashMap<String, String>> weathers = weatherStationDaoDBManager.getAllWeatherStation();
			if(weathers!=null && weathers.size() > 0)
			{
				for(HashMap<String, String> map : weathers)
				{
					getWeatherStationTimeLine(map.get(TablesColumns.TABLEWEATHERSTATION_ID).toString());
				}
			}
			
			BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(this);
			List<HashMap<String, String>> blowers = blowerDaoDBManager.getAllBlowers();
			if(blowers!=null && blowers.size()>0)
			{
				for(HashMap<String, String> map : blowers)
				{
					getBlowerTimeLine(map.get(TablesColumns.TABLEBLOWER_ID).toString());
				}
			}
			
		} catch (Exception e) {
		}
	}
	
	
	//获取控元最新数据
	private void getBlowerTimeLine(String id)
	{
		//TODO 获取放风机最新数据 
		final BlowerTimeLineDaoDBManager blowerTimeLineDaoDBManager = new BlowerTimeLineDaoDBManager(this);
		NetConnectionWeatherStation.WeatherGetNetConnection(
				NetUrls.getUnitLastestInfo(id)
				,null,null,
				new WeatherSuccessCallBack() {
					@Override
					public void onSuccess(final HashMap<String, Object> result) {
						new Thread(
								new Runnable() {
									@Override
									public void run() {
										if(result!=null && result.size()>2)
										{
											List<HashMap<String, Object>> maps = new ArrayList<HashMap<String,Object>>();
											maps.add(result);
											blowerTimeLineDaoDBManager.insertAllBlowerTimeLine(maps);
										}
									}
								}
								).start();
					}
				}
				);
	}
	
	
	
	/**
	 * 获取小站实时数据
	 * @param weatherStationId
	 */
	private void getWeatherStationTimeLine(String weatherStationId)
	{
		final WeatherStationDaoDBManager weatherStationDaoDBManager = new WeatherStationDaoDBManager(this);
		NetConnectionWeatherStation.WeatherGetNetConnection(
				NetUrls.URL_WEATHER_STATIONSTimeLine(weatherStationId)
				,null,null,
				new WeatherSuccessCallBack() {
					@Override
					public void onSuccess(final HashMap<String, Object> result) {
						new Thread(
								new Runnable() {
									@Override
									public void run() {
										if(result!=null && result.size()>2)
										{
											weatherStationDaoDBManager.insertWeatherStationTimeLine(result);
										}
									}
								}
								).start();
					}
				}
				);
	}
	
	
	
	
	
	/**
	 * 获取时间线   并插入数据库
	 * @param collectId
	 * @param time
	 */
	private void getTimeLine(String collectId)
	{
		String time;
		final TimeLineDaoDBManager timeLineDao = new TimeLineDaoDBManager(this);
		HashMap<String, String> map = timeLineDao.getCollectLastUpdateTime(collectId);
		if(map!=null)
		{
			String lastDate = map.get(TablesColumns.TABLETIMELINE_COLLECTEDAT).toString();
			long minutes = 0;
			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date d2 = df.parse(StringUtil.changeSerTo(lastDate));
				minutes = StringUtil.miniteLength(new Date(),d2);
			} catch (ParseException e) {
			}
			if(minutes <10)
			{
				return;
			}
			else
			{
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日    HH:mm");
				time = StringUtil.dataChangeToT(formatter.format(StringUtil.getSpecifiedDateTimeBySeconds(new  Date(System.currentTimeMillis()), -10*1000)));
			}
		}
		else
		{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日    HH:mm");
			time = StringUtil.dataChangeToT(formatter.format(StringUtil.getSpecifiedDateTimeBySeconds(new  Date(System.currentTimeMillis()), -10*1000)));
		}
		
		//获取时间线
		String para = "collector=" + collectId + "&begin_at=" + time;
		NetConnectionTimeLine.TimeLineGetNetConnection(NetUrls.URL_TIMELINE, para, null,
				new TimeLineSuccessCallBackGetAll() {
					@Override
					public void onSuccess(final List<HashMap<String, Object>> result) {
						new Thread(
								new Runnable() {
									@Override
									public void run() {
										if(result!=null && result.size()>0)
										timeLineDao.insertAllTimeLine(result);
									}
								}
								).start();
					}
				}
				);
	}
	
	
	
}
