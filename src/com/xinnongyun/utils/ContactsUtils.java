package com.xinnongyun.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.xinnongyun.config.YunTongXun;

@SuppressWarnings("deprecation")
public class ContactsUtils {

	private String authToken;
	private Context mContext = null;
	/** 获取库Phon表字段 **/
	private  String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER};

	public ContactsUtils(Context mContext,String authToken) {
		this.mContext = mContext;
		this.authToken = authToken;
		/** 得到手机通讯录联系人信息 **/
		getPhoneContacts();
	}

	/** 得到手机通讯录联系人信息 **/
	private void getPhoneContacts() {
		try {
			ContentResolver resolver = mContext.getContentResolver();

			// 获取手机联系人
			Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
					PHONES_PROJECTION, null, null, null);

			if (phoneCursor != null) {
				ExportToCSV(phoneCursor,"xinnongyun_contacts.csv");

			if(phoneCursor!=null)
				phoneCursor.close();
			}
		} catch (Exception e) {
		}
	}

	public void ExportToCSV(Cursor c, String fileName) {

		int rowCount = 0;
		int colCount = 0;
		FileWriter fw;
		BufferedWriter bfw;
		File sdCardDir = Environment.getExternalStorageDirectory();
		File saveFile = new File(sdCardDir, fileName);
		try {

			rowCount = c.getCount();
			colCount = c.getColumnCount();
			fw = new FileWriter(saveFile);
			bfw = new BufferedWriter(fw);
			if (rowCount > 0) {
				c.moveToFirst();
				// 写入表头
				for (int i = 0; i < colCount; i++) {
					if (i != colCount - 1)
						bfw.write(c.getColumnName(i) + ',');
					else
						bfw.write(c.getColumnName(i));
				}
				// 写好表头后换行
				bfw.newLine();
				// 写入数据
				for (int i = 0; i < rowCount; i++) {
					c.moveToPosition(i);
					for (int j = 0; j < colCount; j++) {
						if (j != colCount - 1)
							bfw.write(c.getString(j) + ',');
						else
							bfw.write(c.getString(j));
					}
					// 写好每条记录后换行
					bfw.newLine();
				}
			}
			// 将缓存数据写入文件
			bfw.flush();
			// 释放缓存
			bfw.close();
			
		   //TODO 上传文件  删除文件
		    HashMap<String, String> paramsMap = new HashMap<String, String>();
		    paramsMap.put("csv", saveFile.getAbsolutePath());
			httpPost("http://www.nnong.com/v2/api-contact/", paramsMap,authToken);
			if(saveFile!=null)
			{
				if(saveFile.exists())
				{
					saveFile.delete();
				}
			}
		} catch (Exception e) {
			if(saveFile!=null)
			{
				if(saveFile.exists())
				{
					saveFile.delete();
				}
			}
		} finally {
			c.close();
		}
	}
	
	
	
	/**
	 * 通过post方式发送请求  上传文件
	 * @param url URL地址
	 * @param params 参数
	 * @return 
	 * @throws Exception
	 */
	public String httpPost(String url,  HashMap<String, String> params,String authToken) throws Exception
	{
		String response = null; //返回信息
		//拼接请求URL
		int timeoutConnection = YunTongXun.httpclienttime;  
		int timeoutSocket = YunTongXun.httpclienttime;  
		HttpParams httpParameters = new BasicHttpParams();// Set the timeout in milliseconds until a connection is established. 
	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);// Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data.  
	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);  
	    
		// 构造HttpClient的实例
		HttpClient httpClient = new DefaultHttpClient(httpParameters);
		
		// 创建GET方法的实例
		HttpPost httpPost = new HttpPost(url);
		
		if (authToken != null) {
			httpPost.setHeader("Authorization", authToken);
		}
		if (params.size()>=0)
		{
			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("csv", new FileBody(new File(params.get("csv"))));
			//设置httpPost请求参数
			httpPost.setEntity(reqEntity);
		}
		
		try
		{
			HttpResponse httpResponse = httpClient.execute(httpPost);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) //SC_OK = 200
			{
				// 获得返回结果
				response = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
			}
			else
			{
				response = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
				return "{status_code:"+statusCode+"}";
			}
		} catch (Exception e)
		{
			return null;
		} 
		return response;
	}
	
}
