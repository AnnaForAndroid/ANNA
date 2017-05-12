package com.anna.voice;

/**
 * Created by PARSEA on 14.11.2016.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anna.R;
import com.anna.util.MyApplication;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class VoiceControl implements RecognitionListener {

    /* Named searches allow to quickly reconfigure the decoder */
    private final String KWS_SEARCH = "wakeup";
    private final String YES_NO_SEARCH = "answer";
    private String NAVIGATION_SEARCH;
    private final String MESSAGE_ANSWER = "messageAnswer";
    private String PHONE_SEARCH;
    private String SMS_SEARCH;
    private String MUSIC_SEARCH;
    private final String MENU_SEARCH = "menu";

    /* Keyword we are looking for to activate menu */
    private static final String KEYPHRASE = "hey anna";

    private SpeechRecognizer recognizer;
    private String userAnswer;
    private String grammarDir;

    private String currentSearch;

    private Context context;

    public VoiceControl() {
        NAVIGATION_SEARCH = MyApplication.application.getApplicationContext().getString(R.string.navigation);
        MUSIC_SEARCH = MyApplication.application.getApplicationContext().getString(R.string.music);
        PHONE_SEARCH = MyApplication.application.getApplicationContext().getString(R.string.phone);
        SMS_SEARCH = MyApplication.application.getApplicationContext().getString(R.string.sms);
        this.context = MyApplication.application.getApplicationContext();
        runRecognizerSetup();
    }

    private void switchSearch(String searchName) {
        recognizer.stop();

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH)) {
            recognizer.startListening(searchName);
        } else {
            RelativeLayout relativeLayout = (RelativeLayout) MyApplication.application.getDashboard().findViewById(R.id.voice_overlay);
            relativeLayout.setVisibility(View.VISIBLE);
            recognizer.startListening(searchName, 10000);
        }
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them
        String systemLanguage = Locale.getDefault().getLanguage();

        if ("en".equals(systemLanguage)) {
            recognizer = SpeechRecognizerSetup.defaultSetup()
                    .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                    .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                    .setKeywordThreshold(1e-5f)
                    .getRecognizer();
            grammarDir = "/grammars-en/";
            recognizer.addListener(this);
        } else if ("de".equals(systemLanguage)) {
            recognizer = SpeechRecognizerSetup.defaultSetup()
                    .setAcousticModel(new File(assetsDir, "de-de-ptm"))
                    .setDictionary(new File(assetsDir, "cmusphinx-voxforge-de.dic"))
                    .setKeywordThreshold(1e-5f)
                    .getRecognizer();
            grammarDir = "/grammars-de/";
            recognizer.addListener(this);
        } else {
            Toast.makeText(context.getApplicationContext(), "Sorry your language is not supported yet", Toast.LENGTH_LONG).show();
        }

        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);

        // Create grammar-based search for answer recognition
        File answerGrammar = new File(assetsDir, grammarDir + "answer.gram");
        recognizer.addGrammarSearch(YES_NO_SEARCH, answerGrammar);

        File navigationGrammar = new File(assetsDir, grammarDir + "language-model.lm.bin");
        recognizer.addNgramSearch(NAVIGATION_SEARCH, navigationGrammar);

        File smsGrammar = new File(assetsDir, grammarDir + "language-model.lm.bin");
        recognizer.addNgramSearch(SMS_SEARCH, smsGrammar);

        File phoneGrammar = new File(assetsDir, grammarDir + "language-model.lm.bin");
        recognizer.addNgramSearch(PHONE_SEARCH, phoneGrammar);

        File messageAnswerGrammar = new File(assetsDir, grammarDir + "language-model.lm.bin");
        recognizer.addNgramSearch(MESSAGE_ANSWER, messageAnswerGrammar);

        File menuGrammar = new File(assetsDir, grammarDir + "menu.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);

        switchSearch(KWS_SEARCH);

    }

    private void runRecognizerSetup() {
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(MyApplication.application.getDashboard().getApplicationContext());
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    Toast.makeText(context.getApplicationContext(), "Failed to init recognizer " + result, Toast.LENGTH_LONG).show();
                } else {
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();
    }

    @Override
    public void onBeginningOfSpeech() {
        // Needed for Interface
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        RelativeLayout relativeLayout = (RelativeLayout) MyApplication.application.getDashboard().findViewById(R.id.voice_overlay);
        relativeLayout.setVisibility(View.INVISIBLE);

        if (!recognizer.getSearchName().equals(KWS_SEARCH))
            switchSearch(KWS_SEARCH);
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();

        if (text.equals(KEYPHRASE)) {
            currentSearch = MENU_SEARCH;
            MyApplication.application.getVoiceOutput().read(MyApplication.application.getApplicationContext().getString(R.string.listener_notification));
            while (MyApplication.application.getVoiceOutput().speaking()) {
            }
            switchSearch(MENU_SEARCH);
        } else if (text.equals(NAVIGATION_SEARCH)) {
            currentSearch = NAVIGATION_SEARCH;
            switchSearch(NAVIGATION_SEARCH);
        } else if (text.equals(MUSIC_SEARCH)) {
            currentSearch = MUSIC_SEARCH;
        } else if (text.equals(SMS_SEARCH)) {
            currentSearch = SMS_SEARCH;
            switchSearch(SMS_SEARCH);
        } else if (text.equals(PHONE_SEARCH)) {
            currentSearch = PHONE_SEARCH;
            switchSearch(PHONE_SEARCH);
        }
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {

        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            if (YES_NO_SEARCH.equals(currentSearch) || MESSAGE_ANSWER.equals(currentSearch)) {
                synchronized (userAnswer) {
                    userAnswer = text;
                }
            } else if (NAVIGATION_SEARCH.equals(currentSearch)) {
                navigateTo(text);
            }
            Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateTo(String text) {
        MyApplication.application.getDashboard().switchTab("Here Maps");
        TextView to = (TextView) MyApplication.application.getDashboard().findViewById(R.id.navigationTo);
        String[] result = text.split(context.getApplicationContext().getString(R.string.navigation));
        if (result.length >= 1) {
            to.setText(result[0]);
        }

    }

    @Override
    public void onError(Exception e) {
        Toast.makeText(context.getApplicationContext(),
                e.getMessage(), Toast.LENGTH_LONG).show();
        Log.e("Error", e.toString());
    }

    @Override
    public void onTimeout() {
        switchSearch(KWS_SEARCH);
    }

    private String getAnswer(String search) {
        switchSearch(search);
        String answer;
        synchronized (userAnswer) {
            while (!"".equals(userAnswer)) {
                // wait
            }
            answer = userAnswer;
            userAnswer = "";
        }
        return answer;
    }

    public String getUserAnswer() {
        return getAnswer(YES_NO_SEARCH);
    }

    public String getMessageAnswer() {
        return getAnswer(MESSAGE_ANSWER);
    }
}
