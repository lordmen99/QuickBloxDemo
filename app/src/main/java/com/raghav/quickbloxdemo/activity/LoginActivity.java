package com.raghav.quickbloxdemo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.raghav.quickbloxdemo.R;
import com.raghav.quickbloxdemo.support.Const;
import com.raghav.quickbloxdemo.support.PrefsHelper;
import com.raghav.quickbloxdemo.support.QBListener;
import com.raghav.quickbloxdemo.support.SupportMethods;

public class LoginActivity extends AppCompatActivity {

    private LoginActivity context;
    private EditText edEmail, edPassword;
    private String email, password;
    private ProgressDialog progress;
    private ScrollView scrollView;
    private PrefsHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        prefsHelper = new PrefsHelper(context);

        edEmail = (EditText) findViewById(R.id.edEmail);
        edPassword = (EditText) findViewById(R.id.edPassword);

        if (SupportMethods.isLoggedIn()) {
            getCredentials();
        }
    }

    private void getCredentials() {
        email = prefsHelper.getEmail();
        password = prefsHelper.getPassword();
        if (email != null && password != null) {
            signIn();
        }
    }

    public void gotoRegister(View view) {
        startActivity(new Intent(context, RegisterActivity.class));
        finish();
    }


    public void login(View view) {
        email = edEmail.getText().toString();
        password = edPassword.getText().toString();
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            if (SupportMethods.isValidEmail(email)) {
                signIn();
            } else {
                SupportMethods.showSnackBar(view, "Please enter valid email ID");
            }
        } else {
            SupportMethods.showSnackBar(view, "Fields can not be empty");
        }
    }

    public void signIn() {
        progress = SupportMethods.showProgressDialog(progress, context);
        if (!progress.isShowing())
            progress.show();

        SupportMethods.createSession(new QBListener() {
            @Override
            public void onTaskCompleted(int sessionSuccess) {
                if (sessionSuccess == 1) {
                    final QBUser user = new QBUser();
                    user.setEmail(email);
                    user.setPassword(password);
                    QBUsers.signIn(user, new QBEntityCallback<QBUser>() {
                        @Override
                        public void onSuccess(QBUser user, Bundle args) {
                            Log.d(Const.ErrorTag, "onSuccess:" + "user login");
                            gotoUserList();
                            progress.dismiss();
                        }

                        @Override
                        public void onError(QBResponseException error) {
                            Log.d(Const.ErrorTag, "onError:" + error.getMessage());
                            SupportMethods.showSnackBar(scrollView, error.getMessage());
                            progress.dismiss();
                        }
                    });
                } else if (sessionSuccess == 3) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            signIn();
                        }
                    }, 5000);
                } else {
                    progress.dismiss();
                    Log.d(Const.ErrorTag, "register:" + "Error in QBSession Creation");
                }
            }
        });


    }

    private void gotoUserList() {
        startActivity(new Intent(context, UserListActivity.class));
        finish();
    }
}
