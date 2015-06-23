package com.xinnongyun.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.mapapi.SDKInitializer;
import com.xinnongyun.adapter.BoxSoilAdapter;
import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.gps.GpsManager;
import com.xinnongyun.imageload.ImageDownloadHelper;
import com.xinnongyun.imageload.ImageDownloadHelper.OnImageDownloadListener;
import com.xinnongyun.net.NetConnectionFarm;
import com.xinnongyun.net.NetConnectionFarm.FarmSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.FarmDaoManager;
import com.xinnongyun.utils.DialogManager;
import com.xinnongyun.utils.ImageUtils;
import com.xinnongyun.utils.StringUtil;

/**
 * 农场配置
 * 
 * @author sm
 */
public class FarmConfigActivity extends Activity implements OnClickListener {

	// 界面信息
	private TextView farmName, farmAddress, farmIntroduce;
	private LinearLayout farmconfig_newfarm_et_name_ll;
	private ImageView farmBg, farmLogo, farmConfigReturn, farmAddressImage,popdelete_iv;
	private RelativeLayout farmIntroduceImage,farmconfig_newfarm_et_address_rl;
	private final int RESULT_LOAD_IMAGE_camera = 1;
	private final int RESULT_LOAD_IMAGE_phone = 2;
	/**
	 * 1 BG  2LOGO
	 */
	private int imageClick = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏ActionBar
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//		         WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.acticity_farmconfig);
		initView();
		if(MySharePreference
				.getValueFromKey(this, MySharePreference.FARMNAME).equals(
						MySharePreference.NOTSTATE))
		{
			popdelete_iv.setVisibility(View.INVISIBLE);
			findViewById(R.id.complete_btn).setVisibility(View.VISIBLE);
			findViewById(R.id.complete_btn).setOnClickListener(this);
			createFarm(this);
		}
	}

	/**
	 * 初始化界面控件
	 */
	private void initView() {
		popdelete_iv = (ImageView) findViewById(R.id.popdelete_iv);
		popdelete_iv.setOnClickListener(this);
		farmIntroduceImage = (RelativeLayout) findViewById(R.id.farmconfig_newfarm_iv_introduce_rl);
		farmIntroduceImage.setOnClickListener(this);
		farmconfig_newfarm_et_address_rl = (RelativeLayout) findViewById(R.id.farmconfig_newfarm_et_address_rl);
		farmconfig_newfarm_et_address_rl.setOnClickListener(this);
		farmconfig_newfarm_et_name_ll = (LinearLayout) findViewById(R.id.farmconfig_newfarm_et_name_ll);
		farmconfig_newfarm_et_name_ll.setOnClickListener(this);
		farmAddressImage = (ImageView) findViewById(R.id.farmconfig_newfarm_iv_address);
		farmAddressImage.setOnClickListener(this);
		farmConfigReturn = (ImageView) findViewById(R.id.farmConfigReturn);
		farmConfigReturn.setOnClickListener(this);
		farmName = (TextView) findViewById(R.id.farmconfig_newfarm_et_name);
		farmAddress = (TextView) findViewById(R.id.farmconfig_newfarm_et_address);
		farmIntroduce = (TextView) findViewById(R.id.farmconfig_newfarm_introduce_et);
		// farmIntroduce.setOnClickListener(this);
		farmBg = (ImageView) findViewById(R.id.farmconfig_newfarm_image_bg);
		farmBg.setOnClickListener(this);
		farmLogo = (ImageView) findViewById(R.id.farmconfig_newfarm_image_logo);
		farmLogo.setOnClickListener(this);
		if (!MySharePreference
				.getValueFromKey(this, MySharePreference.FARMNAME).equals(
						MySharePreference.NOTSTATE)) {

			HashMap<String, String> farmInfos = MySharePreference
					.getFarmAllInfo(this);
			// 初始化农场信息
			if (farmInfos != null) {
				farmName.setText(farmInfos.get(MySharePreference.FARMNAME));
				farmAddress.setText(farmInfos
						.get(MySharePreference.FARMADDRESS));
				farmAddress.setTag("lng="
						+ farmInfos.get(MySharePreference.FARMLNG).toString()
						+ "&lat="
						+ farmInfos.get(MySharePreference.FARMLAT).toString());
				farmIntroduce.setText(farmInfos
						.get(MySharePreference.FARMINTRODUCE));
				intialFarmImage();
			}
			return;
		}
	}

	private  void createFarm(final Context context) {
		String para = "name=未设置";
		NetConnectionFarm.FarmPostNetConnection(
				NetUrls.URL_FARM_CREATE,
				para,
				"Token "
						+ MySharePreference.getValueFromKey(
								context,
								MySharePreference.ACCOUNTTOKEN),
				new FarmSuccessCallBack() {
					@Override
					public void onSuccess(HashMap<String, Object> map) {
						if (map != null) {
							if (map.keySet().contains("status_code")) {
								ErrorMarkToast.showCallBackToast(
										context, map);
								return;
							}
							// TODO保存农场信息到本地
							MySharePreference.saveFarmInfo(
									context, map);
							LoginActivity.addFarm(FarmConfigActivity.this);

						} else {
							showToast(R.string.interneterror);
						}
					}
				});
	}
	private Dialog dg;
	private void changeFarmInfoImage(String para)
	{
		dg = MainFragment.createLoadingDialog(this);
		dg.show();
		NetConnectionFarm.FarmPutNetConnection(
				NetUrls.URL_MODIFY_FARM(MySharePreference
						.getValueFromKey(FarmConfigActivity.this,
								MySharePreference.FARMID)),
				para,
				"Token "
						+ MySharePreference.getValueFromKey(
								FarmConfigActivity.this,
								MySharePreference.ACCOUNTTOKEN),
				new FarmSuccessCallBack() {
					@Override
					public void onSuccess(HashMap<String, Object> map) {
						dg.dismiss();
						if (map != null) {
							if (map.keySet().contains("status_code")) {
								ErrorMarkToast.showCallBackToast(
										FarmConfigActivity.this, map);
								return;
							}
							if(imageClick == 1)
							{
								ImageUtils.fileReName(StringUtil.getFileNameFromUrl(farmBg.getTag().toString()),
										StringUtil.getFileNameFromUrl(map.get(MySharePreference.FARMBG).toString()));
							}
							if(imageClick == 2)
							{
								ImageUtils.fileReName(StringUtil.getFileNameFromUrl(farmLogo.getTag().toString()),
										StringUtil.getFileNameFromUrl(map.get(MySharePreference.FARMLOGO).toString()));
							}
							// TODO保存农场信息到本地
							MySharePreference.saveFarmInfo(
									FarmConfigActivity.this, map);
							intialFarmImage();
						} else {
							showToast(R.string.interneterror);
						}
					}
				});
	}
	
	
	private void intialFarmImage() {
		String farmbgUrl;
		if (MySharePreference.getValueFromKey(FarmConfigActivity.this,
				MySharePreference.FARMBG).equals(MySharePreference.NOTSTATE)||MySharePreference.getValueFromKey(FarmConfigActivity.this,
						MySharePreference.FARMBG).equals(YunTongXun.FarmBgImageDefault)) {
			farmbgUrl = YunTongXun.FarmBgImageDefault;
		} else {
			farmbgUrl = MySharePreference.getValueFromKey(
					FarmConfigActivity.this, MySharePreference.FARMBG);
			final String farmBgurlFinal = farmbgUrl;
			farmBg.setTag(farmBgurlFinal);
			ImageDownloadHelper.getInstance().imageDownload(null,
					FarmConfigActivity.this, farmBgurlFinal, farmBg,
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
		if (MySharePreference.getValueFromKey(FarmConfigActivity.this,
				MySharePreference.FARMLOGO).equals(MySharePreference.NOTSTATE)||MySharePreference.getValueFromKey(FarmConfigActivity.this,
						MySharePreference.FARMLOGO).equals(YunTongXun.FarmLogoImageDefault)) {
			farmlogoUrl = YunTongXun.FarmLogoImageDefault;
		} else {
			farmlogoUrl = MySharePreference.getValueFromKey(
					FarmConfigActivity.this, MySharePreference.FARMLOGO);
			final String farmLogourlFinal = farmlogoUrl;
			farmLogo.setTag(farmLogourlFinal);
			ImageDownloadHelper.getInstance().imageDownload(null,
					FarmConfigActivity.this, farmLogourlFinal, farmLogo,
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

	
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.farmconfig_newfarm_iv_address:
			DialogManager.showDialogClickListener(FarmConfigActivity.this,
					R.string.farmconfig_GPS, R.string.farmconfig_GPS_content,
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							((AlertDialog) arg0.getTag()).dismiss();
							GpsManager pps = GpsManager.getInstance();
							pps.setGpsView(farmAddress, 1);
							farmAddress.setEnabled(false);
							farmAddress.setText("正在获取地理信息...");
						}
					});
			break;
		case R.id.farmconfig_newfarm_et_name_ll:
			Intent nameIntent = new Intent();
			nameIntent.putExtra("type", 1);
			nameIntent.setClass(FarmConfigActivity.this,
					FarmConfigChangeActivity.class);
			startActivityForResult(nameIntent, 10);
			break;
		case R.id.farmconfig_newfarm_et_address_rl:
			DialogManager.showDialogClickListener(FarmConfigActivity.this,
					R.string.farmconfig_GPS, R.string.farmconfig_GPS_content,
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							((AlertDialog) arg0.getTag()).dismiss();
							GpsManager pps = GpsManager.getInstance();
							pps.setGpsView(farmAddress, 1);
							farmAddress.setEnabled(false);
							farmAddress.setText("正在获取地理信息...");
						}
					});
//			Intent addressIntent = new Intent();
//			addressIntent.putExtra("type", 2);
//			addressIntent.setClass(FarmConfigActivity.this,
//					FarmConfigChangeActivity.class);
//			startActivityForResult(addressIntent, 10);
			break;
		case R.id.farmconfig_newfarm_iv_introduce_rl:
			Intent introduceIntent = new Intent();
			introduceIntent.putExtra("type", 3);
			introduceIntent.setClass(FarmConfigActivity.this,
					FarmConfigChangeActivity.class);
			startActivityForResult(introduceIntent, 10);
			break;
		case R.id.farmConfigReturn:
			finish();
			break;
		// 设置背景
		case R.id.farmconfig_newfarm_image_bg:
			imageClick =1;
			getImageFromPhone();
			break;

		// 设置Logo
		case R.id.farmconfig_newfarm_image_logo:
			imageClick = 2;
			getImageFromPhone();
			break;

		case R.id.popdelete_iv:
			btn_PopupMenu(popdelete_iv);
			
			break;
		case R.id.popdelet_ll:
			popWindow.dismiss();
			showDialogDelete(FarmConfigActivity.this,1);
			break;
		case R.id.complete_btn:
			finish();
			break;
		default:
			break;
		}
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
		if (!MySharePreference.getValueFromKey(FarmConfigActivity.this, MySharePreference.FARMOWNERSERIAL).equals(
				 MySharePreference.getValueFromKey(FarmConfigActivity.this,MySharePreference.ACCOUNTID)))
		{
			TextView popdelete_tv = (TextView) popContent.findViewById(R.id.popdelete_tv);
			popdelete_tv.setText(R.string.exit);
		}
		popll.setOnClickListener(this);
		
	}
	
	
	private void getImageFromPhone() {
		final AlertDialog dlg = new AlertDialog.Builder(FarmConfigActivity.this).create();
		dlg.show();
		Window window = dlg.getWindow();
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.tishi_show_dialog_add_create_farm);
		ListView list = (ListView) window
				.findViewById(R.id.add_create_farm_list);
		List<HashMap<String, String>> soilItems = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("name", getResources().getString(R.string.beingnew_camera));
		soilItems.add(map);
		map = new HashMap<String, String>();
		map.put("name", getResources().getString(R.string.beingnew_fromsd));
		soilItems.add(map);
		final BoxSoilAdapter adapter = new BoxSoilAdapter(FarmConfigActivity.this, soilItems, getResources().getString(R.string.beingnew_fromsd),3);
		list.setAdapter(adapter);
		list.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View view,
							int arg2, long arg3) {
						if(((TextView)view.findViewById(R.id.solillistitem_name)).getText().toString().equals(getResources().getString(R.string.beingnew_camera).toString()))
						{
							adapter.selectType = getResources().getString(R.string.beingnew_camera).toString();
						}
						else if(((TextView)view.findViewById(R.id.solillistitem_name)).getText().toString().equals(getResources().getString(R.string.beingnew_fromsd).toString()))
						{
							adapter.selectType = getResources().getString(R.string.beingnew_fromsd).toString();
						}
						adapter.notifyDataSetChanged();
					}
				}
				);
		// 为确认按钮添加事件,执行退出应用操作
		Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(adapter.selectType!=null )
				{
					if(adapter.selectType.equals(getResources().getString(R.string.beingnew_camera).toString()))
					{
						Intent intent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
								//下面这句指定调用相机拍照后的照片存储的路径
								intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
								.fromFile(new File(Environment
								.getExternalStorageDirectory(),
								"yuhekeji.png")));
								startActivityForResult(intent, RESULT_LOAD_IMAGE_camera);
					}else if(adapter.selectType.equals(getResources().getString(R.string.beingnew_fromsd).toString()))
					{
						Intent i = new Intent(
								Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(i, RESULT_LOAD_IMAGE_phone);
					}
					adapter.selectType = "";
					dlg.dismiss();
				}
			}
		});
		Button cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				adapter.selectType = "";
				dlg.dismiss();
			}
		});
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		//从手机
		if (requestCode == RESULT_LOAD_IMAGE_phone && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			if(imageClick == 2)
			{
				Intent intent = new Intent();
				intent.setAction("com.android.camera.action.CROP");
				intent.setDataAndType(selectedImage, "image/*");// mUri是已经选择的图片Uri
				intent.putExtra("crop", "true");
				intent.putExtra("aspectX", 1);// 裁剪框比例
				intent.putExtra("aspectY", 1);
				intent.putExtra("outputX", 150);// 输出图片大小
				intent.putExtra("outputY", 150);
				intent.putExtra("circleCrop", "");
				intent.putExtra("return-data", true);
				startActivityForResult(intent, 200);
			}
			if(imageClick == 1)
			{
				Intent intent = new Intent();
				intent.setAction("com.android.camera.action.CROP");
				intent.setDataAndType(selectedImage, "image/*");// mUri是已经选择的图片Uri
				intent.putExtra("crop", "true");
				intent.putExtra("aspectX", farmBg.getWidth()/farmBg.getHeight());// 裁剪框比例
				intent.putExtra("aspectY", 1);
				intent.putExtra("outputX", 150 * farmBg.getWidth()/farmBg.getHeight());// 输出图片大小
				intent.putExtra("outputY", 150);
				intent.putExtra("circleCrop", "");
				intent.putExtra("return-data", true);
				startActivityForResult(intent, 200);
			}
		}

		if (requestCode == 200 && resultCode == RESULT_OK && null != data) {
			Bitmap bmap = data.getParcelableExtra("data");
			if(imageClick == 2)
			{
				String nameTime = UUID.randomUUID().toString()+".png";
				ImageUtils.saveMyBitmap(nameTime, bmap);
				farmLogo.setTag(YunTongXun.ImagePath+ nameTime);
				changeFarmInfoImage(initialURLparaLogo());
			}
			if(imageClick == 1)
			{
				String nameTime = UUID.randomUUID().toString()+".png";
				ImageUtils.saveMyBitmap(nameTime, bmap);
				farmBg.setTag(YunTongXun.ImagePath + nameTime);
				changeFarmInfoImage(initialURLparaBG());
			}
		}
		
		if (requestCode == RESULT_LOAD_IMAGE_camera && resultCode == RESULT_OK) 
		{
			if(imageClick == 2)
			{
				File picture = new File(Environment.getExternalStorageDirectory(),"yuhekeji.png");       
				Intent intent = new Intent("com.android.camera.action.CROP");
				intent.setDataAndType(Uri.fromFile(picture), "image/*");
				//下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
				intent.putExtra("crop", "true");
				// aspectX aspectY 是宽高的比例
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				// outputX outputY 是裁剪图片宽高
				intent.putExtra("outputX", 150);
				intent.putExtra("outputY", 150);
				intent.putExtra("return-data", true);
				startActivityForResult(intent, 200);
			}
			if(imageClick == 1)
			{
				File picture = new File(Environment.getExternalStorageDirectory(),"yuhekeji.png");       
				Intent intent = new Intent("com.android.camera.action.CROP");
				intent.setDataAndType(Uri.fromFile(picture), "image/*");
				//下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
				intent.putExtra("crop", "true");
				// aspectX aspectY 是宽高的比例
				intent.putExtra("aspectX", farmBg.getWidth()/farmBg.getHeight());// 裁剪框比例
				intent.putExtra("aspectY", 1);
				intent.putExtra("outputX", 150 * farmBg.getWidth()/farmBg.getHeight());// 输出图片大小
				intent.putExtra("outputY", 150);
				intent.putExtra("return-data", true);
				startActivityForResult(intent, 200);
			}
		}
		// 修改农场信息
		if (requestCode == 10) {
			initView();
		}
	}

	/**
	 * Toast提示框
	 * 
	 * @param id
	 */
	private void showToast(int id) {
		Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
	}

	

	/**
	 * 初始化logo url
	 * @return
	 */
	private String initialURLparaLogo()
	{
		String para = "name=" + MySharePreference.getValueFromKey(this, MySharePreference.FARMNAME);
		para += "&logo=" + farmLogo.getTag().toString();
		return para;
	}
	
	/**
	 * 初始化 bg url
	 * @return
	 */
	private String initialURLparaBG()
	{
		String para = "name=" + MySharePreference.getValueFromKey(this, MySharePreference.FARMNAME);
		para += "&bg=" + farmBg.getTag().toString();
		return para;
	}
	
	@Override
	protected void onDestroy() {
		GpsManager.getInstance().destory();
		super.onDestroy();
	}
	
	int delete_exit_type = 0;
	private void showDialogDelete(final Context context,final int enterCount)
	{
		
		final AlertDialog dlg = new AlertDialog.Builder(context).create();
		dlg.setCancelable(false);
		dlg.show();
		Window window = dlg.getWindow();
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.tishi_show_dialog_twobtn);
		if (!MySharePreference.getValueFromKey(FarmConfigActivity.this, MySharePreference.FARMOWNERSERIAL).equals(
			 MySharePreference.getValueFromKey(FarmConfigActivity.this,MySharePreference.ACCOUNTID))) 
		{
			((TextView) window.findViewById(R.id.dialog_head)).setText(R.string.farmconfig_newfarm_exitFarm);
	        ((TextView) window.findViewById(R.id.dialog_content)).setText(R.string.farmconfig_exitFarm_tishi);
	        delete_exit_type = 1;
		}else
		{
			((TextView) window.findViewById(R.id.dialog_head)).setText(R.string.farmconfig_newfarm_deleteFarm);
			if(enterCount == 1)
			{
				((TextView) window.findViewById(R.id.dialog_content)).setText(R.string.farmconfig_deleteFarm_tishi);
			}
			else if(enterCount == 2)
			{
				((TextView) window.findViewById(R.id.dialog_content)).setText(R.string.farmconfig_deleteFarm_tishi2);
			}
	        delete_exit_type = 2;
		}
		
		
		// 为确认按钮添加事件,执行退出应用操作
		Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
		ok.setTextColor(getResources().getColor(R.color.text_fe462e));
		if(delete_exit_type ==1)
		{
			ok.setText(R.string.exit);
		}
		else
		{
			ok.setText(R.string.pop_delete);
		}
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dg = MainFragment.createLoadingDialog(FarmConfigActivity.this);
				dg.show();
				if (delete_exit_type == 1) {
					NetConnectionFarm.FarmExitNetConnection(
							NetUrls.URL_EXIT_FARM(MySharePreference
									.getValueFromKey(FarmConfigActivity.this,
											MySharePreference.FARMID)),
							"Token "
									+ MySharePreference.getValueFromKey(
											FarmConfigActivity.this,
											MySharePreference.ACCOUNTTOKEN),
							new FarmSuccessCallBack() {
								@Override
								public void onSuccess(
										HashMap<String, Object> map) {
									if (map != null) {
										
										if (!map.get("status_code").toString()
												.trim().equals("200")) {
											ErrorMarkToast.showCallBackToast(
													FarmConfigActivity.this,
													map);
											return;
										}
										delete_exitFarm();
										dg.dismiss();
										dlg.dismiss();
									} else {
										dg.dismiss();
										dlg.dismiss();
										showToast(R.string.interneterror);
									}
								}
							});
				}
				// 删除农场
				if (delete_exit_type == 2) {
					
					if(enterCount == 1)
					{
						dg.dismiss();
						showDialogDelete(context,2);
						dlg.dismiss();
						return;
					}
					
					NetConnectionFarm.FarmDeleteNetConnection(
							NetUrls.URL_DELETE_FARM(MySharePreference
									.getValueFromKey(FarmConfigActivity.this,
											MySharePreference.FARMID)),
							"Token "
									+ MySharePreference.getValueFromKey(
											FarmConfigActivity.this,
											MySharePreference.ACCOUNTTOKEN),
							new FarmSuccessCallBack() {
								@Override
								public void onSuccess(
										HashMap<String, Object> map) {
									
									if (map != null) {
										if (!map.get("status_code").toString()
												.trim().equals("204")) {
										
											ErrorMarkToast.showCallBackToast(
													FarmConfigActivity.this,
													map);
											return;
										}
										delete_exitFarm();
										dg.dismiss();
										dlg.dismiss();
									} else {
										dg.dismiss();
										dlg.dismiss();
										showToast(R.string.interneterror);
									}
								}
							});
				}
			}
		});
		Button cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
	}

	// TODO 退出-删除 农场
	private void delete_exitFarm() {
		MySharePreference.clearFarmInfo(FarmConfigActivity.this);
		FarmDaoManager farmDao = new FarmDaoManager(FarmConfigActivity.this);
		farmDao.clearFarmTable(0);
		finish();
	}
}
