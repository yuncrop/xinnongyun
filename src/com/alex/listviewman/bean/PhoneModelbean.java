package com.alex.listviewman.bean;
public class PhoneModelbean 
{
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	private String id;
	private String	imgSrc;		    // 显示图片
	private String	name;			// 显示的数据
	private String	sortLetters;	// 显示数据拼音的首字母
	private String date;            //时间
	private String nameSecond;       //显示的数据
	
	
	public String getDate() {
		return date;
	}

	public String getNameSecond() {
		return nameSecond;
	}

	public void setNameSecond(String nameSecond) {
		this.nameSecond = nameSecond;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getImgSrc() {
		return imgSrc;
	}

	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
}
