package com.commax.iphomeiot.calldial;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.commax.settings.content_provider.ContentProviderManager;
import com.commax.settings.doorphone_custom.CustomDevice;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

public class BaseFragment extends Fragment {
	Context m_Context = null;;
	private List<CustomDevice> doorCameraList_;
	private String callNumber_;
	private String callName_;

	interface IFragmentListener{
		void confirmDeleteItem(int fragmentId);
	}

	/* Add Contact */
	public boolean addContact(String DisplayName, String MobileNumber) {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
				.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
				.build());

		/* Names */
		if(DisplayName != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
							DisplayName).build());
		}

		/* Mobile Number */
		if(MobileNumber != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
							MobileNumber)
					.build());
		}

		/* Asking the Contact provider to create a new contact */
		try {
			m_Context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/* Delete Contact */
	public void deleteContact(String strKey) {
		if(strKey == null) {
			return;
		}

		ContentResolver contentResolver = m_Context.getContentResolver();
		Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		while(cursor.moveToNext()) {
			String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
			if(lookupKey != null) {
				if(strKey.equals(lookupKey)) {
					Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
					contentResolver.delete(uri, null, null);
					break;
				}
			}
		}

		if(cursor != null) cursor.close();
		cursor = null;
	}

	/* Delete Contacts All */
	public void deleteContactAll() {
		ContentResolver contentResolver = m_Context.getContentResolver();
		Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		
		while(cursor.moveToNext()) {
			String lookupKey = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
			Uri uri = Uri.withAppendedPath(
					ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
			contentResolver.delete(uri, null, null);
		}

		if(cursor != null) cursor.close();
		cursor = null;
	}

	protected void makeOutgoingCall(Context context, String strNumber) {
		String strName = "";

		Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(strNumber));
		Cursor cursor = m_Context.getContentResolver().query(uri, new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

		if (cursor == null)
			strName = strNumber;

		if(cursor.moveToFirst()) {
			strName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
		}

		if(cursor != null) cursor.close();
		cursor = null;

		if (strName.equals("")) {
			strName = strNumber;
		}

		Intent intent = new Intent("com.commax.iphomeiot.calldial.outgoingcall");
		intent.putExtra("android.intent.extra.PHONE_NUMBER", strNumber);
		intent.putExtra("android.intent.extra.DISPLAY_NAME", strName);
		context.sendBroadcast(intent);

		Log.e("tag", "Number " + strNumber + " Name " + strName);
		intent = null;
	}

	/* Makge Outgoing Call */
	protected void makeOutgoingCall(Context context, String strNumber, int outgoing) {
		callName_ = null;
		callNumber_ = null;
		doorCameraList_ = ContentProviderManager.getAllCustomDoorCamera(getActivity());// .getAllOnvifDoorCamera(this);
		if((strNumber == null) || (strNumber == "")) {
			return;
		}

		String strName = "";

		switch (outgoing) {
			case NameSpace.OUTGOING_NEIGHBORHOOD :
				Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(strNumber));
				Cursor cursor = m_Context.getContentResolver().query(uri, new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

				if (cursor != null) {
					if (cursor.moveToFirst()) {
						callName_ = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
					}
					if (cursor != null)
						cursor.close();
						cursor = null;

					if (callName_ == null) {
						if (strName.equals("")) {
							if (strNumber.contains("*")) {
								StringTokenizer tokenizer = new StringTokenizer(strNumber, "*");
								String dong = tokenizer.nextToken();
								String ho = tokenizer.nextToken();
								callNumber_ = "71" + dong + ho;
								if (dong.substring(0, 1).equals("0"))
									dong = dong.substring(1, dong.length());
								if (ho.substring(0, 1).equals("0"))
									ho = ho.substring(1, ho.length());
								callName_ = dong + "-" + ho;
							}
						}
					}
				}
				else {
					if (strName.equals("")) {
						if (strNumber.contains("*")) {
							StringTokenizer tokenizer = new StringTokenizer(strNumber, "*");
							String dong = tokenizer.nextToken();
							String ho = tokenizer.nextToken();
							callNumber_ = "71" + dong + ho;
							if (dong.substring(0, 1).equals("0"))
								dong = dong.substring(1, dong.length());
							if (ho.substring(0, 1).equals("0"))
								ho = ho.substring(1, ho.length());
							callName_ = dong + "-" + ho;
						}
					}
				}
				break;
			case NameSpace.OUTGOING_DOOR :
				if (strNumber.contains("#2")) {
					StringTokenizer tokenizer = new StringTokenizer(strNumber, "#");
					String number = tokenizer.nextToken();
					if (doorCameraList_ != null) {
						if (doorCameraList_.size() != 0) {
							for (int i = 0; i < doorCameraList_.size(); i++) {
								Log.e("tag", "Door Camera List Name : " + doorCameraList_.get(i).getSipPhoneNo() + " id : " + doorCameraList_.get(i).getNickName());
								if (doorCameraList_.get(i).getSipPhoneNo() != null) {
									if (doorCameraList_.get(i).getSipPhoneNo().equals(number)) {
										callName_ = doorCameraList_.get(i).getModelName();
										callNumber_ = doorCameraList_.get(i).getSipPhoneNo();
									}
								}
							}
						}
						else {
							callName_ = number;
						}
					}
				}
				break;
			case NameSpace.OUTGOING_GUARD:
				StringTokenizer guardTokenizer = new StringTokenizer(strNumber, "#");
				String guardNumber = guardTokenizer.nextToken();
				callNumber_ = guardNumber;
				callName_ = "Guard";
				break;
			case NameSpace.OUTGOING_OFFICE:
				StringTokenizer officeTokenizer = new StringTokenizer(strNumber, "#");
				String officeNumber = officeTokenizer.nextToken();
				callNumber_ = officeNumber;
				callName_ = "Office";
				break;
		}


		Intent intent = new Intent("com.commax.iphomeiot.calldial.outgoingcall");
		intent.putExtra("android.intent.extra.PHONE_NUMBER", callNumber_);
		intent.putExtra("android.intent.extra.DISPLAY_NAME", callName_);
		context.sendBroadcast(intent);

		intent = null;
	}
	/* Send Custom Broadcast Message */
	public void sendCustomBroadcastMessage(String strBroadcastAction) {
		Intent intent = new Intent(strBroadcastAction);

		m_Context.sendBroadcast(intent);

		intent = null;
	}


}
