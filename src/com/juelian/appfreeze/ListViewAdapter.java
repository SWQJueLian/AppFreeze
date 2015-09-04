package com.juelian.appfreeze;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//自定义适配器类，提供给listView的自定义view
public class ListViewAdapter extends BaseAdapter {

    Context mContext;

    private List<AppInfo> mlistAppInfo = null;

    LayoutInflater infater = null;

    public ListViewAdapter(Context context,  List<AppInfo> apps) {
        infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mlistAppInfo = apps ;
        mContext = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        System.out.println("size" + mlistAppInfo.size());
        return mlistAppInfo.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mlistAppInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

	@Override
    public View getView(int position, View convertview, ViewGroup arg2) {
        System.out.println("getView at " + position);
        View view = infater.inflate(R.layout.applist_listview_item, null);
        ImageView appLogo = (ImageView) view.findViewById(R.id.app_logo);;
        TextView appName = (TextView) view.findViewById(R.id.app_name);;
        TextView is_Freeze = (TextView) view.findViewById(R.id.is_freeze);

        AppInfo appInfo = (AppInfo) getItem(position);
        appLogo.setImageDrawable(appInfo.getAppIcon());
        appName.setText(appInfo.getAppLabel());
        
        int flag = mContext.getSharedPreferences("wl", Context.MODE_WORLD_READABLE).getInt(appInfo.getPkgName(), 0);
        if (flag==1) {
        	is_Freeze.setText(R.string.is_freeze);
        	is_Freeze.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_light));
        }
        return view;
    }
    
}
