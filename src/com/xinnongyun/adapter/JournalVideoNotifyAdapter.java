package com.xinnongyun.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xinnongyun.activity.R;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.imageload.ImageDownloadHelper;
import com.xinnongyun.imageload.ImageDownloadHelper.OnImageDownloadListener;
import com.xinnongyun.json.JsonUtil;
import com.xinnongyun.net.NetConnectionJournal;
import com.xinnongyun.net.NetConnectionJournal.JournalSuccessCallBack;
import com.xinnongyun.sqlite.JournalDaoDBManager;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.utils.StringUtil;
import com.xinnongyun.view.CircleImageView;

public class JournalVideoNotifyAdapter extends BaseAdapter {

	public List<HashMap<String, String>> listData;
	private Context contex;
	public JournalVideoNotifyAdapter(Context context,
			List<HashMap<String, String>> listData) {
		this.listData = listData;
		this.contex = context;
		if(this.listData == null)
		{
			this.listData = new ArrayList<HashMap<String,String>>();
		}
	}
	
	public void updateDate(List<HashMap<String, String>> updaData)
	{
		this.listData = updaData;
		if(this.listData == null)
		{
			this.listData = new ArrayList<HashMap<String,String>>();
		}
		notifyDataSetChanged();
	}
	
	public Handler renwuDataHandler = new Handler()
	{
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			updateDate((List<HashMap<String, String>>) msg.obj);
		}
	};
	

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(int position, View view, ViewGroup arg2) {
		if (view == null) {
			journalNotifyHolder = new JournalNotifyHolder();
			view = LayoutInflater.from(contex).inflate(
					R.layout.usercomments_list_item, null);
			journalNotifyHolder.tvContent = (TextView) view.findViewById(R.id.journalNotify_userComment);
			journalNotifyHolder.tvName = (TextView) view.findViewById(R.id.journalNotify_userName);
			journalNotifyHolder.tvTime = (TextView) view.findViewById(R.id.journalNotify_Time);
			journalNotifyHolder.userHead = (CircleImageView) view.findViewById(R.id.journalNotify_userHead);
			journalNotifyHolder.videoThumb = (ImageView) view.findViewById(R.id.journalNotify_videoThumb);
			view.setTag(journalNotifyHolder);
		}
		else
		{
			journalNotifyHolder = (JournalNotifyHolder) view.getTag();
		}
		HashMap<String, String> mapInfo = listData.get(position);
		if(mapInfo.get(TablesColumns.TABLENOTICE_SIGNAL).equals("like"))
		{
			Drawable drawable= contex.getResources().getDrawable(R.drawable.zan_hdpi);  
			/// 这一步必须要做,否则不会显示.  
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			journalNotifyHolder.tvContent.setCompoundDrawables(drawable, null, null, null);
			journalNotifyHolder.tvContent.setText("");
		}
		else
		{
			journalNotifyHolder.tvContent.setCompoundDrawables(null, null, null, null);
		}
		final JournalDaoDBManager journalDaoDBManager = new JournalDaoDBManager(contex);
		HashMap<String,String> journalMap = journalDaoDBManager.getJournalVideoInfoById(JsonUtil.getHashMap(mapInfo.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString());
		if(journalMap != null && journalMap.keySet().size()!=0)
		{
			journalNotifyHolder.videoThumb.setTag(journalMap.get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString());
			journalNotifyHolder.videoThumb.setImageResource(R.drawable.jiazailoading);
			ImageDownloadHelper.getInstance().imageDownload(null,contex, journalMap.get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(), journalNotifyHolder.videoThumb,"/001yunhekeji100",
					new OnImageDownloadListener() {
						public void onImageDownload(Bitmap bitmap, String imgUrl) {
							if (bitmap != null) {
								if(imgUrl.equals(journalNotifyHolder.videoThumb.getTag().toString()))
								{
									journalNotifyHolder.videoThumb.setImageBitmap(bitmap);
								}
							} 
							else
							{
								journalNotifyHolder.videoThumb.setImageResource(R.drawable.jiazailoading);
							}
						}
					});
			
			
			if(mapInfo.get(TablesColumns.TABLENOTICE_SIGNAL).equals("like"))
			{
				journalNotifyHolder.tvContent.setText("");
				List<HashMap<String, Object>> commentsData = JsonUtil.getListHashMapFromJsonArrayString(journalMap.get(TablesColumns.TABLEJOURNAL_LIKES).toString());
				int commentsLength = commentsData.size()-1;
				HashMap<String, Object> commentMap;
				for(int i=commentsLength ; i>=0 ; i--)
				{
					commentMap = commentsData.get(i);
					journalNotifyHolder.tvTime.setText(StringUtil.getTimeFormatInfo(commentMap.get("created_at").toString()));
					HashMap<String, Object> userInfoMap = JsonUtil.getHashMap(commentMap.get("user").toString());
					if(userInfoMap.get("id").toString().equals(mapInfo.get("operator")))
					{
						HashMap<String, Object> userInfoProMap = JsonUtil.getHashMap(userInfoMap.get("profile").toString());
						if(userInfoProMap.get("nickname").toString()!=null && !userInfoProMap.get("nickname").toString().equals("null"))
						{
							journalNotifyHolder.tvName.setText(userInfoProMap.get("nickname").toString());
						}
						else
						{
							journalNotifyHolder.tvName.setText(R.string.farming_being_name_default);
						}
						
						
						journalNotifyHolder.userHead.setImageResource(R.drawable.touxiang);
						if(userInfoProMap.get("avatar").toString()!=null && !userInfoProMap.get("avatar").toString().equals("null"))
						{
							journalNotifyHolder.userHead.setTag(userInfoProMap.get("avatar").toString());
							ImageDownloadHelper.getInstance().imageDownload(null,contex, userInfoProMap.get("avatar").toString(), journalNotifyHolder.userHead,"/001yunhekeji100",
									new OnImageDownloadListener() {
										public void onImageDownload(Bitmap bitmap, String imgUrl) {
											if (bitmap != null) {
												if(imgUrl.equals(journalNotifyHolder.userHead.getTag().toString()))
												{
													journalNotifyHolder.userHead.setImageBitmap(bitmap);
												}
											} 
										}
									});
						}
						break;
					}
				}
			
				
			}
			else
			{
				if(JsonUtil.getHashMap(mapInfo.get("context").toString()).keySet().contains("comment_id"))
				{
					String commentId = JsonUtil.getHashMap(mapInfo.get("context").toString()).get("comment_id").toString();
					List<HashMap<String, Object>> commentsData = JsonUtil.getListHashMapFromJsonArrayString(journalMap.get(TablesColumns.TABLEJOURNAL_COMMENTS).toString());
					int commentsLength = commentsData.size()-1;
					HashMap<String, Object> commentMap;
					for(int i=commentsLength ; i>=0 ; i--)
					{
						commentMap = commentsData.get(i);
						if(commentMap.get("id").toString().equals(commentId))
						{
							journalNotifyHolder.tvContent.setText(commentMap.get("content").toString());
							journalNotifyHolder.tvTime.setText(StringUtil.getTimeFormatInfo(commentMap.get("created_at").toString()));
							HashMap<String, Object> userInfoMap = JsonUtil.getHashMap(commentMap.get("user").toString());
							HashMap<String, Object> userInfoProMap = JsonUtil.getHashMap(userInfoMap.get("profile").toString());
							if(userInfoProMap.get("nickname").toString()!=null && !userInfoProMap.get("nickname").toString().equals("null"))
							{
								journalNotifyHolder.tvName.setText(userInfoProMap.get("nickname").toString());
							}
							else
							{
								journalNotifyHolder.tvName.setText(R.string.farming_being_name_default);
							}
							
							
							journalNotifyHolder.userHead.setImageResource(R.drawable.touxiang);
							if(userInfoProMap.get("avatar").toString()!=null && !userInfoProMap.get("avatar").toString().equals("null"))
							{
								journalNotifyHolder.userHead.setTag(userInfoProMap.get("avatar").toString());
								ImageDownloadHelper.getInstance().imageDownload(null,contex, userInfoProMap.get("avatar").toString(), journalNotifyHolder.userHead,"/001yunhekeji100",
										new OnImageDownloadListener() {
											public void onImageDownload(Bitmap bitmap, String imgUrl) {
												if (bitmap != null) {
													if(imgUrl.equals(journalNotifyHolder.userHead.getTag().toString()))
													{
														journalNotifyHolder.userHead.setImageBitmap(bitmap);
													}
												} 
											}
										});
							}
							break;
						}
					}
				}
				else
				{
					List<HashMap<String, Object>> commentsData = JsonUtil.getListHashMapFromJsonArrayString(journalMap.get(TablesColumns.TABLEJOURNAL_COMMENTS).toString());
					int commentsLength = commentsData.size()-1;
					HashMap<String, Object> commentMap;
					for(int i=commentsLength ; i>=0 ; i--)
					{
						commentMap = commentsData.get(i);
						
						journalNotifyHolder.tvContent.setText(commentMap.get("content").toString());
						journalNotifyHolder.tvTime.setText(StringUtil.getTimeFormatInfo(commentMap.get("created_at").toString()));
						HashMap<String, Object> userInfoMap = JsonUtil.getHashMap(commentMap.get("user").toString());
						if(userInfoMap.get("id").toString().equals(mapInfo.get("operator")))
						{
							HashMap<String, Object> userInfoProMap = JsonUtil.getHashMap(userInfoMap.get("profile").toString());
							if(userInfoProMap.get("nickname").toString()!=null && !userInfoProMap.get("nickname").toString().equals("null"))
							{
								journalNotifyHolder.tvName.setText(userInfoProMap.get("nickname").toString());
							}
							else
							{
								journalNotifyHolder.tvName.setText(R.string.farming_being_name_default);
							}
							
							
							journalNotifyHolder.userHead.setImageResource(R.drawable.touxiang);
							if(userInfoProMap.get("avatar").toString()!=null && !userInfoProMap.get("avatar").toString().equals("null"))
							{
								journalNotifyHolder.userHead.setTag(userInfoProMap.get("avatar").toString());
								ImageDownloadHelper.getInstance().imageDownload(null,contex, userInfoProMap.get("avatar").toString(), journalNotifyHolder.userHead,"/001yunhekeji100",
										new OnImageDownloadListener() {
											public void onImageDownload(Bitmap bitmap, String imgUrl) {
												if (bitmap != null) {
													if(imgUrl.equals(journalNotifyHolder.userHead.getTag().toString()))
													{
														journalNotifyHolder.userHead.setImageBitmap(bitmap);
													}
												} 
											}
										});
							}
							break;
						}
					}
				}
				
			}
		}
		else
		{
			NetConnectionJournal.JournalGetOneNetConnection(NetUrls.get_URL_JOURNALVIDEO_BYID(JsonUtil.getHashMap(mapInfo.get("context").toString()).get(TablesColumns.TABLEJOURNAL_ID).toString()),
					null, null,
					new JournalSuccessCallBack() {
						@Override
						public void onSuccess(HashMap<String, Object> result) {
							if(result!=null && !result.keySet().contains("status_code"))
							{
								journalDaoDBManager.insertUploadVideo(result);
							}
						}
					});
		}
		return view;

	}
	
	JournalNotifyHolder journalNotifyHolder = null;
	final static class JournalNotifyHolder {
		ImageView videoThumb;
		CircleImageView userHead;
		TextView tvName,tvContent,tvTime;
	}

}
