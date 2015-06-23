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
import com.xinnongyun.net.NetConnectionCamera;
import com.xinnongyun.net.NetConnectionCamera.CameraSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.CameraDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.utils.ImageUtils;
import com.xinnongyun.view.OnToggleStateChangeListener;
import com.xinnongyun.view.SlideButton;

public class CameraInfoActivity extends Activity implements OnToggleStateChangeListener {

	private String cameraId = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_info);
		cameraId = getIntent().getStringExtra("id");
		if(cameraId!=null && cameraId.length()>1)
		{
			intialView();
		}else
		{
			Toast.makeText(this, "信息不存在", Toast.LENGTH_SHORT).show();
			setResult(10);
			finish();
		}
	}
	HashMap<String, String> cameraInfo;
	public void onToggleStateChange(boolean b) {
		if (b) {
			if(cameraInfo!=null)
			saveCollectInfo(cameraInfo.get(TablesColumns.TABLECAMERA_ID), cameraInfo.get(TablesColumns.TABLECAMERA_NAME), "true");
		} else {
			if(cameraInfo!=null)
				saveCollectInfo(cameraInfo.get(TablesColumns.TABLECAMERA_ID), cameraInfo.get(TablesColumns.TABLECAMERA_NAME), "false");
		}
	}
	
	public SlideButton slideButton;
	@SuppressWarnings("deprecation")
	private void intialView()
	{
		findViewById(R.id.collectReturn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		CameraDaoDBManager cameraDaoDBManager = new CameraDaoDBManager(this);
		cameraInfo = cameraDaoDBManager.getCameraInfoById(cameraId);
		if(cameraInfo!=null && cameraInfo.keySet().size()>0)
		{
			findViewById(R.id.popdelete_iv).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					btn_PopupMenu(v);
				}
			});
			
			slideButton = (SlideButton) findViewById(R.id.slidebutton);
			if(cameraInfo.get(TablesColumns.TABLECAMERA_PUBLISH).equals("true"))
			{
				slideButton.setToggleState(true);
			}
			else
			{
				slideButton.setToggleState(false);
			}
			
			slideButton.setOnToggleStateChangeListener(this);
			
			findViewById(R.id.box_changename_ll).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent nameChange = new Intent();
					nameChange.setClass(CameraInfoActivity.this, CameraNameModifyActivity.class);
					nameChange.putExtra("id", cameraId);
					nameChange.putExtra("name", cameraInfo.get(TablesColumns.TABLECAMERA_NAME).toString());
					nameChange.putExtra("isPublish", cameraInfo.get(TablesColumns.TABLECAMERA_PUBLISH).toString());
					startActivityForResult(nameChange, 10);
				}
			});
			((TextView)findViewById(R.id.collect_name)).setText(cameraInfo.get(TablesColumns.TABLECAMERA_NAME).toString());
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				((ImageView)findViewById(R.id.collect_image)).setBackground(new BitmapDrawable(ImageUtils
						.Create2DCode(cameraInfo.get(TablesColumns.TABLECAMERA_DEVICESERIAL).toString())));
			} else {
				((ImageView)findViewById(R.id.collect_image)).setBackgroundDrawable(new BitmapDrawable(ImageUtils
						.Create2DCode(cameraInfo.get(TablesColumns.TABLECAMERA_DEVICESERIAL).toString())));
			}
			((TextView)findViewById(R.id.weather_station_serial)).setText(cameraInfo.get(TablesColumns.TABLECAMERA_DEVICESERIAL).toString());
		}else
		{
			Toast.makeText(this, "信息不存在", Toast.LENGTH_SHORT).show();
			setResult(10);
			finish();
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==10)
		{
			setResult(10);
			finish();
		}
	}
	
	
	
	
	
	private  void saveCollectInfo(String collectId,String name,final String isPublish)
	{
		dg = MainFragment.createLoadingDialog(this);
		dg.show();
		String para = "farm="+MySharePreference.getValueFromKey(this, MySharePreference.FARMID)+ 
				"&name=" + name+
				"&publish=" + isPublish;
		NetConnectionCamera.CameraPutNetConnection(NetUrls.URL_CAMREA_MODIFY_ADD(collectId), para,
				"Token "+MySharePreference.getValueFromKey(this, MySharePreference.ACCOUNTTOKEN),
				new CameraSuccessCallBack() {
					@Override
					public void onSuccess(HashMap<String, Object> result) {
						dg.dismiss();
						if(result!=null)
						{
							if(result.keySet().contains("status_code"))
							{
								if(isPublish.equals("true"))
								{
									slideButton.setToggleState(false);
								}else
								{
									slideButton.setToggleState(true);
								}
								ErrorMarkToast.showCallBackToast(CameraInfoActivity.this, result);
								return;
							}
							CameraDaoDBManager daoDBManager = new CameraDaoDBManager(CameraInfoActivity.this);
							daoDBManager.updateCameraById(result,2);
							setResult(30);
							finish();
						}
						else
						{
							if(isPublish.equals("true"))
							{
								slideButton.setToggleState(false);
							}else
							{
								slideButton.setToggleState(true);
							}
							showToast(R.string.interneterror);
						}
					}
				}
				);
	}


	private PopupWindow popWindow;
	@SuppressWarnings("deprecation")
	public void btn_PopupMenu(View view) {
		View popContent = LayoutInflater.from(view.getContext()).inflate(R.layout.pop_delete, null);

		popWindow = new PopupWindow(popContent,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);
		popWindow.showAsDropDown(view);
		LinearLayout popll = (LinearLayout) popContent.findViewById(R.id.popdelet_ll);
		popll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialogDelete(CameraInfoActivity.this);
				if(popWindow!=null)
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
				dg = MainFragment.createLoadingDialog(CameraInfoActivity.this);
				dg.show();
				NetConnectionCamera.CameraDeleteNetConnection(NetUrls.URL_CAMREA_DELETE(cameraId), null, 
						"Token "+ MySharePreference.getValueFromKey(CameraInfoActivity.this,MySharePreference.ACCOUNTTOKEN), 
						new CameraSuccessCallBack() {
							@Override
							public void onSuccess(HashMap<String, Object> result) {
								dg.dismiss();
								dlg.dismiss();
								if(result!=null)
								{
									if(result.get("status_code").toString().contains("20"))
									{
										// 删除摄像头
										CameraDaoDBManager cameraDaoDBManager = new CameraDaoDBManager(CameraInfoActivity.this);
										cameraDaoDBManager.deleteCmareaById(cameraId);
										setResult(20);
										finish();
									}
									else
									{
										ErrorMarkToast.showCallBackToast(CameraInfoActivity.this, result);;
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
		Toast.makeText(CameraInfoActivity.this, id, Toast.LENGTH_SHORT).show();
	}
}
