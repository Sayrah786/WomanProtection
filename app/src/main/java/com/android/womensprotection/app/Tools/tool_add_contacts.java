package com.android.womensprotection.app.Tools;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.womensprotection.app.MessageService.Display_Contacts;
import com.android.womensprotection.app.MessageService.ExportContacts;
import com.android.womensprotection.app.MessageService.ImportContacts;
import com.android.womensprotection.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumanth on 24-02-2018.
 */

public class tool_add_contacts extends AppCompatActivity {

    private static final String TAG = tool_add_contacts.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;     // contacts unique ID
    ListView listView;
    int j = 0;
    ArrayList<String> NameHolder = new ArrayList<>();
    ArrayList<String> NumHolder = new ArrayList<>();
    String contactNumber;
    String contactName;
    String hasnum;
    // private static final String TAG = "AddContacts";
    public List<String> ListElementsArrayList;
    public ArrayAdapter<String> adapter;
    int i = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tool_add_cont);
        setTitle("Add contacts");
        listView = (ListView) findViewById(R.id.LV);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ListElementsArrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>
                (tool_add_contacts.this, android.R.layout.simple_list_item_1, ListElementsArrayList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deleteDialog((int) id);
            }
        });
    }

    public void onClickSelectContact(View btnSelectContact) {
        // using native contacts selection
        // Intent.ACTION_PICK = Pick an item from the data, returning what was selected.
        if (i <= 6) {
            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
            j++;
        } else {
            Toast.makeText(tool_add_contacts.this, "Limit exceed", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            //Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();
                //retrieveContactName();
                retrieveContact();
        }
    }
    private void retrieveContact() {
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
               // hasnum= cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER));
                //contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
       // Log.d(TAG, "Contact Phone Number: " + contactNumber);
        cursorPhone.close();
        if(contactNumber.length()<10) {
            //Toast.makeText(tool_add_contacts.this,"Invalid contact number!",Toast.LENGTH_SHORT).show();
        }
        else {
            NumHolder.add(contactNumber);
           // contactNumber="";
       }
        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);
        if (cursor.moveToFirst()) {
            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.
            //hasnum= cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            //Toast.makeText(tool_add_contacts.this,hasnum,Toast.LENGTH_SHORT).show();
            //contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        //Log.d(TAG, "Contact Name: " + contactName);
       cursor.close();
       if(contactNumber.length()<10){
            Toast.makeText(tool_add_contacts.this,"Invalid contact number!",Toast.LENGTH_SHORT).show();
        }
        else {
           ExportContacts EC = new ExportContacts(this);
           String method = "register";
           Stri
           EC.execute(method, user, contactName, contactName);
            NameHolder.add(contactName);
            ListElementsArrayList.add(contactName);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
           contactNumber="";
        }
    }
    public void addTodb(View v) {
       /*int c, b;

        if (NumHolder.get(j) != null && j>=2) {
            for (c = 0; c <= 5; c++) {
                for (b = c + 1; b <= 5; b++) {
                    if (NumHolder.get(c) == NumHolder.get(c)) {
                        Toast.makeText(tool_add_contacts.this, NameHolder.get(c) + " already exist", Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }
        }*/
        SharedPreferences spp=getSharedPreferences("login",MODE_PRIVATE);
        String uname, unum;
        if(adapter.isEmpty()||NameHolder==null) {
            Toast.makeText(tool_add_contacts.this, "Please select at least 5 contacts ", Toast.LENGTH_SHORT).show();
        }else {
            for (int a = 0; a <= NameHolder.size() - 1; a++) {
                    uname = NameHolder.get(a);
                    unum = NumHolder.get(a);
                    String user = spp.getString("username", "   ");
                    ExportContacts EC = new ExportContacts(this);
                    String method = "register";
                    EC.execute(method, user, uname, unum);
                if (a == NameHolder.size() - 1) {
                    startActivity(new Intent(tool_add_contacts.this, show_contacts.class));
                    finish();
                    break;
                }
            }
        }
    }
    public void deleteDialog(final int id) {
        AlertDialog.Builder delete_dialog=new AlertDialog.Builder(this);
        final AlertDialog.Builder det=new AlertDialog.Builder(this);
        delete_dialog.setTitle(NameHolder.get(id));
        delete_dialog.setIcon(R.drawable.ic_contact);
        delete_dialog.setItems(R.array.cont_det, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0) {
                   // Toast.makeText(tool_add_contacts.this,NameHolder.get(id) + " deleted successfully", Toast.LENGTH_SHORT).show();
                    NameHolder.remove(id);
                    NumHolder.remove(id);
                    contactNumber="";
                    adapter.remove(adapter.getItem(id));
                    adapter.notifyDataSetChanged();
                    i--;

                }
                else {
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
        finish();
        return true;
    }
}