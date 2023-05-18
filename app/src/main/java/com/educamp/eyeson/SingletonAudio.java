package com.educamp.eyeson;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;

public class SingletonAudio {
    static MediaPlayer ref;
    private static SingletonAudio ourInstance = new SingletonAudio();
    private Context appContext;
    private SingletonAudio() {

    }
    public static Context get() {
        return getInstance().getContext();
    }
    public static synchronized SingletonAudio getInstance() {
        return ourInstance;
    }
    public void init(Context context) {
        if (appContext == null) {
            this.appContext = context;
        }
    }
    private Context getContext() {
        return appContext;
    }
    @SuppressLint("SuspiciousIndentation")
    public static MediaPlayer getSingletonMedia(Integer audio) {
        if (ref == null)
        // it's ok, we can call this constructor
        ref = MediaPlayer.create(get(),audio);
        return ref;
    }
}
