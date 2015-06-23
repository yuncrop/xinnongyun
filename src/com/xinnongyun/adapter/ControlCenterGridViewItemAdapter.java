package com.xinnongyun.adapter;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xinnongyun.activity.R;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.utils.StringUtil;

/**
 * 评论列表适配
 * @author sm
 *
 */
public class ControlCenterGridViewItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<HashMap<String, String>> datas;
	/**
	 * @param context
	 * @param data
	 */
	public ControlCenterGridViewItemAdapter(Context context,List<HashMap<String, String>> data) {
		this.datas = data;
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
		convertView = mInflater.inflate(R.layout.robot_fragment_list_item_gvitem, null);
		convertView.setClickable(false);
		if(position == 7)
		{
			convertView.findViewById(R.id.clip_line).setVisibility(View.INVISIBLE);
		}
		boolean isHas = false;
		for(HashMap<String, String> map : datas)
		{
			if(map.size()>0)
			{
				if(map.containsKey("no"))
				{
					if(map.get("no")!=null && map.get("no").equals(position+""))
					{
						try {
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date d2 = df.parse(StringUtil.changeSerTo(map.get(TablesColumns.TABLEBBLOWERENV_CREATEDAT)));
							int precent = 0;
							double cP = 0;
							if(Integer.parseInt(map.get(TablesColumns.TABLEBLOWER_MAX_DISTANCE).toString()) > Integer.parseInt(map.get(TablesColumns.TABLEBLOWER_ADDITION_DISTANCE).toString()))
							{
								if(Double.parseDouble(map.get(TablesColumns.TABLEBBLOWERENV_DISTANCE))<Integer.parseInt(map.get(TablesColumns.TABLEBLOWER_ADDITION_DISTANCE).toString()))
								{
									precent = 0;
								}
								else
								cP = (Double.parseDouble(map.get(TablesColumns.TABLEBBLOWERENV_DISTANCE))-Integer.parseInt(map.get(TablesColumns.TABLEBLOWER_ADDITION_DISTANCE).toString()))/
								(Integer.parseInt(map.get(TablesColumns.TABLEBLOWER_MAX_DISTANCE).toString())-Integer.parseInt(map.get(TablesColumns.TABLEBLOWER_ADDITION_DISTANCE).toString()));
								
								precent = cP>1 ? 100 : (int)(cP*100);
							}
							
							if(StringUtil.miniteLength(new Date(), d2) > YunTongXun.minuteLength)
							{
								((TextView)convertView.findViewById(R.id.robot_gv_item_precent)).setText(precent+"");
							
								try {
									((TextView)convertView.findViewById(R.id.robot_gv_item_tem)).setText("" + new DecimalFormat("#####0").format(Double.parseDouble(map.get(TablesColumns.TABLEBBLOWERENV_TEM).toString())));
								} catch (Exception e) {
									((TextView)convertView.findViewById(R.id.robot_gv_item_tem)).setText(map.get(TablesColumns.TABLEBBLOWERENV_TEM).toString());

								}
								((TextView)convertView.findViewById(R.id.robot_gv_item_precent)).setTextColor(mInflater.getContext().getResources().getColor(R.color.text_grey));
								((TextView)convertView.findViewById(R.id.robot_gv_item_tem)).setTextColor(mInflater.getContext().getResources().getColor(R.color.text_grey));
							}
							else
							{
								((TextView)convertView.findViewById(R.id.robot_gv_item_precent)).setText(precent + "");

								
								try {
									double tem = Double.parseDouble(map.get(TablesColumns.TABLEBBLOWERENV_TEM).toString());
									if(tem > Double.parseDouble(map.get(TablesColumns.TABLEBLOWER_MAXTEMLIMIT).toString())||
											tem < Double.parseDouble(map.get(TablesColumns.TABLEBLOWER_MINTEMLIMIT).toString()))
									{
										((TextView)convertView.findViewById(R.id.robot_gv_item_tem)).setTextColor(mInflater.getContext().getResources().getColor(R.color.text_fe462e));
									}
									((TextView)convertView.findViewById(R.id.robot_gv_item_tem)).setText(new DecimalFormat("#####0").format(Double.parseDouble(map.get(TablesColumns.TABLEBBLOWERENV_TEM).toString())));
								} catch (Exception e) {
									((TextView)convertView.findViewById(R.id.robot_gv_item_tem)).setText(map.get(TablesColumns.TABLEBBLOWERENV_TEM).toString());

								}
							}
						} catch (Exception e) {
						}
						
						isHas = true;
					}
				}
			}
		}
		if(!isHas)
		{
			convertView.findViewById(R.id.robot_gv_item_precent).setVisibility(View.INVISIBLE);
			convertView.findViewById(R.id.robot_gv_item_precent_unit).setVisibility(View.INVISIBLE);
			convertView.findViewById(R.id.robot_gv_item_tem).setVisibility(View.INVISIBLE);
			convertView.findViewById(R.id.robot_gv_item_tem_unit).setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

}
