package com.xinnongyun.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * 开机启动
 * 
 * @author sm
 * 
 */
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//openService(context);
	}
	
	/**
	 * 开启后台服务
	 * @param context
	 */
	public static void openService(final Context context)
	{
		new Thread(
				new Runnable() {
					
					@Override
					public void run() {
						PendingIntent operation = PendingIntent.getService(context, -1,
								new Intent(context, CoreService.class),
								PendingIntent.FLAG_UPDATE_CURRENT);
						AlarmManager alarmManager = (AlarmManager) context
								.getSystemService(Context.ALARM_SERVICE);
							long firstTime = SystemClock.elapsedRealtime();
							alarmManager.cancel(operation);

							alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
									firstTime, 10 * 1000, operation);
					}
				}
				).start();
		
	}

}
