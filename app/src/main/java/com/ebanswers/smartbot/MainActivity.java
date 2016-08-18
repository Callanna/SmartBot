package com.ebanswers.smartbot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ebanswers.smartlib.SmartBot;
import com.ebanswers.smartlib.callback.IFLYRecognizerCallback;
import com.ebanswers.smartlib.data.IFlyJsonResult;
import com.ebanswers.smartlib.util.Constant;
import com.ebanswers.smartlib.util.LogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.tv_one)
    TextView tvOne;
    @Bind(R.id.btn_init_speechrecognize)
    Button btnInitSpeechrecognize;
    @Bind(R.id.btn_speechrecognizer)
    Button btnSpeechrecognizer;
    @Bind(R.id.btn_stoprecognize)
    Button btnStoprecognize;
    @Bind(R.id.btn_destroy_recognize)
    Button btnDestroyRecognize;
    @Bind(R.id.tv_two)
    TextView tvTwo;
    @Bind(R.id.btn_init_smartbot)
    Button btnInitSmartbot;
    @Bind(R.id.btn_smartbot_start)
    Button btnSmartbotStart;
    @Bind(R.id.btn_smartbot_stop)
    Button btnSmartbotStop;
    @Bind(R.id.btn_destroy_smartbot)
    Button btnDestroySmartbot;
    @Bind(R.id.tv_three)
    TextView tvThree;
    @Bind(R.id.btn_init_tuningbot)
    Button btnInitTuningbot;
    @Bind(R.id.btn_tuningbot_start)
    Button btnTuningbotStart;
    @Bind(R.id.btn_tuningbot_stop)
    Button btnTuningbotStop;
    @Bind(R.id.btn_destroy_tuningbot)
    Button btnDestroyTuningbot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SmartBot.init(this);
    }


    @OnClick({R.id.btn_init_speechrecognize, R.id.btn_speechrecognizer, R.id.btn_stoprecognize, R.id.btn_destroy_recognize, R.id.btn_init_smartbot, R.id.btn_smartbot_start, R.id.btn_smartbot_stop, R.id.btn_destroy_smartbot, R.id.btn_init_tuningbot, R.id.btn_tuningbot_start, R.id.btn_tuningbot_stop, R.id.btn_destroy_tuningbot})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_init_speechrecognize:
                SmartBot.initRecognizer(Constant.TYPE_CLOUD);
                break;
            case R.id.btn_speechrecognizer:
                SmartBot.startRecognize(new IFLYRecognizerCallback() {
                    @Override
                    public void onRecognizerResult(IFlyJsonResult result) {
                        if(result.getWords().size()>0) {
                            LogUtil.d("duanyl=================>words: " + result.getWords().get(0));
                        }
                    }
                    @Override
                    public void tip(String msg) {}
                });
                break;
            case R.id.btn_stoprecognize:
                SmartBot.stopRecognize();
                break;
            case R.id.btn_destroy_recognize:
                SmartBot.release();
                break;
            case R.id.btn_init_smartbot:
                SmartBot.initSmartBot();
                break;
            case R.id.btn_smartbot_start:
                SmartBot.startListening();
                break;
            case R.id.btn_smartbot_stop:
                SmartBot.stopListening();
                break;
            case R.id.btn_destroy_smartbot:
                SmartBot.releaseBot();
                break;
            case R.id.btn_init_tuningbot:
                SmartBot.initTuningBot();
                break;
            case R.id.btn_tuningbot_start:
                SmartBot.startTuningListening();
                break;
            case R.id.btn_tuningbot_stop:
                SmartBot.stopTuningListening();
                break;
            case R.id.btn_destroy_tuningbot:
                SmartBot.releaseTuningBot();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
