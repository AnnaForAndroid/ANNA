package com.anna;

/**
 * Created by D062427 on 02.11.2016.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class DashboardAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public DashboardAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (Dashboard.tabOrder.get(position)) {
            case "Messenger":
                ChatViewActivity tab1 = new ChatViewActivity();
                return tab1;
            case "Maps":
                MapsFragment tab2 = new MapsFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}