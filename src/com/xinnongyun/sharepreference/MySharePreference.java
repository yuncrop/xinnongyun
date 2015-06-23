package com.xinnongyun.sharepreference;

import java.util.HashMap;

import com.xinnongyun.activity.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 程序本地文件存储    农场信息   账户信息
 * 
 * @author lmy
 * 
 */
public class MySharePreference {
	
	public static final String ISFIRSTOPEN = "firstOpen";
	//文件名
	private static final String FILENAME = "XINNONGYUNCROP";

	public static final String NOTSTATE = "null";
	//登录状态
	public static final String LOGINSTATE = "loginState";
	
	//账户信息
	public static final String ACCOUNTID = "id";
	public static final String ACCOUNTUSERNAME = "username";
	public static final String ACCOUNTTOKEN = "key";
	public static final String ACCOUNTPWD = "password";
	public static final String ACCOUNTNICKNAME = "nickname";
	public static final String ACCOUNTAVATAR = "avatar";
	public static final String ACCOUNTISSMSALARM = "is_sms_alarm";
	public static final String ACCOUNTCANSMSALARM = "can_sms_alarm";
	public static final String ACCOUNTPUSHALARMLEVEL = "push_alarm_level";
	
	
	//农场信息
	public static final String FARMID = "farmid";
	public static final String FARMNAME = "name";
	public static final String FARMOWNERSERIAL = "owner";
	public static final String FARMADDRESS = "location";
	public static final String FARMBG = "bg";
	public static final String FARMLOGO = "logo";
	public static final String FARMINTRODUCE = "description";
	public static final String FARMCREATEAT = "created_at";
	public static final String FARMUPDATEAT = "updated_at";
	public static final String FARMLAT = "lat";
	public static final String FARMLNG = "lng";
	
	/**
	 * 保存新建农场信息
	 * @param context
	 * @param map    
	 */
	//{id=3, logo=http://m.nnong.com/media/farm_logo/None.jpg, users=[8], updated_at=2014-12-15T14:14:34.617403, location=北京市海淀区唐家岭路, description=阿水电费, name=农场1, owner=8, created_at=2014-12-15T14:14:34.617366, bg=http://m.nnong.com/media/farm_bg/None.jpg, lng=116.300769, lat=40.052587}
	public static void saveFarmInfo(Context context,
			HashMap<String, Object> map) {
		save(context, FARMID, map.get("id").toString());
		save(context, FARMLNG, map.get(FARMLNG).toString());
		save(context, FARMLAT, map.get(FARMLAT).toString());
		save(context, FARMUPDATEAT, map.get(FARMUPDATEAT).toString());
		save(context, FARMCREATEAT, map.get(FARMCREATEAT).toString());
		save(context, FARMINTRODUCE, map.get(FARMINTRODUCE).toString());
		save(context, FARMLOGO, map.get(FARMLOGO).toString());
		save(context, FARMBG, map.get(FARMBG).toString());
		save(context, FARMADDRESS, map.get(FARMADDRESS).toString());
		save(context, FARMOWNERSERIAL, map.get(FARMOWNERSERIAL).toString());
		save(context, FARMNAME, map.get(FARMNAME).toString());
		Intent intent = new Intent();  
		intent.setAction(MainActivity.farmConfigbroastCast);  
		if(context!=null)
		context.sendBroadcast(intent);
	}
	
	
	/**
	 * 保存账户注册信息
	 * @param map
	 */
	public static void saveAccountZhuceInfo(Context context,HashMap<String, Object> map) {
		save(context, ACCOUNTID, map.get(ACCOUNTID).toString());
		save(context, ACCOUNTTOKEN, map.get(ACCOUNTTOKEN).toString());
		save(context, ACCOUNTPWD, map.get(ACCOUNTPWD).toString());
		save(context, ACCOUNTUSERNAME, map.get(ACCOUNTUSERNAME).toString());
		save(context, ACCOUNTISSMSALARM, map.get(ACCOUNTISSMSALARM).toString());
		save(context, ACCOUNTCANSMSALARM, map.get(ACCOUNTCANSMSALARM).toString());
		save(context, ACCOUNTPUSHALARMLEVEL, map.get(ACCOUNTPUSHALARMLEVEL).toString());
	}
	
	/**
	 * 保存用户修改后的信息
	 * @param context
	 * @param map
	 */
	public static void saveAccountModifyInfo(Context context,HashMap<String, Object> map)
	{
		save(context, ACCOUNTID, map.get(ACCOUNTID).toString());
		save(context, ACCOUNTISSMSALARM, map.get(ACCOUNTISSMSALARM).toString());
		save(context, ACCOUNTCANSMSALARM, map.get(ACCOUNTCANSMSALARM).toString());
		save(context, ACCOUNTPUSHALARMLEVEL, map.get(ACCOUNTPUSHALARMLEVEL).toString());
		save(context, ACCOUNTNICKNAME, map.get(ACCOUNTNICKNAME).toString());
		save(context, ACCOUNTAVATAR, map.get(ACCOUNTAVATAR).toString());
	}
	
	
	/**
	 * 保存账户登录信息
	 * @param map
	 */
	public static void saveAccountLoginInfo(Context context,HashMap<String, Object> map) {
		save(context, ACCOUNTID, map.get(ACCOUNTID).toString());
		save(context, ACCOUNTTOKEN, map.get(ACCOUNTTOKEN).toString());
		save(context, ACCOUNTPWD, map.get(ACCOUNTPWD).toString());
		save(context, ACCOUNTUSERNAME, map.get(ACCOUNTUSERNAME).toString());
		save(context, ACCOUNTISSMSALARM, map.get(ACCOUNTISSMSALARM).toString());
		save(context, ACCOUNTCANSMSALARM, map.get(ACCOUNTCANSMSALARM).toString());
		save(context, ACCOUNTPUSHALARMLEVEL, map.get(ACCOUNTPUSHALARMLEVEL).toString());
		save(context, ACCOUNTNICKNAME, map.get(ACCOUNTNICKNAME).toString());
		save(context, ACCOUNTAVATAR, map.get(ACCOUNTAVATAR).toString());
	}
	
	/**
	 * 得到农场所有信息
	 * @param context
	 * @return 不存在则返回null
	 */
	public static HashMap<String,String> getFarmAllInfo(Context context)
	{
		try {
			@SuppressWarnings("static-access")
			SharedPreferences preferences = context.getSharedPreferences(FILENAME,
					context.MODE_PRIVATE);
			if(preferences.getString(FARMID, NOTSTATE).equals(NOTSTATE))
			{
				return null;
			}
			HashMap<String,String> map = new HashMap<String, String>();
			map.put(FARMID, preferences.getString(FARMID, NOTSTATE));
			map.put(FARMNAME, preferences.getString(FARMNAME, NOTSTATE));
			map.put(FARMADDRESS, preferences.getString(FARMADDRESS, NOTSTATE));
			map.put(FARMOWNERSERIAL, preferences.getString(FARMOWNERSERIAL, NOTSTATE));
			map.put(FARMLOGO, preferences.getString(FARMLOGO, NOTSTATE));
			map.put(FARMBG, preferences.getString(FARMBG, NOTSTATE));
			map.put(FARMLAT, preferences.getString(FARMLAT, NOTSTATE));
			map.put(FARMLNG, preferences.getString(FARMLNG, NOTSTATE));
			map.put(FARMINTRODUCE, preferences.getString(FARMINTRODUCE, NOTSTATE));
			return map;
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * 得到键相对应的值
	 * @param context
	 * @param key
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static String getValueFromKey(Context context, String key) {
		try {
			SharedPreferences preferences = context.getSharedPreferences(FILENAME,
					context.MODE_PRIVATE);
			return preferences.getString(key, NOTSTATE);
		} catch (Exception e) {
			return NOTSTATE;
		}
	}

	/**
	 * 保存数据
	 * @param context
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean save(Context context, String key, String value) {
		// SharedPreference:是以键值对的形式来保存私有数据
		// MODE_APPEND MODE_PRIVATE MODE_WORLD_READABLE MODE_WORLD_WRITEABLE
		
		try {
			@SuppressWarnings("static-access")
			SharedPreferences preferences = context.getSharedPreferences(FILENAME,
					context.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.putString(key, value);
			editor.commit();
			return true;
		} catch (Exception e) {
			return false;
		}
	}


	/**
	 * 清空农场信息
	 * @param context
	 */
	public static void clearFarmInfo(Context context)
	{
		clearOneState(context,FARMID);
		clearOneState(context,FARMLNG);
		clearOneState(context,FARMLAT);
		clearOneState(context,FARMLAT);
		clearOneState(context,FARMCREATEAT);
		clearOneState(context,FARMINTRODUCE);
		clearOneState(context,FARMLOGO);
		clearOneState(context,FARMBG);
		clearOneState(context,FARMADDRESS);
		clearOneState(context,FARMOWNERSERIAL);
		clearOneState(context,FARMNAME);
		Intent intent = new Intent();  
		intent.setAction(MainActivity.farmConfigbroastCast);  
		if(context!=null)
		context.sendBroadcast(intent);
	}
	
	
	

	/**
	 * 清除某个状态
	 * @param context
	 * @param key
	 */
	public static void clearOneState(Context context,String key) {
		try {
			@SuppressWarnings("static-access")
			SharedPreferences preferences = context.getSharedPreferences(FILENAME,
					context.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.remove(key);
			editor.commit();
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * 清除所有数据
	 * @param context
	 */
	@SuppressWarnings("static-access")
	public static void clearAllData(Context context) {
		try {
			SharedPreferences preferences = context.getSharedPreferences(FILENAME,
					context.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.clear();
			editor.commit();
		} catch (Exception e) {
		}
	}
}
