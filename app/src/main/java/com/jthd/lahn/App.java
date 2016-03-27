package com.jthd.lahn;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.parse.Parse;


public class App extends Application {


    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        // Register any ParseObject subclass. Must be done before calling Parse.initialize()

        Parse.initialize(this, "kPsctw0p1qYWgNAO4UtOdJyldzpWaLk8dyRddeI4", "34hWl7hMZrfqWw1H1T1hVSXJtGtyPQ1OEtBiuzlK");
        App.context = getApplicationContext();
    }

    public static Context getAppContext(){
        return App.context;
    }
}