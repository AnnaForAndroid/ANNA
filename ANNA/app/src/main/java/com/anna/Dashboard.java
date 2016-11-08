package com.anna;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class Dashboard extends AppCompatActivity {
    public static List<String> tabOrder = new ArrayList<>();
    private long lastInteraction;
    private int FULLSCREEN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        Module.loadModules();
        boolean messenger = true;
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

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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
                        e.printStackTrace();
                    }
                    if (getLastInteractionTime() > 3000) {
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

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == FULLSCREEN) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE);
            }
            super.handleMessage(msg);
        }
    };
}
