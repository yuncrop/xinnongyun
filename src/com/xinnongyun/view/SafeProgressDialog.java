package com.xinnongyun.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

public class SafeProgressDialog extends Dialog
{
    Activity mParentActivity;
    public SafeProgressDialog(Context context)
    {
        super(context);
        mParentActivity = (Activity) context;
    }
 
    public SafeProgressDialog(Context context,int id)
    {
        super(context,id);
        mParentActivity = (Activity) context;
    }
    
    @Override
    public void dismiss()
    {
        if (mParentActivity != null && !mParentActivity.isFinishing())
        {
            super.dismiss();    //调用超类对应方法
        }
    }
}