package com.xinnongyun.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xinnongyun.activity.R;
import com.xinnongyun.sqlite.TablesColumns;

public class BoxSoilAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<HashMap<String, String>> datas;
	public String selectType;
	private int type;
	/**
	 * 
	 * @param context
	 * @param soilItems
	 * @param currentSelect
	 * @param type 1为土壤  2为农场管理
	 */
	public BoxSoilAdapter(Context context,List<HashMap<String, String>> soilItems,String currentSelect,int type) {
		this.datas = soilItems;
		this.type = type;
		selectType = currentSelect;
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
	public View getView(int position, View convertView, ViewGroup arg2) {
		if(type ==1)
		{
			convertView = mInflater.inflate(R.layout.soillist_item, null);
		}else if(type ==2)
		{
			convertView = mInflater.inflate(R.layout.add_create_farm_item, null);
		}else if(type ==3)
		{
			convertView = mInflater.inflate(R.layout.alarmingsetting_item, null);
		}
		((TextView)convertView.findViewById(R.id.solillistitem_name)).setText(datas.get(position).get(TablesColumns.TABLECOLLECTOR_NAME).toString());
		
		if(selectType!=null && datas.get(position).get(TablesColumns.TABLECOLLECTOR_NAME).toString().equals(selectType))
		{
			if(type == 3)
			{
				((TextView)convertView.findViewById(R.id.solillistitem_name)).setTextColor(mInflater.getContext().getResources().getColor(R.color.text_black));
			}
			convertView.findViewById(R.id.soillistitem_iv).setVisibility(View.VISIBLE);
		}
		else
		{
			convertView.findViewById(R.id.soillistitem_iv).setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

}
