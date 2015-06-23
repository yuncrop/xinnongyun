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
 * 土壤DAO
 * @author sm
 *
 */
public class SoilDaoDBManager {

	private DBHelper dbHelper;
	private SQLiteDatabase dataBase;
	public SoilDaoDBManager(Context context)
	{
		try {
			dbHelper = DBHelper.getInstance(context);
			dataBase = dbHelper.getWritableDatabase();
		} catch (Exception e) {
		}
	}
	
	
	/**
	 * 获取所有土壤信息
	 * @return
	 */
	public List<HashMap<String, String>> getAllSoil()
	{
		try {
			Cursor cursor = dataBase.rawQuery("select * from " + TablesColumns.TABLESOIL, null);
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
		}
	}
	
	
	/**
	 * 插入数据库表
	 * @param result
	 */
	public synchronized void insertAllSoil(List<HashMap<String, Object>> result)
	{
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
				
				String sql = "insert into " + TablesColumns.TABLESOIL + " " +
						"("+ cloumns +") values ("+ cloumnValues +")";
				dataBase.execSQL(sql);
			} catch (SQLException e) {
			}
		}
		
	}
}
