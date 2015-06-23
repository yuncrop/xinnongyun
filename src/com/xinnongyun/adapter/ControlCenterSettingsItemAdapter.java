package com.xinnongyun.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xinnongyun.activity.BlowerTimeLineActivity;
import com.xinnongyun.activity.R;
import com.xinnongyun.sqlite.BlowerTimeLineDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.view.IconFontTextView;

/**
 * 评论列表适配
 * @author sm
 *
 */
public class ControlCenterSettingsItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<HashMap<String, String>> datas;
	private Context context;
	/**
	 * @param context
	 * @param data
	 */
	public ControlCenterSettingsItemAdapter(Context context,List<HashMap<String, String>> data) {
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
		convertView = mInflater.inflate(R.layout.robot_infosettings_item, null);
		boolean isHas = false;
		for(final HashMap<String, String> map : datas)
		{
			if(map.keySet().contains(TablesColumns.TABLEBLOWER_NO))
			{
				if(map.get(TablesColumns.TABLEBLOWER_NO).toString().equals("" + position))
				{
					isHas = true;
					
//					ImageView spaceshipImage = (ImageView) convertView.findViewById(R.id.img);
//					spaceshipImage.setVisibility(View.VISIBLE);
//					Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(  
//			                context, R.anim.loading_animation);  
//			        // 使用ImageView显示动画  
//			        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
					convertView.setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent intent = new Intent();
									intent.putExtra("blowerId", map.get(TablesColumns.TABLEBLOWER_ID).toString());
									intent.putExtra("blowerNum", ""+(position +1));
									intent.setClass(context, BlowerTimeLineActivity.class);
									context.startActivity(intent);
								}
							}
							);
					BlowerTimeLineDaoDBManager blowerTimeLineDaoDBManager = new BlowerTimeLineDaoDBManager(context);
					HashMap<String, String> bTimeMap = blowerTimeLineDaoDBManager.getCollectLastUpdateTime(map.get(TablesColumns.TABLEBLOWER_ID).toString());
					if(bTimeMap!=null && bTimeMap.size()>2)
					{
						TextView precentTv = (TextView) convertView.findViewById(R.id.precentTv);
						TextView currentTem = (TextView) convertView.findViewById(R.id.currentTem);
					
						int precent = 0;
						double cP = 0;
						
						
						if(Integer.parseInt(map.get(TablesColumns.TABLEBLOWER_MAX_DISTANCE).toString()) > Integer.parseInt(map.get(TablesColumns.TABLEBLOWER_ADDITION_DISTANCE).toString()))
						{
							if(Double.parseDouble(bTimeMap.get(TablesColumns.TABLEBBLOWERENV_DISTANCE))<Integer.parseInt(map.get(TablesColumns.TABLEBLOWER_ADDITION_DISTANCE).toString()))
							{
								precent = 0;
							}
							else
							cP = (Double.parseDouble(bTimeMap.get(TablesColumns.TABLEBBLOWERENV_DISTANCE))-Integer.parseInt(map.get(TablesColumns.TABLEBLOWER_ADDITION_DISTANCE).toString()))/(Integer.parseInt(map.get(TablesColumns.TABLEBLOWER_MAX_DISTANCE).toString())-
									Integer.parseInt(map.get(TablesColumns.TABLEBLOWER_ADDITION_DISTANCE).toString()));
							
							precent = cP>1 ? 100 : (int)(cP*100);
						}
						precentTv.setText(precent+"");
						
						currentTem.setText(bTimeMap.get(TablesColumns.TABLEBBLOWERENV_TEM));
					}
					TextView itemnum = (TextView) convertView.findViewById(R.id.itemnum);
					itemnum.setText("" + (position + 1));
					itemnum.setBackgroundResource(R.drawable.circle_empty_gv3_default);
					IconFontTextView iconTv =  (IconFontTextView) convertView.findViewById(R.id.settingsFreshTv);
					iconTv.setTextColor(Color.parseColor("#999999"));
					iconTv.setText(String.valueOf(new char[]{0xe662}));
					if(map.get(TablesColumns.TABLEBLOWER_WORKING_MODE).toString().equals("1"))
					{
						IconFontTextView settingsIsClock =  (IconFontTextView) convertView.findViewById(R.id.settingsIsClock);
						settingsIsClock.setTextColor(Color.parseColor("#999999"));
						settingsIsClock.setText(String.valueOf(new char[]{0xe661}));
						
						TextView settingsIsClockTv = (TextView) convertView.findViewById(R.id.settingsIsClock_text);
						settingsIsClockTv.setText("自动");
						settingsIsClockTv.setTextColor(Color.parseColor("#999999"));
					}else
					{
						IconFontTextView settingsIsClock =  (IconFontTextView) convertView.findViewById(R.id.settingsIsClock);
						settingsIsClock.setTextColor(Color.parseColor("#fe462e"));
						settingsIsClock.setText(String.valueOf(new char[]{0xe660}));
						
						TextView settingsIsClockTv = (TextView) convertView.findViewById(R.id.settingsIsClock_text);
						settingsIsClockTv.setText("锁定");
						settingsIsClockTv.setTextColor(Color.parseColor("#fe462e"));
					}
					
					TextView min_max_tem = (TextView) convertView.findViewById(R.id.min_max_tem);
					min_max_tem.setText(map.get(TablesColumns.TABLEBLOWER_MINTEMLIMIT).toString() + "-" + map.get(TablesColumns.TABLEBLOWER_MAXTEMLIMIT).toString());
				}
			}
		}
		
		if(!isHas)
		{
			TextView itemnum = (TextView) convertView.findViewById(R.id.itemnum);
			itemnum.setText("" + (position + 1));
			itemnum.setBackgroundResource(R.drawable.circle_grey);
			convertView.findViewById(R.id.ll1).setVisibility(View.INVISIBLE);
			convertView.findViewById(R.id.ll2).setVisibility(View.INVISIBLE);
			convertView.findViewById(R.id.settingsFreshTv).setVisibility(View.INVISIBLE);
		}
		
		
		return convertView;
	}

	
	
}
