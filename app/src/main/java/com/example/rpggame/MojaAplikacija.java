package com.example.rpggame;

import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class MojaAplikacija extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }
}