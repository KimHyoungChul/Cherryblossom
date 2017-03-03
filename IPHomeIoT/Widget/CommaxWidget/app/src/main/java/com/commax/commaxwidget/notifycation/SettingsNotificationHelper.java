package com.commax.commaxwidget.notifycation;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.RemoteViews;

import com.commax.commaxwidget.MainService;
import com.commax.commaxwidget.R;
import com.commax.commaxwidget.util.Constants;


/**
 * Created by OWNER on 2017-02-16.
 */
public class SettingsNotificationHelper {
    private static final int SETTINGS_NOTIFICATION_ID = 0x7f050142;

    private static final String PACKAGE_LOCAL_IP_SETTINGS = "com.softwinner.tvdsetting";
    private static final String PACKAGE_SIGN_UP = "com.commax.login";
    private static final String PACKAGE_WALLPAD_SETTINGS = "com.commax.settings";

    private Context mContext;

    private RemoteViews mSmallView;
    private Notification mNotification;
    private NotificationManager mNotificationManager;

    private PendingIntent mPendingIntentWifi;
    private PendingIntent mPendingLocalNetwork;
    private PendingIntent mPendingExternalNetwork;
    private PendingIntent mPendingDoorCameraSettings;
    private PendingIntent mPendingSignUp;
    private PendingIntent mPendingWallPadSettings;
    private PendingIntent mPendingIntentOs;

    public SettingsNotificationHelper(Context context) {
        mContext = context;

        initIntent();
        initView();
        initNotification();
    }

    private void initIntent() {
        Intent wifiIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);//wifi settings
        mPendingIntentWifi = PendingIntent.getActivity(mContext, 1, wifiIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent LocalIntent = mContext.getPackageManager().getLaunchIntentForPackage(PACKAGE_LOCAL_IP_SETTINGS);
        mPendingLocalNetwork = PendingIntent.getActivities(mContext, 1, new Intent[]{LocalIntent}, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent externalIntent =   new Intent(mContext, MainService.class);
        externalIntent.setAction(Constants.ACTION_CLICK_EXTERNAL_NETWORK);
        mPendingExternalNetwork = PendingIntent.getService(mContext, 0, externalIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent doorCameraInent = mContext.getPackageManager().getLaunchIntentForPackage(PACKAGE_WALLPAD_SETTINGS);
        doorCameraInent.putExtra(Constants.EXTRA_KEY_FROM, Constants.FROM_DOORPHONE_SETTING);
        mPendingDoorCameraSettings = PendingIntent.getActivities(mContext, 1, new Intent[]{doorCameraInent}, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent signIntent = mContext.getPackageManager().getLaunchIntentForPackage(PACKAGE_SIGN_UP);
        mPendingSignUp = PendingIntent.getActivities(mContext, 1, new Intent[]{signIntent}, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent settingInent = mContext.getPackageManager().getLaunchIntentForPackage(PACKAGE_WALLPAD_SETTINGS);
        mPendingWallPadSettings = PendingIntent.getActivities(mContext, 1, new Intent[]{settingInent}, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent osIntent = new Intent(Settings.ACTION_SETTINGS);
        mPendingIntentOs = PendingIntent.getActivity(mContext, 1, osIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void initView() {
        mSmallView = new RemoteViews(mContext.getPackageName(), R.layout.notification_view_settings);
        mSmallView.setOnClickPendingIntent(R.id.settings_notification_wifi, mPendingIntentWifi);
        mSmallView.setOnClickPendingIntent(R.id.settings_notification_local_network, mPendingLocalNetwork);
        mSmallView.setOnClickPendingIntent(R.id.settings_notification_external_network, mPendingExternalNetwork);
        mSmallView.setOnClickPendingIntent(R.id.settings_notification_door_camera, mPendingDoorCameraSettings);
        mSmallView.setOnClickPendingIntent(R.id.settings_notification_sing_up, mPendingSignUp);
        mSmallView.setOnClickPendingIntent(R.id.settings_notification_wallpad_setting, mPendingWallPadSettings);
        mSmallView.setOnClickPendingIntent(R.id.settings_notification_os_settings, mPendingIntentOs);
    }

    private void initNotification() {
        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotification = new Notification.Builder(mContext)
                .setSmallIcon(R.drawable.ic_iot_pairing)
                .setPriority(Notification.PRIORITY_MAX)
                .build();
        mNotification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        mNotification.bigContentView = mSmallView;
        mNotification.contentView = mSmallView;
    }

    public void setWifieStateNoty(int state) {
        if (state == Constants.TYPE_WIFI) {
            mSmallView.setImageViewResource(R.id.settings_notification_wifi_icon, R.drawable.ic_network_wifi_white_36dp);
        } else {
            mSmallView.setImageViewResource(R.id.settings_notification_wifi_icon, R.drawable.ic_signal_wifi_off_white_36dp);
        }
    }

    public void setCloudStateNoty(String state) {
        if (state.equals(Constants.TRUE_STATE)) {
            mSmallView.setImageViewResource(R.id.settings_notification_wifi_icon, R.drawable.ic_external_network_good);
        } else {
            mSmallView.setImageViewResource(R.id.settings_notification_wifi_icon, R.drawable.ic_external_network_ng);
        }
    }

    public void setDoorCameraStateNoty(String state) {
        if (state.equals(Constants.ON_STATE)) {
            mSmallView.setImageViewResource(R.id.settings_notification_door_camera_icon, R.drawable.ic_condition_door_camera_good);
        } else {
            mSmallView.setImageViewResource(R.id.settings_notification_door_camera_icon, R.drawable.ic_condition_door_camera_ng);
        }
    }

    public void setAccountStateNoty(String state) {
        if (state.equals(Constants.YES_STATE)) {
            mSmallView.setImageViewResource(R.id.settings_notification_sing_up_icon, R.drawable.ic_condition_sign_up);
        } else {
            mSmallView.setImageViewResource(R.id.settings_notification_sing_up_icon, R.drawable.ic_condition_account);
        }
    }

    public void hide() {
        mNotificationManager.cancel(SETTINGS_NOTIFICATION_ID);
    }

    public void putCanShow() {
        mNotificationManager.notify(SETTINGS_NOTIFICATION_ID, mNotification);
    }
}
