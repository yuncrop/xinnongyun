package com.xinnongyun.sqlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * K线
 * 
 * @author sm
 * 
 */
public class KLineDaoDBManager {

	private DBHelper dbHelper;
	private SQLiteDatabase dataBase;

	public KLineDaoDBManager(Context context) {
		try {
			dbHelper = DBHelper.getInstance(context);
			dataBase = dbHelper.getWritableDatabase();
		} catch (Exception e) {
		}
	}

	/**
	 * 获取盒子最后更新时间
	 * 
	 * @param id
	 * @return
	 */
	public HashMap<String, String> getCollectLastUpdateTime(String collectId) {
		try {
			String sql = "select * from " + TablesColumns.TABLEKLINE + " where "
					+ TablesColumns.TABLEKLINE_COLLECTOR + " = '" + collectId
					+ "' ORDER BY " + TablesColumns.TABLEKLINE_ID + " DESC limit 1";
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
	 * 获取盒子近7日k线
	 * 
	 * @param id
	 * @return
	 */
	
	public List<HashMap<String, String>> getCollectKTime(String collectId) {
		try {
			String sql = "select distinct " + 
			        TablesColumns.TABLEKLINE_MAX_FERTILITY + "," +
					TablesColumns.TABLEKLINE_MAXHUM + "," +
					TablesColumns.TABLEKLINE_MAXILLU + "," +
					TablesColumns.TABLEKLINE_MAXTEM + "," +
					TablesColumns.TABLEKLINE_MIN_FERTILITY + "," +
					TablesColumns.TABLEKLINE_MINHUM + "," +
					TablesColumns.TABLEKLINE_MINILLU + "," +
					TablesColumns.TABLEKLINE_CREATEDAT + "," +
					TablesColumns.TABLEKLINE_MINTEM + 
					" from " + TablesColumns.TABLEKLINE + " where "
					+ TablesColumns.TABLEKLINE_COLLECTOR + " = '" + collectId
					+ "' ORDER BY " + TablesColumns.TABLEKLINE_CREATEDAT  + " DESC limit 0,7";
			Cursor cursor = dataBase.rawQuery(sql, null);
			List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
			HashMap<String, String> map = null;
			String[] names = cursor.getColumnNames();
			while (cursor.moveToNext()) {
				map = new HashMap<String, String>();
				for (String name : names) {
					map.put(name, cursor.getString(cursor.getColumnIndex(name)));
				}
				list.add(map);
			}
			cursor.close();
			return list;
		} catch (Exception e) {
			return null;
		}
	}
	

	/**
	 * 获取所有K线
	 * 
	 * @param 作物类型
	 * @return
	 */
	public  List<HashMap<String, String>> getAllKLine() {
		try {
			String sql = "select * from " + TablesColumns.TABLEKLINE;
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
	public synchronized void insertAllKLine(List<HashMap<String, Object>> result) {
		if (result != null) {
			try {
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

					String sql = "insert into " + TablesColumns.TABLEKLINE + " "
							+ "(" + cloumns + ") values (" + cloumnValues + ")";
					dataBase.execSQL(sql);
				}
			} catch (SQLException e) {
			}
		}
	}
}
