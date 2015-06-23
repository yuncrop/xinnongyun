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

import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.net.NetConnectionJournal;
import com.xinnongyun.net.NetConnectionJournal.JournalSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.sqlite.VideoUploadDaoDBManager;
import com.xinnongyun.utils.DialogManager;
import com.xinnongyun.utils.ImageUtils;
import com.xinnongyun.utils.StringUtil;

/**
 * 视频上传
 * @author sm
 *
 */
public class VideoSendActivity extends Activity implements OnClickListener{

	private ImageView video_cutiv,returnPreview;
	private String videoPath = "";
	private String location = "";
	private String videotime = "";
	private String videolatlng;
	/*
	 * 视频时间段 格式：2014-03-02T14:35:01/2014-03-02T14:37:32,2014-03-02T14:42:00/2014-03-02T14:55:13
	 */
	private String segments = "";
	private TextView video_time,video_address,send_tv;
	private EditText video_description;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_send);
		Intent intent = getIntent();
		if(intent!=null)
		{
			videotime = intent.getStringExtra("videotime");
			videoPath = intent.getStringExtra("videoPath");
			location = intent.getStringExtra("location");
			segments = intent.getStringExtra("segments");
			videolatlng = intent.getStringExtra("videolatlng");
		}
		initialView();
	}

	
	
	
	private Dialog dg;
	/*
	 * 新建视频日志
	 */
	private void createVideoData()
	{
		dg = MainFragment.createLoadingDialog(VideoSendActivity.this);
		dg.show();
		send_tv.setClickable(false);
		String para = "farm="+ MySharePreference.getValueFromKey(this, MySharePreference.FARMID)+"&";
		para += "segments=" + segments.replaceAll("年", "-").replaceAll("月", "-").replaceAll("日", "")+"&";
		para += "location=" + location + "&";
		para += videolatlng + "&";
		if(StringUtil.checkEditTextIsEmpty(video_description))
		{
			para += "content=视频无描述&";
		}
		else
		{
			para += "content="+ video_description.getText().toString().trim() +"&";
		}
		para += "key=" + StringUtil.getFileNameFromUrl(videoPath);
		 NetConnectionJournal.PostNetConnection(
				 NetUrls.URL_VIDEO_CREATE,
				 para,
				 "Token "+ MySharePreference.getValueFromKey(this,MySharePreference.ACCOUNTTOKEN),
				 new JournalSuccessCallBack() {
					@Override
					public void onSuccess(HashMap<String, Object> result) {
						send_tv.setClickable(true);
						dg.dismiss();
						if(result!=null && result.keySet().contains("qiniutoken"))
						{
							VideoUploadDaoDBManager uploadDaoDBManager = new VideoUploadDaoDBManager(VideoSendActivity.this);
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put(TablesColumns.TABLEVIDEOUPLOAD_QNTOKEN, result.get("qiniutoken").toString());
							map.put(TablesColumns.TABLEVIDEOUPLOAD_FARM, MySharePreference.getValueFromKey(VideoSendActivity.this, MySharePreference.FARMID));
							map.put(TablesColumns.TABLEVIDEOUPLOAD_FILENAME, videoPath);
							map.put(TablesColumns.TABLEVIDEOUPLOAD_PERCENT, "0");
							map.put(TablesColumns.TABLEVIDEOUPLOAD_STATE, "0");
							map.put(TablesColumns.TABLEVIDEOUPLOAD_CREATETIME, StringUtil.dataChangeToS(StringUtil.getCurrentDataSALL()));
							if(uploadDaoDBManager.insertUploadVideo(map))
							{
								//开启服务开始上传数据
								MainActivity.checkHasVideoUpload(VideoSendActivity.this);
								setResult(10);
								finish();
							}
							else
							{
								DialogManager.showDialogSimple(VideoSendActivity.this, R.string.video_upload_para_failed);
							}
						}else
						{
							if(result!=null && result.keySet().contains("status_code"))
							{
								if(result.get("status_code").toString().contains("4"))
								{
									DialogManager.showDialogSimple(VideoSendActivity.this, R.string.video_upload_para_failed);
									return;
								}
							}
							DialogManager.showDialogSimple(VideoSendActivity.this, R.string.interneterror);
							//DialogManager.showDialogSimple(VideoSendActivity.this, R.string.video_upload_failed);
						}
					}
				});
		
	}
	
	//初始化控件
	private void initialView()
	{
		returnPreview = (ImageView) findViewById(R.id.returnPreview);
		returnPreview.setOnClickListener(this);
		video_time = (TextView) findViewById(R.id.video_time);
		video_time.setText(videotime);
		video_address = (TextView) findViewById(R.id.video_address);
		video_address.setText(location);
		send_tv = (TextView) findViewById(R.id.send_tv);
		send_tv.setOnClickListener(this);
		video_description = (EditText) findViewById(R.id.video_description);
		video_cutiv = (ImageView) findViewById(R.id.video_cutiv);
		video_cutiv.setImageBitmap(ImageUtils.getVideoThumbnail(videoPath, 60, 60));
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.returnPreview:
			finish();
			break;
		case R.id.send_tv:
			createVideoData();
			break;
		default:
			break;
		}
	}
	

	
	
}
