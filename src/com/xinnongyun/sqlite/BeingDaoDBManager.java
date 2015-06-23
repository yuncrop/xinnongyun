package com.xinnongyun.sqlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 作物
 * @author sm
 *
 */
public class BeingDaoDBManager {

	private DBHelper dbHelper;
	private SQLiteDatabase dataBase;
	private Context context;
	public BeingDaoDBManager(Context context)
	{
		try {
			this.context = context;
			dbHelper = DBHelper.getInstance(context);
			dataBase = dbHelper.getWritableDatabase();
		} catch (Exception e) {
		}
	}
	
	
	/**
	 * 获取being
	 * @param id
	 * @return
	 */
	public HashMap<String, String> getBeingById(String id)
	{
		HashMap<String, String> map = null;
		Cursor cursor = null;
		try {
			String sql = "select * from " + TablesColumns.TABLEBEING + " where  id = '" + id + "'";
			cursor = dataBase.rawQuery(sql, null);
			map = new HashMap<String, String>();
			String[] names = cursor.getColumnNames();
			if(cursor.moveToNext())
			{
				for (String name : names) {
					map.put(name,cursor.getString(cursor.getColumnIndex(name)));
				}
			}
			
		} catch (Exception e) {
		}
		finally
		{
			if(cursor!=null)
			cursor.close();
		}
		return map;
	}
	
	
	
	
	
	/**
	 * 获取所有作物信息
	 * @param 作物类型
	 * @return
	 */
	public List<HashMap<String, String>> getAllBeing(String type)
	{
		Cursor cursor = null;
		try {
			String sql;
			if(type==null)
			{
				sql = "select distinct "+
						TablesColumns.TABLEBEING_ID +"," +
						TablesColumns.TABLEBEING_IMAGE +"," +
						TablesColumns.TABLEBEING_KINGDOM +"," +
						TablesColumns.TABLEBEING_NAME +"" 
						+" from " + TablesColumns.TABLEBEING;
			}
			else
			{
				sql = "select distinct " +
						TablesColumns.TABLEBEING_ID +"," +
						TablesColumns.TABLEBEING_IMAGE +"," +
						TablesColumns.TABLEBEING_KINGDOM +"," +
						TablesColumns.TABLEBEING_NAME +"" 
						+ " from " + TablesColumns.TABLEBEING + " where id in (select distinct id from " + TablesColumns.TABLEBEING + " group by id) and kingdom = '" + type + "'";
			}
			cursor = dataBase.rawQuery(sql, null);
			List<HashMap<String, String>> data = new ArrayList<HashMap<String,String>>();
			HashMap<String, String> map;
			String[] names = cursor.getColumnNames();
			while(cursor.moveToNext())
			{
				map = new HashMap<String, String>();
				for (String name : names) {
					map.put(name,cursor.getString(cursor.getColumnIndex(name)));
				}
				data.add(map);
			}
			cursor.close();
			return data;
		} catch (Exception e) {
			return null;
		}finally
		{
			if(cursor!=null)
			cursor.close();
		}
	}
	
	
	/**
	 * 插入数据库表
	 * @param result
	 */
	public synchronized void  insertAllBeing(List<HashMap<String, Object>> result)
	{
		try {
		synchronized (DBHelper.getInstance(context)) {
			
				for (int i = 0; i < result.size(); i++) {
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
					try {
						dataBase.insert(TablesColumns.TABLEBEING, null, cv);
					} catch (Exception e) {
					}
				}
			
		}
		} catch (Exception e) {
		}
	}

}
