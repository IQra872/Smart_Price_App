package com.example.acs.myfyp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements NetworkStateReceiver.NetworkStateReceiverListener, OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener,LocationListener {
     boolean permissionGranted;
     private GoogleMap mMap;
     private FusedLocationProviderClient mfusedLocationProviderClient;
     private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
     private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40,-168),new LatLng(71,136));
     double latitude;
     double longitude;
     private int PROXIMITY_RADIUS = 5000;
     AutoCompleteTextView searchBar;
     ImageButton searchBtn;
     ImageView locationInfo;
     ImageView currentLocInfo;
     ImageButton showDS;
     ImageButton showMS;
     ImageButton showHS;
     Button addProduct;
     PlaceAutocompleteAdapter mplaceAutocompleteAdapter;
     GoogleApiClient mGoogleApiClient;
     Marker mMarker;
     Location currentLocation;
     LocationManager locationManager;
     NetworkStateReceiver receiver;


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        searchBar= (AutoCompleteTextView) findViewById(R.id.searchBar);
        searchBtn = (ImageButton)findViewById(R.id.searchBtn);
        locationInfo = (ImageView)findViewById(R.id.locInfo);
        showDS = (ImageButton) findViewById(R.id.getDS);
        showMS = (ImageButton) findViewById(R.id.getMS);
        showHS = (ImageButton) findViewById(R.id.getHS);
        currentLocInfo = (ImageView)findViewById(R.id.currentLoc);

        receiver =  new NetworkStateReceiver();
        receiver.addListener(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocationPermission();

        Database db = new Database(this);

        addProduct = (Button)findViewById(R.id.AddProduct);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =  new Intent(MapsActivity.this,AddProduct.class);
                startActivity(i);
            }
        });

        locationInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMarker!=null) {
                    if (mMarker.isInfoWindowShown()) {
                        mMarker.hideInfoWindow();
                    } else {
                        mMarker.showInfoWindow();
                    }
                }}
        });

        currentLocInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geoLocate();
            }
        });

       buildGoogleApiClient();

        searchBar.setOnItemClickListener(mAutoCompleteClickListener);
        mplaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this,mGoogleApiClient,LAT_LNG_BOUNDS,null);
        searchBar.setAdapter(mplaceAutocompleteAdapter);

        showDS.setOnClickListener(new View.OnClickListener() {
            String type = "supermarket";
            @Override
            public void onClick(View v) {
                setNearbyPlaces(type);
            }
        });
        showMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = "pharmacy";
                setNearbyPlaces(type);
            }
        });
        showHS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = "hardware_store";
                setNearbyPlaces(type);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        receiver.removeListener(this);
        unregisterReceiver(receiver);

    }


    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toast.makeText(getApplicationContext(), "Map is ready", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (permissionGranted) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            getCurrentLocation();
        }
    }

    private void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mGoogleApiClient.connect();
        Toast.makeText(getApplicationContext(),"connected to google api",Toast.LENGTH_LONG).show();
    }

    private void getCurrentLocation() {
        mMap.clear();
        Toast.makeText(getApplicationContext(), "getting current location", Toast.LENGTH_SHORT).show();
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (permissionGranted) {
                final Task location = mfusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if ((location.isSuccessful()) && (isConnected()) && (task.isSuccessful())) {
                            currentLocation = (Location) task.getResult();
                            latitude = currentLocation.getLatitude();
                            longitude=currentLocation.getLongitude();
                            LatLng lng = new LatLng(latitude,longitude);
                            moveCamera(lng,
                                    15f, "My current location");}
                        else {
                            Toast.makeText(getApplicationContext(), "unable to get current Location", Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        }
        catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), "getCurrentLocation : Security Exception", Toast.LENGTH_SHORT).show();}
    }

    private void moveCamera(LatLng latLng ,float zoom,String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        String snippet = title;
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(snippet);
        if(title == "My current location"){
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
                else
        {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        }
       mMarker = mMap.addMarker(options);
    }

    private void moveCamera(LatLng latLng ,float zoom,Place placeinfo){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        mMap.clear();

        if(placeinfo != null){
            String snippet = "Adress" + placeinfo.getAddress() + placeinfo.getName() + placeinfo.getRating();

            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    .snippet(snippet)
                    .title(placeinfo.getName().toString());
            mMarker = mMap.addMarker(options);
        }
        else
        {
            mMap.addMarker(new MarkerOptions().position(latLng));
        }

    }



    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "entered");

        currentLocation = location;
        if (mMarker != null) {
            mMarker.remove();
        }

        //Place current location marker
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        moveCamera(latLng,15f,"My current Location");

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
    }

    private void geoLocate(){
        Toast.makeText(getApplicationContext()," geoLocate",Toast.LENGTH_SHORT).show();
        String searchString= searchBar.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString,1);
            if(list.size()>0)
            {
                Address address = list.get(0);
                Toast.makeText(getApplicationContext(),"geoLocate : Location found",Toast.LENGTH_SHORT).show();
                moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),15f,address.getAddressLine(0));
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext()," geoLocate : IOException",Toast.LENGTH_SHORT).show();
        }

    }

    private void getLocationPermission(){
        Toast.makeText(getApplicationContext(),"getting location permissions",Toast.LENGTH_SHORT).show();
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                permissionGranted = true;
                initMap();
            }
            else{
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults){
                 permissionGranted = false;
     switch(requestCode){
         case LOCATION_PERMISSION_REQUEST_CODE:
         {
             if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                 for (int i=0 ; i<grantResults.length;i++){
                     if(grantResults.length > 0 && grantResults[i] != PackageManager.PERMISSION_GRANTED){
                         permissionGranted = false;
                         Toast.makeText(getApplicationContext(),"onRequestPermissionResult : Permission Failed",Toast.LENGTH_SHORT).show();
                         return;
                     }
                 }
                 Toast.makeText(getApplicationContext(),"onRequestPermissionResult : Permission granted",Toast.LENGTH_SHORT).show();
                 permissionGranted=true;
                 initMap();
             }
         }
     }

}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }


/*
------------------google place API , AutoComplete Suggestions ---------------------
 */

private AdapterView.OnItemClickListener mAutoCompleteClickListener =  new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final AutocompletePrediction item = mplaceAutocompleteAdapter.getItem(position);
        final String placeID = item.getPlaceId();
        PendingResult <PlaceBuffer> placeResult = Places.GeoDataApi
                .getPlaceById(mGoogleApiClient,placeID);
        placeResult.setResultCallback(mUpdatePlaceDetailsCallbacks);
    }
};


private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallbacks = new ResultCallback<PlaceBuffer>() {
    @Override
    public void onResult(@NonNull PlaceBuffer places) {
        if(!places.getStatus().isSuccess()){
            Toast.makeText(getApplicationContext(),places.getStatus().toString(),Toast.LENGTH_LONG).show();
           // places.release();
        }
        final Place place = places.get(0);


       /* PlaceInfo mplace = new PlaceInfo();

        mplace.setID(place.getId());
        mplace.setAddress(place.getAddress().toString());
        mplace.setLatlng(place.getLatLng());
        mplace.setName(place.getName().toString());
//        mplace.setAttributions(place.getAttributions().toString());
        mplace.setRatings(place.getRating());
        mplace.setPhoneNumber(place.getPhoneNumber().toString());
        mplace.setUri(place.getWebsiteUri());


        String title = mplace.getName() + "\n"
                + mplace.getAddress() + "\n"
                + mplace.getAttributions() + "\n"
                + mplace.getPhoneNumber() +"\n"
                + mplace.getRatings() + "\n"
                + mplace.getLatlng() + "\n"
                + mplace.getUri();*/

       moveCamera(place.getLatLng(),15f,place);
       places.release();

    }
};

    private boolean isConnected() {
    boolean connected = false;
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
        connected = true;
    } else {
        connected = false;
    }
    return connected;
}

    private void setNearbyPlaces(String type){
            mMap.clear();
            String url = getUrl(latitude, longitude, type);
            Object[] DataTransfer = new Object[2];
            DataTransfer[0] = mMap;
            DataTransfer[1] = url;
            Log.d("onClick", url);
            GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
            getNearbyPlacesData.execute(DataTransfer);
}

    @Override
    public void networkAvailable() {
        Toast.makeText(getApplicationContext(), "your connection is back", Toast.LENGTH_SHORT).show();
        getLocationPermission();

    }

    @Override
    public void networkUnavailable() {
       Toast.makeText(getApplicationContext(),"Sorry : Your internet connection is currently unavailable",Toast.LENGTH_LONG).show();
    }
}

