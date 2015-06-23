package com.xinnongyun.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.chart.LineChartActivityColored;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.net.NetConnectionTimeLine;
import com.xinnongyun.net.NetConnectionTimeLine.TimeLineSuccessCallBackGetAll;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.utils.StringUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 风口今日曲线
 * 
 * @author sm
 * 
 */
public class BlowerTimeLineActivity extends Activity {

	private LinearLayout chartWeatherStationll;
	private String blowerId = "";
	private String blowerNum = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.robot_timeline);
		Intent intent = getIntent();
		blowerId = intent.getStringExtra("blowerId");
		blowerNum = intent.getStringExtra("blowerNum");
		((TextView)findViewById(R.id.collectdetailname)).setText("设备"+blowerNum);
		
		findViewById(R.id.collectReturn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				}
				);
		
		chartWeatherStationll = (LinearLayout) findViewById(R.id.chartWeatherStationll);
		getTimeLine(blowerId);
	}

	List<Date[]> dates;
	List<double[]> values;
	int maxY = 0, minY = 0;
	private Dialog dg;
	private List<HashMap<String, Object>> timeLineData;
	private Handler charthandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				LineChartActivityColored chart = new LineChartActivityColored();
				if (chartWeatherStationll.getChildCount() == 0) {
					chartWeatherStationll.addView(chart.onCreateView(
							BlowerTimeLineActivity.this, values, dates, minY,
							maxY));
				} else if (chartWeatherStationll.getChildCount() == 1) {
					chartWeatherStationll.removeAllViews();
					chartWeatherStationll.addView(chart.onCreateView(
							BlowerTimeLineActivity.this, values, dates, minY,
							maxY));
				}
				if (dg != null) {
					dg.dismiss();
				}

			}
		}
	};

	private void initialTimeLineView(
			final List<HashMap<String, Object>> timeData) {

		if (timeData == null || timeData.size() == 0) {
			if (dg != null) {
				dg.dismiss();
			}
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {

					Collections.sort(timeData,
							new Comparator<Map<String, Object>>() {
								@Override
								public int compare(Map<String, Object> map1,
										Map<String, Object> map2) {
									Date a = StringUtil
											.stringConverToDate(StringUtil
													.changeSerTo(map1.get(
															"created_at")
															.toString()));
									Date b = StringUtil
											.stringConverToDate(StringUtil
													.changeSerTo(map2.get(
															"created_at")
															.toString()));
									if (a.after(b)) {
										return 1;
									} else if (a.before(b)) {
										return -1;
									} else {
										return 0;
									}
								}
							});

					dates = new ArrayList<Date[]>();
					values = new ArrayList<double[]>();

					String type = TablesColumns.TABLEBBLOWERENV_TEM;
					int firstUse = 0;
					for (int i = 0; i < 1; i++) {
						int dataLength = timeData.size();
						Date[] datas = new Date[dataLength];
						double[] numvalues = new double[dataLength];
						for (int iData = 0; iData < dataLength; iData++) {
							HashMap<String, Object> map = timeData.get(iData);

							if (map.get(type) != null
									&& !"null".equals(map.get(type).toString())) {
								numvalues[iData] = Double.parseDouble(map.get(
										type).toString());
								if (firstUse == 0) {
									maxY = minY = (int) numvalues[iData];
									firstUse = 1;
								}
								maxY = (maxY < numvalues[iData]) ? (int) numvalues[iData]
										: maxY;
								minY = (minY > numvalues[iData]) ? (int) numvalues[iData]
										: minY;
								datas[iData] = StringUtil
										.stringConverToDate(map
												.get(TablesColumns.TABLEBBLOWERENV_CREATEDAT)
												.toString());

							}
						}
						values.add(numvalues);
						dates.add(datas);
					}

					maxY += 5;
					minY -= 5;

					charthandle.sendEmptyMessage(0);
				} catch (Exception e) {
				}
			}
		}).start();
	}

	private void getTimeLine(String blowerId) {

		String fromtime;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
		fromtime = StringUtil.dataChangeToT(formatter.format(new Date(System
				.currentTimeMillis())) + " 00:00");
		String toTime = StringUtil.getCurrentDataSALL().replace("年", "-")
				.replace("月", "-").replace("日", "T").replace(" ", "");
		// 获取时间线
		String tileLineWeather = NetUrls.getUnitOneDayInfo(blowerId, fromtime,
				toTime);
		dg = MainFragment.createLoadingDialog(this);
		dg.show();
		NetConnectionTimeLine.TimeLineGetNetConnection(tileLineWeather, null,
				null, new TimeLineSuccessCallBackGetAll() {
					@Override
					public void onSuccess(
							final List<HashMap<String, Object>> result) {
						timeLineData = result;
						initialTimeLineView(timeLineData);
					}
				});
	}

}
