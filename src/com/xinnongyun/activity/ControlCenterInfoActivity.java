package com.xinnongyun.activity;

import java.util.HashMap;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.net.NetConnectionWeatherStation;
import com.xinnongyun.net.NetConnectionWeatherStation.WeatherSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.ControlCenterDaoDBManager;
import com.xinnongyun.utils.ImageUtils;

/**
 * 环境小站信息设置
 * 
 * @author sm
 * 
 */
public class ControlCenterInfoActivity extends Activity{

	private String id = "";
	private String serial = "";
	private String name = "";
	private String type = "1";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.robot_controlcenter_info);
		Intent intent = getIntent();
		id = intent.getStringExtra("id");
		name = intent.getStringExtra("name");
		serial = intent.getStringExtra("serial");
		type = intent.getStringExtra("type");
		initialView();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 10)
		{
			setResult(10);
			finish();
		}
	}
	
	
	PopupWindow popWindow;
	@SuppressWarnings("deprecation")
	public void btn_PopupMenu(View view) {
		View popContent = LayoutInflater.from(view.getContext()).inflate(R.layout.pop_delete, null);
		popWindow = new PopupWindow(popContent,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);
		popWindow.showAsDropDown(view);
		LinearLayout popll = (LinearLayout) popContent.findViewById(R.id.popdelet_ll);
		popll.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						showDialogDelete(ControlCenterInfoActivity.this);
						popWindow.dismiss();
					}
				});
		
	}
	
	private Dialog dg;
	private void showDialogDelete(Context context)
	{
		final AlertDialog dlg = new AlertDialog.Builder(context).create();
		dlg.setCancelable(false);
		dlg.show();
		Window window = dlg.getWindow();
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.tishi_show_dialog_twobtn);
		((TextView) window.findViewById(R.id.dialog_head))
				.setText(R.string.pop_delete);
		((TextView) window.findViewById(R.id.dialog_content))
				.setText(R.string.pop_delete_collect);
		// 为确认按钮添加事件,执行退出应用操作
		Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
		
		ok.setTextColor(getResources().getColor(R.color.text_fe462e));
		ok.setText(R.string.pop_delete);
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dg = MainFragment.createLoadingDialog(ControlCenterInfoActivity.this);
				dg.show();
				NetConnectionWeatherStation.WeatherDeleteNetConnection("http://m.nnong.com/v2/api-automator/cc/" + id + "/unmount/", null, 
						"Token "+ MySharePreference.getValueFromKey(ControlCenterInfoActivity.this,MySharePreference.ACCOUNTTOKEN), 
						new WeatherSuccessCallBack() {
							@Override
							public void onSuccess(HashMap<String, Object> result) {
								dg.dismiss();
								dlg.dismiss();
								if(result!=null)
								{
									if(result.get("status_code").toString().equals("200"))
									{
										ControlCenterDaoDBManager collDao = new ControlCenterDaoDBManager(ControlCenterInfoActivity.this);
										collDao.deleteControlCenterById(id);
										setResult(20);
										finish();
									}
									else
									{
										ErrorMarkToast.showCallBackToast(ControlCenterInfoActivity.this, result);
									}
								}else
								{
									showToast(R.string.interneterror);
								}
								
							}
						}
						);
				
			}
		});
		Button cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
	}
	
	private void showToast(int id)
	{
		Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
	}
	
	@SuppressWarnings("deprecation")
	private void initialView()
	{
		((TextView)findViewById(R.id.weatherStationName)).setText("机器人设置");
		findViewById(R.id.collectReturn).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							finish();
						}
					}
					);
			findViewById(R.id.popdelete_iv).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							btn_PopupMenu(v);
						}
					}
					);
			findViewById(R.id.box_changename_ll).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent();
							intent.putExtra("id", id);
							intent.putExtra("name", name);
							intent.setClass(ControlCenterInfoActivity.this, ControlCenterNameModifyActivity.class);
							startActivityForResult(intent, 10);
						}
					}
					);
			((TextView)findViewById(R.id.collect_name)).setText(name);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				((ImageView)findViewById(R.id.collect_image)).setBackground(new BitmapDrawable(ImageUtils
						.Create2DCode(NetUrls.URL+"/" +serial)));
			} else {
				((ImageView)findViewById(R.id.collect_image)).setBackgroundDrawable(new BitmapDrawable(ImageUtils
						.Create2DCode(NetUrls.URL+"/" +serial)));
			}
			((TextView)findViewById(R.id.weather_station_serial)).setText(serial);
		
			if(type!=null && type.equals("2"))
			{
				findViewById(R.id.complete_btn).setVisibility(View.VISIBLE);
				findViewById(R.id.complete_btn).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						finish();
					}
				});
			}
			
	}
}
