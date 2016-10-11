package com.anna;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.anna.modules.Module;
import com.anna.modules.WhatsApp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class initial extends AppCompatActivity {

    private ModuleAdapter adapter;
    private final String prefFileName = "modules";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        displayListView();

        checkButtonClick();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_initial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayListView() {

        ArrayList<Module> moduleList = new ArrayList<Module>();
        Module module = new WhatsApp();
        moduleList.add(module);
        module = new GoogleMaps();
        moduleList.add(module);

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
                        Toast.makeText(getApplicationContext(),
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();
                        module.setSelected(cb.isChecked());
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Module module = moduleList.get(position);
            //holder.code.setText(" (" + module.getCode() + ")");
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

                ArrayList<Module> moduleList = adapter.moduleList;
                for (int i = 0; i < moduleList.size(); i++) {
                    Module module = moduleList.get(i);
                    if (module.isEnabled()) {
                        responseText.append("\n" + module.getName());
                    }
                }

                Toast.makeText(getApplicationContext(),
                        responseText, Toast.LENGTH_LONG).show();

            }
        });

    }
}
