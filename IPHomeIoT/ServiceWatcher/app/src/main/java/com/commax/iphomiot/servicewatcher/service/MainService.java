package com.commax.iphomiot.servicewatcher.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainService extends Service {
    private class ServiceCheckThread extends Thread {
        private boolean terminated_ = false;
        private Lock signalLock_ = new ReentrantLock();
        private Condition signal_ = signalLock_.newCondition();

        private class WatchPackageList {
            private class PackageState {
                public String packageName_ = "";
                public boolean isCheck_ = false;
            }

            private static final String servicePackageFilePath_ = "/user/app/bin/servicewatcher_list.i";
            private ArrayList<PackageState> watchPackageList_ = new ArrayList<>();

            private void loadWatchServicePackage() throws IOException {
                watchPackageList_.clear();
                FileInputStream inputStream = new FileInputStream(new File(servicePackageFilePath_));
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = bufReader.readLine();
                PackageState packageState;
                while (line != null) {
                    packageState = new PackageState();
                    packageState.packageName_ = line;
                    watchPackageList_.add(packageState);
                    line = bufReader.readLine();
                }
                bufReader.close();
                inputStream.close();
            }

            public WatchPackageList() {
                try {
                    loadWatchServicePackage();
                }
                catch (IOException e) {
                }
            }

            public int getSize() {
                return watchPackageList_.size();
            }

            public void resetCheck() {
                for (int i = 0; i < watchPackageList_.size(); i++)
                    watchPackageList_.get(i).isCheck_ = false;
            }

            public void setCheck(String packageName) {
                for (int i = 0; i < watchPackageList_.size(); i++) {
                    if (watchPackageList_.get(i).packageName_.equals(packageName)) {
                        watchPackageList_.get(i).isCheck_ = true;
                        break;
                    }
                }
            }

            public boolean isAllCheck() {
                for (int i = 0; i < watchPackageList_.size(); i++)
                    if (!watchPackageList_.get(i).isCheck_)
                        return false;

                return true;
            }
        }

        private void sendResultBroadcast(boolean ret) {
            Intent intent = new Intent("com.commax.iphomiot.servicewatcher");
            intent.putExtra("SERVICE_LOAD_RESULT", ret);
            sendBroadcast(intent);
        }

        public void terminate() {
            signalLock_.lock();
            try {
                terminated_ = true;
                signal_.signal();
            }
            finally {
                signalLock_.unlock();
            }
        }

        @Override
        public void run() {
            WatchPackageList watchPackageList = new WatchPackageList();
            if (watchPackageList.getSize() == 0) {
                sendResultBroadcast(true);
                return;
            }
            while (!terminated_) {
                try {
                    signalLock_.lock();
                    try {
                        signal_.await(10000, TimeUnit.MILLISECONDS);
                        if (terminated_)
                            break;
                    }
                    finally {
                        signalLock_.unlock();
                    }
                    ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                    watchPackageList.resetCheck();
                    for (ActivityManager.RunningServiceInfo runServiceInfo:activityManager.getRunningServices(Integer.MAX_VALUE))
                        watchPackageList.setCheck(runServiceInfo.service.getPackageName());
                    if (watchPackageList.isAllCheck()) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sendResultBroadcast(true);
                    }
                }
                catch (InterruptedException e) {
                    sendResultBroadcast(false);
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private ServiceCheckThread serviceCheckThread_ = null;

    private void destroyServiceCheckThread() {
        if (serviceCheckThread_ == null)
            return;

        serviceCheckThread_.terminate();
        try {
            serviceCheckThread_.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        serviceCheckThread_ = null;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        destroyServiceCheckThread();
        serviceCheckThread_ = new ServiceCheckThread();
        serviceCheckThread_.start();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        destroyServiceCheckThread();
    }
}
