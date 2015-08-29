package com.juelian.appfreeze;

import java.io.PrintWriter;

import android.util.Log;

public class Utils {

    public static void runCmd(String cmd){
    	try {
    		boolean root = true;
    		if (root) {
    			Process p = Runtime.getRuntime().exec("su");
    			PrintWriter pw = new PrintWriter(p.getOutputStream());
    			pw.println(cmd);
    			pw.flush();
    			pw.close();
    			p.waitFor();
    			Log.e("mijl-->", p.exitValue()+""+root);
    			//return true;
    		} else {
	    		Process p = Runtime.getRuntime().exec(cmd);
	    		p.waitFor();
	    		Log.e("mijl-->", p.exitValue()+"");
	    		//return true;
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//return false;
    }
}
