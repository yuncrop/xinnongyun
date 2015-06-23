package com.xinnongyun.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.xinnongyun.activity.R;
import com.xinnongyun.config.YunTongXun;

public class ImageUtils {

	/*
	 * 转换图片成圆形
	 * 
	 * @param bitmap 传入Bitmap对象
	 * 
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	
	 /** 
     * 获取圆角位图的方法 
     * @param bitmap 需要转化成圆角的位图 
     * @param pixels 圆角的度数，数值越大，圆角越大 
     * @return 处理后的圆角位图 
     */  
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {  
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),  
                bitmap.getHeight(), Config.ARGB_8888);  
        Canvas canvas = new Canvas(output);  
        final int color = 0xff424242;  
        final Paint paint = new Paint();  
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
        final RectF rectF = new RectF(rect);  
        final float roundPx = pixels;  
        paint.setAntiAlias(true);  
        canvas.drawARGB(0, 0, 0, 0);  
        paint.setColor(color);  
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        canvas.drawBitmap(bitmap, rect, rect, paint);  
        return output;  
    } 
    
	/**
	 * 用字符串生成二维码
	 * 
	 * @param str
	 * @return
	 * @throws WriterException
	 */
	public static Bitmap Create2DCode(String str) {
		// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		BitMatrix matrix = null;
		;
		try {
			matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE,
					300, 300);
			int width = matrix.getWidth();
			int height = matrix.getHeight();
			// 二维矩阵转为一维像素数组,也就是一直横着排了
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (matrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					}

				}
			}
			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			// 通过像素数组生成bitmap,具体参考api
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			return bitmap;
		} catch (WriterException e) {
			return null;
		}
	}

	public static String saveImageToSd(String srcPath, int pictureLength,
			String savename) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = pictureLength;// 这里设置高度为800f
		float ww = pictureLength;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		saveMyBitmap(savename, bitmap);
		// return bitmap;// compressImage(bitmap);//压缩好比例大小后再进行质量压缩
		return YunTongXun.ImagePath + StringUtil.getFileNameFromUrl(srcPath);
	}

	// 保存图片
	public static String saveMyBitmap(String bitName, Bitmap mBitmap) {
		File fd = new File(YunTongXun.ImagePath);
		if (!fd.exists()) {
			fd.mkdirs();
		}
		File f = new File(YunTongXun.ImagePath + bitName);
		try {
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
		} catch (IOException e) {
			return "";
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			return "";
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			return "";
		}
		try {
			fOut.close();
		} catch (IOException e) {
			return "";
		}
		return YunTongXun.ImagePath + bitName;
	}

	
	
	public static void fileReName(String oldName,String newName)
	{
		File fd = new File(YunTongXun.ImagePath);
		if (!fd.exists()) {
			fd.mkdirs();
		}
		File f = new File(YunTongXun.ImagePath + oldName);
		if (f.exists()) {
			f.renameTo(new File(YunTongXun.ImagePath + newName));
		}
	}
	
	/**
	 * 得到表情图片
	 * @return 图片ids
	 */
	public static List<Integer> getAllQQFace() {
		
		
		try {
			Field[] drawableFileds = R.drawable.class.getFields();
			List<Integer> faceValues = new ArrayList<Integer>();
			for(Field field : drawableFileds)
			{
				if(field.getName().indexOf("face_")!=-1)
				{
					faceValues.add(field.getInt(R.drawable.class));
				}
			}
			return faceValues;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void addImageToEditView(int id, EditText editText)
	{
		 // 根据资源ID获得资源图像的Bitmap对象
		 Bitmap bitmap = BitmapFactory.decodeResource(editText.getResources(),
				 id);
		 // 根据Bitmap对象创建ImageSpan对象
		 ImageSpan imageSpan = new ImageSpan(editText.getContext(), bitmap);
		 // 创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
		 SpannableString spannableString = new SpannableString("face");
		 // 用ImageSpan对象替换face
		 spannableString.setSpan(imageSpan, 0, 4,
		 Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		 // 将随机获得的图像追加到EditText控件的最后
		 editText.append(spannableString);
	}
	
	// /**
	// * 普通字符串->表情
	// *
	// * @param bqId
	// * 原始字符串
	// * @return 表情资源id
	// */
	// public CharSequence toBiaoQing(int bqId) {
	// return Html.fromHtml("<img src=\"" + bqId + "\">", imageGetter, null);
	// }
	//
	// /** 用于根据资源获取图片id */
	// private ImageGetter imageGetter = new ImageGetter() {
	// @Override
	// public Drawable getDrawable(String source) {
	// int id = Integer.parseInt(source);
	// // 根据id从资源文件中获取图片对象
	// Drawable d = getResources().getDrawable(id);
	// // 以此作为标志位，方便外部取出对应的资源id
	// d.setState(new int[] { id });
	// d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
	// return d;
	// }
	// };
	
	public static int dip2px(Context context, float dipValue)

	{

		float m = context.getResources().getDisplayMetrics().density;

		return (int) (dipValue * m + 0.5f);

	}

	public static int px2dip(Context context, float pxValue)
	{
		float m = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / m + 0.5f);
	}

	public static int getFontSize(Context context, int pxSize) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int rate = (int) (pxSize * (float) screenWidth / 640);
        return rate;
    }

	/** 
     * 获取视频的缩略图 
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。 
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。 
     * @param videoPath 视频的路径 
     * @param width 指定输出视频缩略图的宽度 
     * @param height 指定输出视频缩略图的高度度 
     * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。 
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96 
     * @return 指定大小的视频缩略图 
     */  
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height) {  
        Bitmap bitmap = null;  
        // 获取视频的缩略图  
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MICRO_KIND);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
        return bitmap;  
    }  
	
}
