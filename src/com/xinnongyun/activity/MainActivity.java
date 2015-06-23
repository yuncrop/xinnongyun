package com.xinnongyun.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sitech.oncon.barcode.core.CaptureActivity;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.atyfragment.SlideMenuFragment;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.service.VideoUploadService;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.VideoUploadDaoDBManager;
import com.xinnongyun.utils.NetInterUtils;
import com.xinnongyun.utils.StringUtil;

/**
 * 主界面
 * 
 * @author sm
 * 
 */
public class MainActivity extends SlidingFragmentActivity implements IWeiboHandler.Response{
	public MainFragment mainFragment;
	public static boolean shareInitalFinished = false;
	public SlideMenuFragment slideMenuFragment;
	public int currentpageSelect = 1;
	public static final String farmingNewbroastCast = "xinnongyun.frmingnew";
	public static final String farmConfigbroastCast = "xinnongyun.farmConfigChange";
	public static final String noticeInfobroastCast = "xinnongyun.noticeInfoChange";
	public static final String journalInfobroastCast = "xinnongyun.journalInfoChange";
	public static final String weatherStationHeadInfobroastCast = "xinnongyun.weatherStationHead";
	public static final String weatherStationInfoChangebroastCast = "xinnongyun.weatherStationInfoChange";
	public static final String stationTileLineHeadInfobroastCast = "xinnongyun.stationTimeLineHead";
	public static final String controlcenterbroastCast = "xinnongyun.controlcenterbroadcast";
	
	public static boolean isWifiState = false;
	public static IWXAPI wxApi;
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		// 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
        // 失败返回 false，不调用上述回调
        if (savedInstanceState != null) {
        	if(mWeiboShareAPI!=null)
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
		Intent intent = getIntent();
		if(intent != null)
		{
			currentpageSelect = intent.getIntExtra("currentpage", 1);
			int notifyid = intent.getIntExtra("notify_id", 0);
			if(notifyid!=0)
			{
				NotificationManager manger = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
				manger.cancel(notifyid);
			}
			
		}
		MainFragment.cameraType = 1;
		mainFragment = new MainFragment();
		
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fl_main_shouye, mainFragment).commit();

		setBehindContentView(R.layout.activity_slidemenu);
		slideMenuFragment = new SlideMenuFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.activity_left, slideMenuFragment).commit();
		getSlidingMenu().setBehindWidth(
				getWindowManager().getDefaultDisplay().getWidth() * 4 / 5);
		getSlidingMenu().setMode(SlidingMenu.LEFT);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.LEFT);
		//注册网络监听 
		IntentFilter filter = new IntentFilter();  
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); 
		registerReceiver(internetBroadCast, filter);
		
		//视频列表得到监听 
		IntentFilter filterJournalVideo = new IntentFilter();  
		filterJournalVideo.addAction(journalInfobroastCast); 
		registerReceiver(journalVideoInfoChange, filterJournalVideo);
		
		//种养纪录得到监听 
		IntentFilter filterFarmingNew = new IntentFilter();  
		filterFarmingNew.addAction(farmingNewbroastCast); 
		registerReceiver(farmingNewBroadCast, filterFarmingNew);
		
		//农场设置得到监听 
		IntentFilter filterFarmConfig = new IntentFilter();  
		filterFarmConfig.addAction(farmConfigbroastCast); 
		registerReceiver(farmConfigChange, filterFarmConfig);
		
		//环境小站
		IntentFilter filterWeatherStationConfig = new IntentFilter();  
		filterWeatherStationConfig.addAction(weatherStationHeadInfobroastCast); 
		registerReceiver(weatherSttionInfoChange, filterWeatherStationConfig);
		
		//环境小站修改
		IntentFilter filterWeatherStationConfigChange = new IntentFilter();  
		filterWeatherStationConfigChange.addAction(weatherStationInfoChangebroastCast); 
		registerReceiver(weatherSttionInfoAlreadyChange, filterWeatherStationConfigChange);
		
		//环境小站实时数据
		IntentFilter filterStationTimeLineConfig = new IntentFilter();  
		filterStationTimeLineConfig.addAction(stationTileLineHeadInfobroastCast); 
		registerReceiver(stationTileLineChange, filterStationTimeLineConfig);
		
		//中控实时数据
		IntentFilter ccFilter = new IntentFilter();
		ccFilter.addAction(controlcenterbroastCast); 
		registerReceiver(ccBc, ccFilter);
		
				
		//通知列表
		IntentFilter filterNoticeConfig = new IntentFilter();  
		filterNoticeConfig.addAction(noticeInfobroastCast); 
		registerReceiver(noticeInfoChange, filterNoticeConfig);
		slideMenuFragment.updateVersion(0);
		
		ExitApplication.getInstance().addActivity(this);
		
		if(JPushInterface.isPushStopped(getApplicationContext()))
		{
			JPushInterface.resumePush(getApplicationContext());
		}
		JPushInterface.setAlias(getApplicationContext(), MySharePreference.getValueFromKey(MainActivity.this,MySharePreference.ACCOUNTID),
				new TagAliasCallback() {
					@Override
					public void gotResult(int arg0, String arg1, Set<String> arg2) {
						
					}
				});
		
		
		checkHasVideoUpload(this);
		regToWx();
		regToXL();
	}
	
	//注册微信
	private void regToWx()
	{
		wxApi = WXAPIFactory.createWXAPI(this, YunTongXun.WX_APP_ID, true);
		wxApi.registerApp(YunTongXun.WX_APP_ID);
	}
	
	
	/** 微博微博分享接口实例 */
    public static IWeiboShareAPI  mWeiboShareAPI = null;
	
	//注册新浪
	private void regToXL()
	{
		// 创建微博分享接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, YunTongXun.XINLANG_APP_ID);
        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
        // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
         mWeiboShareAPI.registerApp();
         
	}
	
	/*
	 * 是否有视频等待上传
	 */
	public static void checkHasVideoUpload(Context context)
	{
		if(!isServiceRun(context))
		{
			System.out.println("tttttttttttttttttt");
			if(NetInterUtils.isWifiConnected(context))
			{
				System.out.println("yyyyyyyyyyyyyyyy");
				isWifiState = true;
				//判断是否存在要上传的视频
				VideoUploadDaoDBManager daoDBManager = new VideoUploadDaoDBManager(context);
				List<HashMap<String, String>> videoListMap = daoDBManager.getAllVideoUploadList();
				if(videoListMap!=null && videoListMap.size()>0)
				{
					Toast.makeText(context, "WIFI已连接正在监测是否需要上传", Toast.LENGTH_SHORT).show();
					Intent videoServiceIntent = new Intent(context,VideoUploadService.class);
					context.startService(videoServiceIntent);
				}
			}
			else
			{
				isWifiState = false;
				System.out.println("非WIFI不上传");
			}
		}
		else
		{
			if(NetInterUtils.isWifiConnected(context))
			{
				isWifiState = true;
				System.out.println("服务已经开启");
			}
			else
			{
				isWifiState = false;
			}
		}
	}
	
	
	public void FarmingListFootClick(View view)
	{
		Intent captureIntent = new Intent(MainActivity.this,
				CaptureActivity.class);
		captureIntent.putExtra(YunTongXun.CAPTURE_KEY,
				YunTongXun.CAPTURE_HANRDCONFIG);
		startActivity(captureIntent);
	}
	
	public void OnFarmingListEmptyClick(View view)
	{
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, FarmAddCreateActicity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//取消监听
		unregisterReceiver(internetBroadCast); 
		unregisterReceiver(journalVideoInfoChange);
		unregisterReceiver(farmingNewBroadCast);
		unregisterReceiver(stationTileLineChange);
		unregisterReceiver(weatherSttionInfoChange);
		unregisterReceiver(weatherSttionInfoAlreadyChange);
		unregisterReceiver(farmConfigChange);
		unregisterReceiver(ccBc);
		unregisterReceiver(noticeInfoChange);
		MainFragment.collectUpdateList = false;
		
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(mainFragment!=null)
		mainFragment.userClickState = true;
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 打开或关闭侧滑菜单
	 */
	public void openOrCloseSlideMenu()
	{
		getSlidingMenu().toggle();
	}
	
	public void changeUserSliheHead()
	{
		String imageHeadUrl = MySharePreference.getValueFromKey(this, MySharePreference.ACCOUNTAVATAR);
		if(!imageHeadUrl.equals(MySharePreference.NOTSTATE))
		{
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			slideMenuFragment.slidemenu_userHead.setImageBitmap(BitmapFactory.decodeFile(YunTongXun.ImagePath + StringUtil.getFileNameFromUrl(imageHeadUrl), options));
		}
	}
	
	private long firstTime=0;
	private int backClickCount = 0;
	@Override 
    public boolean onKeyUp(int keyCode, KeyEvent event) { 
        if (keyCode == KeyEvent.KEYCODE_BACK) { 
        	if(currentpageSelect == 1)
        	{
	            long secondTime = System.currentTimeMillis(); 
	            if (secondTime - firstTime > 1000) {//如果两次按键时间间隔大于1000毫秒，则不退出 
	                if(backClickCount >=1)
	                {
		            	backClickCount = 0;
	                }
	                Toast.makeText(MainActivity.this, R.string.exit_app_toast, 
	                        Toast.LENGTH_SHORT).show();
	                backClickCount ++ ;
	                firstTime = secondTime;//更新firstTime 
	                return true; 
	            } else { 
	                finish();//否则退出程序 
	                ExitApplication.getInstance().exit();
	            }
        	}
        	else
        	{
        		finish();
        	}
        } 
        return super.onKeyUp(keyCode, event); 
    } 
	
	
	
	//坚挺网络状态
    BroadcastReceiver internetBroadCast = new  BroadcastReceiver(){
    	@Override
        public void onReceive(Context context, Intent intent) {
    		mainFragment.setInternetTextIsVisible(mainFragment.getActivity());
    		if(!NetInterUtils.isWifiConnected(context))
    		{
    			Toast.makeText(context, "WIFI已断开", Toast.LENGTH_SHORT).show();
    			isWifiState = false;
    			return;
    		}
    		else
    		{
    			isWifiState = true;
    			checkHasVideoUpload(context);
    		}
        }
    };
    
    //添加成功监听种养记录
    BroadcastReceiver farmingNewBroadCast = new  BroadcastReceiver(){
    	@Override
        public void onReceive(Context context, Intent intent) {
    		if (MySharePreference.getValueFromKey(context, MySharePreference.FARMNAME).equals(
					MySharePreference.NOTSTATE))
			{
				findViewById(R.id.farmingListempty_ll).setVisibility(View.VISIBLE);
			}
			else
			{
				findViewById(R.id.farmingListempty_ll).setVisibility(View.INVISIBLE);
			}
    		if(intent.getAction().equals(farmingNewbroastCast))
    			if(mainFragment!=null && mainFragment.farmbeingShuaxin!=null)
        		{
        			mainFragment.loadingHandler.sendEmptyMessage(0);
        		}
    	    	mainFragment.updateFarmngInfos();
        }
    };
	
    
    
    //添加成功监听种养记录
    BroadcastReceiver farmConfigChange = new  BroadcastReceiver(){
    	@Override
        public void onReceive(Context context, Intent intent) {
    		if(intent.getAction().equals(farmConfigbroastCast))
    		{
    			if(!MySharePreference.getValueFromKey(MainActivity.this, MySharePreference.FARMNAME).equals(MySharePreference.NOTSTATE) &&
    					!MySharePreference.getValueFromKey(MainActivity.this, MySharePreference.FARMNAME).equals("未设置"))
    			{
    				mainFragment.shouye_head_farm.setText(MySharePreference.getValueFromKey(MainActivity.this, MySharePreference.FARMNAME));
    			}
    			else
    			{
    				mainFragment.shouye_head_farm.setText(R.string.app_name);
    			}
    			
    			mainFragment.intialVideoFarmImage(mainFragment.journalVideo_head);
    		}
        }
    };
    
    //添加成功通知记录
    BroadcastReceiver noticeInfoChange = new  BroadcastReceiver(){
    	@Override
        public void onReceive(Context context, Intent intent) {
    		if(intent.getAction().equals(noticeInfobroastCast))
    		{
    			if(mainFragment!=null)
    			mainFragment.updateRenwuList();
    		}
        }
    };
    
    
  //视频记录广播通知
    BroadcastReceiver journalVideoInfoChange = new  BroadcastReceiver(){
    	@Override
        public void onReceive(Context context, Intent intent) {
    		if(intent.getAction().equals(journalInfobroastCast))
    		{
    			if(mainFragment!=null)
    				mainFragment.updateJournalList();
    		}
        }
    };
    
    //环境小站实时数据信息广播通知
    BroadcastReceiver stationTileLineChange = new  BroadcastReceiver(){
    	@Override
        public void onReceive(Context context, Intent intent) {
    		if(intent.getAction().equals(stationTileLineHeadInfobroastCast))
    		{
    			try {
					if(mainFragment!=null)
					mainFragment.intialWeatherStationTimeLine();
				} catch (Exception e) {
				}
    		}
        }
    };
    
    
    //环境小站信息广播通知
    BroadcastReceiver weatherSttionInfoChange = new  BroadcastReceiver(){
    	@Override
        public void onReceive(Context context, Intent intent) {
    		if(intent.getAction().equals(weatherStationHeadInfobroastCast))
    		{
    			try {
					if(mainFragment!=null)
						mainFragment.intialStationHead(0);
				} catch (Exception e) {
				}
    			
    		}
        }
    };
    
  //环境小站信息广播通知
    BroadcastReceiver weatherSttionInfoAlreadyChange = new  BroadcastReceiver(){
    	@Override
        public void onReceive(Context context, Intent intent) {
    		if(intent.getAction().equals(weatherStationInfoChangebroastCast))
    		{
    			try {
					if(mainFragment!=null)
						mainFragment.intialStationHead(1);
				} catch (Exception e) {
				}
    		}
        }
    };
    
    
    //中控信息广播通知
    BroadcastReceiver ccBc = new  BroadcastReceiver(){
    	@Override
        public void onReceive(Context context, Intent intent) {
    		if(intent.getAction().equals(controlcenterbroastCast))
    		{
    			try {
					if(mainFragment!=null)
						mainFragment.intialStationHead(1);
				} catch (Exception e) {
				}
    		}
        }
    };
    
    // 判断服务线程是否存在  
	public static boolean isServiceRun(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> list = am.getRunningServices(30);
		for (RunningServiceInfo info : list) {
			if (info.service.getClassName().equals(
					"com.xinnongyun.service.VideoUploadService")) {
				return true;
			}
		}
		return false;
	}

	/**
     * @see {@link Activity#onNewIntent}
     */	
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }
	
	@Override
	public void onResponse(BaseResponse baseResp) {
		switch (baseResp.errCode) {
        case WBConstants.ErrorCode.ERR_OK:
            Toast.makeText(this, "ok", Toast.LENGTH_LONG).show();
            break;
        case WBConstants.ErrorCode.ERR_CANCEL:
            Toast.makeText(this, "cancel", Toast.LENGTH_LONG).show();
            break;
        case WBConstants.ErrorCode.ERR_FAIL:
            Toast.makeText(this, "error" + baseResp.errMsg, 
                    Toast.LENGTH_LONG).show();
            break;
        }
	}
}
