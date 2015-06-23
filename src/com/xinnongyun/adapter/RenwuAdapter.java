package com.xinnongyun.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.sitech.oncon.barcode.core.CaptureActivity;
import com.videogo.constant.IntentConsts;
import com.videogo.openapi.bean.resp.CameraInfo;
import com.videogo.widget.SimpleRealPlayActivity;
import com.xinnongyun.activity.AppUserHelpActivity;
import com.xinnongyun.activity.BlowerDetailInfoSetting;
import com.xinnongyun.activity.CollcetDetailActivity;
import com.xinnongyun.activity.FarmAddCreateActicity;
import com.xinnongyun.activity.FarmConfigActivity;
import com.xinnongyun.activity.FarmingAlarmingSettingActivity;
import com.xinnongyun.activity.JournalVideoNotifyPlayActivity;
import com.xinnongyun.activity.MainActivity;
import com.xinnongyun.activity.R;
import com.xinnongyun.activity.VideoRecordActivity;
import com.xinnongyun.activity.WeatherStationDetailActivity;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.json.JsonUtil;
import com.xinnongyun.net.NetConnectionNotice;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.BeingDaoDBManager;
import com.xinnongyun.sqlite.BlowerDaoDBManager;
import com.xinnongyun.sqlite.BlowerTimeLineDaoDBManager;
import com.xinnongyun.sqlite.CameraDaoDBManager;
import com.xinnongyun.sqlite.ControlCenterDaoDBManager;
import com.xinnongyun.sqlite.NoticeDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.sqlite.WeatherStationDaoDBManager;
import com.xinnongyun.utils.StringUtil;
import com.xinnongyun.view.IconFontTextView;

public class RenwuAdapter extends BaseAdapter {

	private List<HashMap<String, Object>> listData;
	private Context contex;
	public RenwuAdapter(Context context,
			List<HashMap<String, Object>> listData) {
		this.listData = listData;
		this.contex = context;
		if(this.listData == null)
		{
			this.listData = new ArrayList<HashMap<String,Object>>();
		}
	}
	
	public void updateDate(List<HashMap<String, Object>> updaData)
	{
		this.listData = updaData;
		if(this.listData == null)
		{
			this.listData = new ArrayList<HashMap<String,Object>>();
		}
		notifyDataSetChanged();
	}
	
	public Handler renwuDataHandler = new Handler()
	{
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			updateDate((List<HashMap<String, Object>>) msg.obj);
		}
	};
	

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listData.get(arg0);
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public int getItemViewType(int position) {
		if(listData.get(position).containsKey(TablesColumns.TABLEVIDEOUPLOAD_STATE))
		{
			return 0;
		}else
		{
			return 1;
		}
	}
	
	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		
		if(listData.get(position).containsKey(TablesColumns.TABLEVIDEOUPLOAD_STATE))
		{
			if (view == null) {
				viewVideoUploadHolder = new ViewVideoHolder();
				view = LayoutInflater.from(contex).inflate(
						R.layout.main_fragment_renwu_videostate, null);
				viewVideoUploadHolder.tvStateProgress = (TextView) view.findViewById(R.id.renwu_videoitem_precent);
				viewVideoUploadHolder.ivHead = (IconFontTextView) view.findViewById(R.id.renwu_videoitem_iv);
				viewVideoUploadHolder.tvStateContent = (TextView) view.findViewById(R.id.renwu_videoitem_state);
				viewVideoUploadHolder.renwu_video_state_ll = (LinearLayout) view.findViewById(R.id.renwu_video_state_ll);
				viewVideoUploadHolder.video_upload_progress = (SeekBar) view.findViewById(R.id.video_upload_progress);
				viewVideoUploadHolder.tvStateTime = (TextView) view.findViewById(R.id.renwu_videoitem_time);
				view.setTag(viewVideoUploadHolder);
			}
			else
			{
				viewVideoUploadHolder = (ViewVideoHolder) view.getTag();
			}
			viewVideoUploadHolder.ivHead.setText(String.valueOf(new char[]{0xe61a}));
			viewVideoUploadHolder.video_upload_progress.setMax(100);
			viewVideoUploadHolder.video_upload_progress.setClickable(false);
			viewVideoUploadHolder.video_upload_progress.setEnabled(false);
			viewVideoUploadHolder.tvStateTime.setTextColor(contex.getResources().getColor(R.color.text_blue));
			viewVideoUploadHolder.tvStateContent.setTextColor(contex.getResources().getColor(R.color.text_black));
			if(listData.get(position).get(TablesColumns.TABLEVIDEOUPLOAD_STATE).equals("0"))
			{
				viewVideoUploadHolder.renwu_video_state_ll.setBackgroundResource(R.drawable.slidemenu_item_selector);
				viewVideoUploadHolder.tvStateContent.setText("准备上传");
				viewVideoUploadHolder.tvStateProgress.setText("0%");
				
			}else if(listData.get(position).get(TablesColumns.TABLEVIDEOUPLOAD_STATE).equals("1"))
			{
				viewVideoUploadHolder.renwu_video_state_ll.setBackgroundResource(R.drawable.slidemenu_item_selector);
				viewVideoUploadHolder.tvStateContent.setText("正在上传");
				
				int seekLength = (int)(100 * Double.parseDouble(listData.get(position).get(TablesColumns.TABLEVIDEOUPLOAD_PERCENT).toString()));
				viewVideoUploadHolder.video_upload_progress.setProgress(seekLength);
				viewVideoUploadHolder.tvStateProgress.setText(seekLength + "%");
			}else if(listData.get(position).get(TablesColumns.TABLEVIDEOUPLOAD_STATE).equals("2"))
			{
				viewVideoUploadHolder.tvStateTime.setTextColor(contex.getResources().getColor(R.color.text_999999));
				viewVideoUploadHolder.tvStateContent.setTextColor(contex.getResources().getColor(R.color.text_999999));
				viewVideoUploadHolder.renwu_video_state_ll.setBackgroundResource(R.drawable.slidemenu_item_selector_renwu);
				viewVideoUploadHolder.tvStateContent.setText("上传完成");
				viewVideoUploadHolder.tvStateProgress.setText("100%");
				viewVideoUploadHolder.video_upload_progress.setProgress(100);
			}
			String stateTime = listData.get(position).get(TablesColumns.TABLEVIDEOUPLOAD_CREATETIME).toString();
			viewVideoUploadHolder.tvStateTime.setText(StringUtil.getTimeFormatInfo(stateTime.replace("年", "-").replace("月", "-").replace("日", "")));
			
		}
		else
		{
			if (view == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(contex).inflate(
						R.layout.main_fragment_renwu_item, null);
				viewHolder.alertContent = (TextView) view.findViewById(R.id.renwu_item_alert);
				viewHolder.tvDate = (TextView) view.findViewById(R.id.renwu_litem_date);
				viewHolder.tvHead = (IconFontTextView) view.findViewById(R.id.renwu_item_iv);
				viewHolder.enwu_item_rl = (RelativeLayout) view.findViewById(R.id.enwu_item_rl);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			
			try {
				
				if(listData.get(position).keySet().contains(TablesColumns.TABLENOTICE_ALERT))
				{
					viewHolder.alertContent.setText(listData.get(position).get(TablesColumns.TABLENOTICE_ALERT).toString());
				}
				else
				{
					viewHolder.alertContent.setText(JsonUtil.getHashMap(listData.get(position).get("context").toString()).get("success_alert").toString() +
							JsonUtil.getHashMap(listData.get(position).get("context").toString()).get("failure_alert").toString());
				}
				
				
			} catch (Exception e) {
				viewHolder.alertContent.setText("");
			}
			if(listData.get(position).get(TablesColumns.TABLENOTICE_CREATEDAT)!=null)
			viewHolder.tvDate.setText(StringUtil.getTimeFormatInfo(listData.get(position).get(TablesColumns.TABLENOTICE_CREATEDAT).toString()));
			viewHolder.tvHead.setText(String.valueOf(new char[]{0xe61f}));
			viewHolder.tvHead.setTextColor(Color.parseColor("#a5a5a5"));
			if(listData.get(position).get(TablesColumns.TABLENOTICE_READAT)!=null && !listData.get(position).get(TablesColumns.TABLENOTICE_READAT).toString().equals("null"))
			{
				viewHolder.tvHead.setText(String.valueOf(new char[]{0xe617}));
				viewHolder.alertContent.setTextColor(contex.getResources().getColor(R.color.text_999999));
				viewHolder.enwu_item_rl.setBackgroundResource(R.drawable.slidemenu_item_selector_renwu);
				viewHolder.tvDate.setTextColor(contex.getResources().getColor(R.color.text_999999));
				
				
				//TODO 安装中控
				if(listData.get(position).get("subject").toString().equals("cc") && listData.get(position).get("signal").toString().equals("added"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe663}));
				}
				
				//TODO 修改中控
				if(listData.get(position).get("subject").toString().equals("cc") && listData.get(position).get("signal").toString().equals("updated"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe663}));
				}
				
				//TODO 卸载中控
				if(listData.get(position).get("subject").toString().equals("cc") && listData.get(position).get("signal").toString().equals("deleted"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe663}));
				}
				
				//TODO 中控离线
				if(listData.get(position).get("subject").toString().equals("cc/offline") && listData.get(position).get("signal").toString().equals("created"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe65d}));
				}
				
				//TODO 中控离线
				if(listData.get(position).get("subject").toString().equals("cc/exception") && listData.get(position).get("signal").toString().equals("created"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe618}));
				}
				
				//TODO 修改放风机参数
				if(listData.get(position).get("subject").toString().equals("blower/conf") && listData.get(position).get("signal").toString().equals("updated"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe663}));
				}
				
				//TODO 移动风口结果
				if(listData.get(position).get("subject").toString().equals("blower/move") && listData.get(position).get("signal").toString().equals("updated"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe663}));
				}
				
				
				
				
				
				//TODO 农场添加摄像头
				if(listData.get(position).get("subject").toString().equals("camera") && listData.get(position).get("signal").toString().equals("added"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe65d}));
				}
				//TODO 农场修改摄像头
				if(listData.get(position).get("subject").toString().equals("camera") && listData.get(position).get("signal").toString().equals("updated"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe65d}));
				}
				//TODO 农场删除摄像头
				if(listData.get(position).get("subject").toString().equals("camera") && listData.get(position).get("signal").toString().equals("deleted"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe65d}));
				}
				//TODO 农场摄像头下线
				if(listData.get(position).get("subject").toString().equals("camera") && listData.get(position).get("signal").toString().equals("offline"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe65d}));
				}
				
				//TODO 农场其他人发日记
				if(listData.get(position).get("subject").toString().equals("journal") && listData.get(position).get("signal").toString().equals("created"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe620}));
				}
				
				//TODO 最后一篇农场日记超过10天
				else if(listData.get(position).get("subject").toString().equals("journal") && listData.get(position).get("signal").toString().equals("tip"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe620}));
				}
				
				//TODO 添加环境小站
				else if(listData.get(position).get("subject").toString().equals("weather_station") && listData.get(position).get("signal").toString().equals("added"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe65c}));
				}
				
				//TODO 环境小站电量报警
				else if(listData.get(position).get("subject").toString().equals("weather_alarm") && listData.get(position).get("signal").toString().equals("created"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe618}));
				}
				
				//TODO 离线报警环境小站
				else if(listData.get(position).get("subject").toString().equals("weather_offline") && listData.get(position).get("signal").toString().equals("created"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe619}));
				}
				
				//TODO 修改环境小站
				else if(listData.get(position).get("subject").toString().equals("weather_station") && listData.get(position).get("signal").toString().equals("updated"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe65c}));
				}
				
				//TODO 删除环境小站
				else if(listData.get(position).get("subject").toString().equals("weather_station") && listData.get(position).get("signal").toString().equals("deleted"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe65c}));
				}
				//TODO 删除
				else if(listData.get(position).get("subject").toString().equals("journal") && listData.get(position).get("signal").toString().equals("comment"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe620}));
				}
				
				//TODO 新版本
				else if(listData.get(position).get("subject").toString().equals("version") && listData.get(position).get("signal").toString().equals("deleted"))
				{					
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe617}));
				}
				
				
				//TODO 系统通知   浏览器
				else if(listData.get(position).get("subject").toString().equals("notice") && listData.get(position).get("signal").toString().equals("created"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe617}));
				}
				
				//TODO 小站电量低
				else if(listData.get(position).get("subject").toString().equals("weather_alarm") && listData.get(position).get("signal").toString().equals("created"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe618}));
				}
				
				
				//TODO 小站某项指标超过报警值
				else if(listData.get(position).get("subject").toString().equals("weather_offline") && listData.get(position).get("signal").toString().equals("created"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe619}));
				}
				
				
				//TODO 电量低
				else if(listData.get(position).get("subject").toString().equals("alarm") && listData.get(position).get("signal").toString().equals("power"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe618}));
				}
				
				
				//TODO 某项指标超过报警值
				else if(listData.get(position).get("subject").toString().equals("alarm") && listData.get(position).get("signal").toString().equals("created"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe619}));
				}
				
				//TODO 意见反馈
				else if(listData.get(position).get("subject").toString().equals("feedback") && listData.get(position).get("signal").toString().equals("requested"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe617}));
				}
				
				//TODO 修改种养记录报警信息
				else if(listData.get(position).get("subject").toString().equals("farming") && listData.get(position).get("signal").toString().equals("updated-alarm"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe617}));
				}
				
				
				//TODO 修改种养纪录 成熟时间
				else if(listData.get(position).get("subject").toString().equals("farming") && listData.get(position).get("signal").toString().equals("updated-mature"))
				{
					BeingDaoDBManager beingDaoDBManager = new BeingDaoDBManager(contex);
					HashMap<String, String> map = beingDaoDBManager.getBeingById(JsonUtil.getHashMap(listData.get(position).get("context").toString()).get(TablesColumns.TABLEFARMING_BEING).toString());
					if(map!=null && map.keySet().size()>0)
					if(map.get(TablesColumns.TABLEBEING_KINGDOM).equals("1"))
					{
						viewHolder.tvHead.setText(String.valueOf(new char[]{0xe613}));
					}
					else
					{
						viewHolder.tvHead.setText(String.valueOf(new char[]{0xe616}));
					}
				}
				
				//TODO 新建种养纪录
				else if(listData.get(position).get("subject").toString().equals("farming") && listData.get(position).get("signal").toString().equals("created"))
				{
					BeingDaoDBManager beingDaoDBManager = new BeingDaoDBManager(contex);
					HashMap<String, String> map = beingDaoDBManager.getBeingById(JsonUtil.getHashMap(listData.get(position).get("context").toString()).get(TablesColumns.TABLEFARMING_BEING).toString());
					if(map!=null && map.keySet().size()>0)
					if(map.get(TablesColumns.TABLEBEING_KINGDOM).equals("1"))
					{
						viewHolder.tvHead.setText(String.valueOf(new char[]{0xe613}));
					}
					else
					{
						viewHolder.tvHead.setText(String.valueOf(new char[]{0xe616}));
					}
				}
				
				
				//TODO 删除采集器
				else if(listData.get(position).get("subject").toString().equals("collector") && listData.get(position).get("signal").toString().equals("deleted"))
				{
					if(JsonUtil.getHashMap(listData.get(position).get("context").toString()).get(TablesColumns.TABLECOLLECTOR_CATEGORY).toString().equals("1"))
					{
						viewHolder.tvHead.setText(String.valueOf(new char[]{0xe615}));
					}
					else
					{
						viewHolder.tvHead.setText(String.valueOf(new char[]{0xe614}));
					}
					
				}
				
				//TODO 修改采集器
				else if(listData.get(position).get("subject").toString().equals("collector") && listData.get(position).get("signal").toString().equals("updated"))
				{
					if(JsonUtil.getHashMap(listData.get(position).get("context").toString()).get(TablesColumns.TABLECOLLECTOR_CATEGORY).toString().equals("1"))
					{
						viewHolder.tvHead.setText(String.valueOf(new char[]{0xe615}));
					}
					else
					{
						viewHolder.tvHead.setText(String.valueOf(new char[]{0xe614}));
					}
				}
				
				//TODO 添加采集器
				else if(listData.get(position).get("subject").toString().equals("collector") && listData.get(position).get("signal").toString().equals("added"))
				{
					
					if(JsonUtil.getHashMap(listData.get(position).get("context").toString()).get(TablesColumns.TABLECOLLECTOR_CATEGORY).toString().equals("1"))
					{
						viewHolder.tvHead.setText(String.valueOf(new char[]{0xe615}));
					}
					else
					{
						viewHolder.tvHead.setText(String.valueOf(new char[]{0xe614}));
					}
				}
				
				
				//TODO 新建新品种
				else if(listData.get(position).get("subject").toString().equals("being") && listData.get(position).get("signal").toString().equals("created"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe617}));
				}
				
				
				//TODO 退出农场
				else if(listData.get(position).get("subject").toString().equals("farm") && listData.get(position).get("signal").toString().equals("departed"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe61f}));
				}
				
				//TODO 加入农场
				else if(listData.get(position).get("subject").toString().equals("farm") && listData.get(position).get("signal").toString().equals("joined"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe61f}));
				}
				
				//TODO 删除农场
				else if(listData.get(position).get("subject").toString().equals("farm") && listData.get(position).get("signal").toString().equals("deleted"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe61f}));
				}
				
				//TODO 修改农场
				else if(listData.get(position).get("subject").toString().equals("farm") && listData.get(position).get("signal").toString().equals("updated"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe61f}));
				}
				
				
				//TODO 新建农场
				else if(listData.get(position).get("subject").toString().equals("farm") && listData.get(position).get("signal").toString().equals("created"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe638}));
				}
				
				//TODO 新注册用户 头像
				else if(listData.get(position).get("subject").toString().equals("user") && listData.get(position).get("signal").toString().equals("edit-profile"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe612}));
				}
				//TODO 新注册用户 设置农场
				else if(listData.get(position).get("subject").toString().equals("user") && listData.get(position).get("signal").toString().equals("add-farm"))
				{
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe61f}));
				}
			}
			else
			{
				viewHolder.alertContent.setTextColor(contex.getResources().getColor(R.color.text_black));
				viewHolder.tvDate.setTextColor(contex.getResources().getColor(R.color.text_blue));
				viewHolder.enwu_item_rl.setBackgroundResource(R.drawable.slidemenu_item_selector);
				
				
				//TODO 安装中控
				if(listData.get(position).get("subject").toString().equals("cc") && listData.get(position).get("signal").toString().equals("added"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#edfe33"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe663}));
				}
				
				//TODO 修改中控
				if(listData.get(position).get("subject").toString().equals("cc") && listData.get(position).get("signal").toString().equals("updated"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#edfe33"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe663}));
				}
				
				//TODO 卸载中控
				if(listData.get(position).get("subject").toString().equals("cc") && listData.get(position).get("signal").toString().equals("deleted"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#edfe33"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe663}));
				}
				
				//TODO 中控离线
				if(listData.get(position).get("subject").toString().equals("cc/offline") && listData.get(position).get("signal").toString().equals("created"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#7ecdf4"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe65d}));
				}
				
				//TODO 中控离线
				if(listData.get(position).get("subject").toString().equals("cc/exception") && listData.get(position).get("signal").toString().equals("created"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#fe695d"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe618}));
				}
				
				//TODO 修改放风机参数
				if(listData.get(position).get("subject").toString().equals("blower/conf") && listData.get(position).get("signal").toString().equals("updated"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#edfe33"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe663}));
				}
				
				//TODO 移动风口结果
				if(listData.get(position).get("subject").toString().equals("blower/move") && listData.get(position).get("signal").toString().equals("updated"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#edfe33"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe663}));
				}
				
				
				
				
				
				
				
				
				//TODO 农场添加摄像头
				if(listData.get(position).get("subject").toString().equals("camera") && listData.get(position).get("signal").toString().equals("added"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#7ecdf4"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe65d}));
				}
				//TODO 农场修改摄像头
				if(listData.get(position).get("subject").toString().equals("camera") && listData.get(position).get("signal").toString().equals("updated"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#7ecdf4"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe65d}));
				}
				//TODO 农场删除摄像头
				if(listData.get(position).get("subject").toString().equals("camera") && listData.get(position).get("signal").toString().equals("deleted"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#7ecdf4"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe65d}));
				}
				//TODO 农场摄像头下线
				if(listData.get(position).get("subject").toString().equals("camera") && listData.get(position).get("signal").toString().equals("offline"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#7ecdf4"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe65d}));
				}
				
				
				//TODO 农场其他人发日记
				if(listData.get(position).get("subject").toString().equals("journal") && listData.get(position).get("signal").toString().equals("created"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#7ecdf4"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe620}));
				}
				
				//TODO 最后一篇农场日记超过10天
				else if(listData.get(position).get("subject").toString().equals("journal") && listData.get(position).get("signal").toString().equals("tip"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#7ecdf4"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe620}));
				}
				
				//TODO 添加环境小站
				else if(listData.get(position).get("subject").toString().equals("weather_station") && listData.get(position).get("signal").toString().equals("added"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#7ecdf4"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe65c}));
				}
				
				//TODO 修改环境小站
				else if(listData.get(position).get("subject").toString().equals("weather_station") && listData.get(position).get("signal").toString().equals("updated"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#7ecdf4"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe65c}));
				}
				
				//TODO 删除环境小站
				else if(listData.get(position).get("subject").toString().equals("weather_station") && listData.get(position).get("signal").toString().equals("deleted"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#7ecdf4"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe65c}));
				}
				
				//TODO 删除
				else if(listData.get(position).get("subject").toString().equals("journal") && listData.get(position).get("signal").toString().equals("deleted"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#7ecdf4"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe620}));
				}
				
				//TODO 新版本
				else if(listData.get(position).get("subject").toString().equals("version") && listData.get(position).get("signal").toString().equals("created"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#f6cc7f"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe617}));
				}
				
				
				//TODO 系统通知   浏览器
				else if(listData.get(position).get("subject").toString().equals("notice") && listData.get(position).get("signal").toString().equals("created"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#f6cc7f"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe617}));
				}
				
				
				//TODO 小站电量低
				else if(listData.get(position).get("subject").toString().equals("weather_alarm") && listData.get(position).get("signal").toString().equals("created"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#fe695d"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe618}));
				}
				
				
				//TODO 小站某项指标超过报警值
				else if(listData.get(position).get("subject").toString().equals("weather_offline") && listData.get(position).get("signal").toString().equals("created"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#fe695d"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe619}));
				}
				
				//TODO 电量低
				else if(listData.get(position).get("subject").toString().equals("alarm") && listData.get(position).get("signal").toString().equals("power"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#fe695d"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe618}));
				}
				
				
				//TODO 某项指标超过报警值
				else if(listData.get(position).get("subject").toString().equals("alarm") && listData.get(position).get("signal").toString().equals("created"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#fe695d"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe619}));
				}
				
				//TODO 意见反馈
				else if(listData.get(position).get("subject").toString().equals("feedback") && listData.get(position).get("signal").toString().equals("requested"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#f6cc7f"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe617}));
				}
				
				//TODO 修改种养记录报警信息
				else if(listData.get(position).get("subject").toString().equals("farming") && listData.get(position).get("signal").toString().equals("updated-alarm"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#f6cc7f"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe617}));
				}
				
				
				//TODO 修改种养纪录 成熟时间
				else if(listData.get(position).get("subject").toString().equals("farming") && listData.get(position).get("signal").toString().equals("updated-mature"))
				{
					BeingDaoDBManager beingDaoDBManager = new BeingDaoDBManager(contex);
					HashMap<String, String> map = beingDaoDBManager.getBeingById(JsonUtil.getHashMap(listData.get(position).get("context").toString()).get(TablesColumns.TABLEFARMING_BEING).toString());
					if(map!=null && map.keySet().size()>0)
					if(map.get(TablesColumns.TABLEBEING_KINGDOM).equals("1"))
					{
						viewHolder.tvHead.setTextColor(Color.parseColor("#a7da74"));
						viewHolder.tvHead.setText(String.valueOf(new char[]{0xe613}));
					}
					else
					{
						viewHolder.tvHead.setTextColor(Color.parseColor("#f6cc7f"));
						viewHolder.tvHead.setText(String.valueOf(new char[]{0xe616}));
					}
				}
				
				//TODO 新建种养纪录
				else if(listData.get(position).get("subject").toString().equals("farming") && listData.get(position).get("signal").toString().equals("created"))
				{
					BeingDaoDBManager beingDaoDBManager = new BeingDaoDBManager(contex);
					HashMap<String, String> map = beingDaoDBManager.getBeingById(JsonUtil.getHashMap(listData.get(position).get("context").toString()).get(TablesColumns.TABLEFARMING_BEING).toString());
					if(map!=null && map.keySet().size()>0)
					if(map.get(TablesColumns.TABLEBEING_KINGDOM).equals("1"))
					{
						viewHolder.tvHead.setTextColor(Color.parseColor("#a7da74"));
						viewHolder.tvHead.setText(String.valueOf(new char[]{0xe613}));
					}
					else
					{
						viewHolder.tvHead.setTextColor(Color.parseColor("#f6cc7f"));
						viewHolder.tvHead.setText(String.valueOf(new char[]{0xe616}));
					}
				}
				
				
				//TODO 删除采集器
				else if(listData.get(position).get("subject").toString().equals("collector") && listData.get(position).get("signal").toString().equals("deleted"))
				{
					if(JsonUtil.getHashMap(listData.get(position).get("context").toString()).get(TablesColumns.TABLECOLLECTOR_CATEGORY).toString().equals("1"))
					{
						viewHolder.tvHead.setTextColor(Color.parseColor("#7ecdf4"));
						viewHolder.tvHead.setText(String.valueOf(new char[]{0xe615}));
					}
					else
					{
						viewHolder.tvHead.setTextColor(Color.parseColor("#a7da74"));
						viewHolder.tvHead.setText(String.valueOf(new char[]{0xe614}));
					}
					
				}
				
				//TODO 修改采集器
				else if(listData.get(position).get("subject").toString().equals("collector") && listData.get(position).get("signal").toString().equals("updated"))
				{
					if(JsonUtil.getHashMap(listData.get(position).get("context").toString()).get(TablesColumns.TABLECOLLECTOR_CATEGORY).toString().equals("1"))
					{
						viewHolder.tvHead.setTextColor(Color.parseColor("#7ecdf4"));
						viewHolder.tvHead.setText(String.valueOf(new char[]{0xe615}));
					}
					else
					{
						viewHolder.tvHead.setTextColor(Color.parseColor("#a7da74"));
						viewHolder.tvHead.setText(String.valueOf(new char[]{0xe614}));
					}
				}
				
				//TODO 添加采集器
				else if(listData.get(position).get("subject").toString().equals("collector") && listData.get(position).get("signal").toString().equals("added"))
				{
					
					if(JsonUtil.getHashMap(listData.get(position).get("context").toString()).get(TablesColumns.TABLECOLLECTOR_CATEGORY).toString().equals("1"))
					{
						viewHolder.tvHead.setTextColor(Color.parseColor("#7ecdf4"));
						viewHolder.tvHead.setText(String.valueOf(new char[]{0xe615}));
					}
					else
					{
						viewHolder.tvHead.setTextColor(Color.parseColor("#a7da74"));
						viewHolder.tvHead.setText(String.valueOf(new char[]{0xe614}));
					}
				}
				
				
				//TODO 新建新品种
				else if(listData.get(position).get("subject").toString().equals("being") && listData.get(position).get("signal").toString().equals("created"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#f6cc7f"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe617}));
				}
				
				
				//TODO 退出农场
				else if(listData.get(position).get("subject").toString().equals("farm") && listData.get(position).get("signal").toString().equals("departed"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#a7da74"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe61f}));
				}
				
				//TODO 加入农场
				else if(listData.get(position).get("subject").toString().equals("farm") && listData.get(position).get("signal").toString().equals("joined"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#a7da74"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe61f}));
				}
				
				//TODO 删除农场
				else if(listData.get(position).get("subject").toString().equals("farm") && listData.get(position).get("signal").toString().equals("deleted"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#a7da74"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe61f}));
				}
				
				//TODO 修改农场
				else if(listData.get(position).get("subject").toString().equals("farm") && listData.get(position).get("signal").toString().equals("updated"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#a7da74"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe61f}));
				}
				
				
				//TODO 新建农场
				else if(listData.get(position).get("subject").toString().equals("farm") && listData.get(position).get("signal").toString().equals("created"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#7ecdf4"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe638}));
				}
				
				//TODO 新注册用户 头像
				else if(listData.get(position).get("subject").toString().equals("user") && listData.get(position).get("signal").toString().equals("edit-profile"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#77be30"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe612}));
				}
				//TODO 新注册用户 设置农场
				else if(listData.get(position).get("subject").toString().equals("user") && listData.get(position).get("signal").toString().equals("add-farm"))
				{
					viewHolder.tvHead.setTextColor(Color.parseColor("#a7da74"));
					viewHolder.tvHead.setText(String.valueOf(new char[]{0xe61f}));
				}
			}
			
			
			
			
			
			view.setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							
							
							try {
								
								
								
								//TODO 安装中控
								if(listData.get(position).get("subject").toString().equals("cc") && listData.get(position).get("signal").toString().equals("added"))
								{
									ControlCenterDaoDBManager cameraDaoDBManager = new ControlCenterDaoDBManager(contex);
									HashMap<String, String> cMap = cameraDaoDBManager.getControlCenterById(JsonUtil.getHashMap(listData.get(position).get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString());
									if(cMap!=null && cMap.keySet().size()>0)
									{
										BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(contex);
										List<HashMap<String, String>> bMaps = blowerDaoDBManager.getAllBlowersByCCId(cMap.get(TablesColumns.TABLECONTROLCENTER_ID).toString());
										String ccBlowerIds = "";
										BlowerTimeLineDaoDBManager blowerTimeLineDaoDBManager = new BlowerTimeLineDaoDBManager(contex);
										for(HashMap<String, String> map : bMaps)
										{
											HashMap<String, String>  bTMap = blowerTimeLineDaoDBManager.getCollectLastUpdateTime(map.get(TablesColumns.TABLEBLOWER_ID).toString());
											if(bTMap!=null && bTMap.size()>2)
											{
												ccBlowerIds += map.get(TablesColumns.TABLEBLOWER_ID) + " ";
											}
										}
										Intent intent = new Intent();
										intent.setClass(contex, BlowerDetailInfoSetting.class);
										intent.putExtra("id", cMap.get(TablesColumns.TABLECONTROLCENTER_ID).toString());
										intent.putExtra("ccName", cMap.get(TablesColumns.TABLECONTROLCENTER_NAME).toString());
										intent.putExtra("ccBlowerIds", ccBlowerIds.trim());
										intent.putExtra("serial", cMap.get(TablesColumns.TABLECONTROLCENTER_SERIAL).toString());
										contex.startActivity(intent);
									}
								}
								
								//TODO 修改中控
								if(listData.get(position).get("subject").toString().equals("cc") && listData.get(position).get("signal").toString().equals("updated"))
								{
									ControlCenterDaoDBManager cameraDaoDBManager = new ControlCenterDaoDBManager(contex);
									HashMap<String, String> cMap = cameraDaoDBManager.getControlCenterById(JsonUtil.getHashMap(listData.get(position).get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString());
									if(cMap!=null && cMap.keySet().size()>0)
									{
										BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(contex);
										List<HashMap<String, String>> bMaps = blowerDaoDBManager.getAllBlowersByCCId(cMap.get(TablesColumns.TABLECONTROLCENTER_ID).toString());
										String ccBlowerIds = "";
										BlowerTimeLineDaoDBManager blowerTimeLineDaoDBManager = new BlowerTimeLineDaoDBManager(contex);
										for(HashMap<String, String> map : bMaps)
										{
											HashMap<String, String>  bTMap = blowerTimeLineDaoDBManager.getCollectLastUpdateTime(map.get(TablesColumns.TABLEBLOWER_ID).toString());
											if(bTMap!=null && bTMap.size()>2)
											{
												ccBlowerIds += map.get(TablesColumns.TABLEBLOWER_ID) + " ";
											}
										}
										Intent intent = new Intent();
										intent.setClass(contex, BlowerDetailInfoSetting.class);
										intent.putExtra("id", cMap.get(TablesColumns.TABLECONTROLCENTER_ID).toString());
										intent.putExtra("ccName", cMap.get(TablesColumns.TABLECONTROLCENTER_NAME).toString());
										intent.putExtra("ccBlowerIds", ccBlowerIds.trim());
										intent.putExtra("serial", cMap.get(TablesColumns.TABLECONTROLCENTER_SERIAL).toString());
										contex.startActivity(intent);
									}
								}
								
								//TODO 中控离线
								if(listData.get(position).get("subject").toString().equals("cc/offline") && listData.get(position).get("signal").toString().equals("created"))
								{
									ControlCenterDaoDBManager cameraDaoDBManager = new ControlCenterDaoDBManager(contex);
									HashMap<String, String> cMap = cameraDaoDBManager.getControlCenterById(JsonUtil.getHashMap(listData.get(position).get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString());
									if(cMap!=null && cMap.keySet().size()>0)
									{
										BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(contex);
										List<HashMap<String, String>> bMaps = blowerDaoDBManager.getAllBlowersByCCId(cMap.get(TablesColumns.TABLECONTROLCENTER_ID).toString());
										String ccBlowerIds = "";
										BlowerTimeLineDaoDBManager blowerTimeLineDaoDBManager = new BlowerTimeLineDaoDBManager(contex);
										for(HashMap<String, String> map : bMaps)
										{
											HashMap<String, String>  bTMap = blowerTimeLineDaoDBManager.getCollectLastUpdateTime(map.get(TablesColumns.TABLEBLOWER_ID).toString());
											if(bTMap!=null && bTMap.size()>2)
											{
												ccBlowerIds += map.get(TablesColumns.TABLEBLOWER_ID) + " ";
											}
										}
										Intent intent = new Intent();
										intent.setClass(contex, BlowerDetailInfoSetting.class);
										intent.putExtra("id", cMap.get(TablesColumns.TABLECONTROLCENTER_ID).toString());
										intent.putExtra("ccName", cMap.get(TablesColumns.TABLECONTROLCENTER_NAME).toString());
										intent.putExtra("ccBlowerIds", ccBlowerIds.trim());
										intent.putExtra("serial", cMap.get(TablesColumns.TABLECONTROLCENTER_SERIAL).toString());
										contex.startActivity(intent);
									}
								}
								
								//TODO 中控异常
								if(listData.get(position).get("subject").toString().equals("cc/exception") && listData.get(position).get("signal").toString().equals("created"))
								{
									ControlCenterDaoDBManager cameraDaoDBManager = new ControlCenterDaoDBManager(contex);
									HashMap<String, String> cMap = cameraDaoDBManager.getControlCenterById(JsonUtil.getHashMap(listData.get(position).get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString());
									if(cMap!=null && cMap.keySet().size()>0)
									{
										BlowerDaoDBManager blowerDaoDBManager = new BlowerDaoDBManager(contex);
										List<HashMap<String, String>> bMaps = blowerDaoDBManager.getAllBlowersByCCId(cMap.get(TablesColumns.TABLECONTROLCENTER_ID).toString());
										String ccBlowerIds = "";
										BlowerTimeLineDaoDBManager blowerTimeLineDaoDBManager = new BlowerTimeLineDaoDBManager(contex);
										for(HashMap<String, String> map : bMaps)
										{
											HashMap<String, String>  bTMap = blowerTimeLineDaoDBManager.getCollectLastUpdateTime(map.get(TablesColumns.TABLEBLOWER_ID).toString());
											if(bTMap!=null && bTMap.size()>2)
											{
												ccBlowerIds += map.get(TablesColumns.TABLEBLOWER_ID) + " ";
											}
										}
										Intent intent = new Intent();
										intent.setClass(contex, BlowerDetailInfoSetting.class);
										intent.putExtra("id", cMap.get(TablesColumns.TABLECONTROLCENTER_ID).toString());
										intent.putExtra("ccName", cMap.get(TablesColumns.TABLECONTROLCENTER_NAME).toString());
										intent.putExtra("ccBlowerIds", ccBlowerIds.trim());
										intent.putExtra("serial", cMap.get(TablesColumns.TABLECONTROLCENTER_SERIAL).toString());
										contex.startActivity(intent);
									}
								}
								
//								//TODO 修改放风机参数
//								if(listData.get(position).get("subject").toString().equals("blower/conf") && listData.get(position).get("signal").toString().equals("updated"))
//								{
//									viewHolder.tvHead.setText(String.valueOf(new char[]{0xe663}));
//								}
//								
//								//TODO 移动风口结果
//								if(listData.get(position).get("subject").toString().equals("blower/move") && listData.get(position).get("signal").toString().equals("updated"))
//								{
//									viewHolder.tvHead.setText(String.valueOf(new char[]{0xe663}));
//								}
								
								
								
								
								
								//TODO 农场添加摄像头
								if(listData.get(position).get("subject").toString().equals("camera") && listData.get(position).get("signal").toString().equals("added"))
								{
									CameraDaoDBManager cameraDaoDBManager = new CameraDaoDBManager(contex);
									HashMap<String, String> cMap = cameraDaoDBManager.getCameraInfoById(JsonUtil.getHashMap(listData.get(position).get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString());
									if(cMap!=null && cMap.keySet().size()>0)
									{
										 CameraInfo cameraInfo = new CameraInfo();
										 cameraInfo.setCameraId(cMap.get(TablesColumns.TABLECAMERA_ID).toString());
										 cameraInfo.setCameraName(cMap.get(TablesColumns.TABLECAMERA_NAME).toString());
										 //cameraInfo.setCameraNo(1);
										 cameraInfo.setDeviceId(cMap.get(TablesColumns.TABLECAMERA_DEVICEID).toString());
										 cameraInfo.setIsEncrypt(0);
										 cameraInfo.setIsShared(0);
										 cameraInfo.setPicUrl(cMap.get(TablesColumns.TABLECAMERA_COVER_URL1).toString());
										 cameraInfo.setStatus(0);
										 Intent intent = new Intent(contex, SimpleRealPlayActivity.class);
										 intent.putExtra("share_url", cMap.get(TablesColumns.TABLECAMERA_SHARE_URL).toString());
										 intent.putExtra("isPublish", cMap.get(TablesColumns.TABLECAMERA_PUBLISH).toString());
							             intent.putExtra(IntentConsts.EXTRA_CAMERA_INFO, cameraInfo);
							             contex.startActivity(intent);
									}
									
								}
								//TODO 农场修改摄像头
								if(listData.get(position).get("subject").toString().equals("camera") && listData.get(position).get("signal").toString().equals("updated"))
								{
									CameraDaoDBManager cameraDaoDBManager = new CameraDaoDBManager(contex);
									HashMap<String, String> cMap = cameraDaoDBManager.getCameraInfoById(JsonUtil.getHashMap(listData.get(position).get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString());
									if(cMap!=null && cMap.keySet().size()>0)
									{
										 CameraInfo cameraInfo = new CameraInfo();
										 cameraInfo.setCameraId(cMap.get(TablesColumns.TABLECAMERA_ID).toString());
										 cameraInfo.setCameraName(cMap.get(TablesColumns.TABLECAMERA_NAME).toString());
										 //cameraInfo.setCameraNo(1);
										 cameraInfo.setDeviceId(cMap.get(TablesColumns.TABLECAMERA_DEVICEID).toString());
										 cameraInfo.setIsEncrypt(0);
										 cameraInfo.setIsShared(0);
										 cameraInfo.setPicUrl(cMap.get(TablesColumns.TABLECAMERA_COVER_URL1).toString());
										 cameraInfo.setStatus(0);
										 Intent intent = new Intent(contex, SimpleRealPlayActivity.class);
										 intent.putExtra("share_url", cMap.get(TablesColumns.TABLECAMERA_SHARE_URL).toString());
										 intent.putExtra("isPublish", cMap.get(TablesColumns.TABLECAMERA_PUBLISH).toString());
							             intent.putExtra(IntentConsts.EXTRA_CAMERA_INFO, cameraInfo);
							             contex.startActivity(intent);
									}
								}
								
								//TODO 农场其他人发日记
								if(listData.get(position).get("subject").toString().equals("journal") && listData.get(position).get("signal").toString().equals("view"))
								{
									//系统通知   任务
									Intent intent = new Intent();
									intent.putExtra("journalvideoid", JsonUtil.getHashMap(listData.get(position).get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString());
									intent.setClass(contex, JournalVideoNotifyPlayActivity.class);
									contex.startActivity(intent);
								}
								
								//TODO 农场其他人发日记
								if(listData.get(position).get("subject").toString().equals("journal") && listData.get(position).get("signal").toString().equals("created"))
								{
									//系统通知   任务
									Intent intent = new Intent();
									intent.putExtra("journalvideoid", JsonUtil.getHashMap(listData.get(position).get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString());
									intent.setClass(contex, JournalVideoNotifyPlayActivity.class);
									contex.startActivity(intent);
								}
								
								//TODO 最后一篇农场日记超过10天
								else if(listData.get(position).get("subject").toString().equals("journal") && listData.get(position).get("signal").toString().equals("tip"))
								{
									//系统通知   任务
									Intent recordIntent = new Intent();
									recordIntent.setClass(contex, VideoRecordActivity.class);
									contex.startActivity(recordIntent);
								}
								
								//TODO 新版本
								else if(listData.get(position).get("subject").toString().equals("version") && listData.get(position).get("signal").toString().equals("created"))
								{
									((MainActivity)contex).slideMenuFragment.updateVersion(1);
									//系统通知   任务
								
								}
								
								
								//TODO 系统通知   浏览器
								else if(listData.get(position).get("subject").toString().equals("notice") && listData.get(position).get("signal").toString().equals("created"))
								{
									if(JsonUtil.getHashMap(listData.get(position).get("context").toString()).get("url")!=null && !JsonUtil.getHashMap(listData.get(position).get("context").toString()).get("url").toString().equals("null"))
									{
										Intent intentFarmingAlarm = new Intent();
										intentFarmingAlarm.setClass(contex, AppUserHelpActivity.class);
										intentFarmingAlarm.putExtra(TablesColumns.TABLENOTICE_ID+"notice", listData.get(position).get(TablesColumns.TABLENOTICE_ID).toString());
										intentFarmingAlarm.putExtra("url_app",
												JsonUtil.getHashMap(listData.get(position).get("context").toString()).get("url").toString());
										contex.startActivity(intentFarmingAlarm);
									}
								
								}
								
								//TODO 添加环境小站
								else if(listData.get(position).get("subject").toString().equals("weather_station") && listData.get(position).get("signal").toString().equals("added"))
								{
									WeatherStationDaoDBManager stationDaoDBManager = new WeatherStationDaoDBManager(contex);
									List<HashMap<String, String>> stations = stationDaoDBManager.getAllWeatherStation();
									if(stations!=null && stations.size()>0)
									{
										Intent intentWeather = new Intent();
										intentWeather.setClass(contex, WeatherStationDetailActivity.class);
										contex.startActivity(intentWeather);
									}
								}
								
								//TODO 修改环境小站
								else if(listData.get(position).get("subject").toString().equals("weather_station") && listData.get(position).get("signal").toString().equals("updated"))
								{
									WeatherStationDaoDBManager stationDaoDBManager = new WeatherStationDaoDBManager(contex);
									List<HashMap<String, String>> stations = stationDaoDBManager.getAllWeatherStation();
									if(stations!=null && stations.size()>0)
									{
										Intent intentWeather = new Intent();
										intentWeather.setClass(contex, WeatherStationDetailActivity.class);
										contex.startActivity(intentWeather);
									}
								}
								
								//TODO 环境小站电量报警
								else if(listData.get(position).get("subject").toString().equals("weather_alarm") && listData.get(position).get("signal").toString().equals("created"))
								{
									WeatherStationDaoDBManager stationDaoDBManager = new WeatherStationDaoDBManager(contex);
									List<HashMap<String, String>> stations = stationDaoDBManager.getAllWeatherStation();
									if(stations!=null && stations.size()>0)
									{
										Intent intentWeather = new Intent();
										intentWeather.setClass(contex, WeatherStationDetailActivity.class);
										contex.startActivity(intentWeather);
									}
								}
								
								//TODO 离线报警环境小站
								else if(listData.get(position).get("subject").toString().equals("weather_offline") && listData.get(position).get("signal").toString().equals("created"))
								{
									WeatherStationDaoDBManager stationDaoDBManager = new WeatherStationDaoDBManager(contex);
									List<HashMap<String, String>> stations = stationDaoDBManager.getAllWeatherStation();
									if(stations!=null && stations.size()>0)
									{
										Intent intentWeather = new Intent();
										intentWeather.setClass(contex, WeatherStationDetailActivity.class);
										contex.startActivity(intentWeather);
									}
								}
								
								//TODO 某项指标超过报警值
								else if(listData.get(position).get("subject").toString().equals("alarm") && listData.get(position).get("signal").toString().equals("created"))
								{
									Intent intentCollect = new Intent();
									intentCollect.putExtra(TablesColumns.TABLETIMELINE_COLLECTOR, 
											JsonUtil.getHashMap(listData.get(position).get("context").toString()).get("collector_" + TablesColumns.TABLECOLLECTOR_ID).toString());
									intentCollect.setClass(contex, CollcetDetailActivity.class);
									contex.startActivity(intentCollect);
								
								}
								
								//TODO 意见反馈
								else if(listData.get(position).get("subject").toString().equals("feedback") && listData.get(position).get("signal").toString().equals("requested"))
								{
									((MainActivity)contex).slideMenuFragment.usersuggestion();
								}
								
								
								
								
								//TODO 修改种养记录报警信息
								else if(listData.get(position).get("subject").toString().equals("farming") && listData.get(position).get("signal").toString().equals("updated-alarm"))
								{
									Intent intentFarmingAlarm = new Intent();
									intentFarmingAlarm.setClass(contex, FarmingAlarmingSettingActivity.class);
									intentFarmingAlarm.putExtra(TablesColumns.TABLEFARMING_ID,
											JsonUtil.getHashMap(listData.get(position).get("context").toString()).get(TablesColumns.TABLEFARMING_ID).toString());
									contex.startActivity(intentFarmingAlarm);
									
								}
								
								//电池电量过低
								else if(listData.get(position).get("subject").toString().equals("alarm") && listData.get(position).get("signal").toString().equals("power"))
								{
									Intent intentCollect = new Intent();
									intentCollect.putExtra(TablesColumns.TABLETIMELINE_COLLECTOR, 
											JsonUtil.getHashMap(listData.get(position).get("context").toString()).get("collector_" + TablesColumns.TABLECOLLECTOR_ID).toString());
									intentCollect.setClass(contex, CollcetDetailActivity.class);
									contex.startActivity(intentCollect);
								}
								
								//TODO 修改种养纪录 成熟时间
								else if(listData.get(position).get("subject").toString().equals("farming") && listData.get(position).get("signal").toString().equals("updated-mature"))
								{
									//((MainActivity)contex).mainFragment.showMainFragmentPage(3);
								
								}
								
								//TODO 新建种养纪录
								else if(listData.get(position).get("subject").toString().equals("farming") && listData.get(position).get("signal").toString().equals("created"))
								{
									//((MainActivity)contex).mainFragment.showMainFragmentPage(3);
								
								}
								
								
								//TODO 修改采集器
								else if(listData.get(position).get("subject").toString().equals("collector") && listData.get(position).get("signal").toString().equals("updated"))
								{
									Intent intentCollect = new Intent();
									intentCollect.putExtra(TablesColumns.TABLETIMELINE_COLLECTOR, 
											JsonUtil.getHashMap(listData.get(position).get("context").toString()).get(TablesColumns.TABLECOLLECTOR_ID).toString());
									intentCollect.setClass(contex, CollcetDetailActivity.class);
									contex.startActivity(intentCollect);
									//系统通知   任务
									
								}
								
								//TODO 添加采集器
								else if(listData.get(position).get("subject").toString().equals("collector") && listData.get(position).get("signal").toString().equals("added"))
								{
									Intent intentCollect = new Intent();
									intentCollect.putExtra(TablesColumns.TABLETIMELINE_COLLECTOR, 
											JsonUtil.getHashMap(listData.get(position).get("context").toString()).get(TablesColumns.TABLECOLLECTOR_ID).toString());
									intentCollect.setClass(contex, CollcetDetailActivity.class);
									contex.startActivity(intentCollect);
									//系统通知   任务
								
								}
								//修改农场
								else if(listData.get(position).get("subject").toString().equals("farm") && listData.get(position).get("signal").toString().equals("updated"))
								{
									if (MySharePreference.getValueFromKey(contex, MySharePreference.FARMNAME).equals(
											MySharePreference.NOTSTATE))
									{
										Intent intent = new Intent();
										intent.setClass(contex, FarmAddCreateActicity.class);
										contex.startActivity(intent);
									}
									else
									{
										Intent intent = new Intent();
										intent.setClass(contex, FarmConfigActivity.class);
										contex.startActivity(intent);
									}
								}
								
								//TODO 新建农场
								else if(listData.get(position).get("subject").toString().equals("farm") && listData.get(position).get("signal").toString().equals("created"))
								{
									// 任务
									Intent captureIntent = new Intent(contex,
											CaptureActivity.class);
									captureIntent.putExtra(YunTongXun.CAPTURE_KEY,
											YunTongXun.CAPTURE_HANRDCONFIG);
									contex.startActivity(captureIntent);
								
								}
								
								//TODO 新注册用户 头像
								else if(listData.get(position).get("subject").toString().equals("user") && listData.get(position).get("signal").toString().equals("edit-profile"))
								{
									// 任务
									((MainActivity)contex).slideMenuFragment.userinfo();
								
								}
								//TODO 新注册用户 设置农场
								else if(listData.get(position).get("subject").toString().equals("user") && listData.get(position).get("signal").toString().equals("add-farm"))
								{
									if (MySharePreference.getValueFromKey(contex, MySharePreference.FARMNAME).equals(
											MySharePreference.NOTSTATE))
									{
										Intent intent = new Intent();
										intent.setClass(contex, FarmAddCreateActicity.class);
										contex.startActivity(intent);
									}
									else
									{
										Intent intent = new Intent();
										intent.setClass(contex, FarmConfigActivity.class);
										contex.startActivity(intent);
									}
									//任务
								}
								
								else if(listData.get(position).get(TablesColumns.TABLENOTICE_READAT)!=null && !listData.get(position).get(TablesColumns.TABLENOTICE_READAT).toString().equals("null"))
								{
									return;
								}
								NoticeDaoDBManager daoDBManager = new NoticeDaoDBManager(contex);
								SimpleDateFormat sDateFormat = new SimpleDateFormat(
										"yyyy-MM-dd hh:mm:ss");
								String date = sDateFormat.format(new java.util.Date());
								//PUT /api-notice/{id}/ -d 'read_at=2015-02-03T00:00:00' -H 'Authorization: Token 467c2b68aab26f151a7d200707f54ffb2a612222'
								NetConnectionNotice.NoticePutNetConnection(
										NetUrls.getUpdateNotice(listData.get(position).get(TablesColumns.TABLENOTICE_ID).toString())
										,"read_at=" + date.replace(" ", "T")
										,"Token " + MySharePreference.getValueFromKey(contex,MySharePreference.ACCOUNTTOKEN), null);
								daoDBManager.changeFarmingBeingCollects(listData.get(position).get(TablesColumns.TABLENOTICE_ID).toString(), date.replace(" ", "T"));
							} catch (Exception e) {
							}
							
						}
					}
					);
		}
		
		
		
		
		return view;

	}
	
	ViewVideoHolder viewVideoUploadHolder = null;
	final static class ViewVideoHolder {
		LinearLayout renwu_video_state_ll;
		IconFontTextView ivHead,ivUpload;
		TextView tvStateProgress,tvStateTime,tvStateContent;
		SeekBar video_upload_progress;
	}

	ViewHolder viewHolder = null;
	final static class ViewHolder {
		IconFontTextView tvHead;
		TextView tvDate;
		TextView alertContent;
		RelativeLayout enwu_item_rl;
	}

}
