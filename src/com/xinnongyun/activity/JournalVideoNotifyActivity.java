package com.xinnongyun.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.xinnongyun.adapter.JournalVideoNotifyAdapter;
import com.xinnongyun.json.JsonUtil;
import com.xinnongyun.net.NetConnectionNotice;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.JournalDaoDBManager;
import com.xinnongyun.sqlite.NoticeDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;

/**
 * 视频消息列表
 * @author sm
 *
 */
public class JournalVideoNotifyActivity extends Activity {

	private ListView journal_videonnotify_list;
	private ImageView journalVideoReturn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_journalvideonotify);
		
		journal_videonnotify_list = (ListView) findViewById(R.id.journal_videonnotify_list);
		
		List<HashMap<String, String>> datas = new ArrayList<HashMap<String,String>>();
		videoNotifyAdapter = new JournalVideoNotifyAdapter(this, datas);
		journal_videonnotify_list.setAdapter(videoNotifyAdapter);
		updateJournalNotifyList();
		journal_videonnotify_list.setOnItemClickListener(
				new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						final JournalDaoDBManager journalDaoDBManager = new JournalDaoDBManager(JournalVideoNotifyActivity.this);
						String journalId = JsonUtil.getHashMap(videoNotifyAdapter.listData.get(arg2).get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString();
						HashMap<String,String> journalMap = journalDaoDBManager.getJournalVideoInfoById(journalId);
						if(journalMap!=null && journalMap.keySet().size()>0)
						{
							Intent intent = new Intent();
							System.out.println(journalId);
							intent.putExtra("journalvideoid", journalId);
							intent.setClass(JournalVideoNotifyActivity.this, JournalVideoNotifyPlayActivity.class);
							startActivity(intent);
						}
						else
						{
							Toast.makeText(JournalVideoNotifyActivity.this, "该消息已不存在", Toast.LENGTH_SHORT).show();
						}
					}
				}
				);
		journalVideoReturn = (ImageView) findViewById(R.id.journalVideoReturn);
		journalVideoReturn.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						finish();
					}
				}
				);
		
		//通知列表
		IntentFilter filterNoticeConfig = new IntentFilter();  
		filterNoticeConfig.addAction(MainActivity.noticeInfobroastCast); 
		registerReceiver(noticeInfoChange, filterNoticeConfig);
	}
	
	public void updateJournalNotifyList()
	{
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = videoNotifyAdapter.renwuDataHandler.obtainMessage();
				NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(JournalVideoNotifyActivity.this);
				List<HashMap<String, String>> datas = noticeDaoDBManager.getAllNotifyNoRead();
				List<HashMap<String, String>> listmap = datas;
				if(listmap!=null && listmap.size() > 0)
				{
					msg.obj = listmap;
					videoNotifyAdapter.renwuDataHandler.sendMessage(msg);
				}
				else
				{
					msg.obj = new ArrayList<HashMap<String,String>>();
					videoNotifyAdapter.renwuDataHandler.sendMessage(msg);
				}
			}
		}).start();
	}
	
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(noticeInfoChange);
		super.onDestroy();
	}
	
	@Override
	public void finish() {
		NoticeDaoDBManager noticeDaoDBManager = new NoticeDaoDBManager(this);
		List<HashMap<String, String>> noreadMaps = noticeDaoDBManager.getAllNotifyNoRead();
		if(noreadMaps.size()>0)
		{
			String para = "";
			int i=0,c = noreadMaps.size();
			for(HashMap<String, String> str : noreadMaps)
			{
				i++;
				if(i == c)
				{
					para += str.get("id");
				}
				else
				{
					para += str.get("id") +",";
				}
				
			}
			NetConnectionNotice.NoticePutNetConnection(
					"http://www.nnong.com/api-notice/read/"
					,"notice=" + para
					,"Token " + MySharePreference.getValueFromKey(JournalVideoNotifyActivity.this,MySharePreference.ACCOUNTTOKEN), null);
		}
		noticeDaoDBManager.changeNotifyAlreadyRead();
		super.finish();
	}
	
	
	//添加成功通知记录
    BroadcastReceiver noticeInfoChange = new  BroadcastReceiver(){
    	@Override
        public void onReceive(Context context, Intent intent) {
    		if(intent.getAction().equals(MainActivity.noticeInfobroastCast))
    		{
    			updateJournalNotifyList();
    		}
        }
    };
	private JournalVideoNotifyAdapter videoNotifyAdapter;
}
