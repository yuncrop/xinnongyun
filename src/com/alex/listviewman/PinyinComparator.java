package com.alex.listviewman;

import java.util.Comparator;

import com.alex.listviewman.bean.PhoneModelbean;


/**
 * 
 * @author Mr.Z
 */
/**继承比较器接口*/
public class PinyinComparator implements Comparator<PhoneModelbean> {

	public int compare(PhoneModelbean o1, PhoneModelbean o2) {
		if(o1.getSortLetters().equals("@") || o2.getSortLetters().equals("#")) {
			return -1;
		} else if(o1.getSortLetters().equals("#") || o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
