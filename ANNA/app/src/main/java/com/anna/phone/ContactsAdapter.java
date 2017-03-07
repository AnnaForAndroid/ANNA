package com.anna.phone;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anna.R;

import java.util.ArrayList;

/**
 * Created by PARSEA on 07.03.2017.
 */

public class ContactsAdapter extends ArrayAdapter<Contact> {
    private ArrayList<Contact> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView contactName;
    }

    public ContactsAdapter(ArrayList<Contact> data, Context context) {
        super(context, R.layout.contacts_card, data);
        this.dataSet = data;
        this.mContext = context;

    }

//    @Override
//    public void onClick(View v) {
//
//        int position = (Integer) v.getTag();
//        Object object = getItem(position);
//        Contact dataModel = (Contact) object;
//
//        switch (v.getId()) {
//            case R.id.item_info:
//                Snackbar.make(v, "Release date " + dataModel.getFeature(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
//                break;
//        }
//    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Contact contact = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.contacts_card, parent, false);
            viewHolder.contactName = (TextView) convertView.findViewById(R.id.contact_name);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        //Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        //result.startAnimation(animation);
        lastPosition = position;

        viewHolder.contactName.setText(contact.getName());
        //viewHolder.info.setOnClickListener(this);
        //viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
