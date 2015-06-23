package com.xinnongyun.config;

import java.io.File;

import android.os.Environment;

/**
 * 基本常量
 * 
 * @author sm
 * 
 */
public class YunTongXun {

	public static final String API_KEY = "vrH5pWiTbrI6AFVnLjbo1E6u";
	public static final String WX_APP_ID = "wx396427e34a46cebb";//c4bb4e90b0d921f25b20eca6324d8220 签名
	public static final String QQ_APP_ID = "1104418320";
	public static final String XINLANG_APP_ID = "3356495267";
	
	
	public static final String YINGYUNSHI_APP_KEY = "55458d99506a4e0ebeeebe5ee22fc4d2";
	public static final String YINGYUNSHI_SECRET_KEY = "0636ee9b5cb417747507e0a4a85464c5";
	public static String YINGYUNSHI_API_URL = "https://open.ys7.com";
    public static String YINGYUNSHI_WEB_URL = "https://auth.ys7.com";
	/**
	 * 进入二维码界面显示判断
	 */
	public static final String CAPTURE_KEY = "capture_key";
	public static final String CAPTURE_HANRDCONFIG = "capture_hardconfig";
	public static final String CAPTURE_ADDFARM = "capture_addfarm";

	// 默认温度
	public static final double minTemDefault = -99.9;
	public static final double maxTemDefault = 99.9;
	// 默认湿度
	public static final int minHumDefault = 0;
	public static final int maxHumDefault = 100;
	// 默认光照
	public static final int minLightDefault = 0;
	public static final int maxLightDefault = 130000;
	// 土温
	public static final double minSoilTemDefault = -50.0;
	public static final double maxSoilTemDefault = 50.0;
	// 土湿
	public static final int minSoilHumDefault = 0;
	public static final int maxSoilHumDefault = 100;

	// 肥力 极低，较低，一般，良好，优秀，过量 代表0.1-3.0 ,公式待定
	// 土壤 沙土，普通壤土，黏土
	// 信号 4格 代表20-130 4格:<80 ， 3格:80-90,2格：90-95，1格：>95
	// 电池 0%-100% 代表3.38-4.20

	public static final int splashTime = 1000;
	public static final int minuteLength = 33;
	public static final long powderLeft = 10;

	// 请求中断时间
	public static final int httpclienttime = 10 * 1000;

	/**
	 * 不置顶
	 */
	public static final String orderDefault = "200";
	/**
	 * 置顶
	 */
	public static final String order = "100";

	public static final int USERIMAGEARATRA = 80;
	public static final int FARMLOGO = 80;
	public static final int FARMBG = 200;
	// 图片保存路径
	public static final String ImagePath = getSDPath() + "/001yunhekeji100/";
	public static final String VideoPath = ImagePath + "CAMREA/";
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		return sdDir.toString();
	}

	// 用户头像：http://m.nnong.com/media/user_avatar/default.png
	// 农场logo：http://m.nnong.com/media/farm_logo/default.png
	// 农场背景：http://m.nnong.com/media/farm_bg/default.png
	// 作物：http://m.nnong.com/media/being_icon/default_1.png
	// 动物：http://m.nnong.com/media/being_icon/default_2.png
	// 土壤：http://m.nnong.com/media/soil_icon/default.png
	public static final String FarmTuRangImageDefault = "http://m.nnong.com/media/soil_icon/soil_icon_default.png";
	public static final String FarmBeingAnimImageDefault = "http://m.nnong.com/media/being_icon/being_icon_default_2.png";
	public static final String FarmBeingImageDefault = "http://m.nnong.com/media/being_icon/being_icon_default_1.png";
	public static final String FarmBgImageDefault = "http://static.nnong.com/media/farm_bg/default.png";

	public static final String FarmLogoImageDefault = "http://static.nnong.com/media/farm_logo/default.png";
	public static final String UserHeadImageDefault = "http://m.nnong.com/media/user_avatar/user_avatar_default.png";
}
