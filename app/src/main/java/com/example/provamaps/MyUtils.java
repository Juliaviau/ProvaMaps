package com.example.provamaps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MyUtils {

    public static final String USER_TYPE_GOOGLE = "Google";
    public static final String USER_TYPE_EMAIL = "Email";

    public static void toast(Context context, String missatge) {
        Toast.makeText(context, missatge, Toast.LENGTH_SHORT).show();
    }

    public static long timestamp () {
        return System.currentTimeMillis();
    }

    public static void aplicarAnimacioClick(View view) {
        view.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction(() -> {
                    view.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                })
                .start();
    }

}
