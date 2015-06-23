package com.xinnongyun.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xinnongyun.adapter.ControlCenterSettingsItemAdapter;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.sqlite.BlowerDaoDBManager;
import com.xinnongyun.sqlite.BlowerTimeLineDaoDBManager;
import com.xinnongyun.sqlite.ControlCenterDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.utils.StringUtil;
import com.xinnongyun.view.IconFontTextView;

/**
 * 机器人总设置
 * @author sm
 *
 */
public class BlowerDetailInfoSetting extends Activity {

	private String ccId,serial;;
	private String ccBlowerIds;
	private TextView ccName,collectRefreshTime,collectRefreshTimeUnit;
	private String name;
	private ImageView cc_info_setting;
	private ListView robot_detial_list;
	private LinearLayout settingsll,clearClockll,forceOpenClosell;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.robot_detail);
		ccId = getIntent().getStringExtra("id");
		name = getIntent().getStringExtra("ccName");
		serial = getIntent().getStringExtra("serial");
		ccBlowerIds = getIntent().getStringExtra("ccBlowerIds");
		initialView();
		initialListener();
		
		//中控实时数据
		IntentFilter ccFilter = new IntentFilter();
		ccFilter.addAction(MainActivity.controlcenterbroastCast); 
		registerReceiver(ccBc, ccFilter);
	}
	
	
	
	
	
	
	//中控信息广播通知
    BroadcastReceiver ccBc = new  BroadcastReceiver(){
    	@Override
        public void onReceive(Context context, Intent intent) {
    		if(intent.getAction().equals(MainActivity.controlcenterbroastCast))
    		{
    			try {
    				BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(BlowerDetailInfoSetting.this);
    				List<HashMap<String, String>> bMaps = blowerDaoDBManager.getAllBlowersByCCId(ccId);
    				int bTSize = 8 - bMaps.size();
    				for(int i =0; i<bTSize;i++)
    				{
    					bMaps.add(new HashMap<String, String>());
    				}
    				robot_detial_list.setAdapter(new ControlCenterSettingsItemAdapter(BlowerDetailInfoSetting.this, bMaps));
    				BlowerTimeLineDaoDBManager blowerTimeLineDaoDBManager = new BlowerTimeLineDaoDBManager(BlowerDetailInfoSetting.this);
    				HashMap<String, String>  tMap = blowerTimeLineDaoDBManager.getCollectLastUpdateTimeCC(ccBlowerIds);
    				if(tMap!=null && tMap.size()>1)
    				{
    					try {
    						DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    						Date d2 = df.parse(StringUtil.changeSerTo(tMap.get(TablesColumns.TABLEBBLOWERENV_CREATEDAT)));
    						if(StringUtil.miniteLength(new Date(), d2)<=YunTongXun.minuteLength)
    						{
    							collectRefreshTime.setTextColor(getResources().getColor(R.color.text_grey));
    							collectRefreshTimeUnit.setTextColor(getResources().getColor(R.color.text_grey));
    						}
    						
    						String[] time = StringUtil.getTimeDiff(d2).split(":");
    						collectRefreshTime.setText(time[0]);
    						collectRefreshTimeUnit.setText(time[1]);
    					} catch (Exception e) {
    					}
    				}
				} catch (Exception e) {
				}
    		}
        }
    };
	
	
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(ccBc);
		
	};
	
	
	private void initialListener()
	{
		collectRefreshTime = (TextView) findViewById(R.id.collectRefreshTime);
		collectRefreshTimeUnit = (TextView) findViewById(R.id.collectRefreshTime_unit);
		BlowerTimeLineDaoDBManager blowerTimeLineDaoDBManager = new BlowerTimeLineDaoDBManager(this);
		HashMap<String, String>  tMap = blowerTimeLineDaoDBManager.getCollectLastUpdateTimeCC(ccBlowerIds);
		if(tMap!=null && tMap.size()>1)
		{
			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date d2 = df.parse(StringUtil.changeSerTo(tMap.get(TablesColumns.TABLEBBLOWERENV_CREATEDAT)));
				if(StringUtil.miniteLength(new Date(), d2)<=YunTongXun.minuteLength)
				{
					collectRefreshTime.setTextColor(getResources().getColor(R.color.text_grey));
					collectRefreshTimeUnit.setTextColor(getResources().getColor(R.color.text_grey));
				}
				
				String[] time = StringUtil.getTimeDiff(d2).split(":");
				collectRefreshTime.setText(time[0]);
				collectRefreshTimeUnit.setText(time[1]);
			} catch (Exception e) {
			}
		}
		settingsll.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent forceIntent = new Intent();
						forceIntent.putExtra("ccId", ccId);
						forceIntent.setClass(BlowerDetailInfoSetting.this, BlowerInfoSettingActivity.class);
						startActivityForResult(forceIntent,10);
					}
				}
				);
		clearClockll.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent forceIntent = new Intent();
						forceIntent.putExtra("ccId", ccId);
						forceIntent.setClass(BlowerDetailInfoSetting.this, BlowerClearClockActivity.class);
						startActivity(forceIntent);
					}
				}
				);
		forceOpenClosell.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent forceIntent = new Intent();
						forceIntent.putExtra("ccId", ccId);
						forceIntent.setClass(BlowerDetailInfoSetting.this, BlowerForceOpenActivity.class);
						startActivity(forceIntent);
					}
				}
				);
	}
	
	
	
	private void initialView()
	{
		settingsll = (LinearLayout) findViewById(R.id.settingsll);
		clearClockll = (LinearLayout) findViewById(R.id.clearClockll);
		forceOpenClosell = (LinearLayout) findViewById(R.id.forceOpenClosell);
		
		findViewById(R.id.collectReturn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				}
				);
		((IconFontTextView)findViewById(R.id.blowerIcon_tv)).setTextColor(Color.parseColor("#edfe33"));
		((IconFontTextView)findViewById(R.id.blowerIcon_tv)).setText(String.valueOf(new char[]{0xe663}));
		
		((IconFontTextView)findViewById(R.id.forceOpenBlowerTv)).setText(String.valueOf(new char[]{0xe65e}));
		
		((IconFontTextView)findViewById(R.id.OpenOrClockTv)).setText(String.valueOf(new char[]{0xe661}));
		
		((IconFontTextView)findViewById(R.id.settingsFreshTv)).setText(String.valueOf(new char[]{0xe65f}));
		
		ccName = (TextView) findViewById(R.id.collectdetailname);
		ccName.setText(name);
		cc_info_setting = (ImageView) findViewById(R.id.btn_collect_detail_setting);
		cc_info_setting.setOnClickListener(
				new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent cInfoIntent = new Intent();
						cInfoIntent.putExtra("name", name);
						cInfoIntent.putExtra("id", ccId);
						cInfoIntent.putExtra("serial", serial);
						cInfoIntent.setClass(BlowerDetailInfoSetting.this, ControlCenterInfoActivity.class);
						startActivityForResult(cInfoIntent,10);
					}
				}
				);
		robot_detial_list = (ListView) findViewById(R.id.robot_detial_list);
		BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(this);
		List<HashMap<String, String>> bMaps = blowerDaoDBManager.getAllBlowersByCCId(ccId);
		int bTSize = 8 - bMaps.size();
		for(int i =0; i<bTSize;i++)
		{
			bMaps.add(new HashMap<String, String>());
		}
		robot_detial_list.setAdapter(new ControlCenterSettingsItemAdapter(this, bMaps));
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 20)
		{
			finish();
		}
		if(resultCode == 30)
		{
			BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(this);
			List<HashMap<String, String>> bMaps = blowerDaoDBManager.getAllBlowersByCCId(ccId);
			int bTSize = 8 - bMaps.size();
			for(int i =0; i<bTSize;i++)
			{
				bMaps.add(new HashMap<String, String>());
			}
			robot_detial_list.setAdapter(new ControlCenterSettingsItemAdapter(this, bMaps));
		}
		
		if(resultCode == 10)
		{
			ControlCenterDaoDBManager centerDaoDBManager = new ControlCenterDaoDBManager(this);
			HashMap<String, String> map = centerDaoDBManager.getControlCenterById(ccId);
			if(map!=null && map.keySet().contains(TablesColumns.TABLECONTROLCENTER_NAME))
			{
				ccName.setText(map.get(TablesColumns.TABLECONTROLCENTER_NAME).toString());
			}
		}
	}
}
