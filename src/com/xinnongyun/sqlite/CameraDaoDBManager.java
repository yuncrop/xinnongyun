package com.xinnongyun.sqlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.xinnongyun.activity.MainActivity;
import com.xinnongyun.atyfragment.MainFragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 摄像头
 * @author sm
 *
 */
public class CameraDaoDBManager {

	private DBHelper dbHelper;
	private SQLiteDatabase dataBase;
	private Context context;
	public CameraDaoDBManager(Context context)
	{
		try {
			this.context = context;
			dbHelper = DBHelper.getInstance(context);
			dataBase = dbHelper.getWritableDatabase();
		} catch (Exception e) {
		}
	}
	
	
	
	/**
	 * 获取摄像头列表
	 * @return
	 */
	public List<HashMap<String, String>> getAllCameraList()
	{
		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery("select * from " + TablesColumns.TABLECAMERA + " group by id", null);
			List<HashMap<String, String>> data = new ArrayList<HashMap<String,String>>();
			HashMap<String, String> map;
			String[] names = cursor.getColumnNames();
			while(cursor.moveToNext())
			{
				map = new HashMap<String, String>();
				for (String name : names) {
					map.put(name,cursor.getString(cursor.getColumnIndex(name)));
				}
				if(data.size()>0 && data.get(data.size()-1).get("id").contains(map.get(TablesColumns.TABLECAMERA_ID).toString()))
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
	
	public HashMap<String, String> getCameraInfoBySerial(String cameraSerial)
	{
		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery("select * from " + TablesColumns.TABLECAMERA+ "  where device_serial = '" + cameraSerial + "'", null);
			HashMap<String, String> map = new HashMap<String, String>();
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
	
	
	public HashMap<String, String> getCameraInfoById(String id)
	{
		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery("select * from " + TablesColumns.TABLECAMERA+ "  where id = '" + id + "'", null);
			HashMap<String, String> map = new HashMap<String, String>();
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
	
	/**
	 * 
	 * @param map
	 * @param type 1为name  2为publish
	 */
	public synchronized void updateCameraById(HashMap<String, Object> map,int type)
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
			dataBase.update(TablesColumns.TABLECAMERA,cv,"id=?",new String[]{map.get("id").toString()});
			if(type==1)
			{
				MainFragment.cameraType = 1;
				Intent intent = new Intent();  
				intent.setAction(MainActivity.weatherStationInfoChangebroastCast);  
				if(context!=null)
				{
				    context.sendBroadcast(intent);
				}
				
			}
		} catch (Exception e) {
		}
	}
	
	
	public synchronized void deleteCmareaById(String id)
	{
		try {
			String sql = "delete from " + TablesColumns.TABLECAMERA + " where id = '" + id + "'";
			dataBase.execSQL(sql);
			Intent intent = new Intent();  
			intent.setAction(MainActivity.weatherStationHeadInfobroastCast);  
			if(context!=null)
			{
				MainFragment.cameraType = 1;
			    context.sendBroadcast(intent);
			}
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * 插入数据库表
	 * 
	 * @param result
	 */
	public synchronized void insertAllCameras(
			List<HashMap<String, Object>> result) {
		try {
			for (int i = 0; i < result.size(); i++) {

				HashMap<String, Object> map = result.get(i);
				Iterator<String> keyIter = map.keySet().iterator();
				String key;
				ContentValues cv = new ContentValues();
				while (keyIter.hasNext()) {
					key = (String) keyIter.next();
					if (!TablesColumns.getAllFileds().contains(key)) {
						continue;
					}
					if (key != null) {
						cv.put(key, map.get(key).toString());
					}
				}
				dataBase.insert(TablesColumns.TABLECAMERA, null, cv);

			}
			Intent intent = new Intent();
			intent.setAction(MainActivity.weatherStationHeadInfobroastCast);
			if (context != null) {
				MainFragment.cameraType = 1;
				context.sendBroadcast(intent);
			}
		} catch (Exception e) {
		}
	}
	


}
