package com.hashcod.project;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    ListView listView;
    ArrayList<WorkShopClass>arrayList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar) ;
        toolbar.setTitle("Workshops Nearby");
        toolbar.setNavigationIcon(android.R.drawable.title_bar);

        Toolbar toolbarnew=(Toolbar)findViewById(R.id.toolbar) ;
        toolbar.setTitle("Workshops Nearby");
        toolbar.setNavigationIcon(android.R.drawable.title_bar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        WorkshopAdapter workshopAdapter=new WorkshopAdapter(getApplicationContext(),arrayList);

         listView=(ListView)findViewById(R.id.nearbylist);
        listView.setAdapter(workshopAdapter);


        for(int i=0;i<10;i++){
            WorkShopClass workShopClass=new WorkShopClass();
            workShopClass.setTitle("title-"+i);
            workShopClass.setDescription("description-"+i);
            workShopClass.setDistance("destance-"+i);
            arrayList.add(workShopClass);
        }



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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        // Add a marker in Sydney and move the camera
       final LatLng sydney = new LatLng(-34, 151);
        final  LatLng cochin = new LatLng(10.015966, 76.335552);

        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.addMarker(new MarkerOptions().position(cochin).title("Marker in Webmobi"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cochin));


       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


               switch (position){
                   case 0:
                       mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                       mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                       break;
                   case 1:
                       mMap.addMarker(new MarkerOptions().position(cochin).title("Marker in Webmobi"));
                       mMap.moveCamera(CameraUpdateFactory.newLatLng(cochin));

               }
           }
       });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        switch (marker.getTitle().toString())
        {
            case "Marker in Sydney":
              //  Toast.makeText(getApplicationContext(),"first marker clicked",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MapsActivity.this,DetailActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;
            case "Marker in Webmobi":
              //  Toast.makeText(getApplicationContext(),"second marker clicked",Toast.LENGTH_SHORT).show();
                Intent intent1=new Intent(MapsActivity.this,DetailActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;
        }
        return false;
    }
}
