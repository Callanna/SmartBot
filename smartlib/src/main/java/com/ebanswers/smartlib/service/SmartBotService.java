package com.ebanswers.smartlib.service;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ebanswers.smartlib.callback.SpeechUnderstandCallback;
import com.ebanswers.smartlib.manager.TtsManager;
import com.ebanswers.smartlib.manager.Understander;
import com.ebanswers.smartlib.util.LogUtil;

/**
 * Created by Callanna on 2016/8/4.
 */
public class SmartBotService extends Service {
    private Understander understander;

    private TtsManager ttsManager;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        understander = Understander.getInstance(this);
        ttsManager = TtsManager.getInstance(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startUnderstand();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startUnderstand() {
        understander.startUnderstanding(new SpeechUnderstandCallback() {
            @Override
            public void onResultUnderstand(String result) {
                LogUtil.d("duanyl==============onResultUnderstand>"+result);
                ttsManager.startSpeech("好的");
            }

            @Override
            public void onFail(String msg) {
                ttsManager.startSpeech("亲！什么？我没听清楚，再说一次！");
            }
        });
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ttsManager.destory();
        understander.destory();

    }
}
