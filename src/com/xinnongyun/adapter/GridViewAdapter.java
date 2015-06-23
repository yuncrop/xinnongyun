package com.xinnongyun.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xinnongyun.activity.BeingMarturingMangerActivity;
import com.xinnongyun.activity.MainActivity;
import com.xinnongyun.activity.R;
import com.xinnongyun.activity.SelectBeingActivity;
import com.xinnongyun.view.IconFontTextView;

public class GridViewAdapter extends BaseAdapter {

	private List<HashMap<String, Object>> listData;
	private LayoutInflater mInflater;
	private Context contex;
	public GridViewAdapter(Context context,
			List<HashMap<String, Object>> listData) {
		this.listData = listData;
		this.contex = context;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		convertView = mInflater.inflate(
				R.layout.activity_main_fragment_gv_item, null);
		
		TextView name = (TextView) convertView.findViewById(R.id.GvItemText);
		IconFontTextView image = (IconFontTextView) convertView.findViewById(R.id.GvItemImage);
		name.setText(listData.get(position).get("name").toString());
		if(position == 0)
		{
			image.setBackgroundResource(R.drawable.gv2_selector);
			image.setText(String.valueOf(new char[]{0xe630}));
			image.setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							Intent intent = new Intent();
							intent.setClass(contex,
									BeingMarturingMangerActivity.class);
							contex.startActivity(intent);
						}
					}
					);
		}else if(position == 1)
		{
			image.setBackgroundResource(R.drawable.gv1_selector);
			image.setText(String.valueOf(new char[]{0xe613}));
			image.setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							Intent intent = new Intent();
							intent.putExtra("type", "1");
							intent.setClass(contex, SelectBeingActivity.class);
							contex.startActivity(intent);
						}
					}
					);
		}else if(position == 2)
		{
			image.setBackgroundResource(R.drawable.gv3_selector);
			image.setText(String.valueOf(new char[]{0xe616}));
			image.setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							Intent intent = new Intent();
							intent.putExtra("type", "2");
							intent.setClass(contex, SelectBeingActivity.class);
							contex.startActivity(intent);
						}
					}
					);
			
		}else if(position == 3)
		{
			image.setBackgroundResource(R.drawable.gv4_selector);
			image.setText(String.valueOf(new char[]{0xe61c}));
			image.setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							((MainActivity)contex).mainFragment.startToRecord();
						}
					}
					);
		}
		return convertView;
	}

}
