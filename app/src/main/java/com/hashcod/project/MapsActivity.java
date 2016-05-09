package com.hashcod.project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener  {

    Boolean onconnectedcalled=false;
    private GoogleMap mMap;
    String radiusstr="";
    ListView listView;
    String domainstr="";
    WorkshopAdapter workshopAdapter;
    ArrayList<DetailsClass>detailclassarray=new ArrayList<>();
public  GoogleMap currentlocationgooglemap;



    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar) ;
        toolbar.setTitle("Workshops Nearby");
        toolbar.setNavigationIcon(android.R.drawable.title_bar);

        Toolbar toolbarnew=(Toolbar)findViewById(R.id.toolbar) ;
        toolbar.setTitle("Workshops Nearby");
        toolbar.setNavigationIcon(android.R.drawable.title_bar);

        SharedPreferences sharedPreferences = getSharedPreferences("appdata",MODE_PRIVATE);
        domainstr=sharedPreferences.getString("domain","128.199.218.126");




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




         workshopAdapter=new WorkshopAdapter(getApplicationContext(),detailclassarray);

         listView=(ListView)findViewById(R.id.nearbylist);
        listView.setAdapter(workshopAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final LatLng latandlong = new LatLng(detailclassarray.get(position).getLat(), detailclassarray.get(position).getLng());
                mMap.addMarker(new MarkerOptions().position(latandlong).title(detailclassarray.get(position).getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latandlong));

                CameraPosition cameraPosition = new CameraPosition.Builder().target(latandlong).zoom(14.0f).build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                mMap.moveCamera(cameraUpdate);




            }
        });



        Button picklocation=(Button)findViewById(R.id.picklocation);
        picklocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    int PLACE_PICKER_REQUEST = 1;
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                    startActivityForResult(builder.build(MapsActivity.this), PLACE_PICKER_REQUEST);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


        Button radius20=(Button)findViewById(R.id.radius20);
        Button radius50=(Button)findViewById(R.id.radius50);
        Button radius100=(Button)findViewById(R.id.radius100);

        radius20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radiusstr="20";
                getnearestpositionspicradius();
            }
        });

        radius50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radiusstr="50";
                getnearestpositionspicradius();
            }
        });


        radius100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radiusstr="100";
                getnearestpositionspicradius();
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);


                String toastMsg = String.format(""+place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                LatLng latLng=place.getLatLng();
                currentLatitude = latLng.latitude;
                currentLongitude = latLng.longitude;

                getnearestpositionspic();

            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        try{
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            } else {
                //If everything went fine lets get latitude and longitude

                if(onconnectedcalled==false)

                {

                    onconnectedcalled=true;
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();

                    Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
                    getnearestpositions();

                }

                final LatLng currentlocationnew = new LatLng(currentLatitude, currentLongitude);

                mMap.addMarker(new MarkerOptions().position(currentlocationnew).title("You are here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentlocationnew));

                CameraPosition cameraPosition = new CameraPosition.Builder().target(currentlocationnew).zoom(14.0f).build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                mMap.moveCamera(cameraUpdate);

            }
        }
       catch (SecurityException se){
           se.printStackTrace();
       }
    }
    @Override
    public void onConnectionSuspended(int i) {}
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
            /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * If locationChanges change lat and long
     *
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    public  void getnearestpositionspic(){

        detailclassarray.clear();
workshopAdapter.notifyDataSetChanged();


     // final ProgressDialog progressDialognew=new ProgressDialog(this);
      //  progressDialognew.show();


        String tag_json_obj = "json_obj_req";

        String url = "http://"+domainstr+"/search";



        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println("search response===="+response);

                    JSONArray reader = new JSONArray(response);


                    for(int i=0;i<reader.length();i++)
                    {
                        DetailsClass detailclass=new DetailsClass();
                        detailclass.setName(reader.getJSONObject(i).getString("name"));
                        detailclass.setAddress(reader.getJSONObject(i).getString("address"));
                        detailclass.setPincode(reader.getJSONObject(i).getString("pincode"));
                        detailclass.setPhone(reader.getJSONObject(i).getString("phone"));
                        detailclass.setOpenhours(reader.getJSONObject(i).getString("openhours"));
                        detailclass.setWeekdays(reader.getJSONObject(i).getString("weekdays"));
                        detailclass.setType(reader.getJSONObject(i).getString("type"));
                        detailclass.setLat(reader.getJSONObject(i).getDouble("lat"));
                        detailclass.setLng(reader.getJSONObject(i).getDouble("lng"));
                        detailclass.setDistance(reader.getJSONObject(i).getString("distance"));
                        detailclassarray.add(detailclass);

                    }


                    workshopAdapter.notifyDataSetChanged();

                    final LatLng latandlong = new LatLng(currentLatitude, currentLongitude);
                    mMap.addMarker(new MarkerOptions().position(latandlong).title("Current Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latandlong));

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latandlong).zoom(14.0f).build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                    mMap.moveCamera(cameraUpdate);

                } catch (JSONException ignored) {
                }
              //  progressDialognew.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

             //   progressDialognew.hide();
                if (volleyError instanceof TimeoutError) {
                 //   progressDialognew.hide();
                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("lat", ""+currentLatitude);
                headers.put("lng", ""+currentLongitude);
                headers.put("radius", "50");
                return headers;
            }

            @Override
            public Request.Priority getPriority() {
                return Request.Priority.IMMEDIATE;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);


    }

    public  void getnearestpositionspicradius(){

        detailclassarray.clear();
        workshopAdapter.notifyDataSetChanged();



        // final ProgressDialog progressDialognew=new ProgressDialog(this);
        //  progressDialognew.show();


        String tag_json_obj = "json_obj_req";

        String url = "http://"+domainstr+"/search";



        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println("search response===="+response);

                    JSONArray reader = new JSONArray(response);


                    if(response==null){
                        detailclassarray.clear();


                    }
                    else {
                        for(int i=0;i<reader.length();i++)
                        {
                            DetailsClass detailclass=new DetailsClass();
                            detailclass.setName(reader.getJSONObject(i).getString("name"));
                            detailclass.setAddress(reader.getJSONObject(i).getString("address"));
                            detailclass.setPincode(reader.getJSONObject(i).getString("pincode"));
                            detailclass.setPhone(reader.getJSONObject(i).getString("phone"));
                            detailclass.setOpenhours(reader.getJSONObject(i).getString("openhours"));
                            detailclass.setWeekdays(reader.getJSONObject(i).getString("weekdays"));
                            detailclass.setType(reader.getJSONObject(i).getString("type"));
                            detailclass.setLat(reader.getJSONObject(i).getDouble("lat"));
                            detailclass.setLng(reader.getJSONObject(i).getDouble("lng"));
                            detailclass.setDistance(reader.getJSONObject(i).getString("distance"));
                            detailclassarray.add(detailclass);

                        }
                    }


                    final LatLng latandlong = new LatLng(currentLatitude, currentLongitude);
                    mMap.addMarker(new MarkerOptions().position(latandlong).title("Current Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latandlong));

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latandlong).zoom(14.0f).build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                    mMap.moveCamera(cameraUpdate);


                    workshopAdapter.notifyDataSetChanged();
                } catch (JSONException ignored) {
                }
                //  progressDialognew.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                //   progressDialognew.hide();
                if (volleyError instanceof TimeoutError) {
                    //   progressDialognew.hide();
                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("lat", ""+currentLatitude);
                headers.put("lng", ""+currentLongitude);
                headers.put("radius", radiusstr);
                return headers;
            }

            @Override
            public Request.Priority getPriority() {
                return Request.Priority.IMMEDIATE;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);


    }

    public  void getnearestpositions(){

        detailclassarray.clear();
workshopAdapter.notifyDataSetChanged();


        // final ProgressDialog progressDialognew=new ProgressDialog(this);
        //  progressDialognew.show();


        String tag_json_obj = "json_obj_req";

        String url = "http://"+domainstr+"/search";



        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println("search response===="+response);

                    JSONArray reader = new JSONArray(response);


                    for(int i=0;i<reader.length();i++)
                    {
                        DetailsClass detailclass=new DetailsClass();
                        detailclass.setName(reader.getJSONObject(i).getString("name"));
                        detailclass.setAddress(reader.getJSONObject(i).getString("address"));
                        detailclass.setPincode(reader.getJSONObject(i).getString("pincode"));
                        detailclass.setPhone(reader.getJSONObject(i).getString("phone"));
                        detailclass.setOpenhours(reader.getJSONObject(i).getString("openhours"));
                        detailclass.setWeekdays(reader.getJSONObject(i).getString("weekdays"));
                        detailclass.setType(reader.getJSONObject(i).getString("type"));
                        detailclass.setLat(reader.getJSONObject(i).getDouble("lat"));
                        detailclass.setLng(reader.getJSONObject(i).getDouble("lng"));
                        detailclass.setDistance(reader.getJSONObject(i).getString("distance"));
                        detailclassarray.add(detailclass);

                    }


                    workshopAdapter.notifyDataSetChanged();

                } catch (JSONException ignored) {
                }
                //  progressDialognew.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                //   progressDialognew.hide();
                if (volleyError instanceof TimeoutError) {
                    //   progressDialognew.hide();
                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("lat", ""+currentLatitude);
                headers.put("lng", ""+currentLongitude);
                headers.put("radius", "50");
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
    public void onMapReady(GoogleMap googleMap) {

        currentlocationgooglemap=googleMap;

        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        // Add a marker in Sydney and move the camera

        for(int i=0;i<detailclassarray.size();i++){

            final LatLng latandlong = new LatLng(detailclassarray.get(i).getLat(), detailclassarray.get(i).getLng());
            mMap.addMarker(new MarkerOptions().position(latandlong).title(detailclassarray.get(i).getName()));
           mMap.moveCamera(CameraUpdateFactory.newLatLng(latandlong));
        }




    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Intent intent=new Intent(MapsActivity.this,DetailActivity.class);


        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(marker.getId().toString());
        int pos=0;

        while (m.find()) {
            pos=Integer.parseInt(m.group());
            System.out.println(m.group());
        }
        int realpos=pos-1;

        Bundle bundle=new Bundle();

        try{
            bundle.putString("name",detailclassarray.get(realpos).getName());
            bundle.putString("address",detailclassarray.get(realpos).getAddress());
            bundle.putString("pincode",detailclassarray.get(realpos).getPincode());
            bundle.putString("openhours",detailclassarray.get(realpos).getOpenhours());
            bundle.putString("weekdays",detailclassarray.get(realpos).getWeekdays());
            bundle.putString("type",detailclassarray.get(realpos).getType());
            bundle.putString("distance",detailclassarray.get(realpos).getDistance());
            bundle.putString("phone",detailclassarray.get(realpos).getPhone());
        }catch (Exception e){
            e.printStackTrace();
        }

        intent.putExtras(bundle);

        if(realpos==-1)
        {

        }
        else {
            startActivity(intent);
        }


        return false;
    }


}
