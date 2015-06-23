package com.xinnongyun.sqlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.xinnongyun.activity.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * 时间线
 * 
 * @author sm
 * 
 */
public class TimeLineDaoDBManager {

	private DBHelper dbHelper;
	private SQLiteDatabase dataBase;
	private Context context;

	public TimeLineDaoDBManager(Context context) {
		try {
			this.context = context;
			dbHelper = DBHelper.getInstance(context);
			dataBase = dbHelper.getWritableDatabase();
		} catch (Exception e) {
		}
	}

	
	/**
	 * 
	 * 获取盒子最后更新时间
	 * 
	 * @param id
	 * @return
	 */
	public HashMap<String, String> getCollectLastUpdateTime(String collectId) {
		try {
			String sql = "select * from " + TablesColumns.TABLETIMELINE + " where "
					+ TablesColumns.TABLETIMELINE_COLLECTOR + " = '" + collectId
					+ "' ORDER BY " + TablesColumns.TABLETIMELINE_COLLECTEDAT
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
	 * 返回某一段时间内的时间线
	 * 
	 * @param collectId
	 *            盒子Id
	 * @param fromTime
	 *            开始时间
	 * @param toTime
	 *            结束时间
	 * @return
	 */
	public List<HashMap<String, Object>> getTimeLineFromTo(String collectId,
			String fromTime, String toTime) {
		try {
			String sql = "select distinct "
					+ TablesColumns.TABLETIMELINE_COLLECTEDAT + ","
					+ TablesColumns.TABLETIMELINE_TEM + ","
					+ TablesColumns.TABLETIMELINE_HUM + ","
					+ TablesColumns.TABLETIMELINE_SIGNAL + ","
					+ TablesColumns.TABLETIMELINE_POWDER + ","
					+ TablesColumns.TABLETIMELINE_SOIL_TEM + ","
					+ TablesColumns.TABLETIMELINE_SOIL_HUM + ","
					+ TablesColumns.TABLETIMELINE_ILLU + ","
					+ TablesColumns.TABLETIMELINE_FERTILITY + " from "
					+ TablesColumns.TABLETIMELINE + " where "
					+ TablesColumns.TABLETIMELINE_COLLECTOR + " = '" + collectId
					+ "' and " + TablesColumns.TABLETIMELINE_COLLECTEDAT
					+ " Between '" + fromTime + "' and '" + toTime + "' "
					+ " ORDER BY " + TablesColumns.TABLETIMELINE_COLLECTEDAT
					+ " ASC";
			Cursor cursor = dataBase.rawQuery(sql, null);
			List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> map;
			String[] names = cursor.getColumnNames();
			while (cursor.moveToNext()) {
				map = new HashMap<String, Object>();
				for (String name : names) {
					map.put(name, cursor.getString(cursor.getColumnIndex(name)));
				}
				data.add(map);
			}
			cursor.close();
			return data;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取所有时间线
	 * 
	 * @param 作物类型
	 * @return
	 */
	public List<HashMap<String, String>> getAllTimeLine() {
		try {
			String sql = "select * from " + TablesColumns.TABLETIMELINE;
			Cursor cursor = dataBase.rawQuery(sql, null);
			List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			String[] names = cursor.getColumnNames();
			while (cursor.moveToNext()) {
				map = new HashMap<String, String>();
				for (String name : names) {
					map.put(name, cursor.getString(cursor.getColumnIndex(name)));
				}
				data.add(map);
			}
			cursor.close();
			return data;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 插入数据库表
	 * 
	 * @param result
	 */
	public synchronized void insertAllTimeLine(
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

					String sql = "insert into " + TablesColumns.TABLETIMELINE
							+ " " + "(" + cloumns + ") values (" + cloumnValues
							+ ")";
					dataBase.execSQL(sql);
				} catch (SQLException e) {
				}
			}
		}
		try {
			Intent intent = new Intent();
			intent.setAction(MainActivity.farmingNewbroastCast);
			if(context !=null)
			context.sendBroadcast(intent);
		} catch (Exception e) {
			
		}
	}
}
