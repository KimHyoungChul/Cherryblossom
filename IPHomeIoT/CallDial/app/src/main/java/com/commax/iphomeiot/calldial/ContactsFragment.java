package com.commax.iphomeiot.calldial;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends BaseFragment implements OnClickListener, OnTouchListener {
	private ContactsAddUserDialog m_dlgContactsAddUserDialog = null;		/* Contacts Add User Dialog */
	private ListView m_lstContactsListView = null;					/* Contacts List View */
	private ContactsFragmentAdapter m_adtContactsListAdapter = null;	/* Contacts List Adapter */
	private RelativeLayout layoutAllSelect_ = null;
	private ImageView imgAllSelect_;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		m_Context = inflater.getContext();
		View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);

		layoutAllSelect_ = (RelativeLayout) rootView.findViewById(R.id.allSelect_layout);
		m_lstContactsListView = (ListView)rootView.findViewById(R.id.lstContactsListView);

		ArrayList<ContactsEntry> aryContactsList = new ArrayList<ContactsEntry>();

		Cursor pCur = m_Context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		while(pCur.moveToNext()) {
			ContactsEntry entry = createContactsEntry(m_Context.getContentResolver(), pCur);
			aryContactsList.add(entry);
		}
		m_adtContactsListAdapter = new ContactsFragmentAdapter(m_Context, aryContactsList);
		m_lstContactsListView.setTextFilterEnabled(true);
		m_lstContactsListView.setFastScrollEnabled(true);
		m_lstContactsListView.setFastScrollAlwaysVisible(true);
		m_lstContactsListView.setAdapter(m_adtContactsListAdapter);

		m_lstContactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ContactsEntry itemContacts = (ContactsEntry) parent.getAdapter().getItem(position);

				if ((itemContacts != null) && (itemContacts.m_strNumber.charAt(0) != '@')) {
					sendPlaceCall(itemContacts);
				}
			}
		});
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	public void changeUiToDeleteMode() {

	}

	public void changeUiToNormalMode() {

	}

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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.cbAllCheck :

		}
	}

	@Override
	public void onPause() {
		super.onPause();
		/* Contacts Add User Dialog */
		if(m_dlgContactsAddUserDialog != null) m_dlgContactsAddUserDialog.cancel();
		m_dlgContactsAddUserDialog = null;
	}

	/* Create Contacts Entry */
	private ContactsEntry createContactsEntry(ContentResolver cr, Cursor cur) {
		String strNumber = "";
		String strLookupKey = "";
		String _ID = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
		String strName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

		if(Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
			String selection = new StringBuilder().append(ContactsContract.CommonDataKinds.Phone.CONTACT_ID).append(" = ?").toString();
			Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, selection, new String[] { _ID }, null);

			while(pCur.moveToNext()) {
				strNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			}

			if(pCur != null) pCur.close();
			pCur = null;
		}

		strLookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));

		ContactsEntry entry = new ContactsEntry(strLookupKey, strNumber, strName);

		return entry;
	}

	private void sendPlaceCall(ContactsEntry itemContacts) {
/*		LayoutInflater layInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viLayout = layInflater.inflate(R.layout.custom_toast, (ViewGroup)getActivity().findViewById(R.id.frmCustomToastMainFrame));

		TextView txtCustomToastMessage = (TextView)viLayout.findViewById(R.id.txtCustomToastMessage);

		txtCustomToastMessage.setText(R.string.STR_CONNECTIOG);

		Toast toast = new Toast(getActivity());
		toast.setGravity(Gravity.BOTTOM, 0, 46);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(viLayout);
		toast.show();
*/
		/* Make Outgoing Call */
		makeOutgoingCall(m_Context, itemContacts.m_strNumber);
	}


	/* Show Contacts Add User Dialog */
	private void showContactsAddUserDialog(final String strKey, String strName, String strNumber) {
		/* Contacts Add User Dialog */
		if(m_dlgContactsAddUserDialog == null) {
			/* Tab Contacts Fragment */
			m_dlgContactsAddUserDialog = new ContactsAddUserDialog(getContext(), NameSpace.TAB_CONTACTS_FRAGMENT, strKey, strName, strNumber, new ContactsAddUserDialog.OnContactsAddUserEventListener() {
				@Override
				public void onContactsAddUserEvent(boolean bResponse) {

					/* Contacts Add User Dialog */
					if(m_dlgContactsAddUserDialog != null)
						m_dlgContactsAddUserDialog = null;
				}
			});

			m_dlgContactsAddUserDialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					/* Contacts Add User Dialog */
					if(m_dlgContactsAddUserDialog != null)
						m_dlgContactsAddUserDialog = null;

					/* Send Custom Broadcast Message(System Key Hide Action) */
					sendCustomBroadcastMessage(NameSpace.SYSTEM_KEY_HIDE_ACTION);
				}
			});

			/* Contacts Add User Dialog */
			m_dlgContactsAddUserDialog.show();
		}
	}
}
