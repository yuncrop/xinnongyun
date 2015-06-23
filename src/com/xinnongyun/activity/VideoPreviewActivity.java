package com.xinnongyun.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.imageload.ImageDownloadHelper;
import com.xinnongyun.imageload.ImageDownloadHelper.OnImageDownloadListener;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.utils.ImageUtils;
import com.xinnongyun.utils.StringUtil;
import com.xinnongyun.view.FullScreenVideoView;

/**
 * 本地视频预览
 * @author sm
 *
 */
public class VideoPreviewActivity extends Activity implements OnClickListener{

	private String videoPath = "";
	private FullScreenVideoView videoView;
	private Button playStop,returnRecord,nextStep;
	private SeekBar progressBar;
	private TextView timestart,timesend;
	//1播放 2暂停
	private int playOrStop = 2;
	private Handler handler;
	private int minute = 0;
	private int second = 0;
	private int timeLength = 0;
	private int timeFileCount = 0;
	private String time = "";
	private String[] filesTime;
	private String videolatlng = "";
	private TextView videoRecordLocalTime,userAddress;
	private String userLocal = "";
	private TextView userName;
	private TextView farmName;
	private ImageView userHead;
	/**
	 * 预览过程中,时间变化
	 */
	private Runnable timeRun = new Runnable() {

		@Override
		public void run() {
		
			second++;
			if (second == 60) {
				minute++;
				second = 0;
			}
			
			if(minute * 60 + second >= timeLength)
			{
				handler.removeCallbacks(timeRun);
				minute = 0;
				second = 0;
				playOrStop = 1;
				playStop.setBackgroundResource(R.drawable.begin);
				videoRecordLocalTime.setText(StringUtil.removeDateZero(filesTime[0]));
				timeFileCount = 0;
				progressBar.setProgress(0);
				
				timestart.setText("0:00");
				return;
			}
			handler.postDelayed(timeRun, 1000);
			progressBar.setProgress(minute * 60 + second);
			time = String.format("%d:%02d", minute, second);
			timestart.setText(time);
			if(timeFileCount+1 < filesTime.length)
			{
				if(StringUtil.removeDateZero(filesTime[timeFileCount+1]).
						equals(videoRecordLocalTime.getText().toString()))
				{
					timeFileCount+=2;
					if(timeFileCount < filesTime.length)
					 videoRecordLocalTime.setText(StringUtil.removeDateZero(filesTime[timeFileCount]));
				}else
				{
					videoRecordLocalTime.setText(StringUtil.removeDateZero(StringUtil.getCurrentDataSALL(StringUtil.getSpecifiedDateTimeBySeconds(
							StringUtil.stringConverToDate(videoRecordLocalTime.getText().toString().replace("年", "-").replace("月", "-").replace("日", ""))
							, 1))));
				}
			}
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_video_preview);
		Intent intent = getIntent();
		if(intent !=null)
		{
			timeLength = intent.getIntExtra("videoLength",0);
			videoPath = intent.getStringExtra("videoPath");
			filesTime = intent.getStringArrayExtra("videoFilesTime");
			userLocal = intent.getStringExtra("videoaddress");
			videolatlng = intent.getStringExtra("videolatlng");
		}
		if(filesTime==null || filesTime.length<2)
		{
			Toast.makeText(this, "数据异常", Toast.LENGTH_SHORT).show();
			setResult(10);
			finish();
			return;
		}
		Dialog dg = MainFragment.createLoadingDialog(this);
		dg.show();
		initialView();
		dg.dismiss();
	}
	
	@SuppressWarnings("deprecation")
	private void initialView()
	{
		handler = new Handler();
		videoRecordLocalTime = (TextView) findViewById(R.id.videoRecordLocalTime);
		videoRecordLocalTime.setText(StringUtil.removeDateZero(filesTime[0]));
		userAddress = (TextView) findViewById(R.id.userAddress);
		timestart = (TextView) findViewById(R.id.timestart);
		timesend = (TextView) findViewById(R.id.timesend);
		videoView = (FullScreenVideoView) findViewById(R.id.videoViewPreview);
		RelativeLayout.LayoutParams recordlp = new android.widget.RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,VideoRecordActivity.srceentHeight- VideoRecordActivity.srceenWidth -96);
		recordlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		findViewById(R.id.preview_control_ll).setLayoutParams(recordlp);
		playStop = (Button) findViewById(R.id.play_stop);
		playStop.setOnClickListener(this);
		returnRecord = (Button) findViewById(R.id.returnRecord);
		returnRecord.setOnClickListener(this);
		nextStep = (Button) findViewById(R.id.nextStep);
		nextStep.setOnClickListener(this);
		farmName = (TextView) findViewById(R.id.farmName);
		if (!MySharePreference
				.getValueFromKey(this, MySharePreference.FARMNAME).equals(
						MySharePreference.NOTSTATE)) {
			farmName.setText(MySharePreference.getValueFromKey(this, MySharePreference.FARMNAME));
		}
		
		userName = (TextView) findViewById(R.id.userName);
		if(!MySharePreference.getValueFromKey(this, MySharePreference.ACCOUNTNICKNAME).equals(MySharePreference.NOTSTATE) &&
				   !MySharePreference.getValueFromKey(this, MySharePreference.ACCOUNTNICKNAME).equals("null"))
				{
			        userName.setText(MySharePreference.getValueFromKey(this, MySharePreference.ACCOUNTNICKNAME));
				}
				else
				{
					userName.setText(StringUtil.getNickNameFromPhone(MySharePreference.getValueFromKey(this, MySharePreference.LOGINSTATE)));
				}
		userHead = (ImageView) findViewById(R.id.userHead);
		String imageHeadUrl = "";
		if(MySharePreference.getValueFromKey(this, MySharePreference.ACCOUNTAVATAR).equals(MySharePreference.NOTSTATE))
		{
			imageHeadUrl = YunTongXun.UserHeadImageDefault;
		}
		else
		{
			imageHeadUrl = MySharePreference.getValueFromKey(this, MySharePreference.ACCOUNTAVATAR);
		}
		final String imageurlFinal = imageHeadUrl;
		userHead.setTag(imageurlFinal);
		ImageDownloadHelper.getInstance().imageDownload(null,this, imageurlFinal, userHead,"/001yunhekeji100",
				new OnImageDownloadListener() {
					public void onImageDownload(Bitmap bitmap, String imgUrl) {
						if (bitmap != null) {
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
								userHead.setBackground(new BitmapDrawable(bitmap));
							} else {
								userHead.setBackgroundDrawable(new BitmapDrawable(bitmap));
							}
							
							
							userHead.setTag(YunTongXun.ImagePath
									+ StringUtil.getFileNameFromUrl(imageurlFinal));
						} else {
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
								userHead.setBackground(new BitmapDrawable(ImageUtils
										.toRoundBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.user_head))));
							} else {
								userHead.setBackgroundDrawable(new BitmapDrawable(ImageUtils
										.toRoundBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.user_head))));
							}
							
						}
					}
				});
		
		progressBar = (SeekBar) findViewById(R.id.video_progress);
		progressBar.setMax(timeLength);
		progressBar.setClickable(false);
		progressBar.setEnabled(false);
		if(videoPath.length()>0)
		{
			userAddress.setText(userLocal);
			timesend.setText( String.format("%d:%02d", timeLength/60, timeLength%60));
			videoView.setVideoURI(Uri.parse(videoPath));
			videoView.requestFocus();
			videoView.setOnErrorListener(new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
					System.out.println(arg1 + "未知错误，无法预览" +arg2);
					return true;
				}
			});
			handler.post(timeRun);
			videoView.start();
		}
		else
		{
			playStop.setClickable(false);
		}
	}

	
	private int closehome = 0;
	@Override
	protected void onPause() {
		closehome = 1;
		videoView.pause();
		handler.removeCallbacks(timeRun);
		playStop.setBackgroundResource(R.drawable.begin);
		playOrStop = 1;
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		if(closehome ==1)
		{
			videoView.start();
			playStop.setBackgroundResource(R.drawable.suspend);
			videoView.seekTo((minute*60 + second)*1000);
			handler.post(timeRun);
			playOrStop = 2;
		}
		closehome = 0;
		super.onResume();
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.play_stop:
			if(playOrStop == 2)
			{
				videoView.pause();
				playStop.setBackgroundResource(R.drawable.begin);
				handler.removeCallbacks(timeRun);
				playOrStop = 1;
			}else
			{
				videoView.start();
				playStop.setBackgroundResource(R.drawable.suspend);
				handler.post(timeRun);
				playOrStop = 2;
			}
			
			break;
		case R.id.video_progress:
			break;
		case R.id.returnRecord:
			finish();
			break;
		case R.id.nextStep:
			Intent sendIntent = new Intent();
			sendIntent.putExtra("videoPath", videoPath);
			String segments = "";//2014-03-02T14:35:01/2014-03-02T14:37:32,2014-03-02T14:42:00/2014-03-02T14:55:13
			int count = 0;
			for(String str : filesTime)
			{
				if(count%2 == 0)
				{
					segments +=  str.replace(" ", "T")+"/";
				}
				else
				{
					if(count != (filesTime.length-1))
					{
						segments +=  str.replace(" ", "T") + ",";
					}
					else
					{
						segments +=  str.replace(" ", "T");
					}
				}
				count++;
			}
			sendIntent.putExtra("videotime", filesTime[0]);
			sendIntent.putExtra("segments", segments);
			sendIntent.putExtra("videolatlng", videolatlng);
			sendIntent.putExtra("location", userAddress.getText().toString());
			sendIntent.setClass(VideoPreviewActivity.this,VideoSendActivity.class);
			startActivityForResult(sendIntent,10);
			break;
		default:
			break;
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==10 && resultCode==10)
		{
			setResult(10);
			finish();
		}
	}
	
}
