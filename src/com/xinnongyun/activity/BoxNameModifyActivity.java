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
import android.widget.TextView;
import android.widget.Toast;

import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.net.NetConnectionCollector;
import com.xinnongyun.net.NetConnectionCollector.CollectorSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.CollectDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.utils.DialogManager;
import com.xinnongyun.utils.StringUtil;

/**
 * 修改盒子信息
 * @author sm
 *
 */
public class BoxNameModifyActivity extends Activity implements OnClickListener{
	
	private ImageView boxInfoModifyReturn;
	private TextView save_tv;
	private EditText boxModify_name_et;
	private String collectId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏ActionBar
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//		         WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		setContentView(R.layout.acticity_box_motify);
		Intent intent = getIntent();
		collectId = intent.getStringExtra("collectId");
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
				DialogManager.showDialogSimple(BoxNameModifyActivity.this,
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
		String para = TablesColumns.TABLECOLLECTOR_NAME +"=" + boxModify_name_et.getText().toString().trim();
		NetConnectionCollector.CollectorPutNetConnection(1,NetUrls.URL_COLLECT_UPDATE(collectId), para,
				"Token "+MySharePreference.getValueFromKey(this, MySharePreference.ACCOUNTTOKEN),
				new CollectorSuccessCallBack() {
					@Override
					public void onSuccess(HashMap<String, Object> result) {
						dg.dismiss();
						if(result!=null)
						{
							if(result.keySet().contains("status_code"))
							{
								ErrorMarkToast.showCallBackToast(BoxNameModifyActivity.this, result);
								return;
							}
							CollectDaoDBManager dao = new CollectDaoDBManager(BoxNameModifyActivity.this);
							dao.updateCollectById(result);
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
