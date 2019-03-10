package com.kakaoapi.myapplication;

import android.util.Log;

public class PhoneStyle {

    private static String TAG = "PhoneStyle";

    static public int width;
    static public int height;

    static public void getDisplay(int x , int y) {
        width = x;
        height = y;
        Log.d(TAG, "getDisplay ==> " + width +" :: "+ height);
    }



}
