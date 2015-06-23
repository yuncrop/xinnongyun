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
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * 中控 
 * @author sm
 *
 */
public class ControlCenterDaoDBManager {

	private DBHelper dbHelper;
	private SQLiteDatabase dataBase;
	private Context context;
	public ControlCenterDaoDBManager(Context context)
	{
		try {
			this.context = context;
			dbHelper = DBHelper.getInstance(context);
			dataBase = dbHelper.getWritableDatabase();
		} catch (Exception e) {
		}
	}
	
	/**
	 * 获取所有中控信息
	 * @return
	 */
	public List<HashMap<String, String>> getAllControlCenters()
	{
		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery("select * from " + TablesColumns.TABLECONTROLCENTER + " group by id", null);
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
			return new ArrayList<HashMap<String,String>>();
		}
		finally
		{
			if(cursor!=null)
			{
				cursor.close();
			}
			
		}
	}
	
	
	
	
	
	public HashMap<String, String> getControlCenterBySerial(String serial)
	{
		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery("select * from " + TablesColumns.TABLECONTROLCENTER + "  where serial = '" + serial + "'", null);
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
	
	
	
	
	
	public HashMap<String, String> getControlCenterById(String ccId)
	{
		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery("select * from " + TablesColumns.TABLECONTROLCENTER + "  where id = '" + ccId + "'", null);
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
	
	
	public synchronized void updateControlCenterById(HashMap<String, Object> map)
	{
		try {
			
			Iterator<String> keyIter = map.keySet().iterator();
			String key;
			ContentValues cv=new ContentValues();
			while (keyIter.hasNext()) {
				key = (String) keyIter.next();
				cv.put(key, map.get(key).toString());
			}
			dataBase.update(TablesColumns.TABLECONTROLCENTER,cv,"id=?",new String[]{map.get("id").toString()});
			Intent intent = new Intent();  
			intent.setAction(MainActivity.controlcenterbroastCast);  
			if(context!=null)
			context.sendBroadcast(intent);
		} catch (Exception e) {
		}
	}
	
	
	public synchronized void deleteControlCenterById(String id)
	{
		try {
			
			String sql = "delete from " + TablesColumns.TABLECONTROLCENTER + " where id = '" + id + "'";
			dataBase.execSQL(sql);
			
			String sql2 = "delete from " + TablesColumns.TABLEBLOWER + " where " + TablesColumns.TABLEBLOWER_CCID + " = '" + id + "'";
			dataBase.execSQL(sql2);
			Intent intent = new Intent();  
			intent.setAction(MainActivity.controlcenterbroastCast);  
			if(context!=null)
			context.sendBroadcast(intent);
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * 插入中控数据库表
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
				dataBase.insert(TablesColumns.TABLECONTROLCENTER, null, cv);
			} catch (SQLException e) {
			}
		}
		
		Intent intent = new Intent();  
		intent.setAction(MainActivity.controlcenterbroastCast);  
		if(context!=null)
		context.sendBroadcast(intent);
	}

	
	
}
