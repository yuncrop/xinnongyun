package com.xinnongyun.sqlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.xinnongyun.activity.MainActivity;
import com.xinnongyun.sharepreference.MySharePreference;

/**
 * 视频上传队列
 * @author sm
 *
 */
public class VideoUploadDaoDBManager {

	private DBHelper dbHelper;
	private SQLiteDatabase dataBase;
	private Context context;
	public VideoUploadDaoDBManager(Context context)
	{
		try {
			this.context = context;
			dbHelper = DBHelper.getInstance(context);
			dataBase = dbHelper.getWritableDatabase();
		} catch (Exception e) {
		}
	}
	
	/**
	 * 获取所有信息  降序
	 * @return
	 */
	public List<HashMap<String, String>> getAllVideoUploadList()
	{
		Cursor cursor = null;
		try {
			String sql = "select * from " + TablesColumns.TABLEVIDEOUPLOAD + 
					" where " + TablesColumns.TABLEVIDEOUPLOAD_FARM + " = '" + MySharePreference.getValueFromKey(context, MySharePreference.FARMID) +
					"' ORDER BY " + TablesColumns.TABLEVIDEOUPLOAD_CREATETIME +" DESC";
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
	 * 修改记录状态信息
	 * @param qiniuToken
	 * @param state
	 */
	public synchronized void changeUpLoadVideoState(String qiniuToken, String state)
	{
		try {
			String sql = "update " + TablesColumns.TABLEVIDEOUPLOAD + " set " +  TablesColumns.TABLEVIDEOUPLOAD_STATE + " = '" + state + "' where " +
					TablesColumns.TABLEVIDEOUPLOAD_QNTOKEN + " = '" + qiniuToken + "'";
			dataBase.execSQL(sql);
			Intent intent = new Intent();  
			intent.setAction(MainActivity.noticeInfobroastCast);  
			if(context!=null)
			{
			    context.sendBroadcast(intent);
			}
		} catch (SQLException e) {
		}
	}
	
	/**
	 * 修改记录进度
	 * @param qiniuToken
	 * @param percent
	 */
	public synchronized void changeUpLoadVideoPercent(String qiniuToken, String percent)
	{
		try {
			String sql = "update " + TablesColumns.TABLEVIDEOUPLOAD + " set " +  
					TablesColumns.TABLEVIDEOUPLOAD_PERCENT + " = '" + percent + "' where " + 
					TablesColumns.TABLEVIDEOUPLOAD_QNTOKEN + " = '" + qiniuToken + "'";
			dataBase.execSQL(sql);
			Intent intent = new Intent();  
			intent.setAction(MainActivity.noticeInfobroastCast);  
			if(context!=null)
			{
			    context.sendBroadcast(intent);
			}
		} catch (SQLException e) {
		}
	}
	
	
	public synchronized void deleteAllInfos()
	{
		try {
			String sqlJournalUpload ="delete from " + TablesColumns.TABLEVIDEOUPLOAD;
			dataBase.execSQL(sqlJournalUpload);
			Intent intent = new Intent();  
			intent.setAction(MainActivity.noticeInfobroastCast);  
			if(context!=null)
			{
			    context.sendBroadcast(intent);
			}
		} catch (SQLException e) {
		}
	}
	
	/**
	 * 删除一条记录
	 * @param qiniuToken
	 * @param type
	 */
	public synchronized void delleteUpLoadVideoItentByToken(String qiniuToken)
	{
		try {
			String sql = "delete from " + TablesColumns.TABLEVIDEOUPLOAD + " where " + 
					TablesColumns.TABLEVIDEOUPLOAD_QNTOKEN + " = '" + qiniuToken + "'";
			dataBase.execSQL(sql);
			Intent intent = new Intent();  
			intent.setAction(MainActivity.noticeInfobroastCast);  
			if(context!=null)
			{
			    context.sendBroadcast(intent);
			}
		} catch (SQLException e) {
		}
	}
	
	/**
	 * 插入数据库表  一条数据
	 * @param result
	 */
	public synchronized boolean  insertUploadVideo(HashMap<String, Object> result)
	{
		if(result==null || result.keySet().size()==0)
		{
			return false;
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
				if(dataBase.insert(TablesColumns.TABLEVIDEOUPLOAD, null, cv)<0)
				{
					return false;
				}
				Intent intent = new Intent();  
				intent.setAction(MainActivity.noticeInfobroastCast);  
				if(context!=null)
				{
				    context.sendBroadcast(intent);
				}
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}
	

}
