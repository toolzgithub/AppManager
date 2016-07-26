package net.sunniwell.appmanager.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import net.sunniwell.appmanager.bean.AppInfo;

/**
 * 功能：获取应用信息的工具类
 * 
 * @author 郑鹏超
 */
public class AppInfoProvider {

	/**
	 * 功能：获取应用信息对象
	 * 
	 * @author 郑鹏超
	 * @时间 2016年7月26日
	 */
	public static List<AppInfo> getAppInfo(Context context) {
		PackageManager pm = context.getPackageManager();
		List<AppInfo> infos = new ArrayList<AppInfo>();
		// 获取所有安装的包信息
		List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
		for (PackageInfo packageInfo : installedPackages) {
			String packageName = packageInfo.packageName;// 包名
			// 获取应用信息 ApplicationInfo对应的 manifest里的application节点
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			String appName = applicationInfo.loadLabel(pm).toString();// 获取应用名称
			Drawable icon = applicationInfo.loadIcon(pm);// 获取应用图标
			AppInfo info = new AppInfo(appName, icon,packageName);
			infos.add(info);
		}
		return infos;
	}

}
