package com.anna.voice;

import android.content.Context;
import android.util.Log;

import com.anna.phone.Contact;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.cmu.pocketsphinx.Assets;

/**
 * Created by PARSEA on 08.03.2017.
 */

public class DictionaryExtender {

    private Context context;

    public DictionaryExtender(Context context) {
        this.context = context;
    }

    public void createContactsGrammar(List<Contact> contacts) {

        try {
            Assets assets = new Assets(context);
            File assetDir = assets.syncAssets();
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(assetDir, "contacts.gram"), true));
            StringBuilder sb = new StringBuilder();
            sb.append("public <contact> = ");
            for (Contact contact : contacts) {
                sb.append(contact.getName().toLowerCase());
                sb.append(" | ");
                addToDictionary(contact.getName().toLowerCase());
            }
            sb.replace(sb.lastIndexOf("|"), sb.lastIndexOf("|") + 1, ";");
            writer.append(sb.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Log.e("DictionaryExtender: ", e.toString());
        }
    }

    private void addToDictionary(String word) {
        String[] parts = word.split(" ");
        try {
            Assets assets = new Assets(context);
            File assetDir = assets.syncAssets();
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(assetDir, "cmusphinx-voxforge-de.dic"), true));
            StringBuilder sb = new StringBuilder();
            for (String part : parts) {
                String phonetic = getPhonetic(part);
                sb.append('\n');
                sb.append(part);
                sb.append(' ');
                sb.append(phonetic);
            }
            writer.append(sb.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Log.e("", e.toString());
        }
    }

    private String getPhonetic(String word) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            result.append(word.toUpperCase().charAt(i));
            result.append(' ');
        }
        return result.toString();
    }
}
