package com.alex.listviewman.adapter;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.alex.listviewman.bean.PhoneModelbean;
import com.xinnongyun.activity.R;
import com.xinnongyun.imageload.ImageDownloadHelper;
import com.xinnongyun.imageload.ImageDownloadHelper.OnImageDownloadListener;

/**
 * @author Mr.Z
 */
/** 自定义适配器 */
@SuppressLint("DefaultLocale")
public class SortBaseAdapter extends BaseAdapter implements SectionIndexer {
	private List<PhoneModelbean> list = null;
	private Context mContext;
	private String beingType;
	public SortBaseAdapter(Context mContext, List<PhoneModelbean> list,String beingType) {
		this.mContext = mContext;
		this.list = list;
		if(this.list == null)
		{
			this.list = new ArrayList<PhoneModelbean>();
		}
		this.beingType = beingType;
	}

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
		 
		PhoneModelbean mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(
					R.layout.phone_item_adapter, null);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			viewHolder.tvHead = (ImageView) view.findViewById(R.id.image);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
		viewHolder.tvHead.setTag(this.list.get(position).getImgSrc());
		if(beingType.equals("1"))
		{
			viewHolder.tvHead.setImageResource(R.drawable.zuowu_head);
		}
		else
		{
			viewHolder.tvHead.setImageResource(R.drawable.animal_head);
		}
		if(!list.get(position).getImgSrc().contains("default") && this.list.get(position).getImgSrc()!=null && !this.list.get(position).getImgSrc().equals("null"))
		{
			ImageDownloadHelper.getInstance().imageDownload(null,mContext, this.list.get(position).getImgSrc(), viewHolder.tvHead,"/001yunhekeji100",
					new OnImageDownloadListener() {
						public void onImageDownload(Bitmap bitmap, String imgUrl) {
							if (bitmap != null) {
								if(viewHolder.tvHead.getTag()!=null && !viewHolder.tvHead.getTag().equals("null") && viewHolder.tvHead.getTag().toString().equals(imgUrl))
								  {
									viewHolder.tvHead.setImageBitmap(bitmap);
								  }
								else
								{
									if(beingType.equals("1"))
									{
										viewHolder.tvHead.setImageResource(R.drawable.zuowu_head);
									}
									else
									{
										viewHolder.tvHead.setImageResource(R.drawable.animal_head);
									}
								}
							} else {
								if(beingType.equals("1"))
								{
									viewHolder.tvHead.setImageResource(R.drawable.zuowu_head);
								}
								else
								{
									viewHolder.tvHead.setImageResource(R.drawable.animal_head);
								}
							}
						}
					});
		}
		viewHolder.tvTitle.setText(this.list.get(position).getName());
		viewHolder.tvTitle.setTag(this.list.get(position).getId());


		return view;

	}
	ViewHolder viewHolder = null;
	final static class ViewHolder {
		ImageView tvHead;
		TextView tvLetter;
		TextView tvTitle;
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
