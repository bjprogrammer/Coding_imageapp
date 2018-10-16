package com.bobby.coding.utils;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

//Service that converts text to speech
public class TextSpeechService extends Service implements TextToSpeech.OnInitListener {
    private TextToSpeech mTts;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        mTts = new TextToSpeech(this, this);
        mTts.setSpeechRate(0.8f);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        sayHello(Constants.str);
        super.onStart(intent, startId);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
            } else {
                sayHello(Constants.str);
            }
        }
    }

    private void sayHello(String str) {
        mTts.speak(str,
                TextToSpeech.QUEUE_FLUSH,
                null);

    }
}
