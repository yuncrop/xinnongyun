package com.xinnongyun.atyfragment;

import java.util.HashMap;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.sitech.oncon.barcode.core.CaptureActivity;
import com.xinnongyun.activity.AlarmingSettingActivity;
import com.xinnongyun.activity.ContactUsActivity;
import com.xinnongyun.activity.ExitApplication;
import com.xinnongyun.activity.FarmAddCreateActicity;
import com.xinnongyun.activity.FarmConfigActivity;
import com.xinnongyun.activity.MainActivity;
import com.xinnongyun.activity.R;
import com.xinnongyun.activity.UserInfoActivity;
import com.xinnongyun.activity.UserSuggestionActivity;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.imageload.ImageDownloadHelper;
import com.xinnongyun.imageload.ImageDownloadHelper.OnImageDownloadListener;
import com.xinnongyun.jpushbroadcast.JpushBroadCastReceiver;
import com.xinnongyun.net.NetConnectionVersion;
import com.xinnongyun.net.NetConnectionVersion.VersionSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.FarmDaoManager;
import com.xinnongyun.utils.DialogManager;
import com.xinnongyun.utils.StringUtil;
import com.xinnongyun.version.UpdateManager;

/**
 * 侧滑菜单类
 * 
 * @author sm
 * 
 */
public class SlideMenuFragment extends Fragment implements OnClickListener {

	// 退出
	RelativeLayout rlFarmConfig, rlAccountConfig, rlExit, rlHardConfig,
			rlContactUs, rlUseInfo, rlVersion,rlbaojing;

	public ImageView slidemenu_userHead;
	private TextView find_new_version;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sliding_menu, null);
		initView(view);
		return view;
	}

	/**
	 * 初始化数据
	 * 
	 * @param view
	 */
	private void initView(View view) {
		find_new_version = (TextView) view.findViewById(R.id.find_new_version);
		slidemenu_userHead = (ImageView) view.findViewById(R.id.slidemenu_userHead);
		slidemenu_userHead.setOnClickListener(this);
		
		String imageHeadUrl = MySharePreference.getValueFromKey(getActivity(), MySharePreference.ACCOUNTAVATAR);
		slidemenu_userHead.setBackgroundResource(R.drawable.touxiang);
		if(!imageHeadUrl.equals(MySharePreference.NOTSTATE) && !imageHeadUrl.equals(YunTongXun.UserHeadImageDefault))
		{
			final String farmLogourlFinal = imageHeadUrl;
			slidemenu_userHead.setTag(farmLogourlFinal);
			ImageDownloadHelper.getInstance().imageDownload(null,
					getActivity(), farmLogourlFinal, slidemenu_userHead,
					"/001yunhekeji100", new OnImageDownloadListener() {
						@SuppressWarnings("deprecation")
						public void onImageDownload(Bitmap bitmap, String imgUrl) {
							if (bitmap != null) {
								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
									slidemenu_userHead.setBackground(new BitmapDrawable(bitmap));
								} else {
									slidemenu_userHead.setBackgroundDrawable(new BitmapDrawable(bitmap));
								}
								slidemenu_userHead.setTag(YunTongXun.ImagePath
										+ StringUtil
												.getFileNameFromUrl(farmLogourlFinal));
							} else {
								slidemenu_userHead.setBackgroundResource(R.drawable.touxiang);
							}
						}
					});
			
		}
		
		
		rlbaojing = (RelativeLayout) view.findViewById(R.id.SM_rl_baojing);
		rlbaojing.setOnClickListener(this);
		rlExit = (RelativeLayout) view.findViewById(R.id.SM_rl_exit);
		rlHardConfig = (RelativeLayout) view
				.findViewById(R.id.SM_rl_hardConfig);
		rlContactUs = (RelativeLayout) view.findViewById(R.id.SM_rl_contactus);
		rlUseInfo = (RelativeLayout) view.findViewById(R.id.SM_rl_useinfo);
		rlVersion = (RelativeLayout) view.findViewById(R.id.SM_rl_version);
		rlFarmConfig = (RelativeLayout) view
				.findViewById(R.id.SM_rl_farmconfig);
		rlAccountConfig = (RelativeLayout) view
				.findViewById(R.id.SM_rl_accountconfig);
		rlExit.setOnClickListener(this);
		rlContactUs.setOnClickListener(this);
		rlUseInfo.setOnClickListener(this);
		rlVersion.setOnClickListener(this);
		rlFarmConfig.setOnClickListener(this);
		rlAccountConfig.setOnClickListener(this);
		rlHardConfig.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		//报警设置
		case R.id.SM_rl_baojing:
			Intent baojingIntent = new Intent();
			baojingIntent.setClass(getActivity(), AlarmingSettingActivity.class);
			startActivity(baojingIntent);
			break;
		// 点击退出
		case R.id.SM_rl_exit:
			
			final AlertDialog dlg = new AlertDialog.Builder(getActivity()).create();
			dlg.show();
			Window window = dlg.getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			DisplayMetrics d = getActivity().getResources().getDisplayMetrics(); // 获取屏幕宽、高用
			lp.width = (int) (d.widthPixels * 0.8);
			window.setAttributes(lp);
			window.setContentView(R.layout.tishi_show_dialog_twobtn_exit);
			Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
			ok.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
				
					MySharePreference.clearOneState(getActivity(),
							MySharePreference.LOGINSTATE);
					FarmDaoManager farmDao = new FarmDaoManager(
							getActivity());
				    farmDao.clearFarmTable(1);
				    dlg.dismiss();
				    NotificationManager manger = (NotificationManager)getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
					manger.cancelAll();
					JpushBroadCastReceiver.notificationLruCache.clearCache();
					//AppShortCutUtil.addNumShortCut(getActivity(), LoginActivity.class, true, JpushBroadCastReceiver.notificationLruCache.lruCache.size()+"", true);
					if(!JPushInterface.isPushStopped(getActivity().getApplicationContext()))
					{
						JPushInterface.stopPush(getActivity().getApplicationContext());
					}
					ExitApplication.getInstance().exit();
					
				}
			});
			Button cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
			cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					dlg.dismiss();
				}
			});
			
			
			break;
		// 农场设置
		case R.id.SM_rl_farmconfig:
			if (MySharePreference.getValueFromKey(getActivity(), MySharePreference.FARMNAME).equals(
							MySharePreference.NOTSTATE))
			{
				Intent intent = new Intent();
				intent.setClass(getActivity(), FarmAddCreateActicity.class);
				startActivity(intent);
			}
			else
			{
				Intent intent = new Intent();
				intent.setClass(getActivity(), FarmConfigActivity.class);
				startActivity(intent);
			}
			break;
		// 硬件设置
		case R.id.SM_rl_hardConfig:
			Intent captureIntent = new Intent(getActivity(),
					CaptureActivity.class);
			captureIntent.putExtra(YunTongXun.CAPTURE_KEY,
					YunTongXun.CAPTURE_HANRDCONFIG);
			startActivity(captureIntent);
			break;
		// 帐号设置
		case R.id.slidemenu_userHead:
		case R.id.SM_rl_accountconfig:
			userinfo();
			break;
		// 意见反馈
		case R.id.SM_rl_useinfo:
			usersuggestion();
			break;
		// 版本设置
		case R.id.SM_rl_version:
			updateVersion(1);
			break;
		// 联系我们
		case R.id.SM_rl_contactus:
			Intent contaUs = new Intent();
			contaUs.setClass(getActivity(), ContactUsActivity.class);
			startActivity(contaUs);
			break;
		default:
			break;
		}
	}

	public void userinfo()
	{
		Intent userInfoIntent = new Intent(getActivity(), UserInfoActivity.class);
		startActivityForResult(userInfoIntent,11);
	}
	
	public void usersuggestion()
	{
		Intent suggestioIntent = new Intent();
		suggestioIntent.setClass(getActivity(), UserSuggestionActivity.class);
		startActivityForResult(suggestioIntent, 10);
	}
	
	
	
	/**
	 * 检查更新版本
	 */
	public void updateVersion(final int type)
	{
		NetConnectionVersion.VersionGetNetConnection(NetUrls.URL_VERSION,
				new VersionSuccessCallBack() {
					@Override
					public void onSuccess(HashMap<String, Object> result) {
						if(result != null && result.keySet().size()>0)
						{
							if (result.keySet().contains("status_code")) {
//								ErrorMarkToast.showCallBackToast(
//										getActivity(), result);
								return;
							}
							int currentVersion = UpdateManager.getCurrentVersion(getActivity());
							if((currentVersion+"").equals(result.get(NetUrls.URL_VERSION_LAST).toString()))
							{
								find_new_version.setVisibility(View.INVISIBLE);
								if(type == 1)
								{
									DialogManager.showDialogSimple(getActivity(), R.string.version_sameversion);
								}
							}
							else
							{
								find_new_version.setVisibility(View.VISIBLE);
								UpdateManager.showUpdaDialog(getActivity(),
										new String[]{StringUtil.getFileNameFromUrl(result.get(NetUrls.URL_VERSION_URL).toString().trim()),
									result.get(NetUrls.URL_VERSION_URL).toString()});
								UpdateManager.vetsionTishi = find_new_version;
							}
						}
						else
						{
							if(type == 1)
							showToast(R.string.interneterror);
						}
					}
				});

	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 10 && resultCode == 10)
		{
			DialogManager.showDialogSimple(getActivity(), 
					//R.string.userinfo_sliding_menu,
					R.string.userinfo_suggestion_dialog_content);
		}
		if(requestCode == 11)
		{
			((MainActivity)getActivity()).changeUserSliheHead();
		}
		
		if(resultCode == 250)
		{
			DialogManager.showDialogSimple(getActivity(), 
					//R.string.userinfo_sliding_menu,
					R.string.farminglist_item_delete);
		}
	}
	
	/**
	 * Toast提示框
	 * 
	 * @param id
	 */
	private void showToast(int id) {
		Toast.makeText(getActivity(), id, Toast.LENGTH_SHORT).show();
	}
	
}
