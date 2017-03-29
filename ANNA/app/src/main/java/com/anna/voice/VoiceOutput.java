package com.anna.voice;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VoiceOutput implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private boolean initialized;
    private List<String> textToSpeech;

    public VoiceOutput(Context context) {

        tts = new TextToSpeech(context, this);
        this.initialized = false;
        this.textToSpeech = new ArrayList<>();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }
            this.initialized = true;
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
        for (String text : textToSpeech) {
            read(text);
        }
        setInitialized(true);
    }

    public void read(String text) {
        if (isInitialized()) {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null);
        } else {
            textToSpeech.add(text);
        }
    }

    private synchronized void setInitialized(boolean status) {
        this.initialized = status;
    }

    private synchronized boolean isInitialized() {
        return this.initialized;
    }

}
