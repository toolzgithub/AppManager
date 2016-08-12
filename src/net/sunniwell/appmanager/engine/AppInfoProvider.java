package net.sunniwell.appmanager.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import net.sunniwell.appmanager.R;
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
			boolean isSys;// 是否是系统应用
			if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				// 是系统应用
				isSys = true;
			} else {
				isSys = false;
			}
			AppInfo info = new AppInfo(appName, icon, packageName, isSys);
			infos.add(info);
		}
		return infos;
	}

	/**
	 * 功能：根据包名获取应用信息对象
	 * 
	 * @author 郑鹏超
	 * @时间 2016年7月26日
	 */
	public static AppInfo getAppInfo(Context context, String packageName) {
		AppInfo info = null;
		PackageManager pm = context.getPackageManager();
		ApplicationInfo applicationInfo;
		try {
			// 获取应用信息 ApplicationInfo对应的 manifest里的application节点
			applicationInfo = pm.getApplicationInfo(packageName, 0);
			String appName = applicationInfo.loadLabel(pm).toString();// 获取应用名称
			Drawable icon = applicationInfo.loadIcon(pm);// 获取应用图标
			boolean isSys;// 是否是系统应用
			if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				// 是系统应用
				isSys = true;
			} else {
				isSys = false;
			}
			info = new AppInfo(appName, icon, packageName, isSys);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (info == null) {
			return new AppInfo("error", null, packageName, true);
		}
		return info;
	}

}
