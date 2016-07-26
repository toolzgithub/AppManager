package net.sunniwell.appmanager.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import net.sunniwell.appmanager.R;
import net.sunniwell.appmanager.R.drawable;
import net.sunniwell.appmanager.R.id;
import net.sunniwell.appmanager.R.layout;

/**
 * 软件管理器的主类，负责加载应用列表，卸载应用功能
 * 
 * @author 郑鹏超
 * @时间 2016年7月25号 09:40
 */
public class MainActivity extends Activity {

	private ListView mLvMain;// 应用程序列表
	private List<String> test = new ArrayList<>();// 测试数据
	private AppManagerAdapter mAppManagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initData();
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
		for (int i = 0; i < 50; i++) {
			test.add("这是测试数据第" + i + "条");
		}
		mAppManagerAdapter = new AppManagerAdapter(test);
		mLvMain.setAdapter(mAppManagerAdapter);
	}

	/**
	 * 功能：应用列表适配器
	 * 
	 * @author 郑鹏超
	 */
	private class AppManagerAdapter extends BaseAdapter {

		private List<String> appInfos;
		
		/**
		 * 功能：构造方法用来传递要显示的数据
		 * 
		 * @author 郑鹏超
		 * @时间 2016年7月25日
		 */
		public AppManagerAdapter(List<String> appInfos) {
			this.appInfos = appInfos;
		}

		@Override
		public int getCount() {
			return appInfos.size();
		}

		@Override
		public String getItem(int position) {
			return appInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(MainActivity.this, R.layout.itme_lv_main, null);
				AppManagerViews appManagerViews = new AppManagerViews();
				appManagerViews.mIvAppManagerIcon = (ImageView) convertView.findViewById(R.id.iv_app_manager_icon);
				appManagerViews.mTvAppManagerName = (TextView) convertView.findViewById(R.id.tv_app_manager_name);
				appManagerViews.mIvAppManagerIcon.setImageResource(R.drawable.ic_launcher);
				appManagerViews.mTvAppManagerName.setText(getItem(position));
				convertView.setTag(appManagerViews);
			} else {
				AppManagerViews appManagerViews = (AppManagerViews) convertView.getTag();
				appManagerViews.mIvAppManagerIcon.setImageResource(R.drawable.ic_launcher);
				appManagerViews.mTvAppManagerName.setText(getItem(position));
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
}
