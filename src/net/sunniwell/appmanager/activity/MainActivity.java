package net.sunniwell.appmanager.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import net.sunniwell.appmanager.R;
import net.sunniwell.appmanager.bean.AppInfo;
import net.sunniwell.appmanager.engine.AppInfoProvider;

/**
 * 软件管理器的主类，负责加载应用列表，卸载应用功能
 * 
 * @author 郑鹏超
 */
public class MainActivity extends Activity {

	private ListView mLvMain;// 应用程序列表
	/* private List<String> test = new ArrayList<>();// 测试数据 */
	private List<AppInfo> mAppInfos = new ArrayList<>();// 应用信息对象
	private List<AppInfo> mUserAppInfos = new ArrayList<>();// 用户应用信息对象
	private List<AppInfo> mSysAppInfos = new ArrayList<>();// 系统应用信息对象
	private AppManagerAdapter mAppManagerAdapter;
	private AppReceiver mAppReceiver;
	private RadioGroup mRgSorted;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		setSortedListener();
		initData();
		setLvMainListener();
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
		mRgSorted = (RadioGroup) findViewById(R.id.rg_sorted);
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
		for (AppInfo appInfo : mAppInfos) {
			if (appInfo.isSys()) {// 是系统应用
				mSysAppInfos.add(appInfo);
			} else {
				mUserAppInfos.add(appInfo);
			}
		}
		mAppInfos.clear();
		mAppInfos.addAll(mUserAppInfos);
		mAppManagerAdapter = new AppManagerAdapter(mAppInfos);
		mLvMain.setAdapter(mAppManagerAdapter);
	}

	/**
	 * 功能：ListView监听事件
	 * 
	 * @author 郑鹏超
	 * @时间 2016年7月26日
	 */
	private void setLvMainListener() {
		mLvMain.setOnItemLongClickListener(new LvMainItemLongListener());// 卸载
		mLvMain.setOnItemClickListener(new LvMainItemListener());// 跳转
	}

	/**
	 * 功能：用户应用，系统应用切换监听
	 * 
	 * @author 郑鹏超
	 * @时间 2016年8月12日
	 */
	private void setSortedListener() {
		mRgSorted.setOnCheckedChangeListener(new SortedClickListener());
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
	 * 功能：系统应用不能卸载窗口
	 * 
	 * @author 郑鹏超
	 * @时间 2016年8月12日
	 */
	public void showNoUninstallDialog(AppInfo appInfo) {
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setMessage("“" + appInfo.getAppName() + "”不能卸载！");
		builder.setTitle("警告");
		builder.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
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
	 * 功能：LvMain的单条目长按监听，用于卸载
	 * 
	 * @author 郑鹏超
	 */
	private class LvMainItemLongListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			AppInfo appInfo = (AppInfo) parent.getItemAtPosition(position);
			if (appInfo.isSys() || appInfo.getPackageName().equals(getApplicationInfo().packageName)) {
				showNoUninstallDialog(appInfo);
			} else {
				showUninstallDialog(appInfo);
			}
			return true;
		}

	}

	/**
	 * 功能：LvMain的单条目点击监听，用于跳转
	 * 
	 * @author 郑鹏超
	 */
	private class LvMainItemListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			AppInfo appInfo = (AppInfo) parent.getItemAtPosition(position);
			try {
				// 获取点击程序的程序入口的Intent
				Intent intent = MainActivity.this.getPackageManager().getLaunchIntentForPackage(appInfo.getPackageName());
				startActivity(intent);
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "无法打开", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 功能：用于切换用户应用和系统应用
	 * 
	 * @author 郑鹏超
	 * @时间 2016年8月12日
	 */
	private class SortedClickListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {

			switch (checkedId) {
			case R.id.rb_user:// 点击用户应用时，加载用户应用信息集合
				mAppInfos.clear();
				mAppInfos.addAll(mUserAppInfos);
				break;
			case R.id.rb_sys:// 点击系统应用时，加载系统应用信息集合
				mAppInfos.clear();
				mAppInfos.addAll(mSysAppInfos);
				break;

			default:
				break;
			}
			mAppManagerAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 功能：用来接收应用卸载与安装的广播
	 * 
	 * @author 郑鹏超
	 */
	private class AppReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 接收安装广播
			if (TextUtils.equals(intent.getAction(), "android.intent.action.PACKAGE_ADDED")) {
				/*
				 * String packageName = intent.getDataString();
				 * System.out.println("安装了:" + packageName + "包名的程序");
				 */
				AppInfo appInfo = AppInfoProvider.getAppInfo(context, intent.getDataString().replace("package:", ""));
				mUserAppInfos.add(appInfo);
				if (mRgSorted.getCheckedRadioButtonId() == R.id.rb_user) {
					mAppInfos.add(appInfo);
					mAppManagerAdapter.notifyDataSetChanged();// 刷新界面
				}
			}
			// 接收卸载广播
			if (TextUtils.equals(intent.getAction(), "android.intent.action.PACKAGE_REMOVED")) {
				String packageName = intent.getDataString();
				/* System.out.println("卸载了:" + packageName + "包名的程序"); */
				packageName = packageName.replace("package:", "");// 去掉字符串中的package:
				ListIterator<AppInfo> userListIterator = mUserAppInfos.listIterator();
				while (userListIterator.hasNext()) {
					AppInfo info = userListIterator.next();
					if (TextUtils.equals(packageName, info.getPackageName())) {
						userListIterator.remove();
					}
				}
				// 如果当前列表是用户应用列表，则刷新界面
				if (mRgSorted.getCheckedRadioButtonId() == R.id.rb_user) {
					ListIterator<AppInfo> listIterator = mAppInfos.listIterator();
					while (listIterator.hasNext()) {
						AppInfo info = listIterator.next();
						if (TextUtils.equals(packageName, info.getPackageName())) {
							listIterator.remove();
						}
					}
					mAppManagerAdapter.notifyDataSetChanged();// 刷新界面
				}
			}
		}
	}
}
