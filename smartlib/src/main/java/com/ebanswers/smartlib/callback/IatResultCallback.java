package com.ebanswers.smartlib.callback;

/**
 * Created by Callanna on 2016/8/4.
 */
public interface IatResultCallback {

    void onEndSpeech();

    void onResult(String text);

    void onfail(String msg);
}
