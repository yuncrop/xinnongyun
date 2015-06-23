package com.xinnongyun.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xinnongyun.activity.R;

public class MySurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {

	// 背景图
	private Bitmap BackgroundImage;
	// 问号图
	private Bitmap QuestionImage;

	SurfaceHolder Holder;

	public MySurfaceView(Context context) {
		super(context);
		BackgroundImage = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		QuestionImage = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);

		Holder = this.getHolder();// 获取holder
		Holder.addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// 启动自定义线程
		new Thread(new MyThread()).start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	// 自定义线程类
	class MyThread implements Runnable {

		@Override
		public void run() {
			Canvas canvas = null;
			int rotate = 0;// 旋转角度变量
			while (true) {
				try {
					canvas = Holder.lockCanvas();// 获取画布
					Paint mPaint = new Paint();
					// 绘制背景
					canvas.drawBitmap(BackgroundImage, 0, 0, mPaint);
					// 创建矩阵以控制图片旋转和平移
					Matrix m = new Matrix();
					// 设置旋转角度
					m.postRotate((rotate += 48) % 360,
							QuestionImage.getWidth() / 2,
							QuestionImage.getHeight() / 2);
					// 设置左边距和上边距
					m.postTranslate(47, 47);
					// 绘制问号图
					canvas.drawBitmap(QuestionImage, m, mPaint);
					// 休眠以控制最大帧频为每秒约30帧
					Thread.sleep(33);
				} catch (Exception e) {
				} finally {
					Holder.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
				}
			}
		}

	}

}
