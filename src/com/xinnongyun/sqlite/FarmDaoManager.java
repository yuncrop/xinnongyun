package com.xinnongyun.sqlite;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.xinnongyun.activity.MainActivity;

public class FarmDaoManager {

	private DBHelper dbHelper;
	private SQLiteDatabase dataBase;
	private Context context;
	public FarmDaoManager(Context context)
	{
		try {
			this.context = context;
			dbHelper = DBHelper.getInstance(context);
			dataBase = dbHelper.getWritableDatabase();
		} catch (Exception e) {
		}
	}
	
	
	//删除记录 type=1 删除任务
	public synchronized void clearFarmTable(int type)
	{
		try {
			String sqlFarming = "delete from " + TablesColumns.TABLEFARMING;
			dataBase.execSQL(sqlFarming);
			String sqlCollect = "delete from " + TablesColumns.TABLECOLLECTOR;
			dataBase.execSQL(sqlCollect);
			String sqlKline = "delete from " + TablesColumns.TABLEKLINE;
			dataBase.execSQL(sqlKline);
			String sqlTimeline = "delete from " + TablesColumns.TABLETIMELINE;
			dataBase.execSQL(sqlTimeline);
			String sqlBeing = "delete from " + TablesColumns.TABLEBEING;
			dataBase.execSQL(sqlBeing);
			
			String sqlJournalUpload ="delete from " + TablesColumns.TABLEVIDEOUPLOAD;
			dataBase.execSQL(sqlJournalUpload);
			
			String sqlJournalVideo ="delete from " + TablesColumns.TABLEJOURNAL;
			dataBase.execSQL(sqlJournalVideo);
			
			
			String sqlCamera ="delete from " + TablesColumns.TABLECAMERA;
			dataBase.execSQL(sqlCamera);
			
			String sqlWeatherStation ="delete from " + TablesColumns.TABLEWEATHERSTATION;
			dataBase.execSQL(sqlWeatherStation);
			
			String sqlWeatherStationTimeLine ="delete from " + TablesColumns.TABLESTATIONTIMELINR;
			dataBase.execSQL(sqlWeatherStationTimeLine);
			
			
			
			String sqlControlCenter ="delete from " + TablesColumns.TABLECONTROLCENTER;
			dataBase.execSQL(sqlControlCenter);
			
			
			String sqlBlower ="delete from " + TablesColumns.TABLEBLOWER;
			dataBase.execSQL(sqlBlower);
			
			String sqlBlowerEnv ="delete from " + TablesColumns.TABLEBBLOWERENV;
			dataBase.execSQL(sqlBlowerEnv);
			
			String sqlBlowerCmd ="delete from " + TablesColumns.TABLEBLOWERCMD;
			dataBase.execSQL(sqlBlowerCmd);
			
			
			
			String sqlSoil = "delete from " + TablesColumns.TABLESOIL;
			dataBase.execSQL(sqlSoil);
			String sqlNotice = "delete from " + TablesColumns.TABLENOTICE;
			if(type == 1)
			dataBase.execSQL(sqlNotice);
			
			
			Intent intent = new Intent();
			intent.setAction(MainActivity.farmingNewbroastCast);
			if(context !=null)
			context.sendBroadcast(intent);
			
			Intent intent2 = new Intent();
			intent2.setAction(MainActivity.farmConfigbroastCast);
			if(context !=null)
			context.sendBroadcast(intent2);
			if(type == 1)
			{
				Intent intent3 = new Intent();  
				intent3.setAction(MainActivity.noticeInfobroastCast);  
				if(context!=null)
				context.sendBroadcast(intent3);
			}
			
		} catch (Exception e) {
		}
	}
	
}
