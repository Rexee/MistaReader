package com.mistareader.TextProcessors;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.util.Log;

public class StringProcessor {


  	public static String mista_URL_EncodePlus(String str)
	{
		String ret = str;
		try {

			str.replace("+", "___plus___");
			
			ret = URLEncoder.encode(str, "windows-1251").replace("+", "%20");
			
		} catch (UnsupportedEncodingException e) {

			S.L("mista_URL_Encode: " + Log.getStackTraceString(e));

		}
		
		return ret;
	}
	
	public static String mista_URL_Encode(String str)
	{
		String ret = str;
		try {

			ret = URLEncoder.encode(str, "windows-1251").replace("+", "%20");
			
		} catch (UnsupportedEncodingException e) {

			S.L("mista_URL_Encode: " + Log.getStackTraceString(e));

		}
		
		return ret;
	}

}
