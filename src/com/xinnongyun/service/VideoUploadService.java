package com.xinnongyun.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.storage.persistent.FileRecorder;
import com.xinnongyun.activity.MainActivity;
import com.xinnongyun.activity.VideoRecordActivity;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.sqlite.VideoUploadDaoDBManager;
import com.xinnongyun.utils.StringUtil;

/**
 * 视频上传
 * 
 * @author sm
 * 
 */
public class VideoUploadService extends Service {
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		videoListUpload();
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private void videoListUpload()
	{
		if(!MainActivity.isWifiState)
		{
			System.out.println("qqqqqqqqqqqqqqqqqq");
			stopSelf();
			return;
		}
		VideoUploadDaoDBManager daoDBManager = new VideoUploadDaoDBManager(VideoUploadService.this);
		List<HashMap<String, String>> videoListMap = daoDBManager.getAllVideoUploadList();
		if(videoListMap!=null && videoListMap.size()>0)
		{
			int count = 0;
			for(HashMap<String, String> videoMap : videoListMap)
			{
				System.out.println("wwwwwwwwwwwwwww");
				if(videoMap.get(TablesColumns.TABLEVIDEOUPLOAD_STATE).equals("1"))
				{
					sendFileToQiNiu(videoMap.get(TablesColumns.TABLEVIDEOUPLOAD_QNTOKEN),videoMap.get(TablesColumns.TABLEVIDEOUPLOAD_FILENAME));
					break;
				}
				else if(videoMap.get(TablesColumns.TABLEVIDEOUPLOAD_STATE).equals("0"))
				{
					sendFileToQiNiu(videoMap.get(TablesColumns.TABLEVIDEOUPLOAD_QNTOKEN),videoMap.get(TablesColumns.TABLEVIDEOUPLOAD_FILENAME));
	            	VideoUploadDaoDBManager videoDbManager = new VideoUploadDaoDBManager(VideoUploadService.this);
					videoDbManager.changeUpLoadVideoState(videoMap.get(TablesColumns.TABLEVIDEOUPLOAD_QNTOKEN), "1");
					break;
				}else
				{
					count++;
				}
			}
	
			if(count == videoListMap.size())
			{
				if(!VideoRecordActivity.recordingClose)
				{
					if(MainActivity.isWifiState)
					{
						delete(new File(YunTongXun.VideoPath));
					}
				}
				stopSelf();
			}
		}
		else
		{
			stopSelf();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * 获取视频七牛token，上传视频
	 */
	private void sendFileToQiNiu(final String qiNiuToken,final String videoPath)
	{
        new Thread(
        		new Runnable() {
                    public void run() {
                    	UploadManager uploadManager;
                    	try {
                    		File reFile = new File(videoPath);
                    		if(!reFile.exists())
                    		{
                    			//文件不存在，删除记录
                    			VideoUploadDaoDBManager videoDbManager = new VideoUploadDaoDBManager(VideoUploadService.this);
        						videoDbManager.delleteUpLoadVideoItentByToken(qiNiuToken);
                    		}
							FileRecorder fr = new FileRecorder(reFile.getParent());
							uploadManager = new UploadManager(fr);
						} catch (IOException e) {
							//文件异常
                			VideoUploadDaoDBManager videoDbManager = new VideoUploadDaoDBManager(VideoUploadService.this);
    						videoDbManager.delleteUpLoadVideoItentByToken(qiNiuToken);
							return;
						}
                    					
                    	if(!MainActivity.isWifiState)
        				{
        					return;
        				}
                	    File f = new File(videoPath);
                	    
                    	uploadManager.put(f,
                    			"media/video_hd/" + StringUtil.getFileNameFromUrl(videoPath),
                    			qiNiuToken, 
                    			new UpCompletionHandler() {
									@Override
									public void complete(String arg0,ResponseInfo resInfo,JSONObject json) {
										try {
											if(json!=null && null != json.get("error"))
											{
												//文token异常
												VideoUploadDaoDBManager videoDbManager = new VideoUploadDaoDBManager(VideoUploadService.this);
												videoDbManager.delleteUpLoadVideoItentByToken(qiNiuToken);
												videoListUpload();
												return;
											}
										} catch (JSONException e) {
										}
										if(resInfo.isOK())
										{
											VideoUploadDaoDBManager videoDbManager = new VideoUploadDaoDBManager(VideoUploadService.this);
											videoDbManager.changeUpLoadVideoState(qiNiuToken, "2");
											videoListUpload();
										}
										else
										{
											if(!MainActivity.isWifiState)
											{
												stopSelf();
												return;
											}
											else
											{
												videoListUpload();
											}
										}
									}
								}
                    			,
                    			new UploadOptions(null, null, false, 
                    					new UpProgressHandler() {
											@Override
											public void progress(String key, double percent) {
												//更新上传进度
												if(!MainActivity.isWifiState)
												{
													stopSelf();
													return;
												}
												VideoUploadDaoDBManager videoDbManager = new VideoUploadDaoDBManager(VideoUploadService.this);
												videoDbManager.changeUpLoadVideoPercent(qiNiuToken, percent+"");		
											}
										}
                    					, new UpCancellationSignal() {
											@Override
											public boolean isCancelled() {
												return !MainActivity.isWifiState;
											}
										})
                    			
                    			);
                    }
                }
        		).start();
	}
	
	
	public static void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
		}
	}
	
}
