package com.xinnongyun.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;

import com.xinnongyun.jpushbroadcast.JpushBroadCastReceiver;
import com.xinnongyun.sqlite.TablesColumns;

public class MainBrocastActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		
		if(intent != null)
		{
			int notifyid = intent.getIntExtra("notify_id", 0);
			if(notifyid!=0)
			{
				NotificationManager manger = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
				manger.cancel(notifyid);
				JpushBroadCastReceiver.notificationLruCache.reMoveKey(notifyid+"");
			}
			String notice_id = intent.getStringExtra(TablesColumns.TABLENOTICE_ID+"notice");
			if(notice_id!=null && notice_id.length()>0)
			{
				
			}
			
			if(ExitApplication.getInstance().checkActivity(MainActivity.class))
			{
				
				Activity a = ExitApplication.getInstance().getActivity(MainActivity.class);
				if(a!=null)
				{
				((MainActivity)a).mainFragment.showMainFragmentPage(2);
				}
				else
				{
				   intent.setAction(Intent.ACTION_MAIN);
				   intent.addCategory(Intent.CATEGORY_LAUNCHER);
				   intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				   intent.setClass(MainBrocastActivity.this, LoginActivity.class);
				   startActivity(intent);
				}
			}
			else
			{
				  intent.setAction(Intent.ACTION_MAIN);
				  intent.addCategory(Intent.CATEGORY_LAUNCHER);
				  intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				  intent.setClass(MainBrocastActivity.this, LoginActivity.class);
				  startActivity(intent);
			}
		}
		finish();
	}
}
