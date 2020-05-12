package com.android.womensprotection.app.MessageService;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.womensprotection.app.Home;
import com.android.womensprotection.app.R;
import com.android.womensprotection.app.Tools.tool_add_contacts;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumanth on 12-02-2018.
 */

public class ImportContacts extends AppCompatActivity {

    private static final String TAG = ImportContacts.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;     // contacts unique ID
    ListView listView;
    int j = 0;
    ArrayList<String> NameHolder = new ArrayList<>();
    ArrayList<String> NumHolder = new ArrayList<>();
    String contactNumber;
    String contactName;
    // private static final String TAG = "AddContacts";
    boolean invalid = false;
    public List<String> ListElementsArrayList;
    public ArrayAdapter<String> adapter;
    int i = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contacts);
        listView = (ListView) findViewById(R.id.LV);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ListElementsArrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>
                (ImportContacts.this, android.R.layout.simple_list_item_1, ListElementsArrayList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deleteDialog((int) id);
            }
        });
        registerReceiver(broadcast_reciever, new IntentFilter("finish_ImportContacts"));
    }

    public void onClickSelectContact(View btnSelectContact) {
        // using native contacts selection
        // Intent.ACTION_PICK = Pick an item from the data, returning what was selected.
        if (i <= 6) {
            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
            j++;
        } else {
            Toast.makeText(ImportContacts.this, "Limit exceed", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();
            retrieveContact();
        }
    }
    private void retrieveContact() {
        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        Log.d(TAG, "Contact ID: " + contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();

        Log.d(TAG, "Contact Phone Number: " + contactNumber);
        // NumHolder[j] = contactNumber;
        if(contactNumber.length()<10) {
            Toast.makeText(ImportContacts.this,"Invalid contact number!",Toast.LENGTH_SHORT).show();
        }
        else {
            NumHolder.add(contactNumber);
        }
        // mDatabaseHelper = new DatabaseHelper(this);


        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();
        Log.d(TAG, "Contact Name: " + contactName);
        if(contactNumber.length()<10){
            Toast.makeText(ImportContacts.this,"Invalid contact number!",Toast.LENGTH_SHORT).show();
        }
        else {
            if(contactName.equals("")) {
                contactName="No-name";
            }else {
                NameHolder.add(contactName);
                listView.setAdapter(adapter);
                ListElementsArrayList.add(contactName);
                adapter.notifyDataSetChanged();
                contactNumber="";
                i++;
            }
        }
    }
    public void addTodb(View v) {
        //int a, b;
        /*boolean isDuplicate=false;
        if (NumHolder[j] != null && j>=2) {
            for (a = 0; a <= 5; a++) {
                for (b = a + 1; b <= 5; b++) {
                    if (NumHolder[a] == NumHolder[b]) {
                        isDuplicate=true;
                    }
                }
            }
            if(isDuplicate) {
                Toast.makeText(AddContacts.this, NameHolder[a] + " already exist", Toast.LENGTH_LONG).show();
            }
        }*/

        // for (a=0; a<2; a++) {
        //sendSMS(NumHolder[0]);
        //}
        SharedPreferences spp = getSharedPreferences("login", MODE_PRIVATE);
        String uname, unum;
        for (int a = 0; a <= NameHolder.size() - 1; a++) {
            if (adapter.isEmpty() || NameHolder == null) {
                Toast.makeText(ImportContacts.this, "Please select at least 5 contacts ", Toast.LENGTH_SHORT).show();
                break;
            } else {

                uname = NameHolder.get(a);
                unum = NumHolder.get(a);
                String user = spp.getString("username", "   ");
                ExportContacts EC = new ExportContacts(this);
                String method = "register";
                EC.execute(method, user, uname, unum);
            }
            if (a == NameHolder.size() - 1) {
                startActivity(new Intent(ImportContacts.this, Display_Contacts.class));
                break;
            }
        }
    }

    public void deleteDialog(final int id) {
        AlertDialog.Builder delete_dialog = new AlertDialog.Builder(this);
        final AlertDialog.Builder det = new AlertDialog.Builder(this);
        delete_dialog.setTitle(NameHolder.get(id));
        delete_dialog.setIcon(R.drawable.ic_contact);
        delete_dialog.setItems(R.array.cont_det, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    NameHolder.remove(id);
                    NumHolder.remove(id);
                    adapter.remove(adapter.getItem(id));
                    adapter.notifyDataSetChanged();
                    i--;
                    contactNumber="";
                    //Toast.makeText(newContact.this,NameHolder.get(id) + " deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    det.setTitle(NameHolder.get(id));
                    det.setMessage(NumHolder.get(id));
                    det.create();
                    det.show();
                }
            }
        });
        delete_dialog.create().show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    /**Receives the message finish from another activity and finishes the current activity*/
    BroadcastReceiver broadcast_reciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            if (action.equals("finish_ImportContacts")) {
                //finishing the activity
                finish();
            }
        }
    };
}

