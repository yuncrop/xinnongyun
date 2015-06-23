package com.videogo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.sitech.oncon.barcode.core.CaptureActivity;
import com.videogo.androidpn.AndroidpnUtils;
import com.videogo.androidpn.Constants;
import com.videogo.constant.Constant;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EzvizAPI;
import com.videogo.openapi.bean.resp.DeviceInfo;
import com.xinnongyun.config.NetUrls;
import com.xinnongyun.net.NetConnectionCamera;
import com.xinnongyun.net.NetConnectionCamera.CameraSuccessCallBack;
import com.xinnongyun.sharepreference.MySharePreference;
import com.xinnongyun.sqlite.CameraDaoDBManager;

/**
 * 监听广播
 * 
 * @author sm
 * @data 2013-1-17
 */
public class EzvizBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            EzvizAPI.getInstance().refreshNetwork();
        } else if(action.equals(Constant.ADD_DEVICE_SUCCESS_ACTION)) {
           // String deviceId = intent.getStringExtra(IntentConsts.EXTRA_DEVICE_ID);
            try {
            	DeviceInfo deviceInfo = EzvizAPI.getInstance().getDeviceInfo(CaptureActivity.device_serial);
            	// 摄像头加入农场
				NetConnectionCamera.CameraPostNetConnection(NetUrls.URL_CAMERA_ADD,
						"id=" + deviceInfo.getCameraId() +
						"&farm=" + MySharePreference.getValueFromKey(context, MySharePreference.FARMID)+
					    "&device_id=" + deviceInfo.getDeviceId() +
						"&name=" + deviceInfo.getCameraName() +
					    "&device_serial=" + deviceInfo.getDeviceSerial()
						,
						"Token "+MySharePreference.getValueFromKey(context, MySharePreference.ACCOUNTTOKEN), 
						new CameraSuccessCallBack() {
							@Override
							public void onSuccess(HashMap<String, Object> result) {
								CameraDaoDBManager stationDaoDBManager = new CameraDaoDBManager(context);
								result.put("share_url", "http://www.nnong.com/v2/l/" + CaptureActivity.device_serial  + "/");
								List<HashMap<String, Object>> listDate = new ArrayList<HashMap<String, Object>>();
								listDate.add(result);
								stationDaoDBManager.insertAllCameras(listDate);
								Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
							}
						});
            	
            	
			} catch (BaseException e) {
			}
        } else if(action.equals(Constant.OAUTH_SUCCESS_ACTION)) {
//        	Toast.makeText(context, "萤云石授权成功", Toast.LENGTH_SHORT).show();
            AndroidpnUtils.startPushServer(context);
           
        } else if (Constants.NOTIFICATION_RECEIVED_ACTION.equals(action)) {
           // NotifierUtils.showNotification(context, intent);
        }
    }
}
