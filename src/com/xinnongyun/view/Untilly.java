package com.xinnongyun.view;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Matrix;


/**
 * 工具类
 * @author LJC
 *
 */
public class Untilly {
	/**
	 *  缩放图片
	 * @param icon
	 * @param h
	 * @return
	 */
	public static Bitmap  zoomBitmap(Bitmap icon,int h){
		// 缩放图片
		Matrix m = new Matrix();
		float sx = (float) 2 * h / icon.getWidth();
		float sy = (float) 2 * h / icon.getHeight();
		m.setScale(sx, sy);
		// 重新构造一个2h*2h的图片
		return Bitmap.createBitmap(icon, 0, 0,icon.getWidth(), icon.getHeight(), m, false);
	}
	
	
	/**
	 * 
	 * @param fileName
	 * @return byte[]
	 */
	public static  byte[] readFile(String fileName)
	{
		FileInputStream fis=null;
		ByteArrayOutputStream baos=null;
		byte[] data=null;
		try {
			fis=new FileInputStream(fileName);
			byte[] buffer=new byte[8*1024];
			int readSize=-1;
			baos=new ByteArrayOutputStream();
			while((readSize=fis.read(buffer))!=-1)
			{
				baos.write(buffer, 0, readSize);
			}
			return baos.toByteArray();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally
		{
			try {
				if (fis!=null)
				{
					fis.close();
				}
				if (baos!=null)
					baos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return data;
		
	}
	
	/**
	 * 
	 * @param data 数据
	 * @param path 路径
	 * @param fileName 文件名
	 * @return true成功 false失败
	 */
	public static boolean writeToSdcard(byte[]data,String path,String fileName)
	{
		FileOutputStream fos=null;
		try {
				//判断有没有文件夹
				File filePath=new File(path);
				if(!filePath.exists())
				{
					//创建文件夹
					filePath.mkdirs();
				}
				
				//判断有没有同名的文件
				File file=new File(path+fileName);
				//有的话，删除
				if (file.exists())
				{
					file.delete();
				}
				//写文件
				fos=new FileOutputStream(file);
				fos.write(data);
				fos.flush();
				return true;
	//		}	
			 
		} catch (Exception e) {
			return false;
			// TODO: handle exception
		}finally
		{
			try {
				if (fos!=null)
					fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


}
