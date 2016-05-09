package com.hashcod.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirstPage extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_first_page);


        Button login=(Button)findViewById(R.id.loginfp);
        login.setOnClickListener(this);
        Button register=(Button)findViewById(R.id.registerfp);
        register.setOnClickListener(this);

        Button domainset=(Button)findViewById(R.id.setdomain);
        domainset.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()){
            case R.id.loginfp:
                Intent loginpage=new Intent(FirstPage.this,MainActivity.class);
                startActivity(loginpage);
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;
            case R.id.registerfp:
                Intent registerpage=new Intent(FirstPage.this,RegisterActivity.class);

                startActivity(registerpage);
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;
            case R.id.setdomain:
                Intent setdomain=new Intent(FirstPage.this,Settings.class);
                startActivity(setdomain);
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;

            default:

        }
    }
}
