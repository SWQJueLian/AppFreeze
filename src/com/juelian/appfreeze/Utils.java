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
    		} else {
	    		Process p = Runtime.getRuntime().exec(cmd);
	    		p.waitFor();
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
