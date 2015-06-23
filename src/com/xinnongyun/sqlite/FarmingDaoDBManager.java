package com.xinnongyun.sqlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.xinnongyun.activity.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * 种养记录
 * @author sm
 *
 */
public class FarmingDaoDBManager {

	private DBHelper dbHelper;
	private SQLiteDatabase dataBase;
	private Context context;
	public FarmingDaoDBManager(Context context)
	{
		try {
			this.context = context;
			dbHelper = DBHelper.getInstance(context);
			dataBase = dbHelper.getWritableDatabase();
		} catch (Exception e) {
		}
	}
	

	
	/**
	 * 获取种养记录信息
	 * @return
	 */
	public List<HashMap<String, String>> getAllFarming()
	{
		List<HashMap<String, String>> data = null;
		try {
			String sql = "select * from " + TablesColumns.TABLEFARMING + " ORDER BY " + TablesColumns.TABLECOLLECTOR_SETTOHEAD;
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
				if(data.size()>0 && data.get(data.size()-1).get("id").contains(map.get(TablesColumns.TABLEFARMING_ID).toString()))
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
	 * 获取种养记录信息
	 * id
	 * @return
	 */
	public HashMap<String, String> getFarmingInfoById(String farmingId)
	{
		try {
			String sql = "select * from " + TablesColumns.TABLEFARMING + " where id in (select max(id) from " + TablesColumns.TABLEFARMING + " group by id) and " + TablesColumns.TABLECOLLECTOR_ID + " = '" + farmingId + "'";
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
	 * 修改记录信息大棚信息
	 * @param farmingId
	 * @param type
	 */
	public synchronized void changeFarmingBeingCollects(String farmingId, String collects)
	{
		try {
			String sql = "update " + TablesColumns.TABLEFARMING + " set " +  TablesColumns.TABLEFARMING_COLLECTORS + " = '" + collects + "' where id = '" + farmingId + "'";
			dataBase.execSQL(sql);
			Intent intent = new Intent();  
			intent.setAction(MainActivity.farmingNewbroastCast);  
			if(context!=null)
			context.sendBroadcast(intent);
		} catch (SQLException e) {
		}
	}
	
	
	/**
	 * 修改记录
	 * @param farmingId
	 * @param time
	 */
	public synchronized void updateFarmingById(HashMap<String, Object> map)
	{
		try {
			if(!map.keySet().contains("collectors") || !(map.get("collectors").toString().length()>2))
			{
				return;
			}
			Iterator<String> keyIter = map.keySet().iterator();
			String key;
			String value ="";
			int count = 0;
			while (keyIter.hasNext()) {
				key = (String) keyIter.next();
				if(count==0)
				{
					value += key + "='" + map.get(key).toString() + "'";
					count ++;
				}
				else
				{
					value += "," + key + "='" + map.get(key).toString() + "'";
				}
			}
			String sql = "update " + TablesColumns.TABLEFARMING + " set " + value + " where id = '" + map.get("id").toString() + "'";
			dataBase.execSQL(sql);
			Intent intent = new Intent();  
			intent.setAction(MainActivity.farmingNewbroastCast);  
			if(context!=null)
			context.sendBroadcast(intent);
		} catch (SQLException e) {
		}
		catch (Exception e) {
			
		}
	}
	
	/**
	 * 修改成熟时间
	 * @param farmingId
	 * @param time
	 */
	public synchronized void changeMarturingAt(String farmingId,String time)
	{
		try {
			String sql = "update " + TablesColumns.TABLEFARMING + " set " +  TablesColumns.TABLEFARMING_MATURINGAT + " = '" + time + "' where id = '" + farmingId + "'";
			dataBase.execSQL(sql);
			Intent intent = new Intent();  
			intent.setAction(MainActivity.farmingNewbroastCast);  
			if(context!=null)
			context.sendBroadcast(intent);
		} catch (SQLException e) {
		}
	}
	
	/**
	 * 设置置顶
	 * @param farmingId
	 * @param type
	 */
	public synchronized void changeFarmingBeingToHead(String farmingId, String type)
	{
		try {
			String sql = "update " + TablesColumns.TABLEFARMING + " set " +  TablesColumns.TABLECOLLECTOR_SETTOHEAD + " = '" + type + "' where id = '" + farmingId + "'";
			dataBase.execSQL(sql);
		} catch (SQLException e) {
		}
	}
	
	
	/**
	 * 插入数据库表
	 * @param result
	 */
	public synchronized void insertAllFarming(List<HashMap<String, Object>> result)
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
//			cloumns += ",'" + TablesColumns.TABLECOLLECTOR_SETTOHEAD + "'"; 
//			cloumnValues += ",'2'";
				String sql = "insert into " + TablesColumns.TABLEFARMING + " " +
						"("+ cloumns +") values ("+ cloumnValues +")";
				dataBase.execSQL(sql);
			} catch (SQLException e) {
			}
		}
		Intent intent = new Intent();  
		intent.setAction(MainActivity.farmingNewbroastCast);
		if(context!=null)
		context.sendBroadcast(intent);
	}

	//删除一条种养记录
	public synchronized void delectFarmingInfoById(String farmingId)
	{
		try {
			String sql = "delete from " + TablesColumns.TABLEFARMING + " where id = '" + farmingId + "'";
			dataBase.execSQL(sql);
			Intent intent = new Intent();  
			intent.setAction(MainActivity.farmingNewbroastCast);  
			if(context!=null)
			context.sendBroadcast(intent);
		} catch (SQLException e) {
		}
	}
	
	//插入一条新建种养记录
	public synchronized void insertNewFarming(HashMap<String, Object> map) {
		try {
			if(map !=null)
			{
				//所有种养记录
				List<HashMap<String, String>> datas = getAllFarming();
				if(datas != null && datas.size() > 0)
				{
					//查找重复的盒子并删除 如果盒子为空则将记录删除
					String insertNewInfoCollect = map.get(TablesColumns.TABLEFARMING_COLLECTORS).toString();
					String collectsInfo = insertNewInfoCollect.substring(1, insertNewInfoCollect.length()-1);
					//插入的盒子信息
					String newcollect[] = collectsInfo.split(",");
					
					//判断盒子是否冲突，如果冲突则删除记录包含的盒子信息
					for(HashMap<String, String> farmingItem : datas)
					{
						String oldInfoCollects = farmingItem.get(TablesColumns.TABLEFARMING_COLLECTORS);
						String oldCollect[] = oldInfoCollects.substring(1, oldInfoCollects.length()-1).split(",");
						String[] sameCollect = intersect(newcollect,oldCollect);
						//如果包含相同的盒子
						if(sameCollect!=null && sameCollect.length>0)
						{
							if(oldCollect.length==1 || (oldCollect.length == sameCollect.length))
							{
								delectFarmingInfoById(farmingItem.get(TablesColumns.TABLEFARMING_ID));
							}
							else
							{
								String changeCollects = "[";
								int countItem = 0;
								for(String item : oldCollect)
								{
									int count = 0;
									for(String sameString : sameCollect)
									{
										if(sameString.equals(item))
										{
											count ++;
										}
										
									}
									if(count == 0)
									{
										if(countItem == 0)
										{
											changeCollects += item;
											countItem ++;
										}
										else
										{
											changeCollects += "," +item;
										}
									}
								}
								//修改其他种养记录的盒子信息
								changeFarmingBeingCollects(farmingItem.get(TablesColumns.TABLEFARMING_ID), changeCollects+"]");
							}
						}
					}
					
				}
				//插入记录
				String cloumns = "";
				String cloumnValues = "";
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
				cloumns += ",'" + TablesColumns.TABLECOLLECTOR_SETTOHEAD + "'"; 
				cloumnValues += ",'200'";
				String sql = "insert into " + TablesColumns.TABLEFARMING + " " +
						"("+ cloumns +") values ("+ cloumnValues +")";
				dataBase.execSQL(sql);
			}
			Intent intent = new Intent();  
			intent.setAction(MainActivity.farmingNewbroastCast);  
			if(context!=null)
			context.sendBroadcast(intent);
		} catch (SQLException e) {
		}
	}
	
	
	//删除种养记录
		public synchronized void deleteCollectNewFarming(String collectId) {
				//所有种养记录
				List<HashMap<String, String>> datas = getAllFarming();
				if(datas != null && datas.size() > 0)
				{
					String newcollect[] = new String[]{collectId};
					
					//判断盒子是否冲突，如果冲突则删除记录包含的盒子信息
					for(HashMap<String, String> farmingItem : datas)
					{
						String oldInfoCollects = farmingItem.get(TablesColumns.TABLEFARMING_COLLECTORS);
						String oldCollect[] = oldInfoCollects.substring(1, oldInfoCollects.length()-1).split(",");
						String[] sameCollect = intersect(newcollect,oldCollect);
						//如果包含相同的盒子
						if(sameCollect!=null && sameCollect.length>0)
						{
							if(oldCollect.length==1 || (oldCollect.length == sameCollect.length))
							{
								delectFarmingInfoById(farmingItem.get(TablesColumns.TABLEFARMING_ID));
							}
							else
							{
								String changeCollects = "[";
								int countItem = 0;
								for(String item : oldCollect)
								{
									int count = 0;
									for(String sameString : sameCollect)
									{
										if(sameString.equals(item))
										{
											count ++;
										}
										
									}
									if(count == 0)
									{
										if(countItem == 0)
										{
											changeCollects += item;
											countItem ++;
										}
										else
										{
											changeCollects += "," +item;
										}
									}
								}
								//修改其他种养记录的盒子信息
								changeFarmingBeingCollects(farmingItem.get(TablesColumns.TABLEFARMING_ID), changeCollects+"]");
							}
						}
					}
					
				}
				Intent intent = new Intent();  
				intent.setAction(MainActivity.farmingNewbroastCast);  
				if(context!=null)
				context.sendBroadcast(intent);
		}
	
	
	
	
	
	
	//求两个数组的交集   
    public  String[] intersect(String[] arr1, String[] arr2) {   
        Map<String, Boolean> map = new HashMap<String, Boolean>();   
        LinkedList<String> list = new LinkedList<String>();   
        for (String str : arr1) {   
            if (!map.containsKey(str)) {   
                map.put(str, Boolean.FALSE);   
            }   
        }   
        for (String str : arr2) {   
            if (map.containsKey(str)) {   
                map.put(str, Boolean.TRUE);   
            }   
        }   
  
        for (Entry<String, Boolean> e : map.entrySet()) {   
            if (e.getValue().equals(Boolean.TRUE)) {   
                list.add(e.getKey());   
            }   
        }   
        String[] result = {};   
        return list.toArray(result);   
    }   
	
	
	
	
	
	
}
