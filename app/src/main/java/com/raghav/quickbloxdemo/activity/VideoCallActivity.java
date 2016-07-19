package com.raghav.quickbloxdemo.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientVideoTracksCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCSessionConnectionCallbacks;
import com.quickblox.videochat.webrtc.exception.QBRTCException;
import com.quickblox.videochat.webrtc.view.QBRTCVideoTrack;
import com.quickblox.videochat.webrtc.view.RTCGLVideoView;
import com.raghav.quickbloxdemo.R;
import com.raghav.quickbloxdemo.support.Const;
import com.raghav.quickbloxdemo.support.PrefsHelper;
import com.raghav.quickbloxdemo.support.SupportMethods;
import com.raghav.quickbloxdemo.support.listener.QBAppListener;
import com.raghav.quickbloxdemo.support.listener.QBUserListener;

import org.webrtc.VideoRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VideoCallActivity extends AppCompatActivity implements QBRTCClientSessionCallbacks, QBRTCSessionConnectionCallbacks, QBRTCClientVideoTracksCallbacks {


    private ProgressDialog progress;
    private VideoCallActivity context;
    private PrefsHelper prefsHelper;
    private QBUser user;
    private QBChatService chatService;
    private QBRTCClient callback;
    private RTCGLVideoView localView, opponentView;
    private QBRTCSession rtcSession;
    private int opponentID;
    private QBRTCSession callerSession, recieverSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        context = this;

        opponentID = getIntent().getIntExtra(Const.UserID, 0);

        localView = (RTCGLVideoView) findViewById(R.id.localView);
        opponentView = (RTCGLVideoView) findViewById(R.id.opponentView);

        prefsHelper = new PrefsHelper(context);
        chatService = QBChatService.getInstance();
        chatService.getVideoChatWebRTCSignalingManager()
                .addSignalingManagerListener(new QBVideoChatSignalingManagerListener() {
                    @Override
                    public void signalingCreated(QBSignaling qbSignaling, boolean createdLocally) {
                        if (!createdLocally) {
                            QBRTCClient.getInstance(context).addSignaling((QBWebRTCSignaling) qbSignaling);
                        }
                    }
                });
        sessionAppInitialize();
    }

    public void sessionAppInitialize() {
        progress = SupportMethods.showProgressDialog(progress, context, "app session initialize");

        SupportMethods.createAppSession(new QBAppListener() {
            @Override
            public void onTaskCompleted(int sessionStatus) {
                if (sessionStatus == 1) {
                    user = new QBUser();
                    user.setEmail(prefsHelper.getEmail());
                    user.setPassword(prefsHelper.getPassword());

                    sessionUserInitialize(user);
                } else if (sessionStatus == 3) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sessionAppInitialize();
                        }
                    }, 5000);
                } else {
                    SupportMethods.hideProgressDialog(progress, "video error app session");
                    Log.d(Const.ErrorTag, "register:" + "Error in QBAppSession Creation");
                }
            }
        });
    }

    private void sessionUserInitialize(final QBUser user) {
        SupportMethods.createUserSession(user, new QBUserListener() {
            @Override
            public void onTaskCompleted(int sessionStatus, QBSession qbSession) {
                if (sessionStatus == 1) {
                    SupportMethods.hideProgressDialog(progress, "sucess user session video");

                    user.setId(qbSession.getUserId());

                    chatService.login(user, new QBEntityCallback<QBUser>() {
                        @Override
                        public void onSuccess(QBUser qbUser, Bundle bundle) {
                            callback = QBRTCClient.getInstance(context);
                            callback.addSessionCallbacksListener(context);
                            callback.prepareToProcessCalls();

                            setUpCalls();
                        }

                        @Override
                        public void onError(QBResponseException errors) {
                            //error
                        }
                    });
                } else if (sessionStatus == 3) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sessionUserInitialize(user);
                        }
                    }, 5000);
                } else {
                    SupportMethods.hideProgressDialog(progress, "error user session video");
                    Log.d(Const.ErrorTag, "register:" + "Error in QBUserSession Creation");
                }
            }
        });
    }

    private void setUpCalls() {
        QBRTCTypes.QBConferenceType qbConferenceType = QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;

        List<Integer> opponents = new ArrayList<>();
        opponents.add(opponentID);

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("key", "value");

        callerSession = callback.createNewSessionWithOpponents(opponents, qbConferenceType);

        callerSession.startCall(userInfo);
    }

    private void fillVideoView(RTCGLVideoView videoView, QBRTCVideoTrack videoTrack, boolean remoteRenderer) {
        videoTrack.addRenderer(new VideoRenderer(remoteRenderer ?
                videoView.obtainVideoRenderer(RTCGLVideoView.RendererSurface.MAIN) :
                videoView.obtainVideoRenderer(RTCGLVideoView.RendererSurface.SECOND)));
    }

    @Override
    public void onReceiveNewSession(QBRTCSession qbrtcSession) {
        Map<String, String> opponentInfo = qbrtcSession.getUserInfo();

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("Key", "Value");

        qbrtcSession.acceptCall(userInfo);
    }

    @Override
    public void onUserNotAnswer(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onCallRejectByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {

    }

    @Override
    public void onCallAcceptByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
        rtcSession = qbrtcSession;
        rtcSession.addSessionCallbacksListener(context);
        rtcSession.addVideoTrackCallbacksListener(context);
    }

    @Override
    public void onReceiveHangUpFromUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {

    }

    @Override
    public void onUserNoActions(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onSessionClosed(QBRTCSession qbrtcSession) {

    }

    @Override
    public void onSessionStartClose(QBRTCSession qbrtcSession) {

    }

    @Override
    public void onStartConnectToUser(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onConnectedToUser(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onConnectionClosedForUser(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onDisconnectedFromUser(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onDisconnectedTimeoutFromUser(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onConnectionFailedWithUser(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onError(QBRTCSession qbrtcSession, QBRTCException e) {

    }

    @Override
    public void onLocalVideoTrackReceive(QBRTCSession qbrtcSession, QBRTCVideoTrack qbrtcVideoTrack) {
        fillVideoView(localView, qbrtcVideoTrack, true);
    }

    @Override
    public void onRemoteVideoTrackReceive(QBRTCSession qbrtcSession, QBRTCVideoTrack qbrtcVideoTrack, Integer integer) {
        fillVideoView(opponentView, qbrtcVideoTrack, true);
    }
}
