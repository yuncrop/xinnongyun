package com.xinnongyun.activity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.chart.LineChartActivityColored;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.net.NetConnectionNotice;
import com.xinnongyun.net.NetConnectionTimeLine;
import com.xinnongyun.net.NetConnectionTimeLine.TimeLineSuccessCallBackGetAll;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.CollectDaoDBManager;
import com.xinnongyun.sqlite.NoticeDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.sqlite.TimeLineDaoDBManager;
import com.xinnongyun.utils.StringUtil;

/**
 * 盒子详情
 * 
 * @author sm
 * 
 */
public class CollcetDetailActivity extends Activity implements OnClickListener {

	// 图标布局
	private LinearLayout chartLayout;
	private RelativeLayout farmingItem_listlitem_tem_rl,farmingItem_listlitem_hum_rl,farmingItem_listlitem_light_rl;
	private String collectId = "";
	// //1为盒子 2为叉子 1-2
	private String currentCollectType = "1";
	/**
	 *  设置chart当前界面标记 11-15
	 */
	private int chartCurrentPage = 11;
	/**
	 * 当前图形类型 1为时间线默认 2为k线
	 */
	private int currentChartType = 1;
	private CollectDaoDBManager collectDao;
	private TimeLineDaoDBManager timeLineDao;
	//private List<HashMap<String, String>> kLineData;
	private List<HashMap<String, Object>> timeLineData;
	private TextView collectName, collectSignal,collectpowertv,
			collectRefreshTime,collectRefreshTime_unit,collectTemDetail,collectHumDetail,collectLightDetail;
	private HashMap<String, String> collectInfo;
	private Button timeline_btn;//,kline_btn;
	private ProgressBar collectPowerPb;
	private ImageView collectSetting,collectReturn,collectsignaliv,page1,page2,page3,typeImage;
	List<Date[]> dates;
	List<double[]> values;
	int maxY=0,minY=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏ActionBar
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//		         WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		setContentView(R.layout.activity_cololect_detail);
		Intent intent = getIntent();
		if (intent != null) {
			collectId = intent
					.getStringExtra(TablesColumns.TABLETIMELINE_COLLECTOR);
			
			
			int notifyid = intent.getIntExtra("notify_id", 0);
			if(notifyid!=0)
			{
				NotificationManager manger = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
				manger.cancel(notifyid);
			}
			String notice_id = intent.getStringExtra(TablesColumns.TABLENOTICE_ID+"notice");
			if(notice_id!=null && notice_id.length()>0)
			{
				NoticeDaoDBManager daoDBManager = new NoticeDaoDBManager(CollcetDetailActivity.this);
				SimpleDateFormat sDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd hh:mm:ss");
				String date = sDateFormat.format(new java.util.Date());
				NetConnectionNotice.NoticePutNetConnection(
						NetUrls.getUpdateNotice(notice_id)
						,"read_at=" + date.replace(" ", "T")
						,"Token " + MySharePreference.getValueFromKey(CollcetDetailActivity.this,MySharePreference.ACCOUNTTOKEN), null);
				daoDBManager.changeFarmingBeingCollects(notice_id, date.replace(" ", "T"));
			}
		}
		
		initilaBasicView();
		dg = MainFragment.createLoadingDialog(this);
		dg.show();
		initialDatas();
	}

	
	
	/**
	 * 初始化数据
	 */
	private void initialDatas() {
		collectInfo = collectDao.getCollectById(collectId);
		if(collectInfo!=null && collectInfo.size()>=1)
		{
			String name = collectInfo.get(TablesColumns.TABLECOLLECTOR_NAME);
			if(name.equals("null"))
			{
				name = getResources().getString(R.string.farming_being_name_default);
			}
			collectName.setText(name);
			if (collectInfo.get(TablesColumns.TABLECOLLECTOR_CATEGORY).toString().equals("1")) {
				typeImage.setBackgroundResource(R.drawable.hezi_default);
			
			} else {
				findViewById(R.id.light_slide).setVisibility(View.GONE);
				typeImage.setBackgroundResource(R.drawable.chazi_default);
				farmingItem_listlitem_light_rl.setVisibility(View.GONE);
				((TextView)findViewById(R.id.tem_soil)).setText(R.string.tem_soil);
				((TextView)findViewById(R.id.hum_soil)).setText(R.string.hum_soil);
								chartCurrentPage = 14;
				page3.setVisibility(View.GONE);
			}
			currentCollectType = collectInfo.get(
					TablesColumns.TABLECOLLECTOR_CATEGORY).toString();
			HashMap<String, String> timeLineInfo = timeLineDao
					.getCollectLastUpdateTime(collectId);
			if(timeLineInfo!=null)
			{
				if(!timeLineInfo.get(TablesColumns.TABLETIMELINE_TEM).equals("null"))
					collectTemDetail.setText(timeLineInfo.get(TablesColumns.TABLETIMELINE_TEM));
				if(!timeLineInfo.get(TablesColumns.TABLETIMELINE_HUM).equals("null"))
					collectHumDetail.setText(timeLineInfo.get(TablesColumns.TABLETIMELINE_HUM));
				if(timeLineInfo.get(TablesColumns.TABLETIMELINE_ILLU)!=null && !timeLineInfo.get(TablesColumns.TABLETIMELINE_ILLU).equals("null"))
				{
					String illu = Double.parseDouble(timeLineInfo.get(TablesColumns.TABLETIMELINE_ILLU)) <100 ? 
							timeLineInfo.get(TablesColumns.TABLETIMELINE_ILLU): (int)Double.parseDouble(timeLineInfo.get(TablesColumns.TABLETIMELINE_ILLU))+"";
							collectLightDetail.setText(illu);
				}
					
				if(chartCurrentPage == 14)
				{
					if(!timeLineInfo.get(TablesColumns.TABLETIMELINE_SOIL_TEM).equals("null"))
						collectTemDetail.setText(timeLineInfo.get(TablesColumns.TABLETIMELINE_SOIL_TEM));
					if(!timeLineInfo.get(TablesColumns.TABLETIMELINE_SOIL_HUM).equals("null"))
						collectHumDetail.setText(timeLineInfo.get(TablesColumns.TABLETIMELINE_SOIL_HUM));
				}
				collectpowertv.setText(timeLineInfo.get(TablesColumns.TABLETIMELINE_POWDER)+"%");
				int powderNum = Integer.parseInt(timeLineInfo.get(TablesColumns.TABLETIMELINE_POWDER));
				if(Integer.parseInt(timeLineInfo.get(TablesColumns.TABLETIMELINE_POWDER))<=YunTongXun.powderLeft)
				{
					powderNum += 5;
					collectPowerPb.setBackgroundResource(R.drawable.dianliang_shao);
				    collectPowerPb.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_dianchi_low));
				}else
				{
					if(powderNum<25)
					{
						powderNum +=3;
					}
					else
					powderNum = powderNum/15 *13;
					collectPowerPb.setBackgroundResource(R.drawable.dianliang_duo);
					collectPowerPb.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_dianchi));
				}
				
				collectPowerPb.setProgress(powderNum);
				
				if(timeLineInfo.get(TablesColumns.TABLETIMELINE_SIGNAL).equals("0"))
				{
					collectSignal.setText("无");
					collectsignaliv.setBackgroundResource(R.drawable.xinhao_n);
				}else if(timeLineInfo.get(TablesColumns.TABLETIMELINE_SIGNAL).equals("1"))
				{
					collectSignal.setText("弱");
					collectsignaliv.setBackgroundResource(R.drawable.xinhao_o);
				}else if(timeLineInfo.get(TablesColumns.TABLETIMELINE_SIGNAL).equals("2"))
				{
					collectSignal.setText("中");
					collectsignaliv.setBackgroundResource(R.drawable.xinhao_t);
				}else if(timeLineInfo.get(TablesColumns.TABLETIMELINE_SIGNAL).equals("3"))
				{
					collectSignal.setText("中");
					collectsignaliv.setBackgroundResource(R.drawable.xinhao_th);
				}else if(timeLineInfo.get(TablesColumns.TABLETIMELINE_SIGNAL).equals("4"))
				{
					collectSignal.setText("强");
					collectsignaliv.setBackgroundResource(R.drawable.xinhao_f);
				}

				
				try {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date d2 = df.parse(StringUtil.changeSerTo(timeLineInfo.get(TablesColumns.TABLETIMELINE_COLLECTEDAT)));
					if(StringUtil.miniteLength(new Date(), d2)<=YunTongXun.minuteLength)
					{
						collectRefreshTime.setTextColor(getResources().getColor(R.color.text_grey));
						collectRefreshTime_unit.setTextColor(getResources().getColor(R.color.text_grey));
					}
					
					String[] time = StringUtil.getTimeDiff(d2).split(":");
					collectRefreshTime.setText(time[0]);
					collectRefreshTime_unit.setText(time[1]);
				} catch (ParseException e) {
				}
			  // getTimeLine(collectId);
			}
			getTimeLine(collectId);
		}
		else
		{
			if(dg!=null)
			dg.dismiss();
			collectSetting.setClickable(false);
		}
	}

	private void changeTimeLineChart(int page) {
		if (page == chartCurrentPage) {
			return;
		}
		
		chartCurrentPage = page;
		changetPageBg();
		dg = MainFragment.createLoadingDialog(this);
		dg.show();
		if(chartLayout.getChildCount()==3)
		{
			chartLayout.removeViewAt(2);
			initialTimeLineView(timeLineData);
		}else if(chartLayout.getChildCount()==2)
		{
			initialTimeLineView(timeLineData);
		}
		
	}

//	/**
//	 * 更改K线
//	 * 
//	 * @param page
//	 */
//	private void changeKChart(int page) {
//		if (page == chartCurrentPage) {
//			return;
//		}
//		chartCurrentPage = page;
//		
//		if(chartLayout.getChildCount()==2)
//		{
//			initialKLineView(kLineData);
//		} else if(chartLayout.getChildCount()==3)
//		{
//			chartLayout.removeViewAt(2);
//			initialKLineView(kLineData);
//		}
//	}

	// 初始化基本控件
	private void initilaBasicView() {
		typeImage = (ImageView) findViewById(R.id.typeImage);
		page1 = (ImageView) findViewById(R.id.page1);
		page2 = (ImageView) findViewById(R.id.page2);
		page3 = (ImageView) findViewById(R.id.page3);
		farmingItem_listlitem_tem_rl = (RelativeLayout) findViewById(R.id.farmingItem_listlitem_tem_rl);
		farmingItem_listlitem_tem_rl.setOnClickListener(this);
		farmingItem_listlitem_hum_rl = (RelativeLayout) findViewById(R.id.farmingItem_listlitem_hum_rl);
		farmingItem_listlitem_hum_rl.setOnClickListener(this);
		farmingItem_listlitem_light_rl = (RelativeLayout) findViewById(R.id.farmingItem_listlitem_light_rl);
		farmingItem_listlitem_light_rl.setOnClickListener(this);
		collectRefreshTime_unit = (TextView) findViewById(R.id.collectRefreshTime_unit);
		collectpowertv = (TextView) findViewById(R.id.collectpowertv);
		collectsignaliv = (ImageView) findViewById(R.id.collectsignaliv);
		collectReturn = (ImageView) findViewById(R.id.collectReturn);
		collectReturn.setOnClickListener(this);
		collectTemDetail = (TextView) findViewById(R.id.collectdetail_tem);
		collectHumDetail = (TextView) findViewById(R.id.collectdetail_hum);
		collectLightDetail = (TextView) findViewById(R.id.collectdetail_light);
		timeline_btn = (Button) findViewById(R.id.timeline_btn);
		timeline_btn.setOnClickListener(this);
		//kline_btn = (Button) findViewById(R.id.kline_btn);
		//kline_btn.setOnClickListener(this);
		collectName = (TextView) findViewById(R.id.collectdetailname);
		collectPowerPb = (ProgressBar) findViewById(R.id.progressBar1);
		collectSignal = (TextView) findViewById(R.id.collectsignaltv);
		collectRefreshTime = (TextView) findViewById(R.id.collectRefreshTime);
		collectDao = new CollectDaoDBManager(CollcetDetailActivity.this);
		timeLineDao = new TimeLineDaoDBManager(CollcetDetailActivity.this);
		chartLayout = (LinearLayout) findViewById(R.id.chartll);
		collectSetting = (ImageView) findViewById(R.id.btn_collect_detail_setting);
		
		
		collectSetting.setOnClickListener(this);
	}

	// 初始化时间线控件
	private Dialog dg;
	private void initialTimeLineView(final List<HashMap<String, Object>> timeData) {
		
		if(timeData ==null || timeData.size()==0)
		{
			if(dg!=null)
			{
				dg.dismiss();
			}
			return;
		}
		
		new Thread(
				new Runnable() {
					@Override
					public void run() {
					 Collections.sort(timeData, new Comparator<Map<String,Object>>(){ 
				            @Override 
				            public int compare(Map<String, Object> map1, Map<String, Object> map2) { 
				            	Date a = StringUtil.stringConverToDate(StringUtil.changeSerTo(map1.get(TablesColumns.TABLETIMELINE_COLLECTEDAT).toString()));
				            	Date b = StringUtil.stringConverToDate(StringUtil.changeSerTo(map2.get(TablesColumns.TABLETIMELINE_COLLECTEDAT).toString()));
				            	if(a.after(b))
				            	{
				            		return 1;
				            	}
				            	else if(a.before(b))
				            	{
				            		return -1;
				            	}else
				            	{
				            		return 0;
				            	}
				               // return map1.get(TablesColumns.TABLETIMELINE_COLLECTEDAT).toString().compareTo(map2.get(TablesColumns.TABLETIMELINE_COLLECTEDAT).toString());//按sort字段升序 
				            } 
				        }); 
						dates = new ArrayList<Date[]>();
						values = new ArrayList<double[]>();
						
						
						String type = "";
						if (chartCurrentPage == 11) {
							type = TablesColumns.TABLETIMELINE_TEM;
						} else if (chartCurrentPage == 12) {
							type = TablesColumns.TABLETIMELINE_HUM;
						} else if (chartCurrentPage == 13) {
							type = TablesColumns.TABLETIMELINE_ILLU;
						} else if (chartCurrentPage == 14) {
							type = TablesColumns.TABLETIMELINE_SOIL_TEM;
						} else if (chartCurrentPage == 15) {
							type = TablesColumns.TABLETIMELINE_SOIL_HUM;
						}
						int firstUse = 0;
						for (int i = 0; i < 1; i++) {
							int dataLength = timeData.size();
							Date[] datas = new Date[dataLength];
							double[] numvalues = new double[dataLength];
							for (int iData = 0; iData < dataLength; iData++) {
								HashMap<String, Object> map = timeData.get(iData);
								
								if (map.get(type)!=null && !"null".equals(map.get(type).toString()) ) {
									numvalues[iData] = Double.parseDouble(map.get(type).toString());
									if(firstUse ==0)
									{
										maxY = minY = (int)numvalues[iData];
										firstUse = 1;
									}
									maxY = (maxY < numvalues[iData])? (int)numvalues[iData]:maxY;
									minY = (minY > numvalues[iData])? (int)numvalues[iData]:minY;
									datas[iData] = StringUtil.stringConverToDate(map
											.get(TablesColumns.TABLETIMELINE_COLLECTEDAT).toString());
								
								}
							}
							values.add(numvalues);
							dates.add(datas);
						}
						
						if(chartCurrentPage == 11 || chartCurrentPage == 14 || chartCurrentPage == 12 || chartCurrentPage == 15)
						{
							maxY += 5;
							minY -= 5;
						}else if(chartCurrentPage == 13)
						{
							maxY += maxY * 0.1;
							minY = 0;
						}
						
						charthandle.sendEmptyMessage(0);
					}
				}
				).start();
	}

	
	private Handler charthandle = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			if(msg.what ==0)
			{
				LineChartActivityColored chart = new LineChartActivityColored();
				chartLayout.addView(chart.onCreateView(CollcetDetailActivity.this,values,dates,minY,maxY));
				if(dg!=null)
				{
					dg.dismiss();
				}
			}
			if(msg.what == 1)
			{		
			}
		}
	};
	

	

	

//	// 初始化k线
//	private void initialKLineView(final List<HashMap<String, String>> data) {
//
//		if(data ==null || data.size()==0)
//		{
//			return;
//		}
//		new Thread(
//				new Runnable() {
//					@Override
//					public void run() {
//							int YMaxValue = 0, YMinValue = 0, XMaxValue = 0, XMinValue = 0;
//					
//							final double[] minValues = new double[] { 0, 0, 0, 0, 0, 0, 0 };
//							final double[] maxValues = new double[] { 0, 0, 0, 0, 0, 0, 0 };
//							int length = data.size();
//							int distanceLength = minValues.length - length;
//							for (int i = 0; i < length; i++) {
//								if (chartCurrentPage == 11) {
//									if (i == 0) {
//										YMaxValue = (int) YunTongXun.maxTemDefault;
//										YMinValue = (int) YunTongXun.minTemDefault;
//										XMaxValue = 8;
//										XMinValue = 0;
//									}
//									if(!data.get(i).get(TablesColumns.TABLEKLINE_MINTEM).equals("null"))
//									minValues[distanceLength + length - 1 - i] = Double.parseDouble(data.get(i).get(
//											TablesColumns.TABLEKLINE_MINTEM));
//									if(!data.get(i).get(TablesColumns.TABLEKLINE_MAXTEM).equals("null"))
//									maxValues[distanceLength + length - 1 - i] = Double.parseDouble(data.get(i).get(
//											TablesColumns.TABLEKLINE_MAXTEM));
//								} else if (chartCurrentPage == 12) {
//									if (i == 0) {
//										YMaxValue = YunTongXun.maxHumDefault;
//										YMinValue = YunTongXun.minHumDefault;
//										XMaxValue = 8;
//										XMinValue = 0;
//									}
//									if(!data.get(i).get(TablesColumns.TABLEKLINE_MINHUM).equals("null"))
//									minValues[distanceLength + length - 1 - i] = Double.parseDouble(data.get(i).get(
//											TablesColumns.TABLEKLINE_MINHUM));
//									if(!data.get(i).get(TablesColumns.TABLEKLINE_MAXHUM).equals("null"))
//									maxValues[distanceLength + length - 1 - i] = Double.parseDouble(data.get(i).get(
//											TablesColumns.TABLEKLINE_MAXHUM));
//								} else if (chartCurrentPage == 13) {
//									if (i == 0) {
//										YMaxValue = (int) YunTongXun.maxLightDefault;
//										YMinValue = (int) YunTongXun.minLightDefault;
//										XMaxValue = 8;
//										XMinValue = 0;
//									}
//									if(!data.get(i).get(TablesColumns.TABLEKLINE_MINILLU).equals("null"))
//									minValues[distanceLength + length - 1 - i] = Double.parseDouble(data.get(i).get(
//											TablesColumns.TABLEKLINE_MINILLU));
//									if(!data.get(i).get(TablesColumns.TABLEKLINE_MAXILLU).equals("null"))
//									maxValues[distanceLength + length - 1 - i] = Double.parseDouble(data.get(i).get(
//											TablesColumns.TABLEKLINE_MAXILLU));
//								} else if (chartCurrentPage == 14) {
//									if (i == 0) {
//										YMaxValue = (int) YunTongXun.maxSoilTemDefault;
//										YMinValue = (int) YunTongXun.minSoilTemDefault;
//										XMaxValue = 8;
//										XMinValue = 0;
//									}
//									if(data.get(i).get(TablesColumns.TABLEKLINE_MIN_SOIL_TEM)!=null&& !data.get(i).get(TablesColumns.TABLEKLINE_MIN_SOIL_TEM).equals("null"))
//									minValues[distanceLength + length - 1 - i] = Double.parseDouble(data.get(i).get(
//											TablesColumns.TABLEKLINE_MIN_SOIL_TEM));
//									if(data.get(i).get(TablesColumns.TABLEKLINE_MAX_SOIL_TEM)!=null&& !data.get(i).get(TablesColumns.TABLEKLINE_MAX_SOIL_TEM).equals("null"))
//									maxValues[distanceLength + length - 1 - i] = Double.parseDouble(data.get(i).get(
//											TablesColumns.TABLEKLINE_MAX_SOIL_TEM));
//								} else if (chartCurrentPage == 15) {
//									if (i == 0) {
//										YMaxValue = YunTongXun.maxSoilHumDefault;
//										YMinValue = YunTongXun.minSoilHumDefault;
//										XMaxValue = 8;
//										XMinValue = 0;
//									}
//									if(data.get(i).get(TablesColumns.TABLEKLINE_MIN_SOIL_HUM)!=null && !data.get(i).get(TablesColumns.TABLEKLINE_MIN_SOIL_HUM).equals("null"))
//									minValues[distanceLength + length - 1 - i] = Double.parseDouble(data.get(i).get(
//											TablesColumns.TABLEKLINE_MIN_SOIL_HUM));
//									if(data.get(i).get(TablesColumns.TABLEKLINE_MAX_SOIL_HUM)!=null && !data.get(i).get(TablesColumns.TABLEKLINE_MAX_SOIL_HUM).equals("null"))
//									maxValues[distanceLength + length - 1 - i] = Double.parseDouble(data.get(i).get(
//											TablesColumns.TABLEKLINE_MAX_SOIL_HUM));
//								}
//							}
//							charthandle.sendEmptyMessage(1);
//					}
//				}
//				).start();
//		
//	}

	

	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		// 温度
		case R.id.farmingItem_listlitem_tem_rl:
			if (currentCollectType.equals("2")) {
				changeTimeLineChart(14);
			}
			else if (currentChartType == 1) {
				changeTimeLineChart(11);
			}
			else if (currentChartType == 2) {
				//changeKChart(11);
			}

			break;
		// 湿度
		case R.id.farmingItem_listlitem_hum_rl:
			if (currentCollectType.equals("2")) {
				changeTimeLineChart(15);
			}
			else if (currentChartType == 1) {
				changeTimeLineChart(12);
			}
			else if (currentChartType == 2) {
				//changeKChart(12);
			}

			break;
		case R.id.farmingItem_listlitem_light_rl:
			if (currentCollectType.equals("2")) {
				return;
			}
			else if (currentChartType == 1) {
				changeTimeLineChart(13);
			}
			break;
		// 设置
		case R.id.btn_collect_detail_setting:
			Intent intent = new Intent();
			Iterable<String> keys = collectInfo.keySet();
			intent.putExtra("type", "ModifyFarm");
			for (String name : keys) {
				intent.putExtra(name, collectInfo.get(name).toString());
			}
			intent.setClass(CollcetDetailActivity.this, BoxActivity.class);
			startActivityForResult(intent,10);
			break;
		case R.id.timeline_btn:
			if(currentChartType == 1)
			{
				return;
			}
			else
			{
				currentChartType = 1;
				timeline_btn.setBackgroundResource(R.drawable.collectdetail_slide_l);
				timeline_btn.setTextColor(getResources().getColor(R.color.text_green));
				//kline_btn.setBackgroundResource(R.drawable.collectdetail_rdefault);
				//kline_btn.setTextColor(getResources().getColor(R.color.text_white));
				if(chartLayout.getChildCount()==2)
				{
					initialTimeLineView(timeLineData);
				} else if(chartLayout.getChildCount()==3)
				{
					chartLayout.removeViewAt(2);
					initialTimeLineView(timeLineData);
				}
			}
			break;
//		case R.id.kline_btn:
//			if(currentChartType == 2)
//			{
//				return;
//			}
//			else
//			{
//				currentChartType = 2;
//				timeline_btn.setBackgroundResource(R.drawable.collectdetail_slide_ldefault);
//				timeline_btn.setTextColor(getResources().getColor(R.color.text_white));
//				kline_btn.setBackgroundResource(R.drawable.collectdetail_slide_r);
//				kline_btn.setTextColor(getResources().getColor(R.color.text_green));
//				if(chartLayout.getChildCount()==2)
//				{
//					initialKLineView(kLineData);
//				} else if(chartLayout.getChildCount()==3)
//				{
//					chartLayout.removeViewAt(2);
//					initialKLineView(kLineData);
//				}
//			}
//			break;
		case R.id.collectReturn:
			finish();
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 10 && resultCode == 10)
		{
			finish();
		}
	}
	
	private void changetPageBg()
	{
		page1.setVisibility(View.INVISIBLE);
		page2.setVisibility(View.INVISIBLE);
		
		if(chartCurrentPage>13)
		{
			page3.setVisibility(View.GONE);
		}
		else
		{
			page3.setVisibility(View.INVISIBLE);
		}
		if(chartCurrentPage == 11 || chartCurrentPage == 14)
		{
			page1.setVisibility(View.VISIBLE);
		}else if(chartCurrentPage == 12 || chartCurrentPage == 15)
		{
			page2.setVisibility(View.VISIBLE);
		}
		else if(chartCurrentPage == 13)
		{
			page3.setVisibility(View.VISIBLE);
		}
	}
	
	
	/**
	 * 获取时间线   并插入数据库
	 * @param collectId
	 * @param time
	 */
	private void getTimeLine(String collectId)
	{
		String fromtime;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
		fromtime = StringUtil.dataChangeToT(formatter.format(new Date(System
				.currentTimeMillis())) + " 00:00");
		//获取时间线
		String para = "collector=" + collectId + "&begin_at=" + fromtime;
		NetConnectionTimeLine.TimeLineGetNetConnection(NetUrls.URL_TIMELINE, para, null,
				new TimeLineSuccessCallBackGetAll() {
					@Override
					public void onSuccess(final List<HashMap<String, Object>> result) {
						
										timeLineData = result;
										initialTimeLineView(timeLineData);
					}
				}
				);
	}
	
}
