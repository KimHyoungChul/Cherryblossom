package com.commax.iphomeiot.calldial;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.commax.iphomeiot.common.base.DateTimeUtil;
import com.commax.iphomeiot.common.db.CallDBScheme;
import com.commax.iphomeiot.common.provider.CallLogInfo;
import com.commax.settings.content_provider.ContentProviderManager;
import com.commax.settings.doorphone_custom.CustomDevice;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class CallLogFragmentAdapter extends BaseAdapter {
    private ArrayList<CallLogSection> logInfo_ = new ArrayList<>();
    private List<CustomDevice> doorCameraList_;
    private boolean removeMode_ = false;
    private RemovableLog removableLog_ = null;
    private int totalLogCount_ = 0;
    private Context context_;
    private PhoneNumber phoneNumber_;

    private View.OnClickListener removeClickListener_ = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ImageView imgRemove = (ImageView) v.findViewById(R.id.imgRemove);
            CallLogSection logSection = (CallLogSection)imgRemove.getTag();
            logSection.isRemove_ = !logSection.isRemove_;
            if (logSection.isRemove_)
                imgRemove.setImageResource(R.drawable.btn_checkbox_s);
            else
                imgRemove.setImageResource(R.drawable.btn_checkbox_n);
            int selectedCount = getRemoveLogCount();
            if (removableLog_ != null)
                removableLog_.clickRemoveCheckImg(selectedCount);
        }
    };

    private View.OnClickListener recordImgClickListener_ = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CallLogSection log = (CallLogSection)v.getTag();
            if (removableLog_ != null)
                removableLog_.clickRecordImg(log);
        }
    };

    public class CallLogSection extends CallLogInfo {
        public String sectionName_ = "";
        public boolean isRemove_ = false;
        public boolean isSection_ = false;
    }

    public interface RemovableLog {
        void removeLog(int key);
        void clickRecordImg(CallLogSection log);
        void clickRemoveCheckImg(int selectedCount);
    }

    private int getRemoveLogCount() {
        int ret = 0;
        for (int i = 0; i < logInfo_.size(); i++) {
            if (logInfo_.get(i).isRemove_)
                ret++;
        }

        return ret;
    }

    private void setCallStateImg(ImageView imgView, CallLogInfo callLog) {
        switch (callLog.callType_) {
            case CallDBScheme.CallType.INCOMING_CALL:
                imgView.setImageResource(R.drawable.ic_list_call_receive);
                break;
            case CallDBScheme.CallType.OUTGOING_CALL:
                imgView.setImageResource(R.drawable.ic_list_call_transmit);
                break;
            case CallDBScheme.CallType.MISEED_CALL:
                imgView.setImageResource(R.drawable.ic_list_call_absence);
                break;
        }
    }

    private String convertDateToString(String storedDate) {
        if (storedDate.length() == 0)
            return "";

        Date date = new Date(DateTimeUtil.getLocalUnixtimeToUtcUnixTime(Integer.parseInt(storedDate)) * 1000L);
        Date today = new Date();
        SimpleDateFormat dayDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        if (dayDateFormat.format(date).equals(dayDateFormat.format(today))) {
            SimpleDateFormat timeDateFormat = new SimpleDateFormat("a hh:mm");
            return timeDateFormat.format(date);
        }
        else
            return dayDateFormat.format(date);
    }

    public CallLogFragmentAdapter(RemovableLog removableLog, Context context) {
        super();
        removableLog_ = removableLog;
        context_ = context;
        phoneNumber_ = new PhoneNumber(context_);
        doorCameraList_ = ContentProviderManager.getAllCustomDoorCamera(context_);// .getAllOnvifDoorCamera(this);

    }

    public void updateLogInfo(ArrayList<CallLogInfo> logInfo, Context context) {

        logInfo_.clear();
        removeMode_ = false;
        totalLogCount_ = logInfo.size();

        boolean addTodaySection = false;
        boolean addBefore7DaySection = false;
        boolean addAfter7DaySection = false;

        long denomiter = 86400000;
        long today = (DateTimeUtil.getCurrentUnixtimeWithLocal() * 1000) / denomiter;
        long curDate;
        long dayDiff;

        CallLogSection callLogSection;
        for (int i = 0; i < logInfo.size(); i++) {
            curDate = new Date(Integer.parseInt(logInfo.get(i).callDate_) * 1000L).getTime() / denomiter;
            dayDiff = today - curDate;
            if (dayDiff == 0) {
                if (!addTodaySection) {
                    callLogSection = new CallLogSection();
                    callLogSection.sectionName_ = context.getString(R.string.logsection_name_today);
                    callLogSection.isSection_ = true;
                    logInfo_.add(callLogSection);
                    addTodaySection = true;
                }
            }
            else if (dayDiff > 0 && dayDiff <= 7) {
                if (!addBefore7DaySection) {
                    callLogSection = new CallLogSection();
                    callLogSection.sectionName_ = context.getString(R.string.logsection_name_last7days);
                    callLogSection.isSection_ = true;
                    logInfo_.add(callLogSection);
                    addBefore7DaySection = true;
                }
            }
            else if (dayDiff > 7) {
                if (!addAfter7DaySection) {
                    callLogSection = new CallLogSection();
                    callLogSection.sectionName_ = context.getString(R.string.logsection_name_oldDays);
                    callLogSection.isSection_ = true;
                    logInfo_.add(callLogSection);
                    addAfter7DaySection = true;
                }
            }

            callLogSection = new CallLogSection();
            callLogSection.copyFrom(logInfo.get(i));
            logInfo_.add(callLogSection);
        }

        notifyDataSetInvalidated();
    }

    public void setRemoveMode(boolean isRemoveMode) {
        if (removeMode_ != isRemoveMode) {
            removeMode_ = isRemoveMode;
            if (!removeMode_) {
                for (int i = 0; i < logInfo_.size(); i++)
                    logInfo_.get(i).isRemove_ = false;
            }
            notifyDataSetChanged();
        }
    }

    public void removeSelectedIgtem() {
        if (removableLog_ == null)
            return;

        for (int i = 0; i < logInfo_.size(); i++) {
            if (!logInfo_.get(i).isRemove_)
                continue;

            totalLogCount_--;
            removableLog_.removeLog(logInfo_.get(i).key_);
        }
    }

    public void removeSelectAll() {
        for (int i = 0; i < logInfo_.size(); i++) {
            if (!logInfo_.get(i).isSection_)
                logInfo_.get(i).isRemove_ = true;
        }
        notifyDataSetChanged();
        if (removableLog_ != null)
            removableLog_.clickRemoveCheckImg(totalLogCount_);
    }

    public void removeUnselectAll() {
        for (int i = 0; i < logInfo_.size(); i++) {
            if (!logInfo_.get(i).isSection_)
                logInfo_.get(i).isRemove_ = false;

        }

        notifyDataSetChanged();
        if (removableLog_ != null)
            removableLog_.clickRemoveCheckImg(0);
    }

    public int logSize() {
        return totalLogCount_;
    }

    @Override
    public int getCount() {
        if (logInfo_ == null)
            return 0;
        else
            return logInfo_.size();
    }

    @Override
    public Object getItem(int position) {
        return logInfo_.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (logInfo_ == null)
            return null;

        CallLogSection callLog = logInfo_.get(position);
        if (convertView == null) {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_calllog, parent, false);
        }

        LinearLayout itemCalllogLayout = (LinearLayout) convertView.findViewById(R.id.itemCalllogLayout);
        ImageView imgRemove = (ImageView)convertView.findViewById(R.id.imgRemove);
        if (!itemCalllogLayout.hasOnClickListeners())
            itemCalllogLayout.setOnClickListener(removeClickListener_);
        imgRemove.setTag(callLog);
        TextView tvSection = (TextView)convertView.findViewById(R.id.tvSection);
        TextView tvOtherParty = (TextView)convertView.findViewById(R.id.tvOtherParty);
        ImageView imgCallState = (ImageView)convertView.findViewById(R.id.imgCallState);
        TextView tvDateTime = (TextView)convertView.findViewById(R.id.tvDatetime);
        ImageView imgRecord = (ImageView)convertView.findViewById(R.id.imgRecord);
        if (!imgRecord.hasOnClickListeners())
            imgRecord.setOnClickListener(recordImgClickListener_);
        imgRecord.setTag(callLog);

        if (callLog.sectionName_.length() == 0) {
            if (!removeMode_) {
                itemCalllogLayout.setEnabled(false);
                imgRemove.setVisibility(View.INVISIBLE);
            }
            else {
                itemCalllogLayout.setEnabled(true);
                imgRemove.setVisibility(View.VISIBLE);
                if (callLog.isRemove_)
                    imgRemove.setImageResource(R.drawable.btn_checkbox_s);
                else
                    imgRemove.setImageResource(R.drawable.btn_checkbox_n);
            }
            imgCallState.setVisibility(View.VISIBLE);
            tvOtherParty.setVisibility(View.VISIBLE);
            tvDateTime.setVisibility(View.VISIBLE);
            tvSection.setVisibility(View.GONE);
            if (callLog.recordFilePath_.length() == 0)
                imgRecord.setVisibility(View.INVISIBLE);
            else {
                File recordFile = new File(callLog.recordFilePath_);
                if (recordFile.exists())
                    imgRecord.setVisibility(View.VISIBLE);
                else
                    imgRecord.setVisibility(View.INVISIBLE);
            }
            tvOtherParty.setPadding(30, 0, 0, 0);
            tvOtherParty.setText(callLog.displayName_);
            setCallStateImg(imgCallState, callLog);
            tvDateTime.setText(convertDateToString(callLog.callDate_));
        }
        else {
            imgRemove.setVisibility(View.GONE);
            imgCallState.setVisibility(View.GONE);
            tvOtherParty.setVisibility(View.GONE);
            tvDateTime.setVisibility(View.INVISIBLE);
            imgRecord.setVisibility(View.INVISIBLE);
            tvSection.setVisibility(View.VISIBLE);

            tvSection.setPadding(30, 15, 0, 15);
            String callName = getContactName(callLog.sectionName_);
            tvSection.setText(callName);
        }

        return convertView;
    }

    public String getContactName(String strPhoneNumber) {
        String strContactName = strPhoneNumber;

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(strPhoneNumber));
        Cursor cursor = context_.getContentResolver().query(uri, new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

        if(cursor == null) {
            return strContactName;
        }

        if(cursor.moveToFirst()) {
            strContactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null) cursor.close();
        cursor = null;

        return strContactName;
    }

    private String setCallName(String callNumber) {
        String displayCallName = null;

        if (callNumber.equals(phoneNumber_.getGuard()) && callNumber.equals(phoneNumber_.getOffice())) {
            return context_.getString(R.string.STR_GUARD).toString()+"/"+context_.getString(R.string.STR_OFFICE).toString();
        }
        else if (callNumber.equals(phoneNumber_.getGuard())) {
            return context_.getString(R.string.STR_GUARD).toString();
        }
        else if (callNumber.equals(phoneNumber_.getOffice())) {
            return context_.getString(R.string.STR_OFFICE).toString();
        }
        else {
            if (doorCameraList_ != null && doorCameraList_.size() != 0) {
                for (int i = 0; i < doorCameraList_.size(); i++) {
                    if (doorCameraList_.get(i).getSipPhoneNo() != null) {
                        if (doorCameraList_.get(i).getSipPhoneNo().equals(callNumber)) {
                            displayCallName = doorCameraList_.get(i).getModelName();
                            return displayCallName;
                        }
                    }
                }
                if (displayCallName == null && callNumber != null) {
                    String dong = callNumber.substring(2, 6);
                    String ho = callNumber.substring(6, callNumber.length());
                    if (dong.substring(0, 1).equals("0"))
                        dong = dong.substring(1, dong.length());
                    if (ho.substring(0,1).equals("0"))
                        ho = ho.substring(1, ho.length());
                    displayCallName = dong + context_.getString(R.string.STR_DONG).toString() + ho + context_.getString(R.string.STR_HO).toString();
                    return displayCallName;
                }
            }
            else {
                if (displayCallName == null && callNumber != null) {
                    String dong = callNumber.substring(2, 6);
                    String ho = callNumber.substring(6, callNumber.length());
                    if (dong.substring(0, 1).equals("0"))
                        dong = dong.substring(1, dong.length());
                    if (ho.substring(0,1).equals("0"))
                        ho = ho.substring(1, ho.length());
                    displayCallName = dong + context_.getString(R.string.STR_DONG).toString() + ho + context_.getString(R.string.STR_HO).toString();
                    return displayCallName;
                }
            }
        }

        return displayCallName;
    }
}
