package com.anna.project.anna;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.list);
        String[] items = {"WhatsApp", "Google Maps"};
        arrayList = new ArrayList<String>(Arrays.asList(items));
        adapter = new ArrayAdapter<String>(this,R.layout.listitem, R.id.checkBox, arrayList);
        listView.setAdapter(adapter);
    }
}