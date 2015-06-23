package com.xinnongyun.sqlite;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.xinnongyun.activity.MainActivity;

/**
 * 通知
 * @author sm
 *
 */
public class NoticeDaoDBManager {

	private DBHelper dbHelper;
	private SQLiteDatabase dataBase;
	private Context context;
	public NoticeDaoDBManager(Context context)
	{
		try {
			this.context = context;
			dbHelper = DBHelper.getInstance(context);
			dataBase = dbHelper.getWritableDatabase();
		} catch (Exception e) {
		}
	}
	
	
	/**
	 * 获取通知
	 * @param id
	 * @return
	 */
	public HashMap<String, String> getNoticeById(String id)
	{
		HashMap<String, String> map = null;
		Cursor cursor = null;
		try {
			String sql = "select distinct " +
			TablesColumns.TABLENOTICE_ID + "," +
			TablesColumns.TABLENOTICE_SUBJECT + "," +
			TablesColumns.TABLENOTICE_SIGNAL + "," +
			TablesColumns.TABLENOTICE_CONTEXT + "," +
			TablesColumns.TABLENOTICE_USER + "," +
			TablesColumns.TABLENOTICE_ALERT + "," +
			TablesColumns.TABLENOTICE_OPERATOR + "," +
			TablesColumns.TABLENOTICE_CREATEDAT + "," +
			TablesColumns.TABLENOTICE_READAT
					+ " from " + TablesColumns.TABLENOTICE + " where id = '" + id + "'";
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
	 * 通知修改为已读
	 */
	public synchronized void changeNoticeAlreadyRead()
	{
		try {
			SimpleDateFormat sDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());
			String sql = "update " + TablesColumns.TABLENOTICE + " set " +  TablesColumns.TABLENOTICE_READAT + " = '" + date.replace(" ", "T") + "' where " 
			+TablesColumns.TABLENOTICE_READAT+ " = 'null' and " + TablesColumns.TABLENOTICE_SIGNAL + " not in ('like','comment')" ;
			dataBase.execSQL(sql);
			Intent intent = new Intent();  
			intent.setAction(MainActivity.noticeInfobroastCast);  
			if(context!=null)
			context.sendBroadcast(intent);
		} catch (SQLException e) {
		}
	}
	
	/**
	 * 视频通知修改为已读
	 */
	public synchronized void changeNotifyAlreadyRead()
	{
		try {
			SimpleDateFormat sDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());
			String sql = "update " + TablesColumns.TABLENOTICE + " set " +  TablesColumns.TABLENOTICE_READAT + " = '" + date.replace(" ", "T") + "' where " 
			+TablesColumns.TABLENOTICE_READAT+ " = 'null' and " + TablesColumns.TABLENOTICE_SIGNAL + " in ('like','comment')" ;
			dataBase.execSQL(sql);
			Intent intent = new Intent();  
			intent.setAction(MainActivity.noticeInfobroastCast);  
			if(context!=null)
			context.sendBroadcast(intent);
		} catch (SQLException e) {
		}
	}
	
	
	//获取所有视频未读消息
	public List<HashMap<String, String>> getAllNotifyNoRead()
	{
		Cursor cursor = null;
		try {
			String sql = "select distinct " +
					TablesColumns.TABLENOTICE_ID + "," +
					TablesColumns.TABLENOTICE_SUBJECT + "," +
					TablesColumns.TABLENOTICE_SIGNAL + "," +
					TablesColumns.TABLENOTICE_CONTEXT + "," +
					TablesColumns.TABLENOTICE_USER + "," +
					TablesColumns.TABLENOTICE_ALERT + "," +
					TablesColumns.TABLENOTICE_OPERATOR + "," +
					TablesColumns.TABLENOTICE_CREATEDAT + "," +
					TablesColumns.TABLENOTICE_READAT +
			" from " + TablesColumns.TABLENOTICE +" where " + TablesColumns.TABLENOTICE_READAT+ " = 'null' and " + TablesColumns.TABLENOTICE_SIGNAL + " in ('like','comment') ORDER BY " + TablesColumns.TABLENOTICE_CREATEDAT +" DESC";
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
				boolean isAlreadContain = false;
				for(HashMap<String, String> alMap : data)
				{
					if(alMap.containsValue(map.get(TablesColumns.TABLENOTICE_ID).toString()))
					{
						isAlreadContain = true;
						break;
					}
				}
				if(isAlreadContain)
				{
					continue;
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
	 * 获取所有非视频信息
	 * @return
	 */
	public List<HashMap<String, String>> getAllNotice()
	{
		Cursor cursor = null;
		try {
			String sql = "select distinct " +
					TablesColumns.TABLENOTICE_ID + "," +
					TablesColumns.TABLENOTICE_SUBJECT + "," +
					TablesColumns.TABLENOTICE_SIGNAL + "," +
					TablesColumns.TABLENOTICE_CONTEXT + "," +
					TablesColumns.TABLENOTICE_USER + "," +
					TablesColumns.TABLENOTICE_ALERT + "," +
					TablesColumns.TABLENOTICE_OPERATOR + "," +
					TablesColumns.TABLENOTICE_CREATEDAT + "," +
					TablesColumns.TABLENOTICE_READAT +
			" from " + TablesColumns.TABLENOTICE +" where " + TablesColumns.TABLENOTICE_SIGNAL + " not in ('like','comment') ORDER BY " + TablesColumns.TABLENOTICE_CREATEDAT +" DESC";
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
				if(data.size()>0 && data.get(data.size()-1).get("id").contains(map.get(TablesColumns.TABLENOTICE_ID).toString()))
				{
					continue;
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
	
	public List<String> getAllNoReadNoticeIds()
	{
		Cursor cursor = null;
		try {
			String sql = "select distinct id from " + TablesColumns.TABLENOTICE +" where " +TablesColumns.TABLENOTICE_READAT+ " = 'null' and " + TablesColumns.TABLENOTICE_SIGNAL + " not in ('like','comment')";
			cursor = dataBase.rawQuery(sql, null);
			List<String> data = new ArrayList<String>();
			String[] names = cursor.getColumnNames();
			while(cursor.moveToNext())
			{
				for (String name : names) {
					data.add(cursor.getString(cursor.getColumnIndex(name)));
				}
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
	 * 修改记录信息
	 * @param farmingId
	 * @param type
	 */
	public synchronized void changeFarmingBeingCollects(String noticeId, String readedAt)
	{
		try {
			String sql = "update " + TablesColumns.TABLENOTICE + " set " +  TablesColumns.TABLENOTICE_READAT + " = '" + readedAt + "' where id = '" + noticeId + "'";
			dataBase.execSQL(sql);
			
			Intent intent = new Intent();  
			intent.setAction(MainActivity.noticeInfobroastCast);  
			if(context!=null)
			context.sendBroadcast(intent);
		} catch (SQLException e) {
		}
	}
	
	
	/**
	 * 插入数据库表  一条数据
	 * @param result
	 */
	public synchronized void  insertNotice(HashMap<String, Object> result)
	{
		if(result==null || result.keySet().size()==0)
		{
			return;
		}
		try {
			synchronized (DBHelper.getInstance(context)) {
				String cloumns = "";
				String cloumnValues = "";
				HashMap<String, Object> map = result;
				Iterator<String> keyIter = map.keySet().iterator();
				String key;
				int count = 0;
				while (keyIter.hasNext()) {
					key = (String) keyIter.next();
					if(!TablesColumns.getAllFileds().contains(key))
					{
						continue;
					}
					if (key != null) {
						if (count == 0) {
							cloumns += "'" + key + "'";
							cloumnValues += "'" + map.get(key).toString() + "'";
							count++;
						} else {
							cloumns += ",'" + key + "'";
							cloumnValues += ",'" + map.get(key).toString()
									+ "'";
						}

					}
				}
				String sql = "insert into " + TablesColumns.TABLENOTICE + " "
						+ "(" + cloumns + ") values (" + cloumnValues + ")";
				dataBase.execSQL(sql);
				Intent intent = new Intent();  
				intent.setAction(MainActivity.noticeInfobroastCast);  
				if(context!=null)
				{
				    context.sendBroadcast(intent);
				}
			}
		} catch (SQLException e) {
		}
	}
	
	
	/**
	 * 插入数据库表
	 * @param result
	 */
	public synchronized void  insertAllNotice(List<HashMap<String, Object>> result)
	{
		try {
		synchronized (DBHelper.getInstance(context)) {
			
				for (int i = 0; i < result.size(); i++) {
					String cloumns = "";
					String cloumnValues = "";
					HashMap<String, Object> map = result.get(i);
					Iterator<String> keyIter = map.keySet().iterator();
					String key;
					int count = 0;
					while (keyIter.hasNext()) {
						key = (String) keyIter.next();
						if(!TablesColumns.getAllFileds().contains(key))
						{
							continue;
						}
						if (key != null) {
							if(count == 0)
							{
								cloumns += "'" +key + "'";
								cloumnValues += "'" + map.get(key).toString() +"'";
								count++;
							}
							else
							{
								cloumns += ",'" + key + "'";
								cloumnValues += ",'" + map.get(key).toString() + "'";
							}
							
						}
					}
					
					String sql = "insert into " + TablesColumns.TABLENOTICE + " " +
							"("+ cloumns +") values ("+ cloumnValues +")";
					dataBase.execSQL(sql);
				}
				Intent intent = new Intent();  
				intent.setAction(MainActivity.noticeInfobroastCast);  
				if(context!=null)
				{
				    context.sendBroadcast(intent);
				}
		}
		} catch (SQLException e) {
		}
	}

}
