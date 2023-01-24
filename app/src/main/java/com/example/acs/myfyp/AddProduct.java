package com.example.acs.myfyp;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddProduct extends AppCompatActivity {

    EditText Pname,price,weight,manufacturer;
    AutoCompleteTextView store;
    PlaceAutocompleteAdapter mplaceAutocompleteAdapter;
    GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40,-168),new LatLng(71,136));
    Button addProduct , goToMain;
    RadioGroup category;
    RadioButton radioButton, rad_gadgets,rad_men,rad_women,rad_beauty,rad_health,rad_appliances,rad_sports,rad_food,rad_grocery,rad_home;
    Spinner spincurrency, spinweight;
    int maxNum = 1;
    String store_name;
    double LAT;
    double LANG;
    Address address;
    String city = "Peshawar";
    String country = "Pakistan";
    String prod_Category;
    String prodID, store_ID;
    Database db ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db = new Database(this);

        Pname = (EditText) findViewById(R.id.Product_name);
        price = (EditText) findViewById(R.id.Price);
        weight = (EditText) findViewById(R.id.weight);
        manufacturer = (EditText) findViewById(R.id.manufacturer);
        store = (AutoCompleteTextView) findViewById(R.id.store);
        addProduct = (Button) findViewById(R.id.AddProduct);
        goToMain = (Button) findViewById(R.id.GoToMain);
        goToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =  new Intent(AddProduct.this,MapsActivity.class);
                startActivity(i);
            }
        });
        category = (RadioGroup) findViewById(R.id.radio_category);

        spincurrency = (Spinner) findViewById(R.id.currency);
        ArrayAdapter<String> currencyAdapter =  new ArrayAdapter<String>(this,R.layout.spinnerlist,
                getResources().getStringArray(R.array.currency));
        spincurrency.setAdapter(currencyAdapter);
        spincurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemselected = parent.getItemAtPosition(position).toString();
                if(itemselected.equals("Rs")){
                    price.setText("Rs."+price.getText()+"/-");
                }
                else{
                    price.setText("$"+price.getText());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinweight = (Spinner)findViewById(R.id.measure_weight);
        ArrayAdapter<String> weightAdapter =  new ArrayAdapter<String>(this,R.layout.spinnerlist,
                getResources().getStringArray(R.array.weight));
        spinweight.setAdapter(weightAdapter);
        spinweight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemselected = parent.getItemAtPosition(position).toString();
                        weight.setText(weight.getText()+itemselected);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        /*rad_gadgets = (RadioButton) findViewById(R.id.radio_gadgets);
        rad_appliances = (RadioButton) findViewById(R.id.radio_electronic);
        rad_men = (RadioButton) findViewById(R.id.radio_men_fashion);
        rad_women = (RadioButton) findViewById(R.id.radio_women_fashion);
        rad_beauty = (RadioButton) findViewById(R.id.radio_beauty);
        rad_health = (RadioButton) findViewById(R.id.radio_health);
        rad_home = (RadioButton) findViewById(R.id.radio_home);
        rad_sports = (RadioButton) findViewById(R.id.radio_sports);
        rad_grocery = (RadioButton) findViewById(R.id.radio_grocery);
        rad_food = (RadioButton) findViewById(R.id.radio_food);*/


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();
        Toast.makeText(getApplicationContext(),"connected to google api",Toast.LENGTH_LONG).show();

        store.setOnItemClickListener(mAutoCompleteClickListener);
        mplaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, null);
        store.setAdapter(mplaceAutocompleteAdapter);

        final String name = "NAME: " + Pname.getText();
        final String Price = "PRICE: " + price.getText();
        final String Weight = "WEIGHT:" + weight.getText();

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), name + " " + Price + " " + Weight, Toast.LENGTH_SHORT).show();
                setProductInfo();
                setStoreInfo();
                setLocationInfo();
                setPSLinfo();
            }
        });

        category.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                radioButton = (RadioButton)findViewById(checkedId);
                prod_Category = radioButton.getText().toString();
            }
        });
    }

    private void setProductInfo() {

        String prodName = Pname.getText().toString();
        String prodPrice = price.getText().toString();
        String pweight = weight.getText().toString();
        String manuftr = manufacturer.getText().toString();
        char first = prodName.charAt(0);
        char last = prodName.charAt(prodName.lastIndexOf(prodName));
         prodID = first + last + " " + maxNum ;

        db.insertProduct(prodID,prodName,prodPrice,pweight,manuftr,prod_Category);


    }

    private void setStoreInfo() {

        store_ID = "S_101";
        db.insertStore(store_ID,store_name);
    }

    private void setLocationInfo(){

        db.insertLocation(LAT,LANG,city,country);

    }

    private void setPSLinfo(){
        db.insertPSL(prodID,store_ID,LAT,LANG);

    }


    private AdapterView.OnItemClickListener mAutoCompleteClickListener =  new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final AutocompletePrediction item = mplaceAutocompleteAdapter.getItem(position);
            final String placeID = item.getPlaceId();
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient,placeID);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallbacks);
        }
    };


    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallbacks = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Toast.makeText(getApplicationContext(),places.getStatus().toString(),Toast.LENGTH_LONG).show();
            }
            final Place place = places.get(0);
            store_name =   place.getName().toString();
            LatLng latLng = place.getLatLng();
             LAT =latLng.latitude;
             LANG  =  latLng.longitude;
            /* address = (Address) place.getAddress();
             city = address.getAddressLine(0);
             country = address.getCountryName();*/
            Geocoder gcd = new Geocoder(getApplicationContext(),Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses.size() > 0) {
               Address address = addresses.get(0);
                city = address.getAddressLine(0);
                country = address.getCountryName();
            }

            Toast.makeText(getApplicationContext(),place.getName().toString()+ " " +place.getLatLng().toString(), Toast.LENGTH_LONG).show();
            places.release();

        }
    };
}
