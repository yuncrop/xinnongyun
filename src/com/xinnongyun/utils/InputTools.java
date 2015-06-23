package com.xinnongyun.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

//输入法框的控制
public class InputTools {

	//隐藏虚拟键盘
    public static void HideKeyboard(View v)
    {
        InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );     
      if ( imm.isActive( ) ) {     
          imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );
      }    
    }
}
