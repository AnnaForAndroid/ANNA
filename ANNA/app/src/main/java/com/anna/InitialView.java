package com.anna;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.anna.modules.GoogleMaps;
import com.anna.modules.WhatsApp;

public class InitialView extends AppCompatActivity {

    private ModuleAdapter adapter;
    private final String prefFileName = "modules";
    private boolean finished = false;
    private final int PERMISSIONS_REQUEST_AUDIO = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            accessPermissions();
        }

        displayListView();

        checkButtonClick();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (finished) {
            Intent intent = new Intent(InitialView.this, ChatViewActivity.class);
            InitialView.this.startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_REQUEST_AUDIO:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //readContacts();

                } else {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.RECORD_AUDIO)) {
                        new AlertDialog.Builder(this).
                                setTitle("Record Audio").
                                setMessage("You need to grant record audio permission to use speach" +
                                        " recognition feature. Retry and grant it !").show();
                    } else {
                        new AlertDialog.Builder(this).
                                setTitle("Record Audio permission denied").
                                setMessage("You denied record audio permission." +
                                        " So, the feature will be disabled. To enable it" +
                                        ", go on settings and " +
                                        "grant record audio for the application").show();
                    }

                }

                break;
        }
    }

    private void displayListView() {

        ArrayList<Module> moduleList = new ArrayList<Module>();

        PackageManager pm = getApplicationContext().getPackageManager();
        List<ApplicationInfo> appsInfos = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<String> appNames = new ArrayList<String>();

        for (ApplicationInfo info : appsInfos) {
            appNames.add(pm.getApplicationLabel(info).toString());
        }

        if (appNames.contains("WhatsApp")) {
            moduleList.add(new WhatsApp());
        }

        if (appNames.contains("Maps")) {
            moduleList.add(new GoogleMaps());
        }

        adapter = new ModuleAdapter(this,
                R.layout.listitem, moduleList);
        ListView listView = (ListView) findViewById(R.id.list);

        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Module module = (Module) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + module.getName(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private class ModuleAdapter extends ArrayAdapter<Module> {

        private ArrayList<Module> moduleList;

        public ModuleAdapter(Context context, int textViewResourceId,
                             ArrayList<Module> moduleList) {
            super(context, textViewResourceId, moduleList);
            this.moduleList = new ArrayList<Module>();
            this.moduleList.addAll(moduleList);
        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
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
                holder.code = (TextView) convertView.findViewById(R.id.textView1);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Module module = (Module) cb.getTag();
//                        Toast.makeText(getApplicationContext(),
//                                "Clicked on Checkbox: " + cb.getText() +
//                                        " is " + cb.isChecked(),
//                                Toast.LENGTH_LONG).show();
                        module.setSelected(cb.isChecked());
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Module module = moduleList.get(position);
            holder.name.setText(module.getName());
            holder.name.setChecked(module.isEnabled());
            holder.name.setTag(module);

            return convertView;

        }

    }

    private void checkButtonClick() {

        Button myButton = (Button) findViewById(R.id.button);
        myButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                SharedPreferences preferences = getSharedPreferences(prefFileName, 0);
                SharedPreferences.Editor editor = preferences.edit();

                ArrayList<Module> moduleList = adapter.moduleList;
                for (int i = 0; i < moduleList.size(); i++) {
                    Module module = moduleList.get(i);
                    editor.putBoolean(module.getName(), module.isEnabled());
                    if (module.isEnabled()) {
                        responseText.append("\n" + module.getName());
                    }
                }

                editor.commit();

                Toast.makeText(getApplicationContext(),
                        responseText, Toast.LENGTH_LONG).show();
                if (preferences.getBoolean("WhatsApp", false) && !(NotificationService.isNotificationAccessEnabled)) {
                    finished = true;
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                } else {
                    Intent intent = new Intent(InitialView.this, ChatViewActivity.class);
                    InitialView.this.startActivity(intent);
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void accessPermissions() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSIONS_REQUEST_AUDIO);
        }
    }
}
