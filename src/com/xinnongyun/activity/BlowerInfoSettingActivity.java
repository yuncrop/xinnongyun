package com.xinnongyun.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.simonvt.numberpicker.NumberPicker;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xinnongyun.adapter.BoxSoilAdapter;
import com.xinnongyun.adapter.ControlCenterForceItemAdapter;
import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.net.NetConnectionCollector;
import com.xinnongyun.net.NetConnectionCollector.CollectorSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.BlowerDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.utils.DialogManager;
import com.xinnongyun.utils.StringUtil;

/**
 * 风口设置
 * 
 * @author sm
 * 
 */
public class BlowerInfoSettingActivity extends Activity implements
		OnClickListener {

	private GridView gridViewBlower;
	private Button complete_btn;
	private String ccId;
	private List<HashMap<String, String>> bMaps;
	private TextView temmin, temmax, initialdistance, maxdistance, step_time,
			step_stop,zero_or_normal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.robot_detail_infosetting);
		ccId = getIntent().getStringExtra("ccId");

		temmin = (TextView) findViewById(R.id.temmin);
		temmax = (TextView) findViewById(R.id.temmax);
		initialdistance = (TextView) findViewById(R.id.initialdistance);
		maxdistance = (TextView) findViewById(R.id.maxdistance);
		step_time = (TextView) findViewById(R.id.step_time);
		step_stop = (TextView) findViewById(R.id.step_stop);
		zero_or_normal = (TextView) findViewById(R.id.zero_or_normal);
		
		findViewById(R.id.collectReturn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});

		gridViewBlower = (GridView) findViewById(R.id.robot_item_contents);
		ControlCenterForceItemAdapter.hasList = new ArrayList<String>();
		BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(this);
		bMaps = blowerDaoDBManager.getAllBlowersByCCId(ccId);
		int bCount = 0;
		for (HashMap<String, String> map : bMaps) {
			ControlCenterForceItemAdapter.hasList.add(map.get(
					TablesColumns.TABLEBLOWER_NO).toString());
			
			if(bCount == 0)
			{
				temmin.setText(map.get(TablesColumns.TABLEBLOWER_MINTEMLIMIT));
				temmax.setText(map.get(TablesColumns.TABLEBLOWER_MAXTEMLIMIT));
				initialdistance.setText(Integer.parseInt(map.get(TablesColumns.TABLEBLOWER_ADDITION_DISTANCE))/60 + "");
				maxdistance.setText(Integer.parseInt(map.get(TablesColumns.TABLEBLOWER_MAX_DISTANCE))/60 + "");
				step_stop.setText(Integer.parseInt(map.get(TablesColumns.TABLEBLOWER_STEP_PAUSE_DISTANCE))/60 + "");
				step_time.setText(Integer.parseInt(map.get(TablesColumns.TABLEBLOWER_STEP_RUN_DISTANCE))/60 + "");
				bCount ++ ;
			}
			
		}
		
		int bTSize = 8 - bMaps.size();
		for (int i = 0; i < bTSize; i++) {
			bMaps.add(new HashMap<String, String>());
		}

		gridViewBlower
				.setAdapter(new ControlCenterForceItemAdapter(this, bMaps));
		findViewById(R.id.tem_min_max_rl).setOnClickListener(this);
		findViewById(R.id.initialdistancerl).setOnClickListener(this);
		findViewById(R.id.maxdistancerl).setOnClickListener(this);
		findViewById(R.id.step_time_stop_rl).setOnClickListener(this);
		complete_btn = (Button) findViewById(R.id.complete_btn);
		complete_btn.setOnClickListener(this);
		findViewById(R.id.statefreshrl).setOnClickListener(this);
		
		
		
		
	}

	private void modifyBlowerInfo(final int type) {
		if (bMaps == null || bMaps.size() == 0) {
			return;
		}
		
		if(!checkSettingNum(initialdistance.getText().toString(), maxdistance.getText().toString()) )
		{
			DialogManager.showDialogSimple(
					BlowerInfoSettingActivity.this,
					R.string.farmingAlarm_setting_num_error_tem);
			return;
		}
		
		if(!checkSettingNum(temmin.getText().toString(), temmax.getText().toString()) )
		{
			DialogManager.showDialogSimple(
					BlowerInfoSettingActivity.this,
					R.string.farmingAlarm_setting_num_error_blower);
			return;
		}
		int idsLength = ControlCenterForceItemAdapter.selectList.size();
		String lockIds = "[";

		for (int i = 0; i < idsLength; i++) {
			for (HashMap<String, String> map : bMaps) {
				if (map.size()>0 && map.get(TablesColumns.TABLEBLOWER_NO).equals(
						ControlCenterForceItemAdapter.selectList.get(i))
						&& ControlCenterForceItemAdapter.hasList.contains(map
								.get(TablesColumns.TABLEBLOWER_NO))) {
					lockIds += "" + map.get(TablesColumns.TABLEBLOWER_ID) + ",";
				}
			}
		}
		if (lockIds.length() > 2)
			lockIds = lockIds.substring(0, lockIds.length() - 1);
		lockIds += "]";
		
		String para = "";
		if(type==1)
		{
			para = "blower="+ lockIds + 
			"&max_tem_limit=" + temmax.getText().toString() + 
			"&min_tem_limit=" + temmin.getText().toString() +
			"&max_distance=" + 60 * Integer.parseInt(maxdistance.getText().toString())+
			"&addition_distance=" + 60 * Integer.parseInt(initialdistance.getText().toString()) +
			"&step_run_distance=" + 60 * Integer.parseInt(step_time.getText().toString()) +
			"&step_pause_distance=" + 60 * Integer.parseInt(step_stop.getText().toString());
		}
		else
		{
			para = "blower="+ lockIds + 
					"&max_tem_limit=" + temmax.getText().toString() + 
					"&min_tem_limit=" + temmin.getText().toString() +
					"&max_distance=" + 60 * Integer.parseInt(maxdistance.getText().toString())+
					"&addition_distance=" + 60 * Integer.parseInt(initialdistance.getText().toString()) +
					"&step_run_distance=" + 60 * Integer.parseInt(step_time.getText().toString()) +
					"&step_pause_distance=" + 60 * Integer.parseInt(step_stop.getText().toString()) + 
				    "&distance=0";
		}
		
		dg = MainFragment.createLoadingDialog(this);
		dg.show();
		complete_btn.setClickable(false);
		complete_btn.setText(getResources().getString(R.string.send_cmd));
		NetConnectionCollector.CollectorPutNetConnection(2,"http://m.nnong.com/v2/api-automator/blower/conf/", para, 
				"Token " + MySharePreference.getValueFromKey(this,MySharePreference.ACCOUNTTOKEN),
				new CollectorSuccessCallBack() {
					@Override
					public void onSuccess(HashMap<String, Object> result) {
						complete_btn.setClickable(true);
						complete_btn.setText("执行");
						if(dg!=null)
						{
							dg.dismiss();
						}
						
						if(result== null)
						{
							Toast.makeText(BlowerInfoSettingActivity.this,"指令发送失败", Toast.LENGTH_SHORT).show();
							return;
						}
						
						if(result!=null && result.keySet().contains("status_code") && !result.get("status_code").toString().startsWith("2"))
						{
						
							System.out.println(result.get("status_code").toString());
							DialogManager.showDialogSimple(
									BlowerInfoSettingActivity.this,
									R.string.cmd_send_fail);
							
							return;
						}
						
						if(type == 1 && result.get("status_code").toString().startsWith("2"))
						{
							BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(BlowerInfoSettingActivity.this);
							int idsL = bMaps.size();
							for (int i = 0; i < idsL; i++) {
								for (HashMap<String, String> map : bMaps) {
									if (i<= ControlCenterForceItemAdapter.selectList.size() && map.get(TablesColumns.TABLEBLOWER_NO).equals(
											ControlCenterForceItemAdapter.selectList.get(i))
											&& ControlCenterForceItemAdapter.hasList.contains(map
													.get(TablesColumns.TABLEBLOWER_NO))) {
										HashMap<String, String> bmap = blowerDaoDBManager.getBlowerById(map.get(TablesColumns.TABLEBLOWER_ID));
										bmap.put(TablesColumns.TABLEBLOWER_ADDITION_DISTANCE, Integer.parseInt(initialdistance.getText().toString())*60 + "");
										bmap.put(TablesColumns.TABLEBLOWER_MAX_DISTANCE, Integer.parseInt(maxdistance.getText().toString())* 60 +"");
										bmap.put(TablesColumns.TABLEBLOWER_MAXTEMLIMIT, temmax.getText().toString());
										bmap.put(TablesColumns.TABLEBLOWER_MINTEMLIMIT, temmin.getText().toString());
										bmap.put(TablesColumns.TABLEBLOWER_STEP_PAUSE_DISTANCE,Integer.parseInt(step_stop.getText().toString())*60 + "");
										bmap.put(TablesColumns.TABLEBLOWER_STEP_RUN_DISTANCE, Integer.parseInt(step_time.getText().toString())*60 + "");
										blowerDaoDBManager.updateBlowerById(new HashMap<String, Object>(bmap));
									}
								}
							}
							setResult(30);
							Toast.makeText(BlowerInfoSettingActivity.this, "指令发送成功", Toast.LENGTH_SHORT).show();
							finish();
							return;
						}
						else if(type == 2 && result.get("status_code").toString().startsWith("2"))
						{
							BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(BlowerInfoSettingActivity.this);
							int idsL = bMaps.size();
							for (int i = 0; i < idsL; i++) {
								for (HashMap<String, String> map : bMaps) {
									if (i<= ControlCenterForceItemAdapter.selectList.size() && map.get(TablesColumns.TABLEBLOWER_NO).equals(
											ControlCenterForceItemAdapter.selectList.get(i))
											&& ControlCenterForceItemAdapter.hasList.contains(map
													.get(TablesColumns.TABLEBLOWER_NO))) {
										HashMap<String, String> bmap = blowerDaoDBManager.getBlowerById(map.get(TablesColumns.TABLEBLOWER_ID));
										bmap.put(TablesColumns.TABLEBLOWER_DISTANCE, "0");
										bmap.put(TablesColumns.TABLEBLOWER_ADDITION_DISTANCE, Integer.parseInt(initialdistance.getText().toString())*60 + "");
										bmap.put(TablesColumns.TABLEBLOWER_MAX_DISTANCE, Integer.parseInt(maxdistance.getText().toString())* 60 +"");
										bmap.put(TablesColumns.TABLEBLOWER_MAXTEMLIMIT, temmax.getText().toString());
										bmap.put(TablesColumns.TABLEBLOWER_MINTEMLIMIT, temmin.getText().toString());
										bmap.put(TablesColumns.TABLEBLOWER_STEP_PAUSE_DISTANCE,Integer.parseInt(step_stop.getText().toString())*60 + "");
										bmap.put(TablesColumns.TABLEBLOWER_STEP_RUN_DISTANCE, Integer.parseInt(step_time.getText().toString())*60 + "");
										blowerDaoDBManager.updateBlowerById(new HashMap<String, Object>(bmap));
									}
								}
							}
							setResult(30);
							Toast.makeText(BlowerInfoSettingActivity.this, "指令发送成功", Toast.LENGTH_SHORT).show();
							finish();
							return;
						}
						
					}
				}
				);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tem_min_max_rl:
			updateTemMinMax();
			break;
		case R.id.initialdistancerl:
			intialDistance(1);
			break;
		case R.id.maxdistancerl:
			intialDistance(2);
			break;
		case R.id.step_time_stop_rl:
			setStepTimeStop();
			break;
		case R.id.complete_btn:
			if(typeFresh == 1)
			{
				modifyBlowerInfo(1);
			}else if(typeFresh == 2)
			{
				modifyBlowerInfo(2);
			}
			break;
		case R.id.statefreshrl:
			showDialogBeyondLimit(BlowerInfoSettingActivity.this);
			break;
		default:
			break;
		}
	}
	
	private int typeFresh = 1;
	private Dialog dg;
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
		map.put("name", "状态同步，工作正常");
		soilItems.add(map);
		map = new HashMap<String, String>();
		map.put("name", "强制清零（请在实际风口全关时执行）");
		soilItems.add(map);
		
		final BoxSoilAdapter adapter = new BoxSoilAdapter(context, soilItems, "",3);
		list.setAdapter(adapter);
		if(zero_or_normal.getText().toString().equals("状态同步，工作正常"))
		{
			adapter.selectType = "状态同步，工作正常";
		}else
		{
			adapter.selectType = "强制清零（请在实际风口全关时执行）";
		}
		
		list.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View view,
							int arg2, long arg3) {
						if(((TextView)view.findViewById(R.id.solillistitem_name)).getText().toString().equals("状态同步，工作正常"))
						{
							typeFresh = 1;
							adapter.selectType = "状态同步，工作正常";
						}
						else if(((TextView)view.findViewById(R.id.solillistitem_name)).getText().toString().equals("强制清零（请在实际风口全关时执行）"))
						{
							typeFresh =2;
							adapter.selectType = "强制清零（请在实际风口全关时执行）";
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
				dlg.dismiss();
				if(typeFresh==1)
				{
					zero_or_normal.setText("状态同步，工作正常");
					//获取中控下所有放风机
//					NetConnectionFarming.FarmingGetAllNetConnection(NetUrls.getAllBlowerFromControl(ccId), null, null, 
//							new FarmingSuccessCallBackGetAll() {
//								@Override
//								public void onSuccess(List<HashMap<String, Object>> result1) {
//									if(result1!=null && result1.size()>0)
//									{
//										BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(context);
//										for(HashMap<String, Object> map : result1)
//										{
//											HashMap<String, String> bMap = blowerDaoDBManager.getBlowerById(map.get("id").toString());
//											if(bMap!=null && bMap.size()>2)
//											{
//												blowerDaoDBManager.updateBlowerById(map);
//											}
//											else
//											{
//												List<HashMap<String, Object>> res = new ArrayList<HashMap<String,Object>>();
//												res.add(map);
//												blowerDaoDBManager.insertAllControlCenter(res );
//											}
//										}
//										Toast.makeText(context, "同步成功", Toast.LENGTH_SHORT).show();
//										
//									}
//									if(dg!=null) dg.dismiss();
//									if(result1==null)
//									{
//										Toast.makeText(context, "同步失败", Toast.LENGTH_SHORT).show();
//									}
//									
//								}
//							}
//							);
				}
				else if(typeFresh == 2)
				{
					zero_or_normal.setText("强制清零");

				}
				
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
	
	
	
	private void setStepTimeStop()
	{
		final AlertDialog dlg = new AlertDialog.Builder(this).create();
		dlg.show();
		Window window = dlg.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,xml文件中定义view内容
		// window.setContentView(R.layout.dialog_beingmarturing_modify_alarming);
		window.setContentView(R.layout.dialog_being_modify_alarming);
		TextView dialogHead = (TextView) window.findViewById(R.id.dialog_head);
		dialogHead.setText("单次步长");
		TextView alarmingto_et_unit = (TextView) window
				.findViewById(R.id.alarmingto_et_unit);
		alarmingto_et_unit.setText("分钟");
		
		TextView setp_time_text = (TextView) window.findViewById(R.id.setp_time_text);
		setp_time_text.setText("工作");
		
		((TextView)window.findViewById(R.id.dialog_content)).setText("分钟,暂停");
		final NumberPicker settingFrom = (NumberPicker) window
				.findViewById(R.id.alarmingfrom_et);
		final NumberPicker settingTo = (NumberPicker) window
				.findViewById(R.id.alarmingto_et);

		settingFrom.setMinValue(1);
		settingFrom.setMaxValue(10);
		
		try {
			settingFrom.setValue(Integer.parseInt(step_time.getText().toString()
					.trim()));
		} catch (NumberFormatException e) {
			settingFrom.setValue(2);
		}
		
		
		settingTo.setMinValue(1);
		settingTo.setMaxValue(10);
		
		try {
			settingTo.setValue(Integer.parseInt(step_stop.getText().toString()
					.trim()));
		} catch (NumberFormatException e) {
			settingTo.setValue(2);
		}
		
		Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
					dlg.dismiss();
					step_time.setText(settingFrom.getValue()+"");
					step_stop.setText(settingTo.getValue()+"");
			}
		});
		Button cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
	}
	
	private void intialDistance(final int type)
	{
			final AlertDialog dlg = new AlertDialog.Builder(this).create();
			dlg.show();
			Window window = dlg.getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
			// *** 主要就是在这里实现这种效果的.
			// 设置窗口的内容页面,xml文件中定义view内容
		//	window.setContentView(R.layout.dialog_beingmarturing_modify_alarming);
			window.setContentView(R.layout.dialog_being_modify_alarming);
			TextView dialogHead = (TextView) window.findViewById(R.id.dialog_head);
			if(type == 1)
			{
				dialogHead.setText("风口膜初始行程");
			}else if(type == 2)
			{
				dialogHead.setText("风口膜最大行程");
			}
		    
		    TextView alarmingto_et_unit = (TextView) window.findViewById(R.id.alarmingto_et_unit);
		    alarmingto_et_unit.setText("分钟");
			window.findViewById(R.id.dialog_content).setVisibility(View.GONE);
			window.findViewById(R.id.alarmingfrom_et).setVisibility(View.GONE);;
			final NumberPicker settingTo =  (NumberPicker) window.findViewById(R.id.alarmingto_et);
			
			if(type == 1)
			{
				settingTo.setMinValue(0);
				settingTo.setMaxValue(60);
				try {
					settingTo.setValue(Integer.parseInt(initialdistance.getText().toString()
							.trim()));
				} catch (NumberFormatException e) {
					settingTo.setValue(2);
				}
				
			}else if(type == 2)
			{
				settingTo.setMinValue(0);
				settingTo.setMaxValue(120);
				try {
					settingTo.setValue(Integer.parseInt(maxdistance.getText().toString()
							.trim()));
				} catch (NumberFormatException e) {
					settingTo.setValue(10);
				}
			}
			
			
			Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
			ok.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					dlg.dismiss();
					if(type == 1)
					{
						initialdistance.setText(settingTo.getValue()+"");
					}
					else if(type == 2)
					{
						maxdistance.setText(settingTo.getValue()+"");
					}
				}
			});
			Button cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
			cancel.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					dlg.dismiss();
				}
			});
	}
	
	

	private void updateTemMinMax() {
		final AlertDialog dlg = new AlertDialog.Builder(this).create();
		dlg.show();
		Window window = dlg.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,xml文件中定义view内容
		// window.setContentView(R.layout.dialog_beingmarturing_modify_alarming);
		window.setContentView(R.layout.dialog_being_modify_alarming);
		TextView dialogHead = (TextView) window.findViewById(R.id.dialog_head);
		dialogHead.setText("温度上下限");
		TextView alarmingto_et_unit = (TextView) window
				.findViewById(R.id.alarmingto_et_unit);
		alarmingto_et_unit.setText(getResources().getString(
				R.string.farming_from_to_temunit));
		
		final NumberPicker settingFrom = (NumberPicker) window
				.findViewById(R.id.alarmingfrom_et);
		final NumberPicker settingTo = (NumberPicker) window
				.findViewById(R.id.alarmingto_et);

		settingFrom.setMinValue(0);
		settingFrom.setMaxValue(50);
		settingTo.setMinValue(0);
		settingTo.setMaxValue(50);
		try {
			settingFrom.setValue(Integer.parseInt(temmin.getText().toString()
					.trim()));
		} catch (NumberFormatException e) {
			settingFrom.setValue(30);
		}
		try {
			settingTo.setValue(Integer.parseInt(temmax.getText().toString()
					.trim()));
		} catch (NumberFormatException e) {
			settingTo.setValue(30);
		}

		Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (checkSettingNum(settingFrom.getValue()+"",
						settingTo.getValue()+"")) {
					dlg.dismiss();
					temmin.setText(settingFrom.getValue()+"");
					temmax.setText(settingTo.getValue()+"");
				} else {
					DialogManager.showDialogSimple(
							BlowerInfoSettingActivity.this,
							R.string.farmingAlarm_setting_num_error);
				}
			}
		});
		Button cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
	}

	private boolean checkSettingNum(String num1, String num2) {
		if (!StringUtil.checkNumber(num1) || !StringUtil.checkNumber(num2)) {
			return false;
		}
		int numFrom = Integer.parseInt(num1);
		int numTo = Integer.parseInt(num2);
		if (numTo <= numFrom) {
			return false;
		} else {
			return true;
		}
	}
}
