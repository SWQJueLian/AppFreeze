package com.juelian.appfreeze;

import java.io.PrintWriter;

import android.util.Log;

public class Utils {

	/**
	 * @param cmd Ҫ���е�����
	 * @return ����false��ʾ�޷���ȡROOTȨ��
	 */
	public static boolean runCmd(String cmd) {
		try {
			boolean root = true;
			Process process = null;
			if (root) {
				process = Runtime.getRuntime().exec("su");
				PrintWriter pw = new PrintWriter(process.getOutputStream());
				pw.println(cmd);
				pw.flush();
				pw.close();
				process.waitFor();
				Log.d("mijl-->", "root");
			} else {
				process = Runtime.getRuntime().exec(cmd);
				process.waitFor();
				Log.d("mijl-->", "root-->");
			}
			if (process!=null) {
				return process.exitValue()!=0 ? false : true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
