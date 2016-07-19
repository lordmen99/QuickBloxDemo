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
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.raghav.quickbloxdemo.R;
import com.raghav.quickbloxdemo.support.Const;
import com.raghav.quickbloxdemo.support.QBListener;
import com.raghav.quickbloxdemo.support.SupportMethods;

/**
 * Created by raghav.satyadev on 18/7/16.
 */
public class RegisterActivity extends AppCompatActivity {
    private EditText edEmail, edUserName, edPassword, edConfPassword;
    private String email;
    private String username;
    private String password;
    private RegisterActivity context;
    private ProgressDialog progress;
    private ScrollView scrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = this;

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        edEmail = (EditText) findViewById(R.id.edEmail);
        edUserName = (EditText) findViewById(R.id.edUserName);
        edPassword = (EditText) findViewById(R.id.edPassword);
        edConfPassword = (EditText) findViewById(R.id.edConfPassword);
    }

    public void register(final View view) {
        progress = SupportMethods.showProgressDialog(progress, context);
        if (!progress.isShowing())
            progress.show();
        SupportMethods.createSession(new QBListener() {
            @Override
            public void onTaskCompleted(int sessionSuccess) {
                if (sessionSuccess == 1) {
                    if (validation(view)) {
                        final QBUser user = new QBUser(username, password);

                        user.setEmail(email);
                        user.setFullName(username);

                        StringifyArrayList<String> tags = new StringifyArrayList<>();
                        tags.add(Const.QBTAG);
                        user.setTags(tags);

                        QBUsers.signUp(user, new QBEntityCallback<QBUser>() {
                            @Override
                            public void onSuccess(QBUser user, Bundle args) {
                                Log.d(Const.ErrorTag, "onSuccess:" + "user Registered");
                                login();
                                progress.dismiss();
                            }

                            @Override
                            public void onError(QBResponseException errors) {
                                Log.d(Const.ErrorTag, "onError:" + errors.getMessage());
                                SupportMethods.showSnackBar(scrollView, errors.getMessage());
                                progress.dismiss();
                            }
                        });
                    }
                } else if (sessionSuccess == 3) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            register(view);
                        }
                    }, 5000);
                } else {
                    progress.dismiss();
                    Log.d(Const.ErrorTag, "register:" + "Error in QBSession Creation");
                }
            }
        });
    }

    private boolean validation(View view) {
        email = edEmail.getText().toString();
        username = edUserName.getText().toString();
        password = edPassword.getText().toString();
        String confPassword = edConfPassword.getText().toString();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confPassword)) {
            if (SupportMethods.isValidEmail(email)) {
                if (password.equals(confPassword)) {
                    return true;
                } else {
                    SupportMethods.showSnackBar(view, "Password and Confirm Password must match");
                }
            } else {
                SupportMethods.showSnackBar(view, "Please enter valid email ID");
            }
        } else {
            SupportMethods.showSnackBar(view, "Fields can not be empty");
        }
        return false;
    }

    private void login() {
        SupportMethods.saveCredentials(email, password);
        gotoLogin(null);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    public void gotoLogin(View view) {
        startActivity(new Intent(context, LoginActivity.class));
        finish();
    }
}
