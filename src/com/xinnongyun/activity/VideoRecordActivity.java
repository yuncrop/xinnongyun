package com.xinnongyun.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.gps.GpsManager;
import com.xinnongyun.imageload.ImageDownloadHelper;
import com.xinnongyun.imageload.ImageDownloadHelper.OnImageDownloadListener;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.utils.ImageUtils;
import com.xinnongyun.utils.ShortenMp4;
import com.xinnongyun.utils.StringUtil;
import com.xinnongyun.view.RecordLineView;

/**
 * 拍摄视频
 * 
 * @author sm
 * 
 */
public class VideoRecordActivity extends Activity implements
		SurfaceHolder.Callback {
	public static boolean recordingClose = false;
	public static int srceenWidth;
	public static int srceentHeight;
	public static int currentTimeLength;
	private File myRecVideoFile;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private TextView tvTime,tvNext,userName;
	private RecordLineView recordVideoTimeLine;
	private Button returnRecord, btnStart;
	private MediaRecorder recorder;
	private Handler handler;
	private Camera camera;
	private boolean recording; // 记录是否正在录像,false为未录像, true 为正在录像
	private int minute = 0;
	private int second = 0;
	private String time = "";
	private String fileName;
	private String name = "";
	private TextView videoRecordLocalTime,userAddress,farmName;
	private String uuidStr = UUID.randomUUID().toString();
	private int count = 0;
	private ImageView userHead,btnDelete;
	//视频文件列表
	private List<String> fileList = new ArrayList<String>();
	//保存单个视频开始结束时间以及长度
	private  List<String> fileTime = new ArrayList<String>();
	private int MaxTime = 120;
	
	public static boolean isDeletedLast = false;
	private boolean deletedLast = false;
	private boolean alreadDeleted = false;
	public static List<Integer> splitTime = new ArrayList<Integer>();
	/**
	 * 录制过程中,时间变化
	 */
	private Runnable timeRun = new Runnable() {

		@Override
		public void run() {
			if(recorder==null)
			{
				handler.removeCallbacks(timeRun);
				recording = false;
				return;
			}
			second++;
			if (second == 60) {
				minute++;
				second = 0;
			}
			if(second>=5 || minute>0)
			{
				tvNext.setVisibility(View.VISIBLE);
			}
			
			if((minute * 60 + second)>=MaxTime)
			{
				dg = MainFragment.createLoadingDialog(VideoRecordActivity.this);
				dg.show();
				new Thread(new Runnable() {
					@Override
					public void run() {
						stopRecord();
						recordToPreview();
					}
				}).start();
				return;
			}
			
			time = String.format("%d:%02d", minute, second);
			tvTime.setText(time);
			handler.postDelayed(timeRun, 1000);
			currentTimeLength = minute * 60 + second;
			recordVideoTimeLine.postInvalidate();
		}
	};

	
	private Handler videoTimeHandler;
	private Runnable videoTimeRun = new Runnable() {
		public void run() {
			videoTimeHandler.postDelayed(videoTimeRun, 1000);
			videoRecordLocalTime.setText(StringUtil.getCurrentDataS());
		};
	};
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_video_take);
		recordingClose = true;
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		getCurrentSystemViice();
		fileTime = new ArrayList<String>();
		currentTimeLength = 0;
		isDeletedLast = false;
		splitTime = new ArrayList<Integer>();
		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
		videoRecordLocalTime = (TextView) findViewById(R.id.videoRecordLocalTime);
		videoRecordLocalTime.setText(StringUtil.getCurrentDataS());
		srceenWidth = getWindowManager().getDefaultDisplay().getWidth();
		srceentHeight = getWindowManager().getDefaultDisplay().getHeight();
		Rect outRect = new Rect();  
		getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
		LayoutParams lp = new RelativeLayout.LayoutParams(srceenWidth, srceentHeight);
		mSurfaceView.setLayoutParams(lp);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.setKeepScreenOn(true);
		handler = new Handler();
		videoTimeHandler = new Handler();
		RelativeLayout.LayoutParams recordlp = new android.widget.RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,srceentHeight- srceenWidth -96);
		recordlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		findViewById(R.id.record_rl).setLayoutParams(recordlp);
		
		
		tvTime = (TextView) findViewById(R.id.tv_video_time);
		recordVideoTimeLine = (RecordLineView) findViewById(R.id.recordVideoTimeLine);
		userAddress = (TextView) findViewById(R.id.userAddress);
		userAddress.setTag("lat=0.0&lng=0.0");
		tvNext = (TextView) findViewById(R.id.next);
		tvNext.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						dg = MainFragment.createLoadingDialog(VideoRecordActivity.this);
						dg.show();
						new Thread(new Runnable() {
							@Override
							public void run() {
								recordToPreview();
							}
						}).start();;
						
					}
				}
				);
		
		returnRecord = (Button) findViewById(R.id.returnRecord);
		returnRecord.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if(fileList!=null && fileList.size()>0)
						{
							closeRecord();
							return;
						}
						
						finish();
					}
				}
				);
		btnDelete = (ImageView) findViewById(R.id.deleteVideo);
		
		btnDelete.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if(splitTime.size()==0 || alreadDeleted)
						{
							return;
						}
						if(deletedLast && isDeletedLast)
						{
							alreadDeleted = true;
							if(splitTime.size()>0)
							splitTime.remove(splitTime.size()-1);
							if(fileTime.size()>0)
							fileTime.remove(fileTime.size()-1);
							if(fileTime.size()>0)
							fileTime.remove(fileTime.size()-1);
							if(fileList.size()>0)
							{
								try {
									File delile = new File(fileList.get(fileList.size()-1));
									if(delile.exists())
									{
										delile.delete();
									}
								} catch (Exception e) {
								}finally{
									fileList.remove(fileList.size()-1);
								}
							}
							if(splitTime.size()==0)
							{
								currentTimeLength = 0;
							}
							else
							{
								currentTimeLength = splitTime.get(splitTime.size()-1);
							}
							minute = currentTimeLength/60;
							second = currentTimeLength%60;
							time = String.format("%d:%02d", minute, second);
							tvTime.setText(time);
						}
						
						if(!isDeletedLast)
						{
							deletedLast = true;
							isDeletedLast = true;
							btnDelete.setImageResource(R.drawable.revocation_press);
						}
						else
						{
							isDeletedLast = false;
							btnDelete.setImageResource(R.drawable.revocation);
						}
						if((minute*60 + second)<5)
						{
							tvNext.setVisibility(View.INVISIBLE);
						}
						recordVideoTimeLine.postInvalidate();
					}
				}
				);
		btnStart = (Button) findViewById(R.id.takevideo);
		btnStart.setOnTouchListener(mOnSurfaceViewTouchListener);
		// 设置sdcard的路径
		fileName = YunTongXun.VideoPath;
		name = "video_" + uuidStr;
		fileName +=  name;
		
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
		
		
		if(openGPSSettings())
		{
			getLocation(0);
		}else
		{
			final AlertDialog dlg = new AlertDialog.Builder(this).create();
			dlg.show();
			Window window = dlg.getWindow();
			WindowManager.LayoutParams lp1 = window.getAttributes();
			DisplayMetrics d = this.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
			lp1.width = (int) (d.widthPixels * 0.8);
			window.setAttributes(lp1);
			window.setContentView(R.layout.tishi_show_dialog_twobtn_exit);
			((TextView)window.findViewById(R.id.dialog_content)).setText(R.string.video_location_gps);
			Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
			ok.setText("设置");
			ok.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					dlg.dismiss();
					Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			        startActivityForResult(intent1,0); //此为设置完成后返回到获取界面
				}
			});
			Button cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
			cancel.setText("跳过");
			cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					dlg.dismiss();
					GpsManager pps = GpsManager.getInstance();
					pps.setGpsView(userAddress, 3);
				}
			});
			
	        
		}
		
	}

	private boolean openGPSSettings() {
		LocationManager alm = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		return alm
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
	}
	private void getLocation(int typeProvider)
	{
		GpsManager pps = GpsManager.getInstance();
		pps.setGpsView(userAddress, 3);
	}
	
	
//	private void getLocation(int typeProvider) {
//		
//		// 获取位置管理服务
//		String serviceName = Context.LOCATION_SERVICE;
//		final LocationManager locationManager = (LocationManager) this
//				.getSystemService(serviceName);
//		if (typeProvider==0 && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
//		{
//			gpsProvider = LocationManager.GPS_PROVIDER;
//		}else if (typeProvider ==1 && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//			gpsProvider = LocationManager.NETWORK_PROVIDER;
//		}
//		else
//		{
//			GpsManager pps = GpsManager.getInstance();
//			pps.setGpsView(userAddress, 3);
//			return;
//		}
//		Location location = locationManager.getLastKnownLocation(gpsProvider); // 通过GPS获取位置
////		if (location != null) {
////			locationUrl = "http://api.map.baidu.com/geocoder/v2/?" +
////					"ak=vrH5pWiTbrI6AFVnLjbo1E6u&callback=renderReverse&" +
////					"location=" 
////					+ locationManager.getLastKnownLocation(
////							LocationManager.gpsProvider).getLatitude()
////					+ ","
////					+ locationManager.getLastKnownLocation(
////							LocationManager.gpsProvider).getLongitude()
////					+ "&output=json&pois=1&" +
////					"mcode=08:C0:F5:68:56:E2:AE:A9:D9:1B:15:37:BE:78:76:2A:E6:7C:6A:FB;com.xinnongyun.activity";
////			userAddress.setTag("lat="+ locationManager.getLastKnownLocation(
////					LocationManager.gpsProvider).getLatitude() +"&lng="+ locationManager.getLastKnownLocation(
////					LocationManager.gpsProvider).getLongitude());
////			getLoactionReverse();
////			return;
////		}else
//		{
//			locationlistent = new LocationListener() {
//				@Override
//				public void onStatusChanged(String provider, int status,
//						Bundle extras) {
//				}
//
//				@Override
//				public void onProviderEnabled(String provider) {
//					// 当GPS Location Provider可用时，更新位置
////					if (locationManager.getLastKnownLocation(provider) != null) {
////							locationUrl = "http://api.map.baidu.com/geocoder/v2/?" +
////									"ak=vrH5pWiTbrI6AFVnLjbo1E6u&callback=renderReverse&" +
////									"location="
////									+ locationManager.getLastKnownLocation(
////											LocationManager.gpsProvider).getLatitude()
////									+ ","
////									+ locationManager.getLastKnownLocation(
////											LocationManager.gpsProvider).getLongitude()
////									+ "&output=json&pois=1&" +
////									"mcode=08:C0:F5:68:56:E2:AE:A9:D9:1B:15:37:BE:78:76:2A:E6:7C:6A:FB;com.xinnongyun.activity";
////							userAddress.setTag("lat="+ locationManager.getLastKnownLocation(
////									LocationManager.gpsProvider).getLatitude() +"&lng="+ locationManager.getLastKnownLocation(
////									LocationManager.gpsProvider).getLongitude());
////							getLoactionReverse();
////							locationManager.removeUpdates(locationlistent);
////					}
//				}
//
//				@Override
//				public void onProviderDisabled(String provider) {
//				}
//
//				@Override
//				public void onLocationChanged(Location location) {
//					if (locationManager.getLastKnownLocation(gpsProvider) != null) {
//						
//						locationUrl = "http://api.map.baidu.com/geocoder/v2/?" +
//								"ak=vrH5pWiTbrI6AFVnLjbo1E6u&callback=renderReverse&" +
//								"location=" 
//								+ locationManager.getLastKnownLocation(
//										gpsProvider).getLatitude()
//								+ ","
//								+ locationManager.getLastKnownLocation(
//										gpsProvider).getLongitude()
//								+ "&output=json&pois=1&" +
//								"mcode=08:C0:F5:68:56:E2:AE:A9:D9:1B:15:37:BE:78:76:2A:E6:7C:6A:FB;com.xinnongyun.activity";
//						userAddress.setTag("lat="+ locationManager.getLastKnownLocation(
//								gpsProvider).getLatitude() +"&lng="+ locationManager.getLastKnownLocation(
//								gpsProvider).getLongitude());
//						getLoactionReverse();
//						locationManager.removeUpdates(locationlistent);
//				}
//				}
//			};
//			locationManager.requestLocationUpdates(gpsProvider,
//					3000,// 更新频率
//					0,// 更新距离
//					locationlistent);
//			location = locationManager.getLastKnownLocation(gpsProvider);     
//	        if(location != null){
//	        	
////	        	locationUrl = "http://api.map.baidu.com/geocoder/v2/?" +
////						"ak=vrH5pWiTbrI6AFVnLjbo1E6u&callback=renderReverse&" +
////						"location=" 
////						+ locationManager.getLastKnownLocation(
////								gpsProvider).getLatitude()
////						+ ","
////						+ locationManager.getLastKnownLocation(
////								gpsProvider).getLongitude()
////						+ "&output=json&pois=1&" +
////						"mcode=08:C0:F5:68:56:E2:AE:A9:D9:1B:15:37:BE:78:76:2A:E6:7C:6A:FB;com.xinnongyun.activity";
////	        	userAddress.setTag("lat="+ locationManager.getLastKnownLocation(
////								gpsProvider).getLatitude() +"&lng="+ locationManager.getLastKnownLocation(
////								gpsProvider).getLongitude());
////	        	getLoactionReverse();
////	        	locationManager.removeUpdates(locationlistent);
//	        }
//	        else
//	        {
//	        	if(typeProvider==1)
//	        	{
//	        		GpsManager pps = GpsManager.getInstance();
//	    			pps.setGpsView(userAddress, 3);
//	    			return;
//	        	}
//	        	locationManager.removeUpdates(locationlistent);
//	        	getLocation(1);
//	        }
//		}
//	}
	
	Dialog dg;
	/*
	 * 跳转到预览画面
	 */
	private void recordToPreview()
	{
		if(fileTime.size()%2!=0)
		{
			fileTime.add(StringUtil.getCurrentDataSALL());
		}
		String[] timesFile = new String[fileTime.size()];
		fileTime.toArray(timesFile);
		
		if(fileList.size() <=1)
		{
			Intent intent = new Intent();
			intent.setClass(VideoRecordActivity.this, VideoPreviewActivity.class);
			intent.putExtra("videoPath", fileList.get(0));
			intent.putExtra("videoLength", minute * 60 + second);
			intent.putExtra("videoaddress", userAddress.getText().toString());
			intent.putExtra("videolatlng", userAddress.getTag().toString());
			intent.putExtra("videoFilesTime",timesFile);
			startActivityForResult(intent,10);
		}else
		{
			try {
				
				ShortenMp4.appendVideo(fileList.toArray(), fileName +".MP4");
				Intent intent = new Intent();
				intent.setClass(VideoRecordActivity.this, VideoPreviewActivity.class);
				intent.putExtra("videoPath", fileName +".MP4");
				intent.putExtra("videoLength", minute * 60 + second);
				intent.putExtra("videoaddress", userAddress.getText().toString());
				intent.putExtra("videolatlng", userAddress.getTag().toString());
				intent.putExtra("videoFilesTime",timesFile);
				
				startActivityForResult(intent,10);
			} catch (IOException e) {
				
			}
		}
		stopRecord();
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==10 && resultCode==10)
		{
			setResult(100);
			finish();
		}
		
		if(requestCode==0)
		{
			if(openGPSSettings())
			{
				getLocation(0);
			}
			else
			{
				GpsManager pps = GpsManager.getInstance();
				pps.setGpsView(userAddress, 3);
			}
		}
	}
	
	
	
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// 开启相机
		try {
			if (camera == null) {
				int CammeraIndex = FindFrontCamera();
				if (CammeraIndex == -1) {
					
					CammeraIndex = FindBackCamera();
				}
				try {
					camera = Camera.open(CammeraIndex);
				} catch (Exception e1) {
					try {
						Camera.open();
					} catch (Exception e) {
						Toast.makeText(VideoRecordActivity.this, "程序无拍摄权限", Toast.LENGTH_SHORT).show();
						finish();
						return;
					}
				}
				try {
					camera.setPreviewDisplay(mSurfaceHolder);
					if(Build.VERSION.SDK_INT >= 8)
					{
					   camera.setDisplayOrientation(90);
					}
				} catch (IOException e) {
					
					camera.release();
				}
			}
		} catch (Exception e) {
			Toast.makeText(VideoRecordActivity.this, "程序无法访问摄像头", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		try {
			// 开始预览
			camera.startPreview();
			//实现自动对焦 
			camera.autoFocus(new AutoFocusCallback() {  
			    @Override  
			    public void onAutoFocus(boolean success, Camera camera) {  
			        if(success){
			            camera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。  
			        }  
			    }  

			});
		} catch (Exception e) {
		}  
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// 关闭预览并释放资源
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	private void startRecord()
	{
		if (recorder != null) {
			releaseMediaRecorder();
			handler.removeCallbacks(timeRun);
			recording = false;
		}
		fileTime.add(StringUtil.getCurrentDataSALL());
		recorder();
	}
	
	private void stopRecord()
	{
		fileTime.add(StringUtil.getCurrentDataSALL());
		if (recorder != null) {
			releaseMediaRecorder();
			handler.removeCallbacks(timeRun);
			recording = false;
		}
	}

	
	// 判断前置摄像头是否存在
	private int FindFrontCamera() {
		try {
			int cameraCount = 0;
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			cameraCount = Camera.getNumberOfCameras(); // get cameras number

			for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
				Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					// 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
					return -1;
				}
			}
		} catch (Exception e) {
			return -1;
		}
		return -1;
	}

	// 判断后置摄像头是否存在
	private int FindBackCamera() {
		try {
			int cameraCount = 0;
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			cameraCount = Camera.getNumberOfCameras(); // get cameras number

			for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
				Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
					// 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
					return camIdx;
				}
			}
		} catch (Exception e) {
			return -1;
		}
		return -1;
	}

	// 释放recorder资源
	private void releaseMediaRecorder() {
		if (recorder != null) {
			
			try {
				recorder.stop();
				recorder.release();
				recorder = null;
			} catch (Exception e) {
				if(fileTime.size()>0)
				{
					fileTime.remove(fileTime.size()-1);
					if(fileTime.size()>0)
					fileTime.remove(fileTime.size()-1);
					recorder.release();
					recorder = null;
					File file = new File(fileName + (count-1) +".MP4");
					if(file.exists())
					{
						if(fileList.contains(fileName + (count-1) +".MP4"))
							fileList.remove(fileName + (count-1) +".MP4");
						file.delete();
					}
				}
			}
		}
	}

	// 开始录像
	@SuppressWarnings("static-access")
	public void recorder() {
		if (!recording) {
			try {
				setSystemVoice();
				// 关闭预览并释放资源
				if (camera != null) {
					//camera.stopPreview();
					//camera.release();
					//camera = null;
				}else
				{
					// 判断是否有前置摄像头，若有则打开，否则打开后置摄像头
					int CammeraIndex = FindFrontCamera();
					if (CammeraIndex == -1) {
						CammeraIndex = FindBackCamera();
					}
					try {
						camera = Camera.open(CammeraIndex);
					} catch (Exception e) {
						try {
							camera.open();
						} catch (Exception e1) {
							Toast.makeText(VideoRecordActivity.this, "程序无拍摄权限", Toast.LENGTH_SHORT).show();
							finish();
							return;
						}
					}
					
				}
				Parameters params = camera.getParameters();
				params.set("orientation", "portrait");
				params.setRotation(90);   //2.2之前
				params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
				camera.setParameters(params);
				// 设置摄像头预览顺时针旋转90度，才能使预览图像显示为正确的 2.2
				if(Build.VERSION.SDK_INT >= 8)
				{
				   camera.setDisplayOrientation(90);
				}
				recorder = new MediaRecorder();
				// 声明视频文件对象
				myRecVideoFile = new File(YunTongXun.VideoPath);
				if (!myRecVideoFile.exists()) {
					myRecVideoFile.mkdirs();
				}
				recorder.reset();
				camera.unlock();
				recorder.setCamera(camera); // 设置摄像头为相机recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//视频源
				recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
				recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT); //从照相机采集视频
				recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
				recorder.setOrientationHint(90);
				recorder.setVideoEncodingBitRate(3*1024*1024);
				recorder.setOutputFile(fileName + count++ +".MP4");
				fileList.add(fileName + (count-1) +".MP4");
				recorder.setPreviewDisplay(mSurfaceHolder.getSurface()); // 预览
				recorder.prepare(); // 准备录像
				recorder.start(); // 开始录像
				handler.post(timeRun); // 调用Runable
				recording = true; // 改变录制状态为正在录制
			} catch (IOException e1) {
				releaseMediaRecorder();
				handler.removeCallbacks(timeRun);
				recording = false;
				btnStart.setEnabled(true);
			} catch (IllegalStateException e) {
				releaseMediaRecorder();
				handler.removeCallbacks(timeRun);
				recording = false;
				btnStart.setEnabled(true);
			}catch (Exception e) {
				releaseMediaRecorder();
				finish();
			}
		} else
		{
			
		}
	}

	// 检查调用者是否具有 permission权限
	// 此方法仅在调用IPC（interprocess communication）方法时有用
	public static boolean checkPermission(Context context, String permission) {
		// 检查如果是当前应用则返回真
		if (Binder.getCallingPid() == Process.myPid()) {
			return true;
		}
		if (context.checkCallingPermission(permission) == PackageManager.PERMISSION_GRANTED) {
			return true;
		}
		return false;
	}
	
	@Override
	protected void onResume() {
		setSystemVoice();
		videoTimeHandler.post(videoTimeRun);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		stopRecord();
		super.onPause();
		if(dg!=null)
			dg.dismiss();
		videoTimeHandler.removeCallbacks(videoTimeRun);
		recoverySystemVoice();
	}
	
	//拍照触摸事件
	private View.OnTouchListener mOnSurfaceViewTouchListener = new View.OnTouchListener() {

		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// 判断是否已经超时
				if (minute * 60 + second >= MaxTime) {
					return true;
				}
				btnStart.setBackgroundResource(R.drawable.circle_green);
				isDeletedLast = false;
				btnDelete.setImageResource(R.drawable.revocation);
				recordVideoTimeLine.postInvalidate();
				startRecord();
				break;

			case MotionEvent.ACTION_UP:
				btnStart.setBackgroundResource(R.drawable.circle_green_press);
				// 暂停
				if (minute * 60 + second >= MaxTime) {
					return true;
				}
				splitTime.add(minute * 60 + second);
				stopRecord();
				break;
			case MotionEvent.ACTION_CANCEL:
				splitTime.add(minute * 60 + second);
				btnStart.setBackgroundResource(R.drawable.circle_green_press);
				stopRecord();
				break;
			default:
				break;
			}
			return true;
		}

	};
	
	private AudioManager am;
	private int currentVoice = 0;
	public static String locationUrl = "";
	private void getCurrentSystemViice()
	{
		currentVoice = am.getStreamVolume( AudioManager.STREAM_SYSTEM );
	}
	
	private void setSystemVoice()
	{
		am.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0);
	}
	
	private void recoverySystemVoice()
	{
		am.setStreamVolume(AudioManager.STREAM_SYSTEM, currentVoice, 0);
	}
	
	
	@Override 
    public boolean onKeyUp(int keyCode, KeyEvent event) { 
        if (keyCode == KeyEvent.KEYCODE_BACK) { 
        	if(fileList!=null && fileList.size() > 0)
        	{
        		closeRecord();
        		return true;
        	}
        	else
        	{
        		finish();
        	}
        } 
        return super.onKeyUp(keyCode, event); 
    } 
	
	private void closeRecord()
	{
		final AlertDialog dlg = new AlertDialog.Builder(this).create();
		dlg.show();
		Window window = dlg.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		DisplayMetrics d = this.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
		lp.width = (int) (d.widthPixels * 0.8);
		window.setAttributes(lp);
		window.setContentView(R.layout.tishi_show_dialog_twobtn_exit);
		((TextView)window.findViewById(R.id.dialog_content)).setText(R.string.video_record_close_tishi);
		Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
		ok.setTextColor(getResources().getColor(R.color.text_fe462e));
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
				finish();
				
			}
		});
		Button cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
		cancel.setTextColor(getResources().getColor(R.color.text_green));
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
			}
		});
	}
	
	
	
	
	@Override
	protected void onDestroy() {
		recordingClose = false;
		GpsManager.getInstance().destory();
		super.onDestroy();
	}
}

