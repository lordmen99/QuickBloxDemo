package com.raghav.quickbloxdemo.support.listener;

import com.quickblox.auth.model.QBSession;

/**
 * Created by raghav.satyadev on 19/7/16.
 */
public interface QBUserListener {
    void onTaskCompleted(int sessionStatus, QBSession qbSession);
}
