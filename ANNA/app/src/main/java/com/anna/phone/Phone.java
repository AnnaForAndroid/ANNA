package com.anna.phone;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import android.widget.ListView;

import com.anna.R;
import com.anna.util.MyApplication;
import com.anna.voice.DictionaryExtender;

/**
 * Created by PARSEA on 22.11.2016.
 */

public class Phone extends Fragment {

    private ProgressDialog pDialog;
    private Handler updateBarHandler;
    private ArrayList<Contact> contactList;
    private volatile ListView mListView;
    private Cursor cursor;
    private int counter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RelativeLayout rlLayout = (RelativeLayout) inflater.inflate(R.layout.contacts_layout, container, false);

        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Reading contacts...");
        pDialog.setCancelable(false);
        pDialog.show();
        updateBarHandler = new Handler();
        // Since reading contacts takes more time, let's run it on a separate thread.
        if (contactList == null || contactList.isEmpty()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getContacts();
                }
            }).start();
        }

        return rlLayout;
    }

    public void call(String callTo) {
        String number = getNumber(callTo);
        if (number != null) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));
            EndCallListener callListener = new EndCallListener();
            TelephonyManager mTM = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
            mTM.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);
            if (ActivityCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                MyApplication.getAppContext().startActivity(callIntent);
            }
        }
    }

    private String getNumber(String callTo) {
        String pattern = "([+0-9])+";
        if (callTo.matches(pattern)) {
            return callTo;
        } else {
            for (Contact contact : contactList) {
                if (contact.getName().equals(callTo)) {
                    return contact.getPhoneNumber();
                }
            }
        }
        return null;
    }


    protected void sendSMSMessage(String phoneNo, String message) {

        String number = getNumber(phoneNo);
        if (number != null) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message, null, null);
            Toast.makeText(MyApplication.getAppContext().getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void getContacts() {
        contactList = new ArrayList<>();
        String phoneNumber = null;
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        ContentResolver contentResolver = getContext().getContentResolver();
        cursor = contentResolver.query(CONTENT_URI, null, null, null, null);
        // Iterate every contact in the phone
        if (cursor.getCount() > 0) {
            counter = 0;
            while (cursor.moveToNext()) {
                // Update the progress message
                updateBarHandler.post(new Runnable() {
                    public void run() {
                        pDialog.setMessage("Reading contacts : " + counter++ + "/" + cursor.getCount());
                    }
                });
                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    //This is to read multiple phone numbers associated with the same contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                    }
                    phoneCursor.close();
                }
                // Add the contact to the ArrayList
                if (name != null && !name.contains("@") && phoneNumber != null) {
                    contactList.add(new Contact(name, phoneNumber));
                }
            }
            // ListView has to be updated using a ui thread
            MyApplication.dashboard.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ContactsAdapter adapter = new ContactsAdapter(contactList, getContext());
                    mListView = (ListView) getActivity().findViewById(R.id.contacts_list);
                    mListView.setAdapter(adapter);
                }
            });
            // Dismiss the progressbar after 500 millisecondds
            updateBarHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pDialog.cancel();
                }
            }, 500);
            DictionaryExtender dictionaryEnhancer = new DictionaryExtender();
            dictionaryEnhancer.createContactsGrammar(contactList);
        }
    }
}
