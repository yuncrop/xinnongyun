package com.xinnongyun.imageload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;


public class ImageDownloadHelper {

	private static final String TAG = "ImageDownloaderHelper";
	private static ImageDownloadHelper helper = new ImageDownloadHelper();

	public static ImageDownloadHelper getInstance() {
		return helper;
	}
	private HashMap<String, MyAsyncTask> map = new HashMap<String, MyAsyncTask>();
	private Map<String, SoftReference<Bitmap>> softCaches = new LinkedHashMap<String, SoftReference<Bitmap>>();
	//private LruCache<String, Bitmap>  lruCache  = new MyLruCache((int) Runtime.getRuntime().maxMemory()/8);
	private LruCache<String, Bitmap>  lruCache  = new MyLruCache(8*1024*1024);
	private ImageDownloadHelper() {
		
	}

	/**
	 * 
	 * 
	 * 
	 * @param context
	 * 
	 * @param url
	 * 
	 *            该mImageView对应的url
	 * 
	 * @param mImageView
	 * 
	 * @param path
	 * 
	 *            文件存储路径
	 * 
	 * @param downloadListener
	 * 
	 *            OnImageDownload回调接口，在onPostExecute()中被调用
	 */

	public void imageDownload(String type,Context context, String url,

	ImageView mImageView, String path,

	OnImageDownloadListener downloadListener) {
		
		Bitmap bitmap = null;
		// 先从强引用中拿数据
		if (lruCache != null) {
			bitmap = lruCache.get(url);
		}

		if (bitmap != null && url.equals(mImageView.getTag())) {
			Log.i(TAG, "==从强引用中找到数据");
			if(type!=null)
			{
				downloadListener.onImageDownload(bitmap, url);
			}
			else
			mImageView.setImageBitmap(bitmap);
		} else {
			SoftReference<Bitmap> softReference = softCaches.get(url);
			if (softReference != null) {
				bitmap = softReference.get();
			}

			// 从软引用中拿数据
			if (bitmap != null && url.equals(mImageView.getTag())) {
				Log.i(TAG, "==从软引用中找到数据");
				// 添加到强引用中
				lruCache.put(url, bitmap);
				Log.i(TAG, "==添加到强引用中");
				// 从软引用集合中移除
				softCaches.remove(url);
				if(type!=null)
				{
					downloadListener.onImageDownload(bitmap, url);
				}
				else
				mImageView.setImageBitmap(bitmap);

			} else {

				// 从文件缓存中拿数据
				String imageName = "";
				if (url != null) {
					imageName = ImageDownloaderUtil.getInstance().getImageName(
							url);
				}
				bitmap = getBitmapFromFile(context, imageName, path);
				if (bitmap != null && url.equals(mImageView.getTag())) {
					Log.i(TAG, "==从文件缓存中找到数据");
					// 放入强缓存
					lruCache.put(url, bitmap);
					if(type!=null)
					{
						downloadListener.onImageDownload(bitmap, url);
					}
					else
					mImageView.setImageBitmap(bitmap);
				} else {
					// 从网络中拿数据
					if (url != null && needCreateNewTask(mImageView)) {

						MyAsyncTask task = new MyAsyncTask(context, url, path, downloadListener);

						Log.i(TAG, "==从网络中拿数据");

						if (mImageView != null) {

							task.execute();

							// 将对应的url对应的任务存起来

							map.put(url, task);

						}

					}

				}

			}

		}

	}

	/**
	 * 
	 * 判断是否需要重新创建线程下载图片，如果需要，返回值为true。
	 * 
	 * 
	 * 
	 * @param url
	 * 
	 * @param mImageView
	 * 
	 * @return
	 */

	private boolean needCreateNewTask(ImageView mImageView) {
		boolean b = true;
		if (mImageView != null) {
			String curr_task_url = (String) mImageView.getTag();
			if (isTasksContains(curr_task_url)) {
				b = false;
			}
		}
		return b;
	}

	/**
	 * 
	 * 检查该url（最终反映的是当前的ImageView的tag，tag会根据position的不同而不同）对应的task是否存在
	 * 
	 * 
	 * 
	 * @param url
	 * 
	 * @return
	 */

	private boolean isTasksContains(String url) {
		boolean b = false;
		if (map != null && map.get(url) != null) {
			b = true;
		}
		return b;
	}

	/**
	 * 
	 * 删除map中该url的信息，这一步很重要，不然MyAsyncTask的引用会“一直”存在于map中
	 * 
	 * 
	 * 
	 * @param url
	 */

	private void removeTaskFromMap(String url) {
		if (url != null && map != null && map.get(url) != null) {
			map.remove(url);
			Log.i(TAG, "当前map的大小==" + map.size());
		}
	}

	/**
	 * 
	 * 从文件中拿图片
	 * 
	 * 
	 * 
	 * @param mActivity
	 * 
	 * @param imageName
	 * 
	 *            图片名字
	 * 
	 * @param path
	 * 
	 *            图片路径
	 * 
	 * @return
	 */

	private Bitmap getBitmapFromFile(Context context, String imageName,
			String path) {
		Bitmap bitmap = null;
		if (imageName != null) {
			File file = null;
			String real_path = "";
			try {
				if (ImageDownloaderUtil.getInstance().hasSDCard()) {
					real_path = ImageDownloaderUtil.getInstance().getExtPath()
							+ (path != null && path.startsWith("/") ? path
									: "/" + path);
				} else {
					real_path = ImageDownloaderUtil.getInstance()
							.getPackagePath(context)
							+ (path != null && path.startsWith("/") ? path
									: "/" + path);
				}
				file = new File(real_path, imageName);
				if (file.exists())
				{
					FileInputStream fileInput = new FileInputStream(file);
					bitmap = BitmapFactory.decodeStream(fileInput);
					fileInput.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
				bitmap = null;
			}
		
		}
		return bitmap;

	}

	/**
	 * 
	 * 将下载好的图片存放到文件中
	 * 
	 * 
	 * 
	 * @param path
	 * 
	 *            图片路径
	 * 
	 * @param mActivity
	 * 
	 * @param imageName
	 * 
	 *            图片名字
	 * 
	 * @param bitmap
	 * 
	 *            图片
	 * 
	 * @return
	 */
	private boolean setBitmapToFile(String path, Context mActivity,
			String imageName, Bitmap bitmap) {
		File file = null;
		String real_path = "";
		FileOutputStream fos = null;
		try {
			if (ImageDownloaderUtil.getInstance().hasSDCard()) {
				real_path = ImageDownloaderUtil.getInstance().getExtPath()
						+ (path != null && path.startsWith("/") ? path : "/"
								+ path);
			} else {
				real_path = ImageDownloaderUtil.getInstance().getPackagePath(
						mActivity)
						+ (path != null && path.startsWith("/") ? path : "/"
								+ path);
			}

			file = new File(real_path, imageName);

			if (!file.exists()) {

				File file2 = new File(real_path + "/");

				file2.mkdirs();

			}

			file.createNewFile();

			

			if (ImageDownloaderUtil.getInstance().hasSDCard()) {

				fos = new FileOutputStream(file);

			} else {

				fos = mActivity.openFileOutput(imageName, Context.MODE_PRIVATE);

			}

			if (imageName != null

			&& (imageName.contains(".png") || imageName

			.contains(".PNG"))) {

				bitmap.compress(Bitmap.CompressFormat.PNG, 20, fos);

			} else {

				bitmap.compress(Bitmap.CompressFormat.JPEG, 20, fos);

			}

			fos.flush();

			
			return true;
		} catch (Exception e) {
			return false;

		}
		finally
		{
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					
				}

			}
		}

	}

	/**
	 * 
	 * 辅助方法，一般不调用
	 * 
	 * 
	 * 
	 * @param path
	 * 
	 * @param mActivity
	 * 
	 * @param imageName
	 */

	private void removeBitmapFromFile(String path, Context mActivity,

	String imageName) {

		File file = null;

		String real_path = "";

		try {

			if (ImageDownloaderUtil.getInstance().hasSDCard()) {

				real_path = ImageDownloaderUtil.getInstance().getExtPath()

				+ (path != null && path.startsWith("/") ? path : "/"

				+ path);

			} else {

				real_path = ImageDownloaderUtil.getInstance().getPackagePath(

				mActivity)

				+ (path != null && path.startsWith("/") ? path : "/"

				+ path);

			}

			file = new File(real_path, imageName);

			if (file != null)

				file.delete();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	/**
	 * 
	 * 异步下载图片的方法
	 * 
	 * 
	 * 
	 * @author
	 * 
	 * 
	 */

	private class MyAsyncTask extends AsyncTask<String, Void, Bitmap> {

		private Context context;

		private String url;

		private OnImageDownloadListener downloadListener;

		private String path;

		public MyAsyncTask(Context context, String url,

		String path, OnImageDownloadListener downloadListener) {

			this.context = context;

			this.url = url;
			this.path = path;

			this.downloadListener = downloadListener;

		}

		@Override
		protected Bitmap doInBackground(String... params) {

			Bitmap bm = null;

			if (url != null) {

				try {

					Log.i(TAG, "==访问网络加载图片");

					URL urlObj = new URL(url);

					HttpURLConnection httpConn = (HttpURLConnection) urlObj

					.openConnection();

					httpConn.setDoInput(true);

					httpConn.setRequestMethod("GET");

					httpConn.connect();

					if (httpConn.getResponseCode() == 200) {

						InputStream is = httpConn.getInputStream();

						bm = BitmapFactory.decodeStream(is);

					}

					String imageName = ImageDownloaderUtil.getInstance()

					.getImageName(url);

					if (!setBitmapToFile(path, context, imageName, bm)) {

						removeBitmapFromFile(path, context, imageName);

					}

					// 放入强缓存

					lruCache.put(url, bm);

					Log.i(TAG, "==放入强缓存ok");

				} catch (Exception e) {

					e.printStackTrace();

				}

			}

			return bm;

		}

		@Override
		protected void onPostExecute(Bitmap result) {

			// 回调设置图片

			if (downloadListener != null) {

				downloadListener.onImageDownload(result, url);

				// 该url对应的task已经下载完成，从map中将其删除

				removeTaskFromMap(url);

			}

			super.onPostExecute(result);

		}

	}

	public interface OnImageDownloadListener {

		void onImageDownload(Bitmap bitmap, String imgUrl);

	}

	// 定义强引用缓存

	class MyLruCache extends LruCache<String, Bitmap> {

		public MyLruCache(int maxSize) {

			super(maxSize);

		}

		@Override
		protected int sizeOf(String key, Bitmap value) {

			 return value.getHeight() * value.getWidth() * 4;

			// Bitmap图片的一个像素是4个字节
			//return value.getByteCount();

		}

		@Override
		protected void entryRemoved(boolean evicted, String key,

		Bitmap oldValue, Bitmap newValue) {

			if (evicted) {

				SoftReference<Bitmap> softReference = new SoftReference<Bitmap>(

				oldValue);

				softCaches.put(key, softReference);

			}

		}

	}

	static class ImageDownloaderUtil {

		private static ImageDownloaderUtil util;

		private ImageDownloaderUtil() {

		}

		public static ImageDownloaderUtil getInstance() {

			if (util == null) {

				util = new ImageDownloaderUtil();

			}

			return util;

		}

		/**
		 * 
		 * 判断是否有sdcard
		 * 
		 * 
		 * 
		 * @return
		 */

		public boolean hasSDCard() {

			boolean b = false;

			if (Environment.MEDIA_MOUNTED.equals(Environment

			.getExternalStorageState())) {

				b = true;

			}

			return b;

		}

		/**
		 * 
		 * 得到sdcard路径
		 * 
		 * 
		 * 
		 * @return
		 */

		public String getExtPath() {

			String path = "";

			if (hasSDCard()) {

				path = Environment.getExternalStorageDirectory().getPath();

			}

			return path;

		}

		/**
		 * 
		 * 得到包目录
		 * 
		 * 
		 * 
		 * @param mActivity
		 * 
		 * @return
		 */

		public String getPackagePath(Context mActivity) {

			return mActivity.getFilesDir().toString();

		}

		/**
		 * 
		 * 根据url得到图片名
		 * 
		 * 
		 * 
		 * @param url
		 * 
		 * @return
		 */

		public String getImageName(String url) {

			String imageName = "";

			if (url != null) {

				imageName = url.substring(url.lastIndexOf("/") + 1);

			}

			return imageName;

		}

	}

}