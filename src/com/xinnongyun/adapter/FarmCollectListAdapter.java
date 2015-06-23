package com.xinnongyun.adapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xinnongyun.activity.R;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.sqlite.CollectDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.utils.StringUtil;

public class FarmCollectListAdapter extends BaseAdapter {

	private List<HashMap<String, Object>> datas;
	private LayoutInflater mInflater;
	private CollectDaoDBManager collectDao;
	private Context context;
	public FarmCollectListAdapter(Context context,List<HashMap<String, Object>> datas)
	{
		this.context = context;
		this.datas = datas;
		collectDao = new CollectDaoDBManager(context);
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
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		    HashMap<String, Object> map = datas.get(position);
			if(!map.keySet().contains("notused"))
			{  
				CollectsItemViewHolder mHolder;
				if (convertView == null) {
					mHolder = new CollectsItemViewHolder();
					// 热度.Item
					mInflater = LayoutInflater.from(context);
					convertView = mInflater.inflate(R.layout.main_fragment_farm_item_listlitem, null);
					mHolder.colectName = (TextView) convertView.findViewById(R.id.farmingItem_listlitem_collectname);
					mHolder.collectTem = (TextView) convertView.findViewById(R.id.farmingItem_listlitem_tem);
					mHolder.collectHum = (TextView) convertView.findViewById(R.id.farmingItem_listlitem_hum);
					mHolder.electrialiv = (ImageView) convertView.findViewById(R.id.electrialiv);
					mHolder.tem_soil = (TextView) convertView.findViewById(R.id.tem_soil);
					mHolder.hum_soil = (TextView) convertView.findViewById(R.id.hum_soil);
					mHolder.collectLight = (TextView) convertView.findViewById(R.id.farmingItem_listlitem_light);
					mHolder.xinhaoempty = (ImageView) convertView.findViewById(R.id.xinhao_empty);
					mHolder.light_type = (TextView) convertView.findViewById(R.id.light_type);
					mHolder.hum_type = (TextView) convertView.findViewById(R.id.hum_type);
					mHolder.tem_type = (TextView) convertView.findViewById(R.id.tem_type);
					Typeface face = Typeface.createFromAsset (context.getAssets() , "Arial.ttf" );
					mHolder.collectLight.setTypeface(face);
					mHolder.collectTem.setTypeface(face);
					mHolder.collectHum.setTypeface(face);
					mHolder.light_type.setTypeface(face);
					mHolder.tem_type.setTypeface(face);
					mHolder.hum_type.setTypeface(face);
					convertView.setTag(mHolder);
				} else {
					mHolder = (CollectsItemViewHolder) convertView.getTag();
				}
				
				if(map.keySet().size()>=1)
				{
					mHolder.colectName.setTag(map.get(TablesColumns.TABLETIMELINE_COLLECTOR).toString());
					HashMap<String,String> colMap = collectDao.getCollectById(map.get(TablesColumns.TABLETIMELINE_COLLECTOR).toString());
					String name = colMap.get(TablesColumns.TABLECOLLECTOR_NAME);
					mHolder.colectName.setText(name);
					if(map!=null && colMap.get(TablesColumns.TABLETIMELINE_CATEGORY)!=null && colMap.get(TablesColumns.TABLETIMELINE_CATEGORY).equals("2"))
					{
						mHolder.tem_soil.setText(R.string.tem_soil);
						mHolder.hum_soil.setText(R.string.hum_soil);
						convertView.findViewById(R.id.farmingItem_listlitem_light_sliderview).setVisibility(View.INVISIBLE);
						convertView.findViewById(R.id.farmingItem_listlitem_light_rl).setVisibility(View.INVISIBLE);
					}
					else
					{
						convertView.findViewById(R.id.farmingItem_listlitem_light_sliderview).setVisibility(View.VISIBLE);
						convertView.findViewById(R.id.farmingItem_listlitem_light_rl).setVisibility(View.VISIBLE);
						mHolder.tem_soil.setText(R.string.tem);
						mHolder.hum_soil.setText(R.string.hum);
						if(map!=null && map.get(TablesColumns.TABLETIMELINE_ILLU)!=null &&!map.get(TablesColumns.TABLETIMELINE_ILLU).toString().equals("null"))
						{
							String illu = (int)Double.parseDouble(map.get(TablesColumns.TABLETIMELINE_ILLU).toString()) < 100 ? 
									map.get(TablesColumns.TABLETIMELINE_ILLU).toString(): (int)(Double.parseDouble(map.get(TablesColumns.TABLETIMELINE_ILLU).toString()))+"";
							mHolder.collectLight.setText(illu);
						}
						else
						{
							mHolder.collectLight.setText("无");
						}
					}
					
					if(map.get(TablesColumns.TABLETIMELINE_COLLECTEDAT)!=null)
					{
						String lastDate = map.get(TablesColumns.TABLETIMELINE_COLLECTEDAT).toString();
						long minutes = 0;
						try {
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date d2 = df.parse(StringUtil.changeSerTo(lastDate));
							minutes = StringUtil.miniteLength(new Date(),d2);
						} catch (ParseException e) {
						}
						if(map!=null && YunTongXun.powderLeft >= Integer.parseInt(map.get(TablesColumns.TABLETIMELINE_POWDER).toString()))
						{
							mHolder.electrialiv.setVisibility(View.VISIBLE);
						}
						else
						{
							mHolder.electrialiv.setVisibility(View.GONE);
						}
						if(minutes >= YunTongXun.minuteLength)
						{
							mHolder.collectTem.setTextColor(context.getResources().getColor(R.color.text_grey));
							mHolder.collectHum.setTextColor(context.getResources().getColor(R.color.text_grey));
							mHolder.collectLight.setTextColor(context.getResources().getColor(R.color.text_grey));
							mHolder.xinhaoempty.setVisibility(View.VISIBLE);
						}
						else
						{
							mHolder.collectTem.setTextColor(context.getResources().getColor(R.color.text_blue));
							mHolder.collectHum.setTextColor(context.getResources().getColor(R.color.text_blue));
							mHolder.collectLight.setTextColor(context.getResources().getColor(R.color.text_blue));
							mHolder.xinhaoempty.setVisibility(View.INVISIBLE);
						}
						if(map!=null && map.get(TablesColumns.TABLETIMELINE_CATEGORY).equals("2"))
						{
							try {
								mHolder.collectHum.setText(map.get(TablesColumns.TABLETIMELINE_SOIL_HUM).toString());
							} catch (Exception e) {
							}
							mHolder.collectTem.setText(map.get(TablesColumns.TABLETIMELINE_SOIL_TEM).toString());
						}
						if(map!=null && map.get(TablesColumns.TABLETIMELINE_CATEGORY).equals("1"))
						{
							mHolder.collectHum.setText(map.get(TablesColumns.TABLETIMELINE_HUM).toString());
							
							mHolder.collectTem.setText(map.get(TablesColumns.TABLETIMELINE_TEM).toString());
							
						}
					}
					else
					{
						mHolder.collectTem.setTextColor(context.getResources().getColor(R.color.text_grey));
						mHolder.collectHum.setTextColor(context.getResources().getColor(R.color.text_grey));
						mHolder.collectLight.setTextColor(context.getResources().getColor(R.color.text_grey));
						mHolder.electrialiv.setVisibility(View.GONE);
						mHolder.xinhaoempty.setVisibility(View.VISIBLE);
					}
					
				}
			}
			else
			{
				mInflater = LayoutInflater.from(context);
				convertView = mInflater.inflate(R.layout.main_fragment_farmempty_listlitem, null);
				HashMap<String, String> collectMap = collectDao.getCollectById(map.get(TablesColumns.TABLETIMELINE_COLLECTOR).toString());
				if(collectMap!=null && collectMap.keySet().size()>0)
				{
					String name = collectMap.get(TablesColumns.TABLECOLLECTOR_NAME);
					if(name!=null && !name.equals("null"))
					{
						((TextView)convertView.findViewById(R.id.emptycollectname)).setText(name);
						
					}
					((TextView)convertView.findViewById(R.id.emptycollectname)).setTag(map.get(TablesColumns.TABLETIMELINE_COLLECTOR).toString());
					if(collectMap.get(TablesColumns.TABLECOLLECTOR_CATEGORY).equals("2"))
					{
						((ImageView)convertView.findViewById(R.id.emptycollect_iv)).setImageResource(R.drawable.chazi_default);
					}
					else if(collectMap.get(TablesColumns.TABLECOLLECTOR_CATEGORY).equals("1"))
					{
						((ImageView)convertView.findViewById(R.id.emptycollect_iv)).setImageResource(R.drawable.hezi_default);
					}
				}
			}
				
			return convertView;
		}

	 
	
		class CollectsItemViewHolder {
			private ImageView electrialiv,xinhaoempty;
			
			private TextView colectName,collectTem,collectHum,collectLight,hum_soil,tem_soil,light_type,tem_type,hum_type;
		}

}
