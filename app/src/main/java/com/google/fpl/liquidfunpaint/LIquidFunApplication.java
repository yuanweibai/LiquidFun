package com.google.fpl.liquidfunpaint;

import android.app.Application;

public class LIquidFunApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Explicitly load all shared libraries for Android 4.1 (Jelly Bean)
        // Or we could get a crash from dependencies.
        System.loadLibrary("liquidfun");
        System.loadLibrary("liquidfun_jni");
    }
}
