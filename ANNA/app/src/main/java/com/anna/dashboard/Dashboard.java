package com.anna.dashboard;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.anna.R;
import com.anna.util.Module;

import java.util.ArrayList;
import java.util.List;


public class Dashboard extends AppCompatActivity {
    public static List<String> tabOrder = new ArrayList<>();
    private long lastInteraction;
    private static final int FULLSCREEN = 1;
    private final Handler handler = new FullscreenHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        Module.loadModules();
        boolean messenger = true;
        tabLayout.addTab(tabLayout.newTab().setText("Settings"));
        tabOrder.add("Settings");
        for (String name : Module.enabledAppNames) {
            if ("Maps".equals(name)) {
                tabLayout.addTab(tabLayout.newTab().setText(name));
                tabOrder.add(name);
            }
            if (messenger) {
                tabLayout.addTab(tabLayout.newTab().setText("Messenger"));
                messenger = false;
                tabOrder.add("Messenger");
            }
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final DashboardAdapter adapter = new DashboardAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //Required for interface
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //Required for interface
            }
        });
        startDetectUserInactivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    public void startDetectUserInactivity() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        Log.e("InterruptedException", e.toString());
                    }
                    if (getLastInteractionTime() > 5000) {
                        Message msg = handler.obtainMessage();
                        msg.what = FULLSCREEN;
                        handler.sendMessage(msg);
                    }
                }
            }
        }).start();
    }

    public long getLastInteraction() {
        return lastInteraction;
    }

    public long getLastInteractionTime() {
        return System.currentTimeMillis() - getLastInteraction();
    }

    public void setLastInteractionTime() {
        lastInteraction = System.currentTimeMillis();
    }

    public void onUserInteraction() {
        setLastInteractionTime();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        setLastInteractionTime();
    }

    private static class FullscreenHandler extends Handler {

        private Dashboard dashboardObject;

        private FullscreenHandler(Dashboard dashboardObject) {
            this.dashboardObject = dashboardObject;
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == FULLSCREEN) {
                dashboardObject.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE);
            }
            super.handleMessage(msg);
        }
    }
}
