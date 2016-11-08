package com.anna;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.anna.util.IndexedHashMap;
import com.anna.util.Voice;


public class ChatViewActivity extends Fragment {

    private RecyclerView mRecyclerView;
    private ChatViewAdapter mAdapter = new ChatViewAdapter(new IndexedHashMap<String, NotificationData>());
    private Voice voice;
    private static String LOG_TAG = "ChatViewActivity";
    private static ChatViewActivity chatViewActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RelativeLayout llLayout = (RelativeLayout) inflater.inflate(R.layout.activity_card_view, container, false);

        mRecyclerView = (RecyclerView) llLayout.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(super.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        ChatViewActivity.chatViewActivity = this;
        voice = new Voice(super.getActivity());

        return llLayout;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        ChatViewActivity.super.onActivityResult(requestCode, resultCode, data);
        voice.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onResume() {
        super.onResume();
         mAdapter.setOnItemClickListener(new ChatViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(LOG_TAG, " Clicked on Item " + position);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        voice.killService();
    }

    public void answerMessage(NotificationData notificationData, String text) {
        NotificationCompat.Action action = extractWearAction(notificationData.getNotification());

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        if (action != null) {
            for (RemoteInput remoteIn : action.getRemoteInputs()) {
                Log.i("", "RemoteInput: " + remoteIn.getLabel());
                bundle.putCharSequence(remoteIn.getResultKey(), text);
            }
            RemoteInput.addResultsToIntent(action.getRemoteInputs(), intent, bundle);
            try {
                action.actionIntent.send(getContext(), 0, intent);
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    public static void notifyUser(final NotificationData notificationData) {
        if (chatViewActivity != null) {
            chatViewActivity.mAdapter.addItem(notificationData, notificationData.getTitle());
            chatViewActivity.mRecyclerView.setAdapter(chatViewActivity.mAdapter);
            chatViewActivity.voice.read(notificationData.getTitle());
            chatViewActivity.voice.read(chatViewActivity.getString(R.string.read_message));
            chatViewActivity.voice.setStatus(false);
            chatViewActivity.voice.promptSpeechInput();
            new Thread() {
                @Override
                public void run() {
                    if (chatViewActivity.voice.getVoiceInput().toLowerCase().equals("ja")) {
                        chatViewActivity.voice.read(notificationData.getText());
                        chatViewActivity.voice.read(chatViewActivity.getString(R.string.ask_to_answer));
                        new Thread() {
                            @Override
                            public void run() {
                                if (chatViewActivity.voice.getVoiceInput().toLowerCase().equals("ja")) {
                                    chatViewActivity.voice.promptSpeechInput();
                                    chatViewActivity.answerMessage(notificationData, chatViewActivity.voice.getVoiceInput());
                                }
                            }
                        }.start();
                    }
                }
            }.start();
        }
    }

    public NotificationCompat.Action extractWearAction(Notification n) {
        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender(n);
        NotificationCompat.Action answerAction = null;
        if (wearableExtender.getActions().size() > 0) {
            for (NotificationCompat.Action action : wearableExtender.getActions()) {
                if (action.title.toString().toLowerCase().contains(getString(R.string.reply))) {
                    answerAction = action;
                }
            }
        }
        return answerAction;
    }
}