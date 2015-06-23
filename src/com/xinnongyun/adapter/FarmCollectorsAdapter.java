package com.xinnongyun.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.simonvt.numberpicker.NumberPicker;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.xinnongyun.activity.CollcetDetailActivity;
import com.xinnongyun.activity.ErrorMarkToast;
import com.xinnongyun.activity.FarmingAlarmingSettingActivity;
import com.xinnongyun.activity.R;
import com.xinnongyun.activity.SelectBeingActivity;
import com.xinnongyun.atyfragment.CollectLastTimeLine;
import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.imageload.ImageDownloadHelper;
import com.xinnongyun.imageload.ImageDownloadHelper.OnImageDownloadListener;
import com.xinnongyun.net.NetConnectionFarming;
import com.xinnongyun.net.NetConnectionFarming.FarmingSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.FarmingDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.utils.StringUtil;

public class FarmCollectorsAdapter extends BaseAdapter {

	private List<String> openCloseId = new ArrayList<String>();
	public static int opencount=0;
	FarmingDaoDBManager farmingDao;
	public Handler collectListHandler = new Handler()
	{
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			setData((List<HashMap<String, Object>>) msg.obj);
			notifyDataSetChanged();
		}
	};
	
	
	private LayoutInflater mInflater;
	private FarmCollectListAdapter mAdapter;
	private Context context;
    List<HashMap<String, Object>> collectorsData;
    public FarmCollectorsAdapter(final Context context,List<HashMap<String, Object>> collectorsData)
    {
    	this.context = context;
    	farmingDao = new FarmingDaoDBManager(context);
    	this.collectorsData = collectorsData;
    	if(opencount == 0)
    	{
    		opencount ++;
	    	new Thread(
					new Runnable() {
						@Override
						public void run() {
							while(MainFragment.collectUpdateList)
							{
								SystemClock.sleep(60 * 1000);
								Message msg = collectListHandler.obtainMessage();
								msg.obj = CollectLastTimeLine.getFarmingColectInfos(context,MainFragment.beingType);
								collectListHandler.sendMessage(msg);
							}
						}
					}
					).start();
    	}
    	
    }
    
    
    public void  resetData(List<HashMap<String, Object>> collectorsData)
    {
    	this.collectorsData = collectorsData;
    	notifyDataSetChanged();
    }
    
    public void setData(List<HashMap<String, Object>> collectorsData)
    {
    	this.collectorsData = collectorsData;
    }
    
	@Override
	public int getCount() {
		return collectorsData.size();
	}

	@Override
	public Object getItem(int arg0) {
		return collectorsData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	private Dialog dg;
	
	@SuppressWarnings("unchecked")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		 final CollectsViewHolder mHolder;
		if (convertView == null) {
			mHolder = new CollectsViewHolder();
			// 热度.Item
			mInflater = LayoutInflater.from(context);
			convertView = mInflater.inflate(R.layout.main_fragment_farm_item, null);
			
			//mHolder.farmbeingHead = (RelativeLayout) convertView.findViewById(R.id.farm_item_being);
			mHolder.beingName = (TextView) convertView
					.findViewById(R.id.farm_item_beingname);
			mHolder.beingMarturing = (TextView) convertView
					.findViewById(R.id.farm_item_beingmarturingat);
			mHolder.beingMarturSpeed = (TextView) convertView
					.findViewById(R.id.farm_item_beingimagemarturing);
			mHolder.beingImage = (ImageView) convertView
					.findViewById(R.id.farm_item_beingimage);
			mHolder.collectList = (MyListView) convertView
					.findViewById(R.id.farm_item_collectlist);
			mHolder.setAlarming = (LinearLayout) convertView
					.findViewById(R.id.farmingbeing_setAlarming_btn_ll);
			mHolder.farm_item_beingimagemarturing_rl_rl = (RelativeLayout) convertView.findViewById(R.id.farm_item_beingimagemarturing_rl_rl);
			mHolder.setHead = (LinearLayout) convertView
					.findViewById(R.id.farmingbeing_setHead_btn_ll);
			mHolder.modifyMaturing = (LinearLayout) convertView
					.findViewById(R.id.farmingbeing_modifyMaturingAt_btn_ll);
			mHolder.farminglitemSettingll = (LinearLayout) convertView
					.findViewById(R.id.farminglitemSettingll);
			mHolder.farm_item_being_rl = (RelativeLayout) convertView.findViewById(R.id.farm_item_being_rl);
			mHolder.openClose = (TextView) convertView.findViewById(R.id.farmingbeing_openclose);
			convertView.setTag(mHolder);
		} else {
			mHolder = (CollectsViewHolder) convertView.getTag();
		}
		
		if((position!=(getCount()-1)) || collectorsData.get(position).keySet().contains(TablesColumns.TABLEFARMING_ID))
		{
			if(openCloseId.contains(collectorsData.get(position).get(TablesColumns.TABLEFARMING_ID).toString()))
			{
				mHolder.farminglitemSettingll.setVisibility(View.VISIBLE);
			}
			else
			{
				mHolder.farminglitemSettingll.setVisibility(View.GONE);
			}
			
			mHolder.farm_item_beingimagemarturing_rl_rl.setVisibility(View.VISIBLE);
			mHolder.openClose.setVisibility(View.VISIBLE);
			mHolder.setAlarming.setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							Intent intent = new Intent();
							intent.setClass(context, FarmingAlarmingSettingActivity.class);
							intent.putExtra(TablesColumns.TABLEFARMING_ID,  collectorsData.get(position).get(TablesColumns.TABLEFARMING_ID).toString());
							context.startActivity(intent);
						}
					}
					);
			final String farmingid = collectorsData.get(position).get(TablesColumns.TABLEFARMING_ID).toString();
			final String farmingType = collectorsData.get(position).get(TablesColumns.TABLECOLLECTOR_SETTOHEAD).toString();
			if(farmingType.equals("100"))
			{
				((TextView)((mHolder.setHead).findViewById(R.id.farmingbeing_setHead_tv))).setText("取消置顶");
			}
			else
			{
				((TextView)((mHolder.setHead).findViewById(R.id.farmingbeing_setHead_tv))).setText("设置置顶");
			}
			mHolder.setHead.setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View view) {
//							HashMap<String, String> farmingMap = farmingDao.getFarmingInfoById(collectorsData.get(position).get(TablesColumns.TABLEFARMING_ID).toString());
//							String collectsInfo = farmingMap.get(TablesColumns.TABLEFARMING_COLLECTORS).substring(1, farmingMap.get(TablesColumns.TABLEFARMING_COLLECTORS).length()-1);
//							String collect[] = collectsInfo.split(",");
//							String paras = TablesColumns.TABLEFARMING_BEING + "=" + farmingMap.get(TablesColumns.TABLEFARMING_BEING);
//							for(String collceitem : collect)
//							{
//								paras += "&" + TablesColumns.TABLEFARMING_COLLECTORS + "=" + collceitem;
//							}
//							paras += "&" + TablesColumns.TABLEFARMING_FARM + "=" + farmingMap.get(TablesColumns.TABLEFARMING_FARM);
//							if(farmingType.equals(YunTongXun.order))
//							{
//								paras +="&" + TablesColumns.TABLECOLLECTOR_SETTOHEAD + "=" + YunTongXun.orderDefault;
//							}
//							else
//							{
//								paras +="&" + TablesColumns.TABLECOLLECTOR_SETTOHEAD + "=" + YunTongXun.order;
//							}
//							
							
							String paras = "";
							HashMap<String, String> farmingMap = farmingDao.getFarmingInfoById(collectorsData.get(position).get(TablesColumns.TABLEFARMING_ID).toString());
							if(farmingType.equals(YunTongXun.order))
							{
								paras += TablesColumns.TABLECOLLECTOR_SETTOHEAD + "=" + YunTongXun.orderDefault;
							}
							else
							{
								paras +=TablesColumns.TABLECOLLECTOR_SETTOHEAD + "=" + YunTongXun.order;
							}
							paras += "&" + TablesColumns.TABLEFARMING_FARM + "=" + farmingMap.get(TablesColumns.TABLEFARMING_FARM);
							dg = MainFragment.createLoadingDialog(context);
							dg.show();
							NetConnectionFarming.FarmingPutNetConnection(NetUrls.URL_FARMING_MODIFY(collectorsData.get(position).get(TablesColumns.TABLEFARMING_ID).toString()),
									paras,
									"Token "+MySharePreference.getValueFromKey(context, MySharePreference.ACCOUNTTOKEN),
									new FarmingSuccessCallBack() {
										@Override
										public void onSuccess(HashMap<String, Object> result) {
											dg.dismiss();
											if(result!=null)
											{
												if(result.keySet().contains("status_code"))
												{
													ErrorMarkToast.showCallBackToast(context, result);
													return;
												}
												
												if(farmingType.equals(YunTongXun.order))
												{
												    farmingDao.changeFarmingBeingToHead(farmingid, YunTongXun.orderDefault);
												}
												else
												{
													farmingDao.changeFarmingBeingToHead(farmingid, YunTongXun.order);
												}
												new Thread(
														new Runnable() {
															@Override
															public void run() {
																Message msg = collectListHandler.obtainMessage();
																msg.obj = CollectLastTimeLine.getFarmingColectInfos(context,MainFragment.beingType);
																collectListHandler.sendMessage(msg);
																}
															}
														).start();
											}
											else
											{
												showToast(R.string.interneterror);
											}
										}
									});
						}
					}
					);
			final HashMap<String, String> beingMap =  (HashMap<String, String>)collectorsData.get(position).get(TablesColumns.TABLEFARMING_BEING);
			mHolder.beingName.setText(beingMap.get(TablesColumns.TABLEBEING_NAME));
			mHolder.beingImage.setTag(beingMap.get(TablesColumns.TABLEBEING_IMAGE));
			if(beingMap.get(TablesColumns.TABLEBEING_KINGDOM).equals("2"))
			{
				mHolder.beingImage.setImageResource(R.drawable.animal_head);
			}
			else
			{
				mHolder.beingImage.setImageResource(R.drawable.zuowu_head);
			}
			if(!beingMap.get(TablesColumns.TABLEBEING_IMAGE).contains("default"))
			ImageDownloadHelper.getInstance().imageDownload(null,context, beingMap.get(TablesColumns.TABLEBEING_IMAGE), mHolder.beingImage,"/001yunhekeji100",
					new OnImageDownloadListener() {
						public void onImageDownload(Bitmap bitmap, String imgUrl) {
							if(!mHolder.beingImage.getTag().toString().equals("") && !mHolder.beingImage.getTag().toString().equals("null") && imgUrl.equals(mHolder.beingImage.getTag().toString())){
								if (bitmap != null) 
									  mHolder.beingImage.setImageBitmap(bitmap);
							}
						}
					});
			if(!collectorsData.get(position).get(TablesColumns.TABLEFARMING_MATURINGAT).toString().equals("null"))
			{
				mHolder.beingMarturSpeed.setText(StringUtil.getMouthDay(collectorsData.get(position).get(TablesColumns.TABLEFARMING_MATURINGAT).toString()));
			}
			else
			{
				mHolder.beingMarturSpeed.setText("未设置成熟日");
			}
			mHolder.beingMarturing.setText(context.getResources().getString(R.string.farming_itenm_maturingradio) +
					"40"+
					context.getResources().getString(R.string.farming_from_to_humunit));
	//		}else
	//		{
	//			mHolder.beingMarturSpeed.setText("");
	//			mHolder.beingMarturing.setText("");
	//		}
			
			List<HashMap<String, Object>> mList= ((List<HashMap<String, Object>>) collectorsData.get(position).get(TablesColumns.TABLEFARMING_COLLECTORS));
			mAdapter= new FarmCollectListAdapter(context, mList);
			mHolder.collectList.setAdapter(mAdapter);
			mHolder.collectList.setOnItemClickListener(
					new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> adapter, View arg1,
								int arg2, long arg3) {
								try {
									Intent intent = new Intent();
									intent.putExtra(TablesColumns.TABLETIMELINE_COLLECTOR, arg1.findViewById(R.id.farmingItem_listlitem_collectname).getTag().toString());
									intent.setClass(context, CollcetDetailActivity.class);
									context.startActivity(intent);
								} catch (Exception e) {
								}
						}
					}
					);
			
			
			mHolder.modifyMaturing.setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							final AlertDialog dlg = new AlertDialog.Builder(context).create();
							dlg.show();
							dlg.setCanceledOnTouchOutside(false);
							Window window = dlg.getWindow();
							window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
							window.setContentView(R.layout.dialog_beingmarturing_modify_maturingat);
							
							TextView textHead = (TextView) window.findViewById(R.id.dialog_head);
							textHead.setText(R.string.farming_maturingat_setting);
							final NumberPicker yearPicker = (NumberPicker) window.findViewById(R.id.farming_being_year);
							final NumberPicker mouthPicker = (NumberPicker) window.findViewById(R.id.farming_being_mouth);
							final NumberPicker dayPicker = (NumberPicker) window.findViewById(R.id.farming_being_day);
							DatePickerAdapter.yearMouthDay(yearPicker, mouthPicker, dayPicker);
							
							if(!collectorsData.get(position).get(TablesColumns.TABLEFARMING_MATURINGAT).toString().equals("null"))
							{
								String time = collectorsData.get(position).get(TablesColumns.TABLEFARMING_MATURINGAT).toString();
								yearPicker.setValue(Integer.parseInt(time.substring(0, time.indexOf("-"))));
								mouthPicker.setValue(Integer.parseInt(time.substring(time.indexOf("-")+1,time.lastIndexOf("-"))));
								dayPicker.setValue(Integer.parseInt(time.substring(time.lastIndexOf("-")+1,time.lastIndexOf("T"))));
							}
							else
							{
								yearPicker.setValue(StringUtil.getCurrentYear());
								mouthPicker.setValue(StringUtil.getCurrentMouth());
								dayPicker.setValue(StringUtil.getCurrentDay());
							}
							
							Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
							ok.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									dg = MainFragment.createLoadingDialog(context);
									dg.show();
									NetConnectionFarming.FarmingPutNetConnection(NetUrls.URL_FARMING_MODIFY(collectorsData.get(position).get(TablesColumns.TABLEFARMING_ID).toString()),
											initialParas(collectorsData.get(position).get(TablesColumns.TABLEFARMING_ID).toString(),
													yearPicker.getValue()+"-" + mouthPicker.getValue()+"-"+dayPicker.getValue()+"T00:00:00"),
											"Token "+MySharePreference.getValueFromKey(context, MySharePreference.ACCOUNTTOKEN),
											new FarmingSuccessCallBack() {
												@Override
												public void onSuccess(HashMap<String, Object> result) {
													dg.dismiss();
													if(result!=null)
													{
														if(result.keySet().contains("status_code"))
														{
															ErrorMarkToast.showCallBackToast(context, result);
															return;
														}
														
														farmingDao.changeMarturingAt(collectorsData.get(position).get(TablesColumns.TABLEFARMING_ID).toString(), 
																result.get(TablesColumns.TABLEFARMING_MATURINGAT).toString());
														new Thread(
																new Runnable() {
																	@Override
																	public void run() {
																		Message msg = collectListHandler.obtainMessage();
																		msg.obj = CollectLastTimeLine.getFarmingColectInfos(context,MainFragment.beingType);
																		collectListHandler.sendMessage(msg);
																		}
																	}
																).start();
														dlg.dismiss();
													}
													else
													{
														showToast(R.string.interneterror);
													}
												}
											});
								}
							});
							Button cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
							cancel.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									dlg.dismiss();
								}
							});
						}
					}
					);
			
			
			mHolder.farm_item_being_rl.setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							if(mHolder.farminglitemSettingll.getVisibility() == View.VISIBLE)
							{
								mHolder.farminglitemSettingll.setVisibility(View.GONE);
								openCloseId.remove(collectorsData.get(position).get(TablesColumns.TABLEFARMING_ID).toString());
							}
							else
							{
								openCloseId.add(collectorsData.get(position).get(TablesColumns.TABLEFARMING_ID).toString());
								mHolder.farminglitemSettingll.setVisibility(View.VISIBLE);
							}
						}
					}
					);
		}
		else
		{
			mHolder.farm_item_being_rl.setOnClickListener(null);
			mHolder.beingImage.setImageResource(R.drawable.box_head_empty);
			mHolder.farminglitemSettingll.setVisibility(View.GONE);
			mHolder.farm_item_beingimagemarturing_rl_rl.setVisibility(View.INVISIBLE);
			mHolder.beingName.setText(R.string.collect_not_used);
			mHolder.openClose.setVisibility(View.INVISIBLE);
			List<HashMap<String, Object>> mList= ((List<HashMap<String, Object>>) collectorsData.get(position).get(TablesColumns.TABLEFARMING_COLLECTORS));
			mAdapter= new FarmCollectListAdapter(context, mList);
			mHolder.collectList.setAdapter(mAdapter);
			mHolder.collectList.setOnItemClickListener(
					new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> adapter, View arg1,
								int arg2, long arg3) {
								try {
									Intent intent = new Intent();
									intent.putExtra(TablesColumns.TABLETIMELINE_COLLECTOR, arg1.findViewById(R.id.emptycollectname).getTag().toString());
									intent.setClass(context, SelectBeingActivity.class);
									context.startActivity(intent);
								} catch (Exception e) {
								}
						}
					}
					);
		}
		return convertView;
	}

	
	class CollectsViewHolder {
		private LinearLayout setHead,modifyMaturing,setAlarming;
		private TextView openClose;
		//private RelativeLayout farmbeingHead;
		private LinearLayout farminglitemSettingll;
		private TextView beingName,beingMarturing,beingMarturSpeed;
		private ImageView beingImage;
		private RelativeLayout farm_item_beingimagemarturing_rl_rl,farm_item_being_rl;
		private MyListView collectList;
	}
	
	//初始化参数
	private String  initialParas(String farmingId,String date)
	{
		FarmingDaoDBManager farmingDao = new FarmingDaoDBManager(context);
		HashMap<String, String> farmingMap = farmingDao.getFarmingInfoById(farmingId);
		String collectsInfo = farmingMap.get(TablesColumns.TABLEFARMING_COLLECTORS).substring(1, farmingMap.get(TablesColumns.TABLEFARMING_COLLECTORS).length()-1);
		String collect[] = collectsInfo.split(",");
		String paras = TablesColumns.TABLEFARMING_BEING + "=" + farmingMap.get(TablesColumns.TABLEFARMING_BEING);
		for(String collceitem : collect)
		{
			paras += "&" + TablesColumns.TABLEFARMING_COLLECTORS + "=" + collceitem;
		}
		paras += "&" + TablesColumns.TABLEFARMING_MATURINGAT + "=" + date;
		paras += "&" + TablesColumns.TABLEFARMING_FARM + "=" + farmingMap.get(TablesColumns.TABLEFARMING_FARM);
		return paras;
	}
	
	
	private void showToast(int id) {
		Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
	}
}
