package com.anna;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.anna.util.LayoutConfig;
import com.anna.util.PreferencesHelper;
import com.anna.util.Voice;

public class InitialView extends AppCompatActivity {

    private ModuleAdapter adapter;
    private PreferencesHelper sharedPrefs;
    private boolean setupFinished;
    private final int PERMISSIONS_REQUEST_AUDIO = 123;
    private Voice voice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPrefs = new PreferencesHelper(getApplicationContext(), "annaPreferences");
        setupFinished = (boolean) sharedPrefs.getPreferences("setupFinished", Boolean.class);
        this.voice = new Voice(this);
        checkForFirstUse();
    }

    public void checkForFirstUse() {
        if (setupFinished) {
            Intent intent = new Intent(InitialView.this, Dashboard.class);
            InitialView.this.startActivity(intent);
        } else {
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_REQUEST_AUDIO:
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.RECORD_AUDIO)) {
                        new AlertDialog.Builder(this).
                                setTitle(getString(R.string.record_audio)).
                                setMessage(getString(R.string.record_audio_denied_text)).show();
                    } else {
                        new AlertDialog.Builder(this).
                                setTitle(getString(R.string.audio_denied)).
                                setMessage(getString(R.string.audio_record_permission_denied)).show();
                    }
                }
                break;
            default:
                Log.e("RequestCode", String.valueOf(requestCode));
                break;
        }
    }

    private void displayListView() {

        PackageManager pm = getApplicationContext().getPackageManager();
        List<ApplicationInfo> appsInfos = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo info : appsInfos) {
            String appLabel = pm.getApplicationLabel(info).toString();
            if (Module.moduleNames.contains(appLabel)) {
                new Module(appLabel, info.packageName);
            }
        }

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

        public ModuleAdapter(Context context, int textViewResourceId,
                             List<Module> moduleList) {
            super(context, textViewResourceId, moduleList);
            this.moduleList = new ArrayList<Module>();
            this.moduleList.addAll(moduleList);
        }

        private class ViewHolder {
            protected TextView text;
            protected CardView card;
            protected ImageView icon;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.listitem, null);

                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.initialView_installedApps);
                holder.card = (CardView) convertView.findViewById(R.id.initialView_cards);
                holder.icon = (ImageView) convertView.findViewById(R.id.initialView_appIcon);
                convertView.setTag(holder);

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
                holder = (ViewHolder) convertView.getTag();
            }

            Module module = moduleList.get(position);
            holder.text.setText(module.getName());
            holder.icon.setImageDrawable(module.getIcon());
            holder.card.setTag(module);

            return convertView;
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
                            responseText.append("\n" + module.getName());
                        }
                    }

                    Toast.makeText(getApplicationContext(),
                            responseText, Toast.LENGTH_LONG).show();
                    setupFinished = true;
                    sharedPrefs.savePreferences("setupFinished", setupFinished, Boolean.class);
                    if (!(NotificationService.isNotificationAccessEnabled)) {
                        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                    } else {
                        Intent intent = new Intent(InitialView.this, Dashboard.class);
                        InitialView.this.startActivity(intent);
                    }
                }
            }
        });

        voice.read(getString(R.string.init_stmt));

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void accessPermissions() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSIONS_REQUEST_AUDIO);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (setupFinished) {
            voice.killService();
        }
    }
}
