package com.xinnongyun.activity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.imageload.ImageDownloadHelper;
import com.xinnongyun.imageload.ImageDownloadHelper.OnImageDownloadListener;
import com.xinnongyun.json.JsonUtil;
import com.xinnongyun.net.NetConnectionCollector.CollectorSuccessCallBack;
import com.xinnongyun.net.NetConnectionCommentsZan;
import com.xinnongyun.net.NetConnectionCommentsZan.JournalComments_ZanSuccessCallBack;
import com.xinnongyun.net.NetConnectionFarm;
import com.xinnongyun.net.NetConnectionFarm.FarmSuccessCallBack;
import com.xinnongyun.net.NetConnectionJournal;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.JournalDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.sqlite.WeatherStationDaoDBManager;
import com.xinnongyun.utils.DialogManager;
import com.xinnongyun.utils.InputTools;
import com.xinnongyun.utils.ShareUtils;
import com.xinnongyun.utils.StringUtil;
import com.xinnongyun.view.CircleImageView;
import com.xinnongyun.view.IconFontTextView;

/**
 * 播放视频通知中的视频
 * @author sm
 *
 */
public class JournalVideoNotifyPlayActivity extends Activity implements OnClickListener{

	private boolean isClose = false;
	private boolean isShow = false;
	private String journalvideoid;
	private ImageView journalVideoReturn,share,popdelete_iv,video_thumb_iv,userShowHead,userHead,iv_play_stop;
	private RelativeLayout video_thumb_rl,journalvideo_show,userVideoInfo_rl;
	private LinearLayout play_stop_ll;
	private VideoView journalVideoView;
	private SeekBar video_progress;
	private TextView videoshow_time,userShowName,userShowFarmName,videoShowFarmAddress,viewShowContent,userName,farmName,userAddress,videoRecordLocalTime,timestart,timesend;
	private HorizontalScrollView tanmuScroll;
	public static RelativeLayout tanmurl;
	private int preparedCount = 0;
	public static int screenWidth =0;
	//判断是否有小站
	private boolean hasWeatherStation = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_journalvideo_play);
		Intent intent = getIntent();
		journalvideoid = intent.getStringExtra("journalvideoid");
		initialView();
		intialWeatherStation();
		initilPinglun();
		initialTanMu();
	}
	
	private void intialWeatherStation()
	{
		WeatherStationDaoDBManager stationDaoDBManager = new WeatherStationDaoDBManager(this);
		List<HashMap<String, String>> stations = stationDaoDBManager.getAllWeatherStation();
		if(stations!=null && stations.size()>0)
		{
			SpannableString sp1 = new SpannableString("PM2.5");
			sp1.setSpan(new RelativeSizeSpan(0.5f), 2, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			sp1.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 2, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //粗体
			((TextView)findViewById(R.id.PM25)).setText(sp1);
			hasWeatherStation = true;
			HashMap<String, String> timeStation = stationDaoDBManager.getWeatherStationLastUpdateTime(stations.get(0).get("id"));
			if(timeStation!=null && timeStation.keySet().size()>0)
			{
				try {
					((TextView)findViewById(R.id.weather_station_head_tem)).setText(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_TEM));
					((TextView)findViewById(R.id.weather_station_detail_location)).setText(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_LOCATION));
					try {
						((TextView)findViewById(R.id.haiba)).setText(StringUtil.getAlt(Double.parseDouble(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_ALT))));
					} catch (Exception e1) {
						((TextView)findViewById(R.id.haiba)).setText(StringUtil.getAlt(Double.parseDouble("0")));
					}
					((TextView)findViewById(R.id.weather_station_head_hum)).setText(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_HUM));
					((TextView)findViewById(R.id.weather_station_head_light)).setText("" + (int)Double.parseDouble(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_ILLU)));
					((TextView)findViewById(R.id.weather_station_head_coo)).setText("" + (int)Double.parseDouble(timeStation.get(TablesColumns.TABLESTATIONTIMELINR_PM25)));
				} catch (Exception e) {
				}
			}
			
		}else
		{
			findViewById(R.id.weatherStationdetailll).setVisibility(View.GONE);
			findViewById(R.id.weatherLoacationll).setVisibility(View.GONE);
		}
	}
	
	private void initilPinglun()
	{
		final EditText pinglunContent;
		final TextView pinglunSend;
		pinglunContent = (EditText) findViewById(R.id.pinglun_content);
		pinglunSend = (TextView) findViewById(R.id.pinglun_send);
		pinglunSend.setOnClickListener(
				new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						pinglunSend.setClickable(false);
						NetConnectionCommentsZan.PostNetConnection(
								NetUrls.getURL_JOURNALVIDEO_COMMENTS(journalvideoid),
								"content=" + pinglunContent.getText().toString().trim(),
								"Token " + MySharePreference.getValueFromKey(JournalVideoNotifyPlayActivity.this,MySharePreference.ACCOUNTTOKEN),
								new JournalComments_ZanSuccessCallBack() {
									
									@Override
									public void onSuccess(HashMap<String, Object> result) {
										if(result!=null && result.keySet().contains("status_code")&& result.get("status_code").toString().contains("20"))
										{
											
										}
										else
										{
											Toast.makeText(JournalVideoNotifyPlayActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
										}
										pinglunSend.setClickable(true);
									}
								});
						InputTools.HideKeyboard(pinglunContent);
						pinglunContent.setText("");
					}
				}
				);
	}
	

	//初始化弹目
	@SuppressWarnings("deprecation")
	private void initialTanMu()
	{
		screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		tanmuScroll = (HorizontalScrollView) findViewById(R.id.tanmuScroll);
		tanmuScroll.setEnabled(false);
		tanmuScroll.setClickable(false);
		tanmuScroll.setPressed(false);
		tanmuScroll.setSelected(false);
		tanmurl = (RelativeLayout) findViewById(R.id.tanmurl);
		if(journalMap==null || journalMap.keySet().size()==0)
		{
			finish();
			return;
		}
		if (journalMap.get(TablesColumns.TABLEJOURNAL_LIKES).toString()
				.length() < 3) {
			if (journalMap.get(TablesColumns.TABLEJOURNAL_COMMENTS)
					.toString().length() < 3) {
				return;
			} else {
				List<HashMap<String, Object>> commentsData = JsonUtil
						.getListHashMapFromJsonArrayString(journalMap
								.get(TablesColumns.TABLEJOURNAL_COMMENTS)
								.toString());
				for(HashMap<String, Object> map : commentsData)
				{
					View view = LayoutInflater.from(this).inflate(R.layout.video_pinglun_tanmu, null);
					TextView userScrollName,userScrollComment;
					userScrollName = (TextView) view.findViewById(R.id.userName);
					userScrollComment = (TextView) view.findViewById(R.id.userComment);
					userScrollComment.setText(map.get("content").toString());
					final CircleImageView userScrollHead = (CircleImageView) view.findViewById(R.id.userHead);
					
					HashMap<String, Object> userInfoMap = JsonUtil.getHashMap(map.get("user").toString());
				
						HashMap<String, Object> userInfoProMap = JsonUtil.getHashMap(userInfoMap.get("profile").toString());
						if(userInfoProMap.get("nickname").toString()!=null && !userInfoProMap.get("nickname").toString().equals("null"))
						{
							userScrollName.setText(userInfoProMap.get("nickname").toString());
						}
						else
						{
							userScrollName.setText(R.string.farming_being_name_default);
						}
						
						
						userScrollHead.setImageResource(R.drawable.head_100);
						if(userInfoProMap.get("avatar").toString()!=null && !userInfoProMap.get("avatar").toString().equals("null"))
						{
							userScrollHead.setTag(userInfoProMap.get("avatar").toString());
							ImageDownloadHelper.getInstance().imageDownload(null,JournalVideoNotifyPlayActivity.this, userInfoProMap.get("avatar").toString(), userScrollHead,"/001yunhekeji100",
									new OnImageDownloadListener() {
										public void onImageDownload(Bitmap bitmap, String imgUrl) {
											if (bitmap != null) {
												if(imgUrl.equals(userScrollHead.getTag().toString()))
												{
													userScrollHead.setImageBitmap(bitmap);
												}
											} 
										}
									});
						}
					tanmurl.addView(view);
				}
				
				
			}
		} else {
			List<HashMap<String, Object>> likesMapData = JsonUtil
					.getListHashMapFromJsonArrayString(journalMap
							.get(TablesColumns.TABLEJOURNAL_LIKES)
							.toString());
			for(HashMap<String, Object> map : likesMapData)
			{
				View view = LayoutInflater.from(this).inflate(R.layout.video_pinglun_tanmu, null);
				TextView userScrollName,userScrollComment;
				userScrollName = (TextView) view.findViewById(R.id.userName);
				userScrollComment = (TextView) view.findViewById(R.id.userComment);
				userScrollComment.setText("");
				Drawable drawable= getResources().getDrawable(R.drawable.zan_hdpi);  
				/// 这一步必须要做,否则不会显示.  
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				userScrollComment.setCompoundDrawables(drawable, null, null, null);
				final CircleImageView userScrollHead = (CircleImageView) view.findViewById(R.id.userHead);
				HashMap<String, Object> userInfoMap = JsonUtil.getHashMap(map.get("user").toString());
				
				HashMap<String, Object> userInfoProMap = JsonUtil.getHashMap(userInfoMap.get("profile").toString());
				if(userInfoProMap.get("nickname").toString()!=null && !userInfoProMap.get("nickname").toString().equals("null"))
				{
					userScrollName.setText(userInfoProMap.get("nickname").toString());
				}
				else
				{
					userScrollName.setText(R.string.farming_being_name_default);
				}
				
				
				userScrollHead.setImageResource(R.drawable.head_100);
				if(userInfoProMap.get("avatar").toString()!=null && !userInfoProMap.get("avatar").toString().equals("null"))
				{
					userScrollHead.setTag(userInfoProMap.get("avatar").toString());
					ImageDownloadHelper.getInstance().imageDownload(null,JournalVideoNotifyPlayActivity.this, userInfoProMap.get("avatar").toString(), userScrollHead,"/001yunhekeji100",
							new OnImageDownloadListener() {
								public void onImageDownload(Bitmap bitmap, String imgUrl) {
									if (bitmap != null) {
										if(imgUrl.equals(userScrollHead.getTag().toString()))
										{
											userScrollHead.setImageBitmap(bitmap);
										}
									} 
								}
							});
				}
				tanmurl.addView(view);
			}
			
			if (journalMap.get(TablesColumns.TABLEJOURNAL_COMMENTS)
					.toString().length() < 3) {
			} else {
				List<HashMap<String, Object>> commentsData = JsonUtil
						.getListHashMapFromJsonArrayString(journalMap
								.get(TablesColumns.TABLEJOURNAL_COMMENTS)
								.toString());
				for(HashMap<String, Object> map : commentsData)
				{
					View view = LayoutInflater.from(this).inflate(R.layout.video_pinglun_tanmu, null);
					TextView userScrollName,userScrollComment;
					userScrollName = (TextView) view.findViewById(R.id.userName);
					userScrollComment = (TextView) view.findViewById(R.id.userComment);
					userScrollComment.setText(map.get("content").toString());
					final ImageView userScrollHead = (ImageView) view.findViewById(R.id.userHead);
					
					HashMap<String, Object> userInfoMap = JsonUtil.getHashMap(map.get("user").toString());
				
						HashMap<String, Object> userInfoProMap = JsonUtil.getHashMap(userInfoMap.get("profile").toString());
						if(userInfoProMap.get("nickname").toString()!=null && !userInfoProMap.get("nickname").toString().equals("null"))
						{
							userScrollName.setText(userInfoProMap.get("nickname").toString());
						}
						else
						{
							userScrollName.setText(R.string.farming_being_name_default);
						}
						
						
						userScrollHead.setImageResource(R.drawable.head_100);
						if(userInfoProMap.get("avatar").toString()!=null && !userInfoProMap.get("avatar").toString().equals("null"))
						{
							userScrollHead.setTag(userInfoProMap.get("avatar").toString());
							ImageDownloadHelper.getInstance().imageDownload(null,JournalVideoNotifyPlayActivity.this, userInfoProMap.get("avatar").toString(), userScrollHead,"/001yunhekeji100",
									new OnImageDownloadListener() {
										public void onImageDownload(Bitmap bitmap, String imgUrl) {
											if (bitmap != null) {
												if(imgUrl.equals(userScrollHead.getTag().toString()))
												{
													userScrollHead.setImageBitmap(bitmap);
												}
											} 
										}
									});
						}
					tanmurl.addView(view);
				}
				
			}
		}
		
		
		
		
		LinearLayout.LayoutParams lpf = new android.widget.LinearLayout.LayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT);
		lpf.topMargin = 20;
		lpf.leftMargin = getWindowManager().getDefaultDisplay().getWidth();
		tanmurl.setLayoutParams(lpf);
	}
	
	@SuppressWarnings("deprecation")
	private void initialView()
	{
		videoHandler = new Handler();
		video_progress = (SeekBar) findViewById(R.id.video_progress);
		video_progress.setClickable(false);
		video_progress.setEnabled(false);
		timesend = (TextView) findViewById(R.id.timesend);
		timestart = (TextView) findViewById(R.id.timestart);
		journalVideoReturn = (ImageView) findViewById(R.id.journalVideoReturn);
		journalVideoReturn.setOnClickListener(this);
		video_thumb_rl = (RelativeLayout) findViewById(R.id.video_thumb_rl);
		video_thumb_rl.setLayoutParams(new LinearLayout.LayoutParams(getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getWidth()));
		share = (ImageView) findViewById(R.id.share);
		share.setOnClickListener(this);
		play_stop_ll = (LinearLayout) findViewById(R.id.play_stop_ll);
		popdelete_iv =  (ImageView) findViewById(R.id.popdelete_iv);
		iv_play_stop = (ImageView) findViewById(R.id.iv_play_stop);
		iv_play_stop.setOnClickListener(this);
		popdelete_iv.setOnClickListener(this);
		video_thumb_iv = (ImageView) findViewById(R.id.video_thumb_iv);
		userShowHead = (ImageView) findViewById(R.id.userShowHead);
		userHead = (ImageView) findViewById(R.id.userHead);
		journalVideoView = (VideoView) findViewById(R.id.journalVideoView);
		journalVideoView.setOnTouchListener(
				new OnTouchListener() {
					
					@Override
					public boolean onTouch(View arg0, MotionEvent arg1) {
						if(!isPlaying)
						{
							return false;
						}
						if(play_stop_ll.isShown())
						{
							play_stop_ll.setVisibility(View.INVISIBLE);
						}
						else
						{
							play_stop_ll.setVisibility(View.VISIBLE);
						}
						return false;
					}
				}
				);
		
		journalVideoView.setOnPreparedListener(new OnPreparedListener() {
			public void onPrepared(MediaPlayer mp) {
            	if(dg!=null)
            	{
            		dg.dismiss();
            	}
            	isShow = true;
            	videoHandler.postDelayed(timeVideoRun,1000);
                journalVideoView.start();
                if(preparedCount  == 0)
                {
                	preparedCount ++;
	                NetConnectionCommentsZan.PostNetConnection(
							NetUrls.getURL_JOURNALVIDEO_VIEWS(journalvideoid)
							, "os_version="+Build.VERSION.SDK_INT+"&app_version=2",
							"Token " + MySharePreference.getValueFromKey(JournalVideoNotifyPlayActivity.this,MySharePreference.ACCOUNTTOKEN), null);
                }
            }
        });
		
		journalVideoView.setOnErrorListener(
				new OnErrorListener() {
					@Override
					public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
						if(dg!=null)
		            	{
		            		dg.dismiss();
		            	}
						DialogManager.showDialogSimple(JournalVideoNotifyPlayActivity.this, R.string.video_journal_load_fail);
						return true;
					}
				}
				);
		journalVideoView.setOnCompletionListener(
				new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer arg0) {
						journalVideoView.requestFocus();
						journalVideoView.seekTo(0);
						iv_play_stop.setImageResource(R.drawable.begin);
						timeFileCount = 0;
						isShow = false;
						videoHandler.removeCallbacks(timeVideoRun);
						videoRecordLocalTime.setText(StringUtil.removeDateZero(filesTime[0].replace("T", " ")));
						//iv_play_stop.setImageResource(R.drawable.begin);
						android.widget.LinearLayout.LayoutParams mlp =((android.widget.LinearLayout.LayoutParams)tanmurl.getLayoutParams());
						mlp.topMargin = 20;
						mlp.leftMargin = 0;
						tanmurl.setLayoutParams(mlp);
						tanmurl.setVisibility(View.VISIBLE);
					}
				}
				);
		
		journalvideo_show = (RelativeLayout) findViewById(R.id.journalvideo_show);
		videoshow_time = (TextView) findViewById(R.id.videoshow_time);
		userShowName = (TextView) findViewById(R.id.userShowName);
		userShowFarmName = (TextView) findViewById(R.id.userShowFarmName);
		videoShowFarmAddress = (TextView) findViewById(R.id.videoShowFarmAddress);
		viewShowContent = (TextView) findViewById(R.id.viewShowContent);
		userVideoInfo_rl = (RelativeLayout) findViewById(R.id.userVideoInfo_rl);
		userName = (TextView) findViewById(R.id.userName);
		farmName = (TextView) findViewById(R.id.farmName);
		userAddress = (TextView) findViewById(R.id.userAddress);
		videoRecordLocalTime = (TextView) findViewById(R.id.videoRecordLocalTime);
		JournalDaoDBManager journalDaoDBManager = new JournalDaoDBManager(JournalVideoNotifyPlayActivity.this);
		journalMap = journalDaoDBManager.getJournalVideoInfoById(journalvideoid);
		if(journalMap==null || journalMap.keySet().size()==0)
		{
			Toast.makeText(JournalVideoNotifyPlayActivity.this, "信息不存在", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		
		
		
		
		video_thumb_iv.setTag(journalMap.get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString());
		ImageDownloadHelper.getInstance().imageDownload(null,JournalVideoNotifyPlayActivity.this, journalMap.get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(), video_thumb_iv,"/001yunhekeji100",
				new OnImageDownloadListener() {
					public void onImageDownload(Bitmap bitmap, String imgUrl) {
						if (bitmap != null) {
							if(imgUrl.equals(video_thumb_iv.getTag().toString()))
							{
								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
									video_thumb_iv.setBackground(new BitmapDrawable(bitmap));
								} else {
									video_thumb_iv.setBackgroundDrawable(new BitmapDrawable(bitmap));
								}
							}
						} 
					}
				});
		
		
		List<HashMap<String, Object>> segemntsTime= JsonUtil.getListHashMapFromJsonArrayString(journalMap.get(TablesColumns.TABLEJOURNAL_SEGMENTS).toString());
		if(segemntsTime!=null && segemntsTime.size()>0)
		{
			int segLength = segemntsTime.size();
			filesTime = new String[segLength*2];
			for(int i = 0;i<segLength;i++)
			{
				filesTime[2 * i] = segemntsTime.get(segLength-1-i).get("start").toString();
				filesTime[2 * i + 1] = segemntsTime.get(segLength-1-i).get("end").toString();
			}
		}
		if(filesTime==null || filesTime.length<2)
		{
			Toast.makeText(this, "数据异常", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		videoRecordLocalTime.setText(StringUtil.removeDateZero(filesTime[0].replace("T", " ")));
		
		
		
		videoshow_time.setText(journalMap.get(TablesColumns.TABLEJOURNAL_CREATEAT).substring(0, 11).replaceFirst("-", "年").replaceFirst("-", "月").replaceFirst("T", "日"));
		HashMap<String, Object> userInfoMap = JsonUtil.getHashMap(journalMap.get(TablesColumns.TABLEJOURNAL_USER).toString());
		if(!userInfoMap.get("id").toString().equals(MySharePreference.getValueFromKey(JournalVideoNotifyPlayActivity.this, MySharePreference.ACCOUNTID)))
		{
			popdelete_iv.setVisibility(View.GONE);
		}
		
		HashMap<String, Object> userInfoProMap = JsonUtil.getHashMap(userInfoMap.get("profile").toString());
		userShowHead.setImageResource(R.drawable.head_100);
		userHead.setImageResource(R.drawable.head_100);
		
		if(userInfoProMap.get("avatar").toString()!=null && !userInfoProMap.get("avatar").toString().equals("null"))
		{
			userShowHead.setTag(userInfoProMap.get("avatar").toString());
			userHead.setTag(userInfoProMap.get("avatar").toString());
			
			ImageDownloadHelper.getInstance().imageDownload(null,JournalVideoNotifyPlayActivity.this, userInfoProMap.get("avatar").toString(), userHead,"/001yunhekeji100",
					new OnImageDownloadListener() {
						public void onImageDownload(Bitmap bitmap, String imgUrl) {
							if (bitmap != null) {
								if(imgUrl.equals(userHead.getTag().toString()))
								{
									userHead.setImageBitmap(bitmap);
								}
							} 
						}
					});
			ImageDownloadHelper.getInstance().imageDownload(null,JournalVideoNotifyPlayActivity.this, userInfoProMap.get("avatar").toString(), userShowHead,"/001yunhekeji100",
					new OnImageDownloadListener() {
						public void onImageDownload(Bitmap bitmap, String imgUrl) {
							if (bitmap != null) {
								if(imgUrl.equals(userShowHead.getTag().toString()))
								{
									userShowHead.setImageBitmap(bitmap);
								}
							} 
						}
					});
		}
		if(userInfoProMap.get("nickname").toString()!=null && !userInfoProMap.get("nickname").toString().equals("null"))
		{
			userShowName.setText(userInfoProMap.get("nickname").toString());
			userName.setText(userInfoProMap.get("nickname").toString());
		}
		else
		{
			userShowName.setText(R.string.farming_being_name_default);
			userName.setText(R.string.farming_being_name_default);
		}
		
		NetConnectionFarm.FarmGetNetOneConnection(
				NetUrls.URL_FARM_CREATE + journalMap.get(TablesColumns.TABLEJOURNAL_FARM) +"/", null, null,
				new FarmSuccessCallBack() {
					@Override
					public void onSuccess(HashMap<String, Object> result) {
						if(result!=null && result.values().size()>1)
						{
							userShowFarmName.setText(result.get("name").toString());
							farmName.setText(result.get("name").toString());
						}
					}
				});
		
		videoShowFarmAddress.setText(journalMap.get(TablesColumns.TABLEJOURNAL_LOCATION));
		viewShowContent.setText(journalMap.get(TablesColumns.TABLEJOURNAL_CONTENT));
		userAddress.setText(journalMap.get(TablesColumns.TABLEJOURNAL_LOCATION));
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				SystemClock.sleep(3000);
				if(!isClose)
				{
					handler.sendEmptyMessage(0);
				}
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(isCloseActivity)
				{
					progressHandler.sendEmptyMessage(0);
					SystemClock.sleep(10);
				}
			}
		}).start();
	}
	private boolean isCloseActivity = true;
	private boolean playClick = false;
	private boolean isPlaying = false;
	private Dialog dg;
	private Handler handler = new Handler(){
		

		public void handleMessage(Message msg) {
			if(!isClose)
			{
				try {
					dg = MainFragment.createLoadingDialog(JournalVideoNotifyPlayActivity.this);
					dg.show();
					video_thumb_iv.setVisibility(View.INVISIBLE);
					journalvideo_show.setVisibility(View.INVISIBLE);
					userVideoInfo_rl.setVisibility(View.VISIBLE);

					playClick = true;
					journalVideoView.setVideoURI(Uri.parse(journalMap.get(TablesColumns.TABLEJOURNAL_QINIUKEY).toString()));
					journalVideoView.requestFocus();
					journalVideoView.seekTo(0);
					isPlaying = true;
				} catch (Exception e) {
					if(dg!=null)
					dg.dismiss();
				}
			}
		}
	};
	private HashMap<String,String> journalMap;
	
	
	
	@Override
	protected void onPause() {
		if(isPlaying)
		{
			journalVideoView.pause();
		}
		if(dg!=null)
		{
			dg.dismiss();
		}
		videoHandler.removeCallbacks(timeVideoRun);
		isShow = false;
		super.onPause();
	}
	
	
	int iii = 0;
	private Handler progressHandler = new Handler(){
		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {
			if(isShow)
			{
				if(!journalVideoView.isPlaying())
				{
					return;
				}
				int end = Integer.parseInt(new BigDecimal((double)journalVideoView.getDuration()/1000.00).setScale(0, BigDecimal.ROUND_HALF_UP)+"");
				int start = Integer.parseInt(new BigDecimal((double)journalVideoView.getCurrentPosition()/1000.00).setScale(0, BigDecimal.ROUND_HALF_UP)+"");
				timesend.setText(String.format("%d:%02d", end/60, end%60));
				timestart.setText(String.format("%d:%02d", start/60, start%60));
				video_progress.setMax(journalVideoView.getDuration());
				video_progress.setProgress(journalVideoView.getCurrentPosition());
				
				if(iii==0)
				{
					iii++;
					if(tanmurl.getChildCount()>0)
					{
						int widthScreent = getWindowManager().getDefaultDisplay().getWidth();
						
						int count = tanmurl.getChildCount();
						for(int j=0;j<count;j++)
						{
							RelativeLayout.LayoutParams lpChild = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
							if(j==0)
							{
								lpChild.leftMargin = widthScreent;
								tanmurl.getChildAt(j).setLayoutParams(lpChild);
								continue;
							}
							if(j%2==1)
							{
								if(!hasWeatherStation)
								{
									lpChild.topMargin = tanmurl.getChildAt(0).getHeight() +20;
								}
							}
							lpChild.leftMargin = widthScreent + j*(int)(200*((end - widthScreent/200.0)/(count-1)));
							
							tanmurl.getChildAt(j).setLayoutParams(lpChild);
						}
					}
				}
				
				android.widget.LinearLayout.LayoutParams mlp =((android.widget.LinearLayout.LayoutParams)tanmurl.getLayoutParams());
				mlp.topMargin = 20;
				if(iii==1)
				{
					iii=2;
					mlp.leftMargin=0;
				}
				else
				{
					mlp.leftMargin += -2;
				}
				
				if(mlp.leftMargin<0 && Math.abs(mlp.leftMargin)>=tanmurl.getWidth())
				{
					tanmurl.setVisibility(View.INVISIBLE);
					mlp.leftMargin = getWindowManager().getDefaultDisplay().getWidth();
					//mlp.rightMargin = -(tanmurl.getWidth() - getWindowManager().getDefaultDisplay().getWidth());
				}
				tanmurl.setLayoutParams(mlp);
			}
		}
	};
	
	
	
	
	@Override
	protected void onResume() {
		if(isPlaying && playClick)
		{
			try {
				journalVideoView.setVideoURI(Uri.parse(journalMap.get(TablesColumns.TABLEJOURNAL_QINIUKEY).toString()));
				//journalVideoView.start();
				iv_play_stop.setImageResource(R.drawable.suspend);
			} catch (Exception e) {
				
			}
		}
		
		if(dg!=null)
			dg.dismiss();
		super.onResume();
	}
	
	
	protected void onDestroy() {
		isClose = true;
		isCloseActivity = true;
		super.onDestroy();
	};
	
	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.journalVideoReturn:
			finish();
			break;
		case R.id.iv_play_stop:
			if(journalVideoView.isPlaying())
			{
				isShow = false;
				videoHandler.removeCallbacks(timeVideoRun);
				iv_play_stop.setImageResource(R.drawable.begin);
				journalVideoView.pause();
			}
			else
			{
				videoHandler.postDelayed(timeVideoRun, 1000);
				isShow = true;
				iv_play_stop.setImageResource(R.drawable.suspend);
				journalVideoView.start();
			}
			break;

		case R.id.popdelete_iv:
			btn_PopupMenu(popdelete_iv);
			break;
		case R.id.popdelet_ll:
			popWindow.dismiss();
			showDialogDelete(JournalVideoNotifyPlayActivity.this);
			break;
		case R.id.share:
			
			View popContent = LayoutInflater.from(JournalVideoNotifyPlayActivity.this).inflate(R.layout.sharepopwindow, null);
			PopupWindow popWindow = new PopupWindow(popContent,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			((IconFontTextView)popContent.findViewById(R.id.shareWinXinF)).setText(String.valueOf(new char[]{0xe645}));
			((IconFontTextView)popContent.findViewById(R.id.shareWinXinFC)).setText(String.valueOf(new char[]{0xe659}));
			((IconFontTextView)popContent.findViewById(R.id.shareXinLang)).setText(String.valueOf(new char[]{0xe646}));
			((IconFontTextView)popContent.findViewById(R.id.shareQQF)).setText(String.valueOf(new char[]{0xe657}));
			((IconFontTextView)popContent.findViewById(R.id.shareQQZ)).setText(String.valueOf(new char[]{0xe658}));
			((IconFontTextView)popContent.findViewById(R.id.shareWinXinF)).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							ShareUtils.shareWXFriend(JournalVideoNotifyPlayActivity.this,
									journalMap.get(TablesColumns.TABLEJOURNAL_ID).toString(),
									journalMap.get(TablesColumns.TABLEJOURNAL_CONTENT).toString(),
									journalMap.get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(),null);
						}
					}
					);
			((IconFontTextView)popContent.findViewById(R.id.shareWinXinFC)).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							ShareUtils.shareXWFriedCircle(JournalVideoNotifyPlayActivity.this,
									journalMap.get(TablesColumns.TABLEJOURNAL_ID).toString(),
									journalMap.get(TablesColumns.TABLEJOURNAL_CONTENT).toString(),
									journalMap.get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(),null);
						}
					}
					);
			((IconFontTextView)popContent.findViewById(R.id.shareXinLang)).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							ShareUtils.shareXinLang(JournalVideoNotifyPlayActivity.this,
									journalMap.get(TablesColumns.TABLEJOURNAL_ID).toString(),
									journalMap.get(TablesColumns.TABLEJOURNAL_CONTENT).toString(),
									journalMap.get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(),null);
						}
					}
					);
			((IconFontTextView)popContent.findViewById(R.id.shareQQF)).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							ShareUtils.shareQQF(JournalVideoNotifyPlayActivity.this,
									journalMap.get(TablesColumns.TABLEJOURNAL_ID).toString(),
									journalMap.get(TablesColumns.TABLEJOURNAL_CONTENT).toString(),
									journalMap.get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(),null);
						}
					}
					);
			((IconFontTextView)popContent.findViewById(R.id.shareQQZ)).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							ShareUtils.shareQQZ(JournalVideoNotifyPlayActivity.this,
									journalMap.get(TablesColumns.TABLEJOURNAL_ID).toString(),
									journalMap.get(TablesColumns.TABLEJOURNAL_CONTENT).toString(),
									journalMap.get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(),null);
						}
					}
					);
			popWindow.setBackgroundDrawable(new BitmapDrawable());
			popWindow.setFocusable(true);
			popWindow.setOutsideTouchable(true);
			popWindow.showAtLocation(share, Gravity.CENTER, 0, 0);

			break;
		default:
			break;
		}
	}
	
	
	
	
	
	
	private void showDialogDelete(Context context)
	{
		final AlertDialog dlg = new AlertDialog.Builder(context).create();
		dlg.setCancelable(false);
		dlg.show();
		Window window = dlg.getWindow();
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.tishi_show_dialog_twobtn);
        ((TextView) window.findViewById(R.id.dialog_content)).setText(R.string.video_journal_usered_delete);
		
		// 为确认按钮添加事件,执行退出应用操作
		final Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				ok.setClickable(false);
				NetConnectionJournal.JournalDeleteNetConnection(
						NetUrls.URL_VIDEO_CREATE + journalvideoid +"/", null,
						"Token " + MySharePreference.getValueFromKey(JournalVideoNotifyPlayActivity.this,MySharePreference.ACCOUNTTOKEN),
						new CollectorSuccessCallBack() {
							@Override
							public void onSuccess(HashMap<String, Object> result) {
								ok.setClickable(true);
								if(result!=null && result.get("status_code").toString().contains("20"))
								{
									JournalDaoDBManager daoDBManager = new JournalDaoDBManager(JournalVideoNotifyPlayActivity.this);
									daoDBManager.deleteJournalById(journalvideoid);
									finish();
								}
								else if(result!=null && result.get("status_code").toString().contains("40"))
								{
									JournalDaoDBManager daoDBManager = new JournalDaoDBManager(JournalVideoNotifyPlayActivity.this);
									daoDBManager.deleteJournalById(journalvideoid);
									finish();
								}
								else
								{
									DialogManager.showDialogSimple(JournalVideoNotifyPlayActivity.this, R.string.video_journal_usered_delete_fail);
								}
								dlg.dismiss();
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
	
	
	
	
	private PopupWindow popWindow;
	@SuppressWarnings("deprecation")
	public void btn_PopupMenu(View view) {
		View popContent = LayoutInflater.from(view.getContext()).inflate(R.layout.pop_delete, null);
		popWindow = new PopupWindow(popContent,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);
		popWindow.showAsDropDown(view);
		LinearLayout popll = (LinearLayout) popContent.findViewById(R.id.popdelet_ll);
		popll.setOnClickListener(this);
		
	}
	
	private String[] filesTime;
	private int timeFileCount = 0;
	
	private Handler videoHandler;
	 private Runnable timeVideoRun = new Runnable() {

		@Override
		public void run() {
			if(!isShow)
			{
				return;
			}
			if(filesTime==null || filesTime.length<2 || filesTime.length%2 != 0)
			{
				return;
			}
			if(timeFileCount+1<filesTime.length)
			{
				if(StringUtil.removeDateZero(filesTime[timeFileCount+1].replace("T", " ")).
						equals(videoRecordLocalTime.getText().toString()))
				{
					timeFileCount+=2;
					if(timeFileCount < filesTime.length)
					{
						videoRecordLocalTime.setText(StringUtil.removeDateZero(filesTime[timeFileCount].replace("T", " ")));
					}
				}else
				{
					videoRecordLocalTime.setText(StringUtil.removeDateZero(StringUtil.getCurrentDataSALL(StringUtil.getSpecifiedDateTimeBySeconds(
							StringUtil.stringConverToDate(videoRecordLocalTime.getText().toString().replace("年", "-").replace("月", "-").replace("日", "")), 1))
							.replace("年", "-").replace("月", "-").replace("日", "")));
				}
			}
			videoHandler.postDelayed(timeVideoRun, 1000);
		}
	 };
}
