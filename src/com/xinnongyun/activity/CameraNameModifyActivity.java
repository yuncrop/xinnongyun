package com.xinnongyun.activity;

import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.net.NetConnectionCamera;
import com.xinnongyun.net.NetConnectionCamera.CameraSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.CameraDaoDBManager;
import com.xinnongyun.utils.DialogManager;
import com.xinnongyun.utils.StringUtil;

/**
 * 修改盒子信息
 * @author sm
 *
 */
public class CameraNameModifyActivity extends Activity implements OnClickListener{
	
	private ImageView boxInfoModifyReturn;
	private TextView save_tv;
	private EditText boxModify_name_et;
	private String collectId;
	private String isPublish = "true";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acticity_box_motify);
		((TextView)findViewById(R.id.userInfoModify_nameHint)).setText("请输入摄像头名称");
		Intent intent = getIntent();
		collectId = intent.getStringExtra("id");
		isPublish = intent.getStringExtra("isPublish");
		boxInfoModifyReturn = (ImageView) findViewById(R.id.boxInfoModifyReturn);
		boxInfoModifyReturn.setOnClickListener(this);
		save_tv = (TextView) findViewById(R.id.save_tv);
		save_tv.setOnClickListener(this);
		boxModify_name_et = (EditText) findViewById(R.id.boxModify_name_et);
		if(intent.getStringExtra("name")!=null)
		boxModify_name_et.setText(intent.getStringExtra("name"));
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.boxInfoModifyReturn:
			finish();
			break;
		case R.id.save_tv:
			if(!StringUtil.checkEditTextIsEmpty(boxModify_name_et) && StringUtil.checkBoxName(boxModify_name_et.getText().toString().trim()))
			{
				saveCollectInfo(collectId);
			}else
			{
				DialogManager.showDialogSimple(CameraNameModifyActivity.this,
						R.string.box_collect_name_empty);
			}
			break;
		default:
			break;
		}
	}
	
	private Dialog dg;
	private  void saveCollectInfo(String collectId)
	{
		dg = MainFragment.createLoadingDialog(this);
		dg.show();
		String para = "farm="+MySharePreference.getValueFromKey(this, MySharePreference.FARMID)+ 
				"&name=" + boxModify_name_et.getText().toString().trim()+
				"&publish=" + isPublish;
		NetConnectionCamera.CameraPutNetConnection(NetUrls.URL_CAMREA_MODIFY_ADD(collectId), para,
				"Token "+MySharePreference.getValueFromKey(this, MySharePreference.ACCOUNTTOKEN),
				new CameraSuccessCallBack() {
					@Override
					public void onSuccess(HashMap<String, Object> result) {
						dg.dismiss();
						if(result!=null)
						{
							if(result.keySet().contains("status_code"))
							{
								ErrorMarkToast.showCallBackToast(CameraNameModifyActivity.this, result);
								return;
							}
							CameraDaoDBManager daoDBManager = new CameraDaoDBManager(CameraNameModifyActivity.this);
							daoDBManager.updateCameraById(result,1);
							setResult(10);
							finish();
						}
						else
						{
							showToast(R.string.interneterror);
						}
					}
				}
				);
	}
	
	private void showToast(int id)
	{
		Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
	}
}
