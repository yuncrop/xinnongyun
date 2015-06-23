package com.xinnongyun.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

/**
 * 联系我们
 * 
 * @author sm
 * 
 */
public class ContactUsActivity extends Activity {

	private ImageView farmConfigChangeReturn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏ActionBar
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//		         WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		setContentView(R.layout.acticity_contact_us);
		farmConfigChangeReturn = (ImageView) findViewById(R.id.farmConfigChangeReturn);
		farmConfigChangeReturn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
}
