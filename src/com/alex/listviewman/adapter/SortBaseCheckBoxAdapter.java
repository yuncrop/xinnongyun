package com.alex.listviewman.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.alex.listviewman.bean.PhoneModelbean;
import com.xinnongyun.activity.R;
import com.xinnongyun.utils.StringUtil;

/**
 * 
 * @author Mr.Z
 */
/** 自定义适配器 */
@SuppressLint("DefaultLocale")
public class SortBaseCheckBoxAdapter extends BaseAdapter implements SectionIndexer {
	private List<PhoneModelbean> list = new ArrayList<PhoneModelbean>();;
	private Context mContext;
	public static HashMap<Integer, Boolean> isSelected;
	public String selectDefaultId = null;
	public SortBaseCheckBoxAdapter(Context mContext, List<PhoneModelbean> list) {
		this.mContext = mContext;
		this.list = list;
		isSelected = new HashMap<Integer, Boolean>();    
		if(list!=null)
		{
	        for (int i = 0; i < list.size(); i++) {    
	            isSelected.put(i, false);    
	        }  
		}
		else
		{
			list = new ArrayList<PhoneModelbean>();
		}
	}

	
	public Handler datatHandler = new Handler()
	{
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			updateListView((List<PhoneModelbean>) msg.obj);
		}
	};
	
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<PhoneModelbean> list) {
		this.list = list;
		if(this.list == null)
		{
			this.list = new ArrayList<PhoneModelbean>();
		}
		for (int i = 0; i < list.size(); i++) {    
            isSelected.put(i, false);    
        }  
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final PhoneModelbean mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(
					R.layout.phone_item_adapter_check, null);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.title_checkBox);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog_checkBox);
			viewHolder.beingName = (TextView) view.findViewById(R.id.name);
			viewHolder.colllect_not_used = (TextView) view.findViewById(R.id.collect_not_used);
			viewHolder.collect_used_ll = (LinearLayout) view.findViewById(R.id.collect_used_ll);
			viewHolder.maturingAt = (TextView) view.findViewById(R.id.date);

			viewHolder.checkBox=(ImageView) view.findViewById(R.id.checkBox);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			//viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
		String ti = this.list.get(position).getName();
		if(ti.equals("null")) ti = "未设置";
		viewHolder.tvTitle.setText(ti);
		if(selectDefaultId!=null)
		if(mContent.getId().equals(selectDefaultId))
		{
			isSelected.put(position, true);
			selectDefaultId = null;
		}
		
		if(isSelected.get(position))
		{
			viewHolder.checkBox.setBackgroundResource(R.drawable.duoxuan_hou);
		}
		else
		{
			viewHolder.checkBox.setBackgroundResource(R.drawable.duoxuan_qian);
			
		}
		if(this.list.get(position).getNameSecond()==null || this.list.get(position).getNameSecond().equals("") || this.list.get(position).getNameSecond().equals("null"))
		{
			viewHolder.beingName.setText("未设置");
			viewHolder.collect_used_ll.setVisibility(View.INVISIBLE);
			viewHolder.colllect_not_used.setVisibility(View.VISIBLE);
		}
		else
		{
			viewHolder.collect_used_ll.setVisibility(View.VISIBLE);
			viewHolder.colllect_not_used.setVisibility(View.GONE);
			viewHolder.beingName.setText(this.list.get(position).getNameSecond());
		}
		if(this.list.get(position).getDate()==null || this.list.get(position).getDate().equals("") || this.list.get(position).getDate().equals("null"))
		{
			viewHolder.maturingAt.setText("未设置");
		}
		else
		{
			viewHolder.maturingAt.setText(StringUtil.getMouthDay(this.list.get(position).getDate()));
		}
		
		viewHolder.checkBox.setTag(isSelected.get(position));
		return view;

	}

	public class ViewHolder {
		TextView tvLetter,tvTitle,maturingAt,beingName,colllect_not_used;
		LinearLayout collect_used_ll;
		public ImageView checkBox;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	@SuppressLint("DefaultLocale")
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}
