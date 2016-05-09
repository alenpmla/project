package com.hashcod.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    String name,address,pincode,distance,openhours,weekdays,type,phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        name= bundle.getString("name","");
        address= bundle.getString("address","");
        pincode= bundle.getString("pincode","");
        distance= bundle.getString("distance","");
        openhours= bundle.getString("openhours","");
        weekdays= bundle.getString("weekdays","");
        type= bundle.getString("type","");
        phone=bundle.getString("phone","");


        TextView nametv=(TextView)findViewById(R.id.nameofservice);
        nametv.setText(name);
        TextView addresstv=(TextView)findViewById(R.id.address);
        addresstv.setText(address);
        TextView pincodetv=(TextView)findViewById(R.id.pincode);
        pincodetv.setText(pincode);

        TextView distancetv=(TextView)findViewById(R.id.distance);
        distancetv.setText(distance);

        TextView openhourstv=(TextView)findViewById(R.id.openhours);
        openhourstv.setText(openhours);

        TextView workingdaystv=(TextView)findViewById(R.id.workingdays);
        workingdaystv.setText(weekdays);

        TextView typetv=(TextView)findViewById(R.id.type);
        typetv.setText(type);

        TextView phonetv=(TextView)findViewById(R.id.phonetv);
        phonetv.setText(phone);



    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }
}
