package com.xinnongyun.sqlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xinnongyun.activity.MainActivity;

/**
 * 放风机
 * @author sm
 *
 */
public class BlowerDaoDBManager {

	private DBHelper dbHelper;
	private SQLiteDatabase dataBase;
	private Context context;
	public BlowerDaoDBManager(Context context)
	{
		try {
			this.context = context;
			dbHelper = DBHelper.getInstance(context);
			dataBase = dbHelper.getWritableDatabase();
		} catch (Exception e) {
		}
	}
	
	
	/**
	 * 获取所有设备
	 * @return
	 */
	public List<HashMap<String, String>> getAllBlowers()
	{
		List<HashMap<String, String>> data = new ArrayList<HashMap<String,String>>();
		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery("select * from " + TablesColumns.TABLEBLOWER + " group by id", null);
			
			HashMap<String, String> map;
			String[] names = cursor.getColumnNames();
			while(cursor.moveToNext())
			{
				map = new HashMap<String, String>();
				for (String name : names) {
					map.put(name,cursor.getString(cursor.getColumnIndex(name)));
				}
				
				if(data.size()>0 && data.get(data.size()-1).get("id").contains(map.get(TablesColumns.TABLENOTICE_ID).toString()))
				{
					continue;
				}
				data.add(map);
			}
			
			return data;
		} catch (Exception e) {
			return data;
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
	 * 获取中控下所有设备
	 * @return
	 */
	public List<HashMap<String, String>> getAllBlowersByCCId(String ccId)
	{
		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery("select * from " + TablesColumns.TABLEBLOWER + " where " + TablesColumns.TABLEBLOWER_CCID + " = '" + ccId + "' group by id", null);
			List<HashMap<String, String>> data = new ArrayList<HashMap<String,String>>();
			HashMap<String, String> map;
			String[] names = cursor.getColumnNames();
			while(cursor.moveToNext())
			{
				map = new HashMap<String, String>();
				for (String name : names) {
					map.put(name,cursor.getString(cursor.getColumnIndex(name)));
				}
				
				if(data.size()>0 && data.get(data.size()-1).get("id").contains(map.get(TablesColumns.TABLENOTICE_ID).toString()))
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
	
	public HashMap<String, String> getBlowerById(String blowerId)
	{
		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery("select * from " + TablesColumns.TABLEBLOWER + "  where id = '" + blowerId + "'", null);
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
	
	
	public synchronized void updateBlowerById(HashMap<String, Object> map)
	{
		try {
			Iterator<String> keyIter = map.keySet().iterator();
			String key;
			ContentValues cv=new ContentValues();
			while (keyIter.hasNext()) {
				key = (String) keyIter.next();
				cv.put(key, map.get(key).toString());
			}
			dataBase.update(TablesColumns.TABLEBLOWER,cv,"id=?",new String[]{map.get("id").toString()});
			Intent intent = new Intent();
			intent.setAction(MainActivity.controlcenterbroastCast);  
			if(context!=null)
			context.sendBroadcast(intent);
		} catch (Exception e) {
		}
	}
	
	
	public synchronized void deleteBlowerById(String id)
	{
		try {
			String sql = "delete from " + TablesColumns.TABLEBLOWER + " where id = '" + id + "'";
			dataBase.execSQL(sql);
			Intent intent = new Intent();  
			intent.setAction(MainActivity.controlcenterbroastCast);  
			if(context!=null)
			context.sendBroadcast(intent);
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * 插入数据库表
	 * @param result
	 */
	public synchronized void insertAllControlCenter(List<HashMap<String, Object>> result)
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
				dataBase.insert(TablesColumns.TABLEBLOWER, null, cv);
			} catch (Exception e) {
			}
		}
		Intent intent = new Intent();  
		intent.setAction(MainActivity.controlcenterbroastCast);  
		if(context!=null)
		context.sendBroadcast(intent);
	}

	
	
}
