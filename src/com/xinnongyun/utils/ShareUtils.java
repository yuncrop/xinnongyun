package com.xinnongyun.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.tauth.Tencent;
import com.xinnongyun.activity.MainActivity;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.sharepreference.MySharePreference;

public class ShareUtils {

	public static void shareXinLang(final Context contex, String jouralId,
			String videoContent, String videoThumb,String url) {
		String shareURl = "http://www.nnong.com/v2/j/" + jouralId + "/";
		if(url!=null)
		{
			shareURl = url;
		}
		// 1. 初始化微博的分享消息
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
		// 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        if (MainActivity.mWeiboShareAPI.isWeiboAppSupportAPI()) {
                int supportApi = MainActivity.mWeiboShareAPI.getWeiboAppSupportAPI();
                if (supportApi >= 10351 /*ApiUtils.BUILD_INT_VER_2_2*/) {
                   //多条
                	TextObject textObject = new TextObject();
            	    textObject.text = videoContent;
            		weiboMessage.textObject = textObject;
            		
            		
            		
                    // 设置 Bitmap 类型的图片到视频对象里
                    try {
            			Bitmap bmp = BitmapFactory.decodeStream(new URL(videoThumb)
            					.openStream());
            			Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
            			bmp.recycle();
            			ImageObject imageObject = new ImageObject();
            		    imageObject.setImageObject(thumbBmp);
            			weiboMessage.imageObject = imageObject;
            			
            			WebpageObject mediaObject = new WebpageObject();
                        mediaObject.identify = Utility.generateGUID();
                        String name = MySharePreference.getValueFromKey(contex, MySharePreference.FARMNAME);
                        if(name.equals("null"))
                        {
                        	name = "新农云";
                        }
                        name += "农场视频分享";
                        name = name.replace("农场农场", "农场");
                        mediaObject.title = name;
                        mediaObject.description = videoContent;
                        // 设置 Bitmap 类型的图片到视频对象里
                        mediaObject.setThumbImage(thumbBmp);
                        mediaObject.actionUrl = shareURl;
                        mediaObject.defaultText = videoContent;
                		weiboMessage.mediaObject = mediaObject;
            		} catch (MalformedURLException e) {
            		} catch (IOException e) {
            		}
                } else {
                    //单条
                	WebpageObject mediaObject = new WebpageObject();
                    mediaObject.identify = Utility.generateGUID();
                    String name = MySharePreference.getValueFromKey(contex, MySharePreference.FARMNAME);
                    if(name.equals("null"))
                    {
                    	name = "新农云";
                    }
                    name = name.replace("农场", "");
                    mediaObject.title = name + "农场视频分享";
                    mediaObject.actionUrl = shareURl;
            		weiboMessage.mediaObject = mediaObject;
                }
                // 用transaction唯一标识一个请求
                request.transaction = String.valueOf(System.currentTimeMillis());
                request.multiMessage = weiboMessage;
                MainActivity.mWeiboShareAPI.sendRequest((Activity)contex, request);
                return;
            }
        //多条
    	TextObject textObject = new TextObject();
	    textObject.text = videoContent;
		weiboMessage.textObject = textObject;
		
	
        // 设置 Bitmap 类型的图片到视频对象里
        try {
        	
			Bitmap bmp = BitmapFactory.decodeStream(new URL(videoThumb)
					.openStream());
			Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
			bmp.recycle();
			ImageObject imageObject = new ImageObject();
		    imageObject.setImageObject(thumbBmp);
			weiboMessage.imageObject = imageObject;
			
			WebpageObject mediaObject = new WebpageObject();
            mediaObject.identify = Utility.generateGUID();
            String name = MySharePreference.getValueFromKey(contex, MySharePreference.FARMNAME);
            if(name.equals("null"))
            {
            	name = "新农云";
            }
            name = name.replace("农场", "");
            mediaObject.title = name + "农场视频分享";
            mediaObject.description = videoContent;
            // 设置 Bitmap 类型的图片到视频对象里
            mediaObject.setThumbImage(thumbBmp);
            mediaObject.actionUrl = shareURl;
            mediaObject.defaultText = videoContent;
    		weiboMessage.mediaObject = mediaObject;
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}
        
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        //MainActivity.mWeiboShareAPI.sendRequest((Activity)contex, request);
        AuthInfo authInfo = new AuthInfo(contex, YunTongXun.XINLANG_APP_ID, "http://www.sina.com",
        		"email,direct_messages_read,direct_messages_write,"
        	            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
        	            + "follow_app_official_microblog," + "invitation_write");
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(contex.getApplicationContext());
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }
        MainActivity.mWeiboShareAPI.sendRequest((Activity)contex, request, authInfo, token, new WeiboAuthListener() {
            
            @Override
            public void onWeiboException( WeiboException arg0 ) {
            }
            
            @Override
            public void onComplete( Bundle bundle ) {
                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                AccessTokenKeeper.writeAccessToken(contex.getApplicationContext(), newToken);
            }
            
            @Override
            public void onCancel() {
            }
        });
       
        
	}
	public static void shareQQZ(final Context contex, String jouralId, String videoContent,String videoThumb,String url) {
		try {
			String shareURl = "http://www.nnong.com/v2/j/" + jouralId + "/";
			if(url!=null)
			{
				shareURl = url;
			}
			Bundle params = new Bundle();
			params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
			String name = MySharePreference.getValueFromKey(contex, MySharePreference.FARMNAME);
            if(name.equals("null"))
            {
            	name = "新农云";
            }
            name = name.replace("农场", "");
			params.putString(QzoneShare.SHARE_TO_QQ_TITLE, name + "农场视频分享");//必填
			params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, videoContent);//选填
			params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareURl);//必填
			ArrayList<String> images = new ArrayList<String>();
			images.add(videoThumb);
			params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,images);
			Tencent mTencent = Tencent.createInstance(YunTongXun.QQ_APP_ID,contex.getApplicationContext());
			mTencent.shareToQzone((Activity)contex, params, null);
		} catch (Exception e) {
		}
	}
	
	public static void shareQQF(final Context contex, String jouralId, String videoContent,String videoThumb,String url) {
		    try {
				String shareURl = "http://www.nnong.com/v2/j/" + jouralId + "/";
				if(url!=null)
				{
					shareURl = url;
				}
				Bundle params = new Bundle();
				params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
				params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "新农云");
				String name = MySharePreference.getValueFromKey(contex, MySharePreference.FARMNAME);
                if(name.equals("null"))
                {
                	name = "新农云";
                }
                name = name.replace("农场", "");
				params.putString(QQShare.SHARE_TO_QQ_TITLE, name + "农场视频分享");
				params.putString(QQShare.SHARE_TO_QQ_SUMMARY, videoContent);
				params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, videoThumb);
				params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareURl);
				Tencent mTencent = Tencent.createInstance(YunTongXun.QQ_APP_ID,contex.getApplicationContext());
				mTencent.shareToQQ((Activity)contex, params, null);
			} catch (Exception e) {
			}
	}

	// 分享到微信好友
	public static void shareWXFriend(Context contex, String jouralId,
			String videoContent, String videoThumb,String url) {
		if(!MainActivity.wxApi.isWXAppInstalled())
		{
			Toast.makeText(contex, "未安装微信", Toast.LENGTH_SHORT).show();
			return;
		}
		String shareURl = "http://www.nnong.com/v2/j/" + jouralId + "/";
		if(url!=null)
		{
			shareURl = url;
		}
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = shareURl;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		String name = MySharePreference.getValueFromKey(contex, MySharePreference.FARMNAME);
        if(name.equals("null"))
        {
        	name = "新农云";
        }
        name = name.replace("农场", "");
        msg.title = name + "农场视频分享";
		msg.description = videoContent;
		try {
			Bitmap bmp = BitmapFactory.decodeStream(new URL(videoThumb)
					.openStream());
			Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 80, 80, true);
			bmp.recycle();
			msg.thumbData = bmpToByteArray(thumbBmp, true);
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		MainActivity.wxApi.sendReq(req);
	}

	// 分享到微信朋友圈
	public static void shareXWFriedCircle(Context contex, String jouralId,
			String videoContent, String videoThumb,String url) {
		if(!MainActivity.wxApi.isWXAppInstalled())
		{
			Toast.makeText(contex, "未安装微信", Toast.LENGTH_SHORT).show();
			return;
		}
		int wxApi = MainActivity.wxApi.getWXAppSupportAPI();
		if (wxApi >= 0x2102001) {
			String shareURl = "http://www.nnong.com/v2/j/" + jouralId + "/";
			if(url!=null)
			{
				shareURl = url;
			}
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = shareURl;
			WXMediaMessage msg = new WXMediaMessage(webpage);
			String name = MySharePreference.getValueFromKey(contex, MySharePreference.FARMNAME);
            if(name.equals("null"))
            {
            	name = "新农云";
            }
            name = name.replace("农场", "");
			msg.title = name + "农场视频分享";
			msg.description = videoContent;
			try {
				Bitmap bmp = BitmapFactory.decodeStream(new URL(videoThumb)
						.openStream());
				Bitmap thumbBmp = Bitmap
						.createScaledBitmap(bmp, 80, 80, true);
				bmp.recycle();
				msg.thumbData = bmpToByteArray(thumbBmp, true);
			} catch (MalformedURLException e) {
			} catch (IOException e) {
			}
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = String.valueOf(System.currentTimeMillis());
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
			MainActivity.wxApi.sendReq(req);
		} else {
			Toast.makeText(contex, "微信版本过低", Toast.LENGTH_SHORT).show();
		}
	}

	public static byte[] bmpToByteArray(final Bitmap bmp,
			final boolean needRecycle) {
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			bmp.compress(CompressFormat.PNG, 100, output);
			if (needRecycle) {
				bmp.recycle();
			}
			byte[] result = output.toByteArray();
			output.close();
			return result;
		} catch (Exception e) {
			return null;
		}

	}

}
