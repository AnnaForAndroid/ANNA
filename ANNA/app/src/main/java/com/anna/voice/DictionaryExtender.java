package com.anna.voice;

import com.anna.phone.Contact;
import com.anna.util.MyApplication;

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

    public void createContactsGrammar(List<Contact> contacts) {
        Assets assets = null;
        try {
            assets = new Assets(MyApplication.getAppContext());
            File assetDir = assets.syncAssets();
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(assetDir, "contacts.gram"), true));
            StringBuffer sb = new StringBuffer();
            sb.append("public <contact> = ");
            for (Contact contact : contacts) {
                sb.append(contact.getName().toLowerCase() + " | ");
                addToDictionary(contact.getName().toLowerCase());
            }
            sb.replace(sb.lastIndexOf("|"), sb.lastIndexOf("|") + 1, ";");
            writer.append(sb.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addToDictionary(String word) {
        String[] parts = word.split(" ");
        try {
            Assets assets = new Assets(MyApplication.getAppContext());
            File assetDir = assets.syncAssets();
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(assetDir, "cmusphinx-voxforge-de.dic"), true));
            StringBuffer sb = new StringBuffer();
            for (String part : parts) {
                String phonetic = getPhonetic(part);
                sb.append("\n" + part + " " + phonetic);
            }
            writer.append(sb.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPhonetic(String word) {
        String result = "";
        for (int i = 0; i < word.length(); i++) {
            result += word.toUpperCase().charAt(i) + " ";
        }
        return result;
    }
}
