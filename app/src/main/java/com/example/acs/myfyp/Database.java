package com.example.acs.myfyp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ACS on 7/7/2018.
 */

public class Database extends SQLiteOpenHelper {

   private static final String DATABASE_NAME = "FYPdatabse";
   private static final int DATABASE_VERSION = 1;
    final String TABLE_USER = "USER";
    final String TABLE_PRODUCT = "PRODUCT";
    final String TABLE_CATEGORY = "CATEGORY";
    final String TABLE_PREFRENCES = "USER_PREFRENCES";
    final String TABLE_STORE = "STORE";
    final String TABLE_LOCATION = "LOCATION";
    final String TABLE_PRODUCT_STORE_LOCATION = "PRODUCT_STORE_LOCATION";
    SQLiteDatabase database;

            public Database(Context context) {
                super(context, DATABASE_NAME, null, DATABASE_VERSION);
                database = getWritableDatabase();
            }


            @Override
            public void onCreate(SQLiteDatabase db) {

                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USER + "(U_ID TEXT PRIMARY KEY  NOT NULL , F_name TEXT," +
                        "L_name TEXT," +
                        "Email TEXT," +
                        "Password TEXT);");

                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCT + "(PRD_ID TEXT PRIMARY KEY NOT NULL , " +
                        "Product_Name TEXT," +
                        "Price INTEGER," +
                        "Weight TEXT," +
                        "Manufacturer TEXT," +
                        "Category_ID TEXT ," +
                        "FOREIGN KEY(Category_ID) REFERENCES "+ TABLE_CATEGORY + "(C_ID));");

                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORY + "( C_ID TEXT PRIMARY KEY NOT NULL,Category_Name TEXT);");

                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PREFRENCES + "(Category_ID TEXT,User_ID TEXT ," +
                        "FOREIGN KEY(Category_ID) REFERENCES "+ TABLE_CATEGORY + "(C_ID)" +
                        ",FOREIGN KEY(User_ID) REFERENCES "+ TABLE_USER + "(U_ID));");

                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_STORE + "(Str_ID TEXT PRIMARY KEY NOT NULL,Store_Name TEXT);");

                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LOCATION + "(  Latitude FLOAT, " +
                        "Longitude FLOAT," +
                        "City TEXT," +
                        "Country TEXT," +
                        "PRIMARY KEY(Latitude,Longitude));");

                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCT_STORE_LOCATION + "(Product_ID TEXT," +
                        "Store_ID TEXT, " +
                        "Latitude FLOAT," +
                        "Longitude FLOAT," +
                        "FOREIGN KEY(Product_ID) REFERENCES "+ TABLE_PRODUCT + "(PRD_ID)," +
                        "FOREIGN KEY(STore_ID) REFERENCES "+ TABLE_STORE + "(Str_ID)," +
                        "FOREIGN KEY(Latitude) REFERENCES "+ TABLE_LOCATION + "(Latitude)," +
                        "FOREIGN KEY(Longitude) REFERENCES "+ TABLE_LOCATION + "(Longitude));");

                String[] catID = {"PT_01", "MF_02", "WF_03", "BTY_04", "HLTH_05", "EA_06", "HL_07", "SP_08", "GRC_09", "FD_10"};
                String[] catName = {"Electronic Gadgets","Men Fashion","Women Fashion", "Beauty","Health and Medicine",
                        "Electronic Appliances","Home and Living", "Sports", "Grocery", "Food"};

                ContentValues values =  new ContentValues();

                for(int i=0; i < catID.length; i++) {

                    values.put("C_ID", catID[i]);
                    values.put("Category_Name", catName[i]);
                    db.insert(TABLE_CATEGORY,null, values);
                    values.clear();
                }
                Log.d("Tag","categories table populated successfully");
            }


            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


            }

            public void doSignup(Context context,String uid,String fname,String lname,String email,String pwd){

                ContentValues values = new ContentValues();
                values.put("U_ID",uid);
                values.put("F_name",fname);
                values.put("L_name",lname);
                values.put("Email",email);
                values.put("Password",pwd);
                database.insert(TABLE_USER,null,values);
                Toast.makeText(context,"You are signed up successfully",Toast.LENGTH_SHORT).show();

            }

            public boolean checkForLogin(String email,String pwd){

                boolean authenticated ;

                String[] args ={email,pwd};
                String query = "SELECT * FROM USER "
                        + "WHERE Email = '" + args[0] + "'"
                        + " AND Password = '" + args[1] + "'";
               Cursor c= database.rawQuery(query,null);

              if((c != null) && (c.moveToFirst())){
                  authenticated = true;
              }
              else{
                  authenticated = false;
              }
                return authenticated;
            }

            public void storePreferences(ArrayList list,String userID){

                ArrayList Ids = new ArrayList();
                for(int i=0 ; i< list.size(); i++) {

                    String query = "Select C_ID from " + TABLE_CATEGORY + " where Category_Name = '" + list.get(i) + "'";
                 Cursor c =    database.rawQuery(query,null);
                    if((c != null) && (c.moveToFirst())){
                    Ids.add(c.getString(c.getColumnIndex("C_ID")));
                }}
                ContentValues values = new ContentValues();
                for(int i=0 ; i<Ids.size();i++){
                    values.put("Category_ID",Ids.get(i).toString());
                    values.put("User_ID",userID);
                    database.insert(TABLE_PREFRENCES,null,values);
                    values.clear();
                }

            }

            public void insertProduct(String product_ID, String product_name, String price , String weight, String manufacturer,String prod_category){

                String cat_ID = " ";
                String query = "Select C_ID from " + TABLE_CATEGORY + " where Category_Name = '" + prod_category + "'";
                Cursor c =    database.rawQuery(query,null);
                if((c != null) && (c.moveToFirst())) {
                    cat_ID = c.getString(c.getColumnIndex("C_ID"));
                }
                ContentValues values =  new ContentValues();
                values.put("PRD_ID",product_ID);
                values.put("Product_Name",product_name);
                values.put("Price",price);
                values.put("Weight",weight);
                values.put("Manufacturer",manufacturer);
                values.put("Category_ID",cat_ID);
                database.insert(TABLE_PRODUCT,null,values);
            }

            public void insertStore(String store_ID, String store_name){

                 ContentValues values = new ContentValues();
                 values.put("Str_ID",store_ID);
                 values.put("Store_Name",store_name);
                 database.insert(TABLE_STORE,null,values);
             }

            public void insertLocation(double lat, double lang, String city, String country){

                ContentValues values = new ContentValues();
                values.put("Latitude",lat);
                values.put("Longitude",lang);
                values.put("City",city);
                values.put("Country",country);

                database.insert(TABLE_LOCATION,null,values);

            }

            public void insertPSL(String prod_ID, String store_ID, double lat, double lan){

                ContentValues values = new ContentValues();
                values.put("Product_ID",prod_ID);
                values.put("STore_ID",store_ID);
                values.put("Latitude",lat);
                values.put("Longitude",lan);

                database.insert(TABLE_PRODUCT_STORE_LOCATION,null,values);

            }


        }
