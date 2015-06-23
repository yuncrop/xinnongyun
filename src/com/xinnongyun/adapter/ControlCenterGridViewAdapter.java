package com.xinnongyun.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.xinnongyun.activity.BlowerDetailInfoSetting;
import com.xinnongyun.activity.R;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.sqlite.BlowerDaoDBManager;
import com.xinnongyun.sqlite.BlowerTimeLineDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.utils.StringUtil;
import com.xinnongyun.view.IconFontTextView;
import com.xinnongyun.view.MyGridView;

/**
 * 评论列表适配
 * @author sm
 *
 */
public class ControlCenterGridViewAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private List<HashMap<String, String>> datas;
	/**
	 * @param context
	 * @param data
	 */
	public ControlCenterGridViewAdapter(Context context,List<HashMap<String, String>> data) {
		this.datas = data;
		mInflater = LayoutInflater.from(context);
		this.context = context;
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
		convertView = mInflater.inflate(R.layout.robot_fragment_list_item, null);
		final HashMap<String, String> ccMap = datas.get(position);
		((TextView)convertView.findViewById(R.id.robot_name)).setText(ccMap.get(TablesColumns.TABLECONTROLCENTER_NAME).toString());
		MyGridView robotItemInfo = (MyGridView) convertView.findViewById(R.id.robot_item_contents);
		String ccBlowerIds = "";
		robotItemInfo.setClickable(false);
		robotItemInfo.setEnabled(false);
		robotItemInfo.setSelected(false);
		BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(context);
		List<HashMap<String, String>> bMaps = blowerDaoDBManager.getAllBlowersByCCId(ccMap.get(TablesColumns.TABLECONTROLCENTER_ID).toString());
		BlowerTimeLineDaoDBManager blowerTimeLineDaoDBManager = new BlowerTimeLineDaoDBManager(context);
		List<HashMap<String, String>> btMaps = new ArrayList<HashMap<String,String>>();
		
		boolean isLock = false;
		for(HashMap<String, String> map : bMaps)
		{
			if(map.values().size()<2)
			{
				continue;
			}
			if(map.get(TablesColumns.TABLEBLOWER_WORKING_MODE).equals("2"))
			{
				isLock = true;
			}
			
			HashMap<String, String>  bTMap = blowerTimeLineDaoDBManager.getCollectLastUpdateTime(map.get(TablesColumns.TABLEBLOWER_ID).toString());
			
			if(bTMap!=null && bTMap.size()>2)
			{
				bTMap.remove("id");
				ccBlowerIds += map.get(TablesColumns.TABLEBLOWER_ID) + " ";
				bTMap.put("no", map.get("no"));
				bTMap.put(TablesColumns.TABLEBLOWER_ADDITION_DISTANCE, map.get(TablesColumns.TABLEBLOWER_ADDITION_DISTANCE));
				bTMap.put(TablesColumns.TABLEBLOWER_MAX_DISTANCE, map.get(TablesColumns.TABLEBLOWER_MAX_DISTANCE));
				bTMap.put(TablesColumns.TABLEBLOWER_MAXTEMLIMIT, map.get(TablesColumns.TABLEBLOWER_MAXTEMLIMIT));
				bTMap.put(TablesColumns.TABLEBLOWER_MINTEMLIMIT, map.get(TablesColumns.TABLEBLOWER_MINTEMLIMIT));
				btMaps.add(bTMap);
			}
		}
		
		if(isLock)
		{
			IconFontTextView robot_isclock_tv = (IconFontTextView) convertView.findViewById(R.id.robot_isclock_tv);
			robot_isclock_tv.setText(String.valueOf(new char[]{0xe660}));
			robot_isclock_tv.setTextColor(Color.parseColor("#fe462e"));
			robot_isclock_tv.setVisibility(View.VISIBLE);
			
		}
		else
		{
			convertView.findViewById(R.id.robot_isclock_tv).setVisibility(View.GONE);
		}
		
		
		HashMap<String, String>  tMap = blowerTimeLineDaoDBManager.getCollectLastUpdateTimeCC(ccBlowerIds);
		if(tMap!=null && tMap.size()>1)
		{
			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date d2 = df.parse(StringUtil.changeSerTo(tMap.get(TablesColumns.TABLEBBLOWERENV_CREATEDAT)));
				if(StringUtil.miniteLength(new Date(), d2)>YunTongXun.minuteLength)
				{
					IconFontTextView robot_power_tv = (IconFontTextView) convertView.findViewById(R.id.robot_power_tv);
					robot_power_tv.setText(String.valueOf(new char[]{0xe644}));
					robot_power_tv.setTextColor(Color.parseColor("#999999"));
					robot_power_tv.setVisibility(View.VISIBLE);
				}
				else
				{
					convertView.findViewById(R.id.robot_power_tv).setVisibility(View.GONE);
				}
			} catch (Exception e) {
			}
		}
		
		
		int bTSize = 8 - btMaps.size();
		
		for(int i =0; i<bTSize;i++)
		{
			btMaps.add(new HashMap<String, String>());
		}
		robotItemInfo.setAdapter(new ControlCenterGridViewItemAdapter(context, btMaps));
		final String ccBlowerIds2 = ccBlowerIds;
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, BlowerDetailInfoSetting.class);
				intent.putExtra("id", ccMap.get(TablesColumns.TABLECONTROLCENTER_ID).toString());
				intent.putExtra("ccName", ccMap.get(TablesColumns.TABLECONTROLCENTER_NAME).toString());
				intent.putExtra("ccBlowerIds", ccBlowerIds2.trim());
				intent.putExtra("serial", ccMap.get(TablesColumns.TABLECONTROLCENTER_SERIAL).toString());
				context.startActivity(intent);
			}
		});
		
		return convertView;
	}

}
