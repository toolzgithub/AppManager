package net.sunniwell.appmanager.bean;

import android.graphics.drawable.Drawable;

/**
 * 应用程序类，包括了程序相关属性
 * 
 * @author 郑鹏超
 * @时间 2016年7月25号 
 */
public class AppInfo {
	private Drawable icon;
	private String appName;

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

}
