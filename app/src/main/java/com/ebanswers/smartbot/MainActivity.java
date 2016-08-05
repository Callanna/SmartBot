package com.ebanswers.smartbot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ebanswers.smartlib.SmartBot;
import com.ebanswers.smartlib.callback.IFLYRecognizerCallback;
import com.ebanswers.smartlib.data.IFlyJsonResult;
import com.ebanswers.smartlib.util.Constant;
import com.ebanswers.smartlib.util.LogUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_speechrecognizer,btn_smartbot,btn_stoprecognize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_speechrecognizer = (Button) findViewById(R.id.btn_speechrecognizer);
        btn_smartbot = (Button) findViewById(R.id.btn_smartbot);
        btn_smartbot.setOnClickListener(this);
        btn_speechrecognizer.setOnClickListener(this);
        SmartBot.initRecognizer(this, Constant.TYPE_CLOUD);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_smartbot:

                break;
            case R.id.btn_speechrecognizer:
                SmartBot.startRecognize(new IFLYRecognizerCallback() {
                    @Override
                    public void onRecognizerResult(IFlyJsonResult result) {
                        LogUtil.d("duanyl=============>getSlots:"+result.getSlots().get(0));
                        LogUtil.d("duanyl=============>getWords:"+result.getWords().get(0));
                    }

                    @Override
                    public void tip(String msg) {

                    }
                });

            case R.id.btn_stoprecognize:
                SmartBot.stopRecognize();
                break;
        }
    }
}
