package com.raghav.quickbloxdemo.support;

import android.app.Application;

import com.quickblox.core.QBSettings;

/**
 * Created by raghav.satyadev on 18/7/16.
 */
public class AppController extends Application {
    private static AppController appController;

    public static synchronized AppController getInstance() {
        return appController;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appController = this;

        QBSettings.getInstance().init(getApplicationContext(), Const.QBCreds.APP_ID, Const.QBCreds.AUTH_KEY, Const.QBCreds.AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(Const.QBCreds.ACCOUNT_KEY);

        SupportMethods.createAppSession(null);
    }
}
