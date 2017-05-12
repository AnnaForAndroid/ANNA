package com.anna.dashboard;

/**
 * Created by D062427 on 02.11.2016.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.anna.chat.ChatViewActivity;
import com.anna.maps.HereMapsFragment;
import com.anna.music.MusicPlayer;
import com.anna.phone.Phone;
import com.anna.preferences.Preferences;

public class DashboardAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    public DashboardAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (Dashboard.tabOrder.get(position)) {
            case "Messenger":
                return new ChatViewActivity();
            case "Here Maps":
                return new HereMapsFragment();
            case "Settings":
                return new Preferences();
            case "Phone":
                return new Phone();
            case "Music":
                return new MusicPlayer();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}