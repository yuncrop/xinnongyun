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
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * 盒子
 * @author sm
 *
 */
public class CollectDaoDBManager {

	private DBHelper dbHelper;
	private SQLiteDatabase dataBase;
	private Context context;
	public CollectDaoDBManager(Context context)
	{
		try {
			this.context = context;
			dbHelper = DBHelper.getInstance(context);
			dataBase = dbHelper.getWritableDatabase();
		} catch (Exception e) {
		}
	}
	
	/**
	 * 获取所有盒子信息
	 * @param 作物类型
	 * @return
	 */
	public List<HashMap<String, String>> getAllCollect()
	{
		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery("select * from " + TablesColumns.TABLECOLLECTOR + " group by id", null);
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
	
	public HashMap<String, String> getCollectById(String collectId)
	{
		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery("select * from " + TablesColumns.TABLECOLLECTOR + "  where id = '" + collectId + "'", null);
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
	
	
	public synchronized void updateCollectById(HashMap<String, Object> map)
	{
		try {
			Iterator<String> keyIter = map.keySet().iterator();
			String key;
			ContentValues cv=new ContentValues();
			while (keyIter.hasNext()) {
				key = (String) keyIter.next();
				cv.put(key, map.get(key).toString());
			}
			dataBase.update(TablesColumns.TABLECOLLECTOR,cv,"id=?",new String[]{map.get("id").toString()});
			Intent intent = new Intent();  
			intent.setAction(MainActivity.farmingNewbroastCast);  
			if(context!=null)
			context.sendBroadcast(intent);
		} catch (SQLException e) {
		}
	}
	
	
	public synchronized void deleteCollectById(String id)
	{
		try {
			String sql = "delete from " + TablesColumns.TABLECOLLECTOR + " where id = '" + id + "'";
			dataBase.execSQL(sql);
			Intent intent = new Intent();  
			intent.setAction(MainActivity.farmingNewbroastCast);  
			if(context!=null)
			context.sendBroadcast(intent);
		} catch (SQLException e) {
			
		}
	}
	
	/**
	 * 插入数据库表
	 * @param result
	 */
	public synchronized void insertAllCollect(List<HashMap<String, Object>> result)
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
				dataBase.insert(TablesColumns.TABLECOLLECTOR, null, cv);
			} catch (SQLException e) {
			}
		}
		Intent intent = new Intent();  
		intent.setAction(MainActivity.farmingNewbroastCast);  
		if(context!=null)
		context.sendBroadcast(intent);
	}

}
