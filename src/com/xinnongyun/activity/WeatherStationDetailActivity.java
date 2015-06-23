package com.xinnongyun.activity;

import java.text.DateFormat;
import java.text.DecimalFormat;
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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.chart.LineChartActivityColored;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.net.NetConnectionTimeLine;
import com.xinnongyun.net.NetConnectionTimeLine.TimeLineSuccessCallBackGetAll;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.sqlite.WeatherStationDaoDBManager;
import com.xinnongyun.utils.StringUtil;
import com.xinnongyun.view.IconFontTextView;

/**
 * 环境小站详情
 * @author sm
 *
 */
public class WeatherStationDetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather_station_detail);
		SpannableString sp1 = new SpannableString("PM2.5");
		sp1.setSpan(new RelativeSizeSpan(0.5f), 2, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp1.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 2, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //粗体
		((TextView)findViewById(R.id.pm25a)).setText(sp1);
		((TextView)findViewById(R.id.today_pm25)).setText(sp1);
		SpannableString sp2 = new SpannableString("PM10");
		sp2.setSpan(new RelativeSizeSpan(0.5f), 2, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp2.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 2, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //粗体
		((TextView)findViewById(R.id.pm10a)).setText(sp2);
		
		SpannableString sp3 = new SpannableString("PM1");
		sp3.setSpan(new RelativeSizeSpan(0.5f), 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp3.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //粗体
		((TextView)findViewById(R.id.pm1a)).setText(sp3);
		intialWeatherStationTimeLine();
	}

	private List<HashMap<String, Object>> timeLineData;
	private Dialog dg;
	
	private void getTimeLine(String weatherStationId)
	{
		
		String fromtime;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
		fromtime = StringUtil.dataChangeToT(formatter.format(new Date(System
				.currentTimeMillis())) + " 00:00");
		String toTime = StringUtil.getCurrentDataSALL().replace("年", "-").replace("月", "-").replace("日", "T").replace(" ", "");
		//获取时间线
		String tileLineWeather = "http://m.nnong.com/v2/api-weather-station/" + weatherStationId + "/timeline/?begin_at=" + fromtime + "&end_at=" + toTime;
		dg = MainFragment.createLoadingDialog(this);
		dg.show();
		NetConnectionTimeLine.TimeLineGetNetConnection(tileLineWeather, null, null,
				new TimeLineSuccessCallBackGetAll() {
					@Override
					public void onSuccess(final List<HashMap<String, Object>> result) {
										timeLineData = result;
										initialTimeLineView(timeLineData);
					}
				}
				);
	}
	
	
	
	
	
	List<Date[]> dates;
	List<double[]> values;
	private int chartCurrentPage = 13;
	int maxY=0,minY=0;
	private int selcecout = 0;
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
						
						try {
							if(selcecout==0)
							{
								 selcecout =1;
								 Collections.sort(timeData, new Comparator<Map<String,Object>>(){ 
							            @Override 
							            public int compare(Map<String, Object> map1, Map<String, Object> map2) { 
							            	Date a = StringUtil.stringConverToDate(StringUtil.changeSerTo(map1.get("created_at").toString()));
							            	Date b = StringUtil.stringConverToDate(StringUtil.changeSerTo(map2.get("created_at").toString()));
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
							            } 
							        }); 
							}
  
							dates = new ArrayList<Date[]>();
							values = new ArrayList<double[]>();
							
							
							String type = "";
							if (chartCurrentPage == 11) {
								type = "pm2_5";
							} else if (chartCurrentPage == 12) {
								type = "co2";
							} else if (chartCurrentPage == 13) {
								type = "tem";
							} else if (chartCurrentPage == 14) {
								type = "hum";
							} else if (chartCurrentPage == 15) {
								type = "illu";
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
												.get("created_at").toString());
									
									}
								}
								values.add(numvalues);
								dates.add(datas);
							}
							if(chartCurrentPage == 13 || chartCurrentPage == 14)
							{ 
								maxY += 5;
								minY -= 5;
							}else if(chartCurrentPage == 15 || chartCurrentPage == 11 || chartCurrentPage == 12)
							{
								maxY += maxY * 0.1;
								minY = (int)(minY -minY * 0.1);
							}
							
							charthandle.sendEmptyMessage(0);
						} catch (Exception e) {
						}
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
				if(chartWeatherStationll.getChildCount()==0)
				{
					chartWeatherStationll.addView(chart.onCreateView(WeatherStationDetailActivity.this,values,dates,minY,maxY));
				} else if(chartWeatherStationll.getChildCount()==1)
				{
					chartWeatherStationll.removeAllViews();
					chartWeatherStationll.addView(chart.onCreateView(WeatherStationDetailActivity.this,values,dates,minY,maxY));
				}
				if(dg!=null)
				{
					dg.dismiss();
				}
				
			}
		}
	};
	
	
	private LinearLayout chartWeatherStationll;
	private ImageView page1,page2,page3,page4,page5;
	private void intialWeatherStationTimeLine()
	{
		page1 = (ImageView) findViewById(R.id.page1);
		page2 = (ImageView) findViewById(R.id.page2);
		page3 = (ImageView) findViewById(R.id.page3);
		page4 = (ImageView) findViewById(R.id.page4);
		page5 = (ImageView) findViewById(R.id.page5);
		intitialClickType();
		findViewById(R.id.collectReturn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				}
				);
		findViewById(R.id.btn_collect_detail_setting).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent weatherIntent = new Intent();
						weatherIntent.setClass(WeatherStationDetailActivity.this, WeatherStationActivity.class);
						startActivityForResult(weatherIntent, 0);
					}
				}
				);
		
		chartWeatherStationll = (LinearLayout) findViewById(R.id.chartWeatherStationll);
		((IconFontTextView)findViewById(R.id.weather_station_detail_iv)).setText(String.valueOf(new char[]{0xe65c}));
		WeatherStationDaoDBManager stationDaoDBManager = new WeatherStationDaoDBManager(this);
		List<HashMap<String, String>> stations = stationDaoDBManager.getAllWeatherStation();
		if(stations!=null && stations.size()>0)
		{
			getTimeLine(stations.get(0).get("id"));
			((TextView)findViewById(R.id.collectdetailname)).setText(stations.get(0).get("name"));
//			NetConnectionWeatherStation.WeatherGetNetConnection(NetUrls.URL_WEATHER_STATIONSAVG30(stations.get(0).get("id")), null, null,
//					new WeatherSuccessCallBack() {
//						@Override
//						public void onSuccess(HashMap<String, Object> result) {
//							if(result!=null && result.keySet().contains("sunny_par"))
//							{
//								String humlevelStr = result.get("hum").toString();
//								String pm25levelStr = result.get("pm2_5").toString();
//								((TextView)findViewById(R.id.tableStationTimeLine_30_PM25)).setText(pm25levelStr);
//								((TextView)findViewById(R.id.tableStationTimeLine_30_HUM)).setText(humlevelStr);
//								((TextView)findViewById(R.id.tableStationTimeLine_30_SUNNYPAR)).setText(result.get("sunny_par").toString());
//								double humLevel = 9,pm25Level = 0;
//								try {
//									humLevel = Double.parseDouble(humlevelStr);
//								} catch (Exception e) {
//								}
//								try {
//									pm25Level = Double.parseDouble(pm25levelStr);
//								} catch (Exception e) {
//								}
//								if(humLevel<=1)
//								{
//									((TextView)findViewById(R.id.tableStationTimeLine_30_HUM_level)).setText("（非常湿润）");
//								}else if(humLevel<=8)
//								{
//									((TextView)findViewById(R.id.tableStationTimeLine_30_HUM_level)).setText("（湿润）");
//								}else if(humLevel<=15)
//								{
//									((TextView)findViewById(R.id.tableStationTimeLine_30_HUM_level)).setText("（舒适）");
//								}else if(humLevel<=30)
//								{
//									((TextView)findViewById(R.id.tableStationTimeLine_30_HUM_level)).setText("（干燥）");
//								}else if(humLevel>30)
//								{
//									((TextView)findViewById(R.id.tableStationTimeLine_30_HUM_level)).setText("（非常干燥）");
//								}
//								
//								if(pm25Level<=50)
//								{
//									((TextView)findViewById(R.id.tableStationTimeLine_30_PM25_level)).setText("（优）");
//								}else if(pm25Level<=100)
//								{
//									((TextView)findViewById(R.id.tableStationTimeLine_30_PM25_level)).setText("（良）");
//								}else if(pm25Level<=200)
//								{
//									((TextView)findViewById(R.id.tableStationTimeLine_30_PM25_level)).setText("（中）");
//								}else if(pm25Level>200)
//								{
//									((TextView)findViewById(R.id.tableStationTimeLine_30_PM25_level)).setText("（差）");
//								}
//								
//							}
//						}
//					});
			HashMap<String, String> timeStation = stationDaoDBManager.getWeatherStationLastUpdateTime(stations.get(0).get("id"));
			if(timeStation!=null && timeStation.keySet().size()>0)
			{
				try {
					try {
						((TextView)findViewById(R.id.tableStationTimeLine_TEM)).setText(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_TEM));
						((TextView)findViewById(R.id.tableStationTimeLine_HUM)).setText(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_HUM));
						((TextView)findViewById(R.id.tableStationTimeLine_ILLU)).setText("" + (int)Double.parseDouble(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_ILLU)));
						((TextView)findViewById(R.id.tableStationTimeLine_CO2)).setText("" + (int)Double.parseDouble(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_CO2)));
						((TextView)findViewById(R.id.tableStationTimeLine_PM25)).setText("" + (int)Double.parseDouble(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_PM25)));
						((TextView)findViewById(R.id.tableStationTimeLine_PM10)).setText("" + (int)Double.parseDouble(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_PM10)));
						((TextView)findViewById(R.id.tableStationTimeLine_PM1)).setText("" + (int)Double.parseDouble(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_PM1)));
					} catch (Exception e3) {
					}
					try {
						((TextView)findViewById(R.id.tableStationTimeLine_DEW)).setText("" + new DecimalFormat("#####0.0").format(Double.parseDouble(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_DEW))));
					} catch (Exception e2) {
						((TextView)findViewById(R.id.tableStationTimeLine_DEW)).setText(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_DEW));
					}
					((TextView)findViewById(R.id.tableStationTimeLine_PRES)).setText(new DecimalFormat("#####0.0").format(Double.parseDouble(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_PRES))/1000));
					((TextView)findViewById(R.id.weather_station_detail_location)).setText(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_LOCATION));
					try {
						((TextView)findViewById(R.id.haiba)).setText(StringUtil.getAlt(Double.parseDouble(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_ALT))));
					} catch (Exception e1) {
						((TextView)findViewById(R.id.haiba)).setText(StringUtil.getAlt(Double.parseDouble("0")));
					}
					try {
						((TextView)findViewById(R.id.weather_station_detail_lat_lng)).setText(
								"经纬度 " + timeStation.get(TablesColumns.TABLESTATIONTIMELINR_LAT)+"N,"+timeStation.get(TablesColumns.TABLESTATIONTIMELINR_LNG)+"E");
					} catch (Exception e1) {
						((TextView)findViewById(R.id.weather_station_detail_lat_lng)).setText(
								"经纬度 -N,-E");
					}
					
					try {
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date d2 = df.parse(StringUtil.changeSerTo(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_CREATEDAT)));
						if(StringUtil.miniteLength(new Date(), d2)<=YunTongXun.minuteLength)
						{
							((TextView)findViewById(R.id.collectRefreshTime)).setTextColor(getResources().getColor(R.color.text_grey));
							((TextView)findViewById(R.id.collectRefreshTime_unit)).setTextColor(getResources().getColor(R.color.text_grey));
							
						}
						String[] time = StringUtil.getTimeDiff(d2).split(":");
						((TextView)findViewById(R.id.collectRefreshTime)).setText(time[0]);
						((TextView)findViewById(R.id.collectRefreshTime_unit)).setText(time[1]);
					} catch (ParseException e) {
					}
					ProgressBar collectPowerPb = (ProgressBar) findViewById(R.id.progressBar1);
					String powerStation = timeStation.get(TablesColumns.TABLESTATIONTIMELINR_POWER);
					if(powerStation== null || powerStation.equals("null"))
					{
						powerStation = "0";
					}
					double powderNum = Double.parseDouble(powerStation);
					if(powderNum<=YunTongXun.powderLeft)
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
					collectPowerPb.setProgress((int)powderNum);
					ImageView head_weather_station_sign = (ImageView) findViewById(R.id.head_weather_station_sign);
					if(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_SIGNAL).equals("0"))
					{
						head_weather_station_sign.setBackgroundResource(R.drawable.xinhao_n);
					}else if(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_SIGNAL).equals("1"))
					{
						head_weather_station_sign.setBackgroundResource(R.drawable.xinhao_o);
					}else if(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_SIGNAL).equals("2"))
					{
						head_weather_station_sign.setBackgroundResource(R.drawable.xinhao_t);
					}else if(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_SIGNAL).equals("3"))
					{
						head_weather_station_sign.setBackgroundResource(R.drawable.xinhao_th);
					}else
					{
						head_weather_station_sign.setBackgroundResource(R.drawable.xinhao_f);
					}
				} catch (NumberFormatException e) {
				} catch (Exception e) {
				}
			}
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 10)
		{
			WeatherStationDaoDBManager stationDaoDBManager = new WeatherStationDaoDBManager(this);
			List<HashMap<String, String>> stations = stationDaoDBManager.getAllWeatherStation();
			if(stations!=null && stations.size()>0)
			{
				((TextView)findViewById(R.id.collectdetailname)).setText(stations.get(0).get("name"));
			}
		}
		if(resultCode == 20)
		{
			finish();
		}
	}
	
	private void intitialClickType()
	{
		findViewById(R.id.today_pm25).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						changeTypeSelect(11);
					}
				}
				);
		findViewById(R.id.today_coo).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						changeTypeSelect(12);
					}
				}
				);
		findViewById(R.id.today_tem).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						changeTypeSelect(13);
					}
				}
				);
		findViewById(R.id.today_hum).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						changeTypeSelect(14);
					}
				}
				);
		findViewById(R.id.today_illu).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						changeTypeSelect(15);
					}
				}
				);
	}
	private void changeTypeSelect(int selectType)
	{
		if(selectType == chartCurrentPage)
		{
		}else
		{
			dg = MainFragment.createLoadingDialog(this);
			dg.show();
			chartCurrentPage = selectType;
			page5.setVisibility(View.INVISIBLE);
			page4.setVisibility(View.INVISIBLE);
			page3.setVisibility(View.INVISIBLE);
			page2.setVisibility(View.INVISIBLE);
			page1.setVisibility(View.INVISIBLE);
			if(chartCurrentPage == 11)
			{
				page1.setVisibility(View.VISIBLE);
			}else if(chartCurrentPage == 12)
			{
				page2.setVisibility(View.VISIBLE);
			}else if(chartCurrentPage == 13)
			{
				page3.setVisibility(View.VISIBLE);
			}else if(chartCurrentPage == 14)
			{
				page4.setVisibility(View.VISIBLE);
			}else if(chartCurrentPage == 15)
			{
				page5.setVisibility(View.VISIBLE);
			}
			initialTimeLineView(timeLineData);
		}	
	}
	
	
}
