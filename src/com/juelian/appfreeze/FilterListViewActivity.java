package com.juelian.appfreeze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class FilterListViewActivity extends Activity {

	private ListView mListView;
	private List<AppInfo> mAppInfos;
	private PackageManager pm;
	private ListViewAdapter browseApplicationInfoAdapter;
	private String appPackNameString = "";
	private String appNameString = "";
	public SharedPreferences sp;
	private AppInfo appInfo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.applist_listview);
		sp = getSharedPreferences("wl", Context.MODE_WORLD_READABLE);
		mListView = (ListView) findViewById(R.id.all_app_listview);
		mAppInfos = queryFilterAppInfo(getIntent().getIntExtra("filter",
				MainActivity.ALL_APP));
		browseApplicationInfoAdapter = new ListViewAdapter(
				getApplicationContext(), mAppInfos);
		mListView.setAdapter(browseApplicationInfoAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				appInfo = mAppInfos.get(position);
				appPackNameString = appInfo.getPkgName();
				appNameString = appInfo.getAppLabel();
				if (sp.getInt(appPackNameString, 0) == 1) {
					confirmRemove();
				} else {
					confirmAdd();
				}
			}
		});
	}

	private List<AppInfo> queryFilterAppInfo(int filter) {
		pm = this.getPackageManager();
		List<ApplicationInfo> listAppcations = pm
				.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		Collections.sort(listAppcations,
				new ApplicationInfo.DisplayNameComparator(pm));// 排序
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		appInfos.clear();
		switch (filter) {
		case MainActivity.ALL_APP:
			for (ApplicationInfo app : listAppcations) {
				if (!app.packageName.equals(getBaseContext().getPackageName())) {
					appInfos.add(getAppInfo(app));
				}
			}
			break;
		case MainActivity.USERS_APP:
			for (ApplicationInfo app : listAppcations) {
				if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0
						&& (!app.packageName.equals(getBaseContext()
								.getPackageName()))) {
					appInfos.add(getAppInfo(app));
				}
			}
			break;
		}
		return appInfos;
	}

	private AppInfo getAppInfo(ApplicationInfo app) {
		AppInfo appInfo = new AppInfo();
		appInfo.setAppLabel((String) app.loadLabel(pm));
		appInfo.setAppIcon(app.loadIcon(pm));
		appInfo.setPkgName(app.packageName);
		return appInfo;
	}

	public void confirmAdd() {
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
		mBuilder.setTitle(R.string.dialog_title);
		mBuilder.setMessage(String.format(
				getResources().getString(R.string.add_freeze_message_format),
				appNameString));
		mBuilder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Utils.runCmd("pm block " + appPackNameString
								+ ";pm disable " + appPackNameString);
						Editor editor = sp.edit();
						editor.putInt(appPackNameString, 1);
						editor.commit();
						browseApplicationInfoAdapter.notifyDataSetChanged();
					}
				});

		mBuilder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});

		mBuilder.show();
	}

	public void confirmRemove() {
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
		mBuilder.setTitle(R.string.dialog_title);
		mBuilder.setMessage(String
				.format(getResources().getString(
						R.string.remove_freeze_message_format), appNameString));
		mBuilder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Utils.runCmd("pm unblock " + appPackNameString
								+ ";pm enable " + appPackNameString);
						Editor editor = sp.edit();
						editor.putInt(appPackNameString, 0);
						editor.commit();
						browseApplicationInfoAdapter.notifyDataSetChanged();
					}
				});

		mBuilder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});

		mBuilder.show();
	}
}
