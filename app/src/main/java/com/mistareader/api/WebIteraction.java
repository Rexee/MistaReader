package com.mistareader.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.mistareader.util.S;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class WebIteraction {

    private static final String POST_CONNECTIONTYPE_DEFAULT = "application/x-www-form-urlencoded; charset=windows-1251";
    private static final String GET_ACCEPT_DEFAULT          = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
    private static final String GET_ACCEPTLANGUAGE_DEFAULT  = "ru-RU";
    private static final String REQUIRED_COOKIE             = "document.cookie=\"";
    private static final int    DEFAULT_TIMEOUT             = 10000;
    private static final String HTML_TAG                    = "<html>";
    private static final int    HTML_TAG_LEN                = 6;

    // "text/html, application/xml;q=0.9, application/xhtml+xml, image/png, image/webp, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1";

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        } else {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
    }

    public static class RequestResult<T> {
        public String  resultStr;
        public String  cookie;
        public String  sessionID;
        public boolean success;
        public T       result;

        public RequestResult() {
            this.resultStr = "";
        }

        public RequestResult(String resultSrt, String cookie) {
            this.cookie = cookie;
            this.resultStr = resultSrt;

        }
    }

    public static class POST {

        public String url;

        public String cookie;
        public String POSTString;

    }

    private static String cookiesRequired(String result) {
        if (result.length() < HTML_TAG_LEN) {
            return "";
        }
        String htmlTag = result.substring(0, HTML_TAG_LEN).toLowerCase();
        if (!htmlTag.equals(HTML_TAG)) {
            return "";
        }

        int indStart = result.indexOf(REQUIRED_COOKIE);
        if (indStart >= 0) {
            int indEnd = result.indexOf(";", indStart);
            if (indEnd >= 0) {
                return result.substring(indStart + REQUIRED_COOKIE.length(), indEnd);
            }
        }

        return "";
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

    static public RequestResult postWebRequest(POST request) {
        RequestResult mRes = new RequestResult();

        try {

            URL page = new URL(request.url);
            HttpURLConnection conn = (HttpURLConnection) page.openConnection();
            setUpConnection(conn, request.cookie);
            conn.setRequestProperty("Content-Type", POST_CONNECTIONTYPE_DEFAULT);

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

            mRes.resultStr = sb.toString();

        } catch (Exception e) {
            S.L("postWebRequest: " + Log.getStackTraceString(e));
        }

        return mRes;
    }

    public static RequestResult doServerRequest(String url, String cookie, String entr_hash, String entr_id) {
        return doServerRequest(url, cookie + (!cookie.isEmpty() ? ";" : "") + "entr_id=" + entr_id + "; entr_hash=" + entr_hash);
    }

    static public RequestResult doServerRequest(String url, String cookie) {
        RequestResult res = new RequestResult("", cookie);

        HttpURLConnection conn = null;
        try {
            URL page = new URL(url);
            conn = (HttpURLConnection) page.openConnection();
            setUpConnection(conn, cookie);
            //            conn.getContent();

            InputStreamReader in = new InputStreamReader(conn.getInputStream(), "windows-1251");
            BufferedReader buff = new BufferedReader(in);
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = buff.readLine()) != null) {
                sb.append(line);
            }

            String resultSrt = sb.toString();
            String resCookie = cookiesRequired(resultSrt);
            if (!resCookie.isEmpty()) {
                conn.disconnect();

                conn = (HttpURLConnection) page.openConnection();
                setUpConnection(conn, resCookie);
                //                conn.getContent();

                in = new InputStreamReader(conn.getInputStream(), "windows-1251");
                buff = new BufferedReader(in);
                sb = new StringBuffer();
                while ((line = buff.readLine()) != null) {
                    sb.append(line);
                }

                resultSrt = sb.toString();
                res.cookie = resCookie;
            }

            if (false)
                res.sessionID = getEntr_hash(conn.getHeaderFields());


            res.resultStr = resultSrt;

            return res;

        } catch (Exception e) {
            S.L("getServerResponse: " + Log.getStackTraceString(e));
            return res;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static void setUpConnection(HttpURLConnection conn, String cookie) {
        conn.setReadTimeout(DEFAULT_TIMEOUT);
        conn.setConnectTimeout(DEFAULT_TIMEOUT);

        conn.setRequestProperty("Accept", GET_ACCEPT_DEFAULT);
        conn.setRequestProperty("Accept-Language", GET_ACCEPTLANGUAGE_DEFAULT);
        conn.setRequestProperty("Cookie", cookie);
    }

}
