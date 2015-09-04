package com.juelian.appfreeze;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private Button mAllAppButton;
	private Button mUserAppButton;
	
	public static final int ALL_APP = 1;
	public static final int USERS_APP = 2;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mAllAppButton = (Button) findViewById(R.id.all_apps);
		mUserAppButton = (Button) findViewById(R.id.users_app);
		
		mAllAppButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				filterAppAndStartActivity(ALL_APP);
			}
		});
		
		mUserAppButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				filterAppAndStartActivity(USERS_APP);
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
