package com.xinnongyun.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * 字符串处理
 * 
 * @author sm
 * 
 */
public class StringUtil {
	
	/**
	 * 判断数字 -0.34
	 * 
	 * @param strNum
	 * @return 是数字返回true
	 */
	public static boolean checkNumber(String strNum) {
		return strNum.matches("^-?\\d+$");
	}

	/**
	 * 判断EditText是否为空
	 * 
	 * @param et
	 * @return 为空反回true
	 */
	public static boolean checkEditTextIsEmpty(EditText et) {
		if (et != null && !TextUtils.isEmpty(et.getText())
				&& !TextUtils.isEmpty(et.getText().toString().trim())) {
			return false;
		}
		return true;
	}

	/**
	 * 验证手机号码
	 * 
	 * @param mobiles
	 * @return 正确返回true
	 */
	public static boolean isMobileNO(String mobiles) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		String telRegex = "[1][0123456789]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
	}

	/**
	 * 检查昵称 农场名
	 * 
	 * @param nickName
	 * @return
	 */
	public static boolean checkNickName(String nickName) {
		if (nickName != null && nickName.trim().length() <= 15) {
			return true;
		}
		return false;
	}

	/**
	 * 检查盒子名称
	 * 
	 * @param boxName
	 * @return
	 */
	public static boolean checkBoxName(String boxName) {
		if (boxName != null && boxName.trim().length() <= 10) {
			return true;
		}
		return false;
	}

	/**
	 * 评论 意见
	 * 
	 * @param nickName
	 * @return
	 */
	public static boolean checkSuggestion(String str) {
		if (str != null && str.trim().length() <= 255) {
			return true;
		}
		return false;
	}

	/**
	 * 农场介绍
	 * 
	 * @param farmName
	 * @return
	 */
	public static boolean checkFarmIntroduce(String farmName) {
		if (farmName != null && farmName.trim().length() <= 140) {
			return true;
		}
		return false;
	}

	/**
	 * 验证密码格式 字母数字
	 * 
	 * @param pwd
	 * @return 正确返回true
	 */
	public static boolean checkPwd(String pwd) {
		String telRegex = "[0-9a-zA-Z]{6,10}";
		if (TextUtils.isEmpty(pwd))
			return false;
		else
			return pwd.matches(telRegex);
	}

	public static String randomNum() {
		return "" + ((int) ((Math.random() * 9 + 1) * 100000));
	}

	/**
	 * 从url中获取文件名
	 * 
	 * @param url
	 * @return
	 */
	public static String getFileNameFromUrl(String url) {
		return url.substring(url.lastIndexOf("/") + 1);
	}

	/**
	 * 设置默认昵称
	 * 
	 * @param url
	 * @return
	 */
	public static String getNickNameFromPhone(String phone) {
		return phone.substring(0, 3) + "*****" + phone.substring(8);
	}

	/**
	 * 转化时间 到服务器格式  无秒
	 * 
	 * @param data
	 * @return
	 */
	public static String dataChangeToT(String data) {
		String newData = data.replaceAll("年", "-").replaceAll("月", "-")
				.replaceAll("日", "").replace(" ", "T");
		while (newData.contains("TT"))
			newData = newData.replaceAll("TT", "T");
		newData += ":00";
		return newData;
	}
	
	/**
	 * 转化时间 到服务器格式  秒
	 * 
	 * @param data
	 * @return
	 */
	public static String dataChangeToS(String data) {
		String newData = data.replaceAll("年", "-").replaceAll("月", "-")
				.replaceAll("日", "").replace(" ", "T");
		while (newData.contains("TT"))
			newData = newData.replaceAll("TT", "T");
		return newData;
	}

	/**
	 * 将服务器时间2014-01-01T00:00:00转化为X月X日
	 * 
	 * @param serverData
	 * @return
	 */
	public static String getMouthDay(String serverData) {
		int i = Integer.parseInt(serverData.substring(serverData.lastIndexOf("-") + 1,
				serverData.lastIndexOf("T")))%33;
		int j = Integer.parseInt(serverData.substring(serverData.indexOf("-") + 1,
				serverData.lastIndexOf("-")))%13;
		return j + "月" + i + "日";
	}

	/**
	 * 得到当前日期时间2014年12月17日 HH:MM
	 * 
	 * @return
	 */
	public static String getCurrentData() {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy年MM月dd日    HH:mm");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		return formatter.format(curDate);
	}

	/**
	 * 得到当前日期时间 12月12日 HH:MM:ss
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getCurrentDataS() {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		 Calendar cal2 = Calendar.getInstance();
		  cal2.setTime(curDate);
		  String str = (curDate.getMonth()+1) + "月" + (cal2.get(Calendar.DAY_OF_MONTH))+"日 ";
		return str + formatter.format(curDate);
	}
	
	/**
	 * 得到当前日期时间 xxxx年12月12日 HH:MM:ss
	 * 
	 * @return
	 */
	public static String getCurrentDataSALL() {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy年MM月dd日 HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		return formatter.format(curDate);
	}
	
	
	/**
	 * 得到当前日期时间 xxxx年12月12日 HH:MM:ss
	 * 
	 * @return
	 */
	public static String getCurrentDataSALL(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy年MM月dd日 HH:mm:ss");
		return formatter.format(date);
	}
	
	/**
	 * 得到日期的前或者后几小时
	 * 
	 * @param iHour
	 *            如果要获得前几小时日期，该参数为负数； 如果要获得后几小时日期，该参数为正数
	 * @see java.util.Calendar#add(int, int)
	 * @return Date 返回参数<code>curDate</code>定义日期的前或者后几小时
	 */
	public static Date getDateBeforeOrAfterHours(Date curDate, int iHour) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(curDate);
		cal.add(Calendar.HOUR_OF_DAY, iHour);
		return cal.getTime();
	}
	
	
	/**
     * 获得给定时间若干秒以前或以后的日期的标准格式。
     *
     * @param curDate
     * @param seconds
     * @return curDate
     */
    public static Date getSpecifiedDateTimeBySeconds(Date curDate, int seconds) {
        long time = (curDate.getTime() / 1000) + seconds;
        curDate.setTime(time * 1000);
        return curDate;
    }
	
	
	// 把字符串转为日期
	public static Date stringConverToDate(String strDate) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return df.parse(changeSerTo(strDate));
		} catch (ParseException e) {
			return new Date();
		}
	}

	public static String changeSerTo(String serverData) {
		return serverData.replaceAll("T", " ");
	}
	
	/**
	 * 将xxxx-bb-dd sd:sd:sd 转化为xxxx年bb月dd日 sd:sd:sd
	 * 
	 * @return
	 */
	public static String changeTimeStringToDataUtil(String dataString) {
		return dataString.replaceFirst("-", "年").replace("-", "月")
				.replace(" ", "日 ");
	}

	/**
	 * 时间差
	 * 
	 * @param newDate
	 * @param oldDate
	 * @return minutes
	 */
	public static long miniteLength(Date newDate, Date oldDate) {
		long diff = newDate.getTime() - oldDate.getTime();// 这样得到的差值是微秒级别
		long minutes = (long)Math.floor(diff/60000);
		return minutes;
	}

	/**
	 * 当前年
	 * 
	 * @return
	 */
	public static int getCurrentYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	/**
	 * 当前月
	 * 
	 * @return
	 */
	public static int getCurrentMouth() {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	/**
	 * 当前日
	 * 
	 * @return
	 */
	public static int getCurrentDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * xx前
	 * @param date
	 * @return
	 */
   public static String getTimeDiff(Date date) {
        Calendar cal = Calendar.getInstance();
        long diff = 0;
        Date dnow = cal.getTime();
        String str = "";
        diff = dnow.getTime() - date.getTime();
        if(diff > 2592000000L * 12)
        {
        	str= (int)(diff/(2592000000L * 12)) + ":年前更新";
        }
        if  (diff > 2592000000L) {//30 * 24 * 60 * 60 * 1000=2592000000 毫秒
            str=  (int)(diff/2592000000L) + "个月前更新";
        } else if  (diff > 1814400000) {//21 * 24 * 60 * 60 * 1000=1814400000 毫秒
            str="3:周前更新";
        } else if  (diff > 1209600000) {//14 * 24 * 60 * 60 * 1000=1209600000 毫秒
            str="2:周前更新";
        } else if  (diff > 604800000) {//7 * 24 * 60 * 60 * 1000=604800000 毫秒
            str="1:周前更新";
        } else if (diff > 86400000) {    //24 * 60 * 60 * 1000=86400000 毫秒
            str=(int)Math.floor(diff/86400000f) + ":天前更新";
        } else if (diff > 18000000 ) {// * 60 * 60 * 1000=18000000 毫秒
            str=(int)Math.floor(diff/3600000f) + ":小时前更新";
        } else if (diff > 60000) {//1 * 60 * 1000=60000 毫秒
            //System.out.println("X分钟前");
            str=(int)Math.floor(diff/60000) +":分钟前更新";
        }else if(diff > 0){
            str=(int)Math.floor(diff/1000) +":秒前更新";
        }else
        {
        	 str="1:秒前更新";
        }
        return str;
    }
	
   /**
	 * notice 时间处理
	 * @param date
	 * @return
	 */
  @SuppressWarnings("deprecation")
public static String getTimeFormatInfo(String dataString) {
       Calendar cal = Calendar.getInstance();
       long diff = 0;
       Date dnow = cal.getTime();
       String str = "";
       Date date = stringConverToDate(dataString.replace("T", " "));
       diff = dnow.getTime() - date.getTime();
       if(isToday(dataString))
       {
    	   if (diff >= 60000 && 3600000 > diff) {//1 * 60 * 1000=60000 毫秒
               str=(int)Math.floor(diff/60000) +"分钟前";
           }else if(diff < 60000){
               str="刚刚";
           }else
           {
        	   str = (getHour(date)<10 ?  "0" +getHour(date) : getHour(date)+"") + ":" + (getMinute(date)<10? "0"+getMinute(date) :getMinute(date)+"");
           }
    	   
       }
       else
       {
    	   if(date.getYear() == new Date().getYear())
    	   {
    		   Calendar cal2 = Calendar.getInstance();
    		   cal2.setTime(date);
    		   str = (date.getMonth()+1) + "月" + (cal2.get(Calendar.DAY_OF_MONTH))+"日";
    	   }
    	   else
    	   {
    		   str = dateFormater2.get().format(date);
    	   }
       }
       
       return str;
   }

   /**
    * 判断给定字符串时间是否为今日
    * @param sdate
    * @return boolean
    */
   public static boolean isToday(String sdate){
       boolean b = false;
       Date time = stringConverToDate(sdate);
       Date today = new Date();
       if(time != null){
           String nowDate = dateFormater2.get().format(today);
           String timeDate = dateFormater2.get().format(time);
           if(nowDate.equals(timeDate)){
               b = true;
           }
       }
       return b;
   }
   
   private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
       @Override
       protected SimpleDateFormat initialValue() {
           return new SimpleDateFormat("yyyy-MM-dd");
       }
   };
   
	
 //得到小时
   public static int getHour(Date date) {
       Calendar calendar = Calendar.getInstance();
       calendar.setTime(date);
       return calendar.get(Calendar.HOUR_OF_DAY);
   }

   //得到分钟
   public static int getMinute(Date date) {
       Calendar calendar = Calendar.getInstance();
       calendar.setTime(date);
       return calendar.get(Calendar.MINUTE);
   }
	
	// 获取屏幕的宽度
	@SuppressWarnings("deprecation")
	public static int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getWidth();
	}

	// 获取屏幕的高度
	@SuppressWarnings("deprecation")
	public static int getScreenHeight(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getHeight();
	}
	
	public static String removeDateZero(String dateStr)
	{
		String firstStr = dateStr.substring(0, 5);
		String secondStr = dateStr.substring(5,8);
		String thirdStr = dateStr.substring(8);
		if(secondStr.startsWith("0"))
		{
			secondStr = secondStr.substring(1);
		}
		if(thirdStr.startsWith("0"))
		{
			thirdStr = thirdStr.substring(1);
		}
		return firstStr + secondStr +thirdStr;
	}

	public static String getAlt(double alt) {
		int altSec = (int)alt;
//		if(altSec < 0)
//		{
//			altSec = 0;
//		}
		String str = "海拔 " + altSec + "米";
//		if(altSec >=3500)
//		{
//			str += ":超高海拔";
//		}else if(altSec>=1500)
//		{
//			str += ":高海拔";
//		}else if(altSec>=500)
//		{
//			str += ":中海拔";
//		}else if(altSec>=0)
//		{
//			str += ":低海拔";
//		}
		return str;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
