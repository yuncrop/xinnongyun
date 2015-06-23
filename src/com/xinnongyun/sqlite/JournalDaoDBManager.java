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
 * 视频日志管理
 * @author sm
 *
 */
public class JournalDaoDBManager {

	private DBHelper dbHelper;
	private SQLiteDatabase dataBase;
	private Context context;
	public JournalDaoDBManager(Context context)
	{
		try {
			this.context = context;
			dbHelper = DBHelper.getInstance(context);
			dataBase = dbHelper.getWritableDatabase();
		} catch (Exception e) {
		}
	}
	

	/**
	 * 获取一条视频记录信息
	 * id
	 * @return
	 */
	public HashMap<String, String> getJournalVideoInfoById(String journalVideoId)
	{
		try {
			String sql = "select * from " + TablesColumns.TABLEJOURNAL + " where " + TablesColumns.TABLEJOURNAL_ID + " = '" + journalVideoId + "'";
			Cursor cursor = dataBase.rawQuery(sql, null);
			HashMap<String, String> map = null;
			String[] names = cursor.getColumnNames();
			if(cursor.moveToNext())
			{
				map = new HashMap<String, String>();
				for (String name : names) {
					map.put(name,cursor.getString(cursor.getColumnIndex(name)));
				}
			}
			cursor.close();
			return map;
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * 获取视频日志记录信息
	 * @return
	 */
	public List<HashMap<String, String>> getAllJournalVideo()
	{
		List<HashMap<String, String>> data = null;
		try {
			String sql = "select * from " + TablesColumns.TABLEJOURNAL + " ORDER BY " + TablesColumns.TABLEJOURNAL_CREATEAT + " desc";
			Cursor cursor = dataBase.rawQuery(sql, null);
			data = new ArrayList<HashMap<String,String>>();
			HashMap<String, String> map;
			String[] names = cursor.getColumnNames();
			while(cursor.moveToNext())
			{
				map = new HashMap<String, String>();
				for (String name : names) {
					map.put(name,cursor.getString(cursor.getColumnIndex(name)));
				}
				if(data.size()>0 && data.get(data.size()-1).get("id").contains(map.get(TablesColumns.TABLEJOURNAL_ID).toString()))
				{
					continue;
				}
				data.add(map);
			}
			cursor.close();
		} catch (Exception e) {
		}
		return data;
	}
	
	/**
	 * 插入数据库表  一条数据
	 * @param result
	 */
	public synchronized void  insertUploadVideo(HashMap<String, Object> result)
	{
		if(result==null || result.keySet().size()==0)
		{
			return;
		}
		try {
			synchronized (DBHelper.getInstance(context)) {
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
				dataBase.insert(TablesColumns.TABLEJOURNAL, null, cv);
				Intent intent = new Intent();  
				intent.setAction(MainActivity.journalInfobroastCast);  
				if(context!=null)
				context.sendBroadcast(intent);
			}
		} catch (SQLException e) {
		}
	}
	
	
	/**
	 * 插入数据库表  多条
	 * @param result
	 */
	public synchronized void insertAllJournal(List<HashMap<String, Object>> result)
	{
		
			try {
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
						dataBase.insert(TablesColumns.TABLEJOURNAL, null, cv);
					} catch (SQLException e) {
						
					}
					
				}
				Intent intent = new Intent();  
				intent.setAction(MainActivity.journalInfobroastCast);  
				if(context!=null)
				context.sendBroadcast(intent);
			} catch (Exception e) {
			}
	}
	
	
	public synchronized void updateJOurnalById(HashMap<String, Object> map)
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
			
			dataBase.update(TablesColumns.TABLEJOURNAL,cv,"id=?",new String[]{map.get("id").toString()});
			Intent intent = new Intent();  
			intent.setAction(MainActivity.journalInfobroastCast);  
			if(context!=null)
			context.sendBroadcast(intent);
		} catch (SQLException e) {
		}
	}
	
	
	public synchronized void deleteJournalById(String id)
	{
		try {
			String sql = "delete from " + TablesColumns.TABLEJOURNAL + " where id = '" + id + "'";
			dataBase.execSQL(sql);
			Intent intent = new Intent();  
			intent.setAction(MainActivity.journalInfobroastCast);  
			if(context!=null)
			context.sendBroadcast(intent);
		} catch (Exception e) {
			
		}
	}
	
	
}
