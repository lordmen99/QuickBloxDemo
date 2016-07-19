package com.raghav.quickbloxdemo.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.quickblox.core.Consts;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.raghav.quickbloxdemo.R;
import com.raghav.quickbloxdemo.adapter.UserListAdapter;
import com.raghav.quickbloxdemo.support.Const;
import com.raghav.quickbloxdemo.support.PrefsHelper;
import com.raghav.quickbloxdemo.support.QBListener;
import com.raghav.quickbloxdemo.support.SupportMethods;

import java.util.ArrayList;

/**
 * Created by raghav.satyadev on 18/7/16.
 */
public class UserListActivity extends AppCompatActivity implements QBEntityCallback<ArrayList<QBUser>> {
    static int userNumber = 1;
    private UserListActivity context;
    private ArrayList<String> users;
    private UserListAdapter userListAdapter;
    private PrefsHelper prefsHelper;
    private ArrayList<String> userTags;
    private ProgressDialog progress;
    private String QBemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        context = this;
        users = new ArrayList<>();
        userListAdapter = new UserListAdapter(users);
        prefsHelper = new PrefsHelper(context);

        userTags = new ArrayList<>();
        userTags.clear();
        userTags.add(Const.QBTAG);

        QBemail = prefsHelper.getEmail();

        getUsers();

        RecyclerView packageList = (RecyclerView) findViewById(R.id.tagList);
        packageList.setLayoutManager(new LinearLayoutManager(this));
        packageList.setHasFixedSize(true);
        packageList.setAdapter(userListAdapter);
    }

    private void getUsers() {
        progress = SupportMethods.showProgressDialog(progress, context);
        if (!progress.isShowing())
            progress.show();
        SupportMethods.createSession(new QBListener() {
            @Override
            public void onTaskCompleted(int sessionSuccess) {
                if (sessionSuccess == 1) {
                    userListAdapter.deleteAll();
                    retrieveAllUsersFromPage(1);
                } else if (sessionSuccess == 3) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getUsers();
                        }
                    }, 5000);
                } else {
                    progress.dismiss();
                    Log.d(Const.ErrorTag, "register:" + "Error in QBSession Creation");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        userListAdapter.setOnItemClickListener(new UserListAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {

            }
        });
    }

    private void retrieveAllUsersFromPage(int page) {
        QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(page);
        pagedRequestBuilder.setPerPage(10);

        QBUsers.getUsersByTags(userTags, pagedRequestBuilder, this);
    }

    @Override
    public void onSuccess(ArrayList<QBUser> users, Bundle params) {
        int currentPage = params.getInt(Consts.CURR_PAGE);
        int totalEntries = params.getInt(Consts.TOTAL_ENTRIES);
        for (QBUser user : users) {
            String email = user.getEmail();
            Log.d(Const.ErrorTag, userNumber + ": " + email + " " + user.getTags());
            if (!QBemail.equalsIgnoreCase(email))
                userListAdapter.addItem(email, userListAdapter.getItemCount());
            userNumber++;
        }

        if (userNumber < totalEntries) {
            retrieveAllUsersFromPage(currentPage + 1);
        } else if (progress.isShowing()) {
            progress.dismiss();
        }
    }

    @Override
    public void onError(QBResponseException errors) {

    }
}
