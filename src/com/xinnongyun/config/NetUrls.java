package com.xinnongyun.config;

/**
 * url地址
 * @author sm
 *
 */
public class NetUrls {

	public static final String AppUseHelp = "https://yuncrop.com/";


	//获取农场中控列表
	public static final String getFarmAllControlsInfo(String farmId)
	{
		return "https://m.nnong.com/v2/api-automator/cc/?farm=" + farmId;
	}
	
	//获取中控信息 serial
	public static final String getControlInfoBySerial(String serial)
	{
		return "https://m.nnong.com/v2/api-automator/cc/?serial=" + serial;
	}
		
	//获取中控信息 id
	public static final String getControlInfoById(String id)
	{
		return "https://m.nnong.com/v2/api-automator/cc/" + id + "/";
	}
	
	//修改中控信息 serial
	public static final String modifyControlInfo(String id)
	{
		return "https://m.nnong.com/v2/api-automator/cc/" + id + "/";
	}
	
	//安装中控
	public static final String mountControl(String id)
	{
		return "https://m.nnong.com/v2/api-automator/cc/" + id + "/mount/";
	}
	
	//卸载中控
	public static final String unmountControl(String id)
	{
		return "https://m.nnong.com/v2/api-automator/cc/" + id + "/unmount/";
	}

	//获取中控下所有控制单元
	public static final String getAllBlowerFromControl(String controlId)
	{
		return "https://m.nnong.com/v2/api-automator/blower/?cc=" + controlId;
	}
	
	//获取控制单元信息
	public static final String getBlowerInfoById(String id)
	{
		return "https://m.nnong.com/v2/api-automator/blower/" + id + "/";
	}
	
	//修改放风机信息
	public static final String modifyBlowerInfos()
	{
		return "https://m.nnong.com/v2/api-automator/blower/";
	}
	
	//移动放风机风口
	public static final String moveBlowerDistance()
	{
		return "https://m.nnong.com/v2/api-automator/blower/move/";
	}
	

	
	//重置放风机
	public static final String resetBlower()
	{
		return "https://m.nnong.com/v2/api-automator/blower/reset/";
	}
	
	//获取控元最新数据
	public static final String getUnitLastestInfo(String id)
	{
		return "https://m.nnong.com/v2/api-automator/blower-env/observe/?blower=" + id;
	}
	
	
	//获取控元一段时间内的环境信息序列
	public static final String getUnitOneDayInfo(String id,String fromTime,String endTime)
	{
		return "https://m.nnong.com/v2/api-automator/blower-env/?blower=" + id + "&begin_at=" + fromTime + "&end_at=" + endTime;
	}
	
	
	
	
	/**
	 * 萤云石地址
	 */
	public static final String URIYINGYUNSHI = "httpss://open.ys7.com/api/method";
	
	
	public static final String URL_CAMERA_ADD = "https://m.nnong.com/v2/api-camera/";
	
	
	public static final String URI_CAMREA_GET_BY__ID(String id)
	{
		return " https://m.nnong.com/v2/api-camera/camera/" + id + "/";
	}
	
	public static final String URI_CAMREA_GET_BY__SERIAL(String serial)
	{
		return "https://m.nnong.com/v2/api-camera/camera/?device_serial=" + serial;
	}
	
	public static final String URI_CAMREAList(String farm)
	{
		return "https://m.nnong.com/v2/api-camera/farm/?farm=" + farm;
	}
	
	
	public static final String URL_CAMREA_MODIFY_ADD(String id)
	{
		return "https://m.nnong.com/v2/api-camera/" + id + "/";
	}
	
	public static final String URL_CAMREA_DELETE(String id)
	{
		return "https://m.nnong.com/v2/api-camera/" + id + "/unmount/";
	}
	
	
	//修改环境小站
	public static final String URL_WEATHER_STATIONSINFOCHANGE(String weatherId)
	{
		return "https://m.nnong.com/v2/api-weather-station/"+ weatherId +"/";
	}
		 
	//获取农场下所有的环境小站实时数据
	public static final String URL_WEATHER_STATIONSTimeLine(String weatherId)
	{
		return "https://m.nnong.com/v2/api-weather-station/" + weatherId +"/observe/";
	}
	
	//获取农场下所有的环境小站实时数据
	public static final String URL_WEATHER_STATIONSAVG30(String weatherId)
	{
		return "https://m.nnong.com/v2/api-weather-station/" + weatherId + "/avg30/";
	}
	
	
	//获取农场下所有的环境小站
	public static final String URL_WEATHER_STATIONS(String farmId)
	{
		return "https://m.nnong.com/v2/api-weather-station/farm/?farm=" + farmId;
	}
	
	
	//七牛token路径
	public static final String URL_QINIU_TOKEN = "https://www.nnong.com/api-journal/token/";
	
	
	public static String get_URL_JOURNALVIDEO_BYID(String journal_id)
	{
		return "https://m.nnong.com/api-journal/" + journal_id + "/";
	}
	
	
	/**
	 * 赞   需要usertoken
	 * @param journal_id
	 * @return
	 */
	public static  String getURL_JOURNALVIDEO_LIKE(String journal_id)
	{
		return "https://m.nnong.com/api-journal/" + journal_id + "/like/";
	}
	
	/**
	 * 播放次数
	 * @param journal_id
	 * @return
	 */
	public static  String getURL_JOURNALVIDEO_VIEWS(String journal_id)
	{
		return "https://m.nnong.com/api-journal/" + journal_id + "/view/";
	}
	
	/**
	 * 评论  需要usertoken  content=要钱
	 * @param journal_id
	 * @return
	 */
	public static String getURL_JOURNALVIDEO_COMMENTS(String journal_id)
	{
		 return "https://m.nnong.com/api-journal/" + journal_id + "/comment/";
	}
	
	//视频日志api
	public static final String URL_VIDEO_CREATE = "https://www.nnong.com/api-journal/";
	public static final String URL_VIDEO_DELETE(String id)
	{
		return "https://www.nnong.com/api-journal/" + id + "/";
	}
	
	
	public static final String URL_NOTICE_GETALL = "https://m.nnong.com/api-notice/";
	public static String getUpdateNotice(String id)
	{
		return "https://m.nnong.com/api-notice/"+ id +"/";
	}
	
	//新建种养记录
	public static final String URL_FARMING_NEW = "https://m.nnong.com/api-farming/";
	public static final String URL_FARMING_GETALL(String farmId)
	{
		return "https://m.nnong.com/api-farming/?farm=" + farmId;
	}
	
	
	public static final String URL_FARMING_MODIFY(String farmId)
	{
		return "https://m.nnong.com/api-farming/" + farmId+ "/";
	}
	
	/**
	 * 意见反馈 POST 'content=test'
	 */
	public static final String URL_SUGGESTION = "https://m.nnong.com/api-feedback/";
	
	/**
	 * 找回密码
	 */
	public static final String FINDPWD = "https://m.nnong.com/api-user/reset_password/";
	
	
	/**
	 * 二维码序列判断
	 */
	public static final String URL = "https://m.nnong.com";
	public static final String URL2 = "https://m.nnonq.com";
	
	
	//创建、获取农场
	// /api-farm/ -H 'Authorization: Token b23d1ff85398571932801bde0ae3f73e3e5e595c' -d 'name=测试农场1&bg=&description=&lat=&lng=&location=&logo='
	public static final String URL_FARM_CREATE = "https://m.nnong.com/api-farm/";
	public static final String URL_FARMMANAGER = "https://m.nnong.com/farm/";
	//获取所属农村的信息
	public static final String URL_FARMMANAGER_GETMINE = "https://m.nnong.com/api-farm/mine/";
	//修改农场
	public static String URL_MODIFY_FARM(String farmId)
	{
		return "https://m.nnong.com/api-farm/" + farmId + "/";
	}
	
	//退出农场
	public static String URL_EXIT_FARM(String farmId)
	{
		return "https://m.nnong.com/api-farm/"+ farmId +"/depart/";
	}
	
	//删除农场
	public static String URL_DELETE_FARM(String farmId)
	{
		return "https://m.nnong.com/api-farm/" + farmId + "/";
	}
	
	//更新采集器信息
	public static String URL_COLLECT_UPDATE(String collectID)
	{
		return "https://m.nnong.com/api-collector/" + collectID + "/";
	}
	
	//获取当前农场的种养盒子
	public static String URL_FARMING_COLLECTS = "https://m.nnong.com/api-farming/";
	
	//获取农场下所有盒子信息
	public static String URL_FARM_COLLECTS = "https://m.nnong.com/api-collector/";
	
	//获取盒子信息
	public static String URL_COLLECTOR_GET(String collectserial)
	{
		return "https://m.nnong.com/api-collector/?serial=" + collectserial + "";
	}
	
	//获取盒子信息
	public static String URL_GATEWAY_GET(String id)
	{
		return "https://m.nnong.com/api-gateway/" + id + "/";
	}
		
	
	/**
	 * 作物/动物管理
	 * 添加  POST user_serial, password, being_name, (being_image), kingdom(输入为1表示动物，2表示作物)
	 * -----
	 * 获取信息 GET user_serial, password
	 * ------
	 * 修改信息 PUT user_serial, password, being_serial, (new_name, new_image, new_kingdom(同kingdom，1表示动物，2表示作物))
	 * ------
	 * 删除  DELETE user_serial, password, being_serial
	 * --------
	 * 200(ok); 400(参数错误); 401(未授权); 403(没有权限); 404(该动物/作物不存在)
	 */
	public static final String URL_BEINGS = "https://m.nnong.com/api-being/reserved/";
	public static final String URL_BEINGS_FARM = "https://m.nnong.com/api-being/";
	/**
	 * 土壤管理
	 * 获取土壤类型 GET user_serial, password
	 * -----
	 * 200(ok); 400(参数错误); 401(未授权)
	 */
    public static final String URL_SOIL = "https://m.nnong.com/api-soil/reserved/";
	
    
    /**
     * 时间线管理
     * 获取时间线 GET user_serial, password, collector_serial, last_refreshed_at(格式为yyyy-mm-ddTHH:MM:SS)
     * --------
     * 200(ok); 400(参数错误); 401(未授权); 404(该盒子不存在)
     */
    public static final String URL_TIMELINE = "https://m.nnong.com/api-timeline/";
    
    
    /**
     * K线管理
     * 获取K线 
     * GET user_serial, password, collector_serial, last_refreshed_at(格式为yyyy-mm-ddTHH:MM:SS)
     * -------
     * 200(ok); 400(参数错误); 401(未授权); 404(该盒子不存在)
     */
    public static final String URL_K_TIMELINE = "https://m.nnong.com/api-kline/";
    
    
    /**
     * 版本管理
     * 获取信息 GET  
     * -----
     * 200(ok)
     */
    public static final String URL_VERSION = "https://m.nnong.com/api-version/latest/";
    public static final String URL_VERSION_LAST = "version_code";
    public static final String URL_VERSION_URL = "package";
    
	/**
	 * 账户信息管理路径 登录get   注册post
	 */
    public static final String URL_LOGIN = "https://m.nnong.com/api-user/login/";
    public static final String URL_LOGIN_ZHUCE = "https://m.nnong.com/api-user/register/";
    public static final String URL_MODIFY_USERINFO = "https://m.nnong.com/api-user/";
    public static String URL_MOFDIFYUSERPWD(String id)
    {
    	return "https://m.nnong.com/api-user/set_password/";          //"https://m.nnong.com/user/" + id + "/set_password/";
    }
    
	//public static final String URL_LOGIN_ZHUCE = "https://m.nnong.com/account/api";
	public static final String URL_LOGIN_ZHUCE_USERNAME = "username";
	public static final String URL_LOGIN_ZHUCE_PASSWORD = "password";
	
	/**
	 * 获取验证码
	 */
	public static final String URL_ZHUCE_GETYAZHENGMA="https://m.nnong.com/api-sms/verify/";
	public static final String URL_ZHUCE_GETYAZHENGMA_PHONE = "phone_number";
	public static final String URL_ZHUCE_USERNAME = "username";
	//验证码的返回关键字
	public static final String URL_ZHUCE_YANZHENGMA_RESPONSE_KEY = "response";
	
	//返回信息
	//返回的数字
	public static final String LOGIN_BACK_CODE_KEY = "code";
	public static final String ZHUCE_BACK_CODE_RESPONSE = "response";
	
	//注册返回码
	public static final String ZHUCE_BACK_CODE_OK_VALUE = "201";
	public static final String ZHUCE_BACK_CODE_BADREQUEST_VALUE = "400";
	//登录返回码
	public static final String LOGIN_BACK_CODE_OK_VALUE = "200";
	public static final String LOGIN_BACK_CODE_UNAUTHORIZED_VALUE = "401";
	public static final String LOGIN_BACK_CODE_BADREQUEST_VALUE = "400";
}

