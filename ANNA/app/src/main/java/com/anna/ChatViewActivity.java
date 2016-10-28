package com.anna;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.anna.util.IndexedHashMap;
import com.anna.util.Voice;

import java.util.Date;


public class ChatViewActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Voice voice;
    private static String LOG_TAG = "ChatViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(new IndexedHashMap<String, NotificationData>());
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));
        voice = new Voice(this);
        // Code to Add an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).addItem(obj, index);

        // Code to remove an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).deleteItem(index);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        ChatViewActivity.super.onActivityResult(requestCode, resultCode, data);
        voice.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onResume() {
        super.onResume();
        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(LOG_TAG, " Clicked on Item " + position);
            }
        });
    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String pack = intent.getStringExtra("package");
            final String title = intent.getStringExtra("title");
            final String text = intent.getStringExtra("text");
            final Bitmap icon = intent.getParcelableExtra("icon");
            final Date time = new Date(intent.getLongExtra("time", 0));
            final String app = intent.getStringExtra("app");
            mAdapter.addItem(new NotificationData(title, text, icon, time, app), title);
            mRecyclerView.setAdapter(mAdapter);
            voice.read(title);
            voice.read(getString(R.string.read_message));
            voice.setStatus(false);
            voice.promptSpeechInput();
            new Thread() {
                @Override
                public void run() {
                        if (voice.getVoiceInput().toLowerCase().equals("ja")) {
                            voice.read(text);
                        }
                }
            }.start();
        }
    };
}