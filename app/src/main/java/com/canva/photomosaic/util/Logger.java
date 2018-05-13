package com.canva.photomosaic.util;

import android.util.Log;

public class Logger {

    private static final String LOGGER = "Logger";

    public void d(String message){
        Log.d(LOGGER, message);
    }

    public void i(String message){
        Log.i("Logger", message);
    }

    public void e(String message){
        Log.e("Logger", message);
    }

}
