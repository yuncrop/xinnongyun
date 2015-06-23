package com.xinnongyun.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xinnongyun.activity.JournalVideoDetailActivity;
import com.xinnongyun.activity.R;
import com.xinnongyun.atyfragment.MainFragment;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.imageload.ImageDownloadHelper;
import com.xinnongyun.imageload.ImageDownloadHelper.OnImageDownloadListener;
import com.xinnongyun.json.JsonUtil;
import com.xinnongyun.net.NetConnectionCommentsZan;
import com.xinnongyun.net.NetConnectionCommentsZan.JournalComments_ZanSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.TablesColumns;
import com.xinnongyun.utils.InputTools;
import com.xinnongyun.utils.ShareUtils;
import com.xinnongyun.utils.StringUtil;
import com.xinnongyun.view.IconFontTextView;
import com.xinnongyun.view.MyGridView;

/**
 * 视频列表适配器
 * @author sm
 *
 */
public class JournalListAdapter extends BaseAdapter {

	private int widthLentgh = 0;
	private List<HashMap<String, Object>> listData;
	private Context contex;
	public JournalListAdapter(Context context,
			List<HashMap<String, Object>> listData,int length) {
		this.listData = listData;
		this.contex = context;
		this.widthLentgh = length;
		if(this.listData == null)
		{
			this.listData = new ArrayList<HashMap<String,Object>>();
		}
	}
	
	public void updateDate(List<HashMap<String, Object>> updaData)
	{
		this.listData = updaData;
		if(this.listData == null)
		{
			this.listData = new ArrayList<HashMap<String,Object>>();
		}
		notifyDataSetChanged();
	}
	
	public Handler journalDataHandler = new Handler()
	{
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			updateDate((List<HashMap<String, Object>>) msg.obj);
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

	public View getView(final int position, View view, ViewGroup arg2) {
		
		
		if (view == null) {
			journalUploadHolder = new ViewJopurnalHolder();
			view = LayoutInflater.from(contex).inflate(
					R.layout.main_fragment_video_item, null);
			journalUploadHolder.comments_zan_ll = (LinearLayout) view.findViewById(R.id.comments_zan_ll);
			journalUploadHolder.comments_zan_div = view.findViewById(R.id.commets_zan_div);
			journalUploadHolder.zanll = (LinearLayout) view.findViewById(R.id.zanll);
			journalUploadHolder.journalvideo_comments_list = (MyListView) view.findViewById(R.id.journalvideo_comments_list);
			journalUploadHolder.video_thumb_rl = (RelativeLayout) view.findViewById(R.id.video_thumb_rl);
			journalUploadHolder.video_thumb_rl.setLayoutParams(new LinearLayout.LayoutParams(widthLentgh,widthLentgh));
			journalUploadHolder.videoUserHead = (ImageView) view.findViewById(R.id.video_userHead);
			journalUploadHolder.videoUserName = (TextView) view.findViewById(R.id.video_userName);
			journalUploadHolder.videoTime = (TextView) view.findViewById(R.id.video_time);
			journalUploadHolder.videoContent = (TextView) view.findViewById(R.id.video_content);
			journalUploadHolder.videoEyeCount = (TextView) view.findViewById(R.id.video_eye_count);
			journalUploadHolder.videoLikeCount = (TextView) view.findViewById(R.id.zannum);
			journalUploadHolder.videoCommentCount = (TextView) view.findViewById(R.id.pinglunnum);
			journalUploadHolder.video_thumb_iv = (ImageView) view.findViewById(R.id.video_thumb_iv);
			journalUploadHolder.video_zan = (ImageView) view.findViewById(R.id.zan);
			journalUploadHolder.video_pinglun = (ImageView) view.findViewById(R.id.pinglun);
			journalUploadHolder.video_share = (ImageView) view.findViewById(R.id.share_weixin_weibo);
			journalUploadHolder.zan_list_head = (MyGridView) view.findViewById(R.id.zan_list_head);
			view.setTag(journalUploadHolder);
		}
		else
		{
			journalUploadHolder = (ViewJopurnalHolder) view.getTag();
		}
		
		journalUploadHolder.comments_zan_ll.setVisibility(View.VISIBLE);
		journalUploadHolder.zanll.setVisibility(View.VISIBLE);
		journalUploadHolder.comments_zan_div.setVisibility(View.VISIBLE);
		journalUploadHolder.journalvideo_comments_list.setVisibility(View.VISIBLE);
		if(listData.get(position).get(TablesColumns.TABLEJOURNAL_VIEWS).toString().length()>3)
		{
			List<HashMap<String, Object>> viewsData = JsonUtil.getListHashMapFromJsonArrayString(listData.get(position).get(TablesColumns.TABLEJOURNAL_VIEWS).toString());
			journalUploadHolder.videoEyeCount.setText(""+ viewsData.size());
		}
		else
		{
			journalUploadHolder.videoEyeCount.setText("0");
		}
		if(listData.get(position).get(TablesColumns.TABLEJOURNAL_LIKES).toString().length()<3 )
		{
			if(listData.get(position).get(TablesColumns.TABLEJOURNAL_COMMENTS).toString().length()<3)
			{
				journalUploadHolder.videoCommentCount.setText("0");
				journalUploadHolder.videoLikeCount.setText("0");
				journalUploadHolder.comments_zan_ll.setVisibility(View.GONE);
			}else
			{
				journalUploadHolder.videoLikeCount.setText("0");
				journalUploadHolder.zanll.setVisibility(View.GONE);
				journalUploadHolder.comments_zan_div.setVisibility(View.GONE);
				List<HashMap<String, Object>> commentsData = JsonUtil.getListHashMapFromJsonArrayString(listData.get(position).get(TablesColumns.TABLEJOURNAL_COMMENTS).toString());
				journalUploadHolder.videoCommentCount.setText("" + commentsData.size());
				CommentListAdapter commentsAdapter = new CommentListAdapter(contex, commentsData);
				journalUploadHolder.journalvideo_comments_list.setAdapter(commentsAdapter);
			}
		}else
		{
			List<HashMap<String, Object>> likesMapData = JsonUtil.getListHashMapFromJsonArrayString(listData.get(position).get(TablesColumns.TABLEJOURNAL_LIKES).toString());
			journalUploadHolder.videoLikeCount.setText("" + likesMapData.size());
			journalUploadHolder.zan_list_head.setAdapter(new ZanGridViewAdapter(contex, likesMapData));
			
			if(listData.get(position).get(TablesColumns.TABLEJOURNAL_COMMENTS).toString().length()<3)
			{
				journalUploadHolder.videoCommentCount.setText("0");
				journalUploadHolder.comments_zan_div.setVisibility(View.GONE);
				journalUploadHolder.journalvideo_comments_list.setVisibility(View.GONE);
			}else
			{
				List<HashMap<String, Object>> commentsData = JsonUtil.getListHashMapFromJsonArrayString(listData.get(position).get(TablesColumns.TABLEJOURNAL_COMMENTS).toString());
				journalUploadHolder.videoCommentCount.setText("" + commentsData.size());
				CommentListAdapter commentsAdapter = new CommentListAdapter(contex, commentsData);
				journalUploadHolder.journalvideo_comments_list.setAdapter(commentsAdapter);
				
			}
		}
		journalUploadHolder.journalvideo_comments_list.setOnItemClickListener(
				new OnItemClickListener() {

					@SuppressWarnings("deprecation")
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						MainFragment.currentPinglunId = listData.get(position).get(TablesColumns.TABLEJOURNAL_ID).toString();
						View popContent = LayoutInflater.from(contex).inflate(R.layout.pinglun_dialog, null);
						PopupWindow popWindow = new PopupWindow(popContent,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
						popWindow.setBackgroundDrawable(new BitmapDrawable());
						popWindow.setFocusable(true);
						popWindow.setOutsideTouchable(true);
						popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
						popWindow.showAtLocation(journalUploadHolder.video_pinglun, Gravity.BOTTOM, 0, 0);
						initilPinglun(popContent,popWindow);
					}
				}
				);
		//控件自身无法计算自身大小需要自己设置
	   // setListViewHeightBasedOnChildren(journalUploadHolder.journalvideo_comments_list);
		journalUploadHolder.video_thumb_iv.setTag(listData.get(position).get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString());
		journalUploadHolder.video_thumb_iv.setImageResource(R.drawable.jiazailoading);
		ImageDownloadHelper.getInstance().imageDownload(null,contex, listData.get(position).get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(), journalUploadHolder.video_thumb_iv,"/001yunhekeji100",
				new OnImageDownloadListener() {
					public void onImageDownload(Bitmap bitmap, String imgUrl) {
						if (bitmap != null) {
							journalUploadHolder.video_thumb_iv.setImageBitmap(null);
							if(journalUploadHolder.video_thumb_iv.getTag()!=null && journalUploadHolder.video_thumb_iv.getTag().toString().equals(imgUrl))
							{
								journalUploadHolder.video_thumb_iv.setImageBitmap(bitmap);
							}
						}
						else
						{
							journalUploadHolder.video_thumb_iv.setImageResource(R.drawable.jiazailoading);
						}
					}
				});
		journalUploadHolder.video_thumb_iv.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent();
						intent.putExtra("journalVideoId", listData.get(position).get(TablesColumns.TABLEJOURNAL_ID).toString());
						intent.setClass(contex, JournalVideoDetailActivity.class);
						contex.startActivity(intent);
					}
				}
				);
		journalUploadHolder.videoTime.setText(StringUtil.getTimeFormatInfo(listData.get(position).get(TablesColumns.TABLEJOURNAL_CREATEAT).toString()));
		journalUploadHolder.videoContent.setText(listData.get(position).get(TablesColumns.TABLEJOURNAL_CONTENT).toString());
		HashMap<String, Object> userInfoMap = JsonUtil.getHashMap(listData.get(position).get(TablesColumns.TABLEJOURNAL_USER).toString());
		HashMap<String, Object> userInfoProMap = JsonUtil.getHashMap(userInfoMap.get("profile").toString());
		journalUploadHolder.videoUserHead.setImageResource(R.drawable.touxiang);
		if(userInfoProMap.get("avatar").toString()!=null && !userInfoProMap.get("avatar").toString().equals("null"))
		{
			journalUploadHolder.videoUserHead.setTag(userInfoProMap.get("avatar").toString());
			ImageDownloadHelper.getInstance().imageDownload(null,contex, userInfoProMap.get("avatar").toString(), journalUploadHolder.videoUserHead,"/001yunhekeji100",
					new OnImageDownloadListener() {
						public void onImageDownload(Bitmap bitmap, String imgUrl) {
							if (bitmap != null) {
								if(imgUrl.equals(journalUploadHolder.videoUserHead.getTag().toString()))
								{
									journalUploadHolder.videoUserHead.setImageBitmap(bitmap);
								}
							} 
						}
					});
		}
		if(userInfoProMap.get("nickname").toString()!=null && !userInfoProMap.get("nickname").toString().equals("null"))
		{
			journalUploadHolder.videoUserName.setText(userInfoProMap.get("nickname").toString());
		}
		else
		{
			journalUploadHolder.videoUserName.setText(R.string.farming_being_name_default);
		}
		
		journalUploadHolder.video_zan.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						
						if(listData.get(position).get(TablesColumns.TABLEJOURNAL_LIKES).toString().length()>3)
						{
							List<HashMap<String, Object>> likesMapData = JsonUtil.getListHashMapFromJsonArrayString(listData.get(position).get(TablesColumns.TABLEJOURNAL_LIKES).toString());
							for(HashMap<String, Object> likeMap : likesMapData)
							{
								HashMap<String, Object> userInfoMap = JsonUtil.getHashMap(likeMap.get("user").toString());
								
								if(userInfoMap.get("id").toString().equals(MySharePreference.getValueFromKey(contex, MySharePreference.ACCOUNTID)))
								{
									Toast.makeText(contex, R.string.video_already_zan, Toast.LENGTH_SHORT).show();
									return;
								}
							}
						}
						dg = MainFragment.createLoadingDialog(contex);
						dg.show();
						
						NetConnectionCommentsZan.PostNetConnection(
								NetUrls.getURL_JOURNALVIDEO_LIKE(listData.get(position).get(TablesColumns.TABLEJOURNAL_ID).toString()),
								"",
								"Token " + MySharePreference.getValueFromKey(contex,MySharePreference.ACCOUNTTOKEN),
								new JournalComments_ZanSuccessCallBack() {
									@Override
									public void onSuccess(HashMap<String, Object> result) {
										dg.dismiss();
										if(result!=null && result.get("status_code").toString().contains("20"))
										{
											//赞 成功
										}else
										{
											//赞 失败
											Toast.makeText(contex, R.string.dianzan_fail, Toast.LENGTH_SHORT).show();
										}
									}
								});
					}
				}
				);
		journalUploadHolder.video_pinglun.setOnClickListener(
				new OnClickListener() {
					@SuppressWarnings("deprecation")
					@Override
					public void onClick(View arg0) {
							MainFragment.currentPinglunId = listData.get(position).get(TablesColumns.TABLEJOURNAL_ID).toString();
							View popContent = LayoutInflater.from(contex).inflate(R.layout.pinglun_dialog, null);
							PopupWindow popWindow = new PopupWindow(popContent,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
							popWindow.setBackgroundDrawable(new BitmapDrawable());
							popWindow.setFocusable(true);
							popWindow.setOutsideTouchable(true);
							popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
							popWindow.showAtLocation(journalUploadHolder.video_pinglun, Gravity.BOTTOM, 0, 0);
							initilPinglun(popContent,popWindow);
					}
				});

		journalUploadHolder.video_share.setOnClickListener(
				new OnClickListener() {
					@SuppressWarnings("deprecation")
					@Override
					public void onClick(View arg0) {
						
						View popContent = LayoutInflater.from(contex).inflate(R.layout.sharepopwindow, null);
						PopupWindow popWindow = new PopupWindow(popContent,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
						((IconFontTextView)popContent.findViewById(R.id.shareWinXinF)).setText(String.valueOf(new char[]{0xe645}));
						((IconFontTextView)popContent.findViewById(R.id.shareWinXinFC)).setText(String.valueOf(new char[]{0xe659}));
						((IconFontTextView)popContent.findViewById(R.id.shareXinLang)).setText(String.valueOf(new char[]{0xe646}));
						((IconFontTextView)popContent.findViewById(R.id.shareQQF)).setText(String.valueOf(new char[]{0xe657}));
						((IconFontTextView)popContent.findViewById(R.id.shareQQZ)).setText(String.valueOf(new char[]{0xe658}));
						((IconFontTextView)popContent.findViewById(R.id.shareWinXinF)).setOnClickListener(
								new OnClickListener() {
									@Override
									public void onClick(View arg0) {
										ShareUtils.shareWXFriend(contex,listData.get(position).get(TablesColumns.TABLEJOURNAL_ID).toString(),
												listData.get(position).get(TablesColumns.TABLEJOURNAL_CONTENT).toString(),
												listData.get(position).get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(),null);
									}
								}
								);
						((IconFontTextView)popContent.findViewById(R.id.shareWinXinFC)).setOnClickListener(
								new OnClickListener() {
									@Override
									public void onClick(View arg0) {
										ShareUtils.shareXWFriedCircle(contex,listData.get(position).get(TablesColumns.TABLEJOURNAL_ID).toString(),
												listData.get(position).get(TablesColumns.TABLEJOURNAL_CONTENT).toString(),
												listData.get(position).get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(),null);
									}
								}
								);
						((IconFontTextView)popContent.findViewById(R.id.shareXinLang)).setOnClickListener(
								new OnClickListener() {
									@Override
									public void onClick(View arg0) {
										ShareUtils.shareXinLang(contex,listData.get(position).get(TablesColumns.TABLEJOURNAL_ID).toString(),
												listData.get(position).get(TablesColumns.TABLEJOURNAL_CONTENT).toString(),
												listData.get(position).get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(),null);
									}
								}
								);
						((IconFontTextView)popContent.findViewById(R.id.shareQQF)).setOnClickListener(
								new OnClickListener() {
									@Override
									public void onClick(View arg0) {
										ShareUtils.shareQQF(contex,listData.get(position).get(TablesColumns.TABLEJOURNAL_ID).toString(),
												listData.get(position).get(TablesColumns.TABLEJOURNAL_CONTENT).toString(),
												listData.get(position).get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(),null);
									}
								}
								);
						((IconFontTextView)popContent.findViewById(R.id.shareQQZ)).setOnClickListener(
								new OnClickListener() {
									@Override
									public void onClick(View arg0) {
										ShareUtils.shareQQZ(contex,listData.get(position).get(TablesColumns.TABLEJOURNAL_ID).toString(),
												listData.get(position).get(TablesColumns.TABLEJOURNAL_CONTENT).toString(),
												listData.get(position).get(TablesColumns.TABLEJOURNAL_THUMBNAIL).toString(),null);
									}
								}
								);
						popWindow.setBackgroundDrawable(new BitmapDrawable());
						popWindow.setFocusable(true);
						popWindow.setOutsideTouchable(true);
						popWindow.showAtLocation(journalUploadHolder.video_pinglun, Gravity.CENTER, 0, 0);
						
					}
				}
				);
		return view;

	}
	
	private Dialog dg;
	
	
	
	private void initilPinglun(View mainview,final PopupWindow popWindow)
	{
		final EditText pinglunContent;
		TextView pinglunSend;
		pinglunContent = (EditText) mainview.findViewById(R.id.pinglun_content);
		pinglunSend = (TextView) mainview.findViewById(R.id.pinglun_send);
		pinglunSend.setOnClickListener(
				new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						NetConnectionCommentsZan.PostNetConnection(
								NetUrls.getURL_JOURNALVIDEO_COMMENTS(MainFragment.currentPinglunId),
								"content=" + pinglunContent.getText().toString().trim(),
								"Token " + MySharePreference.getValueFromKey(contex,MySharePreference.ACCOUNTTOKEN),
								new JournalComments_ZanSuccessCallBack() {
									@Override
									public void onSuccess(HashMap<String, Object> result) {
										if(result!=null && result.keySet().contains("status_code")&& result.get("status_code").toString().contains("20"))
										{
											
										}
										else
										{
											Toast.makeText(contex, "评论失败", Toast.LENGTH_SHORT).show();
										}
									}
								});
						InputTools.HideKeyboard(pinglunContent);
						pinglunContent.setText("");
						popWindow.dismiss();
					}
				}
				);
	}
	
	
	
	public void setListViewHeightBasedOnChildren(ListView listView) {   
        // 获取ListView对应的Adapter   
        CommentListAdapter listAdapter = (CommentListAdapter) listView.getAdapter();   
        if (listAdapter == null) {
            return;   
        }   
   
        int totalHeight = 0;   
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {   
            // listAdapter.getCount()返回数据项的数目   
            View listItem = listAdapter.getView(i, null, listView);   
            // 计算子项View 的宽高   
            listItem.measure(0, 0);    
            // 统计所有子项的总高度   
            totalHeight += listItem.getMeasuredHeight();    
        }   
   
        ViewGroup.LayoutParams params = listView.getLayoutParams();   
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));   
        // listView.getDividerHeight()获取子项间分隔符占用的高度   
        // params.height最后得到整个ListView完整显示需要的高度   
        listView.setLayoutParams(params);
    }
	ViewJopurnalHolder journalUploadHolder = null;
	final static class ViewJopurnalHolder {
		private View comments_zan_div;
		private LinearLayout comments_zan_ll,zanll;
		private MyGridView zan_list_head;
		private RelativeLayout video_thumb_rl;
		private MyListView journalvideo_comments_list;
		private ImageView videoUserHead,video_thumb_iv,video_zan,video_pinglun,video_share;
		private TextView videoTime,videoContent,videoLikeCount,videoCommentCount,videoEyeCount,videoUserName;
	}
}
