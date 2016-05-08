package com.hashcod.project;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int PICK_CONTACT=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button loginbutton=(Button)findViewById(R.id.loginbutton);
        loginbutton.setOnClickListener(this);

        ImageButton imageButton=(ImageButton)findViewById(R.id.piccontact);
        imageButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId()){
            case R.id.loginbutton:
                login();
                break;

            case R.id.piccontact:
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, PICK_CONTACT);
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        EditText phonenumber=(EditText) findViewById(R.id.phonenumbertext);
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case PICK_CONTACT:
                    Cursor cursor = null;
                    try {
                        String phoneNo = null ;
                        String name = null;
                        Uri uri = data.getData();
                        cursor = getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();
                        int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        phoneNo = cursor.getString(phoneIndex);

                        phonenumber.setText(phoneNo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }


    public void login()
    {

        EditText name=(EditText) findViewById(R.id.nametext);
        EditText phonenumber=(EditText) findViewById(R.id.phonenumbertext);

        if(name.getText().toString()==null||name.getText().toString().equals(""))
        {
            System.out.println("cannot be null");
            Toast.makeText(getApplicationContext(),"Name and Phonenumber cannot be null",Toast.LENGTH_SHORT).show();



        }
       else
        {
            System.out.println(name.getText().toString());
            System.out.println(phonenumber.getText().toString());
            SharedPreferences sharedPreferences=getSharedPreferences("app_data",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("key_name",name.getText().toString());
            editor.putString("key_number",phonenumber.getText().toString());
            editor.commit();


            Intent mapintent=new Intent(MainActivity.this,MapsActivity.class);
            startActivity(mapintent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }
}
