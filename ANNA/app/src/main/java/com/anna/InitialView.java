package com.anna;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anna.dashboard.Dashboard;
import com.anna.notification.NotificationService;
import com.anna.preferences.PreferencesHelper;
import com.anna.util.LayoutConfig;
import com.anna.util.Module;
import com.anna.util.MyApplication;
import com.anna.voice.VoiceOutput;

import java.util.ArrayList;
import java.util.List;

public class InitialView extends AppCompatActivity {

    private ModuleAdapter adapter;
    private PreferencesHelper sharedPrefs;
    private boolean setupFinished;
    private VoiceOutput voiceOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefs = MyApplication.application.getSharedPreferences();
        setupFinished = (boolean) sharedPrefs.getPreferences("setupFinished", Boolean.class);
        checkForFirstUse();
    }

    public void checkForFirstUse() {
        if (setupFinished) {
            Intent intent = new Intent(InitialView.this, Dashboard.class);
            InitialView.this.startActivity(intent);
        } else {
            setContentView(R.layout.activity_initial);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            voiceOutput = MyApplication.application.getVoiceOutput();
            Module.initializeModules();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                accessPermissions();
            }
            displayListView();
            checkButtonClick();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (setupFinished) {
            Intent intent = new Intent(InitialView.this, Dashboard.class);
            InitialView.this.startActivity(intent);
        }
    }

    private void displayListView() {

        adapter = new ModuleAdapter(this,
                R.layout.listitem, Module.modules);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                parent.getItemAtPosition(position);
            }
        });
        LayoutConfig.setListViewHeightBasedOnChildren(listView);
    }

    private class ModuleAdapter extends ArrayAdapter<Module> {

        private ArrayList<Module> moduleList;

        private ModuleAdapter(Context context, int textViewResourceId,
                              List<Module> moduleList) {
            super(context, textViewResourceId, moduleList);
            this.moduleList = new ArrayList<>();
            this.moduleList.addAll(moduleList);
        }

        private class ViewHolder {
            private TextView text;
            private CardView card;
            private ImageView icon;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            ViewHolder holder;
            View tempConvertView = convertView;
            if (BuildConfig.DEBUG) {
                Log.v("ConvertView", String.valueOf(position));
            }
            if (tempConvertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                tempConvertView = vi.inflate(R.layout.listitem, parent, false);

                holder = new ViewHolder();
                holder.text = (TextView) tempConvertView.findViewById(R.id.initialView_installedApps);
                holder.card = (CardView) tempConvertView.findViewById(R.id.initialView_cards);
                holder.icon = (ImageView) tempConvertView.findViewById(R.id.initialView_appIcon);
                tempConvertView.setTag(holder);

                holder.card.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CardView cb = (CardView) v;
                        Module module = (Module) cb.getTag();
                        module.setSelected(!module.isEnabled());
                        if (module.isEnabled()) {
                            cb.setCardBackgroundColor(getResources().getColor(R.color.selected_cards));
                        } else {
                            cb.setCardBackgroundColor(getResources().getColor(R.color.white));
                        }
                    }
                });
            } else {
                holder = (ViewHolder) tempConvertView.getTag();
            }

            Module module = moduleList.get(position);
            holder.text.setText(module.getName());
            holder.icon.setImageDrawable(module.getIcon());
            holder.card.setTag(module);

            return tempConvertView;
        }

    }

    private void checkButtonClick() {

        Button myButton = (Button) findViewById(R.id.button);
        myButton.setText(getString(R.string.initial_view_button));
        myButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Module.enabledAppNames.size() == 0) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.select_one_module), Toast.LENGTH_LONG).show();
                } else {
                    StringBuffer responseText = new StringBuffer();
                    responseText.append(getString(R.string.selected));

                    ArrayList<Module> moduleList = adapter.moduleList;
                    for (int i = 0; i < moduleList.size(); i++) {
                        Module module = moduleList.get(i);
                        if (module.isEnabled()) {
                            responseText.append('\n').append(module.getName());
                        }
                    }

                    Toast.makeText(getApplicationContext(),
                            responseText, Toast.LENGTH_LONG).show();
                    setupFinished = true;
                    sharedPrefs.savePreferences("setupFinished", true, Boolean.class);
                    if (!(NotificationService.isNotificationAccessEnabled)) {
                        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                    } else {
                        Intent intent = new Intent(InitialView.this, Dashboard.class);
                        InitialView.this.startActivity(intent);
                    }
                }
            }
        });

        voiceOutput.read(getString(R.string.init_stmt));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void accessPermissions() {
        List<String> permissions = new ArrayList<>();
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CALL_PHONE);
        }
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_CONTACTS);
        }
        if (checkSelfPermission(Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.SEND_SMS);
        }
        if (permissions.size() > 0) {
            String[] permissionArray = new String[permissions.size()];
            permissions.toArray(permissionArray);
            requestPermissions(permissionArray, 1234567);
        }
    }
}
