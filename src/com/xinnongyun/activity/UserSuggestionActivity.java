package com.xinnongyun.activity;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.net.NetConnectionNotice;
import com.xinnongyun.net.NetConnectionSuggestion;
import com.xinnongyun.net.NetConnectionSuggestion.SuggestionSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.NoticeDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.utils.DialogManager;
import com.xinnongyun.utils.StringUtil;

public class UserSuggestionActivity extends Activity implements OnClickListener{

	private ImageView suggestionReturn;
	private TextView save_tv;
	private EditText userinfo_suggestion;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏ActionBar
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//		         WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		setContentView(R.layout.acticity_user_suggestion);
		Intent intent = getIntent();
		if(intent!=null)
		{
			int notifyid = intent.getIntExtra("notify_id", 0);
			if(notifyid!=0)
			{
				NotificationManager manger = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
				manger.cancel(notifyid);
			}
			
			String notice_id = intent.getStringExtra(TablesColumns.TABLENOTICE_ID+"notice");
			if(notice_id!=null && notice_id.length()>0)
			{
				NoticeDaoDBManager daoDBManager = new NoticeDaoDBManager(UserSuggestionActivity.this);
				SimpleDateFormat sDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd hh:mm:ss");
				String date = sDateFormat.format(new java.util.Date());
				NetConnectionNotice.NoticePutNetConnection(
						NetUrls.getUpdateNotice(notice_id)
						,"read_at=" + date.replace(" ", "T")
						,"Token " + MySharePreference.getValueFromKey(UserSuggestionActivity.this,MySharePreference.ACCOUNTTOKEN), null);
				daoDBManager.changeFarmingBeingCollects(notice_id, date.replace(" ", "T"));
			}
		}
		
		suggestionReturn = (ImageView) findViewById(R.id.farmConfigChangeReturn);
		suggestionReturn.setOnClickListener(this);
		userinfo_suggestion = (EditText) findViewById(R.id.userinfo_suggestion);
		save_tv = (TextView) findViewById(R.id.save_tv);
		save_tv.setOnClickListener(this);
	}
	
	private Dialog dg;
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.farmConfigChangeReturn:
			finish();
			break;
		case R.id.save_tv:
			if(!StringUtil.checkEditTextIsEmpty(userinfo_suggestion) && StringUtil.checkSuggestion(userinfo_suggestion.getText().toString().trim()))
			{
				dg = MainFragment.createLoadingDialog(UserSuggestionActivity.this);
				dg.show();
				String para = "content="+userinfo_suggestion.getText().toString().trim();
				NetConnectionSuggestion.PostNetConnection(NetUrls.URL_SUGGESTION, para, 
						"Token "
								+ MySharePreference
										.getValueFromKey(
												UserSuggestionActivity.this,
												MySharePreference.ACCOUNTTOKEN),
						new SuggestionSuccessCallBack() {
							@Override
							public void onSuccess(HashMap<String, Object> result) {
								dg.dismiss();
								if (result != null) {
									if (!result.get("status_code").toString().equals("201")) {
										ErrorMarkToast.showCallBackToast(
												UserSuggestionActivity.this, result);
										return;
									}
									else
									{
										setResult(10);
										finish();
									}
								} else {
									Toast.makeText(UserSuggestionActivity.this,
											R.string.interneterror,
											Toast.LENGTH_SHORT).show();
								}
							}
						});
			}
			else
			{
				DialogManager.showDialogSimple(UserSuggestionActivity.this,
						//R.string.userinfo_sliding_menu,
						R.string.userinfo_suggestion_error);
			}
			break;
		default:
			break;
		}
	}
}
