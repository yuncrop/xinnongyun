package com.xinnongyun.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.xinnongyun.activity.R;
import com.xinnongyun.imageload.ImageDownloadHelper;
import com.xinnongyun.imageload.ImageDownloadHelper.OnImageDownloadListener;
import com.xinnongyun.json.JsonUtil;
import com.xinnongyun.view.CircleImageView;

/**
 * 评论列表适配
 * @author sm
 *
 */
public class ZanGridViewAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private List<HashMap<String, Object>> datas;
	/**
	 * @param context
	 * @param data
	 */
	public ZanGridViewAdapter(Context context,List<HashMap<String, Object>> data) {
		this.datas = data;
		mInflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int arg0) {
		return datas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		convertView = mInflater.inflate(R.layout.zanheadlist_item, null);
		HashMap<String, Object> userInfoMap = JsonUtil.getHashMap(datas.get(position).get("user").toString());
		HashMap<String, Object> userInfoProMap = JsonUtil.getHashMap(userInfoMap.get("profile").toString());
		final CircleImageView zanHead = (CircleImageView) convertView.findViewById(R.id.zan_listitem_head_iv);
		zanHead.setImageResource(R.drawable.touxiang);
		if(userInfoProMap.get("avatar").toString()!=null && !userInfoProMap.get("avatar").toString().equals("null"))
		{
			zanHead.setTag(userInfoProMap.get("avatar").toString());
			ImageDownloadHelper.getInstance().imageDownload(null,context, userInfoProMap.get("avatar").toString(), zanHead,"/001yunhekeji100",
					new OnImageDownloadListener() {
						public void onImageDownload(Bitmap bitmap, String imgUrl) {
							if (bitmap != null) {
								if(imgUrl.equals(zanHead.getTag().toString()))
								{
									zanHead.setImageBitmap(bitmap);
								}
							} 
						}
					});
		}
		
		
		return convertView;
	}

}
