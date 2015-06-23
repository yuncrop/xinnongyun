package com.xinnongyun.sqlite;

import com.xinnongyun.sharepreference.MySharePreference;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	protected DBHelper(Context context) {
			super(context, TablesColumns.DBNAME, null, TablesColumns.DB_VERSION);
			this.context = context;
	}

	private static DBHelper dbInstance;
	private Context context;
	public synchronized static DBHelper getInstance(Context context) {
		if (dbInstance == null) {
			dbInstance = new DBHelper(context.getApplicationContext());
		}
		return dbInstance;
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
			//TODO 创建盒子
			String tableCollect = "create table if not exists %s (%s integer primary key, %s text";
			for (int i = 0; i < 24; i++) {
				tableCollect += ", %s text";
			}
			tableCollect += ");";
			String sql = String.format(tableCollect, TablesColumns.TABLECOLLECTOR,
					TablesColumns.TABLE_ID,
					TablesColumns.TABLECOLLECTOR_ID,
					TablesColumns.TABLECOLLECTOR_CATEGORY,
					TablesColumns.TABLECOLLECTOR_NAME,
					TablesColumns.TABLECOLLECTOR_QRCODE,
					TablesColumns.TABLECOLLECTOR_SERIAL,
					TablesColumns.TABLECOLLECTOR_GATEWAY,
					TablesColumns.TABLECOLLECTOR_UPDATEDAT,
					TablesColumns.TABLECOLLOCTOR_FARM,
					TablesColumns.TABLECOLLOCTOR_LAT,
					TablesColumns.TABLECOLLOCTOR_LNG,
					TablesColumns.TABLECOLLOCTOR_LOCATION,
					TablesColumns.TABLECOLLOCTOR_SOIL,
					TablesColumns.TABLECOLLOCTOR_MAX_TEM,
					TablesColumns.TABLECOLLOCTOR_MIN_TEM,
					TablesColumns.TABLECOLLOCTOR_MAX_HUM,
					TablesColumns.TABLECOLLOCTOR_MIN_HUM,
					TablesColumns.TABLECOLLOCTOR_TEM_OVERFLOW,
					TablesColumns.TABLECOLLOCTOR_HUM_OVERFLOW,
					TablesColumns.TABLECOLLOCTOR_SOIL_HUM_OVERFLOW,
					TablesColumns.TABLECOLLOCTOR_TEM_OVERFLOW_BEGIN_AT,
				    TablesColumns.TABLECOLLOCTOR_HUM_OVERFLOW_BEGIN_AT, 
				    TablesColumns.TABLECOLLOCTOR_SOIL_HUM_OVERFLOW_BEGIN_AT,
				    TablesColumns.TABLECOLLOCTOR_MAX_SOIL_HUM,
				    TablesColumns.TABLECOLLOCTOR_MIN_SOIL_HUM,
					TablesColumns.TABLECOLLECTOR_PRODUCTEDAT);
			db.execSQL(sql);
			
			//TODO 创建时间线表
			String tableTimeLine = "create table if not exists %s (%s integer primary key, %s text";
			for (int i = 0; i < 11; i++) {
				tableTimeLine += ", %s text";
			}
			tableTimeLine += ");";
			sql = String.format(tableTimeLine, TablesColumns.TABLETIMELINE,
					TablesColumns.TABLE_ID,
					TablesColumns.TABLETIMELINE_ID,
					TablesColumns.TABLETIMELINE_COLLECTEDAT,
					TablesColumns.TABLETIMELINE_HUM,
					TablesColumns.TABLETIMELINE_SIGNAL,
					TablesColumns.TABLETIMELINE_POWDER,
					TablesColumns.TABLETIMELINE_ILLU,
					TablesColumns.TABLETIMELINE_COLLECTOR,
					TablesColumns.TABLETIMELINE_FERTILITY,
					TablesColumns.TABLETIMELINE_CATEGORY,
					TablesColumns.TABLETIMELINE_SOIL_TEM,
					TablesColumns.TABLETIMELINE_SOIL_HUM,
					TablesColumns.TABLETIMELINE_TEM);
			db.execSQL(sql);
	
			//TODO 创建K时间线表
			String tableKLine = "create table if not exists %s (%s integer primary key, %s text";
			for (int i = 0; i < 16; i++) {
				tableKLine += ", %s text";
			}
			tableKLine += ");";
			sql = String.format(tableKLine, TablesColumns.TABLEKLINE,
					TablesColumns.TABLE_ID,
					TablesColumns.TABLEKLINE_ID,
					TablesColumns.TABLEKLINE_COLLECTOR,
					TablesColumns.TABLEKLINE_CREATEDAT,
					TablesColumns.TABLEKLINE_GATEWAY,
					TablesColumns.TABLEKLINE_MAXHUM,
					TablesColumns.TABLEKLINE_MAX_FERTILITY,
					TablesColumns.TABLEKLINE_MIN_FERTILITY,
					TablesColumns.TABLEKLINE_MAXILLU,
					TablesColumns.TABLEKLINE_MAXTEM,
					TablesColumns.TABLEKLINE_MINHUM,
					TablesColumns.TABLEKLINE_MINILLU,
					TablesColumns.TABLEKLINE_CATEGORY,
					TablesColumns.TABLEKLINE_MAX_SOIL_TEM,
					TablesColumns.TABLEKLINE_MIN_SOIL_TEM,
					TablesColumns.TABLEKLINE_MAX_SOIL_HUM,
					TablesColumns.TABLEKLINE_MIN_SOIL_HUM,
					TablesColumns.TABLEKLINE_MINTEM);
			
			db.execSQL(sql);
	
			//TODO 创建作物动物表
			String tableBeing = "create table if not exists %s (%s integer primary key, %s text";
			for (int i = 0; i < 18; i++) {
				tableBeing += ", %s text";
			}
			tableBeing += ");";
			sql = String
					.format(tableBeing, TablesColumns.TABLEBEING,
							TablesColumns.TABLE_ID,
							TablesColumns.TABLEBEING_ID,
							TablesColumns.TABLEBEING_ADDBY,
							TablesColumns.TABLEBEING_ISRESERVED,
							TablesColumns.TABLEBEING_ADDAT,
							TablesColumns.TABLEBEING_ISANNUAL,
							TablesColumns.TABLEBEING_UPDATEAT,
							TablesColumns.TABLEBEING_IMAGE,
							TablesColumns.TABLEBEING_KINGDOM,
							TablesColumns.TABLEBEING_NAME,
							TablesColumns.TABLEBEING_MAXHUM,
							TablesColumns.TABLEBEING_MAXSOILHUM,
							TablesColumns.TABLEBEING_MAXTEM,
							TablesColumns.TABLEBEING_MINHUM,
							TablesColumns.TABLEBEING_MINSOILHUM,
							TablesColumns.TABLEBEING_MINTEM,
							TablesColumns.TABLEBEING_FIT_HUM,
							TablesColumns.TABLEBEING_FIT_TEM,
							TablesColumns.TABLEBEING_FIT_SOIL_HUM,
							TablesColumns.TABLEBEING_FARM);
			db.execSQL(sql);
			//TODO 创建土壤
			String tableSoil = "create table if not exists %s (%s integer primary key, %s text";
			for (int i = 0; i < 6; i++) {
				tableSoil += ", %s text";
			}
			tableSoil += ");";
			sql = String.format(tableSoil, TablesColumns.TABLESOIL,
					TablesColumns.TABLE_ID,
					TablesColumns.TABLESOIL_ID,
					TablesColumns.TABLESOIL_ADDBY,
					TablesColumns.TABLESOIL_ISRESERVED,
					TablesColumns.TABLESOIL_ADDAT,
					TablesColumns.TABLESOIL_UPDATEAT,
					TablesColumns.TABLESOIL_NAME,
					TablesColumns.TABLESOIL_FARM);
			db.execSQL(sql);
			
			
			//TODO 创建种养记录
			String tableFarming = "create table if not exists %s (%s integer primary key, %s text";
			for (int i = 0; i < 15; i++) {
				tableFarming += ", %s text";
			}
			tableFarming += ");";
			sql = String.format(tableFarming, TablesColumns.TABLEFARMING,
					TablesColumns.TABLE_ID,
					TablesColumns.TABLEFARMING_ID,
					TablesColumns.TABLEFARMING_FARM,
					TablesColumns.TABLEFARMING_GATEWAY,
					TablesColumns.TABLEFARMING_COLLECTORS,
					TablesColumns.TABLEFARMING_BEING,
					TablesColumns.TABLEFARMING_MATURINGAT, 
					TablesColumns.TABLEFARMING_MATURERATIO,
					TablesColumns.TABLEFARMING_BEGINAT,
					TablesColumns.TABLECOLLECTOR_SETTOHEAD,
					TablesColumns.TABLEFARMING_ENDAT,
					TablesColumns.TABLEFARMING_MAXTEM,
					TablesColumns.TABLEFARMING_MINTEM,
					TablesColumns.TABLEFARMING_MAXHUM,
					TablesColumns.TABLEFARMING_MINHUM,
					TablesColumns.TABLEFARMING_MAXSOILHUM,
					TablesColumns.TABLEFARMING_MINSOILHUM);
			db.execSQL(sql);
			
			
			
			//TODO 创建通知
			String tableNotice = "create table if not exists %s (%s integer primary key, %s text";
			for (int i = 0; i < 8; i++) {
				tableNotice += ", %s text";
			}
			tableNotice += ");";
			sql = String.format(tableNotice, TablesColumns.TABLENOTICE,
					TablesColumns.TABLE_ID,
					TablesColumns.TABLENOTICE_ID,
					TablesColumns.TABLENOTICE_CONTEXT,
					TablesColumns.TABLENOTICE_CREATEDAT,
					TablesColumns.TABLENOTICE_READAT,
					TablesColumns.TABLENOTICE_SIGNAL,
					TablesColumns.TABLENOTICE_SUBJECT,
					TablesColumns.TABLENOTICE_ALERT,
					TablesColumns.TABLENOTICE_OPERATOR,
					TablesColumns.TABLENOTICE_USER);
			db.execSQL(sql);
			
			
			//TODO 创建视频上传列表
			String tableVideoList = "create table if not exists %s (%s integer primary key, %s text";
			for (int i = 0; i < 5; i++) {
				tableVideoList += ", %s text";
			}
			tableVideoList += ");";
			sql = String.format(tableVideoList, TablesColumns.TABLEVIDEOUPLOAD,
					TablesColumns.TABLE_ID,
					TablesColumns.TABLEVIDEOUPLOAD_FARM,
					TablesColumns.TABLEVIDEOUPLOAD_FILENAME,
					TablesColumns.TABLEVIDEOUPLOAD_PERCENT,
					TablesColumns.TABLEVIDEOUPLOAD_QNTOKEN,
					TablesColumns.TABLEVIDEOUPLOAD_CREATETIME,
					TablesColumns.TABLEVIDEOUPLOAD_STATE);
			db.execSQL(sql);
			
			
			//TODO 创建视频日志列表
			String tableJournalList = "create table if not exists %s (%s integer primary key, %s text";
			for (int i = 0; i < 18; i++) {
				tableJournalList += ", %s text";
			}
			tableJournalList += ");";
			sql = String.format(tableJournalList, TablesColumns.TABLEJOURNAL,
					TablesColumns.TABLE_ID,
					TablesColumns.TABLEJOURNAL_ID,
					TablesColumns.TABLEJOURNAL_KEY,
					TablesColumns.TABLEJOURNAL_LAT,
					TablesColumns.TABLEJOURNAL_LNG,
					TablesColumns.TABLEJOURNAL_LIKES,
					TablesColumns.TABLEJOURNAL_LOCATION,
					TablesColumns.TABLEJOURNAL_QINIUKEY,
					TablesColumns.TABLEJOURNAL_QINIUTOKEN,
					TablesColumns.TABLEJOURNAL_SEGMENTS,
					TablesColumns.TABLEJOURNAL_TRANSCODEDAT,
					TablesColumns.TABLEJOURNAL_USER,
					TablesColumns.TABLEJOURNAL_MASKKEY,
					TablesColumns.TABLEJOURNAL_VIEWS,
					TablesColumns.TABLEJOURNAL_FARMINGS,
					TablesColumns.TABLEJOURNAL_FARM,
					TablesColumns.TABLEJOURNAL_COMMENTS,
					TablesColumns.TABLEJOURNAL_CONTENT,
					TablesColumns.TABLEJOURNAL_CREATEAT,
					TablesColumns.TABLEJOURNAL_THUMBNAIL);
			db.execSQL(sql);
			
			
			//TODO 创建环境小站列表
			String weatherStationList = "create table if not exists %s (%s integer primary key, %s text";
			for (int i = 0; i < 5; i++) {
				weatherStationList += ", %s text";
			}
			weatherStationList += ");";
			sql = String.format(weatherStationList, TablesColumns.TABLEWEATHERSTATION,
					TablesColumns.TABLE_ID,
					TablesColumns.TABLEWEATHERSTATION_ID,
					TablesColumns.TABLEWEATHERSTATION_CREATEDAT,
					TablesColumns.TABLEWEATHERSTATION_FARM,
					TablesColumns.TABLEWEATHERSTATION_NAME,
					TablesColumns.TABLEWEATHERSTATION_SERIAL,
					TablesColumns.TABLEWEATHERSTATION_UPDATEDAT
					);
			db.execSQL(sql);
	
			
			//TODO 创建环境小站时间线表
			String stationLimeLineList = "create table if not exists %s (%s integer primary key, %s text";
			for (int i = 0; i < 17; i++) {
				stationLimeLineList += ", %s text";
			}
			stationLimeLineList += ");";
			sql = String.format(stationLimeLineList, TablesColumns.TABLESTATIONTIMELINR,
					TablesColumns.TABLE_ID,
					TablesColumns.TABLESTATIONTIMELINR_ID,
					TablesColumns.TABLESTATIONTIMELINR_ALT,
					TablesColumns.TABLESTATIONTIMELINR_CO2,
					TablesColumns.TABLESTATIONTIMELINR_CREATEDAT,
					TablesColumns.TABLESTATIONTIMELINR_DEW,
					TablesColumns.TABLESTATIONTIMELINR_HUM,
					TablesColumns.TABLESTATIONTIMELINR_ILLU,
					TablesColumns.TABLESTATIONTIMELINR_LAT,
					TablesColumns.TABLESTATIONTIMELINR_LNG,
					TablesColumns.TABLESTATIONTIMELINR_LOCATION,
					TablesColumns.TABLESTATIONTIMELINR_PM1,
					TablesColumns.TABLESTATIONTIMELINR_PM10,
					TablesColumns.TABLESTATIONTIMELINR_PM25,
					TablesColumns.TABLESTATIONTIMELINR_POWER,
					TablesColumns.TABLESTATIONTIMELINR_PRES,
					TablesColumns.TABLESTATIONTIMELINR_SENSOR,
					TablesColumns.TABLESTATIONTIMELINR_SIGNAL,
					TablesColumns.TABLESTATIONTIMELINR_TEM
					);
			db.execSQL(sql);
			
			
			
			
			//TODO 创建摄像头列表
			String cameraList = "create table if not exists %s (%s integer primary key, %s text";
			for (int i = 0; i < 12; i++) {
				cameraList += ", %s text";
			}
			cameraList += ");";
			sql = String.format(cameraList, TablesColumns.TABLECAMERA,
					TablesColumns.TABLE_ID,
					TablesColumns.TABLECAMERA_ID,
					TablesColumns.TABLECAMERA_COVER_URL1,
					TablesColumns.TABLECAMERA_CREATEDAT,
					TablesColumns.TABLECAMERA_DEVICEID,
					TablesColumns.TABLECAMERA_DEVICESERIAL,
					TablesColumns.TABLECAMERA_FARM,
					TablesColumns.TABLECAMERA_NAME,
					TablesColumns.TABLECAMERA_ONLINE,
					TablesColumns.TABLECAMERA_PLAYURL1,
					TablesColumns.TABLECAMERA_PLAYURL2,
					TablesColumns.TABLECAMERA_PUBLISH,
					TablesColumns.TABLECAMERA_SHARE_URL,
					TablesColumns.TABLECAMERA_UPDATEDAT
					);
			db.execSQL(sql);
			
			
			
			//TODO 创建中控表
			String controlcenter = "create table if not exists %s (%s integer primary key, %s text";
			for (int i = 0; i < 5; i++) {
				controlcenter += ", %s text";
			}
			controlcenter += ");";
			sql = String.format(controlcenter, TablesColumns.TABLECONTROLCENTER,
					TablesColumns.TABLE_ID,
					TablesColumns.TABLECONTROLCENTER_ID,
					TablesColumns.TABLECONTROLCENTER_FARMID,
					TablesColumns.TABLECONTROLCENTER_NAME,
					TablesColumns.TABLECONTROLCENTER_PRODUCEDAT,
					TablesColumns.TABLECONTROLCENTER_SERIAL,
					TablesColumns.TABLECONTROLCENTER_UPDATEDAT
					);
			db.execSQL(sql);
			
			
			//TODO 创建放风机
			String blower = "create table if not exists %s (%s integer primary key, %s text";
			for (int i = 0; i < 11; i++) {
				blower += ", %s text";
			}
			blower += ");";
			sql = String.format(blower, TablesColumns.TABLEBLOWER,
					TablesColumns.TABLE_ID,
					TablesColumns.TABLEBLOWER_ID,
					TablesColumns.TABLEBLOWER_CCID,
					TablesColumns.TABLEBLOWER_ADDITION_DISTANCE,
					TablesColumns.TABLEBLOWER_DISTANCE,
					TablesColumns.TABLEBLOWER_MAX_DISTANCE,
					TablesColumns.TABLEBLOWER_MAXTEMLIMIT,
					TablesColumns.TABLEBLOWER_MINTEMLIMIT,
					TablesColumns.TABLEBLOWER_NO,
					TablesColumns.TABLEBLOWER_STEP_PAUSE_DISTANCE,
					TablesColumns.TABLEBLOWER_STEP_RUN_DISTANCE,
					TablesColumns.TABLEBLOWER_UPDATEDAT,
					TablesColumns.TABLEBLOWER_WORKING_MODE
					);
			db.execSQL(sql);
			
			
			//TODO 创建放风机数据表
			String blowerenv = "create table if not exists %s (%s integer primary key, %s text";
			for (int i = 0; i < 4; i++) {
				blowerenv += ", %s text";
			}
			blowerenv += ");";
			sql = String.format(blowerenv, TablesColumns.TABLEBBLOWERENV,
					TablesColumns.TABLE_ID,
					TablesColumns.TABLEBBLOWERENV_ID,
					TablesColumns.TABLEBBLOWERENV_BLOWERID,
					TablesColumns.TABLEBBLOWERENV_CREATEDAT,
					TablesColumns.TABLEBBLOWERENV_DISTANCE,
					TablesColumns.TABLEBBLOWERENV_TEM
					);
			db.execSQL(sql);
			
			
			//TODO 创建放风机命令表
			String blowercmd = "create table if not exists %s (%s integer primary key, %s text";
			for (int i = 0; i < 6; i++) {
				blowercmd += ", %s text";
			}
			blowercmd += ");";
			sql = String.format(blowercmd, TablesColumns.TABLEBLOWERCMD,
					TablesColumns.TABLE_ID,
					TablesColumns.TABLEBLOWERCMD_ID,
					TablesColumns.TABLEBLOWERCMD_BLOWERID,
					TablesColumns.TABLEBLOWERCMD_CMD,
					TablesColumns.TABLEBLOWERCMD_CREATEDAT,
					TablesColumns.TABLEBLOWERCMD_EXECUTEDAT,
					TablesColumns.TABLEBLOWERCMD_RESULT,
					TablesColumns.TABLEBLOWERCMD_RETRIES
					);
			db.execSQL(sql);
			
			
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		TablesColumns.listMap = null;
		MySharePreference.clearOneState(context,
				MySharePreference.LOGINSTATE);
		onCreate(db);
	}
	
//	/**
//	 * 更新数据库版本   舍弃
//	 */
//	private void UpdateTables(String[] tableNames)
//	{
//		//进度条显示版本更新
//	
//		if(tableNames!=null && tableNames.length >=1)
//		{
//			for(String tableName : tableNames)
//			{
//				//db.execSQL("alter table users add userage integer");
//				//db.execSQL("alter table users add usersalary doouble");
//			}
//		}
//	}
//	
	
//	/**
//	 * 得到表列
//	 * @param db
//	 * @param tableName
//	 * @return
//	 */
//	private String[] getColumnNames(SQLiteDatabase db, String tableName)  
//	{
//	    String[] columnNames = null;
//	    Cursor c = null;
//	    try
//	    {
//	        c = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
//	        if (null != c)
//	        {
//	            int columnIndex = c.getColumnIndex("name");
//	            if (-1 == columnIndex)
//	            {
//	                return null;
//	            }
//	            int index = 0;
//	            columnNames = new String[c.getCount()];
//	            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
//	            {
//	                columnNames[index] = c.getString(columnIndex);
//	                index++;
//	            }
//	        }
//	    }
//	    catch (Exception e)
//	    {
//	        e.printStackTrace();
//	    }
//	    finally
//	    {
//	    	c.close();
//	    }
//	    return columnNames;
//	}  
//
//	
//	/**
//	 * 更新升级表
//	 * @param db
//	 * @param tableName
//	 * @param columns
//	 */
//	private void upGradeTables(SQLiteDatabase db, String tableName, String columns)
//	{
//		 try  
//		    {  
//		        db.beginTransaction();
//		        // 1, Rename table.
//		        String tempTableName = tableName + "_temp";
//		        String sql = "ALTER TABLE " + tableName +" RENAME TO " + tempTableName;
//		        db.execSQL(sql);
//		        // 2, Create table.
//		        onCreate(db);
//		        // 3, Load data  拷贝到 临时表
//		        sql = "INSERT INTO " + tableName +
//		               " (" + columns + ") " +
//		               " SELECT " + columns + " FROM " + tempTableName;
//		        db.execSQL(sql);
//		        // 4, Drop the temporary table.
//		        sql = "DROP TABLE IF EXISTS " + tempTableName;
//		        db.execSQL(sql);
//		        db.setTransactionSuccessful();
//		    }
//		    catch (SQLException e)
//		    {  
//		        e.printStackTrace();
//		    } 
//		    catch (Exception e)
//		    {
//		        e.printStackTrace();
//		    }  
//		    finally
//		    {
//		        db.endTransaction();
//		    } 
//	}
//	
	
	
	
	
	
	
	
}
