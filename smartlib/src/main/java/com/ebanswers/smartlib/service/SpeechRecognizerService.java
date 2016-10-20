package com.ebanswers.smartlib.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ebanswers.smartlib.SmartBot;
import com.ebanswers.smartlib.callback.IFLYRecognizerCallback;
import com.ebanswers.smartlib.data.IFlyJsonResult;
import com.ebanswers.smartlib.util.Constant;
import com.ebanswers.smartlib.util.LogUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by Callanna on 2016/8/3.
 */
public class SpeechRecognizerService extends Service {
    protected Context mContext;
    private String grammar =  "";
    private String grammarID = "";

    // 语音识别对象
    private SpeechRecognizer mAsr;

    private IFLYRecognizerCallback iflyRecognizerCallback;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void setmRecognizerListener(IFLYRecognizerCallback mRecognizerListener) {
        this.iflyRecognizerCallback = mRecognizerListener;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        SmartBot.recognizerService = this;
        grammar = String.format(Constant.GARMMAR,SmartBot.command);
        // 初始化识别对象
        mAsr = SpeechRecognizer.createRecognizer(mContext, mInitListener);
        LogUtil.d("duanyl=================>onCreate"+grammar);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d("duanyl=================>onCreate 2");
        initGrammar();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initGrammar() {
       //指定引擎类型
        if(SmartBot.recoginertype.equals(Constant.TYPE_CLOUD)) {
            mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
            int ret = mAsr.buildGrammar(Constant.GRAMMAR_TYPE_ABNF, grammar, new GrammarListener() {
                @Override
                public void onBuildFinish(String grammarId, SpeechError error) {
                    LogUtil.d("duanyl=================>onCreate 3");
                    if (error == null) {
                        grammarID = new String(grammarId);
                        LogUtil.d("duanyl============>语法构建成功：" + grammarId);
                        mAsr.setParameter(SpeechConstant.CLOUD_GRAMMAR, grammarId);
                        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
                        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
                        mAsr.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
                        LogUtil.d("duanyl=============>" + Environment.getExternalStorageDirectory() + "/msc/asr.wav");
                        mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/asr.wav");

                    } else {
                        LogUtil.d("duanyl============>语法构建失败,错误码：" + error.getErrorCode());
                    }
                }
            });
            if (ret != ErrorCode.SUCCESS)
                LogUtil.d("duanyl============>语法构建失败,错误码：" + ret);
        }else{

        }
    }

    public void startListening() {
        int ret = mAsr.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            LogUtil.d("duanyl============>识别失败,错误码: " + ret);
        }
    }

    public void stopListening(){
        if(mAsr != null){
            mAsr.stopListening();
            mAsr.cancel();
            myhandler.removeCallbacks(runnable);
        }
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAsr.cancel();
        mAsr.destroy();
    }
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                LogUtil.d("duanyl============>初始化失败,错误码："+code);
            }
        }
    };

    private Handler myhandler = new Handler() ;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startListening();
        }
    };
    private RecognizerListener mRecognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            LogUtil.d("duanyl============>onVolumeChanged i "+i);
        }

        @Override
        public void onBeginOfSpeech() {
            LogUtil.d("duanyl============>onBeginOfSpeech  ");
        }

        @Override
        public void onEndOfSpeech() {
            LogUtil.d("duanyl============>onEndOfSpeech  ");
            myhandler.postDelayed(runnable,500);
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
             LogUtil.d("duanyl============>recognizerResult: "+recognizerResult.getResultString());
             parseGrammarResult(recognizerResult.getResultString());
        }

        @Override
        public void onError(SpeechError speechError) {
            LogUtil.d("duanyl============>speechError  :"+speechError.getErrorCode());
            myhandler.postDelayed(runnable,500);
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
            LogUtil.d("duanyl============>onEvent  ");
        }
    };

//    {
//        "sn": 1,
//            "ls": true,
//            "bg": 0,
//            "ed": 0,
//            "ws": [
//        {
//            "bg": 0,
//                "cw": [
//            {
//                "sc": "72",
//                    "gm": "0",
//                    "w": "中风"
//            },
//            {
//                "sc": "45",
//                    "gm": "0",
//                    "w": "烘干"
//            }
//            ]
//        }
//        ]
//    }
    //解析语音识别后的JSON数据
    public void parseGrammarResult(String json) {
        IFlyJsonResult mIFlyJsonResult = new IFlyJsonResult();
        try {
            LogUtil.d("duanyl==========>get  data " + json);
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);
            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);//曲该词组的第一项,第一项的分数最大
                String score = obj.getString("sc");//分数
                String word= obj.getString("w");
                mIFlyJsonResult.addWords(word);
                LogUtil.d("duanyl===============>word :" +word+",score:"+score);
            }
            iflyRecognizerCallback.onRecognizerResult(mIFlyJsonResult);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
