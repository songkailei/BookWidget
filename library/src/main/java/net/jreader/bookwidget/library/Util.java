package net.jreader.bookwidget.library;


import java.io.BufferedInputStream;
import java.io.FileInputStream;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;


public class Util{

	public static int getWindowWidth(Context context){
		DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
	}

	public static int getWindowHeight(Context context){
		DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
	}

	public static String formatPath(String rootPath){
		String showPath = "";
		String str[] = rootPath.split("/");
		if(str.length > 3){
			if(str[str.length-1].length() > 8)
				str[str.length-1] = str[str.length-1].substring(0, 8)+"...";
			if(str[str.length-2].length() > 8)
				str[str.length-2] = str[str.length-2].substring(0, 8)+"...";
			showPath = str[str.length-2] + "/" + str[str.length-1];
			return showPath;
		}else{
			return rootPath;
		}
	}

	
	public static String charsetDetect(String fileName) throws Exception{
		BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));
		int p = (bin.read() << 8) + bin.read();
		String code = null;
		
		switch (p) {
			case 0xefbb:
				code = "UTF-8";
				break;
			case 0xfffe:
				code = "UTF-16LE";
				break;
			case 0xfeff:
				code = "UTF-16BE";
				break;
			default:
				code = "GBK";
		}
		bin.close();
		return code;
	}

	public static String int2Ip(int ip) {
		return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "."
				+ ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);
	}
	
}