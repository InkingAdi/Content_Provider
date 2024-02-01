package com.example.content_provider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.os.MessageCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    //List
    ListView listview;

    //Array adapter to getTheString and add it into ListView
    ArrayAdapter<String> adapter;

    //To Store list of Contact
    ArrayList<String> contactList, recent_contacts ;

    String Name, Contact, contact_id;

    public final static int contactPermissionRC = 101;
    public final static int permissionRL = 102, permissionCL = 103;

    //Cursor object to store large amount of data;
    Cursor cursor_contact_list, cursor_recent_contact;

    @SuppressLint(value = "Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList = new ArrayList<String>();
        recent_contacts = new ArrayList<String>();
        listview = findViewById(R.id.listView);

        //Asking Runtime permission
        checkContactPermission(Manifest.permission.READ_CONTACTS,contactPermissionRC);

        checkContactPermission(Manifest.permission.READ_CALL_LOG,permissionRL);

        checkContactPermission(Manifest.permission.ACCESS_COARSE_LOCATION,permissionCL);

        findViewById(R.id.btn_contact_list).setOnClickListener(view -> {

                //Getting All the Data into the cursor variable
                String[] projection = new String[]{CallLog.Calls.BLOCK_REASON};

                String sortOrder = String.format(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

                cursor_contact_list = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null, sortOrder);

                while(cursor_contact_list.moveToNext() && cursor_contact_list != null) {

                    Name = cursor_contact_list.getString(cursor_contact_list.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                    Contact = cursor_contact_list.getString(cursor_contact_list.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    //ID of the Contacts
                    //cursor_contact_list.getString(cursor_contact_list.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

                    //Ringtone of the Contacts
                    //cursor_contact_list.getString(cursor_contact_list.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CUSTOM_RINGTONE));

                    contactList.add(Name + " : " + Contact+" : "+ contact_id);

                }

                Log.d("LOGS",contactList.toString());
                cursor_contact_list.close();

                adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_dropdown_item,contactList);
                listview.setAdapter(adapter);

        });

        findViewById(R.id.btn_show_recent_call).setOnClickListener(view -> {
            String  numb, call_type_name = "",name , date;
            Integer call_type ;
            cursor_recent_contact = getContentResolver().query(CallLog.Calls.CONTENT_URI,null,null,null,CallLog.Calls.CACHED_NAME+" ASC");
            while(cursor_recent_contact.moveToNext() && cursor_recent_contact != null) {

                numb = cursor_recent_contact.getString(cursor_recent_contact.getColumnIndex(CallLog.Calls.NUMBER));

                name = cursor_recent_contact.getString(cursor_recent_contact.getColumnIndex(CallLog.Calls.CACHED_NAME));

                date = cursor_recent_contact.getString(cursor_recent_contact.getColumnIndex(CallLog.Calls.DATE));
                Date dates = new Date(Long.valueOf(date));
                call_type = cursor_recent_contact.getInt(cursor_recent_contact.getColumnIndex(CallLog.Calls.TYPE));
                switch (call_type) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        call_type_name = "OUTGOING";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        call_type_name = "INCOMING";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        call_type_name = "MISSED";
                        break;
                }
                recent_contacts.add(name+" : "+numb +"\n"+call_type_name+" : "+ dates);
            }
            cursor_recent_contact.close();
            adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item,recent_contacts);
            listview.setAdapter(adapter);
        });

    }

    private void checkContactPermission(String readContacts, int contactPermissionRC) {

        //With ContextCompat we are checking selfPermission
        if(ContextCompat.checkSelfPermission(MainActivity.this,readContacts) == PackageManager.PERMISSION_DENIED){

            //With ActivityCompat we are requesting main_activity to ask readContacts permission and passing request_Code for verification whether that permission is agreed or not.
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{readContacts},contactPermissionRC);

        }else {
            Toast.makeText(MainActivity.this,"Access to Contact permission denied !",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //We are comparing requestCode with the variable contactPermissionRC because we are checking whether the request is for Contacts or not.
        if(requestCode == contactPermissionRC){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this,"Contact Permission Granted !",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(MainActivity.this,"Contact Permission Denied !",Toast.LENGTH_LONG).show();
            }
        }

        if(requestCode == permissionRL){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,"Read Log Permission Granted !",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(MainActivity.this,"Read Log Permission Denied !",Toast.LENGTH_LONG).show();
            }
        }

        if(requestCode == permissionCL){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this,"Read Location Granted !",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(MainActivity.this,"Read Location Denied !",Toast.LENGTH_LONG).show();
            }
        }

    }
}