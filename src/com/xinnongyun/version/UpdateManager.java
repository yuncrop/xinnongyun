package com.xinnongyun.version;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.xinnongyun.activity.ErrorMarkToast;
import com.xinnongyun.activity.R;
import com.xinnongyun.json.JsonUtil;

/*
 * 先获取本地版本，设置要更新的文件名   和  文件下载地址
 */
public class UpdateManager {

	//侧滑菜单版本更新提示
	public static TextView vetsionTishi;
	private static Context currentContext;
	private static ProgressBar mProgress;
	private static boolean cancelUpdate = false;
	/* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	/* 下载保存路径 */
	private static String mSavePath;
	/* 记录进度条数量 */
	private static int progress;
	//下载到本地的路径    新版本路径
	private static String versions[] = new String[2];

	private static Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 正在下载
			case DOWNLOAD:
				// 设置进度条位置
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// 安装文件
				installApk(currentContext);
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 得到当前项目版本
	 * @param context
	 * @return
	 */
	public static int getCurrentVersion(Context context) {
		int versionCode = 1;
		try {
			versionCode = context.getPackageManager().getPackageInfo(
					"com.xinnongyun.activity", 0).versionCode;
		} catch (Exception e) {
			return versionCode;
		}
		return versionCode;
	}



	/**
	 * 版本更新对话框
	 * @param context
	 * @param version 更新的文件名 和 文件下载地址
	 */
	public static void showUpdaDialog(final Context context,String[] version) {
		versions = version;
		
		final AlertDialog dlg = new AlertDialog.Builder(context).create();
		dlg.setCancelable(false);
		dlg.show();
		Window window = dlg.getWindow();
		window.setContentView(R.layout.tishi_show_dialog_twobtn);
		((TextView) window.findViewById(R.id.dialog_content))
				.setText(R.string.version_update_isupdate);
		Button ok = (Button) window.findViewById(R.id.dialog_btn_submit);
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dlg.dismiss();
				showDownloadDialog(context);
			}
		});
		Button cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
	}

	/**
	 * 显示软件下载对话框
	 */
	private static void showDownloadDialog(Context mContext) {
		currentContext = mContext;
		// 构造软件下载对话框
		final AlertDialog dlg = new AlertDialog.Builder(mContext).create();
		dlg.setCancelable(false);
		dlg.show();
		Window window = dlg.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
		lp.width = (int) (d.widthPixels * 0.8);
		window.setAttributes(lp);
		window.setContentView(R.layout.softupdate_progress);
		((TextView) window.findViewById(R.id.dialog_head)).setText(R.string.version_update_updating);
		mProgress = (ProgressBar) window.findViewById(R.id.update_progress);
		Button cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dlg.dismiss();
				// 设置取消状态
				cancelUpdate = true;
				try {
					cancelAndDeleteApk();
				} catch (Exception e) {
					cancelAndDeleteApk();
				}
			}
		});
		// 现在文件
		downloadApk();
	}

	/**
	 * 下载apk文件
	 */
	private static void downloadApk() {
		// 启动新线程下载软件
		new downloadApkThread().start();
	}

	/**
	 * 取消下载  删除下载文件
	 */
	private static void cancelAndDeleteApk() {
		new DeleteApkThread().start();
	}

	private static class DeleteApkThread extends Thread {
		@Override
		public void run() {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				// 获得存储卡的路径
				String sdpath = Environment.getExternalStorageDirectory() + "/";
				mSavePath = sdpath + "download" + "/" + versions[0];
				File file = new File(mSavePath);
				// 判断文件目录是否存在
				if (!file.exists()) {
					return;
				}
				file.delete();
			}
		}
	}

	/**
	 * 下载文件线程
	 * 
	 */
	private static class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory()
							+ "/";
					mSavePath = sdpath + "download";
					URL url = new URL(versions[1]);
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入流
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(mSavePath, versions[0]);
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do {
						int numread = is.read(buf);
						count += numread;
						// 计算进度条位置
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// 点击取消就停止下载.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				 String jsonStr = "{status_code:"+ErrorMarkToast.urlError+"}";
				 Looper.prepare();
				 ErrorMarkToast.showCallBackToast(currentContext, JsonUtil.getHashMap(jsonStr));
				 Looper.loop();
				 cancelAndDeleteApk();
			} catch (IOException e) {
				 String jsonStr = "{status_code:"+2000+"}";
				 Looper.prepare();
				 ErrorMarkToast.showCallBackToast(currentContext, JsonUtil.getHashMap(jsonStr));
				 Looper.loop();
				 cancelAndDeleteApk();
			}
			// 取消下载对话框显示
		}
	};

	/**
	 * 安装APK文件
	 */
	private static void installApk(Context context) {
		File apkfile = new File(mSavePath, versions[0]);
		if (!apkfile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		context.startActivity(i);
		vetsionTishi.setVisibility(View.INVISIBLE);
	}

}
