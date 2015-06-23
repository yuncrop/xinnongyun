package com.xinnongyun.view;

/**
 * 开关状态改变监听事件
 * 
 * @author Administrator
 * 
 */
public interface OnToggleStateChangeListener {
	/**
	 * 当开关状态改变回调此方法
	 * 
	 * @param b
	 *            当前开关的最新状态
	 */
	void onToggleStateChange(boolean b);
}
