package com.xinnongyun.activity;

import java.util.HashMap;

import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.net.NetConnectionLoginZhuCe;
import com.xinnongyun.net.NetConnectionLoginZhuCe.ZhuceSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.utils.DialogManager;
import com.xinnongyun.utils.StringUtil;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 设置昵称
 * 
 * @author sm
 * 
 */
public class NickNameActivity extends Activity implements OnClickListener {

	private EditText nickname;
	private Button save;
	private ImageView returnback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏ActionBar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		         WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		setContentView(R.layout.activity_login_zhuce_nickname);
		initialView();
		ExitApplication.getInstance().addActivity(this);
	}

	private void initialView() {
		nickname = (EditText) findViewById(R.id.nickname_et);
		save = (Button) findViewById(R.id.zhuce_btn_submit);
		save.setOnClickListener(this);
		returnback = (ImageView) findViewById(R.id.zhuce_return);
		returnback.setOnClickListener(this);
	}

	private Dialog dg;
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.zhuce_return:
			LoginActivity.firstOpenApp(NickNameActivity.this);
			finish();
			break;
		case R.id.zhuce_btn_submit:
			if(!StringUtil.checkEditTextIsEmpty(nickname) && StringUtil.checkNickName(nickname.getText().toString().trim()))
			{
				dg = MainFragment.createLoadingDialog(NickNameActivity.this);
				dg.show();
				String userPara = "nickname="
						+ nickname.getText().toString().trim();
				NetConnectionLoginZhuCe.ModifyPutNetConnection(
						NetUrls.URL_MODIFY_USERINFO+MySharePreference.getValueFromKey(NickNameActivity.this, 
								MySharePreference.ACCOUNTID)+"/", userPara,
								"Token "+MySharePreference.getValueFromKey(NickNameActivity.this, MySharePreference.ACCOUNTTOKEN),
						new ZhuceSuccessCallBack() {
							@Override
							public void onSuccess(HashMap<String, Object> result) {
								dg.dismiss();
								if(result!=null)
								{
									if(result.keySet().contains("status_code"))
									{
										ErrorMarkToast.showCallBackToast(NickNameActivity.this, result);
										return;
									}
									result.put(MySharePreference.ACCOUNTID, result.get("user").toString());
									MySharePreference.saveAccountModifyInfo(NickNameActivity.this, result);
									LoginActivity.firstOpenApp(NickNameActivity.this);
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
				DialogManager.showDialogSimple(NickNameActivity.this,
						//R.string.zhuce_setnickname,
						R.string.zhuce_setnickname_isempty);
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
