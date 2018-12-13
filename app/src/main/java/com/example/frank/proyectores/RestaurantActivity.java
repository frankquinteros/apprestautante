package com.example.frank.proyectores;


import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frank.proyectores.utils.BitmapStruct;
import com.example.frank.proyectores.utils.Data;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class RestaurantActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView map;
    private GoogleMap mMap;
    private Geocoder geocoder;
    private TextView street;
    private Button next;
    private LatLng mainposition;
    //camara
    private final int CODE = 100;
    private final int CODE_PERMISSIONS = 101;
    private ImageView IMG;
   // private Button btnphoto;
    private ImageButton btn;
    private ImageButton SEND;
    private BitmapStruct DATAIMAGE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurante);
        map = findViewById(R.id.mapView);
        map.onCreate(savedInstanceState);
        map.onResume();
        MapsInitializer.initialize(this);
        map.getMapAsync(this);
        geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        street = findViewById(R.id.street);
        //boton siguiente para la foto
        btn = findViewById(R.id.camera);

        SEND = findViewById(R.id.insertar);
        IMG = findViewById(R.id.image);

        btn.setVisibility(View.INVISIBLE);
        if (reviewPermissions()) {
            btn.setVisibility(View.VISIBLE);

        }
        SEND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DATAIMAGE != null) {
                    AsyncHttpClient client = new AsyncHttpClient();
                    File img = new File(DATAIMAGE.path);
                    client.addHeader("authorization", Data.TOKEN);
                    RequestParams params = new RequestParams();
                    try {
                        params.put("img", img);

                        client.post(Data.UPLOAD_RESTORANT, params, new JsonHttpResponseHandler(){
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Toast.makeText(RestaurantActivity.this, "EXITO", Toast.LENGTH_LONG).show();
                                //AsyncHttpClient.log.w(LOG_TAG, "onSuccess(int, Header[], JSONObject) was not overriden, but callback was received");
                            }
                        });

                    } catch(FileNotFoundException e) {}
                }
            }
        });


        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendData();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                RestaurantActivity.this.startActivityForResult(camera,CODE);

            }
        });

    }
    private boolean reviewPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        requestPermissions(new String [] {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_PERMISSIONS);
        return false;
    }
    private BitmapStruct saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String path = directory.getAbsolutePath() + "/profile.jpg";
        BitmapStruct p = new BitmapStruct();
        p.img = BitmapFactory.decodeFile(path);
        p.path = path;
        return p;
        //return directory.getAbsolutePath();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (CODE_PERMISSIONS == requestCode) {
            if (permissions.length == 3) {
                btn.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE) {
            Bitmap img = (Bitmap)data.getExtras().get("data");
            DATAIMAGE = saveToInternalStorage(img);
            IMG.setImageBitmap(DATAIMAGE.img);

        }
    }

    public void sendData (){
        TextView name = findViewById(R.id.name);
        TextView nit = findViewById(R.id.nit);
        TextView street = findViewById(R.id.street);
        TextView property = findViewById(R.id.property);
        TextView phone = findViewById(R.id.phone);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", Data.TOKEN);

        RequestParams params = new RequestParams();
        params.add("name", name.getText().toString());
        params.add("nit", nit.getText().toString());
        params.add("street", street.getText().toString());
        params.add("property", property.getText().toString());
        params.add("phone",phone.getText().toString());
        params.add("Lat", String.valueOf(mainposition.latitude));
        params.add("Log", String.valueOf(mainposition.longitude));

        client.post(Data.REGISTER_RESTORANT, params, new JsonHttpResponseHandler(){
        //client.post(Data.REGISTER_RESTORANTE, params,(JsonHttpResponseHandler)onSucess(status){
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                AlertDialog alertDialog = new AlertDialog.Builder(RestaurantActivity.this).create();
                try {
                    String msn = response.getString("msn");
                    alertDialog.setTitle("RESPONSE SERVER");
                    alertDialog.setMessage(msn);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng potosi = new LatLng(-19.5783329, -65.7563853);
        mainposition = potosi;

        mMap.addMarker(new MarkerOptions().position(potosi).title("Lugar").zIndex(17).draggable(true));
        mMap.setMinZoomPreference(16);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(potosi));
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener(){
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                mainposition = marker.getPosition();
                String street_string = getstreet(marker.getPosition().latitude, marker.getPosition().longitude);
                street.setText(street_string);
            }
        });
    }
    public String getstreet (double lat, double lon){
        List<Address> addresses;
        String result = "";
        try {
           addresses =  geocoder.getFromLocation(lat, lon, 1);
            result += addresses.get(0).getThoroughfare();

        } catch (IOException e){
            e.printStackTrace();
        }

        return result;
    }

}

