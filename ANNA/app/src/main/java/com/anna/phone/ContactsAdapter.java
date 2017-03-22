package com.anna.phone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.anna.R;

import java.util.ArrayList;

/**
 * Created by PARSEA on 07.03.2017.
 */

public class ContactsAdapter extends ArrayAdapter<Contact> {

    // View lookup cache
    private static class ViewHolder {
        private TextView contactName;
    }

    protected ContactsAdapter(ArrayList<Contact> data, Context context) {
        super(context, R.layout.contacts_card, data);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Contact contact = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.contacts_card, parent, false);
            viewHolder.contactName = (TextView) convertView.findViewById(R.id.contact_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (contact != null) {
            viewHolder.contactName.setText(contact.getName());
        }
        return convertView;
    }
}
