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
     *
     * @param msg
     * @return
     */
    public static void sendMsg(String msg, final AnswerCallBack callBack) {
        LogUtil.d("duanyl==============>msg:" + msg);
        String url = setParams(msg);

        doGetAnsync(url, new AnswerCallBack() {
            @Override
            public void onAnswer(String answerjson) {
                if (!answerjson.equals("")) {
                    try {
                        String answer = "";
                        JSONObject jsonObject = new JSONObject(answerjson);
                        int code = jsonObject.getInt("code");
                        String text = jsonObject.getString("text");
                        if (!(code < 40000 || text.equals("") || text == null)) {
                            answer = text;
                            LogUtil.d("duanyl============>answer:" + answer);
                            callBack.onAnswer(answer);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 拼接Url
     *
     * @param msg
     * @return
     */
    private static String setParams(String msg) {
        try {
            msg = URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return TULINGURL + "?key=" + API_KEY + "&info=" + msg;
    }
  private static void doGetAnsync(final String urlStr, final AnswerCallBack callBack){
    new Thread(new Runnable() {
        @Override
        public void run() {
            try {
               String answer = doGet(urlStr);
                callBack.onAnswer(answer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }).start();
  }
    /**
     * Get请求，获得返回数据
     *
     * @param urlStr
     * @return
     */
    private static String doGet(String urlStr) throws IOException {
        LogUtil.d("duanyl=============>urlstr:" + urlStr);
        URL url = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        url = new URL(urlStr);
        conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(8000);
        conn.setConnectTimeout(8000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        int code = conn.getResponseCode();
        if (code >= 200 && code < 300) {
            is = conn.getInputStream();
            baos = new ByteArrayOutputStream();
            int len = -1;
            byte[] buf = new byte[128];

            while ((len = is.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            baos.flush();
            is.close();
            byte[] result = baos.toByteArray();
            baos.close();
            conn.disconnect();
            LogUtil.d("duanyl==========>response:" + new String(result));
            return new String(result);
        } else {
            LogUtil.d("服务器连接错误！response code:" + code);
            return "";
        }
    }
    public interface AnswerCallBack {
        void onAnswer(String msg);
    }
}
