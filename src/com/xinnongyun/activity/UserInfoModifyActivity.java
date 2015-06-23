package com.xinnongyun.activity;

import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.json.JsonUtil;
import com.xinnongyun.net.NetConnectionLoginZhuCe;
import com.xinnongyun.net.NetConnectionLoginZhuCe.ZhuceSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.utils.DialogManager;
import com.xinnongyun.utils.StringUtil;

public class UserInfoModifyActivity extends Activity implements OnClickListener{

	private RelativeLayout userInfoModify_nickname_rl;
	private LinearLayout userInfoModify_pwd_ll;
	private TextView userInfoModifyHead,save_tv;
	private ImageView userInfoModifyReturn;
	private EditText userInfoModify_name_et,userInfoModify_newpwd_et,userInfoModify_previouspwd_et;
	/**
	 * 1为昵称   2为密码
	 */
	private int type = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏ActionBar
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//		         WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		setContentView(R.layout.acticity_userinfo_motify);
		Intent intent = getIntent();
		if(intent!=null)
		{
			type = intent.getIntExtra("type", 0);
		}
		userInfoModifyReturn = (ImageView) findViewById(R.id.userInfoModifyReturn);
		userInfoModifyReturn.setOnClickListener(this);
		save_tv = (TextView) findViewById(R.id.save_tv);
		save_tv.setOnClickListener(this);
		userInfoModify_name_et = (EditText) findViewById(R.id.userInfoModify_name_et);
		userInfoModify_newpwd_et = (EditText) findViewById(R.id.userInfoModify_newpwd_et);
		userInfoModify_previouspwd_et = (EditText) findViewById(R.id.userInfoModify_previouspwd_et);
		userInfoModifyHead = (TextView) findViewById(R.id.userInfoModifyHead);
		userInfoModify_nickname_rl = (RelativeLayout) findViewById(R.id.userInfoModify_nickname_rl);
		userInfoModify_pwd_ll = (LinearLayout) findViewById(R.id.userInfoModify_pwd_ll);
		
		if(type == 1)
		{
			userInfoModifyHead.setText(R.string.userinfo_Modify_nickname_head);
			if(!MySharePreference.getValueFromKey(this, MySharePreference.ACCOUNTNICKNAME).equals(MySharePreference.NOTSTATE))
			{
				userInfoModify_name_et.setText(MySharePreference.getValueFromKey(this, MySharePreference.ACCOUNTNICKNAME));
			}
			
			userInfoModify_pwd_ll.setVisibility(View.GONE);
		}
		else if(type == 2)
		{
			userInfoModifyHead.setText(R.string.userinfo_modifyPwd);
			userInfoModify_nickname_rl.setVisibility(View.GONE);
		}
	}
	
	private Dialog dg;
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.userInfoModifyReturn:
			finish();
			break;
		case R.id.save_tv:
			if(type == 1)
			{
				if(!StringUtil.checkEditTextIsEmpty(userInfoModify_name_et) && StringUtil.checkNickName(userInfoModify_name_et.getText().toString().trim()))
				{
					dg = MainFragment.createLoadingDialog(UserInfoModifyActivity.this);
					dg.show();
					String userPara = "nickname="	+ userInfoModify_name_et.getText().toString().trim();
					NetConnectionLoginZhuCe.ModifyPutNetConnection(
							NetUrls.URL_MODIFY_USERINFO+MySharePreference.getValueFromKey(UserInfoModifyActivity.this, 
									MySharePreference.ACCOUNTID)+"/", userPara,
									"Token "+MySharePreference.getValueFromKey(UserInfoModifyActivity.this, MySharePreference.ACCOUNTTOKEN),
							new ZhuceSuccessCallBack() {
								@Override
								public void onSuccess(HashMap<String, Object> result) {
									dg.dismiss();
									if(result!=null)
									{
										if(result.keySet().contains("status_code"))
										{
											ErrorMarkToast.showCallBackToast(UserInfoModifyActivity.this, result);
											return;
										}
										result.put(MySharePreference.ACCOUNTID, result.get("user").toString());
										MySharePreference.saveAccountModifyInfo(UserInfoModifyActivity.this, result);
										finish();
									}
									else
									{
										showToast(R.string.interneterror);
									}
								}
							});
				}
				else
				{
					DialogManager.showDialogSimple(UserInfoModifyActivity.this,
							//R.string.userinfo_modifyPwd,
							R.string.userinfo_nicknameempty);
				}
			}else if(type == 2)
			{
				if (StringUtil.checkEditTextIsEmpty(userInfoModify_previouspwd_et)) {
					DialogManager.showDialogSimple(UserInfoModifyActivity.this,
							//R.string.userinfo_modifyPwd,
							R.string.userinfo_previousPwdEmpty);
					return;
				} else {
					if (!userInfoModify_previouspwd_et
							.getText()
							.toString()
							.equals(MySharePreference.getValueFromKey(this,
									MySharePreference.ACCOUNTPWD))) {
						DialogManager.showDialogSimple(UserInfoModifyActivity.this,
								//R.string.userinfo_modifyPwd,
								R.string.userinfo_previousPwdError);
						return;
					}
				}
				if (!StringUtil.checkEditTextIsEmpty(userInfoModify_newpwd_et) && StringUtil.checkPwd(userInfoModify_newpwd_et.getText().toString().trim())) {
					String userPara = "username=" + MySharePreference.getValueFromKey(UserInfoModifyActivity.this, MySharePreference.ACCOUNTUSERNAME) +"&password="
							+ userInfoModify_previouspwd_et.getText().toString().trim()+"&new_password="+userInfoModify_newpwd_et.getText().toString().trim();
					dg = MainFragment.createLoadingDialog(UserInfoModifyActivity.this);
					dg.show();
					NetConnectionLoginZhuCe.ModifyPutNetConnection(
							NetUrls.URL_MOFDIFYUSERPWD(MySharePreference.getValueFromKey(UserInfoModifyActivity.this, MySharePreference.ACCOUNTID)),
							userPara,"Token "+MySharePreference.getValueFromKey(UserInfoModifyActivity.this, MySharePreference.ACCOUNTTOKEN),
							new ZhuceSuccessCallBack() {
								@Override
								public void onSuccess(HashMap<String, Object> result) {
									dg.dismiss();
									if(result!=null)
									{
										if(result.keySet().contains("status_code"))
										{
											ErrorMarkToast.showCallBackToast(UserInfoModifyActivity.this, result);
											return;
										}
										result.putAll(JsonUtil.getHashMap(result.get("profile").toString()));
										result.put("key", JsonUtil.getOneValueFromJsonString(result.get("auth_token").toString(), "key"));
										result.put("password", userInfoModify_newpwd_et.getText().toString().trim());
										MySharePreference.saveAccountLoginInfo(UserInfoModifyActivity.this, result);				
										finish();
									}
									else
									{
										showToast(R.string.interneterror);
									}
								}
							});
					
					
				}
				else
				{
					DialogManager.showDialogSimple(UserInfoModifyActivity.this,
							//R.string.userinfo_modifyPwd,
							R.string.userinfo_newEmpty);
					return;
				}
			}
			
			break;
		default:
			break;
		}
	}
	
	
	private void showToast(int id) {
		Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
	}
}
