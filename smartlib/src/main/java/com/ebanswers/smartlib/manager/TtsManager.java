package com.ebanswers.smartlib.manager;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;

import com.ebanswers.smartlib.callback.TtsSpeakCallback;
import com.ebanswers.smartlib.util.LogUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by Callanna on 2016/8/4.
 */
public class TtsManager {
    private static  TtsManager instance;
    // 语音合成对象
    private SpeechSynthesizer mTts;
    private TtsSpeakCallback callback;
    // 默认发音人
    private String voicer = "xiaoyan";
    private TtsManager(Context context){
        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(context, mTtsInitListener);
    }
    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            LogUtil.d("duanyl===========InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                LogUtil.d("duanyl=========== 初始化失败,错误码："+code);
            } else {
                initTtsParam();
            }
        }
    };
    private void initTtsParam() {
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置在线合成发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED,  "50" );
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH,  "50" );
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME,  "50");
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE,  "3" );
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");
    }

    public static TtsManager getInstance(Context context){
        synchronized (TtsManager.class){
            if(instance == null){
                instance = new TtsManager(context);
            }
            return instance;
        }
    }



    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            callback.onStart();
            LogUtil.d("duanyl============>开始播放");
        }

        @Override
        public void onSpeakPaused() {
            LogUtil.d("duanyl============暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            LogUtil.d("duanyl===========继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度

        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                LogUtil.d("duanyl===========播放完成");
                callback.onEnd();
            } else if (error != null) {
                LogUtil.d("duanyl==========="+error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    public void startSpeech(String text, TtsSpeakCallback callback){
        this.callback = callback;
        if(mTts != null){
            mTts.startSpeaking(text,mTtsListener);
        }
    }

    public void stopSpeech(){
        if(mTts != null){
            mTts.stopSpeaking();
        }
    }

    public void destory(){
        if(mTts != null){
            mTts.destroy();
        }
    }
}
