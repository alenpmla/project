package com.hashcod.project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.SubMenuBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    String domainstr="";

    Double lattitude,logintude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        SharedPreferences sharedPreferences = getSharedPreferences("appdata",MODE_PRIVATE);
        domainstr=sharedPreferences.getString("domain","");

        Button regbtn=(Button)findViewById(R.id.registerbtn);
        Button chooselocation=(Button)findViewById(R.id.chooselocation);


        chooselocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    int PLACE_PICKER_REQUEST = 1;
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                    startActivityForResult(builder.build(RegisterActivity.this), PLACE_PICKER_REQUEST);
                }
             catch (Exception e){
                 e.printStackTrace();
             }
            }
        });


        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)

            {

                senddatatoserver();


            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);


                String toastMsg = String.format(""+place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

LatLng latLng=place.getLatLng();
                 lattitude = latLng.latitude;
                 logintude = latLng.longitude;

            }
        }
    }

    public void senddatatoserver()
    {
      final  ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.show();

        String url = "http://"+domainstr+"/register";



        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //  progressDialognew.hide();
                Toast.makeText(getApplicationContext(),"Registered",Toast.LENGTH_SHORT).show();
                      System.out.print("Register response"+response);
                progressDialog.hide();

                AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();

                // Setting Dialog Title
                alertDialog.setTitle("Success");

                // Setting Dialog Message
                alertDialog.setMessage("Press ok to go back");

                // Setting Icon to Dialog
                alertDialog.setIcon(R.mipmap.ic_launcher);

                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        finish();
                    }
                });

                // Setting OK Button



                alertDialog.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.hide();
                Toast.makeText(getApplicationContext(),"Error sending data",Toast.LENGTH_SHORT).show();
                //   progressDialognew.hide();
                if (volleyError instanceof TimeoutError) {
                    //   progressDialognew.hide();

                    Toast.makeText(getApplicationContext(),"Error sending data",Toast.LENGTH_SHORT).show();

                    progressDialog.hide();
                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                EditText name=(EditText)findViewById(R.id.servicecentername);
                EditText place=(EditText)findViewById(R.id.placeservice);

                EditText districtservice=(EditText)findViewById(R.id.districtservice);

                EditText pinservice=(EditText)findViewById(R.id.pinservice);

                EditText licenseservice=(EditText)findViewById(R.id.licenseservice);

                EditText phoneservicescenter=(EditText)findViewById(R.id.phoneservicescenter);


                EditText openhours=(EditText)findViewById(R.id.openhourset);
                CheckBox two=(CheckBox)findViewById(R.id.two);

                CheckBox three=(CheckBox)findViewById(R.id.three);

                CheckBox four=(CheckBox)findViewById(R.id.four);

                CheckBox eight=(CheckBox)findViewById(R.id.eight);
                StringBuffer result = new StringBuffer();
                String typeofvehicle="";



                if(two.isChecked())
                {
                    result.append("2");
                }
                else if(three.isChecked())
                {
                    if(typeofvehicle!=""){
                        result.append(",");
                    }
                    result.append("3");
                }
                else if(four.isChecked())
                {
                    if(typeofvehicle!=""){
                        result.append(",");
                    }
                    result.append("4");
                }

                else if(eight.isChecked())

                {
                    if(typeofvehicle!=""){
                        result.append(",");
                    }
                    result.append("8");
                }

                System.out.println("Type of vehicle==="+result.toString());
                HashMap<String, String> headers = new HashMap<>();
                headers.put("name",name.getText().toString() );
                headers.put("phone",phoneservicescenter.getText().toString());
                headers.put("licenseno",licenseservice.getText().toString());
                headers.put("pincode",pinservice.getText().toString());
                headers.put("openhours",openhours.getText().toString());
                headers.put("weekdays", "all");
                headers.put("address", districtservice.getText().toString());
                headers.put("type",result.toString());
                headers.put("lat",""+lattitude);
                headers.put("lng",""+logintude);
                return headers;
            }

            @Override
            public Request.Priority getPriority() {
                return Request.Priority.IMMEDIATE;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);







    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }
}
