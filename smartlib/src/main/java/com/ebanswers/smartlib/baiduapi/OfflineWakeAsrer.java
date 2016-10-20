package com.ebanswers.smartlib.baiduapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AndroidRuntimeException;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.ebanswers.smartlib.callback.IatResultCallback;
import com.ebanswers.smartlib.manager.IatManager;
import com.ebanswers.smartlib.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Callanna on 2016/10/21.
 */

public class OfflineWakeAsrer {
    public static OfflineWakeAsrer instance;
    public Context mContext;
    private EventManager mWpEventManager;

    private OfflineWakeAsrer(final Activity context){
        mContext = context;
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
                        stop();
                        String word = json.getString("word");
                        LogUtil.d("duanyl===========>唤醒成功, 唤醒词: " + word + "\r\n");
                        Intent intent = new Intent("com.baidu.action.RECOGNIZE_SPEECH");
                        intent.putExtra("grammar", "asset:///baidu_speech_grammar.bsg");
                        context.startActivityForResult(intent, 1);
                    } else if ("wp.exit".equals(name)) {

                        LogUtil.d("duanyl===========>" + params + "\r\n");
                    }
                } catch (JSONException e) {
                    throw new AndroidRuntimeException(e);
                }
            }
        });

    }
    Handler myhandler = new Handler();
    public void restart(){
        myhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                start(mResname);
            }
        },1000) ;
    }
    public static OfflineWakeAsrer getInstance(Activity context){
        if(instance == null){
            instance = new OfflineWakeAsrer(context);
        }
        return instance;
    }
    private String mResname;

    public void start(String resname){
        mResname = resname;
        // 3) 通知唤醒管理器, 启动唤醒功能
        HashMap params = new HashMap();
        params.put("kws-file", "assets:///"+resname); // 设置唤醒资源
        mWpEventManager.send("wp.start", new JSONObject(params).toString(), null, 0, 0);
    }
    public void stop(){
        // 停止唤醒监听
        mWpEventManager.send("wp.stop", null, null, 0, 0);
    }
}
