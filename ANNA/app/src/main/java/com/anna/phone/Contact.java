package com.anna.phone;

import android.graphics.drawable.Drawable;

/**
 * Created by PARSEA on 07.03.2017.
 */

public class Contact {

    private String name;
    private String phoneNumber;
    private Drawable image;

    public Contact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
