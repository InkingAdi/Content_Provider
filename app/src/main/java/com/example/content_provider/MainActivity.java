package com.example.content_provider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //List
    ListView listview;

    //Array adapter to getTheString and add it into ListView
    ArrayAdapter<String> adapter;

    //To Store list of Contact
    ArrayList<String> contactList ;

    String Name, Contact;

    public final static int contactPermissionRC = 101;

    //Cursor object to store large amount of data;
    Cursor cursor;


    @SuppressLint(value = "Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        contactList = new ArrayList<String>();
        listview = findViewById(R.id.listView);

        //Asking Runtime permission
        checkContactPermission(Manifest.permission.READ_CONTACTS,contactPermissionRC);


        findViewById(R.id.button).setOnClickListener(view -> {

            //Getting All the Data into the cursor variable
                cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);

            while(cursor.moveToNext() && cursor != null) {

                Name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                Contact = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                contactList.add(Name + " : " + Contact+"\n");

            }
            Log.d("LOGS",contactList.toString());
            cursor.close();

            adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_dropdown_item,contactList);
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

    }
}