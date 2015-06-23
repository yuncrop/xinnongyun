package com.xinnongyun.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.sitech.oncon.barcode.core.CaptureActivity;
import com.xinnongyun.activity.FarmConfigActivity;
import com.xinnongyun.activity.R;
import com.xinnongyun.adapter.BoxSoilAdapter;
import com.xinnongyun.config.YunTongXun;

public class DialogManager {

	/**
	 * 简单提示框
	 * 
	 * @param context
	 * @param headStringId
	 * @param contentStirngId
	 */
	public static void showDialogSimple(Context context,
			int contentStirngId) {
		final AlertDialog dlg = new AlertDialog.Builder(context).create();
		dlg.show();
		Window window = dlg.getWindow();
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.tishi_show_dialog);
		((TextView) window.findViewById(R.id.dialog_content))
				.setText(contentStirngId);
		// 为确认按钮添加事件,执行退出应用操作
		Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
	}
	
//	/**
//	 * 简单提示框
//	 * 
//	 * @param context
//	 * @param headStringId
//	 * @param contentStirngId
//	 */
//	public static void showDialogSimple(Context context, int headStringId,
//			int contentStirngId) {
//		final AlertDialog dlg = new AlertDialog.Builder(context).create();
//		dlg.show();
//		Window window = dlg.getWindow();
//		// *** 主要就是在这里实现这种效果的.
//		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
//		window.setContentView(R.layout.tishi_show_dialog);
//
//		((TextView) window.findViewById(R.id.dialog_head))
//				.setText(headStringId);
//		((TextView) window.findViewById(R.id.dialog_content))
//				.setText(contentStirngId);
//		// 为确认按钮添加事件,执行退出应用操作
//		Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
//		ok.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				dlg.dismiss();
//			}
//		});
//	}

	/**
	 * 带事件的提示框
	 * 
	 * @param context
	 * @param headStringId
	 * @param contentStirngId
	 * @param listener
	 */
	public static void showDialogClickListener(Context context,
			int headStringId, int contentStirngId, View.OnClickListener listener) {
		final AlertDialog dlg = new AlertDialog.Builder(context).create();
		dlg.show();
		Window window = dlg.getWindow();
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.tishi_show_dialog_twobtn);
//		((TextView) window.findViewById(R.id.dialog_head))
//				.setText(headStringId);
		((TextView) window.findViewById(R.id.dialog_content))
				.setText(contentStirngId);
		// 为确认按钮添加事件,执行退出应用操作
		Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
		ok.setTag(dlg);
		ok.setOnClickListener(listener);
		Button cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
			}
		});
	}

	public static void showDialogAddOrCreateFarm(final Context context) {
		final AlertDialog dlg = new AlertDialog.Builder(context).create();
		dlg.show();
		Window window = dlg.getWindow();
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.tishi_show_dialog_add_create_farm);
		((TextView) window.findViewById(R.id.dialog_head)).setText(R.string.nofarm_dialog);
		ListView list = (ListView) window
				.findViewById(R.id.add_create_farm_list);
		List<HashMap<String, String>> soilItems = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("name", context.getResources().getString(R.string.farmconfig_newfarm));
		soilItems.add(map);
		map = new HashMap<String, String>();
		map.put("name", context.getResources().getString(R.string.farmconfig_addfarm));
		soilItems.add(map);
		final BoxSoilAdapter adapter = new BoxSoilAdapter(context, soilItems, "",2);
		list.setAdapter(adapter);
		list.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View view,
							int arg2, long arg3) {
						if(((TextView)view.findViewById(R.id.solillistitem_name)).getText().toString().equals(context.getResources().getString(R.string.farmconfig_newfarm)))
						{
							adapter.selectType = context.getResources().getString(R.string.farmconfig_newfarm);
						}
						else
						{
							adapter.selectType = context.getResources().getString(R.string.farmconfig_addfarm);
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
				if(adapter.selectType == context.getResources().getString(R.string.farmconfig_newfarm))
				{
					dlg.dismiss();
					Intent intent = new Intent();
					intent.setClass(context, FarmConfigActivity.class);
					context.startActivity(intent);
				}
				else if(adapter.selectType == context.getResources().getString(R.string.farmconfig_addfarm))
				{
					dlg.dismiss();
					Intent addFarmIntent = new Intent(context,
							CaptureActivity.class);
					addFarmIntent.putExtra(YunTongXun.CAPTURE_KEY,
							YunTongXun.CAPTURE_ADDFARM);
					context.startActivity(addFarmIntent);
				}else
				{
					
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

}
