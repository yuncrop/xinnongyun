package com.xinnongyun.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xinnongyun.adapter.BoxSoilAdapter;
import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.imageload.ImageDownloadHelper;
import com.xinnongyun.imageload.ImageDownloadHelper.OnImageDownloadListener;
import com.xinnongyun.net.NetConnectionLoginZhuCe;
import com.xinnongyun.net.NetConnectionLoginZhuCe.ZhuceSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.utils.ImageUtils;
import com.xinnongyun.utils.StringUtil;

public class UserInfoActivity extends Activity implements OnClickListener {

	private ImageView userImage,userConfigChangeReturn;
	private TextView userinfo_change_nickname,userinfo_change_phone;
	private RelativeLayout userinfoChangeImage_rl,userinfo_change_nickname_rl,userinfo_change_weixin_rl,userinfo_change_weibo_rl,userinfo_change_pwd_rl;
	//更改头像返回码
	private final int RESULT_LOAD_IMAGE_BG_camera = 3;
	private final int RESULT_LOAD_IMAGE_BG_phone = 4;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏ActionBar
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//		         WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		setContentView(R.layout.acticity_userinfo);
		initialView();
	}

	/**
	 * 初始化界面控件
	 */
	@SuppressWarnings("deprecation")
	private void initialView() {
		userinfoChangeImage_rl = (RelativeLayout) findViewById(R.id.userinfoChangeImage_rl);
		userinfoChangeImage_rl.setOnClickListener(this);
		userConfigChangeReturn = (ImageView) findViewById(R.id.userConfigChangeReturn);
		userConfigChangeReturn.setOnClickListener(this);
		userImage = (ImageView) findViewById(R.id.userinfoChangeImage);
		userImage.setOnClickListener(this);
		userinfo_change_nickname_rl = (RelativeLayout) findViewById(R.id.userinfo_change_nickname_rl);
		userinfo_change_nickname_rl.setOnClickListener(this);
		userinfo_change_weixin_rl = (RelativeLayout) findViewById(R.id.userinfo_change_weixin_rl);
		userinfo_change_weixin_rl.setOnClickListener(this);
		userinfo_change_weibo_rl = (RelativeLayout) findViewById(R.id.userinfo_change_weibo_rl);
		userinfo_change_weibo_rl.setOnClickListener(this);
		userinfo_change_pwd_rl = (RelativeLayout) findViewById(R.id.userinfo_change_pwd_rl);
		userinfo_change_pwd_rl.setOnClickListener(this);
		
		userinfo_change_nickname = (TextView) findViewById(R.id.userinfo_change_nickname);
		userinfo_change_phone = (TextView) findViewById(R.id.userinfo_change_phone);
		
		
		if(!MySharePreference.getValueFromKey(UserInfoActivity.this, MySharePreference.ACCOUNTNICKNAME).equals(MySharePreference.NOTSTATE) &&
		   !MySharePreference.getValueFromKey(UserInfoActivity.this, MySharePreference.ACCOUNTNICKNAME).equals("null"))
		{
			userinfo_change_nickname.setText(MySharePreference.getValueFromKey(UserInfoActivity.this, MySharePreference.ACCOUNTNICKNAME));
		}
		else
		{
			userinfo_change_nickname.setText(StringUtil.getNickNameFromPhone(MySharePreference.getValueFromKey(UserInfoActivity.this, MySharePreference.LOGINSTATE)));
		}
		userinfo_change_phone.setText(MySharePreference.getValueFromKey(UserInfoActivity.this, MySharePreference.LOGINSTATE));
		String imageHeadUrl = "";
		if(MySharePreference.getValueFromKey(UserInfoActivity.this, MySharePreference.ACCOUNTAVATAR).equals(MySharePreference.NOTSTATE))
		{
			imageHeadUrl = YunTongXun.UserHeadImageDefault;
		}
		else
		{
			imageHeadUrl = MySharePreference.getValueFromKey(UserInfoActivity.this, MySharePreference.ACCOUNTAVATAR);
		}
		final String imageurlFinal = imageHeadUrl;
		userImage.setTag(imageurlFinal);
		ImageDownloadHelper.getInstance().imageDownload(null,UserInfoActivity.this, imageurlFinal, userImage,"/001yunhekeji100",
				new OnImageDownloadListener() {
					public void onImageDownload(Bitmap bitmap, String imgUrl) {
						if (bitmap != null) {
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
								userImage.setBackground(new BitmapDrawable(bitmap));
							} else {
								userImage.setBackgroundDrawable(new BitmapDrawable(bitmap));
							}
							
							
							userImage.setTag(YunTongXun.ImagePath
									+ StringUtil.getFileNameFromUrl(imageurlFinal));
						} else {
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
								userImage.setBackground(new BitmapDrawable(ImageUtils
										.toRoundBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.user_head))));
							} else {
								userImage.setBackgroundDrawable(new BitmapDrawable(ImageUtils
										.toRoundBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.user_head))));
							}
							
						}
					}
				});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		//更改头像
		case R.id.userinfoChangeImage:
		case R.id.userinfoChangeImage_rl:
			getImageFromPhone();
			break;
		case R.id.userinfo_change_nickname_rl:
			Intent nameIntent = new Intent();
			nameIntent.putExtra("type", 1);
			nameIntent.setClass(UserInfoActivity.this, UserInfoModifyActivity.class);
			startActivityForResult(nameIntent,10);
			break;
		case R.id.userinfo_change_weixin_rl:
			break;
		case R.id.userinfo_change_weibo_rl:
			break;
		case R.id.userinfo_change_pwd_rl:
			Intent pwdIntent = new Intent();
			pwdIntent.putExtra("type", 2);
			pwdIntent.setClass(UserInfoActivity.this, UserInfoModifyActivity.class);
			startActivity(pwdIntent);
			break;
		case R.id.userConfigChangeReturn:
			finish();
			break;
		default:
			break;
		}
	}

	
	private void getImageFromPhone() {
		final AlertDialog dlg = new AlertDialog.Builder(UserInfoActivity.this).create();
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
		final BoxSoilAdapter adapter = new BoxSoilAdapter(UserInfoActivity.this, soilItems, getResources().getString(R.string.beingnew_fromsd).toString(),3);
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
								startActivityForResult(intent, RESULT_LOAD_IMAGE_BG_camera);
					}else if(adapter.selectType.equals(getResources().getString(R.string.beingnew_fromsd).toString()))
					{
						Intent i = new Intent(
								Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(i, RESULT_LOAD_IMAGE_BG_phone);
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
	
	@SuppressWarnings("deprecation")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_LOAD_IMAGE_BG_phone && resultCode == RESULT_OK) {
			Uri selectedImage = data.getData();
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
		if (resultCode == RESULT_OK && requestCode == RESULT_LOAD_IMAGE_BG_camera) {
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
		
		if (requestCode == 200 && resultCode == RESULT_OK) {
			Bitmap bmap = data.getParcelableExtra("data");
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			String nameTime = UUID.randomUUID().toString()+".png";
			ImageUtils.saveMyBitmap(nameTime,ImageUtils.toRoundBitmap(bmap));
			userImage.setTag(YunTongXun.ImagePath + nameTime);
			String userPara = "avatar=" + userImage.getTag().toString();
			dg = MainFragment.createLoadingDialog(this);
			dg.show();
			NetConnectionLoginZhuCe.ModifyPutNetConnection(
					NetUrls.URL_MODIFY_USERINFO+MySharePreference.getValueFromKey(UserInfoActivity.this, 
							MySharePreference.ACCOUNTID)+"/", userPara,
							"Token "+MySharePreference.getValueFromKey(UserInfoActivity.this, MySharePreference.ACCOUNTTOKEN),
					new ZhuceSuccessCallBack() {
						@Override
						public void onSuccess(HashMap<String, Object> result) {
							dg.dismiss();
							if(result!=null)
							{
								if(result.keySet().contains("status_code"))
								{
									ErrorMarkToast.showCallBackToast(UserInfoActivity.this, result);
									return;
								}
								ImageUtils.fileReName(StringUtil.getFileNameFromUrl(userImage.getTag().toString()),
										StringUtil.getFileNameFromUrl(result.get(MySharePreference.ACCOUNTAVATAR).toString()));
								result.put(MySharePreference.ACCOUNTID, result.get("user").toString());
								MySharePreference.saveAccountModifyInfo(UserInfoActivity.this, result);
								String imageHeadUrl;
								if(MySharePreference.getValueFromKey(UserInfoActivity.this, MySharePreference.ACCOUNTAVATAR).equals(MySharePreference.NOTSTATE))
								{
									imageHeadUrl = YunTongXun.UserHeadImageDefault;
								}
								else
								{
									imageHeadUrl = MySharePreference.getValueFromKey(UserInfoActivity.this, MySharePreference.ACCOUNTAVATAR);
								}
								final String imageurlFinal = imageHeadUrl;
								userImage.setTag(imageurlFinal);
								ImageDownloadHelper.getInstance().imageDownload(null,UserInfoActivity.this, imageurlFinal, userImage,"/001yunhekeji100",
										new OnImageDownloadListener() {
											public void onImageDownload(Bitmap bitmap, String imgUrl) {
												if (bitmap != null) {
													if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
														userImage.setBackground(new BitmapDrawable(bitmap));
													} else {
														userImage.setBackgroundDrawable(new BitmapDrawable(bitmap));
													}
													userImage.setTag(YunTongXun.ImagePath
															+ StringUtil.getFileNameFromUrl(imageurlFinal));
												} else {
													if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
														userImage.setBackground(new BitmapDrawable(ImageUtils
																.toRoundBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.user_head))));
													} else {
														userImage.setBackgroundDrawable(new BitmapDrawable(ImageUtils
																.toRoundBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.user_head))));
													}
												}
											}
										});
								
							}
							else
							{
								showToast(R.string.interneterror);
							}
						}
					});
		}
		
		if(requestCode == 10)
		{
			if(!MySharePreference.getValueFromKey(UserInfoActivity.this, MySharePreference.ACCOUNTNICKNAME).equals(MySharePreference.NOTSTATE) &&
					   !MySharePreference.getValueFromKey(UserInfoActivity.this, MySharePreference.ACCOUNTNICKNAME).equals("null"))
					{
						userinfo_change_nickname.setText(MySharePreference.getValueFromKey(UserInfoActivity.this, MySharePreference.ACCOUNTNICKNAME));
					}
					else
					{
						userinfo_change_nickname.setText(StringUtil.getNickNameFromPhone(MySharePreference.getValueFromKey(UserInfoActivity.this, MySharePreference.LOGINSTATE)));
					}
		}
		
	}

	private Dialog dg;
	
	/**
	 * Toast提示框
	 * 
	 * @param id
	 */
	private void showToast(int id) {
		Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
	}

}
