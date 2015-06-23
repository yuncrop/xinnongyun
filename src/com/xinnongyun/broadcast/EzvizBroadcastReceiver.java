package com.xinnongyun.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.videogo.androidpn.AndroidpnUtils;
import com.videogo.openapi.EzvizAPI;
import com.videogo.util.Utils;

public class EzvizBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "EzvizBroadcastReceiver";
    
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            EzvizAPI.getInstance().refreshNetwork();
            return;
        }
        if(action.equals("com.videogo.action.ADD_DEVICE_SUCCESS_ACTION")) {
            String deviceId = intent.getStringExtra("com.videogo.EXTRA_DEVICE_ID");
            Utils.showToast(context, context.getString(0x7f0900e1, new Object[] {deviceId}));
            return;
        }
        if(action.equals("com.videogo.action.OAUTH_SUCCESS_ACTION")) {
//            Intent toIntent = new Intent(context, CameraListActivity.class);
//            toIntent.setFlags(0x10000000);
//            context.startActivity(toIntent);
            AndroidpnUtils.startPushServer(context);
            return;
        }
        if("com.videogo.androidpn.NOTIFICATION_RECEIVED_ACTION".equals(action)) {
        }
    }
}