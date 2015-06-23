package com.xinnongyun.activity;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import net.simonvt.numberpicker.NumberPicker;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.net.NetConnectionFarming;
import com.xinnongyun.net.NetConnectionNotice;
import com.xinnongyun.net.NetConnectionFarming.FarmingSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.BeingDaoDBManager;
import com.xinnongyun.sqlite.FarmingDaoDBManager;
import com.xinnongyun.sqlite.NoticeDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.utils.DialogManager;
import com.xinnongyun.utils.StringUtil;

/**
 * 种养记录报警设置
 * @author sm
 *
 */
public class FarmingAlarmingSettingActivity extends Activity implements OnClickListener {

	private TextView farmingBeingName,farmingBeingTemFrom,farmingBeingTemTo,farmingBeingHumFrom,farmingBeingHumTo,farmingSoilHumFrom,farmingSoilHumTo;
	private Button alarmingDefault;
	private String farmingId;
	private FarmingDaoDBManager farmingDao;
	private BeingDaoDBManager beingDao;
	private LinearLayout soilhumll,temll,humll;
	private ImageView returnBack;
	private HashMap<String, String> farmingMap;
	private HashMap<String, String> beingMap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏ActionBar
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//		         WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		setContentView(R.layout.activity_being_detail);
		Intent intent= getIntent();
		
		farmingId = intent.getStringExtra(TablesColumns.TABLEFARMING_ID);
		
		
		int notifyid = intent.getIntExtra("notify_id", 0);
		if(notifyid!=0)
		{
			NotificationManager manger = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
			manger.cancel(notifyid);
		}
		String notice_id = intent.getStringExtra(TablesColumns.TABLENOTICE_ID+"notice");
		if(notice_id!=null && notice_id.length()>0)
		{
			NoticeDaoDBManager daoDBManager = new NoticeDaoDBManager(FarmingAlarmingSettingActivity.this);
			SimpleDateFormat sDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());
			NetConnectionNotice.NoticePutNetConnection(
					NetUrls.getUpdateNotice(notice_id)
					,"read_at=" + date.replace(" ", "T")
					,"Token " + MySharePreference.getValueFromKey(FarmingAlarmingSettingActivity.this,MySharePreference.ACCOUNTTOKEN), null);
			daoDBManager.changeFarmingBeingCollects(notice_id, date.replace(" ", "T"));
		}
		
		intialView();
	}

	private void intialView() {
		returnBack = (ImageView) findViewById(R.id.farmingAlarm_return);
		returnBack.setOnClickListener(this);
		soilhumll = (LinearLayout) findViewById(R.id.farmingbeingalarming_soilhumll);
		soilhumll.setOnClickListener(this);
		temll = (LinearLayout) findViewById(R.id.farmingbeingalarming_temll);
		temll.setOnClickListener(this);
		humll = (LinearLayout) findViewById(R.id.farmingbeingalarming_humll);
		humll.setOnClickListener(this);
		farmingBeingName = (TextView) findViewById(R.id.farmingAlarm_settingsbeingname);
		alarmingDefault = (Button) findViewById(R.id.farmingAlarm_setting_default_btn);
		alarmingDefault.setOnClickListener(this);
		farmingBeingTemFrom = (TextView) findViewById(R.id.farmingtemfrom);
		farmingBeingTemTo = (TextView) findViewById(R.id.farmingtemto);
		farmingBeingHumFrom = (TextView) findViewById(R.id.farminghumfrom);
		farmingBeingHumTo = (TextView) findViewById(R.id.farminghumto);
		farmingSoilHumFrom = (TextView) findViewById(R.id.farmingsoilhumfrom);
		farmingSoilHumTo = (TextView) findViewById(R.id.farmingsoilhumto);
		farmingDao = new FarmingDaoDBManager(FarmingAlarmingSettingActivity.this);
		farmingMap = farmingDao.getFarmingInfoById(farmingId);
		if(farmingMap==null || farmingMap.keySet().size()==0)
		{
			setResult(250);
			finish();
			return;
		}
		beingDao = new BeingDaoDBManager(FarmingAlarmingSettingActivity.this);
		beingMap = beingDao.getBeingById(farmingMap.get(TablesColumns.TABLEFARMING_BEING));
		intialData();
	}

	private void intialData()
	{
		if(farmingMap.get(TablesColumns.TABLEFARMING_MATURINGAT).equals("null"))
		{
			farmingBeingName.setText(beingMap.get(TablesColumns.TABLEBEING_NAME));
		}
		else
		{
			farmingBeingName.setText(beingMap.get(TablesColumns.TABLEBEING_NAME) + "("+ StringUtil.getMouthDay(farmingMap.get(TablesColumns.TABLEFARMING_MATURINGAT)) +"成熟)");
		}
		FarmingDaoDBManager farmingDao = new FarmingDaoDBManager(FarmingAlarmingSettingActivity.this);
		HashMap<String, String> farmingMap = farmingDao.getFarmingInfoById(farmingId);
		String beingId = farmingMap.get(TablesColumns.TABLEFARMING_BEING).trim();
		HashMap<String, String> beingMap = beingDao.getBeingById(beingId);
		try {
			farmingBeingTemFrom.setText((int)Double.parseDouble(farmingMap.get(TablesColumns.TABLEFARMING_MINTEM).equals("null")? (beingMap.get(TablesColumns.TABLEBEING_MINTEM).equals("null")? YunTongXun.minTemDefault+"" : beingMap.get(TablesColumns.TABLEBEING_MINTEM)) : farmingMap.get(TablesColumns.TABLEFARMING_MINTEM))+"");
			farmingBeingTemTo.setText((int)Double.parseDouble(farmingMap.get(TablesColumns.TABLEFARMING_MAXTEM).equals("null")? (beingMap.get(TablesColumns.TABLEBEING_MAXTEM).equals("null")? YunTongXun.maxTemDefault+"" : beingMap.get(TablesColumns.TABLEBEING_MAXTEM)) : farmingMap.get(TablesColumns.TABLEFARMING_MAXTEM))+"");
			farmingBeingHumFrom.setText((int)Double.parseDouble(farmingMap.get(TablesColumns.TABLEFARMING_MINHUM).equals("null")? (beingMap.get(TablesColumns.TABLEBEING_MINHUM).equals("null")? YunTongXun.minHumDefault+"" : beingMap.get(TablesColumns.TABLEBEING_MINHUM)): farmingMap.get(TablesColumns.TABLEFARMING_MINHUM))+"");
			farmingBeingHumTo.setText((int)Double.parseDouble(farmingMap.get(TablesColumns.TABLEFARMING_MAXHUM).equals("null")? (beingMap.get(TablesColumns.TABLEBEING_MAXHUM).equals("null")? YunTongXun.maxHumDefault+"" : beingMap.get(TablesColumns.TABLEBEING_MAXHUM)) : farmingMap.get(TablesColumns.TABLEFARMING_MAXHUM))+"");
			farmingSoilHumFrom.setText((int)Double.parseDouble(farmingMap.get(TablesColumns.TABLEFARMING_MINSOILHUM).equals("null")? (beingMap.get(TablesColumns.TABLEBEING_MINSOILHUM).equals("null")? YunTongXun.minSoilHumDefault+"" : beingMap.get(TablesColumns.TABLEBEING_MINSOILHUM)) : farmingMap.get(TablesColumns.TABLEFARMING_MINSOILHUM))+"");
			farmingSoilHumTo.setText((int)Double.parseDouble(farmingMap.get(TablesColumns.TABLEFARMING_MAXSOILHUM).equals("null")? (beingMap.get(TablesColumns.TABLEBEING_MAXSOILHUM).equals("null")? YunTongXun.maxSoilHumDefault+"" : beingMap.get(TablesColumns.TABLEBEING_MAXSOILHUM)): farmingMap.get(TablesColumns.TABLEFARMING_MAXSOILHUM))+"");
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.farmingAlarm_setting_default_btn:
			updateAlarmingDedault(intialAlarmingDefaultParas());
			break;
		case R.id.farmingbeingalarming_soilhumll:
			updateFarmingAlarming(3);
			break;
		case R.id.farmingbeingalarming_humll:
			updateFarmingAlarming(2);
	    	break;
		case R.id.farmingbeingalarming_temll:
			updateFarmingAlarming(1);
			break;
		case R.id.farmingAlarm_return:
			finish();
			break;
		default:
			break;
		}
	}
	
	
	private boolean checkSettingNum(String num1,String num2)
	{
		if(!StringUtil.checkNumber(num1) || !StringUtil.checkNumber(num2))
		{
			return false;
		}
		int numFrom = Integer.parseInt(num1);
		int numTo = Integer.parseInt(num2);
		if(numTo <= numFrom)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	
	
	//修改范围  1为温度   2为湿度   3为土湿
	private void updateFarmingAlarming(final int type)
	{
		final AlertDialog dlg = new AlertDialog.Builder(FarmingAlarmingSettingActivity.this).create();
		dlg.show();
		Window window = dlg.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
	//	window.setContentView(R.layout.dialog_beingmarturing_modify_alarming);
		window.setContentView(R.layout.dialog_being_modify_alarming);
		TextView dialogHead = (TextView) window.findViewById(R.id.dialog_head);
		
		final String fromValues[] = new String[201];
		final String toValues[] = new String[201];
		
		
		final NumberPicker settingFrom =  (NumberPicker) window.findViewById(R.id.alarmingfrom_et);
		final NumberPicker settingTo =  (NumberPicker) window.findViewById(R.id.alarmingto_et);
		
		
		
		if(type == 1)
		{
			for(int i = -30;i<=170;i++)
			{
				fromValues[i+30] = i+"";
				toValues[i+30] = i+"";
			}
			settingFrom.setDisplayedValues(fromValues);
			settingTo.setDisplayedValues(fromValues);
			settingFrom.setMinValue(0);
			settingFrom.setMaxValue(80);
			settingTo.setMinValue(0);
			settingTo.setMaxValue(80);
			settingFrom.setValue(Integer.parseInt(farmingBeingTemFrom.getText().toString().trim())+30);
			settingTo.setValue(Integer.parseInt(farmingBeingTemTo.getText().toString().trim())+30);
		}else if(type == 2)
		{
			for(int i = 0;i<=200;i++)
			{
				fromValues[i] = i+"";
				toValues[i] = i+"";
			}
			settingFrom.setDisplayedValues(fromValues);
			settingTo.setDisplayedValues(fromValues);
			settingFrom.setMinValue(0);
			settingFrom.setMaxValue(100);
			settingTo.setMinValue(0);
			settingTo.setMaxValue(100);
			settingFrom.setValue(Integer.parseInt(farmingBeingHumFrom.getText().toString().trim()));
			settingTo.setValue(Integer.parseInt(farmingBeingHumTo.getText().toString().trim()));
		}else if(type == 3)
		{
			for(int i = 0;i<=200;i++)
			{
				fromValues[i] = i+"";
				toValues[i] = i+"";
			}
			settingFrom.setDisplayedValues(fromValues);
			settingTo.setDisplayedValues(fromValues);
			settingFrom.setMinValue(0);
			settingFrom.setMaxValue(150);
			settingTo.setMinValue(0);
			settingTo.setMaxValue(150);
			settingFrom.setValue(Integer.parseInt(farmingSoilHumFrom.getText().toString().trim()));
			settingTo.setValue(Integer.parseInt(farmingSoilHumTo.getText().toString().trim()));
		}
		
		
		if(type == 1)
		{
			((TextView)window.findViewById(R.id.alarmingto_et_unit)).setText(R.string.farming_from_to_temunit);
			dialogHead.setText(R.string.farmingAlarm_setting_tem_tv_show);
		}else if(type == 2)
		{
			dialogHead.setText(R.string.farmingAlarm_setting_hum_tv_show);
		}else if(type == 3)
		{
			dialogHead.setText(R.string.farmingAlarm_setting_soilhum_tv_show);
		}
		Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
					if(checkSettingNum(fromValues[settingFrom.getValue()],toValues[settingTo.getValue()]))
					{
						String paras = "";
						if(type == 1)
						{
							paras = initialAlarmingParasTem(fromValues[settingFrom.getValue()],toValues[settingTo.getValue()]);
						}
						else if(type == 2)
						{
							paras = initialAlarmingParasHum(fromValues[settingFrom.getValue()],toValues[settingTo.getValue()]);
						}
						else if(type ==3)
						{
							paras = initialAlarmingParasSoilHum(fromValues[settingFrom.getValue()],toValues[settingTo.getValue()]);
						}
						
						dg = MainFragment.createLoadingDialog(FarmingAlarmingSettingActivity.this);
						dg.show();
						NetConnectionFarming.FarmingPutNetConnection(NetUrls.URL_FARMING_MODIFY(farmingId),
								paras,
								"Token "+MySharePreference.getValueFromKey(FarmingAlarmingSettingActivity.this, MySharePreference.ACCOUNTTOKEN),
								new FarmingSuccessCallBack() {
									@Override
									public void onSuccess(HashMap<String, Object> result) {
										dg.dismiss();
										if(result!=null)
										{
											if(result.keySet().contains("status_code"))
											{
												ErrorMarkToast.showCallBackToast(FarmingAlarmingSettingActivity.this, result);
												return;
											}
											farmingDao.updateFarmingById(result);
											farmingMap = farmingDao.getFarmingInfoById(farmingId);
											intialData();
											dlg.dismiss();
										}
										else
										{
											showToast(R.string.interneterror);
										}
									}
								});
						
					}
					else
					{
						if(type == 1)
						{
							DialogManager.showDialogSimple(FarmingAlarmingSettingActivity.this,
									//R.string.farmingAlarm_setting_tem_tv_show, 
									R.string.farmingAlarm_setting_num_error);
						}else if(type == 2)
						{
							DialogManager.showDialogSimple(FarmingAlarmingSettingActivity.this,
									//R.string.farmingAlarm_setting_hum_tv_show,
									R.string.farmingAlarm_setting_num_error);
						}else if(type == 3)
						{
							DialogManager.showDialogSimple(FarmingAlarmingSettingActivity.this,
									//R.string.farmingAlarm_setting_soilhum_tv_show,
									R.string.farmingAlarm_setting_num_error);
						}
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
	
	
	private String initialAlarmingParasTem(String fromMin,String toMax)
	{
		String paras = initialAlarmingParas();
		paras += "&" + TablesColumns.TABLEFARMING_MINTEM + "=" + fromMin;
		paras += "&" + TablesColumns.TABLEFARMING_MAXTEM + "=" + toMax;
		return paras;
	}
	
	private String initialAlarmingParasHum(String fromMin,String toMax)
	{
		String paras = initialAlarmingParas();
		paras += "&" + TablesColumns.TABLEFARMING_MAXHUM + "=" + toMax;
		paras += "&" + TablesColumns.TABLEFARMING_MINHUM + "=" + fromMin;
		return paras;
	}
	
	private String initialAlarmingParasSoilHum(String fromMin,String toMax)
	{
		String paras = initialAlarmingParas();
		paras += "&" + TablesColumns.TABLEFARMING_MINSOILHUM + "=" + fromMin;
		paras += "&" + TablesColumns.TABLEFARMING_MAXSOILHUM + "=" + toMax;
		return paras;
	}
	
	private Dialog dg;
	
	//更新设置
	private void updateAlarmingDedault(String paras)
	{
		dg = MainFragment.createLoadingDialog(this);
		dg.show();
		NetConnectionFarming.FarmingPutNetConnection(NetUrls.URL_FARMING_MODIFY(farmingId),
				paras,
				"Token "+MySharePreference.getValueFromKey(FarmingAlarmingSettingActivity.this, MySharePreference.ACCOUNTTOKEN),
				new FarmingSuccessCallBack() {
					@Override
					public void onSuccess(HashMap<String, Object> result) {
						dg.dismiss();
						if(result!=null)
						{
							if(result.keySet().contains("status_code"))
							{
								ErrorMarkToast.showCallBackToast(FarmingAlarmingSettingActivity.this, result);
								return;
							}
							farmingDao.updateFarmingById(result);
							DialogManager.showDialogSimple(FarmingAlarmingSettingActivity.this,
									//R.string.farming_maturingat_modify_success,
									R.string.farming_maturingat_modify_success_defaule);
							farmingMap = farmingDao.getFarmingInfoById(farmingId);
							intialData();
						}
						else
						{
							showToast(R.string.interneterror);
						}
					}
				});
	}
	
	
	//默认参数
	private String intialAlarmingDefaultParas()
	{
		String paras = initialAlarmingParas();
		
		FarmingDaoDBManager farmingDao = new FarmingDaoDBManager(FarmingAlarmingSettingActivity.this);
		HashMap<String, String> farmingMap = farmingDao.getFarmingInfoById(farmingId);
		String beingId = farmingMap.get(TablesColumns.TABLEFARMING_BEING).trim();
		HashMap<String, String> beingMap = beingDao.getBeingById(beingId);
		
		paras += "&" + TablesColumns.TABLEFARMING_MINTEM + "=" + 
				(beingMap.get(TablesColumns.TABLEBEING_MINTEM).equals("null")? YunTongXun.minTemDefault+"" : beingMap.get(TablesColumns.TABLEBEING_MINTEM));
		paras += "&" + TablesColumns.TABLEFARMING_MAXTEM + "=" +
				(beingMap.get(TablesColumns.TABLEBEING_MAXTEM).equals("null")? YunTongXun.maxTemDefault+"" : beingMap.get(TablesColumns.TABLEBEING_MAXTEM));
		paras += "&" + TablesColumns.TABLEFARMING_MAXHUM + "=" +
				(beingMap.get(TablesColumns.TABLEBEING_MAXHUM).equals("null")? YunTongXun.maxHumDefault+"" : beingMap.get(TablesColumns.TABLEBEING_MAXHUM));
		paras += "&" + TablesColumns.TABLEFARMING_MINHUM + "=" +
				(beingMap.get(TablesColumns.TABLEBEING_MINHUM).equals("null")? YunTongXun.minHumDefault+"" : beingMap.get(TablesColumns.TABLEBEING_MINHUM));
		paras += "&" + TablesColumns.TABLEFARMING_MINSOILHUM + "=" +
				(beingMap.get(TablesColumns.TABLEBEING_MINSOILHUM).equals("null")? YunTongXun.minSoilHumDefault+"" : beingMap.get(TablesColumns.TABLEBEING_MINSOILHUM));
		paras += "&" + TablesColumns.TABLEFARMING_MAXSOILHUM + "=" +
				(beingMap.get(TablesColumns.TABLEBEING_MAXSOILHUM).equals("null")? YunTongXun.maxSoilHumDefault+"" : beingMap.get(TablesColumns.TABLEBEING_MAXSOILHUM));
		return paras;
	}
	
	
	//初始化参数
	private String  initialAlarmingParas()
	{
		FarmingDaoDBManager farmingDao = new FarmingDaoDBManager(FarmingAlarmingSettingActivity.this);
		HashMap<String, String> farmingMap = farmingDao.getFarmingInfoById(farmingId);
		String collectsInfo = farmingMap.get(TablesColumns.TABLEFARMING_COLLECTORS).substring(1, farmingMap.get(TablesColumns.TABLEFARMING_COLLECTORS).length()-1);
		String collect[] = collectsInfo.split(",");
		String paras = TablesColumns.TABLEFARMING_BEING + "=" + farmingMap.get(TablesColumns.TABLEFARMING_BEING);
		for(String collceitem : collect)
		{
			paras += "&" + TablesColumns.TABLEFARMING_COLLECTORS + "=" + collceitem;
		}
		paras += "&" + TablesColumns.TABLEFARMING_FARM + "=" + farmingMap.get(TablesColumns.TABLEFARMING_FARM);
		return paras;
	}
	
	private void showToast(int id) {
		Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
	}

}
