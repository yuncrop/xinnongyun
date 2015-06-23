package com.xinnongyun.adapter;

import net.simonvt.numberpicker.NumberPicker;

public class DatePickerAdapter {

	public static String str1 = "2015",str2 = "1",str3 = "1";
	public static void yearMouthDay(final NumberPicker yearPicker,final NumberPicker mouthPicker,final NumberPicker dayPicker)
	{
		
		yearPicker.setMaxValue(2299);
		yearPicker.setMinValue(1970);
		
		yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {  
            
            @Override  
            public void onValueChange(NumberPicker arg0, int arg1, int arg2) {  
                str1 = yearPicker.getValue() + "";  
                if (Integer.parseInt(str1) % 4 == 0  
                        && Integer.parseInt(str1) % 100 != 0  
                        || Integer.parseInt(str1) % 400 == 0) {  
                    if(str2.equals("1")||str2.equals("3")||str2.equals("5")||str2.equals("7")||str2.equals("8")||str2.equals("10")||str2.equals("12")){  
                        dayPicker.setMaxValue(31);  
                        dayPicker.setMinValue(1);  
                    }else if(str2.equals("4")||str2.equals("6")||str2.equals("9")||str2.equals("11")){  
                        dayPicker.setMaxValue(30);  
                        dayPicker.setMinValue(1);  
                    }else{  
                            dayPicker.setMaxValue(29);  
                            dayPicker.setMinValue(1);  
                        }  
                      
                } else {  
                    if(str2.equals("1")||str2.equals("3")||str2.equals("5")||str2.equals("7")||str2.equals("8")||str2.equals("10")||str2.equals("12")){  
                        dayPicker.setMaxValue(31);  
                        dayPicker.setMinValue(1);  
                    }else if(str2.equals("4")||str2.equals("6")||str2.equals("9")||str2.equals("11")){  
                        dayPicker.setMaxValue(30);  
                        dayPicker.setMinValue(1);  
                    }else{  
                            dayPicker.setMaxValue(28);  
                            dayPicker.setMinValue(1);  
                        }  
                }  
  
            }  
        });  
  
        mouthPicker.setMaxValue(12);  
        mouthPicker.setMinValue(1);  
        mouthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {  
              
            @Override  
            public void onValueChange(NumberPicker arg0, int arg1, int arg2) {  
                // TODO Auto-generated method stub  
                str2 = mouthPicker.getValue()+"";  
                if(str2.equals("1")||str2.equals("3")||str2.equals("5")||str2.equals("7")||str2.equals("8")||str2.equals("10")||str2.equals("12")){  
                    dayPicker.setMaxValue(31);  
                    dayPicker.setMinValue(1);  
                }else if(str2.equals("4")||str2.equals("6")||str2.equals("9")||str2.equals("11")){  
                    dayPicker.setMaxValue(30);  
                    dayPicker.setMinValue(1);  
                }else{  
                    if (Integer.parseInt(str1) % 4 == 0  
                            && Integer.parseInt(str1) % 100 != 0  
                            || Integer.parseInt(str1) % 400 == 0) {  
                        dayPicker.setMaxValue(29);  
                        dayPicker.setMinValue(1);  
                    } else {  
                        dayPicker.setMaxValue(28);  
                        dayPicker.setMinValue(1);  
                    }  
                }  
            }  
        });  
        dayPicker.setMaxValue(31);  
        dayPicker.setMinValue(1);  
        dayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {  
              
            @Override  
            public void onValueChange(NumberPicker arg0, int arg1, int arg2) {  
                // TODO Auto-generated method stub  
                str3 = dayPicker.getValue()+"";  
            }  
        });  
	}
}
