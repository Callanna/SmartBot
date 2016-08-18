package com.ebanswers.smartlib.manager;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;

import com.ebanswers.smartlib.callback.SpeechUnderstandCallback;
import com.ebanswers.smartlib.util.LogUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;

/**
 * Created by Callanna on 2016/8/4.
 */
public class Understander {


    // 语义理解对象（语音到语义）
    private SpeechUnderstander mSpeechUnderstander;

    private SpeechUnderstandCallback callback;

    private static Understander instance;

    private Understander(Context context) {
        // 初始化对象
        mSpeechUnderstander = SpeechUnderstander.createUnderstander(context, mSpeechUdrInitListener);
    }

    private final InitListener mSpeechUdrInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            LogUtil.d("duanyl==========>speechUnderstanderListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                LogUtil.d("duanyl==========>初始化失败,错误码：" + code);
            }else{
                initUnderStand();
            }
        }
    };

    private void initUnderStand() {
        // 设置语言
        mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mSpeechUnderstander.setParameter(SpeechConstant.ACCENT, "mandarin");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS,  "4000" );

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS, "1000" );

        // 设置标点符号，默认：1（有标点）
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT,   "1" );

        mSpeechUnderstander.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/sud.wav");
    }

    public static  Understander getInstance(Context context) {
        synchronized (Understander.class) {
            if (instance == null) {
                instance = new Understander(context);
            }
            return instance;
        }
    }
    public void startUnderstanding(SpeechUnderstandCallback callback){
        if(mSpeechUnderstander != null) {
            this.callback = callback;
            mSpeechUnderstander.startUnderstanding(mSpeechUnderstanderListener);
        }
    }

    public void stopUnderstanding(){
        if(mSpeechUnderstander != null) {
            mSpeechUnderstander.stopUnderstanding();
        }
    }

    public void destory(){
        if(mSpeechUnderstander != null){
            mSpeechUnderstander.cancel();
            mSpeechUnderstander.destroy();
        }
    }
    /**
     * 语义理解回调。
     */
    private SpeechUnderstanderListener mSpeechUnderstanderListener = new SpeechUnderstanderListener() {

        @Override
        public void onResult(final UnderstanderResult result) {
            if (null != result) {
                LogUtil.d("duanyl==========>"+ result.getResultString());
                // 显示
                String text = result.getResultString();
                if(callback != null){
                    callback.onResultUnderstand(text);
                }
            } else {
                LogUtil.d("duanyl==========>识别结果不正确。");
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            LogUtil.d("duanyl==========>当前正在说话，音量大小：" + volume);
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            LogUtil.d("duanyl==========>结束说话");
            callback.onEndSpeech();
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            LogUtil.d("duanyl==========>开始说话");

        }

        @Override
        public void onError(SpeechError error) {
            LogUtil.d("duanyl==========>"+error.getPlainDescription(true));
            callback.onFail(error.toString());
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };


}
