package com.commax.iphomeiot.calldial;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.iphomeiot.common.provider.CallLogInfo;

import java.util.List;

/**
 * Created by Woohj on 2017-02-10.
 */

public class ContactsFragmentAdapter extends BaseAdapter {
    private Context context_;
    private List<ContactsEntry> objects_;

    private View.OnClickListener removeClickListener_ = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ImageView imgRemove = (ImageView)v;
            CallLogFragmentAdapter.CallLogSection logSection = (CallLogFragmentAdapter.CallLogSection)imgRemove.getTag();
            logSection.isRemove_ = !logSection.isRemove_;
            if (logSection.isRemove_)
                imgRemove.setImageResource(R.drawable.btn_checkbox_s);
            else
                imgRemove.setImageResource(R.drawable.btn_checkbox_n);
        }
    };

    public ContactsFragmentAdapter(Context context, List<ContactsEntry> objects) {
        context_ = context;
        objects_ = objects;
    }

    @Override
    public int getCount() {
        return objects_.size();
    }

    @Override
    public Object getItem(int position) {
        return objects_.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View itemContactsView = convertView;
        if(itemContactsView == null) {
            itemContactsView = LayoutInflater.from(context_).inflate(R.layout.item_contacts, null);
        }

        final ContactsEntry itemContacts = objects_.get(position);

        ((TextView)itemContactsView.findViewById(R.id.imgContactsItemName)).setText(itemContacts.m_strName);
        ImageView imgRemove = (ImageView) itemContactsView.findViewById(R.id.imgRemove);

        try {
				/* Contacts Item Call Connect Request */
            ((ImageView)itemContactsView.findViewById(R.id.imgContactsItemCallConnectReq)).setVisibility((itemContacts.m_strNumber.charAt(0) == '@') ? View.INVISIBLE : View.VISIBLE);
        }
        catch(StringIndexOutOfBoundsException e) {
				/* Contacts Item Call Connect Request */
            ((ImageView)itemContactsView.findViewById(R.id.imgContactsItemCallConnectReq)).setVisibility(View.INVISIBLE);
        }
			/* Contacts Item Call Connect Request */
        if(((ImageView)itemContactsView.findViewById(R.id.imgContactsItemCallConnectReq)).getVisibility() == View.VISIBLE) {
            ((ImageView)itemContactsView.findViewById(R.id.imgContactsItemCallConnectReq)).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction()){
                        case MotionEvent.ACTION_DOWN: v.setAlpha(0.6f); break;
                        case MotionEvent.ACTION_MOVE: v.setAlpha(0.6f); break;
                        case MotionEvent.ACTION_UP: v.setAlpha(1.0f); break;
                        default: v.setAlpha(1.0f); break;
                    }

                    return false;
                }
            });

            ((ImageView)itemContactsView.findViewById(R.id.imgContactsItemCallConnectReq)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
						/* Send Place Call */
                    sendPlaceCall((ContactsEntry)objects_.get(position));
                }
            });
        }

        return itemContactsView;
    }

    private void sendPlaceCall(ContactsEntry itemContacts) {
        makeOutgoingCall(context_, itemContacts.m_strNumber);
    }

    protected void makeOutgoingCall(Context context, String strNumber) {
        if((strNumber == null) || (strNumber == "")) {
            return;
        }

        String strContactName = "";

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(strNumber));
        Cursor cursor = context.getContentResolver().query(uri, new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

        if (cursor == null)
            strContactName = strNumber;

        if(cursor.moveToFirst()) {
            strContactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null) cursor.close();
        cursor = null;

        if (strContactName.equals(""))
            strContactName = strNumber;


        Intent intent = new Intent("com.commax.iphomeiot.calldial.outgoingcall");
        intent.putExtra("android.intent.extra.PHONE_NUMBER", strNumber);
        intent.putExtra("android.intent.extra.DISPLAY_NAME", strContactName);
        context.sendBroadcast(intent);

        intent = null;
    }
}
