package com.ebanswers.smartlib.service;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.ebanswers.smartlib.SmartBot;
import com.ebanswers.smartlib.callback.SpeechUnderstandCallback;
import com.ebanswers.smartlib.callback.TtsSpeakCallback;
import com.ebanswers.smartlib.manager.TtsManager;
import com.ebanswers.smartlib.manager.Understander;
import com.ebanswers.smartlib.util.LogUtil;

/**
 * Created by Callanna on 2016/8/4.
 */
public class SmartBotService extends Service {
    private Understander understander;

    private TtsManager ttsManager;

    private boolean stopListen = false;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SmartBot.smartBotService = this;
        understander = Understander.getInstance(this);
        ttsManager = TtsManager.getInstance(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    Handler myhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0) {
                startUnderstand();
            }else if(msg.what == 1){
                LogUtil.d("duanyl============>msg:"+msg.getData().getString("word"));
                ttsManager.startSpeech( msg.getData().getString("word"), new TtsSpeakCallback() {
                    @Override
                    public void onStart() {
                        stopUnderstand();
                        stopListen = true;
                    }

                    @Override
                    public void onEnd() {
                        myhandler.sendEmptyMessageDelayed(0,1000);
                        stopListen = false;
                    }
                });
            }
        }
    };

    public void speechWords(String words){
        myhandler.removeMessages(1);
        Message msg = Message.obtain();
        msg.what = 1;
        Bundle bundle = new Bundle();
        bundle.putString("word",words);
        msg.setData(bundle);
        myhandler.sendMessageDelayed(msg,500);
    }

    public void startUnderstand() {
        understander.startUnderstanding(new SpeechUnderstandCallback() {
            @Override
            public void onEndSpeech() {
                if(!stopListen)
                   myhandler.sendEmptyMessageDelayed(0,1000);
            }

            @Override
            public void onResultUnderstand(String result) {
                LogUtil.d("duanyl==============onResultUnderstand>"+result);
                speechWords(result);
            }

            @Override
            public void onFail(String msg) {
                speechWords("亲！我没听清楚!");
            }
        });
    }
  public void stopUnderstand(){
      understander.stopUnderstanding();
  }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ttsManager.destory();
        understander.destory();
    }
}
