package com.webivoire.babyissweetest.view;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.SupportActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;
import com.webivoire.babyissweetest.Manifest;
import com.webivoire.babyissweetest.R;
import com.webivoire.babyissweetest.base.BaseApplication;
import com.webivoire.babyissweetest.model.Item;
import com.webivoire.babyissweetest.tools.Tools;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fabian on 26/02/2017.
 */

public class HomeActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        OnMapReadyCallback,
        LocationListener
        {

            private static final int REQUEST_LOCATION = 235;


            @BindView(R.id.viewPager) ViewPager viewPager;

    //------------------------------------- mes varialbles-----------------------------------------------//
    private HomePagerAdapter homePagerAdapter;
    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;

           //-----------------------------------------------------------------------------//


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        ButterKnife.bind(this);

        // initiation du google api client

        googleApiClient = new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks(this)
                            .addApi(LocationServices.API)
                            .build();

        initViewPager();

    }

            private void initViewPager(){

                homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
                viewPager.setOffscreenPageLimit(2);
                viewPager.setAdapter(homePagerAdapter);

            }

    // On override le onMapReady pour le google map
            @Override
            public void onMapReady(GoogleMap googleMap) {

                this.googleMap = googleMap;
                if(hasUserLocationAuthorization()){
                    googleMap.setMyLocationEnabled(true);
                }else{
                    requestUserAuthorization();
                }
                RealmResults<Item> lieux = BaseApplication.getRealmInstance()
                                            .where(Item.class).findAll();
                googleMap.clear(); // on nettoie la map
                LatLngBounds.Builder bounds = new LatLngBounds.Builder();// on contruire la lat et lng
                for (Item lieu : lieux){
                    LatLng latLng = new LatLng(lieu.getLat(), lieu.getLng());
                    googleMap.addMarker(
                            new MarkerOptions()
                                    .position(latLng)
                                    .title(lieu.getNom())
                    );
                    bounds.include(latLng);
                }
                CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds.build(), 50);
                googleMap.animateCamera(update);
            }


//---------------------------------- les permissions-------------------------------------//


            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

                if(requestCode == REQUEST_LOCATION) {
                    if(grantResults.length > 0){
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && googleMap !=null)
                            googleMap.setMyLocationEnabled(true);
                    }

                }
            }

            private boolean hasUserLocationAuthorization(){ // si permission hautorisé

            return ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;

        }
            private void requestUserAuthorization() {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            }

//--------------------------------------------------------------------------------//

// --------------------------------------connexion-----------------------------------------//

            @Override
            public void onConnected(@Nullable Bundle bundle) {
                if (hasUserLocationAuthorization()){

                    Location userLoc = LocationServices.
                            FusedLocationApi.getLastLocation(googleApiClient);

                    if (userLoc != null) {
                        Toast.makeText(this,
                                userLoc.getLatitude() + "-" + userLoc.getLongitude(),
                                Toast.LENGTH_SHORT).show();
                    }
                    LocationRequest request = LocationRequest.create();
                    request.setInterval(15000); // 1000 * 60 * 5
                    //request.setSmallestDisplacement(100); // in meters
                    LocationServices.FusedLocationApi.
                            requestLocationUpdates(
                                    googleApiClient,
                                    request,
                                    this);


                }else{
                    requestUserAuthorization();
                }


            }

            @Override
            public void onConnectionSuspended(int i) {
                //si la cnnexion est suspedu il faut effacer la localisation
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

            }

            @Override
            public void onLocationChanged(Location location) {

            }

//------------------------------------------------------------------------------------------------//
 //------------------------------------adapter------------------------------------------//

            class HomePagerAdapter extends FragmentPagerAdapter {

        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {

                case 0:
                    return new ListFragment();
                case 1:
                    SupportMapFragment fg = SupportMapFragment.newInstance();
                    fg.getMapAsync(HomeActivity.this);
                    return fg;

                default:
                return new ListFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
