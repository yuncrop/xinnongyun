package com.xinnongyun.atyfragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.videogo.CameraListAdapter;
import com.videogo.constant.IntentConsts;
import com.videogo.openapi.bean.resp.CameraInfo;
import com.videogo.widget.SimpleRealPlayActivity;
import com.xinnongyun.activity.AppUserHelpActivity;
import com.xinnongyun.activity.FarmAddCreateActicity;
import com.xinnongyun.activity.JournalVideoNotifyActivity;
import com.xinnongyun.activity.MainActivity;
import com.xinnongyun.activity.R;
import com.xinnongyun.activity.VideoRecordActivity;
import com.xinnongyun.activity.WeatherStationDetailActivity;
import com.xinnongyun.adapter.ControlCenterGridViewAdapter;
import com.xinnongyun.adapter.FarmCollectorsAdapter;
import com.xinnongyun.adapter.GridViewAdapter;
import com.xinnongyun.adapter.JournalListAdapter;
import com.xinnongyun.adapter.RenwuAdapter;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.imageload.ImageDownloadHelper;
import com.xinnongyun.imageload.ImageDownloadHelper.OnImageDownloadListener;
import com.xinnongyun.jpushbroadcast.JpushBroadCastReceiver;
import com.xinnongyun.json.JsonUtil;
import com.xinnongyun.net.NetConnectionNotice;
import com.xinnongyun.service.CoreService;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.CameraDaoDBManager;
import com.xinnongyun.sqlite.ControlCenterDaoDBManager;
import com.xinnongyun.sqlite.JournalDaoDBManager;
import com.xinnongyun.sqlite.NoticeDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.sqlite.VideoUploadDaoDBManager;
import com.xinnongyun.sqlite.WeatherStationDaoDBManager;
import com.xinnongyun.utils.DialogManager;
import com.xinnongyun.utils.StringUtil;
import com.xinnongyun.view.CircleImageView;
import com.xinnongyun.view.IconFontTextView;
import com.xinnongyun.view.MyGridView;
import com.xinnongyun.view.SafeProgressDialog;

public class MainFragment extends Fragment implements OnClickListener {

	public static String currentPinglunId = "";
	public static boolean collectUpdateList = true;

	// 农作物 1 动物2
	public static String beingType = "1";

	public int curMainPage = 1;
	// 首页
	private TextView shouyehead;
	private RelativeLayout shouyeLayout;
	public View mainview;
	private ViewPager mainVp;
	public boolean userClickState = false;
	private GridView mainGridView;
	private ImageView openSlideMenu;
	public List<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
	
	// 农场
	private int firstOpenFarm = 0;
	public ImageView farmbeingShuaxin;
	private LinearLayout farmHeadll;
	public TextView shouye_head_farm;
	private Button animalBtn, cropBtn;
	private LinearLayout farmLayout;
	private ListView mListView;
	public FarmCollectorsAdapter mAdapter;

	
	//任务
	private LinearLayout mainfragment_renwu_ll;
	private ListView renwu_list;
	private TextView renwu_head;
	private RenwuAdapter renwuAdapter;
	private TextView mainfragment_renwu_et_head;
	private ImageView popdelete_iv;
	
	
	//视频
	private LinearLayout mainfragment_video_ll;
	private ImageView mainfragment_video_et,userrecord_iv;
	private ListView journal_video_list;
	public View journalVideo_head;
	private CircleImageView zan_comment_tishi_iv;
	private TextView journal_no_read_count,shouye_head_video;
	
	// 网络异常
	private RelativeLayout interneterror_TT;

	// //成熟日期
	// private ImageView changeBeingMarturingIv;
	//
	// 底部标题
	private ImageView shouye_TT, renwu_TT, farm_TT;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainview = inflater.inflate(R.layout.activity_main_fragment, null);
		shouyeLayout = (RelativeLayout) mainview
				.findViewById(R.id.mainfragment_shouye_rl);
		
		farmLayout = (LinearLayout) mainview
				.findViewById(R.id.mainfragment_farm_ll);
		shouye_head_farm = (TextView) mainview.findViewById(R.id.shouye_head_farm);
		shouye_head_video = (TextView) mainview.findViewById(R.id.shouye_head_video);
		shouye_TT = (ImageView) mainview.findViewById(R.id.mainfragment_shouye_et);
		shouye_TT.setOnClickListener(this);
		renwu_TT = (ImageView) mainview.findViewById(R.id.mainfragment_renwu_et);
		renwu_TT.setOnClickListener(this);
		farm_TT = (ImageView) mainview.findViewById(R.id.mainfragment_farm_et);
		farm_TT.setOnClickListener(this);
		userrecord_iv = (ImageView) mainview.findViewById(R.id.userrecord_iv);
		userrecord_iv.setOnClickListener(this);
		mainfragment_video_et = (ImageView) mainview.findViewById(R.id.mainfragment_video_et);
		mainfragment_video_et.setOnClickListener(this);
		shouyehead = (TextView) mainview.findViewById(R.id.shouye_head);
		farmHeadll = (LinearLayout) mainview.findViewById(R.id.farm_headll);
		animalBtn = (Button) mainview.findViewById(R.id.farm_head_animal);
		animalBtn.setOnClickListener(this);
		cropBtn = (Button) mainview.findViewById(R.id.farm_head_crop);
		cropBtn.setOnClickListener(this);
		farmbeingShuaxin = (ImageView) mainview
				.findViewById(R.id.main_farming_being_shuaxin);
		farmbeingShuaxin.setOnClickListener(this);
		initViewShouYe(mainview);
		intialViewVideo(mainview);
		initViewNongChang(mainview);
		initialViewRenwu(mainview);
		if(((MainActivity)getActivity()).currentpageSelect == 2)
		{
			showMainFragmentPage(2);
		}
		
		return mainview;
	}

	
	//初始化视频
	@SuppressWarnings({ "deprecation", "static-access" })
	public void intialViewVideo(View viewMain)
	{
		mainfragment_video_ll = (LinearLayout) viewMain.findViewById(R.id.mainfragment_video_ll);
		journal_video_list = (ListView) viewMain.findViewById(R.id.journal_video_list);
		journalVideo_head = viewMain.inflate(getActivity(), R.layout.main_fragment_video_list_head, null);
		journalVideo_head.findViewById(R.id.journalvideo_noitify_ll).setOnClickListener(this);
		journal_no_read_count = (TextView) journalVideo_head.findViewById(R.id.journal_no_read_count);
		journal_no_read_count.setText(R.string.video_journal_no_notify_count);
		zan_comment_tishi_iv = (CircleImageView) journalVideo_head.findViewById(R.id.zan_comment_tishi_iv);
		intialVideoFarmImage(journalVideo_head);
		journal_video_list.addHeaderView(journalVideo_head);
		journal_video_list.addFooterView(viewMain.inflate(getActivity(), R.layout.videolist_foot, null));
		jouAdapter = new JournalListAdapter(getActivity(), null,getActivity().getWindowManager().getDefaultDisplay().getWidth());
		journal_video_list.setAdapter(jouAdapter);
		updateJournalList();
	}
	
	public void intialVideoFarmImage(View view) {
		final ImageView farmBg = (ImageView) view.findViewById(R.id.farmconfig_newfarm_image_bg);
		final ImageView farmLogo = (ImageView) view.findViewById(R.id.farmconfig_newfarm_image_logo);
		String farmbgUrl;
		if (MySharePreference.getValueFromKey(getActivity(),
				MySharePreference.FARMBG).equals(MySharePreference.NOTSTATE) ||MySharePreference.getValueFromKey(getActivity(),
						MySharePreference.FARMBG).equals(YunTongXun.FarmBgImageDefault)) {
			farmbgUrl = YunTongXun.FarmBgImageDefault;
			farmBg.setBackgroundResource(R.drawable.farm_bg_default);
		} else {
			farmbgUrl = MySharePreference.getValueFromKey(
					getActivity(), MySharePreference.FARMBG);
			final String farmBgurlFinal = farmbgUrl;
			farmBg.setTag(farmBgurlFinal);
			farmBg.setBackgroundResource(R.drawable.farm_bg_default);
			ImageDownloadHelper.getInstance().imageDownload(null,
					getActivity(), farmBgurlFinal, farmBg,
					"/001yunhekeji100", new OnImageDownloadListener() {
						@SuppressWarnings("deprecation")
						public void onImageDownload(Bitmap bitmap, String imgUrl) {
							if (bitmap != null) {
							
								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
									farmBg.setBackground(new BitmapDrawable(bitmap));
								} else {
									farmBg.setBackgroundDrawable(new BitmapDrawable(bitmap));
								}
								farmBg.setTag(YunTongXun.ImagePath
										+ StringUtil
												.getFileNameFromUrl(farmBgurlFinal));
							} 
						}
					});
		}
		
		String farmlogoUrl;
		if (MySharePreference.getValueFromKey(getActivity(),
				MySharePreference.FARMLOGO).equals(MySharePreference.NOTSTATE) || MySharePreference.getValueFromKey(getActivity(),
						MySharePreference.FARMLOGO).equals(YunTongXun.FarmLogoImageDefault)) {
			farmlogoUrl = YunTongXun.FarmLogoImageDefault;
			farmLogo.setBackgroundResource(R.drawable.nongchang_head);
		} else {
			farmlogoUrl = MySharePreference.getValueFromKey(
					getActivity(), MySharePreference.FARMLOGO);
			final String farmLogourlFinal = farmlogoUrl;
			farmLogo.setTag(farmLogourlFinal);
			ImageDownloadHelper.getInstance().imageDownload(null,
					getActivity(), farmLogourlFinal, farmLogo,
					"/001yunhekeji100", new OnImageDownloadListener() {
						@SuppressWarnings("deprecation")
						public void onImageDownload(Bitmap bitmap, String imgUrl) {
							if (bitmap != null) {
								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
									farmLogo.setBackground(new BitmapDrawable(bitmap));
								} else {
									farmLogo.setBackgroundDrawable(new BitmapDrawable(bitmap));
								}
								
								farmLogo.setTag(YunTongXun.ImagePath
										+ StringUtil
												.getFileNameFromUrl(farmLogourlFinal));
							} 
						}
					});
		}
		
	}

	
	public void updateJournalList()
	{
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = jouAdapter.journalDataHandler.obtainMessage();
				JournalDaoDBManager daoDBManager = new JournalDaoDBManager(getActivity());
				List<HashMap<String, String>> listmap = daoDBManager.getAllJournalVideo();
				if(listmap!=null && listmap.size() > 0)
				{
					msg.obj = listmap;
					jouAdapter.journalDataHandler.sendMessage(msg);
				}
			}
		}).start();
	}
	//初始化任务
	public void initialViewRenwu(View viewMain)
	{
		popdelete_iv = (ImageView) viewMain.findViewById(R.id.popdelete_iv);
		popdelete_iv.setOnClickListener(this);
		mainfragment_renwu_et_head = (TextView) viewMain.findViewById(R.id.mainfragment_renwu_et_head);
		mainfragment_renwu_ll = (LinearLayout) viewMain.findViewById(R.id.mainfragment_renwu_ll);
		renwu_list = (ListView) viewMain.findViewById(R.id.renwu_list);
		renwu_head = (TextView) viewMain.findViewById(R.id.renwu_head);
		renwuAdapter = new RenwuAdapter(getActivity(), null);
		renwu_list.setAdapter(renwuAdapter);
		updateRenwuList();
	}
	
	
	public Handler updateJournalCountHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			 //未读视频通知条数
			 if(!msg.obj.toString().contains(","))
			 {
				 int journalCount = (Integer) msg.obj;
				 if(journalCount == 0)
				 {
					 mainview.findViewById(R.id.mainfragment_video_et_head).setVisibility(View.INVISIBLE);
					 journal_no_read_count.setText(R.string.video_journal_no_notify_count);
					 journalVideo_head.findViewById(R.id.journalvideo_noitify_ll).setVisibility(View.INVISIBLE);
				 }
			 }
			 else
			 {
				 String[] infos = msg.obj.toString().split(",");
				
				 
				 journal_no_read_count.setText(infos[0] + "条新消息");
				 if(infos.length==4)
				 {
					 videoLikeCommentsHeadShow(infos[1], infos[2], infos[3],null);
				 }else if(infos.length==5)
				 {
					 videoLikeCommentsHeadShow(infos[1], infos[2], infos[3],infos[4]);
				 }
				 
				 journalVideo_head.findViewById(R.id.journalvideo_noitify_ll).setVisibility(View.VISIBLE);
				 mainview.findViewById(R.id.mainfragment_video_et_head).setVisibility(View.VISIBLE);
			 }
		}
	};
	
	
	public Handler updateRenwuCountHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			 //未读任务条数
			 int count = (Integer)msg.obj;
			 if(count == 0)
			 {
				 mainfragment_renwu_et_head.setVisibility(View.INVISIBLE);
				 renwu_head.setText(R.string.main_renwu);
			 }
			 else
			 {
				 mainfragment_renwu_et_head.setVisibility(View.VISIBLE);
				 mainfragment_renwu_et_head.setText(count + "");
				 if(renwu_head!=null)
				 renwu_head.setText(getActivity().getResources().getString(R.string.main_renwu)+"("+count+")");
			 }
		}
	};
	
	
	
	
	
	public void updateRenwuList()
	{
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = renwuAdapter.renwuDataHandler.obtainMessage();
				NoticeDaoDBManager daoDBManager = new NoticeDaoDBManager(getActivity());
				List<HashMap<String, String>> listmap = daoDBManager.getAllNotice();
				List<HashMap<String, String>> journalList = daoDBManager.getAllNotifyNoRead();
				if(journalList==null || journalList.size() == 0 || MySharePreference.getValueFromKey(getActivity(), MySharePreference.FARMNAME).equals(
						MySharePreference.NOTSTATE))
				{
					Message countMsg = updateJournalCountHandler.obtainMessage();
					countMsg.obj = 0;
					updateJournalCountHandler.sendMessage(countMsg);
				}else
				{
					Message countMsg = updateJournalCountHandler.obtainMessage();
					HashMap<String, Object> cmap = JsonUtil.getHashMap(journalList.get(0).get("context"));
					if(cmap.size()==1)
					{
						countMsg.obj = journalList.size() + "," +
								cmap.get("id").toString() + ","+
						journalList.get(0).get("signal").toString() + "," +
						journalList.get(0).get("operator").toString();
					}
					else
					{
						countMsg.obj = journalList.size() + "," +
								cmap.get("id").toString() + ","+
						journalList.get(0).get("signal").toString() + "," +
						journalList.get(0).get("operator").toString() + "," +
						cmap.get("comment_id").toString();
					}
					updateJournalCountHandler.sendMessage(countMsg);
				}
				
				int count = 0;
				if(listmap==null || listmap.size() == 0)
				{
					count = 0;
				}
				else
				{
					
					for(HashMap<String, String> map : listmap)
					{
						if(map.get(TablesColumns.TABLENOTICE_READAT)==null || map.get(TablesColumns.TABLENOTICE_READAT).toString().equals("null"))
						{
							count ++ ;
						}
					}
				}
				
				VideoUploadDaoDBManager uploadDaoDBManager = new VideoUploadDaoDBManager(getActivity());
				List<HashMap<String, String>> videoList = uploadDaoDBManager.getAllVideoUploadList();
				if(videoList!=null && videoList.size()>0)
				{
					listmap.addAll(0, videoList);
					int videoStateCount = 0;
					for(HashMap<String, String> videoStateMap : videoList)
					{
						if(!videoStateMap.get(TablesColumns.TABLEVIDEOUPLOAD_STATE).equals("2"))
						{
							videoStateCount ++;
						}
					}
					count +=videoStateCount;
					Collections.sort(listmap, new Comparator<Map<String,String>>(){ 
			            @Override 
			            public int compare(Map<String, String> map1, Map<String, String> map2) { 
			            	Date a = StringUtil.stringConverToDate(StringUtil.changeSerTo(map1.get("created_at").toString()));
			            	Date b = StringUtil.stringConverToDate(StringUtil.changeSerTo(map2.get("created_at").toString()));
			            	if(a.after(b))
			            	{
			            		return -1;
			            	}
			            	else if(a.before(b))
			            	{
			            		return 1;
			            	}else
			            	{
			            		return 0;
			            	}
			            } 
			        }); 
				}
				
				Message countMsg = updateRenwuCountHandler.obtainMessage();
				countMsg.obj = count;
				updateRenwuCountHandler.sendMessage(countMsg);
				msg.obj = listmap;
				renwuAdapter.renwuDataHandler.sendMessage(msg);
			}
		}).start();
	}
	
	
	/**
	 * 初始化农场界面
	 */
	public void initViewNongChang(View viewMain) {
		listHeadView = LayoutInflater.from(getActivity()).inflate(R.layout.farming_list_head_weatherstation, null);
		interneterror_TT = (RelativeLayout) viewMain
				.findViewById(R.id.farm_main_internet_error);
		setInternetTextIsVisible(getActivity());
		mListView = (ListView) viewMain
				.findViewById(R.id.pulltorefresh);
		mListView.setDividerHeight(getActivity().getResources().getDimensionPixelSize(R.dimen.maring_15));
		mListView.setFadingEdgeLength(0);
		mListView.addFooterView(LayoutInflater.from(getActivity()).inflate(R.layout.main_fragment_farminglist_foot, null));
		intialStationHead(0);
//		mList = CollectLastTimeLine.getFarmingColectInfos(getActivity(),
//				beingType);
		mAdapter = new FarmCollectorsAdapter(getActivity(), mList);
		mListView.setAdapter(mAdapter);

		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = mAdapter.collectListHandler.obtainMessage();
				msg.obj = CollectLastTimeLine.getFarmingColectInfos(
						getActivity(), MainFragment.beingType);
				mAdapter.collectListHandler.sendMessage(msg);
			}
		}).start();
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});
	}

	
	
	
	private IconFontTextView head_weather_station_iv;
	private TextView head_weather_station_name;
	private View listHeadView;
	private int addHeadFarming = 0;
	public static int cameraType = 1;
	public void intialStationHead(int type) {
		WeatherStationDaoDBManager stationDaoDBManager = new WeatherStationDaoDBManager(getActivity());
		List<HashMap<String, String>> stations = stationDaoDBManager.getAllWeatherStation();
		
		CameraDaoDBManager cameraDa0Managet = new CameraDaoDBManager(getActivity());
		final List<HashMap<String, String>> cameraList = cameraDa0Managet.getAllCameraList();
		
		ControlCenterDaoDBManager centerDaoDBManager = new ControlCenterDaoDBManager(getActivity());
		List<HashMap<String, String>> ccList = centerDaoDBManager.getAllControlCenters();
		
		
		if((stations!=null && stations.size()>0) || (cameraList!=null && cameraList.size() > 0) || (ccList!=null && ccList.size()>0))
		{
				listHeadView.setVisibility(View.VISIBLE);
				if(addHeadFarming==0)
				{
					mListView.addHeaderView(listHeadView);
					addHeadFarming++;
				}
				if(stations!=null && stations.size()>0)
				{
					HashMap<String, String> station = stations.get(0);
					if(type ==0)
					{
						mListView.findViewById(R.id.head_weather_station_ll).setOnClickListener(this);
						head_weather_station_iv =(IconFontTextView)mainview.findViewById(R.id.head_weather_station_iv);
						head_weather_station_iv.setText(String.valueOf(new char[]{0xe65c}));
						head_weather_station_name = (TextView) listHeadView.findViewById(R.id.head_weather_station_name);
						head_weather_station_name.setText(station.get(TablesColumns.TABLEWEATHERSTATION_NAME));
						intialWeatherStationTimeLine();
						mListView.findViewById(R.id.head_weather_station_ll).setVisibility(View.VISIBLE);
					}
					else
					{
						head_weather_station_name.setText(station.get(TablesColumns.TABLEWEATHERSTATION_NAME));
					}
				}
				else
				{
					mListView.findViewById(R.id.head_weather_station_ll).setVisibility(View.GONE);
				}
				
				
				if(ccList!=null && ccList.size()>0)
				{
					MyGridView ccGv = (MyGridView) (mListView.findViewById(R.id.controlcenterList_GV));
					ccGv.setVisibility(View.VISIBLE);
					ccGv.setAdapter(new ControlCenterGridViewAdapter(getActivity(),ccList));
				}else
				{
					mListView.findViewById(R.id.controlcenterList_GV).setVisibility(View.GONE);
				}
				
				
				
				
				
				
				
				if(cameraList!=null && cameraList.size() > 0   && cameraType ==1)
				{
					cameraType = 2;
					final CameraListAdapter cameraAdapter = new CameraListAdapter(getActivity());
					
					 CameraInfo cameraInfo = null;
					 for(HashMap<String, String> cMap : cameraList)
					 {
						 cameraInfo = new CameraInfo();
						 cameraInfo.setCameraId(cMap.get(TablesColumns.TABLECAMERA_ID).toString());
						 cameraInfo.setCameraName(cMap.get(TablesColumns.TABLECAMERA_NAME).toString());
						 //cameraInfo.setCameraNo(1);
						 cameraInfo.setDeviceId(cMap.get(TablesColumns.TABLECAMERA_DEVICEID).toString());
						 cameraInfo.setIsEncrypt(0);
						 cameraInfo.setIsShared(0);
						 cameraInfo.setPicUrl(cMap.get(TablesColumns.TABLECAMERA_COVER_URL1).toString());
						 cameraInfo.setStatus(0);
						 cameraAdapter.addItem(cameraInfo);
					 }
					 cameraAdapter.setOnClickListener(new CameraListAdapter.OnClickListener() {
						
						@Override
						public void onPlayClick(BaseAdapter adapter, View view, int position) {
							 Intent intent = new Intent(getActivity(), SimpleRealPlayActivity.class);
							 intent.putExtra("share_url", cameraList.get(position).get(TablesColumns.TABLECAMERA_SHARE_URL).toString());
							 intent.putExtra("isPublish", cameraList.get(position).get(TablesColumns.TABLECAMERA_PUBLISH).toString());
				             intent.putExtra(IntentConsts.EXTRA_CAMERA_INFO, cameraAdapter.mCameraInfoList.get(position));
				             startActivity(intent);
						}
					});
					((GridView)mListView.findViewById(R.id.cameraList_GV)).setAdapter(cameraAdapter);
					mListView.findViewById(R.id.cameraList_GV).setVisibility(View.VISIBLE);
				}
				else if(cameraList!=null && cameraList.size() == 0)
				{
					cameraType = 2;
					mListView.findViewById(R.id.cameraList_GV).setVisibility(View.GONE);
				}else if(cameraList==null)
				{
					cameraType = 2;
					mListView.findViewById(R.id.cameraList_GV).setVisibility(View.GONE);
				}
				
				
				
		}
		else
		{
			addHeadFarming = 0;
			listHeadView.setVisibility(View.GONE);
			mListView.removeHeaderView(listHeadView);
		}
	}

	
	
	public void intialWeatherStationTimeLine()
	{
		WeatherStationDaoDBManager stationDaoDBManager = new WeatherStationDaoDBManager(getActivity());
		List<HashMap<String, String>> stations = stationDaoDBManager.getAllWeatherStation();
		if(stations!=null && stations.size()>0)
		{
			
			HashMap<String, String> timeStation = stationDaoDBManager.getWeatherStationLastUpdateTime(stations.get(0).get("id"));
			if(timeStation!=null && timeStation.keySet().size()>0)
			{
				SpannableString sp1 = new SpannableString("PM2.5");
				sp1.setSpan(new RelativeSizeSpan(0.5f), 2, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				sp1.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 2, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //粗体
				((TextView)listHeadView.findViewById(R.id.head_weather_station_pm_num_unit)).setText(sp1);
				((TextView)listHeadView.findViewById(R.id.weather_station_head_tem)).setText(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_TEM));
				((TextView)listHeadView.findViewById(R.id.weather_station_head_hum)).setText(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_HUM));
				try {
					((TextView)listHeadView.findViewById(R.id.weather_station_head_light)).setText("" + (int)Double.parseDouble(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_ILLU)));
				} catch (Exception e1) {
					((TextView)listHeadView.findViewById(R.id.weather_station_head_light)).setText(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_ILLU));
				}
				try {
					((TextView)listHeadView.findViewById(R.id.weather_station_head_coo)).setText("" + (int)Double.parseDouble(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_CO2)));
				} catch (NumberFormatException e1) {
					((TextView)listHeadView.findViewById(R.id.weather_station_head_coo)).setText(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_CO2));
				}
				
				String powerStation = timeStation.get(TablesColumns.TABLESTATIONTIMELINR_POWER);
				if(powerStation== null || powerStation.equals("null"))
				{
					powerStation = "0";
				}
				double powderNum = Double.parseDouble(powerStation);
				if(powderNum<=YunTongXun.powderLeft)
				{
					((IconFontTextView)listHeadView.findViewById(R.id.weather_station_powder)).setTextColor(Color.parseColor("#fe695d"));
					((IconFontTextView)listHeadView.findViewById(R.id.weather_station_powder)).setText(String.valueOf(new char[]{0xe618}));
				}else
				{
					listHeadView.findViewById(R.id.weather_station_powder).setVisibility(View.GONE);
				}
			    
			    try {
			    	TextView head_weather_station_pm_num = (TextView) listHeadView.findViewById(R.id.head_weather_station_pm_num);
					String station_pm_num = timeStation.get(TablesColumns.TABLESTATIONTIMELINR_PM25);
					double pm25Num = Double.parseDouble(station_pm_num);
					head_weather_station_pm_num.setText("" + (int)pm25Num);
					if(pm25Num<=50)
					{
						((TextView) listHeadView.findViewById(R.id.head_weather_station_pm_num_level)).setText("优");
						head_weather_station_iv.setTextColor(getResources().getColor(R.color.color_weather_you));
						((TextView) listHeadView.findViewById(R.id.head_weather_station_pm_num_level)).setBackgroundResource(R.drawable.circle_rangle_you);
						((TextView) listHeadView.findViewById(R.id.head_weather_station_pm_num)).setTextColor(getResources().getColor(R.color.color_weather_you));
						mListView.findViewById(R.id.head_weather_station_ll).setBackgroundResource(R.drawable.you);
					}else if(pm25Num<=100)
					{
						((TextView) listHeadView.findViewById(R.id.head_weather_station_pm_num_level)).setText("良");
						head_weather_station_iv.setTextColor(getResources().getColor(R.color.color_weather_liang));
						((TextView) listHeadView.findViewById(R.id.head_weather_station_pm_num_level)).setBackgroundResource(R.drawable.circle_rangle_liang);
						((TextView) listHeadView.findViewById(R.id.head_weather_station_pm_num)).setTextColor(getResources().getColor(R.color.color_weather_liang));
						mListView.findViewById(R.id.head_weather_station_ll).setBackgroundResource(R.drawable.liang);
					}else if(pm25Num<=200)
					{
						((TextView) listHeadView.findViewById(R.id.head_weather_station_pm_num_level)).setText("中");
						head_weather_station_iv.setTextColor(getResources().getColor(R.color.color_weather_zhong));
						((TextView) listHeadView.findViewById(R.id.head_weather_station_pm_num_level)).setBackgroundResource(R.drawable.circle_rangle_zhong);
						((TextView) listHeadView.findViewById(R.id.head_weather_station_pm_num)).setTextColor(getResources().getColor(R.color.color_weather_zhong));
						mListView.findViewById(R.id.head_weather_station_ll).setBackgroundResource(R.drawable.zhong);
					}
					else if(pm25Num>200)
					{
						((TextView) listHeadView.findViewById(R.id.head_weather_station_pm_num_level)).setText("差");
						head_weather_station_iv.setTextColor(getResources().getColor(R.color.color_weather_cha));
						((TextView) listHeadView.findViewById(R.id.head_weather_station_pm_num_level)).setBackgroundResource(R.drawable.circle_rangle_cha);
						((TextView) listHeadView.findViewById(R.id.head_weather_station_pm_num)).setTextColor(getResources().getColor(R.color.color_weather_cha));
						mListView.findViewById(R.id.head_weather_station_ll).setBackgroundResource(R.drawable.cha);
					}
					
					
					try {
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date d2 = df.parse(StringUtil.changeSerTo(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_CREATEDAT)));
						
						if(StringUtil.miniteLength(new Date(), d2)>=YunTongXun.minuteLength)
						{
							listHeadView.findViewById(R.id.head_weather_station_sign).setVisibility(View.VISIBLE);
							((IconFontTextView)listHeadView.findViewById(R.id.head_weather_station_sign)).setText(String.valueOf(new char[]{0xe644}));
							((IconFontTextView)listHeadView.findViewById(R.id.head_weather_station_sign)).setTextColor(getResources().getColor(R.color.text_grey));
							((TextView)listHeadView.findViewById(R.id.weather_station_head_tem)).setTextColor(getResources().getColor(R.color.text_grey));
							((TextView)listHeadView.findViewById(R.id.weather_station_head_hum)).setTextColor(getResources().getColor(R.color.text_grey));
							((TextView)listHeadView.findViewById(R.id.weather_station_head_light)).setTextColor(getResources().getColor(R.color.text_grey));
							((TextView)listHeadView.findViewById(R.id.weather_station_head_coo)).setTextColor(getResources().getColor(R.color.text_grey));
							head_weather_station_iv.setTextColor(getResources().getColor(R.color.text_grey));
							mListView.findViewById(R.id.head_weather_station_ll).setBackgroundResource(R.drawable.tishi_dialog_show_bg);
						}else
						{
							((TextView)listHeadView.findViewById(R.id.weather_station_head_tem)).setTextColor(getResources().getColor(R.color.text_blue));
							((TextView)listHeadView.findViewById(R.id.weather_station_head_hum)).setTextColor(getResources().getColor(R.color.text_blue));
							((TextView)listHeadView.findViewById(R.id.weather_station_head_light)).setTextColor(getResources().getColor(R.color.text_blue));
							((TextView)listHeadView.findViewById(R.id.weather_station_head_coo)).setTextColor(getResources().getColor(R.color.text_blue));
							listHeadView.findViewById(R.id.head_weather_station_sign).setVisibility(View.GONE);
						}
					}catch(Exception e)
					{}
					
				} catch (Exception e) {
					
				}
			}
		}
	}
	

	/**
	 * 初始化首页界面
	 * 
	 * @param viewMain
	 */
	private void initViewShouYe(View viewMain) {
		// changeBeingMarturingIv = (ImageView)
		// viewMain.findViewById(R.id.changeBeingMarturing);
		// changeBeingMarturingIv.setOnClickListener(this);
		openSlideMenu = (ImageView) viewMain
				.findViewById(R.id.main_openSlideMenu);
		openSlideMenu.setOnClickListener(this);
		viewMain.findViewById(R.id.ll_mainvp).setLayoutParams(
				new RelativeLayout.LayoutParams(StringUtil.getScreenWidth(getActivity()), 
						StringUtil.getScreenWidth(getActivity())/2));
		mainVp = (ViewPager) viewMain.findViewById(R.id.mainfragment_vp);
		mainGridView = (GridView) viewMain.findViewById(R.id.mainfragmentGV);

		ArrayList<HashMap<String, Object>> al = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map.put("name", getResources().getString(R.string.maturingAt));// 按序号做ItemText
		al.add(map);
		// map2.put("icon", R.drawable.gv_zhiwu);// 添加图像资源的ID
		map2.put("name", getResources().getString(R.string.xinzhong));// 按序号做ItemText
		al.add(map2);
		HashMap<String, Object> map3 = new HashMap<String, Object>();
		// map3.put("icon", R.drawable.gv_yazi);// 添加图像资源的ID
		map3.put("name", getResources().getString(R.string.xinyang));// 按序号做ItemText
		al.add(map3);
		HashMap<String, Object> map4 = new HashMap<String, Object>();
		// map3.put("icon", R.drawable.gv_yazi);// 添加图像资源的ID
		map4.put("name", getResources().getString(R.string.paishipin));// 按序号做ItemText
		al.add(map4);
		GridViewAdapter sa = new GridViewAdapter(getActivity(), al);
		mainGridView.setAdapter(sa);
		
		imageList = new ArrayList<ImageView>();
		ImageView b = new ImageView(viewMain.getContext());
		b.setBackgroundResource(R.drawable.guanggao);
		b.setOnClickListener(
				new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent();
						intent.setClass(getActivity(), AppUserHelpActivity.class);
						startActivity(intent);
					}
				}
				);
		imageList.add(b);
		
		mainVp.setAdapter(new MyPagerAdapter(imageList));
	
	}

	

	private List<ImageView> imageList;

	
	class MyPagerAdapter extends PagerAdapter {
		private List<ImageView> imageList;

		public MyPagerAdapter(List<ImageView> imageList) {
			this.imageList = imageList;
		}

		// 返回可获取的页面的数量
		@Override
		public int getCount() {
			return imageList.size();
		}

		// 用来产生一个页面 container：Viewpager position:页面位置
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// 将集合中某个位置上的imageview添加到viewpager
			container.addView(imageList.get(position));
			return imageList.get(position);
		}

		// 决定了一个页面view是不是和instantiateItem的返回值是同一一个对象
		@Override
		public boolean isViewFromObject(View view, Object object) {
			// 判断一个原有的view和object是不是同一个对象，如果是，则直接复用
			// 否则调用instantiateItem来产生一个新的对象
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// 从容器中移除position上的图片
			container.removeView(imageList.get(position));
		}
	}

	public void updateFarmngInfos()
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message msg = mAdapter.collectListHandler.obtainMessage();
				msg.obj = CollectLastTimeLine.getFarmingColectInfos(
						getActivity(), MainFragment.beingType);
				mAdapter.collectListHandler.sendMessage(msg);
				
			}
		}).start();
		handler.sendEmptyMessage(0); 
	}
	
	/**
	 * 显示主界面中的条目
	 * 
	 * @param page
	 */
	public void showMainFragmentPage(int page) {
		
		if (curMainPage == page) {
			return;
		}
		
		if(firstOpenFarm == 0 && page == 3)
		{
//			TimeLineDaoDBManager timeLineDao = new TimeLineDaoDBManager(getActivity());
//			List<HashMap<String, String>> lists = timeLineDao.getAllTimeLine();
//			if(lists==null || lists.size()==0)
//			{
				pd = createLoadingDialog(getActivity());
				pd.show();
//			}
			firstOpenFarm++;
		}
		
		
		shouyeLayout.setVisibility(View.INVISIBLE);
		farmLayout.setVisibility(View.INVISIBLE);
		mainfragment_renwu_ll.setVisibility(View.INVISIBLE);
		shouye_head_video.setVisibility(View.INVISIBLE);
		
		curMainPage = page;
		if (page == 1) {
			mainview.findViewById(R.id.farmingListempty_ll).setVisibility(View.INVISIBLE);
			userrecord_iv.setVisibility(View.GONE);
			mainfragment_video_ll.setVisibility(View.INVISIBLE);
			popdelete_iv.setVisibility(View.GONE);
			renwu_head.setVisibility(View.INVISIBLE);
			shouyeLayout.setVisibility(View.VISIBLE);
			shouyehead.setVisibility(View.VISIBLE);
			farmHeadll.setVisibility(View.INVISIBLE);
			farmbeingShuaxin.setVisibility(View.GONE);
			shouye_TT.setImageResource(R.drawable.zhuye_press);
			renwu_TT.setImageResource(R.drawable.renwu);
			farm_TT.setImageResource(R.drawable.nongchang);
			mainfragment_video_et.setImageResource(R.drawable.videolist);

		}
		
		else if(page == 2)
		{
			mainview.findViewById(R.id.farmingListempty_ll).setVisibility(View.INVISIBLE);
			userrecord_iv.setVisibility(View.GONE);
			mainfragment_video_ll.setVisibility(View.INVISIBLE);
			popdelete_iv.setVisibility(View.VISIBLE);
			renwu_head.setVisibility(View.VISIBLE);
			mainfragment_renwu_ll.setVisibility(View.VISIBLE);
			farmHeadll.setVisibility(View.INVISIBLE);
			shouyehead.setVisibility(View.INVISIBLE);
			farmbeingShuaxin.setVisibility(View.GONE);
			shouye_TT.setImageResource(R.drawable.zhuye);
			renwu_TT.setImageResource(R.drawable.renwu_press);
			farm_TT.setImageResource(R.drawable.nongchang);
			mainfragment_video_et.setImageResource(R.drawable.videolist);
		}
		
		else if (page == 3) {
			if (MySharePreference.getValueFromKey(getActivity(), MySharePreference.FARMNAME).equals(
					MySharePreference.NOTSTATE))
			{
				mainview.findViewById(R.id.farmingListempty_ll).setVisibility(View.VISIBLE);
			}
			else
			{
				mainview.findViewById(R.id.farmingListempty_ll).setVisibility(View.INVISIBLE);
			}
			userrecord_iv.setVisibility(View.GONE);
			mainfragment_video_ll.setVisibility(View.INVISIBLE);
			popdelete_iv.setVisibility(View.GONE);
			renwu_head.setVisibility(View.INVISIBLE);
			updateFarmngInfos();
			farmbeingShuaxin.setVisibility(View.VISIBLE);
			farmLayout.setVisibility(View.VISIBLE);
			if(!MySharePreference.getValueFromKey(getActivity(), MySharePreference.FARMNAME).equals(MySharePreference.NOTSTATE) &&
					!MySharePreference.getValueFromKey(getActivity(), MySharePreference.FARMNAME).equals("未设置"))
			{
				shouye_head_farm.setText(MySharePreference.getValueFromKey(getActivity(), MySharePreference.FARMNAME));
			}
			else
			{
				shouye_head_farm.setText(R.string.app_name);
			}
			farmHeadll.setVisibility(View.VISIBLE);
			shouyehead.setVisibility(View.INVISIBLE);
			farm_TT.setImageResource(R.drawable.nongchang_press);
			renwu_TT.setImageResource(R.drawable.renwu);
			shouye_TT.setImageResource(R.drawable.zhuye);
			mainfragment_video_et.setImageResource(R.drawable.videolist);
		}
		else if(page == 4)
		{
			if (MySharePreference.getValueFromKey(getActivity(), MySharePreference.FARMNAME).equals(
					MySharePreference.NOTSTATE))
			{
				mainview.findViewById(R.id.farmingListempty_ll).setVisibility(View.VISIBLE);
			}
			else
			{
				mainview.findViewById(R.id.farmingListempty_ll).setVisibility(View.INVISIBLE);
			}
			mainfragment_video_et.setImageResource(R.drawable.videolistpress);
			shouye_head_video.setVisibility(View.VISIBLE);
			userrecord_iv.setVisibility(View.VISIBLE);
			mainfragment_video_ll.setVisibility(View.VISIBLE);
			popdelete_iv.setVisibility(View.GONE);
			renwu_head.setVisibility(View.INVISIBLE);
			shouyeLayout.setVisibility(View.INVISIBLE);
			shouyehead.setVisibility(View.INVISIBLE);
			farmHeadll.setVisibility(View.INVISIBLE);
			farmbeingShuaxin.setVisibility(View.GONE);
			shouye_TT.setImageResource(R.drawable.zhuye);
			renwu_TT.setImageResource(R.drawable.renwu);
			farm_TT.setImageResource(R.drawable.nongchang);
		}
	}
	
	
	/** 
     * 用Handler来更新UI 
     */  
    public Handler handler = new Handler(){  
  
        @Override  
        public void handleMessage(Message msg) {  
              
            //关闭ProgressDialog
        	if(pd!=null)
            pd.dismiss();  
            
        }};  
        
        
    //开始录像
    public void startToRecord()
    {
    	if (MySharePreference.getValueFromKey(getActivity(), MySharePreference.FARMNAME).equals(
				MySharePreference.NOTSTATE))
        {
			Intent intent = new Intent();
			intent.setClass(getActivity(), FarmAddCreateActicity.class);
			startActivity(intent);
		}
		else
		{
			Intent recordIntent = new Intent();
			recordIntent.setClass(getActivity(), VideoRecordActivity.class);
			startActivityForResult(recordIntent,100);
		}
    }
    
    private Dialog pd = null;;
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.head_weather_station_ll:
			Intent weatherStationDetailIntent = new Intent();
			weatherStationDetailIntent.setClass(getActivity(), WeatherStationDetailActivity.class);
			startActivity(weatherStationDetailIntent);
			break;
		case R.id.main_openSlideMenu:
			((MainActivity) getActivity()).openOrCloseSlideMenu();
			break;
		case R.id.mainfragment_shouye_et:
			showMainFragmentPage(1);
			break;
		case R.id.mainfragment_renwu_et:
			showMainFragmentPage(2);
			break;
		case R.id.mainfragment_farm_et:
			showMainFragmentPage(3);
			break;
		case R.id.mainfragment_video_et:
			showMainFragmentPage(4);
			break;
		case R.id.userrecord_iv:
			startToRecord();
			break;
		case R.id.farm_head_animal:
			changeBeingType("2");
			cropBtn.setBackgroundResource(R.drawable.slider_green_crop);
			cropBtn.setTextColor(getResources().getColor(R.color.text_white));
			animalBtn.setBackgroundResource(R.drawable.slider_animal);
			animalBtn.setTextColor(getResources().getColor(R.color.text_green));
			break;
		case R.id.farm_head_crop:
			changeBeingType("1");
			cropBtn.setBackgroundResource(R.drawable.slider_crop);
			cropBtn.setTextColor(getResources().getColor(R.color.text_green));
			animalBtn.setBackgroundResource(R.drawable.slider_green_animal);
			animalBtn.setTextColor(getResources().getColor(R.color.text_white));
			break;
		case R.id.main_farming_being_shuaxin:
			loadingHandler.sendEmptyMessage(0);
			break;
		case R.id.popdelete_iv:
			btn_PopupMenu(popdelete_iv);
			break;
		case R.id.popdelet_ll:
			popWindow.dismiss();
			@SuppressWarnings("static-access")
			NotificationManager manger = (NotificationManager)getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
			manger.cancelAll();
			JpushBroadCastReceiver.notificationLruCache.clearCache();
			NoticeDaoDBManager daoDBManager = new NoticeDaoDBManager(getActivity());
			List<String> noReadNoticeIds = daoDBManager.getAllNoReadNoticeIds();
			daoDBManager.changeNoticeAlreadyRead();
			if(noReadNoticeIds.size()>0)
			{
				String para = "";
				int i=0,c = noReadNoticeIds.size();
				for(String str : noReadNoticeIds)
				{
					i++;
					if(i == c)
					{
						para += str;
					}
					else
					{
						para += str +",";
					}
					
				}
				NetConnectionNotice.NoticePutNetConnection(
						"http://www.nnong.com/api-notice/read/"
						,"notice=" + para
						,"Token " + MySharePreference.getValueFromKey(getActivity(),MySharePreference.ACCOUNTTOKEN), null);
			}
			break;
		case R.id.journalvideo_noitify_ll:
			NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(getActivity());
			List<HashMap<String, String>> datas = noticeDaoDBManager.getAllNotifyNoRead();
			if(datas.size() == 0)
			{
				return;
			}
			Intent journalVideoNotify = new Intent();
			journalVideoNotify.setClass(getActivity(), JournalVideoNotifyActivity.class);
			getActivity().startActivity(journalVideoNotify);
			break;
		default:
			break;
		}
	}

	private PopupWindow popWindow;
	@SuppressWarnings("deprecation")
	public void btn_PopupMenu(View view)
	{
		View popContent = LayoutInflater.from(view.getContext()).inflate(R.layout.pop_delete, null);
		popWindow = new PopupWindow(popContent,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);
		popWindow.showAsDropDown(view);
		LinearLayout popll = (LinearLayout) popContent.findViewById(R.id.popdelet_ll);
		popContent.findViewById(R.id.soillistitem_iv).setVisibility(View.GONE);
		TextView popdelete_tv = (TextView) popContent.findViewById(R.id.popdelete_tv);
		popdelete_tv.setText("全部设为已读");
		popll.setOnClickListener(this);
	
	}
	public Handler loadingHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			if(curMainPage == 3)
			{
				Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(  
			               getActivity(), R.anim.loading_animation_farming);
					if(farmbeingShuaxin.getAnimation()==null || farmbeingShuaxin.getAnimation().hasEnded())
					farmbeingShuaxin.startAnimation(hyperspaceJumpAnimation);
			}
			else
			{
				farmbeingShuaxin.setVisibility(View.INVISIBLE);
			}
		}
	};

	private JournalListAdapter jouAdapter;
	private void changeBeingType(String type) {
		if (beingType.equals(type)) {
			return;
		} else {
			//beingType = type;
			new Thread(new Runnable() {
				@Override
				public void run() {
					Message msg = mAdapter.collectListHandler.obtainMessage();
					msg.obj = CollectLastTimeLine.getFarmingColectInfos(
							getActivity(), MainFragment.beingType);
					mAdapter.collectListHandler.sendMessage(msg);
				}
			}).start();
		}
	}

	
	public void setInternetTextIsVisible(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeInfo = manager.getActiveNetworkInfo();
		if (activeInfo == null) {
			interneterror_TT.setVisibility(View.VISIBLE);
		} else {
			CoreService coreService = new CoreService();
			coreService.getAllCollect();
			interneterror_TT.setVisibility(View.INVISIBLE);
		}
	}
	
	/** 
     * 得到自定义的progressDialog 
     * @param context 
     * @param msg 
     * @return 
     */  
    public static SafeProgressDialog createLoadingDialog(Context context) {  
  
        LayoutInflater inflater = LayoutInflater.from(context);  
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view  
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局  
        // main.xml中的ImageView  
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);  
       // TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字  
        // 加载动画  
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(  
                context, R.anim.loading_animation);  
        // 使用ImageView显示动画  
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
       // tipTextView.setText(msg);// 设置加载信息  
  
       SafeProgressDialog loadingDialog = new SafeProgressDialog(context,R.style.loading_dialog);// 创建自定义样式dialog  
       loadingDialog.setCanceledOnTouchOutside(false);
       // loadingDialog.setCancelable(false);// 不可以用“返回键”取消  
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(  
                LinearLayout.LayoutParams.MATCH_PARENT,  
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局  
        return loadingDialog;  
  
    }  
    
    
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if(requestCode==100 && resultCode==100)
    	{
    		DialogManager.showDialogSimple(getActivity(), R.string.video_upload_ready);
    	}
    	
    }
    
    
    public Handler updateVideoTishiHead = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			 String count = msg.obj.toString();
			 zan_comment_tishi_iv.setTag(count);
				ImageDownloadHelper.getInstance().imageDownload("1",getActivity(), count, zan_comment_tishi_iv,"/001yunhekeji100",
						new OnImageDownloadListener() {
							public void onImageDownload(Bitmap bitmap, String imgUrl) {
								if (bitmap != null) {
									if(imgUrl.equals(zan_comment_tishi_iv.getTag().toString()))
									{
										zan_comment_tishi_iv.setImageBitmap(bitmap);
										
									}
								} 
							}
						});
		}
	};
    
    
    private void videoLikeCommentsHeadShow(String journalVideoId,String state,String operatorId,String commentId)
    {
    	final JournalDaoDBManager journalDaoDBManager = new JournalDaoDBManager(getActivity());
		HashMap<String,String> journalMap = journalDaoDBManager.getJournalVideoInfoById(journalVideoId);
		if(journalMap != null && journalMap.keySet().size()!=0)
		{
			if(state.equals("like") && commentId==null)
			{
				List<HashMap<String, Object>> commentsData = JsonUtil.getListHashMapFromJsonArrayString(journalMap.get(TablesColumns.TABLEJOURNAL_LIKES).toString());
				int commentsLength = commentsData.size()-1;
				HashMap<String, Object> commentMap;
				for(int i=commentsLength ; i>=0 ; i--)
				{
					commentMap = commentsData.get(i);
					HashMap<String, Object> userInfoMap = JsonUtil.getHashMap(commentMap.get("user").toString());
					if(userInfoMap.get("id").toString().equals(operatorId))
					{
						HashMap<String, Object> userInfoProMap = JsonUtil.getHashMap(userInfoMap.get("profile").toString());
						if(userInfoProMap.get("avatar").toString()!=null && !userInfoProMap.get("avatar").toString().equals("null"))
						{
							Message msg = updateVideoTishiHead.obtainMessage();
							msg.obj = userInfoProMap.get("avatar").toString();
							updateVideoTishiHead.sendMessage(msg);
						}
						break;
					}
				}
			}
			else
			{
				List<HashMap<String, Object>> commentsData = JsonUtil.getListHashMapFromJsonArrayString(journalMap.get(TablesColumns.TABLEJOURNAL_COMMENTS).toString());
				int commentsLength = commentsData.size()-1;
				HashMap<String, Object> commentMap;
				for(int i=commentsLength ; i>=0 ; i--)
				{
					commentMap = commentsData.get(i);
					if(commentMap.get("id").toString().equals(commentId))
					{
						HashMap<String, Object> userInfoMap = JsonUtil.getHashMap(commentMap.get("user").toString());
						HashMap<String, Object> userInfoProMap = JsonUtil.getHashMap(userInfoMap.get("profile").toString());
						if(userInfoProMap.get("avatar").toString()!=null && !userInfoProMap.get("avatar").toString().equals("null") && !userInfoProMap.get("avatar").toString().equals(YunTongXun.UserHeadImageDefault))
						{
							Message msg = updateVideoTishiHead.obtainMessage();
							msg.obj = userInfoProMap.get("avatar").toString();
							updateVideoTishiHead.sendMessage(msg);
						}
						break;
					}
				}
			}
		}
		else
		{
			mainview.findViewById(R.id.mainfragment_video_et_head).setVisibility(View.INVISIBLE);
		}
    }
    
}
