/* 
 * @ProjectName VideoGoJar
 * @Copyright HangZhou Hikvision System Technology Co.,Ltd. All Right Reserved
 * 
 * @FileName CameraListAdapter.java
 * @Description 这里对文件进行描述
 * 
 * @author chenxingyf1
 * @data 2014-7-14
 * 
 * @note 这里写本文件的详细功能描述和注释
 * @note 历史记录
 * 
 * @warning 这里写本文件的相关警告
 */
package com.videogo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.videogo.constant.Constant;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EzvizAPI;
import com.videogo.openapi.bean.resp.CameraInfo;
import com.videogo.util.LogUtil;
import com.xinnongyun.activity.MainActivity;
import com.xinnongyun.activity.R;

/**
 * 摄像头列表适配器
 * @author chenxingyf1
 * @data 2014-7-14
 */
public class CameraListAdapter extends BaseAdapter {
    private static final String TAG = "CameraListAdapter";

    private Context mContext = null;
    private Handler mHandler = null;
    public List<CameraInfo> mCameraInfoList = null;
    /** 监听对象 */
    private OnClickListener mListener;
    private ImageLoader mImageLoader;
    private final ExecutorService mExecutorService;// 线程池
    public Map<String, CameraInfo> mExecuteItemMap = null;
    
    /**
     * 自定义控件集合
     * 
     * @author dengsh
     * @data 2012-6-25
     */
    public static class ViewHolder {
    	public RelativeLayout item_icon_area;
        public ImageView iconIv;
        public ImageView playBtn;
        public TextView cameraNameTv;
    }
    
    public CameraListAdapter(Context context) {
        mContext = context;
        mHandler = new Handler();
        mCameraInfoList = new ArrayList<CameraInfo>();
        mImageLoader = ImageLoader.getInstance();
        // 线程个数
        mExecutorService = Executors.newFixedThreadPool(Constant.CPU_NUMS * Constant.POOL_SIZE);
        mExecuteItemMap = new HashMap<String, CameraInfo>();
    }
    
    public void clearImageCache() {
        mImageLoader.clearMemoryCache();
    }
    
    public void setOnClickListener(OnClickListener l) {
        mListener = l;
    }
    
    public void addItem(CameraInfo item) {
        mCameraInfoList.add(item);
    }

    public void removeItem(CameraInfo item) {
        for(int i = 0; i < mCameraInfoList.size(); i++) {
            if(item == mCameraInfoList.get(i)) {
                mCameraInfoList.remove(i);
            }
        }
    }
    
    public void clearItem() {
        //mExecuteItemMap.clear();
        mCameraInfoList.clear();
    }
    
    /* (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return mCameraInfoList.size();
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public CameraInfo getItem(int position) {
        CameraInfo item = null;
        if (position >= 0 && getCount() > position) {
            item = mCameraInfoList.get(position);
        }
        return item;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @SuppressWarnings("deprecation")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 自定义视图
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();

            // 获取list_item布局文件的视图
            convertView = LayoutInflater.from(mContext).inflate(R.layout.cameralist_small_item, null);

            // 获取控件对象
            viewHolder.item_icon_area = (RelativeLayout) convertView.findViewById(R.id.item_icon_area);
            viewHolder.iconIv = (ImageView) convertView.findViewById(R.id.item_icon);
            viewHolder.iconIv.setDrawingCacheEnabled(false);
            viewHolder.iconIv.setWillNotCacheDrawing(true);
            viewHolder.playBtn = (ImageView) convertView.findViewById(R.id.item_play_btn);
            RelativeLayout.LayoutParams recordlp = new android.widget.RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,((MainActivity)mContext).getWindowManager().getDefaultDisplay().getWidth()/4*3);
            viewHolder.item_icon_area.setLayoutParams(recordlp);
            viewHolder.cameraNameTv = (TextView) convertView.findViewById(R.id.camera_name_tv);
            // 设置点击图标的监听响应函数
            viewHolder.playBtn.setOnClickListener(mOnClickListener);
            viewHolder.iconIv.setOnClickListener(mOnClickListener);
            // 设置控件集到convertView
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        // 设置position
        viewHolder.playBtn.setTag(position);
        viewHolder.iconIv.setTag(position);
        
        CameraInfo cameraInfo = getItem(position);
        if(cameraInfo != null) {
            viewHolder.cameraNameTv.setText(cameraInfo.getCameraName());   
            //viewHolder.iconIv.setVisibility(View.INVISIBLE);
            
            String snapshotPath = EzvizAPI.getInstance().getSnapshotPath(cameraInfo.getCameraId());
            File snapshotFile = new File(snapshotPath);
            if(snapshotFile.exists()) {
                snapshotPath = "file://" + snapshotPath;
            } else {
                snapshotPath = cameraInfo.getPicUrl();
            }
            if(!TextUtils.isEmpty(snapshotPath)) {
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中  
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .build();//构建完成                              
                // 依次从内存和sd中获取，如果没有则网络下载
                mImageLoader.displayImage(snapshotPath, viewHolder.iconIv, options, mImgLoadingListener);
            }  
            getCameraSnapshotTask(cameraInfo);
        }
        
        return convertView;
    }
    
    private final ImageLoadingListener mImgLoadingListener = new ImageLoadingListener() {

        @Override
        public void onLoadingStarted(String imageUri, View view) {
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (view != null && view instanceof ImageView && loadedImage != null) {
                ImageView imgView = (ImageView) view;
                imgView.setImageBitmap(loadedImage);
                imgView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {

        }
    };
    
    private void getCameraSnapshotTask(final CameraInfo cameraInfo) {
        synchronized (mExecuteItemMap) {
            if (mExecuteItemMap.containsKey(cameraInfo.getCameraId())) {
                return;
            }
            mExecuteItemMap.put(cameraInfo.getCameraId(), cameraInfo);
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String snapshotPath = EzvizAPI.getInstance().getCameraSnapshot(cameraInfo.getCameraId());
                    LogUtil.infoLog(TAG, "getCameraSnapshotTask:" + snapshotPath);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            CameraListAdapter.this.notifyDataSetChanged();
                        }
                    });                    
                } catch (BaseException e) {
                    e.printStackTrace();
                }
                synchronized (mExecuteItemMap) {
                    mExecuteItemMap.remove(cameraInfo.getCameraId());
                }
            }
        };

        Future<?> ret = mExecutorService.submit(runnable);
    }
    
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = (Integer) v.getTag();
                switch (v.getId()) {
                    case R.id.item_play_btn:
                        mListener.onPlayClick(CameraListAdapter.this, v, position);
                        break;
                    case R.id.item_icon:
                        mListener.onPlayClick(CameraListAdapter.this, v, position);
                        break;
                }
            }
        }
    };
    
    public interface OnClickListener {

        public void onPlayClick(BaseAdapter adapter, View view, int position);
    }
}
