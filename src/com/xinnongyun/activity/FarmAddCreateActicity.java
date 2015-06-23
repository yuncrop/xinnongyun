package com.xinnongyun.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

import com.sitech.oncon.barcode.core.CaptureActivity;
import com.xinnongyun.config.YunTongXun;

/**
 * 添加或加入农场
 * @author sm
 *
 */
public class FarmAddCreateActicity extends Activity implements OnClickListener{

	private ImageView uiReturn,farmAdd,farmCreate;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏ActionBar
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//		         WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		setContentView(R.layout.activity_add_create_farm);
		
		uiReturn = (ImageView) findViewById(R.id.farm_add_create_Return);
		uiReturn.setOnClickListener(this);
		farmCreate = (ImageView) findViewById(R.id.farm_create_iv);
		farmCreate.setOnClickListener(this);
		farmAdd = (ImageView) findViewById(R.id.farm_add_iv);
		farmAdd.setOnClickListener(this);
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.farm_add_create_Return:
			finish();
			break;
		case R.id.farm_add_iv:
			Intent addFarmIntent = new Intent(FarmAddCreateActicity.this,
					CaptureActivity.class);
			addFarmIntent.putExtra(YunTongXun.CAPTURE_KEY,
					YunTongXun.CAPTURE_ADDFARM);
			startActivity(addFarmIntent);
			finish();
			break;
		case R.id.farm_create_iv:
			Intent intent = new Intent();
			intent.setClass(FarmAddCreateActicity.this, FarmConfigActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
		
	}
	
}
