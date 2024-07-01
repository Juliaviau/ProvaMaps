package com.example.provamaps;

import android.content.Context;
import android.widget.Toast;

public class MyUtils {

    public static final String USER_TYPE_GOOGLE = "Google";
    public static final String USER_TYPE_EMAIL = "Email";



    public static void toast(Context context, String missatge) {
        Toast.makeText(context, missatge, Toast.LENGTH_SHORT).show();
    }

    public static long timestamp () {
        return System.currentTimeMillis();
    }
}
