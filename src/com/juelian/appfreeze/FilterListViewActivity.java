package com.juelian.appfreeze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class FilterListViewActivity extends BaseActivity {

	private ListView mListView;
	private List<AppInfo> mAppInfos;
	private PackageManager pm;
	private ListViewAdapter browseApplicationInfoAdapter;
	private String appPackNameString = "";
	private String appNameString = "";
	public SharedPreferences sp;
	private AppInfo appInfo = null;
	private ProgressDialog mProgressDialog;
	private boolean state = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.applist_listview);
		sp = getSharedPreferences("wl", Context.MODE_WORLD_WRITEABLE);
		
		mListView = (ListView) findViewById(R.id.all_app_listview);
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
		
		mProgressDialog = new ProgressDialog(FilterListViewActivity.this);
		mProgressDialog.setMessage(getResources().getText(R.string.loading));
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);
		
		//mProgressDialog.show();
		
		new AsyncTask<Void, Void, Void>() {
			
			@Override
			protected void onPreExecute() {
				mProgressDialog.show();
			};

			@Override
			protected Void doInBackground(Void... params) {
				mAppInfos = queryFilterAppInfo(getIntent().getIntExtra(
						"filter", MainActivity.ALL_APP));
				browseApplicationInfoAdapter = new ListViewAdapter(
						getApplicationContext(), mAppInfos);
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				mListView.setAdapter(browseApplicationInfoAdapter);
				mProgressDialog.cancel();
			}
		}.execute();
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
				if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
					if (!app.packageName.equals(getBaseContext().getPackageName())) {
						appInfos.add(getAppInfo(app));
					}
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
						new AsyncTask<Void, Void, Void>() {
							
							protected void onPreExecute() {
								mProgressDialog.setMessage("正在冻结...");
								mProgressDialog.show();
							};

							@Override
							protected Void doInBackground(Void... params) {
								if (Utils.runCmd("pm block " + appPackNameString
										+ ";pm disable " + appPackNameString)) {
									Editor editor = sp.edit();
									editor.putInt(appPackNameString, 1);
									editor.commit();
									state = true;
								}else {
									state = false;
								}
								return null;
							}
							
							@Override
							protected void onPostExecute(Void result) {
								// TODO Auto-generated method stub
								super.onPostExecute(result);
								browseApplicationInfoAdapter.notifyDataSetChanged();
								if (!state) {
									Toast.makeText(FilterListViewActivity.this, R.string.root_fail, 0).show();
								}
								mProgressDialog.cancel();
							}
						}.execute();
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
						new AsyncTask<Void, Void, Void>() {
							
							protected void onPreExecute() {
								mProgressDialog.setMessage("正在解冻...");
								mProgressDialog.show();
							};

							@Override
							protected Void doInBackground(Void... params) {
								if (Utils.runCmd("pm unblock " + appPackNameString
										+ ";pm enable " + appPackNameString)) {
									Editor editor = sp.edit();
									editor.putInt(appPackNameString, 0);
									editor.commit();
									state = true;
								}else {
									state = false;
								}
								return null;
							}

							@Override
							protected void onPostExecute(Void result) {
								// TODO Auto-generated method stub
								super.onPostExecute(result);
								browseApplicationInfoAdapter.notifyDataSetChanged();
								if (!state) {
									Toast.makeText(FilterListViewActivity.this, R.string.root_fail, 0).show();
								}
								mProgressDialog.cancel();
							}
						}.execute();							
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
