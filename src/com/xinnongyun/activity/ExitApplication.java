package com.xinnongyun.activity;

import java.util.LinkedList;
import java.util.List;
import com.videogo.MyTokenAsy;
import com.videogo.openapi.EzvizAPI;
import com.xinnongyun.config.YunTongXun;
import com.xinnongyun.crashexception.CrashHandler;
import com.xinnongyun.sharepreference.MySharePreference;
import android.app.Activity;
import android.app.Application;
import cn.jpush.android.api.JPushInterface;

public class ExitApplication extends Application  {

	private List<Activity> activityList = new LinkedList<Activity>();
	private static ExitApplication instance;
	@Override  
    public void onCreate() {  
        super.onCreate();
        
        try {
            Class.forName("android.os.AsyncTask");
            } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //jpush推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(getApplicationContext());
        
        EzvizAPI.init(this, YunTongXun.YINGYUNSHI_APP_KEY, YunTongXun.YINGYUNSHI_SECRET_KEY);
        EzvizAPI.getInstance().setServerUrl(YunTongXun.YINGYUNSHI_API_URL, YunTongXun.YINGYUNSHI_WEB_URL);
        
        
        String tokenTime = MySharePreference.getValueFromKey(this, "tokenTime");
		if(tokenTime.equals("null"))
		{
			MyTokenAsy myToken = new MyTokenAsy();
			myToken.context = this;
			myToken.execute();
		}
		else
		{
			if(System.currentTimeMillis() / 1000 - Long.parseLong(tokenTime) > 60 * 60 * 24 * 5)
			{
				MyTokenAsy myToken = new MyTokenAsy();
				myToken.context = this;
				myToken.execute();
			}else
			{
				EzvizAPI.getInstance().setAccessToken(MySharePreference.getValueFromKey(this, "yingyunshi_token"));
			}
		}
        CrashHandler crashHandler = CrashHandler.getInstance();  
        crashHandler.init(getApplicationContext());  
    }  
	
	// 单例模式中获取唯一的ExitApplication实例
	public static ExitApplication getInstance() {
		if (null == instance) {
			instance = new ExitApplication();
		}
		return instance;

	}
	
	public  Activity getActivity(Class<?> activityInfo)
	{
		for (Activity activity : activityList) {
			if(activity!=null && activity.getClass() == activityInfo)
			{
				return activity;
			}
		}
		return null;
	}
	

	public boolean checkActivity(Class<?> activityInfo)
	{
		for (Activity activity : activityList) {
			if(activity!=null && activity.getClass() == activityInfo)
			{
				return true;
			}
		}
		return false;
	}
	
	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	// 遍历所有Activity并finish

	public void exit() {
		for (Activity activity : activityList) {
			if(activity!=null)
			     activity.finish();
		}
		activityList = new LinkedList<Activity>();
		
	}
}