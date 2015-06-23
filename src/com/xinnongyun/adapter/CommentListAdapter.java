package com.xinnongyun.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
public class CommentListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<HashMap<String, Object>> datas;
	/**
	 * @param context
	 * @param data
	 */
	public CommentListAdapter(Context context,List<HashMap<String, Object>> data) {
		this.datas = data;
		mInflater = LayoutInflater.from(context);
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
		convertView = mInflater.inflate(R.layout.pinglunlist_item, null);
		TextView pinglunName = (TextView) convertView.findViewById(R.id.pinglun_name);
		TextView pinglunContent = (TextView) convertView.findViewById(R.id.pinglun_content);
		final CircleImageView pinglun_head_iv = (CircleImageView) convertView.findViewById(R.id.pinglun_head_iv);
		HashMap<String, Object> userInfoMap = JsonUtil.getHashMap(datas.get(position).get("user").toString());
		
		pinglunContent.setText(datas.get(position).get("content").toString());
		
		HashMap<String, Object> userInfoProMap = JsonUtil.getHashMap(userInfoMap.get("profile").toString());
		if(userInfoProMap.get("nickname").toString()!=null && !userInfoProMap.get("nickname").toString().equals("null"))
		{
			pinglunName.setText(userInfoProMap.get("nickname").toString());
		}
		else
		{
			pinglunName.setText(R.string.farming_being_name_default);
		}
		pinglun_head_iv.setImageResource(R.drawable.head_40);
		if(userInfoProMap.get("avatar").toString()!=null && !userInfoProMap.get("avatar").toString().equals("http://static.nnong.com/media/user_avatar/default.png"))
		{
			pinglun_head_iv.setTag(userInfoProMap.get("avatar").toString());
			ImageDownloadHelper.getInstance().imageDownload(null,mInflater.getContext(), userInfoProMap.get("avatar").toString(), pinglun_head_iv,"/001yunhekeji100",
					new OnImageDownloadListener() {
						public void onImageDownload(Bitmap bitmap, String imgUrl) {
							if (bitmap != null) {
								if(imgUrl.equals(pinglun_head_iv.getTag().toString()))
								{
									pinglun_head_iv.setImageBitmap(bitmap);
								}
							} 
						}
					});
		}
		return convertView;
	}

}
