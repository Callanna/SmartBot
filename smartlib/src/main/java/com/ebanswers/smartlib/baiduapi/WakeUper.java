package com.ebanswers.smartlib.baiduapi;

import android.content.Context;
import android.util.AndroidRuntimeException;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.ebanswers.smartlib.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Callanna on 2016/10/20.
 */

public class WakeUper  {
    public static WakeUper instance;

    private EventManager mWpEventManager;

    private WakeUpListener listener;
    private WakeUper(Context context){
        // 1) 创建唤醒事件管理器
        mWpEventManager = EventManagerFactory.create(context, "wp");
        // 2) 注册唤醒事件监听器
        mWpEventManager.registerListener(new EventListener() {
            @Override
            public void onEvent(String name, String params, byte[] data, int offset, int length) {
                LogUtil.d("duanyl===========>"+ String.format("event: name=%s, params=%s", name, params));
                try {
                    JSONObject json = new JSONObject(params);
                    if ("wp.data".equals(name)) { // 每次唤醒成功, 将会回调name=wp.data的时间, 被激活的唤醒词在params的word字段
                        String word = json.getString("word");
                        LogUtil.d("duanyl===========>唤醒成功, 唤醒词: " + word + "\r\n");
                        if(listener != null){
                            listener.onResult(word);
                        }
                    } else if ("wp.exit".equals(name)) {
                        if(listener != null){
                            listener.onExit();
                        }
                        LogUtil.d("duanyl===========>" + params + "\r\n");
                    }
                } catch (JSONException e) {
                    throw new AndroidRuntimeException(e);
                }
            }
        });

    }

    public static WakeUper getInstance(Context context){
        if(instance == null){
            instance = new WakeUper(context);
        }
        return instance;
    }


    public void start(String resname,WakeUpListener wakeUpListener){
        listener = wakeUpListener;
        // 3) 通知唤醒管理器, 启动唤醒功能
        HashMap params = new HashMap();
        params.put("kws-file", "assets:///"+resname); // 设置唤醒资源
        mWpEventManager.send("wp.start", new JSONObject(params).toString(), null, 0, 0);
    }
    public void stop(){
        // 停止唤醒监听
        mWpEventManager.send("wp.stop", null, null, 0, 0);
    }

    public interface WakeUpListener {
        void onResult(String data);
        void onExit();
    }
}
