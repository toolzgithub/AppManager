package net.sunniwell.appmanager.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import net.sunniwell.appmanager.R;
import net.sunniwell.appmanager.bean.AppInfo;
import net.sunniwell.appmanager.engine.AppInfoProvider;

/**
 * 软件管理器的主类，负责加载应用列表，卸载应用功能
 * 
 * @author 郑鹏超
 * @时间 2016年7月25号 09:40
 */
public class MainActivity extends Activity {

	private ListView mLvMain;// 应用程序列表
	/* private List<String> test = new ArrayList<>();// 测试数据 */
	private List<AppInfo> mAppInfos = new ArrayList<>();// 应用信息对象
	private AppManagerAdapter mAppManagerAdapter;
	private AppReceiver mAppReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initData();
		click();
		registerBroadcastReceiver();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 解除注册卸载的广播接收者
		unregisterReceiver(mAppReceiver);
	}

	/**
	 * 功能： 初始化控件
	 * 
	 * @author 郑鹏超
	 * @时间 2016年7月25日
	 */
	private void initView() {
		mLvMain = (ListView) findViewById(R.id.lv_main);

	}

	/**
	 * 功能：初始化数据
	 * 
	 * @author 郑鹏超
	 * @时间 2016年7月25日
	 */
	private void initData() {
		// 测试数据
		/*
		 * for (int i = 0; i < 50; i++) { test.add("这是测试数据第" + i + "条"); }
		 * mAppManagerAdapter = new AppManagerAdapter(test);
		 */
		mAppInfos = AppInfoProvider.getAppInfo(getApplicationContext());
		mAppManagerAdapter = new AppManagerAdapter(mAppInfos);
		mLvMain.setAdapter(mAppManagerAdapter);
	}

	/**
	 * 功能：监听事件
	 * 
	 * @author 郑鹏超
	 * @时间 2016年7月26日
	 */
	private void click() {
		mLvMain.setOnItemLongClickListener(new LvMainItemLongListener());
	}

	/**
	 * 功能：注册一个广播接收者
	 * 
	 * @author 郑鹏超
	 * @时间 2016年7月26日
	 */
	private void registerBroadcastReceiver() {
		mAppReceiver = new AppReceiver();
		// 注册一个app卸载的广播接收者
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");// 必须添加
		registerReceiver(mAppReceiver, filter);
	}

	/**
	 * 功能：卸载窗口
	 * 
	 * @author 郑鹏超
	 * @时间 2016年7月26日
	 */
	private void showUninstallDialog(final AppInfo appInfo) {
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setMessage("你是否要卸载“" + appInfo.getAppName() + "”?");
		builder.setTitle("卸载");
		builder.setIcon(android.R.drawable.ic_menu_delete);
		builder.setPositiveButton("是", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showUninstallVerifyDialog(appInfo);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("否", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	/**
	 * 功能：卸载确认窗口，完成卸载功能
	 * 
	 * @author 郑鹏超
	 * @param appInfo
	 * @时间 2016年7月26日
	 */
	private void showUninstallVerifyDialog(final AppInfo appInfo) {
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setMessage("请确认卸载！");
		builder.setTitle("请确认");
		builder.setIcon(android.R.drawable.ic_menu_info_details);
		builder.setPositiveButton("确认", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				uninstall(appInfo.getPackageName());
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	/**
	 * 功能：卸载APP
	 * 
	 * @author 郑鹏超
	 * @时间 2016年7月26日
	 */
	private void uninstall(String packageName) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + packageName));
		startActivity(intent);
	}

	/**
	 * 功能：应用列表适配器
	 * 
	 * @author 郑鹏超
	 */
	private class AppManagerAdapter extends BaseAdapter {

		private List<AppInfo> appInfos;

		/**
		 * 功能：构造方法用来传递要显示的数据
		 * 
		 * @author 郑鹏超
		 * @时间 2016年7月25日
		 */
		public AppManagerAdapter(List<AppInfo> appInfos) {
			this.appInfos = appInfos;
		}

		@Override
		public int getCount() {
			return appInfos.size();
		}

		@Override
		public AppInfo getItem(int position) {
			return appInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appInfo = getItem(position);
			if (convertView == null) {
				convertView = View.inflate(MainActivity.this, R.layout.itme_lv_main, null);
				AppManagerViews appManagerViews = new AppManagerViews();
				appManagerViews.mIvAppManagerIcon = (ImageView) convertView.findViewById(R.id.iv_app_manager_icon);
				appManagerViews.mTvAppManagerName = (TextView) convertView.findViewById(R.id.tv_app_manager_name);
				appManagerViews.mIvAppManagerIcon.setImageDrawable(appInfo.getIcon());
				appManagerViews.mTvAppManagerName.setText(appInfo.getAppName());
				convertView.setTag(appManagerViews);
			} else {
				AppManagerViews appManagerViews = (AppManagerViews) convertView.getTag();
				appManagerViews.mIvAppManagerIcon.setImageDrawable(appInfo.getIcon());
				appManagerViews.mTvAppManagerName.setText(appInfo.getAppName());
			}
			return convertView;
		}

	}

	/**
	 * 功能：用来优化listview的类
	 * 
	 * @author 郑鹏超
	 */
	private class AppManagerViews {
		ImageView mIvAppManagerIcon;
		TextView mTvAppManagerName;
	}

	/**
	 * 功能：LvMain的但条目长按监听
	 * 
	 * @author 郑鹏超
	 */
	private class LvMainItemLongListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			AppInfo appInfo = (AppInfo) parent.getItemAtPosition(position);
			showUninstallDialog(appInfo);
			return true;
		}

	}

	/**
	 * 功能：用来接收应用卸载与安装的广播
	 * 
	 * @author 郑鹏超
	 */
	public class AppReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 接收安装广播
			if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
				String packageName = intent.getDataString();
				System.out.println("安装了:" + packageName + "包名的程序");
			}
			// 接收卸载广播
			if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
				String packageName = intent.getDataString();
				System.out.println("卸载了:" + packageName + "包名的程序");
			}
		}
	}
}
