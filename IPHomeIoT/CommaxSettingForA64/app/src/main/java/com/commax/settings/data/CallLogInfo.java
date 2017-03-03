package com.commax.settings.data;

public class CallLogInfo {
    public int key_;
    public int callType_;
    public String otherParty_;
    public String callDate_;
    public String recordFilePath_;

    public CallLogInfo() {
        callType_ = -1;
        otherParty_ = "Door";
        callDate_ = "0";
        recordFilePath_ = "";
        key_ = -1;
    }

    public void copyFrom(CallLogInfo logInfo) {
        key_ = logInfo.key_;
        callType_ = logInfo.callType_;
        otherParty_ = logInfo.otherParty_;
        callDate_ = logInfo.callDate_;
        recordFilePath_ = logInfo.recordFilePath_;
    }
}