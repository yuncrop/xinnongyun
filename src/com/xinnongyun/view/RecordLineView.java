package com.xinnongyun.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

import com.xinnongyun.activity.R;
import com.xinnongyun.activity.VideoRecordActivity;

public class RecordLineView extends View {

	private Paint paint;
	public RecordLineView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RecordLineView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		paint = new Paint();// new一个画笔
		paint.setStrokeWidth(32);
		int lineToLength = 0;
		int recordTimesCount = VideoRecordActivity.splitTime.size();
		if(recordTimesCount == 0)
		{
			if(VideoRecordActivity.currentTimeLength == 0)
			{
				paint.setColor(getResources().getColor(R.color.bg_clolr_white));// 设置画笔颜色
				paint.setStyle(Style.FILL);// 设置画笔填充
				canvas.drawLine(0, 0, 8, 0, paint);
				lineToLength = 8;
			}
			else
			{
				paint.setColor(getResources().getColor(R.color.text_green));// 设置画笔颜色
				paint.setStyle(Style.FILL);// 设置画笔填充
				lineToLength = VideoRecordActivity.srceenWidth/120 * VideoRecordActivity.currentTimeLength;
				canvas.drawLine(0, 0, lineToLength , 0, paint);
				paint.setColor(getResources().getColor(R.color.bg_clolr_white));// 设置画笔颜色
				canvas.drawLine(lineToLength, 0, lineToLength+8, 0, paint);
				lineToLength += 8;
			}
			
		}else
		{
			if(!VideoRecordActivity.isDeletedLast)
			{
				if(recordTimesCount== 1 && VideoRecordActivity.currentTimeLength==VideoRecordActivity.splitTime.get(0))
				{
					paint.setColor(getResources().getColor(R.color.text_green));// 设置画笔颜色
					paint.setStyle(Style.FILL);// 设置画笔填充
					lineToLength = VideoRecordActivity.srceenWidth/120 * VideoRecordActivity.splitTime.get(0);
					canvas.drawLine(0, 0, lineToLength , 0, paint);
					paint.setColor(getResources().getColor(R.color.bg_clolr_white));// 设置画笔颜色
					canvas.drawLine(lineToLength, 0, lineToLength + 8, 0, paint);
					lineToLength+=8;
				}
				else
				{
					
					for(int i=0;i<recordTimesCount;i++)
					{
						paint.setColor(getResources().getColor(R.color.text_green));// 设置画笔颜色
						paint.setStyle(Style.FILL);// 设置画笔填充
						canvas.drawLine(lineToLength, 0, VideoRecordActivity.srceenWidth/120 * VideoRecordActivity.splitTime.get(i), 0, paint);
						paint.setColor(getResources().getColor(R.color.transparent));// 设置画笔颜色
						lineToLength = VideoRecordActivity.srceenWidth/120 * VideoRecordActivity.splitTime.get(i);
						canvas.drawLine(lineToLength, 0, lineToLength+4 , 0, paint);
						lineToLength+=4;
					}
					if(VideoRecordActivity.currentTimeLength==VideoRecordActivity.splitTime.get(recordTimesCount-1))
					{
						paint.setColor(getResources().getColor(R.color.bg_clolr_white));// 设置画笔颜色
						canvas.drawLine(lineToLength, 0, lineToLength + 8, 0, paint);
						lineToLength+=8;
					}else
					{
						paint.setColor(getResources().getColor(R.color.text_green));// 设置画笔颜色
						paint.setStyle(Style.FILL);// 设置画笔填充
						
						canvas.drawLine(lineToLength, 0, VideoRecordActivity.srceenWidth/120 * VideoRecordActivity.currentTimeLength , 0, paint);
						lineToLength = VideoRecordActivity.srceenWidth/120 * VideoRecordActivity.currentTimeLength;
						paint.setColor(getResources().getColor(R.color.bg_clolr_white));// 设置画笔颜色
						canvas.drawLine(lineToLength, 0, lineToLength + 8, 0, paint);
						lineToLength+=8;
					}
					
				}
			}else
			{
				if(recordTimesCount== 1)
				{
					paint.setColor(getResources().getColor(R.color.color_ff5842));// 设置画笔颜色
					paint.setStyle(Style.FILL);// 设置画笔填充
					lineToLength = VideoRecordActivity.srceenWidth/120 * VideoRecordActivity.splitTime.get(0);
					canvas.drawLine(0, 0, lineToLength , 0, paint);
				}
				else
				{
					for(int i=0;i<recordTimesCount-1;i++)
					{
						paint.setColor(getResources().getColor(R.color.text_green));// 设置画笔颜色
						paint.setStyle(Style.FILL);// 设置画笔填充
						canvas.drawLine(lineToLength, 0, VideoRecordActivity.srceenWidth/120 * VideoRecordActivity.splitTime.get(i), 0, paint);
						paint.setColor(getResources().getColor(R.color.transparent));// 设置画笔颜色
						lineToLength = VideoRecordActivity.srceenWidth/120 * VideoRecordActivity.splitTime.get(i);
						canvas.drawLine(lineToLength, 0, lineToLength+4 , 0, paint);
						lineToLength+=4;
					}
					paint.setColor(getResources().getColor(R.color.color_ff5842));
					canvas.drawLine(lineToLength, 0, VideoRecordActivity.srceenWidth/120 * VideoRecordActivity.splitTime.get(recordTimesCount-1) , 0, paint);
					lineToLength+=4;
				}
			}
		}
		paint.setColor(getResources().getColor(R.color.camera_progress_split));// 设置画笔颜色
		canvas.drawLine(VideoRecordActivity.srceenWidth/120 *5, 0, VideoRecordActivity.srceenWidth/120 *5+4 , 0, paint);
	}
}
