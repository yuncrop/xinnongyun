package com.xinnongyun.activity;

import java.util.HashMap;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.xinnongyun.adapter.CommentListAdapter;
import com.xinnongyun.adapter.MyListView;
import com.xinnongyun.adapter.ZanGridViewAdapter;
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
import com.xinnongyun.net.NetConnectionJournal.JournalSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.JournalDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.sqlite.WeatherStationDaoDBManager;
import com.xinnongyun.utils.DialogManager;
import com.xinnongyun.utils.InputTools;
import com.xinnongyun.utils.ShareUtils;
import com.xinnongyun.utils.StringUtil;
import com.xinnongyun.view.IconFontTextView;
import com.xinnongyun.view.MyGridView;

/**
 * 视频日记详情
 * 
 * @author sm
 * 
 */
public class JournalVideoDetailActivity extends Activity {

	private View comments_zan_div;
	private LinearLayout comments_zan_ll, zanll;
	private MyGridView zan_list_head;
	private RelativeLayout video_thumb_rl;
	private MyListView journalvideo_comments_list;
	private ImageView videoUserHead,popdelete_iv, video_thumb_iv, video_zan, video_pinglun,
	share,video_share,journalVideo_begin,return_iv;
	private TextView video_detail_head,videoContent, videoLikeCount,farmName,
			videoCommentCount, videoEyeCount,userName,userAddress,videoRecordLocalTime;
	private VideoView journalVideoView;
	private boolean isPlaying = false;
	private boolean playClick = false;
	private int preparedCount = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_fragment_video_item_detail);
		Intent intent = getIntent();
		if (intent != null) {
			journalVideoId = intent.getStringExtra("journalVideoId");
			final JournalDaoDBManager journalDaoDBManager = new JournalDaoDBManager(this);
			HashMap<String, String> map = journalDaoDBManager.getJournalVideoInfoById(journalVideoId);
			if(map==null || map.keySet().size() ==0)
			{
				NetConnectionJournal.JournalGetOneNetConnection(NetUrls.get_URL_JOURNALVIDEO_BYID(journalVideoId),
						null, null,
						new JournalSuccessCallBack() {
							@Override
							public void onSuccess(HashMap<String, Object> result) {
								if(result!=null && !result.keySet().contains("status_code"))
								{
									journalDaoDBManager.insertUploadVideo(result);
									intialView(journalVideoId);
								}
								else
								{
									finish();
								}
							}
						});
			}
			else
			{
				intialView(journalVideoId);
			}
			intialWeatherStation();
			//视频列表得到监听 
			IntentFilter filterJournalVideo = new IntentFilter();  
			filterJournalVideo.addAction(MainActivity.journalInfobroastCast); 
			registerReceiver(journalVideoInfoChange, filterJournalVideo);
		}
		SpannableString sp1 = new SpannableString("PM2.5");
		sp1.setSpan(new RelativeSizeSpan(0.5f), 2, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp1.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 2, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //粗体
		((TextView)findViewById(R.id.PM25)).setText(sp1);
	}


	private void intialWeatherStation()
	{
		WeatherStationDaoDBManager stationDaoDBManager = new WeatherStationDaoDBManager(this);
		List<HashMap<String, String>> stations = stationDaoDBManager.getAllWeatherStation();
		if(stations!=null && stations.size()>0)
		{
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

	@SuppressWarnings("deprecation")
	private void intialView(final String journalVideoId) {
		videoHandler = new Handler();
		comments_zan_ll = (LinearLayout) findViewById(R.id.comments_zan_ll);
		comments_zan_div = findViewById(R.id.commets_zan_div);
		zanll = (LinearLayout) findViewById(R.id.zanll);
		journalvideo_comments_list = (MyListView) findViewById(R.id.journalvideo_comments_list);
		video_thumb_rl = (RelativeLayout) findViewById(R.id.video_thumb_rl);
		video_thumb_rl.setLayoutParams(new LinearLayout.LayoutParams(
				getWindowManager().getDefaultDisplay().getWidth(),
				getWindowManager().getDefaultDisplay().getWidth()));
		videoUserHead = (ImageView) findViewById(R.id.userHead);
		videoContent = (TextView) findViewById(R.id.video_content);
		videoEyeCount = (TextView) findViewById(R.id.video_eye_count);
		videoLikeCount = (TextView) findViewById(R.id.zannum);
		userName = (TextView) findViewById(R.id.userName);
		videoCommentCount = (TextView) findViewById(R.id.pinglunnum);
		popdelete_iv =  (ImageView) findViewById(R.id.popdelete_iv);
		video_thumb_iv = (ImageView) findViewById(R.id.video_thumb_iv);
		video_zan = (ImageView) findViewById(R.id.zan);
		video_pinglun = (ImageView) findViewById(R.id.pinglun);
		video_share = (ImageView) findViewById(R.id.share_weixin_weibo);
		
		zan_list_head = (MyGridView) findViewById(R.id.zan_list_head);
		userAddress = (TextView) findViewById(R.id.userAddress);
		videoRecordLocalTime = (TextView) findViewById(R.id.videoRecordLocalTime);
		video_detail_head = (TextView) findViewById(R.id.video_detail_head);
		journalVideoView = (VideoView) findViewById(R.id.journalVideoView);
		return_iv = (ImageView) findViewById(R.id.return_iv);
		return_iv.setOnClickListener(
				new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						finish();
					}
				}
				);
		popdelete_iv.setOnClickListener(
				new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						btn_PopupMenu(popdelete_iv);
					}
				}
				);
		journalVideoView.setLayoutParams(new RelativeLayout.LayoutParams(
				getWindowManager().getDefaultDisplay().getWidth(),
				getWindowManager().getDefaultDisplay().getWidth()));
		journalVideoView.getHolder().setFixedSize(getWindowManager().getDefaultDisplay().getWidth(),
				getWindowManager().getDefaultDisplay().getWidth());
		journalVideo_begin = (ImageView) findViewById(R.id.journalVideo_begin);
		farmName = (TextView) findViewById(R.id.farmName);
		JournalDaoDBManager journalDaoDBManager = new JournalDaoDBManager(this);
		journalVideoInfo = journalDaoDBManager
				.getJournalVideoInfoById(journalVideoId);
		
		NetConnectionFarm.FarmGetNetOneConnection(
				NetUrls.URL_FARM_CREATE + journalVideoInfo.get(TablesColumns.TABLEJOURNAL_FARM) +"/", null, null,
				new FarmSuccessCallBack() {
					@Override
					public void onSuccess(HashMap<String, Object> result) {
						if(result!=null && result.values().size()>1)
						{
							farmName.setText(result.get("name").toString());
						}
					}
				});
		
		
		List<HashMap<String, Object>> segemntsTime= JsonUtil.getListHashMapFromJsonArrayString(journalVideoInfo.get(TablesColumns.TABLEJOURNAL_SEGMENTS).toString());
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
			Toast.makeText(JournalVideoDetailActivity.this, "数据异常", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		videoRecordLocalTime.setText(StringUtil.removeDateZero(filesTime[0].replace("T", " ")));
		if(journalVideoInfo.get(TablesColumns.TABLEJOURNAL_VIEWS).toString().length()>3)
		{
			List<HashMap<String, Object>> viewsData = JsonUtil.getListHashMapFromJsonArrayString(journalVideoInfo.get(TablesColumns.TABLEJOURNAL_VIEWS).toString());
			videoEyeCount.setText(""+ viewsData.size());
		}
		else
		{
			videoEyeCount.setText("0");
		}
		
		share = (ImageView) findViewById(R.id.share);
		share.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						View popContent = LayoutInflater.from(JournalVideoDetailActivity.this).inflate(R.layout.sharepopwindow, null);
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
										ShareUtils.shareWXFriend(JournalVideoDetailActivity.this,
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_ID).toString(),
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_CONTENT).toString(),
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(),null);
									}
								}
								);
						((IconFontTextView)popContent.findViewById(R.id.shareWinXinFC)).setOnClickListener(
								new OnClickListener() {
									@Override
									public void onClick(View arg0) {
										ShareUtils.shareXWFriedCircle(JournalVideoDetailActivity.this,
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_ID).toString(),
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_CONTENT).toString(),
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(),null);
									}
								}
								);
						((IconFontTextView)popContent.findViewById(R.id.shareXinLang)).setOnClickListener(
								new OnClickListener() {
									@Override
									public void onClick(View arg0) {
										ShareUtils.shareXinLang(JournalVideoDetailActivity.this,
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_ID).toString(),
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_CONTENT).toString(),
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(),null);
									}
								}
								);
						((IconFontTextView)popContent.findViewById(R.id.shareQQF)).setOnClickListener(
								new OnClickListener() {
									@Override
									public void onClick(View arg0) {
										ShareUtils.shareQQF(JournalVideoDetailActivity.this,
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_ID).toString(),
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_CONTENT).toString(),
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(),null);
									}
								}
								);
						((IconFontTextView)popContent.findViewById(R.id.shareQQZ)).setOnClickListener(
								new OnClickListener() {
									@Override
									public void onClick(View arg0) {
										ShareUtils.shareQQZ(JournalVideoDetailActivity.this,
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_ID).toString(),
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_CONTENT).toString(),
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(),null);
									}
								}
								);
						popWindow.setBackgroundDrawable(new BitmapDrawable());
						popWindow.setFocusable(true);
						popWindow.setOutsideTouchable(true);
						popWindow.showAtLocation(video_share, Gravity.CENTER, 0, 0);
					}
				}
				);
		video_share.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						
						View popContent = LayoutInflater.from(JournalVideoDetailActivity.this).inflate(R.layout.sharepopwindow, null);
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
										ShareUtils.shareWXFriend(JournalVideoDetailActivity.this,
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_ID).toString(),
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_CONTENT).toString(),
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(),null);
									}
								}
								);
						((IconFontTextView)popContent.findViewById(R.id.shareWinXinFC)).setOnClickListener(
								new OnClickListener() {
									@Override
									public void onClick(View arg0) {
										ShareUtils.shareXWFriedCircle(JournalVideoDetailActivity.this,
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_ID).toString(),
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_CONTENT).toString(),
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(),null);
									}
								}
								);
						((IconFontTextView)popContent.findViewById(R.id.shareXinLang)).setOnClickListener(
								new OnClickListener() {
									@Override
									public void onClick(View arg0) {
										ShareUtils.shareXinLang(JournalVideoDetailActivity.this,
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_ID).toString(),
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_CONTENT).toString(),
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(),null);
									}
								}
								);
						((IconFontTextView)popContent.findViewById(R.id.shareQQF)).setOnClickListener(
								new OnClickListener() {
									@Override
									public void onClick(View arg0) {
										ShareUtils.shareQQF(JournalVideoDetailActivity.this,
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_ID).toString(),
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_CONTENT).toString(),
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(),null);
									}
								}
								);
						((IconFontTextView)popContent.findViewById(R.id.shareQQZ)).setOnClickListener(
								new OnClickListener() {
									@Override
									public void onClick(View arg0) {
										ShareUtils.shareQQZ(JournalVideoDetailActivity.this,
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_ID).toString(),
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_CONTENT).toString(),
												journalVideoInfo.get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(),null);
									}
								}
								);
						popWindow.setBackgroundDrawable(new BitmapDrawable());
						popWindow.setFocusable(true);
						popWindow.setOutsideTouchable(true);
						popWindow.showAtLocation(video_share, Gravity.CENTER, 0, 0);
					}
				}
				);
		
		
		
		
		
		String headTime = journalVideoInfo.get(TablesColumns.TABLEJOURNAL_CREATEAT).substring(5, 11).replaceFirst("-", "月").replaceFirst("T", "日");
		if(headTime.subSequence(0, 1).equals("0"))
		{
			headTime = headTime.replaceFirst("0", "");
		}
		if(headTime.substring(headTime.length()-3).startsWith("0"))
		{
			headTime = headTime.substring(0, headTime.length()-3) + headTime.substring(headTime.length()-2);
		}
		video_detail_head.setText(headTime);
		if (journalVideoInfo.get(TablesColumns.TABLEJOURNAL_LIKES).toString()
				.length() < 3) {
			if (journalVideoInfo.get(TablesColumns.TABLEJOURNAL_COMMENTS)
					.toString().length() < 3) {
				videoCommentCount.setText("0");
				videoLikeCount.setText("0");
				comments_zan_ll.setVisibility(View.GONE);
			} else {
				videoLikeCount.setText("0");
				zanll.setVisibility(View.GONE);
				comments_zan_div.setVisibility(View.GONE);
				List<HashMap<String, Object>> commentsData = JsonUtil
						.getListHashMapFromJsonArrayString(journalVideoInfo
								.get(TablesColumns.TABLEJOURNAL_COMMENTS)
								.toString());
				videoCommentCount.setText("" + commentsData.size());
				CommentListAdapter commentsAdapter = new CommentListAdapter(
						this, commentsData);
				journalvideo_comments_list.setAdapter(commentsAdapter);
			}
		} else {
			List<HashMap<String, Object>> likesMapData = JsonUtil
					.getListHashMapFromJsonArrayString(journalVideoInfo
							.get(TablesColumns.TABLEJOURNAL_LIKES)
							.toString());
			videoLikeCount.setText("" + likesMapData.size());
			zan_list_head.setAdapter(new ZanGridViewAdapter(this,
					likesMapData));
			if (journalVideoInfo.get(TablesColumns.TABLEJOURNAL_COMMENTS)
					.toString().length() < 3) {
				videoCommentCount.setText("0");
				comments_zan_div.setVisibility(View.GONE);
				journalvideo_comments_list.setVisibility(View.GONE);
			} else {

				

				List<HashMap<String, Object>> commentsData = JsonUtil
						.getListHashMapFromJsonArrayString(journalVideoInfo
								.get(TablesColumns.TABLEJOURNAL_COMMENTS)
								.toString());
				videoCommentCount.setText("" + commentsData.size());
				CommentListAdapter commentsAdapter = new CommentListAdapter(
						this, commentsData);
				journalvideo_comments_list.setAdapter(commentsAdapter);
				// 控件自身无法计算自身大小需要自己设置
			}
		}

		video_thumb_iv.setTag(journalVideoInfo.get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString());
		ImageDownloadHelper.getInstance().imageDownload(null,
				this,
				journalVideoInfo.get(TablesColumns.TABLEJOURNAL_THUMBNAIL)
						.toString(), video_thumb_iv, "/001yunhekeji100",
				new OnImageDownloadListener() {
					public void onImageDownload(Bitmap bitmap, String imgUrl) {
						if (bitmap != null) {
							if (imgUrl.equals(video_thumb_iv.getTag()
									.toString())) {
								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
									video_thumb_iv
											.setBackground(new BitmapDrawable(
													bitmap));
								} else {
									video_thumb_iv
											.setBackgroundDrawable(new BitmapDrawable(
													bitmap));
								}
							}
						}
					}
				});
		journalVideo_begin.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if(!isPlaying)
						{
							try {
								dg = MainFragment.createLoadingDialog(JournalVideoDetailActivity.this);
								dg.show();
								playClick = true;
								journalVideoView.setVideoURI(Uri.parse(journalVideoInfo.get(TablesColumns.TABLEJOURNAL_QINIUKEY).toString()));
								journalVideoView.requestFocus();
								journalVideoView.seekTo(0);
								journalVideoView.start();
								
								journalVideo_begin.setVisibility(View.INVISIBLE);
								video_thumb_iv.setVisibility(View.INVISIBLE);
								isPlaying = true;
							} catch (Exception e) {
								if(dg!=null)
								dg.dismiss();
							}
							
						}
						
					}
				}
				);
		journalVideoView.setOnErrorListener(
				new OnErrorListener() {
					@Override
					public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
						if(dg!=null)
			                dg.dismiss();
						DialogManager.showDialogSimple(JournalVideoDetailActivity.this, R.string.video_journal_load_fail);
						journalVideo_begin.setVisibility(View.VISIBLE);
						video_thumb_iv.setVisibility(View.VISIBLE);
						isPlaying = false;
						return true;
					}
				}
				);
		
		journalVideoView.setOnPreparedListener(new OnPreparedListener() {


			public void onPrepared(MediaPlayer mp) {
            	if(dg!=null)
                dg.dismiss();
            	videoHandler.postDelayed(timeVideoRun,1000);
            	journalVideoView.start();
            	if(preparedCount == 0)
            	{
            		preparedCount ++;
            		NetConnectionCommentsZan.PostNetConnection(
						NetUrls.getURL_JOURNALVIDEO_VIEWS(journalVideoId)
						, "os_version="+Build.VERSION.SDK_INT+"&app_version=2",
						"Token " + MySharePreference.getValueFromKey(JournalVideoDetailActivity.this,MySharePreference.ACCOUNTTOKEN), null);
            	}
            	
            }
        });
		
		journalVideoView.setOnCompletionListener(
				new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer arg0) {
						journalVideo_begin.setClickable(true);
						isPlaying = false;
						playClick = false;
						journalVideoView.requestFocus();
						journalVideoView.seekTo(0);
						timeFileCount = 0;
						videoHandler.removeCallbacks(timeVideoRun);
						videoRecordLocalTime.setText(StringUtil.removeDateZero(filesTime[0].replace("T", " ")));
						journalVideo_begin.setVisibility(View.VISIBLE);
						video_thumb_iv.setVisibility(View.VISIBLE);
					}
				}
				);
		
		videoContent.setText(journalVideoInfo.get(TablesColumns.TABLEJOURNAL_CONTENT).toString());
		userAddress.setText(journalVideoInfo.get(TablesColumns.TABLEJOURNAL_LOCATION).toString());
		
		HashMap<String, Object> userInfoMap = JsonUtil
				.getHashMap(journalVideoInfo.get(
						TablesColumns.TABLEJOURNAL_USER).toString());
		HashMap<String, Object> userInfoProMap = JsonUtil
				.getHashMap(userInfoMap.get("profile").toString());
		
		if(!userInfoMap.get("id").toString().equals(MySharePreference.getValueFromKey(JournalVideoDetailActivity.this, MySharePreference.ACCOUNTID)))
		{
			popdelete_iv.setVisibility(View.GONE);
		}
		if(userInfoProMap.get("nickname").toString()!=null && !userInfoProMap.get("nickname").toString().equals("null"))
		{
			userName.setText(userInfoProMap.get("nickname").toString());
		}
		else
		{
			userName.setText(R.string.farming_being_name_default);
		}
		
		videoUserHead.setImageResource(R.drawable.touxiang);
		if (userInfoProMap.get("avatar").toString() != null
				&& !userInfoProMap.get("avatar").toString().equals("null")) {
			videoUserHead.setTag(userInfoProMap.get("avatar").toString());
			ImageDownloadHelper.getInstance().imageDownload(null,this,
					userInfoProMap.get("avatar").toString(), videoUserHead,
					"/001yunhekeji100", new OnImageDownloadListener() {
						public void onImageDownload(Bitmap bitmap, String imgUrl) {
							if (bitmap != null) {
								if (imgUrl.equals(videoUserHead.getTag()
										.toString())) {
									videoUserHead.setImageBitmap(bitmap);
								}
							}
						}
					});
		}
		
		video_zan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				
				if(journalVideoInfo.get(TablesColumns.TABLEJOURNAL_LIKES).toString().length()>3)
				{
					List<HashMap<String, Object>> likesMapData = JsonUtil.getListHashMapFromJsonArrayString(journalVideoInfo.get(TablesColumns.TABLEJOURNAL_LIKES).toString());
					for(HashMap<String, Object> likeMap : likesMapData)
					{
						HashMap<String, Object> userInfoMap = JsonUtil.getHashMap(likeMap.get("user").toString());
						
						if(userInfoMap.get("id").toString().equals(MySharePreference.getValueFromKey(JournalVideoDetailActivity.this, MySharePreference.ACCOUNTID)))
						{
							Toast.makeText(JournalVideoDetailActivity.this, R.string.video_already_zan, Toast.LENGTH_SHORT).show();
							return;
						}
					}
				}
				dg = MainFragment.createLoadingDialog(JournalVideoDetailActivity.this);
				dg.show();
				NetConnectionCommentsZan.PostNetConnection(
						NetUrls.getURL_JOURNALVIDEO_LIKE(journalVideoInfo.get(
								TablesColumns.TABLEJOURNAL_ID).toString()),
						"",
						"Token "
								+ MySharePreference.getValueFromKey(
										JournalVideoDetailActivity.this,
										MySharePreference.ACCOUNTTOKEN),
						new JournalComments_ZanSuccessCallBack() {
							@Override
							public void onSuccess(HashMap<String, Object> result) {
								dg.dismiss();
								if (result != null
										&& result.get("status_code").toString()
												.contains("20")) {
									// 赞 成功
								} else {
									// 赞 失败
									Toast.makeText(JournalVideoDetailActivity.this, R.string.dianzan_fail, Toast.LENGTH_SHORT).show();
								}
							}
						});
			}
		});
		journalvideo_comments_list.setOnItemClickListener(
				new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						MainFragment.currentPinglunId = journalVideoInfo.get(TablesColumns.TABLEJOURNAL_ID).toString();
						View popContent = LayoutInflater.from(JournalVideoDetailActivity.this).inflate(R.layout.pinglun_dialog, null);
						PopupWindow popWindow = new PopupWindow(popContent,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
						popWindow.setBackgroundDrawable(new BitmapDrawable());
						popWindow.setFocusable(true);
						popWindow.setOutsideTouchable(true);
						popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);          popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
						popWindow.showAtLocation(video_pinglun, Gravity.BOTTOM, 0, 0);
						initilPinglun(popContent,popWindow);
					}
				}
				);
		
		video_pinglun.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MainFragment.currentPinglunId = journalVideoInfo.get(TablesColumns.TABLEJOURNAL_ID).toString();
				View popContent = LayoutInflater.from(JournalVideoDetailActivity.this).inflate(R.layout.pinglun_dialog, null);
				PopupWindow popWindow = new PopupWindow(popContent,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
				popWindow.setBackgroundDrawable(new BitmapDrawable());
				popWindow.setFocusable(true);
				popWindow.setOutsideTouchable(true);
				popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);          popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
				popWindow.showAtLocation(video_pinglun, Gravity.BOTTOM, 0, 0);
				initilPinglun(popContent,popWindow);
			}
		});
	}

	private Dialog dg;
	
	private void initilPinglun(View mainview,final PopupWindow popWindow)
	{
		final EditText pinglunContent;
		TextView pinglunSend;
		pinglunContent = (EditText) mainview.findViewById(R.id.pinglun_content);
		pinglunSend = (TextView) mainview.findViewById(R.id.pinglun_send);
		pinglunSend.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						NetConnectionCommentsZan.PostNetConnection(
								NetUrls.getURL_JOURNALVIDEO_COMMENTS(MainFragment.currentPinglunId),
								"content=" + pinglunContent.getText().toString().trim(),
								"Token " + MySharePreference.getValueFromKey(JournalVideoDetailActivity.this,MySharePreference.ACCOUNTTOKEN),
								new JournalComments_ZanSuccessCallBack() {
									@Override
									public void onSuccess(HashMap<String, Object> result) {
										if(result!=null && result.keySet().contains("status_code")&& result.get("status_code").toString().contains("20"))
										{
											
										}
										else
										{
											Toast.makeText(JournalVideoDetailActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
										}
									}
								});
						InputTools.HideKeyboard(pinglunContent);
						pinglunContent.setText("");
						popWindow.dismiss();
					}
				}
				);
	}
	
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
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		if(isPlaying && playClick)
		{
			
				journalVideoView.setVideoURI(Uri.parse(journalVideoInfo.get(TablesColumns.TABLEJOURNAL_QINIUKEY).toString()));
		}
		if(dg!=null)
			dg.dismiss();
		super.onResume();
	}
	
	

	
	@Override
	protected void onDestroy() {
		//取消监听
		unregisterReceiver(journalVideoInfoChange);
		super.onDestroy();
		
	}
	
	BroadcastReceiver journalVideoInfoChange = new  BroadcastReceiver(){
    	@Override
        public void onReceive(Context context, Intent intent) {
    		if(intent.getAction().equals(MainActivity.journalInfobroastCast))
    		{
    			try {
					JournalDaoDBManager journalDaoDBManager = new JournalDaoDBManager(JournalVideoDetailActivity.this);
					journalVideoInfo = journalDaoDBManager
							.getJournalVideoInfoById(journalVideoId);
					if(journalVideoInfo.get(TablesColumns.TABLEJOURNAL_VIEWS).toString().length()>3)
					{
						List<HashMap<String, Object>> viewsData = JsonUtil.getListHashMapFromJsonArrayString(journalVideoInfo.get(TablesColumns.TABLEJOURNAL_VIEWS).toString());
						videoEyeCount.setText(""+ viewsData.size());
					}
					else
					{
						videoEyeCount.setText("0");
					}
					if (journalVideoInfo.get(TablesColumns.TABLEJOURNAL_LIKES).toString()
							.length() < 3) {
						if (journalVideoInfo.get(TablesColumns.TABLEJOURNAL_COMMENTS)
								.toString().length() < 3) {
							videoCommentCount.setText("0");
							videoLikeCount.setText("0");
							comments_zan_ll.setVisibility(View.GONE);
						} else {
							videoLikeCount.setText("0");
							zanll.setVisibility(View.GONE);
							comments_zan_div.setVisibility(View.GONE);
							List<HashMap<String, Object>> commentsData = JsonUtil
									.getListHashMapFromJsonArrayString(journalVideoInfo
											.get(TablesColumns.TABLEJOURNAL_COMMENTS)
											.toString());
							videoCommentCount.setText("" + commentsData.size());
							CommentListAdapter commentsAdapter = new CommentListAdapter(
									JournalVideoDetailActivity.this, commentsData);
							journalvideo_comments_list.setAdapter(commentsAdapter);
						}
					} else {
						List<HashMap<String, Object>> likesMapData = JsonUtil
								.getListHashMapFromJsonArrayString(journalVideoInfo
										.get(TablesColumns.TABLEJOURNAL_LIKES)
										.toString());
						videoLikeCount.setText("" + likesMapData.size());
						zan_list_head.setAdapter(new ZanGridViewAdapter(JournalVideoDetailActivity.this,
								likesMapData));
						if (journalVideoInfo.get(TablesColumns.TABLEJOURNAL_COMMENTS)
								.toString().length() < 3) {
							videoCommentCount.setText("0");
							comments_zan_div.setVisibility(View.GONE);
							journalvideo_comments_list.setVisibility(View.GONE);
						} else {

							List<HashMap<String, Object>> commentsData = JsonUtil
									.getListHashMapFromJsonArrayString(journalVideoInfo
											.get(TablesColumns.TABLEJOURNAL_COMMENTS)
											.toString());
							videoCommentCount.setText("" + commentsData.size());
							CommentListAdapter commentsAdapter = new CommentListAdapter(
									JournalVideoDetailActivity.this, commentsData);
							journalvideo_comments_list.setAdapter(commentsAdapter);
						}
					}
				} catch (Exception e) {
				}
    		}
        }
    };
	private String journalVideoId;
	private HashMap<String, String> journalVideoInfo;
	
	
	private String[] filesTime;
	private int timeFileCount = 0;
	
	private Handler videoHandler;
	 private Runnable timeVideoRun = new Runnable() {

		@Override
		public void run() {
			
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
			popll.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					popWindow.dismiss();
					showDialogDelete(JournalVideoDetailActivity.this);
				}
			});
			
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
							NetUrls.URL_VIDEO_CREATE + journalVideoId +"/", null,
							"Token " + MySharePreference.getValueFromKey(JournalVideoDetailActivity.this,MySharePreference.ACCOUNTTOKEN),
							new CollectorSuccessCallBack() {
								@Override
								public void onSuccess(HashMap<String, Object> result) {
									ok.setClickable(true);
									if(result!=null && result.get("status_code").toString().contains("20"))
									{
										JournalDaoDBManager daoDBManager = new JournalDaoDBManager(JournalVideoDetailActivity.this);
										daoDBManager.deleteJournalById(journalVideoId);
										finish();
									}
									else if(result!=null && result.get("status_code").toString().contains("40"))
									{
										JournalDaoDBManager daoDBManager = new JournalDaoDBManager(JournalVideoDetailActivity.this);
										daoDBManager.deleteJournalById(journalVideoId);
										finish();
									}
									else
									{
										DialogManager.showDialogSimple(JournalVideoDetailActivity.this, R.string.video_journal_usered_delete_fail);
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
}
