package com.ebanswers.smartlib;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.ebanswers.smartlib.callback.IFLYRecognizerCallback;
import com.ebanswers.smartlib.service.SmartBotService;
import com.ebanswers.smartlib.service.SpeechRecognizerService;
import com.ebanswers.smartlib.service.TuningBotService;
import com.ebanswers.smartlib.util.LogUtil;
import com.iflytek.cloud.SpeechUtility;

import java.lang.ref.WeakReference;

/**
 * Created by Callanna on 2016/8/3.
 */
public class SmartBot {
    private static WeakReference<Context> mContext;
    public static String appId;
    public static String command ;
    public static String recoginertype =  "";
    public static SpeechRecognizerService recognizerService;
    public static SmartBotService smartBotService;
    public static TuningBotService tuningBotService;

    /**
        建议在Application里初始化
     */
    public static void init(Context context){
        mContext = new WeakReference<>(context);
        try {
            appId = mContext.get().getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.getString("smartBotappId");
            SpeechUtility.createUtility(context.getApplicationContext(), "appid=" +appId);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     *   ====================================================语音识别=======================
     */

    public static void initRecognizer(String type) {
        try {
            command = mContext.get().getPackageManager().getApplicationInfo(mContext.get().getPackageName(),PackageManager.GET_META_DATA).metaData.getString("smartCommand");
            LogUtil.d("command:" + command );
            recoginertype = type;
            mContext.get().startService(new Intent(mContext.get(),SpeechRecognizerService.class));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void startRecognize(IFLYRecognizerCallback iflyRecognizerCallback){
        if(recognizerService !=null) {
            recognizerService.setmRecognizerListener(iflyRecognizerCallback);
            recognizerService.startListening();
        }
    }

    public static void stopRecognize(){
        if(recognizerService !=null) {
            recognizerService.stopListening();
        }
    }

     public static void release(){
        if(recognizerService != null){
            recognizerService.onDestroy();
        }

     }

    /**
     *   ====================================================语音识别========end===============
          ================================================================================
     ============================================================================
     */


    /**
     *   ====================================================SmartBot=======================
     */
    public static void initSmartBot(){
        mContext.get().startService(new Intent(mContext.get(),SmartBotService.class));
    }

    public static void startListening(){
        if(smartBotService != null){
            smartBotService.startUnderstand();
        }
    }

    public static void stopListening(){
        if(smartBotService != null){
            smartBotService.stopUnderstand();
        }
    }

    public static void releaseBot(){
        if (smartBotService != null){
            smartBotService.onDestroy();
        }
    }

    /**
     *   ====================================================TuningBot=======================
     */
    public static void initTuningBot(){
        mContext.get().startService(new Intent(mContext.get(),TuningBotService.class));
    }

    public static void startTuningListening(){
        if(tuningBotService != null){
            tuningBotService.startUnderstand();
        }
    }

    public static void stopTuningListening(){
        if(tuningBotService != null){
            tuningBotService.stopUnderstand();
        }
    }

    public static void releaseTuningBot(){
        if (tuningBotService != null){
            tuningBotService.onDestroy();
        }
    }


}
