package com.anna;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import java.util.LinkedList;
import java.util.Queue;


public class ChatViewActivity extends Fragment {

    private RecyclerView mRecyclerView;
    private ChatViewAdapter mAdapter = new ChatViewAdapter(new IndexedHashMap<String, NotificationData>());
    private Voice voice;
    private static String LOG_TAG = "ChatViewActivity";
    public static Queue<NotificationData> notifications;
    private volatile boolean notificationProcessingActive;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RelativeLayout llLayout = (RelativeLayout) inflater.inflate(R.layout.activity_card_view, container, false);

        mRecyclerView = (RecyclerView) llLayout.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(super.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        voice = new Voice(super.getActivity());
        notificationProcessingActive = true;
        notifications = new LinkedList<>();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mRecyclerView.setAdapter(mAdapter);
                super.handleMessage(msg);
            }
        };
        Thread notificationProcessor = new Thread() {
            @Override
            public void run() {
                while (notificationProcessingActive) {
                    NotificationData notificationData = notifications.poll();
                    if (notificationData != null) {
                        notifyUser(notificationData);
                    }
                }
            }
        };
        notificationProcessor.start();

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
        notificationProcessingActive = false;
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

    public void notifyUser(final NotificationData notificationData) {
        mAdapter.addItem(notificationData, notificationData.getTitle());
        Message msg = handler.obtainMessage();
        msg.obj = mAdapter;
        handler.sendMessage(msg);
        voice.read(notificationData.getTitle());
        voice.read(getString(R.string.read_message));
        voice.promptSpeechInput();
        new Thread() {
            @Override
            public void run() {
                if (voice.getVoiceInput().toLowerCase().equals(getString(R.string.yes))) {
                    voice.read(notificationData.getText());
                    voice.read(getString(R.string.ask_to_answer));
                    voice.promptSpeechInput();
                    if (voice.getVoiceInput().toLowerCase().equals(getString(R.string.yes))) {
                        voice.promptSpeechInput();
                        answerMessage(notificationData, voice.getVoiceInput());
                    }

                }
            }
        }.start();

    }

    private NotificationCompat.Action extractWearAction(Notification n) {
        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender(n);
        NotificationCompat.Action answerAction = null;
        for (NotificationCompat.Action action : wearableExtender.getActions()) {
            if (action.title.toString().toLowerCase().contains(getString(R.string.reply))) {
                answerAction = action;
            }
        }
        return answerAction;
    }
}