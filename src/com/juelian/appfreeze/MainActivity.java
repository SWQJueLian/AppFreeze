package com.juelian.appfreeze;

import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
	
	private Button mAllAppButton;
	private Button mUserAppButton;
	private Button mCleanButton;
	
	public static final int SYS_APP = 1;
	public static final int USERS_APP = 2;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mAllAppButton = (Button) findViewById(R.id.all_apps);
		mUserAppButton = (Button) findViewById(R.id.users_app);
		mCleanButton = (Button) findViewById(R.id.clean_list);
		
		mAllAppButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				filterAppAndStartActivity(SYS_APP);
			}
		});
		
		mUserAppButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				filterAppAndStartActivity(USERS_APP);
			}
		});
		
		mCleanButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						SharedPreferences sharedPreferences = getSharedPreferences("wl", Context.MODE_WORLD_WRITEABLE);
						Map<String, ?> map = sharedPreferences.getAll();
						if (map.size()==0) {
							Looper.prepare();
							Toast.makeText(getApplicationContext(), R.string.unnecessary_unfreeze_software, 0).show();
							Looper.loop();
							return;
						}
						for (String string : map.keySet()) {
							Log.e("mijl-->", "packname: "+string);
							Utils.runCmd("pm unblock " + string);
							Utils.runCmd("pm enable " + string);
						}
						Editor editor = sharedPreferences.edit();
						editor.clear();
						editor.commit();
						Looper.prepare();
						Toast.makeText(getBaseContext(), R.string.success, Toast.LENGTH_SHORT).show();
						Looper.loop();
					}
				}).start();
			}
		});
	}
	
	/**
	 * @param filter 1--> all app; 2--> user instatlled app
	 */
	private void filterAppAndStartActivity(int filter){
		Intent intent = new Intent(getBaseContext(),FilterListViewActivity.class);
		intent.putExtra("filter", filter);
		this.startActivity(intent);
	}

}
