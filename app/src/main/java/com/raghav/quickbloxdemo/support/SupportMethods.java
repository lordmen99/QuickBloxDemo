package com.raghav.quickbloxdemo.support;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

/**
 * Created by raghav.satyadev on 18/7/16.
 */
public class SupportMethods {
    public static int sessionSuccess = 0;

    public static void createSession(final QBListener qbListener) {
        if (sessionSuccess != 1 && sessionSuccess != 3) {
            sessionSuccess = 3;
            Log.d(Const.ErrorTag, "createSession:");
            QBAuth.createSession(new QBEntityCallback<QBSession>() {
                @Override
                public void onSuccess(QBSession session, Bundle params) {
                    sessionSuccess = 1;
                    Log.d(Const.ErrorTag, "onSuccess:" + "Success");
                    if (qbListener != null)
                        qbListener.onTaskCompleted(sessionSuccess);
                }

                @Override
                public void onError(QBResponseException error) {
                    sessionSuccess = 2;
                    Log.d(Const.ErrorTag, "onError:" + error.getMessage());
                    if (qbListener != null)
                        qbListener.onTaskCompleted(sessionSuccess);
                }
            });
        } else if (qbListener != null)
            qbListener.onTaskCompleted(sessionSuccess);

    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (!(netInfo != null && netInfo.isConnected())) {
            Toast.makeText(context, "No Internet Connection\nPlease Connect To Internet First", Toast.LENGTH_SHORT).show();
        }
        return netInfo != null && netInfo.isConnected();
    }

    public static boolean checkPlayServices(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity.getApplicationContext());
        Log.d(Const.ErrorTag, "checkPlayServices: " + resultCode);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (resultCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED || apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, Const.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(activity.getApplicationContext(), "This Device Does Not Support Google Play Services", Toast.LENGTH_LONG).show();
                activity.finish();
            }
            return false;
        }
        return true;
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void saveCredentials(String email, String password) {
        PrefsHelper prefsHelper = new PrefsHelper(AppController.getInstance());
        prefsHelper.setEmail(email);
        prefsHelper.setPassword(password);
    }

    public static ProgressDialog showProgressDialog(ProgressDialog progress, Context context) {
        if (progress == null) {
            progress = new ProgressDialog(context);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setMessage("Please Wait ...");
            progress.setCancelable(false);
        }
        return progress;
    }

    public static boolean isLoggedIn() {
        PrefsHelper prefsHelper = new PrefsHelper(AppController.getInstance());
        return prefsHelper.getEmail() != null && prefsHelper.getPassword() != null;
    }
}
