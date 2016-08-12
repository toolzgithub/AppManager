package net.sunniwell.appmanager.bean;

import android.graphics.drawable.Drawable;

/**
 * 应用程序类，包括了程序相关属性
 * 
 * @author 郑鹏超
 */
public class AppInfo {
	private Drawable icon;
	private String appName;
	private String packageName;
	private boolean isSys;

	public AppInfo(String appName, Drawable icon, String packageName, boolean isSys) {
		super();
		this.appName = appName;
		this.icon = icon;
		this.packageName = packageName;
		this.isSys = isSys;
	}

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

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public boolean isSys() {
		return isSys;
	}
	
}
