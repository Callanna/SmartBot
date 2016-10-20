package com.ebanswers.smartlib.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.ebanswers.smartlib.SmartBot;
import com.ebanswers.smartlib.callback.IatResultCallback;
import com.ebanswers.smartlib.callback.TtsSpeakCallback;
import com.ebanswers.smartlib.manager.IatManager;
import com.ebanswers.smartlib.manager.TtsManager;
import com.ebanswers.smartlib.util.LogUtil;
import com.ebanswers.smartlib.util.TuningBotUtil;

/**
 * Created by Callanna on 2016/8/5.
 */
public class TuningBotService extends Service {

    private IatManager iatManager;

    private TtsManager ttsManager;

    private boolean stopListen = false;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    Handler myhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0) {
                startUnderstand();
            }else if(msg.what == 1) {
                if (ttsManager != null) {
                    LogUtil.d("duanyl============>msg:" + msg.getData().getString("word"));
                    ttsManager.startSpeech(msg.getData().getString("word"),ttsSpeakCallback );
                }
            }
        }
    };

    IatResultCallback iatResultCallback = new IatResultCallback() {
        @Override
        public void onEndSpeech() {
            if(!stopListen) {
                myhandler.removeMessages(0);
                myhandler.sendEmptyMessageDelayed(0, 1000);
            }
        }
        @Override
        public void onResult(String text) {
            TuningBotUtil.sendMsg(text, new TuningBotUtil.AnswerCallBack() {
                @Override
                public void onAnswer(String msg) {
                    speechWords(msg);
                }
            });
        }

        @Override
        public void onfail(String msg) {
            //speechWords("亲！我没听清楚!");
        }
    };
    TtsSpeakCallback ttsSpeakCallback = new TtsSpeakCallback() {
        @Override
        public void onStart() {
            LogUtil.d("Duanyl=============ttsSpeakCallback>onStart");
            stopUnderstand();
            stopListen = true;
        }

        @Override
        public void onEnd() {
            myhandler.removeMessages(0);
            myhandler.sendEmptyMessageDelayed(0, 1000);
            stopListen = false;
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        SmartBot.tuningBotService = this;
        iatManager = IatManager.getInstance(this);
        ttsManager = TtsManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }


    public void startUnderstand() {
        if(iatManager != null)
           iatManager.startRecognize(iatResultCallback);
    }
    public void stopUnderstand(){
        iatManager.stopRecognize();
        ttsManager.stopSpeech();
    }

    public void speechWords(String words){
        myhandler.removeMessages(1);
        Message msg = Message.obtain();
        msg.what = 1;
        Bundle bundle = new Bundle();
        bundle.putString("word",words);
        msg.setData(bundle);
        myhandler.sendMessageDelayed(msg,500);
    }
    String answer = "";


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ttsManager.destory();
        iatManager.destory();
    }
}
