package com.xinnongyun.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class IconFontTextView extends TextView {

	public IconFontTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initView();
	}

	private Context mContext;  
	  
	public IconFontTextView(Context context) {
		super(context);
		mContext = context;
		initView();
	}

	public IconFontTextView(android.content.Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}
  
    private void initView()  
    {  
        Typeface iconfont = Typeface.createFromAsset(mContext.getAssets(), "iconfont.ttf");  
        setTypeface(iconfont);  
    }  
}
