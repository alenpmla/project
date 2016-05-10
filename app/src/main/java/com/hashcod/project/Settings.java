package com.hashcod.project;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Settings extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);



        Button savesettingsbtn=(Button)findViewById(R.id.savesettings);
        savesettingsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText domainname=(EditText)findViewById(R.id.domain);

                if(domainname.getText().toString()!=null||domainname.getText().toString()!=""){
                    SharedPreferences sharedPreferences = getSharedPreferences("appdata",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("domain",domainname.getText().toString());
                    editor.commit();

                    Toast.makeText(getApplicationContext(),"Domain Saved",Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Cannot be null. Please try again",Toast.LENGTH_SHORT).show();
                }




            }
        });

    }
}
