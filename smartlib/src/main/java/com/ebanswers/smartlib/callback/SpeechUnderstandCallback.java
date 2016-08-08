package com.ebanswers.smartlib.callback;

/**
 * Created by Callanna on 2016/8/4.
 */
public interface SpeechUnderstandCallback {
    void onEndSpeech();
    void onResultUnderstand(String result);
    void onFail(String msg);
}
