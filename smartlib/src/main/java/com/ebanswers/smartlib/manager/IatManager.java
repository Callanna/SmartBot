package com.ebanswers.smartlib.manager;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;

import com.ebanswers.smartlib.callback.IatResultCallback;
import com.ebanswers.smartlib.util.LogUtil;
import com.ebanswers.smartlib.view.SpeechDialog;
import com.iflytek.cloud.ErrorCode;
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
 * Created by Callanna on 2016/8/4.
 */
public class IatManager {
    private static IatManager instance;

    private IatResultCallback callback;
    // 语音听写对象
    private SpeechRecognizer mIat;

    private SpeechDialog speechDialog;

    private boolean isAllowShow;

    private IatManager(Context context) {
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        speechDialog = new SpeechDialog(context);
        mIat = SpeechRecognizer.createRecognizer(context, mInitListener);
    }

    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            LogUtil.d("duanyl===========>SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                LogUtil.d("duanyl===========>初始化失败，错误码：" + code);
            } else {
                initParam();
            }
        }
    };

    private void initParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");

    }

    public static IatManager getInstance(Context context) {
        synchronized (IatManager.class) {
            if (instance == null) {
                instance = new IatManager(context);
            }
            return instance;
        }
    }

    public void startRecognize(IatResultCallback callback,boolean isshow) {
        isAllowShow = isshow;
        if(isshow){
            speechDialog.show();
        }
        if (mIat != null) {
            this.callback = callback;
            mIat.startListening(mRecognizerListener);
        }
    }

    public void stopRecognize() {
        if(speechDialog.isShown()){
            speechDialog.hide();
        }
        if (mIat != null) {
            mIat.stopListening();
            mIat.cancel();
        }
    }

    public void destory() {
        if (mIat != null) {
            stopRecognize();
            mIat.destroy();
        }
    }

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            LogUtil.d("duanyl=================>开始说话");
            if(isAllowShow){
                speechDialog.setTip("开始说话");
            }
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            LogUtil.d("duanyl=================>" + error.getPlainDescription(true));
            callback.onfail(error.toString());
            if(isAllowShow){
                speechDialog.setTip("error:"+error.getPlainDescription(true));
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            LogUtil.d("duanyl=================>结束说话");
            callback.onEndSpeech();
            if(isAllowShow){
                speechDialog.setTip("结束说话");
            }

        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            LogUtil.d("duanyl=================>" + results.getResultString());
            parseResult(results.getResultString());
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            LogUtil.d("duanyl=================>当前正在说话，音量大小：" + volume);
            if(isAllowShow){
                speechDialog.volumChanged(volume);
                speechDialog.setTip("识别中...");
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

    String listenResult = "";

    private void parseResult(String resultString) {
        LogUtil.d("duanyl===========>result:" + resultString);
        try {
            JSONTokener tokener = new JSONTokener(resultString);
            JSONObject joResult = new JSONObject(tokener);
            boolean ls = joResult.getBoolean("ls");//是否是最后一句
            JSONArray words = joResult.getJSONArray("ws");
            StringBuffer ret = new StringBuffer();
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                for (int j = 0; j < items.length(); j++) {
                    JSONObject obj = items.getJSONObject(j);
                    ret.append(obj.getString("w"));
                }
            }
            listenResult = listenResult + "" + ret;
            if (ls) {//如果是最后一句。
                LogUtil.d("duanyl============>listenResult:" + listenResult);
                if(isAllowShow){
                    speechDialog.setTip(listenResult);
                }
                if (callback != null)
                    callback.onResult(listenResult);
                listenResult = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
