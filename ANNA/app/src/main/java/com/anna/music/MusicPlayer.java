package com.anna.music;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.anna.util.MyApplication;

/**
 * Created by PARSEA on 29.03.2017.
 */

public class MusicPlayer extends Fragment{

    public void startMusicPlayer() {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
        MyApplication.application.startActivity(intent);
    }
}