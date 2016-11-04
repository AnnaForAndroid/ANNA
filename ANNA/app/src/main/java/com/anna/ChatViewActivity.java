package com.anna;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
    private MyRecyclerViewAdapter mAdapter = new MyRecyclerViewAdapter(new IndexedHashMap<String, NotificationData>());
    private RecyclerView.LayoutManager mLayoutManager;
    private Voice voice;
    private static String LOG_TAG = "ChatViewActivity";
    public static ChatViewActivity chatViewActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FragmentActivity faActivity = (FragmentActivity) super.getActivity();
        RelativeLayout llLayout = (RelativeLayout) inflater.inflate(R.layout.activity_card_view, container, false);

        mRecyclerView = (RecyclerView) llLayout.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(super.getActivity());
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
        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(LOG_TAG, " Clicked on Item " + position);
            }
        });
    }

    public void answerMessage(String text, String packageName) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.setPackage(packageName);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(sendIntent);
    }

    public static class NotificationDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (chatViewActivity != null) {
                final NotificationData notificationData = (NotificationData) intent.getParcelableExtra("notificationData");
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
                                        chatViewActivity.answerMessage(notificationData.getText(), notificationData.getPackageName());
                                    }
                                }
                            }.start();
                        }
                    }
                }.start();
            }
        }
    }
}