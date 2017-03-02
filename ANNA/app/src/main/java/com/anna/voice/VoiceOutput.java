package com.anna.voice;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import com.anna.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VoiceOutput implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private Context context;
    private String voiceInput;
    private boolean isIdle;
    private boolean initialized;
    private List<String> textToSpeech;

    public VoiceOutput(Context context) {

        tts = new TextToSpeech(context, this);
        this.context = context;
        this.isIdle = true;
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

    /**
     * Showing google speech input dialog
     */
    public void promptSpeechInput() {
        setStatus(false);
        while (tts.isSpeaking()) {
            //do nothing
        }
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.speech_prompt));
        try {
            ((Activity) context).startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(context,
                    context.getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    voiceInput = result.get(0);
                    setStatus(true);
                }
                break;
            }
            default:
                Log.e("ActivityRersultCode", String.valueOf(requestCode));
                break;

        }
    }

    public void read(String text) {
        if (isInitialized()) {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null);
        } else {
            textToSpeech.add(text);
        }
    }

    public String getVoiceInput() {
        while (!isIdle()) {
            //do nothing
        }
        return voiceInput;
    }

    private synchronized boolean isIdle() {
        return isIdle;
    }

    private synchronized void setStatus(boolean status) {
        this.isIdle = status;
    }

    private synchronized void setInitialized(boolean status) {
        this.initialized = status;
    }

    private synchronized boolean isInitialized() {
        return this.initialized;
    }

    public void killService() {
        this.tts.shutdown();
    }

}
