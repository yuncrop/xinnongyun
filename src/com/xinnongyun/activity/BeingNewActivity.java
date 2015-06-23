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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xinnongyun.adapter.BoxSoilAdapter;
import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.net.NetConnectionBeing;
import com.xinnongyun.net.NetConnectionBeing.BeingSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.BeingDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.utils.DialogManager;
import com.xinnongyun.utils.ImageUtils;
import com.xinnongyun.utils.StringUtil;

public class BeingNewActivity extends Activity implements OnClickListener {

	private String beingType;
	private Button submitBtn;
	private EditText beingName;
	private ImageView beingImage, newbeingReturn;
	private TextView newbeing_head;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏ActionBar
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//		         WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		setContentView(R.layout.activity_beingnew);
		Intent intent = getIntent();
		if (intent != null) {
			beingType = intent.getStringExtra("type");
		}
		initialView();
		if (beingType.equals("1")) {
			newbeing_head.setText(R.string.beingnew_crop_head);
			newbeing_head.setHint(R.string.beingnew_crop_namehint);
			beingImage.setBackgroundResource(R.drawable.zuowu_head);
		} else {
			newbeing_head.setText(R.string.beingnew_anim_head);
			newbeing_head.setHint(R.string.beingnew_anim_namehint);
			beingImage.setBackgroundResource(R.drawable.animal_head);
		}
	}

	// 初始化控件
	private void initialView() {
		newbeing_head = (TextView) findViewById(R.id.newbeing_head);
		submitBtn = (Button) findViewById(R.id.submit_beingnew);
		submitBtn.setOnClickListener(this);
		newbeingReturn = (ImageView) findViewById(R.id.newbeingReturn);
		newbeingReturn.setOnClickListener(this);
		beingName = (EditText) findViewById(R.id.newbeingName);
		beingImage = (ImageView) findViewById(R.id.newbeingImage);
		beingImage.setOnClickListener(this);
	}

	
	private Dialog dg;
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.submit_beingnew:
			
			if (!StringUtil.checkEditTextIsEmpty(beingName)
					&& beingName.getText().toString().trim().length() > 0) {
				submitBtn.setClickable(false);
				dg = MainFragment.createLoadingDialog(this);
				dg.show();
				NetConnectionBeing.BeingPostNetConnection(
						NetUrls.URL_BEINGS_FARM,
						initialParas(),
						"Token "
								+ MySharePreference.getValueFromKey(
										BeingNewActivity.this,
										MySharePreference.ACCOUNTTOKEN),
						new BeingSuccessCallBack() {

							@Override
							public void onSuccess(HashMap<String, Object> result) {
								dg.dismiss();
								submitBtn.setClickable(true);
								if (result != null) {
									if (result.keySet().contains("status_code")) {
										ErrorMarkToast.showCallBackToast(
												BeingNewActivity.this, result);
										return;
									}
									if(result.get(TablesColumns.TABLEBEING_IMAGE)!=null && !result.get(TablesColumns.TABLEBEING_IMAGE).toString().equals("null"))
									{
										ImageUtils.fileReName(beingImage.getTag().toString(), StringUtil.getFileNameFromUrl(result.get(TablesColumns.TABLEBEING_IMAGE).toString()));
									}
									BeingDaoDBManager beingDao = new BeingDaoDBManager(BeingNewActivity.this);
									List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
									list.add(result);
									beingDao.insertAllBeing(list);
									setResult(2);
									finish();
								} else {
									Toast.makeText(BeingNewActivity.this,
											R.string.interneterror,
											Toast.LENGTH_SHORT).show();
								}
							}
						});
			} else {
				if (beingType.equals("1")) {
					DialogManager.showDialogSimple(BeingNewActivity.this,
							//R.string.beingnew_crop_namehint,
							R.string.beingnew_name_empty);
				} else if (beingType.equals("2")) {
					DialogManager.showDialogSimple(BeingNewActivity.this,
							//R.string.beingnew_anim_namehint,
							R.string.beingnew_name_empty);
				}
			}
			break;
		case R.id.newbeingReturn:
			finish();
			break;
		case R.id.newbeingImage:
			getImageFromPhone();
			break;
		default:
			break;
		}
	}

	String imagePath = "";
	private void getImageFromPhone() {
		
		final AlertDialog dlg = new AlertDialog.Builder(BeingNewActivity.this).create();
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
		final BoxSoilAdapter adapter = new BoxSoilAdapter(BeingNewActivity.this, soilItems, getResources().getString(R.string.beingnew_fromsd),3);
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
								startActivityForResult(intent, 2);
					}else if(adapter.selectType.equals(getResources().getString(R.string.beingnew_fromsd).toString()))
					{
						Intent i = new Intent(
								Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(i, 1);
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
		if (resultCode == RESULT_OK && requestCode == 1) {
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
		
		if(requestCode == 200 && resultCode == RESULT_OK)
		{
			Bitmap bmap = data.getParcelableExtra("data");
			
	
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				beingImage.setBackground(new BitmapDrawable(bmap));
			} else {
				beingImage.setBackgroundDrawable(new BitmapDrawable(bmap));
			}
			
			String nameTime = UUID.randomUUID().toString()+".png";
			beingImage.setTag(YunTongXun.ImagePath
					+ nameTime);
			beingImage.setDrawingCacheEnabled(true);
			ImageUtils.saveMyBitmap(nameTime, Bitmap.createBitmap(beingImage.getDrawingCache()));
			beingImage.setDrawingCacheEnabled(false);
		}
		if (resultCode == RESULT_OK && requestCode == 2) {
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
	}

	// 初始化请求参数 name=番茄& icon= e/ru/Downloads/icon.png& kingdom=1& farm={id}' -H
	// 'Authorization:
	private String initialParas() {
		String para = TablesColumns.TABLEBEING_NAME
				+ "="
				+ beingName.getText().toString().trim()
				+ "&"
				+ TablesColumns.TABLEBEING_KINGDOM
				+ "="
				+ beingType
				+ "&"
				+ TablesColumns.TABLEBEING_FARM
				+ "="
				+ MySharePreference.getValueFromKey(BeingNewActivity.this,
						MySharePreference.FARMID);
		if (beingImage.getTag() != null) {
			para += "&" + TablesColumns.TABLEBEING_IMAGE + "="
					+ beingImage.getTag().toString();
		}
		return para;
	}

}
