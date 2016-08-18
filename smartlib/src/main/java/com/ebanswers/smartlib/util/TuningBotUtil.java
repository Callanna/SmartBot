package com.ebanswers.smartlib.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Callanna on 2016/8/8.
 */
public class TuningBotUtil {
    //http://www.tuling123.com/openapi/api?key=62a124a72df9470d9c62ecfd23677923&info=nihao
    private static String API_KEY = "62a124a72df9470d9c62ecfd23677923";
    private static String TULINGURL = "http://www.tuling123.com/openapi/api";

    /**
     * 发送一个消息，并得到返回的消息
     * @param msg
     * @return
     */
    public static String sendMsg(String msg)
    {
        String answer = "sorry!我也无能为力了！";
        LogUtil.d("duanyl==============>msg:"+msg);
        String url = setParams(msg);
        String res = doGet(url);
        if(!res.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(res);
                int code = jsonObject.getInt("code");
                String text = jsonObject.getString("text");
                if (!(code > 40000 || text.equals("") || text == null)) {
                    answer = text;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return answer;
    }

    /**
     * 拼接Url
     * @param msg
     * @return
     */
    private static String setParams(String msg)
    {
        try
        {
            msg = URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return TULINGURL + "?key=" + API_KEY + "&info=" + msg;
    }

    /**
     * Get请求，获得返回数据
     * @param urlStr
     * @return
     */
    private static String doGet(String urlStr)
    {
        URL url = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try
        {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5 * 1000);
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200)
            {
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                int len = -1;
                byte[] buf = new byte[128];

                while ((len = is.read(buf)) != -1)
                {
                    baos.write(buf, 0, len);
                }
                baos.flush();
                return baos.toString();
            } else
            {
               LogUtil.d("服务器连接错误！");
            }

        } catch (Exception e)
        {
            e.printStackTrace();
            LogUtil.d("服务器连接错误！");
        } finally
        {
            try
            {
                if (is != null)
                    is.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                if (baos != null)
                    baos.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            conn.disconnect();
        }
        if(baos != null) {
            return baos.toString();
        }else{
            return "";
        }
    }
}
