package com.xinnongyun.activity;

import java.text.SimpleDateFormat;

import com.xinnongyun.config.NetUrls;
import com.xinnongyun.net.NetConnectionNotice;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.NoticeDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.view.ProgressWebView;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

/**
 * 使用说明 帮助
 * 
 * @author sm
 * 
 */
public class AppUserHelpActivity extends Activity {

	private ProgressWebView webView;
	private String url = "";

	private TextView htmlurl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏ActionBar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		setContentView(R.layout.activity_appusehelp);
		Intent intent = getIntent();
		if (intent != null) {
			int notifyid = intent.getIntExtra("notify_id", 0);
			if (notifyid != 0) {
				NotificationManager manger = (NotificationManager) this
						.getSystemService(NOTIFICATION_SERVICE);
				manger.cancel(notifyid);
			}

			url = NetUrls.AppUseHelp;
			String notice_id = intent
					.getStringExtra(TablesColumns.TABLENOTICE_ID + "notice");
			if (notice_id != null && notice_id.length() > 0) {
				NoticeDaoDBManager daoDBManager = new NoticeDaoDBManager(
						AppUserHelpActivity.this);
				SimpleDateFormat sDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd hh:mm:ss");
				String date = sDateFormat.format(new java.util.Date());
				NetConnectionNotice.NoticePutNetConnection(
						NetUrls.getUpdateNotice(notice_id),
						"read_at=" + date.replace(" ", "T"),
						"Token "
								+ MySharePreference.getValueFromKey(
										AppUserHelpActivity.this,
										MySharePreference.ACCOUNTTOKEN), null);
				daoDBManager.changeFarmingBeingCollects(notice_id,
						date.replace(" ", "T"));

				if (intent.getStringExtra("url_app") != null) {
					url = intent.getStringExtra("url_app");
				}
			}
		} else {
			url = NetUrls.AppUseHelp;
		}

		intiWebView();
	}

	public void intiWebView() {
		htmlurl = (TextView) findViewById(R.id.htmlurl);
		webView = (ProgressWebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.requestFocus();
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setBuiltInZoomControls(false);
		webView.loadUrl(url);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				htmlurl.setText("页面正在加载中，请稍后...");
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				htmlurl.setText(url);

			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

		});
	}

	
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
		// webView.goBack();
		// return true;
		// }
		return super.onKeyDown(keyCode, event);
	}
}
