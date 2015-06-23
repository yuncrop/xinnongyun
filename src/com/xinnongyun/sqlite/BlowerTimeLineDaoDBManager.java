package com.xinnongyun.sqlite;

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
 * 放风机时间线
 * 
 * @author sm
 * 
 */
public class BlowerTimeLineDaoDBManager {

	private DBHelper dbHelper;
	private SQLiteDatabase dataBase;
	private Context context;

	public BlowerTimeLineDaoDBManager(Context context) {
		try {
			this.context = context;
			dbHelper = DBHelper.getInstance(context);
			dataBase = dbHelper.getWritableDatabase();
		} catch (Exception e) {
		}
	}

	
	/**
	 * 
	 * 获取放风机最后更新时间
	 * 
	 * @param id
	 * @return
	 */
	public HashMap<String, String> getCollectLastUpdateTime(String blowerId) {
		try {
			String sql = "select * from " + TablesColumns.TABLEBBLOWERENV + " where "
					+ TablesColumns.TABLEBBLOWERENV_BLOWERID + " = '" + blowerId
					+ "' ORDER BY " + TablesColumns.TABLEBBLOWERENV_CREATEDAT
					+ " DESC limit 1";
			HashMap<String, String> map = new HashMap<String, String>();
			Cursor cursor = dataBase.rawQuery(sql, null);
			
			String[] names = cursor.getColumnNames();
			if (cursor.moveToNext()) {
				
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
	 * 
	 * 获取放风机最后更新时间
	 * 
	 * @param id
	 * @return
	 */
	public HashMap<String, String> getCollectLastUpdateTimeCC(String blowerIds) {
		try {
			
			String ids[] = blowerIds.split(" ");
			String idsStr = "";
			for(int i= 0;i<ids.length;i++)
			{
				if(i == (ids.length-1))
				{
					idsStr += "'" + ids[i] + "'";
				}
				else
				{
					idsStr += "'" + ids[i] + "',";
				}
			}
			String sql = "select * from " + TablesColumns.TABLEBBLOWERENV + " where "
					+ TablesColumns.TABLEBBLOWERENV_BLOWERID + " in (" + idsStr
					+ ") ORDER BY " + TablesColumns.TABLEBBLOWERENV_CREATEDAT
					+ " DESC limit 1";
			HashMap<String, String> map = new HashMap<String, String>();
			Cursor cursor = dataBase.rawQuery(sql, null);
			
			String[] names = cursor.getColumnNames();
			if (cursor.moveToNext()) {
				
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

//	String sql = "update " + TablesColumns.TABLENOTICE + " set " +  TablesColumns.TABLENOTICE_READAT + " = '" + date.replace(" ", "T") + "' where " 
//			+TablesColumns.TABLENOTICE_READAT+ " = 'null' and " + TablesColumns.TABLENOTICE_SIGNAL + " not in ('like','comment')" ;

	/**
	 * 插入数据库表
	 * 
	 * @param result
	 */
	public synchronized void insertAllBlowerTimeLine(
			List<HashMap<String, Object>> result) {
		if (result != null) {
			for (int i = 0; i < result.size(); i++) {
				try {
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
							if (count == 0) {
								cloumns += "'" + key + "'";
								cloumnValues += "'" + map.get(key).toString()
										+ "'";
								count++;
							} else {
								cloumns += ",'" + key + "'";
								cloumnValues += ",'" + map.get(key).toString()
										+ "'";
							}

						}
					}
					String sql = "insert into " + TablesColumns.TABLEBBLOWERENV
							+ " " + "(" + cloumns + ") values (" + cloumnValues
							+ ")";
					dataBase.execSQL(sql);
				} catch (SQLException e) {
				}
			}
		}
		try {
			Intent intent = new Intent();
			intent.setAction(MainActivity.controlcenterbroastCast);
			if(context !=null)
			context.sendBroadcast(intent);
		} catch (Exception e) {
			
		}
	}
}
