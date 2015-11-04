package com.mistareader.TextProcessors;

import android.util.Log;

public class S {
	
	public static class ResultContainer {

		public boolean result;
		public String  resultStr;
		public String  userID;
		public String  resultSessionID;
		public String  cookie;

	}

	public static void L(Object object) {

		Log.d("mylog", "" + object);
	}

	public static void L(String string, Exception e) {
        Log.d("mylog", "" + string + " " + Log.getStackTraceString(e));
    }
	
}
