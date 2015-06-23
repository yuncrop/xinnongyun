package com.xinnongyun.sqlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.xinnongyun.activity.MainActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 环境小站
 * @author sm
 *
 */
public class WeatherStationDaoDBManager {

	private DBHelper dbHelper;
	private SQLiteDatabase dataBase;
	private Context context;
	public WeatherStationDaoDBManager(Context context)
	{
		try {
			this.context = context;
			dbHelper = DBHelper.getInstance(context);
			dataBase = dbHelper.getWritableDatabase();
		} catch (Exception e) {
		}
	}
	
	
	
	/**
	 * 
	 * 获取最后更新时间记录
	 * 
	 * @param id
	 * @return
	 */
	public HashMap<String, String> getWeatherStationLastUpdateTime(String stationId) {
		try {
			String sql = "select * from " + TablesColumns.TABLESTATIONTIMELINR + " where "
					+ TablesColumns.TABLESTATIONTIMELINR_SENSOR + " = '" + stationId
					+ "' ORDER BY " + TablesColumns.TABLESTATIONTIMELINR_CREATEDAT
					+ " DESC limit 1";
			Cursor cursor = dataBase.rawQuery(sql, null);
			HashMap<String, String> map = null;
			String[] names = cursor.getColumnNames();
			if (cursor.moveToNext()) {
				map = new HashMap<String, String>();
				for (String name : names) {
					map.put(name, cursor.getString(cursor.getColumnIndex(name)));
				}
			}
			cursor.close();
			return map;
		} catch (Exception e) {
			return null;
		}
	}
	
	
	
	
	
	
	/**
	 * 获取所有环境小站
	 * @return
	 */
	public List<HashMap<String, String>> getAllWeatherStation()
	{
		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery("select * from " + TablesColumns.TABLEWEATHERSTATION + " group by id", null);
			List<HashMap<String, String>> data = new ArrayList<HashMap<String,String>>();
			HashMap<String, String> map;
			String[] names = cursor.getColumnNames();
			while(cursor.moveToNext())
			{
				map = new HashMap<String, String>();
				for (String name : names) {
					map.put(name,cursor.getString(cursor.getColumnIndex(name)));
				}
				if(data.size()>0 && data.get(data.size()-1).get("id").contains(map.get(TablesColumns.TABLEWEATHERSTATION_ID).toString()))
				{
					continue;
				}
				data.add(map);
			}
			
			return data;
		} catch (Exception e) {
			return null;
		}
		finally
		{
			if(cursor!=null)
			{
				cursor.close();
			}
			
		}
	}
	
	public HashMap<String, String> getWeatherStationBySerial(String weatherStationSerial)
	{
		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery("select * from " + TablesColumns.TABLEWEATHERSTATION+ "  where serial = '" + weatherStationSerial + "'", null);
			HashMap<String, String> map = new HashMap<String, String>();;
			String[] names = cursor.getColumnNames();
			if(cursor.moveToNext())
			{
				for (String name : names) {
					map.put(name,cursor.getString(cursor.getColumnIndex(name)));
				}
			}
			cursor.close();
			return map;
		} catch (Exception e) {
			return null;
		}
		finally
		{
			if(cursor!=null)
			{
				cursor.close();
			}
		}
	}
	
	
	public synchronized void updateWeatherStationById(HashMap<String, Object> map)
	{
		try {
			Iterator<String> keyIter = map.keySet().iterator();
			String key;
			ContentValues cv=new ContentValues();
			while (keyIter.hasNext()) {
				key = (String) keyIter.next();
				if(!TablesColumns.getAllFileds().contains(key))
				{
					continue;
				}
				cv.put(key, map.get(key).toString());
			}
			dataBase.update(TablesColumns.TABLEWEATHERSTATION,cv,"id=?",new String[]{map.get("id").toString()});
			Intent intent = new Intent();  
			intent.setAction(MainActivity.weatherStationInfoChangebroastCast);  
			if(context!=null)
			{
			    context.sendBroadcast(intent);
			}
		} catch (Exception e) {
		}
	}
	
	
	public synchronized void deleteWeatherStationById(String id)
	{
		try {
			String sql = "delete from " + TablesColumns.TABLEWEATHERSTATION + " where id = '" + id + "'";
			dataBase.execSQL(sql);
			Intent intent = new Intent();  
			intent.setAction(MainActivity.weatherStationHeadInfobroastCast);  
			if(context!=null)
			{
			    context.sendBroadcast(intent);
			}
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * 插入数据库表
	 * @param result
	 */
	public synchronized void insertAllWeatherStation(List<HashMap<String, Object>> result)
	{
		for (int i = 0; i < result.size(); i++) {
			try {
				HashMap<String, Object> map = result.get(i);
				Iterator<String> keyIter = map.keySet().iterator();
				String key;
				ContentValues cv=new ContentValues();
				while (keyIter.hasNext()) {
					key = (String) keyIter.next();
					if(!TablesColumns.getAllFileds().contains(key))
					{
						continue;
					}
					if (key != null) {
						cv.put(key, map.get(key).toString());
					}
				}
				dataBase.insert(TablesColumns.TABLEWEATHERSTATION, null, cv);
				Intent intent = new Intent();  
				intent.setAction(MainActivity.weatherStationHeadInfobroastCast);  
				if(context!=null)
				{
				    context.sendBroadcast(intent);
				}
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * 插入数据库表 时间线
	 * @param result
	 */
	public synchronized void insertWeatherStationTimeLine(HashMap<String, Object> result)
	{
		try {
			HashMap<String, Object> map = result;
			Iterator<String> keyIter = map.keySet().iterator();
			String key;
			ContentValues cv=new ContentValues();
			while (keyIter.hasNext()) {
				key = (String) keyIter.next();
				if(!TablesColumns.getAllFileds().contains(key))
				{
					continue;
				}
				if (key != null) {
					cv.put(key, map.get(key).toString());
				}
			}
			dataBase.insert(TablesColumns.TABLESTATIONTIMELINR, null, cv);
			Intent intent = new Intent();  
			intent.setAction(MainActivity.stationTileLineHeadInfobroastCast);  
			if(context!=null)
			{
			    context.sendBroadcast(intent);
			}
		} catch (Exception e) {
		}
		
	}

}
