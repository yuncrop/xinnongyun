package com.xinnongyun.sqlite;

import java.util.ArrayList;
import java.util.List;

/**
 * 列名  表名
 * @author sm
 *
 */
public class TablesColumns {

	/**
	 * 数据库名
	 */
	public static final String DBNAME = "yuntongxun.db";
	//2015-2/3 9:30更新版本
    public static final int DB_VERSION = 7;
	public static final String TABLE_ID = "_id";
	/**
	 * 盒子表
	 */
	public static final String TABLECOLLECTOR = "collector";
	public static final String TABLECOLLECTOR_ID = "id";
	public static final String TABLECOLLECTOR_CATEGORY = "category";
	public static final String TABLECOLLECTOR_UPDATEDAT = "updated_at";
	public static final String TABLECOLLECTOR_SERIAL = "serial";
	public static final String TABLECOLLECTOR_GATEWAY = "gateway";
	public static final String TABLECOLLECTOR_PRODUCTEDAT = "produced_at";
	public static final String TABLECOLLECTOR_NAME = "name";
	public static final String TABLECOLLECTOR_QRCODE = "qrcode";
	public static final String TABLECOLLOCTOR_FARM = "farm";
	//public static final String TABLECOLLOCTOR_BEING = "being";
	public static final String TABLECOLLOCTOR_LAT = "lat";
	public static final String TABLECOLLOCTOR_LNG = "lng";
	public static final String TABLECOLLOCTOR_LOCATION = "location";
	public static final String TABLECOLLOCTOR_SOIL = "soil";
	public static final String TABLECOLLOCTOR_MAX_TEM = "max_tem";
	public static final String TABLECOLLOCTOR_MIN_TEM = "min_tem";
	public static final String TABLECOLLOCTOR_MAX_HUM = "max_hum";
	public static final String TABLECOLLOCTOR_MIN_HUM = "min_hum";
	public static final String TABLECOLLOCTOR_TEM_OVERFLOW = "tem_overflow";
	public static final String TABLECOLLOCTOR_HUM_OVERFLOW = "hum_overflow";
	public static final String TABLECOLLOCTOR_SOIL_HUM_OVERFLOW = "soil_hum_overflow";
	public static final String TABLECOLLOCTOR_TEM_OVERFLOW_BEGIN_AT = "tem_overflow_begin_at";
	public static final String TABLECOLLOCTOR_HUM_OVERFLOW_BEGIN_AT = "hum_overflow_begin_at";
	public static final String TABLECOLLOCTOR_SOIL_HUM_OVERFLOW_BEGIN_AT = "soil_hum_overflow_begin_at";
	public static final String TABLECOLLOCTOR_MAX_SOIL_HUM = "max_soil_hum";
	public static final String TABLECOLLOCTOR_MIN_SOIL_HUM = "min_soil_hum";
	
	/*
	 * 种养记录
	 */
	public static final String TABLEFARMING = "farming";
	public static final String TABLEFARMING_ID = "id";
	public static final String TABLEFARMING_FARM = "farm";
	public static final String TABLEFARMING_GATEWAY = "gateway";
	public static final String TABLEFARMING_COLLECTORS = "collectors";
	public static final String TABLEFARMING_BEING = "being";
	public static final String TABLEFARMING_MATURINGAT = "maturing_at";
	public static final String TABLEFARMING_MATURERATIO = "mature_ratio";
	public static final String TABLEFARMING_BEGINAT = "begin_at";
	public static final String TABLEFARMING_ENDAT = "end_at";
	public static final String TABLECOLLECTOR_SETTOHEAD = "ordering";//100置顶200否
	public static final String TABLEFARMING_MAXTEM = "max_tem";
	public static final String TABLEFARMING_MINTEM = "min_tem";
	public static final String TABLEFARMING_MAXHUM = "max_hum";
	public static final String TABLEFARMING_MINHUM = "min_hum";
	public static final String TABLEFARMING_MAXSOILHUM = "max_soil_hum";
	public static final String TABLEFARMING_MINSOILHUM = "min_soil_hum";
	
	
	/**
	 * 盒子更新时间线表
	 */
	public static final String TABLETIMELINE = "timeline";
	public static final String TABLETIMELINE_ID = "id";
	public static final String TABLETIMELINE_COLLECTOR = "collector";
	public static final String TABLETIMELINE_COLLECTEDAT = "collected_at";
	public static final String TABLETIMELINE_TEM = "tem";
	public static final String TABLETIMELINE_HUM = "hum";
	public static final String TABLETIMELINE_SIGNAL = "signal";
	public static final String TABLETIMELINE_POWDER = "power";
	public static final String TABLETIMELINE_ILLU = "illu";
	public static final String TABLETIMELINE_FERTILITY = "fertility";
	public static final String TABLETIMELINE_CATEGORY = "category";
	public static final String TABLETIMELINE_SOIL_TEM = "soil_tem";
	public static final String TABLETIMELINE_SOIL_HUM = "soil_hum";
	
	/**
	 * 盒子 K线表
	 */
	public static final String TABLEKLINE = "k_timeline";
	public static final String TABLEKLINE_ID = "id";
	public static final String TABLEKLINE_COLLECTOR = "collector";
	public static final String TABLEKLINE_CREATEDAT = "collected_at";
	public static final String TABLEKLINE_GATEWAY = "gateway";
	public static final String TABLEKLINE_MAXTEM = "max_tem";
	public static final String TABLEKLINE_MINTEM = "min_tem";
	public static final String TABLEKLINE_MAXHUM = "max_hum";
	public static final String TABLEKLINE_MINHUM = "min_hum";
	public static final String TABLEKLINE_MAXILLU = "max_illu";
	public static final String TABLEKLINE_MINILLU = "min_illu";
	public static final String TABLEKLINE_MAX_FERTILITY = "max_fertility";
	public static final String TABLEKLINE_MIN_FERTILITY= "min_fertility";
	public static final String TABLEKLINE_CATEGORY = "category";
	public static final String TABLEKLINE_MAX_SOIL_TEM = "max_soil_tem";
	public static final String TABLEKLINE_MIN_SOIL_TEM = "min_soil_tem";
	public static final String TABLEKLINE_MAX_SOIL_HUM = "max_soil_hum";
	public static final String TABLEKLINE_MIN_SOIL_HUM = "min_soil_hum";
	
	

	/**
	 * 作物  动物
	 */
	public static final String TABLEBEING = "being";
	public static final String TABLEBEING_ID = "id";
	public static final String TABLEBEING_ADDBY = "added_by";
	public static final String TABLEBEING_ISRESERVED = "is_reserved";
	public static final String TABLEBEING_NAME = "name";
	public static final String TABLEBEING_IMAGE = "icon";
	public static final String TABLEBEING_KINGDOM = "kingdom";
	public static final String TABLEBEING_ISANNUAL = "is_annual";
	public static final String TABLEBEING_ADDAT = "added_at";
	public static final String TABLEBEING_UPDATEAT = "updated_at";
	public static final String TABLEBEING_MAXTEM = "max_tem";
	public static final String TABLEBEING_MINTEM = "min_tem";
	public static final String TABLEBEING_MAXHUM = "max_hum";
	public static final String TABLEBEING_MINHUM = "min_hum";
	public static final String TABLEBEING_MAXSOILHUM = "max_soil_hum";
	public static final String TABLEBEING_MINSOILHUM = "min_soil_hum";
	public static final String TABLEBEING_FARM = "farm";
	public static final String TABLEBEING_FIT_TEM = "fit_tem";
	public static final String TABLEBEING_FIT_HUM = "fit_hum";
	public static final String TABLEBEING_FIT_SOIL_HUM = "fit_soil_hum";
	
	
	
	/**
	 * 土壤
	 */
	public static final String TABLESOIL = "soil";
	public static final String TABLESOIL_ID = "id";
	public static final String TABLESOIL_ISRESERVED = "is_reserved";
	public static final String TABLESOIL_ADDAT = "added_at";
	public static final String TABLESOIL_UPDATEAT = "updated_at";
	public static final String TABLESOIL_NAME = "name";
	public static final String TABLESOIL_ADDBY = "added_by";
	public static final String TABLESOIL_FARM = "farm";
	
	
	
	
	
	/**
	 * 通知
	 */
	public static final String TABLENOTICE = "notice";
	public static final String TABLENOTICE_ID = "id";
	public static final String TABLENOTICE_SUBJECT = "subject"; 
	public static final String TABLENOTICE_SIGNAL = "signal";
	public static final String TABLENOTICE_CONTEXT = "context";
	public static final String TABLENOTICE_USER = "user";
	public static final String TABLENOTICE_ALERT = "alert";
	public static final String TABLENOTICE_OPERATOR = "operator";
	public static final String TABLENOTICE_CREATEDAT = "created_at";
	public static final String TABLENOTICE_READAT = "read_at";
	
	
	/**
	 * 上传队列
	 */
	public static final String TABLEVIDEOUPLOAD = "videoupload";
	public static final String TABLEVIDEOUPLOAD_QNTOKEN = "qiniutoken";
	public static final String TABLEVIDEOUPLOAD_FILENAME = "videofilename";
	public static final String TABLEVIDEOUPLOAD_PERCENT = "videopercent";
	/**
	 * 0等待上传  1开始上传  2上传完成
	 */
	public static final String TABLEVIDEOUPLOAD_STATE = "videostate";
	public static final String TABLEVIDEOUPLOAD_FARM = "farmid";
	public static final String TABLEVIDEOUPLOAD_CREATETIME = "created_at";
	//目前未使用
	public static final String TABLEVIDEOUPLOAD_FAILEDCOUNT = "videofailedcount";
	
	
	
	/**
	 * 视频日志
	 */
	public static final String TABLEJOURNAL = "journal";
	public static final String TABLEJOURNAL_ID = "id";
	public static final String TABLEJOURNAL_USER = "user";
	public static final String TABLEJOURNAL_SEGMENTS = "segments";
	public static final String TABLEJOURNAL_LIKES = "likes";
	public static final String TABLEJOURNAL_COMMENTS = "comments";
	public static final String TABLEJOURNAL_VIEWS = "views";
	public static final String TABLEJOURNAL_KEY = "key";
	public static final String TABLEJOURNAL_QINIUTOKEN = "qiniutoken";
	public static final String TABLEJOURNAL_QINIUKEY = "qiniukey";
	public static final String TABLEJOURNAL_CONTENT = "content";
	public static final String TABLEJOURNAL_LAT = "lat";
	public static final String TABLEJOURNAL_LNG = "lng";
	public static final String TABLEJOURNAL_LOCATION = "location";
	public static final String TABLEJOURNAL_CREATEAT = "created_at";
	public static final String TABLEJOURNAL_TRANSCODEDAT = "transcoded_at";
	public static final String TABLEJOURNAL_FARM = "farm";
	public static final String TABLEJOURNAL_MASKKEY = "maskkey";
	public static final String TABLEJOURNAL_FARMINGS = "farmings";
	public static final String TABLEJOURNAL_THUMBNAIL = "thumbnailkey";
	
	/**
	 * 环境小站
	 */
	public static final String TABLEWEATHERSTATION = "weatherstation";
	public static final String TABLEWEATHERSTATION_ID = "id";
	public static final String TABLEWEATHERSTATION_SERIAL = "serial";
	public static final String TABLEWEATHERSTATION_NAME = "name";
	public static final String TABLEWEATHERSTATION_CREATEDAT = "created_at";
	public static final String TABLEWEATHERSTATION_UPDATEDAT = "updated_at";
	public static final String TABLEWEATHERSTATION_FARM = "farm";
	
	
	/**
	 * 环境小站实时数据表
	 */
	public static final String TABLESTATIONTIMELINR = "weatherstationtimeline";
	public static final String TABLESTATIONTIMELINR_ID = "id";
	public static final String TABLESTATIONTIMELINR_SENSOR = "sensor";
	public static final String TABLESTATIONTIMELINR_ALT = "alt";
	public static final String TABLESTATIONTIMELINR_CO2 = "co2";
	public static final String TABLESTATIONTIMELINR_CREATEDAT = "created_at";
	public static final String TABLESTATIONTIMELINR_DEW = "dew";
	public static final String TABLESTATIONTIMELINR_HUM = "hum";
	public static final String TABLESTATIONTIMELINR_ILLU = "illu";
	public static final String TABLESTATIONTIMELINR_LAT = "lat";
	public static final String TABLESTATIONTIMELINR_LNG = "lng";
	public static final String TABLESTATIONTIMELINR_LOCATION = "location";
	public static final String TABLESTATIONTIMELINR_PM1 = "pm1";
	public static final String TABLESTATIONTIMELINR_PM10 = "pm10";
	public static final String TABLESTATIONTIMELINR_PM25 = "pm2_5";
	public static final String TABLESTATIONTIMELINR_POWER = "power";
	public static final String TABLESTATIONTIMELINR_PRES = "pres";
	public static final String TABLESTATIONTIMELINR_SIGNAL = "signal";
	public static final String TABLESTATIONTIMELINR_TEM = "tem";
	
	/**
	 * 摄像头
	 */
	public static final String TABLECAMERA = "cameratable";
	public static final String TABLECAMERA_ID = "id";
	public static final String TABLECAMERA_DEVICEID = "device_id";
	public static final String TABLECAMERA_DEVICESERIAL = "device_serial";
	public static final String TABLECAMERA_COVER_URL1 = "cover_url1";
	public static final String TABLECAMERA_CREATEDAT = "created_at";
	public static final String TABLECAMERA_FARM = "farm";
	public static final String TABLECAMERA_NAME = "name";
	public static final String TABLECAMERA_ONLINE = "online"; 
	public static final String TABLECAMERA_PLAYURL1 = "play_url1"; 
	public static final String TABLECAMERA_PLAYURL2 = "play_url2"; 
	public static final String TABLECAMERA_PUBLISH = "publish";
	public static final String TABLECAMERA_SHARE_URL = "share_url";
	public static final String TABLECAMERA_UPDATEDAT = "updated_at";
	
	
	
	
	/**
	 * 中控表
	 */
	public static final String TABLECONTROLCENTER = "controlcenter";
	public static final String TABLECONTROLCENTER_ID = "id";
	public static final String TABLECONTROLCENTER_SERIAL = "serial";
	public static final String TABLECONTROLCENTER_FARMID = "farm";
	public static final String TABLECONTROLCENTER_NAME = "name";
	public static final String TABLECONTROLCENTER_PRODUCEDAT = "produced_at";
	public static final String TABLECONTROLCENTER_UPDATEDAT = "updated_at";
	
	
	/**
	 * 放风机
	 */
	public static final String TABLEBLOWER = "blower";
	public static final String TABLEBLOWER_ID = "id";
	public static final String TABLEBLOWER_CCID = "cc";
	public static final String TABLEBLOWER_NO = "no";
	public static final String TABLEBLOWER_MAXTEMLIMIT = "max_tem_limit";
	public static final String TABLEBLOWER_MINTEMLIMIT = "min_tem_limit";
	public static final String TABLEBLOWER_WORKING_MODE = "working_mode";
	public static final String TABLEBLOWER_MAX_DISTANCE = "max_distance";
	public static final String TABLEBLOWER_ADDITION_DISTANCE = "addition_distance";
	public static final String TABLEBLOWER_STEP_RUN_DISTANCE = "step_run_distance";
	public static final String TABLEBLOWER_STEP_PAUSE_DISTANCE = "step_pause_distance";
	public static final String TABLEBLOWER_DISTANCE = "distance";
	public static final String TABLEBLOWER_UPDATEDAT = "updated_at";
	
	
	/**
	 * 放风机环境数据
	 */
	public static final String TABLEBBLOWERENV = "blowerenv";
	public static final String TABLEBBLOWERENV_ID = "id";
	public static final String TABLEBBLOWERENV_BLOWERID = "blower";
	public static final String TABLEBBLOWERENV_TEM = "tem";
	public static final String TABLEBBLOWERENV_DISTANCE = "distance";
	public static final String TABLEBBLOWERENV_CREATEDAT = "created_at";
	
	/**
	 * 放风机指令
	 */
	public static final String TABLEBLOWERCMD = "blowercmd";
	public static final String TABLEBLOWERCMD_ID = "id";
	public static final String TABLEBLOWERCMD_BLOWERID = "blower_id";
	public static final String TABLEBLOWERCMD_CMD = "cmd";
	public static final String TABLEBLOWERCMD_RETRIES = "retries";
	public static final String TABLEBLOWERCMD_CREATEDAT = "created_at";
	public static final String TABLEBLOWERCMD_EXECUTEDAT = "executed_at";
	public static final String TABLEBLOWERCMD_RESULT = "result";
	
	
	
	
	
	
	
	
	
	
	public static List<String> listMap;
	public static List<String> getAllFileds()
	{
		if(listMap==null)
		{
			try {
				listMap = new ArrayList<String>();
				
				
				
				listMap.add(TABLEBLOWERCMD_BLOWERID);
				listMap.add(TABLEBLOWERCMD_CMD);
				listMap.add(TABLEBLOWERCMD_CREATEDAT);
				listMap.add(TABLEBLOWERCMD_EXECUTEDAT);
				listMap.add(TABLEBLOWERCMD_RESULT);
				listMap.add(TABLEBLOWERCMD_RETRIES);
				
				
				
				listMap.add(TABLEBBLOWERENV_ID);
				listMap.add(TABLEBBLOWERENV_BLOWERID);
				listMap.add(TABLEBBLOWERENV_CREATEDAT);
				listMap.add(TABLEBBLOWERENV_DISTANCE);
				listMap.add(TABLEBBLOWERENV_TEM);
				
				
				listMap.add(TABLEBLOWER_ADDITION_DISTANCE);
				listMap.add(TABLEBLOWER_CCID);
				listMap.add(TABLEBLOWER_DISTANCE);
				listMap.add(TABLEBLOWER_ID);
				listMap.add(TABLEBLOWER_MAX_DISTANCE);
				listMap.add(TABLEBLOWER_MAXTEMLIMIT);
				listMap.add(TABLEBLOWER_MINTEMLIMIT);
				listMap.add(TABLEBLOWER_NO);
				listMap.add(TABLEBLOWER_STEP_PAUSE_DISTANCE);
				listMap.add(TABLEBLOWER_STEP_RUN_DISTANCE);
				listMap.add(TABLEBLOWER_UPDATEDAT);
				listMap.add(TABLEBLOWER_WORKING_MODE);
				
				
				
				listMap.add(TABLECONTROLCENTER_ID);
				listMap.add(TABLECONTROLCENTER_FARMID);
				listMap.add(TABLECONTROLCENTER_NAME);
				listMap.add(TABLECONTROLCENTER_PRODUCEDAT);
				listMap.add(TABLECONTROLCENTER_SERIAL);
				listMap.add(TABLECONTROLCENTER_UPDATEDAT);
				
				
				listMap.add(TABLESTATIONTIMELINR_SENSOR);
				listMap.add(TABLESTATIONTIMELINR_ALT);
				listMap.add(TABLESTATIONTIMELINR_CO2);
				listMap.add(TABLESTATIONTIMELINR_CREATEDAT);
				listMap.add(TABLESTATIONTIMELINR_DEW);
				listMap.add(TABLESTATIONTIMELINR_HUM);
				listMap.add(TABLESTATIONTIMELINR_ILLU);
				listMap.add(TABLESTATIONTIMELINR_LAT);
				listMap.add(TABLESTATIONTIMELINR_LNG);
				listMap.add(TABLESTATIONTIMELINR_LOCATION);
				listMap.add(TABLESTATIONTIMELINR_PM1);
				listMap.add(TABLESTATIONTIMELINR_PM10);
				listMap.add(TABLESTATIONTIMELINR_PM25);
				listMap.add(TABLESTATIONTIMELINR_POWER);
				listMap.add(TABLESTATIONTIMELINR_PRES);
				
				
				
				
				listMap.add(TABLECOLLECTOR_ID);
				listMap.add(TABLECOLLECTOR_CATEGORY);
				listMap.add(TABLECOLLECTOR_UPDATEDAT);
				listMap.add(TABLECOLLECTOR_SERIAL);
				listMap.add(TABLECOLLECTOR_GATEWAY);
				listMap.add(TABLECOLLECTOR_PRODUCTEDAT);
				listMap.add(TABLECOLLECTOR_NAME);
				listMap.add(TABLECOLLECTOR_QRCODE);
				listMap.add(TABLECOLLOCTOR_FARM);
				listMap.add(TABLECOLLOCTOR_LAT);
				listMap.add(TABLECOLLOCTOR_LNG);
				listMap.add(TABLECOLLOCTOR_LOCATION);
				listMap.add(TABLECOLLOCTOR_SOIL);
				listMap.add(TABLECOLLOCTOR_MAX_TEM);
				listMap.add(TABLECOLLOCTOR_MIN_TEM);
				listMap.add(TABLECOLLOCTOR_MAX_HUM);
				listMap.add(TABLECOLLOCTOR_MIN_HUM);
				listMap.add(TABLECOLLOCTOR_TEM_OVERFLOW);
				listMap.add(TABLECOLLOCTOR_HUM_OVERFLOW);
				listMap.add(TABLECOLLOCTOR_SOIL_HUM_OVERFLOW);
				listMap.add(TABLECOLLOCTOR_TEM_OVERFLOW_BEGIN_AT);
				listMap.add(TABLECOLLOCTOR_HUM_OVERFLOW_BEGIN_AT);
				listMap.add(TABLECOLLOCTOR_SOIL_HUM_OVERFLOW_BEGIN_AT);
				listMap.add(TABLECOLLOCTOR_MAX_SOIL_HUM);
				listMap.add(TABLECOLLOCTOR_MIN_SOIL_HUM);
				
				
				listMap.add(TABLEFARMING_FARM);
				listMap.add(TABLEFARMING_GATEWAY);
				listMap.add(TABLEFARMING_COLLECTORS);
				listMap.add(TABLEFARMING_BEING);
				listMap.add(TABLEFARMING_MATURINGAT);
				listMap.add(TABLEFARMING_MATURERATIO);
				listMap.add(TABLEFARMING_BEGINAT);
				listMap.add(TABLEFARMING_ENDAT);
				listMap.add(TABLECOLLECTOR_SETTOHEAD);
				listMap.add(TABLEFARMING_MAXTEM);
				listMap.add(TABLEFARMING_MINTEM);
				listMap.add(TABLEFARMING_MAXHUM);
				listMap.add(TABLEFARMING_MINHUM);
				listMap.add(TABLEFARMING_MAXSOILHUM);
				listMap.add(TABLEFARMING_MINSOILHUM);
				
				
				listMap.add(TABLETIMELINE_COLLECTOR);
				listMap.add(TABLETIMELINE_COLLECTEDAT);
				listMap.add(TABLETIMELINE_TEM);
				listMap.add(TABLETIMELINE_HUM);
				listMap.add(TABLETIMELINE_SIGNAL);
				listMap.add(TABLETIMELINE_POWDER);
				listMap.add(TABLETIMELINE_ILLU);
				listMap.add(TABLETIMELINE_FERTILITY);
				listMap.add(TABLETIMELINE_CATEGORY);
				listMap.add(TABLETIMELINE_SOIL_TEM);
				listMap.add(TABLETIMELINE_SOIL_HUM);
				
				
				
				listMap.add(TABLEKLINE_COLLECTOR);
				listMap.add(TABLEKLINE_CREATEDAT);
				listMap.add(TABLEKLINE_GATEWAY);
				listMap.add(TABLEKLINE_MAXTEM);
				listMap.add(TABLEKLINE_MINTEM);
				listMap.add(TABLEKLINE_MAXHUM);
				listMap.add(TABLEKLINE_MINHUM);
				listMap.add(TABLEKLINE_MAXILLU);
				listMap.add(TABLEKLINE_MINILLU);
				listMap.add(TABLEKLINE_MAX_FERTILITY);
				listMap.add(TABLEKLINE_MIN_FERTILITY);
				listMap.add(TABLEKLINE_CATEGORY);
				listMap.add(TABLEKLINE_MAX_SOIL_TEM);
				listMap.add(TABLEKLINE_MIN_SOIL_TEM);
				listMap.add(TABLEKLINE_MAX_SOIL_HUM);
				listMap.add(TABLEKLINE_MIN_SOIL_HUM);
				
				
				
				listMap.add(TABLEBEING_ADDBY);
				listMap.add(TABLEBEING_ISRESERVED);
				listMap.add(TABLEBEING_NAME);
				listMap.add(TABLEBEING_IMAGE);
				listMap.add(TABLEBEING_KINGDOM);
				listMap.add(TABLEBEING_ISANNUAL);
				listMap.add(TABLEBEING_ADDAT);
				listMap.add(TABLEBEING_UPDATEAT);
				listMap.add(TABLEBEING_MAXTEM);
				listMap.add(TABLEBEING_MINTEM);
				listMap.add(TABLEBEING_MAXHUM);
				listMap.add(TABLEBEING_MINHUM);
				listMap.add(TABLEBEING_MAXSOILHUM);
				listMap.add(TABLEBEING_MINSOILHUM);
				listMap.add(TABLEBEING_FARM);
				listMap.add(TABLEBEING_FIT_TEM);
				listMap.add(TABLEBEING_FIT_HUM);
				listMap.add(TABLEBEING_FIT_SOIL_HUM);

				
				
				listMap.add(TABLESOIL_ISRESERVED);
				listMap.add(TABLESOIL_ADDAT);
				listMap.add(TABLESOIL_UPDATEAT);
				listMap.add(TABLESOIL_NAME);
				listMap.add(TABLESOIL_ADDBY);
				listMap.add(TABLESOIL_FARM);
				
				
				listMap.add(TABLENOTICE_SUBJECT); 
				listMap.add(TABLENOTICE_SIGNAL);
				listMap.add(TABLENOTICE_CONTEXT);
				listMap.add(TABLENOTICE_USER);
				listMap.add(TABLENOTICE_ALERT);
				listMap.add(TABLENOTICE_OPERATOR);
				listMap.add(TABLENOTICE_CREATEDAT);
				listMap.add(TABLENOTICE_READAT);
				
				
				listMap.add(TABLEVIDEOUPLOAD_QNTOKEN);
				listMap.add(TABLEVIDEOUPLOAD_FILENAME);
				listMap.add(TABLEVIDEOUPLOAD_PERCENT);
				/**
				 * 0等待上传  1开始上传  2上传完成
				 */
				listMap.add(TABLEVIDEOUPLOAD_STATE);
				listMap.add(TABLEVIDEOUPLOAD_FARM);
				listMap.add(TABLEVIDEOUPLOAD_CREATETIME);
				//目前未使用
				listMap.add(TABLEVIDEOUPLOAD_FAILEDCOUNT);
				
				
				listMap.add(TABLEJOURNAL_USER);
				listMap.add(TABLEJOURNAL_SEGMENTS);
				listMap.add(TABLEJOURNAL_LIKES);
				listMap.add(TABLEJOURNAL_COMMENTS);
				listMap.add(TABLEJOURNAL_VIEWS);
				listMap.add(TABLEJOURNAL_KEY);
				listMap.add(TABLEJOURNAL_QINIUTOKEN);
				listMap.add(TABLEJOURNAL_QINIUKEY);
				listMap.add(TABLEJOURNAL_CONTENT);
				listMap.add(TABLEJOURNAL_LAT);
				listMap.add(TABLEJOURNAL_LNG);
				listMap.add(TABLEJOURNAL_LOCATION);
				listMap.add(TABLEJOURNAL_CREATEAT);
				listMap.add(TABLEJOURNAL_TRANSCODEDAT);
				listMap.add(TABLEJOURNAL_FARM);
				listMap.add(TABLEJOURNAL_MASKKEY);
				listMap.add(TABLEJOURNAL_FARMINGS);
				listMap.add(TABLEJOURNAL_THUMBNAIL);
				
				
				
				
				
				listMap.add(TABLECAMERA_DEVICEID);
				listMap.add(TABLECAMERA_DEVICESERIAL);
				listMap.add(TABLECAMERA_COVER_URL1);
				listMap.add(TABLECAMERA_CREATEDAT);
				listMap.add(TABLECAMERA_FARM);
				listMap.add(TABLECAMERA_NAME);
				listMap.add(TABLECAMERA_ONLINE); 
				listMap.add(TABLECAMERA_PLAYURL1); 
				listMap.add(TABLECAMERA_PLAYURL2); 
				listMap.add(TABLECAMERA_PUBLISH);
				listMap.add(TABLECAMERA_SHARE_URL);
				listMap.add(TABLECAMERA_UPDATEDAT);
				
				
				
				
				
				
				return listMap;
			} catch (Exception e) {
				return listMap;
		}
		}
		return listMap;
	}	
}


//try {
//Field[] fields=TablesColumns.class.getDeclaredFields();  
//Method[] methods = TablesColumns.class.getMethods();
//String[] fieldNames=new String[fields.length];  
//for(int i=0;i<fields.length;i++){  
//    fieldNames[i]=fields[i].getName();  
//}  
// 
//for(int i=0;i<fieldNames.length;i++){
//         for (int j = 0; j < methods.length; j++) {
//			if(methods[j].toString().toUpperCase().contains(fieldNames[i].toUpperCase()))
//			{
//				 Method method = methods[j];    
//				 Object value = method.invoke(TablesColumns.class);
//		         listMap.add(value.toString());
//			}
//		}				          
//        
//}
//return listMap;
//} catch (Exception e) {
//} 