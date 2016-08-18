package com.ebanswers.smartlib.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.ebanswers.smartlib.SmartBot;
import com.ebanswers.smartlib.callback.IatResultCallback;
import com.ebanswers.smartlib.callback.TtsSpeakCallback;
import com.ebanswers.smartlib.manager.IatManager;
import com.ebanswers.smartlib.manager.TtsManager;

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
    Handler myhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0) {
                startUnderstand();
            }
        }
    };
    public void startUnderstand() {
        iatManager.startRecognize(new IatResultCallback() {
            @Override
            public void onEndSpeech() {
                if(!stopListen) {
                    myhandler.sendEmptyMessageDelayed(0, 1000);
                }
            }

            @Override
            public void onResult(String text) {
                ttsManager.startSpeech(text, new TtsSpeakCallback() {
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

            @Override
            public void onfail(String msg) {

            }
        });
    }
    public void stopUnderstand(){
        iatManager.stopRecognize();
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
        iatManager.destory();
    }
}
