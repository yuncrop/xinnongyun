package com.xinnongyun.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xinnongyun.activity.R;

/**
 * 适配
 * @author sm
 *
 */
public class ControlCenterForceItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<HashMap<String, String>> datas;
	private Context context;
	public static List<String> selectList;
	public static List<String> hasList = new ArrayList<String>();
	
	/**
	 * @param context
	 * @param data
	 */
	public ControlCenterForceItemAdapter(Context context,List<HashMap<String, String>> data) {
		selectList = new ArrayList<String>(hasList);
		this.datas = data;
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int arg0) {
		return datas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		convertView = mInflater.inflate(R.layout.robot_detail_forceopen_gvitem, null);
		TextView gv_item_tv = (TextView) convertView.findViewById(R.id.gv_item_tv);
		gv_item_tv.setText("" +(position+1));
		
		if(hasList.contains("" + position))
		{
			if(selectList.contains(position + ""))
			{
				gv_item_tv.setBackgroundResource(R.drawable.rangle_green);
				gv_item_tv.setTextColor(context.getResources().getColor(R.color.text_white));
			}
			else
			{
				gv_item_tv.setTextColor(context.getResources().getColor(R.color.text_grey));
				gv_item_tv.setBackgroundResource(R.drawable.rangle_grey);
			}
		}
		else
		{
			gv_item_tv.setBackground(null);
			gv_item_tv.setTextColor(context.getResources().getColor(R.color.text_white));
			gv_item_tv.setText("");
		}
		gv_item_tv.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(!hasList.contains(position + ""))
						{
							return;
						}
						if(selectList.contains(position + ""))
						{
							selectList.remove(position + "");
						}
						else
						{
							selectList.add(position + "");
						}
						notifyDataSetChanged();
					}
				}
				);
		return convertView;
	}

	
	
}
