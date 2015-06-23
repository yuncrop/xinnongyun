package com.xinnongyun.atyfragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import com.xinnongyun.sqlite.BeingDaoDBManager;
import com.xinnongyun.sqlite.CollectDaoDBManager;
import com.xinnongyun.sqlite.FarmingDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.sqlite.TimeLineDaoDBManager;

public class CollectLastTimeLine {
	/**
	 * 获取farming列表信息
	 * @param context
	 * @param type 2动物  还是  1作物
	 * @return
	 */
	public static List<HashMap<String, Object>> getFarmingColectInfos(Context context,String type)
	{
		ArrayList<String> collectIds = new ArrayList<String>();
		List<HashMap<String, Object>> datas = new ArrayList<HashMap<String,Object>>();;
		BeingDaoDBManager beingDao = new BeingDaoDBManager(context);
		FarmingDaoDBManager farmingDao = new FarmingDaoDBManager(context);
		List<HashMap<String,String>> farmingInfos =  farmingDao.getAllFarming();
		TimeLineDaoDBManager timeLineDao = new TimeLineDaoDBManager(context);
		if(farmingInfos!=null && farmingInfos.size() > 0)
		{
			for(HashMap<String, String> map : farmingInfos) {
				HashMap<String, String> beinInfo = beingDao.getBeingById(map.get(TablesColumns.TABLEFARMING_BEING));
//				if(beinInfo!=null && beinInfo.get(TablesColumns.TABLEBEING_KINGDOM)!=null && beinInfo.get(TablesColumns.TABLEBEING_KINGDOM).toString().equals(type))
				if(beinInfo!=null && beinInfo.get(TablesColumns.TABLEBEING_KINGDOM)!=null)
				{
					HashMap<String, Object> newMap = new HashMap<String, Object>(map);
					newMap.put(TablesColumns.TABLEFARMING_BEING, beinInfo);
					String collectsRaw = map.get(TablesColumns.TABLEFARMING_COLLECTORS);
					if(collectsRaw.length() <= 2)
					{
						continue;
					}
					String collectsInfo = collectsRaw.substring(1, collectsRaw.length()-1);
					String collect[] = collectsInfo.split(",");
					List<HashMap<String, String>> colloects = new ArrayList<HashMap<String,String>>();
					if(collect!=null && collect.length >=1)
					{
						for(String collectId : collect)
						{
							collectIds.add(collectId);
							if(timeLineDao.getCollectLastUpdateTime(collectId)!=null)
							{
							   colloects.add(timeLineDao.getCollectLastUpdateTime(collectId));
							}else
							{
								HashMap<String, String> cMap = new HashMap<String, String>();
								cMap.put(TablesColumns.TABLETIMELINE_COLLECTOR, collectId);
								colloects.add(cMap);
							}
						}
						newMap.put(TablesColumns.TABLEFARMING_COLLECTORS, colloects);
						datas.add(newMap);
					}
				}
				
			}
		}
		
		CollectDaoDBManager collDao = new CollectDaoDBManager(context);
		List<HashMap<String, String>> collectsMap =collDao.getAllCollect();
		if(collectsMap!=null && collectsMap.size()>0)
		{
			HashMap<String, Object> newMap = new HashMap<String, Object>();
			List<HashMap<String, String>> colloects = new ArrayList<HashMap<String,String>>();
			for(HashMap<String, String> cmap : collectsMap)
			{
				if(!collectIds.contains(cmap.get(TablesColumns.TABLECOLLECTOR_ID)))
				{
					HashMap<String, String> cMap = new HashMap<String, String>();
					cMap.put("notused", "notused");
					cMap.put(TablesColumns.TABLETIMELINE_COLLECTOR, cmap.get(TablesColumns.TABLECOLLECTOR_ID));
					colloects.add(cMap);
//					if(timeLineDao.getCollectLastUpdateTime(cmap.get(TablesColumns.TABLECOLLECTOR_ID))!=null)
//					{
//					   colloects.add(timeLineDao.getCollectLastUpdateTime(cmap.get(TablesColumns.TABLECOLLECTOR_ID)));
//					}else
//					{
//						HashMap<String, String> cMap = new HashMap<String, String>();
//						cMap.put("notused", "notused");
//						cMap.put(TablesColumns.TABLETIMELINE_COLLECTOR, cmap.get(TablesColumns.TABLECOLLECTOR_ID));
//						colloects.add(cMap);
//					}
					
				}
			}
			if(colloects.size()>0)
			{
				newMap.put(TablesColumns.TABLEFARMING_COLLECTORS, colloects);
				datas.add(newMap);
			}
			
		}
		
		
		
		
		
		
		
		
		
		
		return datas;
	}
}
