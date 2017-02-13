package com.anna.phone;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.anna.util.MyApplication;

/**
 * Created by PARSEA on 22.11.2016.
 */

public class PhoneCall {

    private Context context;

    public void call(String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        EndCallListener callListener = new EndCallListener();
        TelephonyManager mTM = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        mTM.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);
        if (ActivityCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            MyApplication.getAppContext().startActivity(callIntent);
        }
    }

    public void fetchContact(String contactName) {
        // Define the fields that the query will return
        String[] PROJECTION = new String[]{
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        ContentResolver cr = context.getContentResolver();

        // Execute the query and receive the cursor with the results
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?",
                new String[]{contactName},
                null);

        // First discover the index of the desired field (Number)
        final int indexNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        String number = cursor.getString(indexNumber);
    }
}
