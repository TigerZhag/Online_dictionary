package com.readboy.online.model.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {


    private static final String TAG = HttpUtil.class.getSimpleName();

    private MD5 md5 = new MD5();
        /*   请求输出的参数   */
    private static final String TOKEN = "4fbfc9b4d58838f1757b68c8eff5b5564ba2c7fb";

    /*   获取请求返回数据   */
    public String getConResult(String uriAddress, String query) {

        String result = null;
        HttpURLConnection con = null;
        BufferedReader bufferedReader = null;
        try {
            con = getNormalCon(uriAddress,query);
            con.setConnectTimeout(20000);
            con.setReadTimeout(10000);
            con.connect();
            Log.i(TAG,"Response Code = "+con.getResponseCode()+" ---- Response Message = "+con.getResponseMessage());
            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            int size = -1;
            char[] bt = new char[1024];
            StringBuffer sb = new StringBuffer();
            while ((size = bufferedReader.read(bt, 0, bt.length)) != -1) {
                sb.append(new String(bt, 0, size));
            }

            result = sb.toString();

        } catch (Exception e) {
            Log.i(TAG,e.toString());
            result = null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (con != null) {
                con.disconnect();
            }
        }

        return result;
    }

    public HttpURLConnection getNormalCon(String url,String query) throws IOException {
        return getNormalCon(url, false, query);
    }

    /*   设置请求头发起请求   */
    public HttpURLConnection getNormalCon(String url, boolean usePost, String query) throws IOException {
        String authPwd;
        authPwd = md5.GetMD5Code(query+TOKEN);
        HttpURLConnection conn = null;
        conn = (HttpURLConnection)(new URL(url)).openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(30000);
        conn.setRequestMethod(usePost?"POST":"GET");
        conn.setUseCaches(false);
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authentication", "4G:"+authPwd);
        return conn;
    }
}