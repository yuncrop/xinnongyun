package com.xinnongyun.activity;

import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.net.NetConnectionFarm;
import com.xinnongyun.net.NetConnectionFarm.FarmSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.utils.DialogManager;
import com.xinnongyun.utils.StringUtil;

/**
 * 农场信息修改
 * @author sm
 *
 */
public class FarmConfigChangeActivity extends Activity implements OnClickListener {

	private ImageView farmConfigChangeReturn;
	private EditText farmname_et,farmChangeIntroduce;
	private TextView save_tv,FarmChangenameHint,farmChangeHead;
	/**
	 * 1为name 2为地址 3为简介
	 */
	private int type = 0;
	private RelativeLayout farmIntroduce_rl,farmName_address_rl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏ActionBar
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//		         WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		setContentView(R.layout.acticity_farmconfig_change);
		farmConfigChangeReturn = (ImageView) findViewById(R.id.farmConfigChangeReturn);
		farmConfigChangeReturn.setOnClickListener(this);
		farmname_et = (EditText) findViewById(R.id.farmname_et);
		farmChangeHead = (TextView) findViewById(R.id.farmChangeHead);
		FarmChangenameHint= (TextView) findViewById(R.id.FarmChangenameHint);
		farmChangeIntroduce = (EditText) findViewById(R.id.farmconfig_change_introduce);
		farmIntroduce_rl = (RelativeLayout) findViewById(R.id.farmIntroduce_rl);
		farmName_address_rl = (RelativeLayout) findViewById(R.id.farmName_address_rl);
		save_tv = (TextView) findViewById(R.id.save_tv);
		save_tv.setOnClickListener(this);
		Intent intent = getIntent();
		if(intent!=null)
		{
			type = intent.getIntExtra("type", 0);
		}
		if(type!=1)
		{
			FarmChangenameHint.setVisibility(View.INVISIBLE);
		}
		
		if(type == 1)
		{
			farmIntroduce_rl.setVisibility(View.GONE);
			if(!MySharePreference.getValueFromKey(FarmConfigChangeActivity.this, MySharePreference.FARMNAME).equals(MySharePreference.NOTSTATE))
			{
				farmname_et.setText(MySharePreference.getValueFromKey(FarmConfigChangeActivity.this, MySharePreference.FARMNAME));
			}
		}
		
		if(type == 2)
		{
			farmChangeHead.setText(R.string.farmconfig_changefarm_address_head);
			farmIntroduce_rl.setVisibility(View.GONE);
			if(!MySharePreference.getValueFromKey(FarmConfigChangeActivity.this, MySharePreference.FARMADDRESS).equals(MySharePreference.NOTSTATE))
			{
				farmname_et.setText(MySharePreference.getValueFromKey(FarmConfigChangeActivity.this, MySharePreference.FARMADDRESS));
			}
			
		}
		if(type == 3)
		{
			farmName_address_rl.setVisibility(View.GONE);
			farmChangeHead.setText(R.string.farmconfig_newfarm_farmIntroduce);
			if(!MySharePreference.getValueFromKey(FarmConfigChangeActivity.this, MySharePreference.FARMINTRODUCE).equals(MySharePreference.NOTSTATE))
			{
				farmChangeIntroduce.setText(MySharePreference.getValueFromKey(FarmConfigChangeActivity.this, MySharePreference.FARMINTRODUCE));
			}
		}
	}
	
	
	
	private Dialog dg;
	
	/**
	 * 修改信息
	 * @param para
	 */
	private void changeFarmInfo(String para)
	{
		dg = MainFragment.createLoadingDialog(this);
		dg.show();
		NetConnectionFarm.FarmPutNetConnection(
				NetUrls.URL_MODIFY_FARM(MySharePreference
						.getValueFromKey(FarmConfigChangeActivity.this,
								MySharePreference.FARMID)),
				para,
				"Token "
						+ MySharePreference.getValueFromKey(
								FarmConfigChangeActivity.this,
								MySharePreference.ACCOUNTTOKEN),
				new FarmSuccessCallBack() {
					@Override
					public void onSuccess(HashMap<String, Object> map) {
						dg.dismiss();
						if (map != null) {
							if (map.keySet().contains("status_code")) {
								ErrorMarkToast.showCallBackToast(
										FarmConfigChangeActivity.this, map);
								return;
							}
							// TODO保存农场信息到本地
							MySharePreference.saveFarmInfo(
									FarmConfigChangeActivity.this, map);
							
							if(type == 1)
							{
								Intent intent2 = new Intent();
								intent2.setAction(MainActivity.farmConfigbroastCast);
								sendBroadcast(intent2);
							}
							finish();
						} else {
							showToast(R.string.interneterror);
						}
					}
				});
	}
	
	
	/**
	 * 农场名
	 * @return
	 */
	private String initialURLparaName() {
		String para = "name=" + farmname_et.getText().toString().trim();
		return para;
	}
	
	/**
	 * 地址
	 * @return
	 */
	private String initialURLparaAddress()
	{
		String para = "name=" + MySharePreference.getValueFromKey(FarmConfigChangeActivity.this, MySharePreference.FARMNAME);
		para += "&location=" + farmname_et.getText().toString().trim();
		return para;
	}
	
	/**
	 * 简介
	 * @return
	 */
	private String initialURLparaIntroduce()
	{
		String para = "name=" + MySharePreference.getValueFromKey(FarmConfigChangeActivity.this, MySharePreference.FARMNAME);
		para += "&description=" + farmChangeIntroduce.getText().toString();
		return para;
	}
	
	private void showToast(int id) {
		Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
	}





	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.save_tv:
			if(type == 1)
			{
				if(!StringUtil.checkEditTextIsEmpty(farmname_et) && StringUtil.checkNickName(farmname_et.getText().toString().trim()))
				{
					changeFarmInfo(initialURLparaName());
				}else
				{
					DialogManager.showDialogSimple(FarmConfigChangeActivity.this,
							//R.string.farmconfig_newfarm_name_change,
							R.string.farmconfig_newfarm_name_change_error);
					return;
				}
			}else if(type == 2)
			{
				changeFarmInfo(initialURLparaAddress());
			}else if(type == 3)
			{
				if(!StringUtil.checkEditTextIsEmpty(farmChangeIntroduce) && StringUtil.checkFarmIntroduce(farmChangeIntroduce.getText().toString().trim()))
				{
					changeFarmInfo(initialURLparaIntroduce());
				}
				else
				{
					DialogManager.showDialogSimple(FarmConfigChangeActivity.this,
							//R.string.farmconfig_newfarm_farmIntroduce,
							R.string.farmconfig_newfarm_farmIntroduce_error);
					return;
				}
			}
			break;
		case R.id.farmConfigChangeReturn:
			finish();
			break;
		default:
			break;
		}
	}
}
