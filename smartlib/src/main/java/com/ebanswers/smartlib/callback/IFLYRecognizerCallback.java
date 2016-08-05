package com.ebanswers.smartlib.callback;

import com.ebanswers.smartlib.data.IFlyJsonResult;

/**
 * Created by Callanna on 2016/8/4.
 */
public interface IFLYRecognizerCallback {

    void onRecognizerResult(IFlyJsonResult result);

    void tip(String msg);
}
