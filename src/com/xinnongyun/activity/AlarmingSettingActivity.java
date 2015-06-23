package com.xinnongyun.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xinnongyun.adapter.BoxSoilAdapter;

/**
 * 报警设置
 * @author sm
 *
 */





public class AlarmingSettingActivity extends Activity implements OnClickListener{

	private ImageView alarmingReturn;
	private RelativeLayout slidemenu_alarming_rl;
	private LinearLayout alarming_open_close_ll;
	private TextView alarming_content;
	/**
	 * true开启短信通知
	 */
	private boolean isOpen = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏ActionBar
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//		         WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
//		
		setContentView(R.layout.activity_slidemenu_alarming_);
		alarmingReturn = (ImageView) findViewById(R.id.farmConfigChangeReturn);
		alarmingReturn.setOnClickListener(this);
		alarming_open_close_ll = (LinearLayout) findViewById(R.id.alarming_open_close_ll);
		alarming_open_close_ll.setOnClickListener(this);
		slidemenu_alarming_rl = (RelativeLayout) findViewById(R.id.slidemenu_alarming_rl);
		slidemenu_alarming_rl.setOnClickListener(this);
		alarming_content = (TextView) findViewById(R.id.alarming_content);
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.farmConfigChangeReturn:
			finish();
			break;
		case R.id.slidemenu_alarming_rl:
			showDialogBeyondLimit(AlarmingSettingActivity.this);
			
			break;
		case R.id.alarming_open_close_ll:
			mailOpenOrClose();
			break;
		default:
			break;
		}
	}
	
	
	private void mailOpenOrClose()
	{
		if(isOpen)
		{
			
		}
	}
	
	/**
	 * 提示设置系统报警通知
	 * @param context
	 */
	private void showDialogBeyondLimit(final Context context)
	{
		final AlertDialog dlg = new AlertDialog.Builder(context).create();
		dlg.show();
		Window window = dlg.getWindow();
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.tishi_show_dialog_add_create_farm);
		ListView list = (ListView) window
				.findViewById(R.id.add_create_farm_list);
		List<HashMap<String, String>> soilItems = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("name", getResources().getString(R.string.alarmingsetting_item_1));
		soilItems.add(map);
		map = new HashMap<String, String>();
		map.put("name", getResources().getString(R.string.alarmingsetting_item_2));
		soilItems.add(map);
		map = new HashMap<String, String>();
		map.put("name", getResources().getString(R.string.alarmingsetting_item_3));
		soilItems.add(map);
		final BoxSoilAdapter adapter = new BoxSoilAdapter(context, soilItems, "",3);
		list.setAdapter(adapter);
		adapter.selectType = alarming_content.getText().toString();
		list.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View view,
							int arg2, long arg3) {
						if(((TextView)view.findViewById(R.id.solillistitem_name)).getText().toString().equals(getResources().getString(R.string.alarmingsetting_item_1).toString()))
						{
							adapter.selectType = getResources().getString(R.string.alarmingsetting_item_1).toString();
						}
						else if(((TextView)view.findViewById(R.id.solillistitem_name)).getText().toString().equals(getResources().getString(R.string.alarmingsetting_item_2).toString()))
						{
							adapter.selectType = getResources().getString(R.string.alarmingsetting_item_2).toString();
						}
						else if(((TextView)view.findViewById(R.id.solillistitem_name)).getText().toString().equals(getResources().getString(R.string.alarmingsetting_item_3).toString()))
						{
							adapter.selectType = getResources().getString(R.string.alarmingsetting_item_3).toString();
						}
						adapter.notifyDataSetChanged();
					}
				}
				);
		// 为确认按钮添加事件,执行退出应用操作
		Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
			}
		});
		Button cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
			}
		});
	}
	
	
	
	
	
	
	
	
	
}
