package com.mistareader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.mistareader.TextProcessors.S;

public class WebIteraction {

	static final String POST_CONNECTIONTYPE_DEFAULT =  "application/x-www-form-urlencoded; charset=windows-1251";
	static final String GET_ACCEPT_DEFAULT = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
	static final String GET_ACCEPTLANGUAGE_DEFAULT = "ru-RU";

//	static final String GET_ACCEPT_DEFAULT = "text/html, application/xml;q=0.9, application/xhtml+xml, image/png, image/webp, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1";
	
	public static boolean isInternetAvailable(Activity context)
	{
        ConnectivityManager cm =
	            (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	     
	    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
	    boolean isConnected = activeNetwork != null &&
	                          activeNetwork.isConnectedOrConnecting();
	    
	    return isConnected;
	}
	
	public static class hashResult {
		String sessionID;
		String result;

		public hashResult() {
			sessionID = "";
			result = "";
		}

	}

	public static class PostResult {
		String result;

		public PostResult() {
			result = "";
		}

	}

	public static class POST {

		String url;
		
		String cookie;
		String POSTString;
		
	}

	static public String getServerResponse(String url) {

		try {

			S.L(url);

			URL page = new URL(url);

			HttpURLConnection conn = (HttpURLConnection) page.openConnection();
			conn.setConnectTimeout(5000);

			conn.setRequestProperty("Accept", GET_ACCEPT_DEFAULT);
			
			conn.setRequestProperty("Accept-Language", GET_ACCEPTLANGUAGE_DEFAULT);
//			conn.setRequestProperty("Cache-Control", "no-cache");
//			conn.setRequestProperty("Connection", "Keep-Alive");
//			conn.setRequestProperty("Accept-Encoding", "gzip, deflate");

			conn.getContent();

			InputStreamReader in = new InputStreamReader(conn.getInputStream(), "windows-1251");
			BufferedReader buff = new BufferedReader(in);
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = buff.readLine()) != null) {
				sb.append(line);
			}

			return sb.toString();

		} catch (Exception e) {
			S.L("getServerResponse: " + Log.getStackTraceString(e));
			return null;
		}
	}

	static public hashResult getServerResponseWithCookie(String url) {

		hashResult mRes = new hashResult();

		try {

			S.L(url);

			URL page = new URL(url);

			HttpURLConnection conn = (HttpURLConnection) page.openConnection();
			conn.setConnectTimeout(5000);

			conn.setRequestProperty("Accept", GET_ACCEPT_DEFAULT);
			conn.setRequestProperty("Accept-Language", GET_ACCEPTLANGUAGE_DEFAULT);
			 
			conn.getContent();

			mRes.sessionID = getEntr_hash(conn.getHeaderFields());

			InputStreamReader in = new InputStreamReader(conn.getInputStream(), "windows-1251");
			BufferedReader buff = new BufferedReader(in);
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = buff.readLine()) != null) {
				sb.append(line);
			}

			mRes.result = sb.toString();
			return mRes;

		} catch (Exception e) {

			S.L("getServerResponse: " + Log.getStackTraceString(e));
			return mRes;
		}
	}

	private static String getEntr_hash(Map<String, List<String>> headerFields) {

		final String mHash = "entr_hash";

		if (headerFields == null || headerFields.size() == 0) {
			return "";
		}

		List<String> cookieList = headerFields.get("Set-Cookie");
		if (cookieList != null) {
			for (String cookieTemp : cookieList) {

				if (cookieTemp.indexOf(mHash) == -1) {
					continue;
				}

				String[] arrayOfString = cookieTemp.split(";");

				String str;
				int pos;
				for (int i = 0; i < arrayOfString.length; i++) {

					str = arrayOfString[i];
					pos = str.indexOf(mHash);

					if (pos == -1) {
						continue;
					}

					return str.substring(pos + mHash.length() + 1);
				}

			}
		}

		return "";
	}

	static public String getServerResponseWithAuth(String url, String entr_hash, String entr_id) {

		try {
			if (entr_hash == null || entr_id == null || entr_hash.isEmpty() || entr_id.isEmpty()) {
				return "";
			}

			S.L("auth: " + url);

			URL page = new URL(url);

			HttpURLConnection conn = (HttpURLConnection) page.openConnection();
			conn.setConnectTimeout(5000);

			conn.setRequestProperty("Accept", GET_ACCEPT_DEFAULT);
			conn.setRequestProperty("Accept-Language", GET_ACCEPTLANGUAGE_DEFAULT);

			conn.setRequestProperty("Cookie", "entr_id=" + entr_id + "; entr_hash=" + entr_hash);

			conn.getContent();

			InputStreamReader in = new InputStreamReader(conn.getInputStream(), "windows-1251");
			BufferedReader buff = new BufferedReader(in);
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = buff.readLine()) != null) {
				sb.append(line);
			}

			return sb.toString();

		} catch (Exception e) {
		    S.L("getServerResponseWithAuth: " + Log.getStackTraceString(e));
			return null;
		}
	}

	static public PostResult postWebRequest(POST request) {
		PostResult mRes = new PostResult();

		try {

			S.L(request.url);

			URL page = new URL(request.url);

			HttpURLConnection conn = (HttpURLConnection) page.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(10000);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", POST_CONNECTIONTYPE_DEFAULT);
			conn.setRequestProperty("Accept", GET_ACCEPT_DEFAULT);
			conn.setRequestProperty("Accept-Language", GET_ACCEPTLANGUAGE_DEFAULT);
			conn.setRequestProperty("Cookie", request.cookie);

			conn.setDoInput(true);
			conn.setDoOutput(true);

			byte[] POSTBytes = request.POSTString.getBytes();
	        OutputStream os = conn.getOutputStream();
	        os.write(POSTBytes);
	        os.flush();
	        os.close();	

			conn.connect();

			InputStreamReader in = new InputStreamReader(conn.getInputStream(), "windows-1251");
			BufferedReader buff = new BufferedReader(in);
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = buff.readLine()) != null) {
				sb.append(line);
			}

			mRes.result = sb.toString();

		} catch (Exception e) {
		    S.L("getServerResponse: " + Log.getStackTraceString(e));
		}

		return mRes;
	}

}
