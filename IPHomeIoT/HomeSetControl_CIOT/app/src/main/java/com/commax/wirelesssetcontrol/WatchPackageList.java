package com.commax.wirelesssetcontrol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WatchPackageList {
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
        try {
            for (int i = 0; i < watchPackageList_.size(); i++)
                watchPackageList_.get(i).isCheck_ = false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setCheck(String packageName) {
        try {
            for (int i = 0; i < watchPackageList_.size(); i++) {
                if (watchPackageList_.get(i).packageName_.equals(packageName)) {
                    watchPackageList_.get(i).isCheck_ = true;
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isAllCheck() {
        try {
            for (int i = 0; i < watchPackageList_.size(); i++)
                if (!watchPackageList_.get(i).isCheck_)
                    return false;
        }catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }
}
