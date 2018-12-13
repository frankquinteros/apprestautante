package com.example.frank.proyectores;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.frank.proyectores.util.Data;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RestauranteActivity extends AppCompatActivity {

    private ImageButton btnguardar;
    private EditText editName,editNit,editProperty,editStreet,editPhone;
    private final int CODE = 100;
    private final int CODE_PERMISSIONS = 101;
    private ImageView IMG;
    private ImageButton btnphoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurante);
        btnguardar = findViewById(R.id.guardar);
        btnphoto = findViewById(R.id.sacar_foto);
        editName = findViewById(R.id.name);
        editNit = findViewById(R.id.nit);
        editPhone = findViewById(R.id.phone);
        editProperty = findViewById(R.id.property);
        editStreet = findViewById(R.id.street);
        IMG = findViewById(R.id.foto);

        if (reviewPermissions()){
            btnphoto.setVisibility(View.VISIBLE);
        }

        btnphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                RestauranteActivity.this.startActivityForResult(camera,CODE);

            }
        });


        if (reviewPermissions()){
            btnguardar.setVisibility(View.VISIBLE);
        }
        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
            }
        });


    }

    private boolean reviewPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return  true;
        }
        if (this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_PERMISSIONS);
        return  false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (CODE_PERMISSIONS == requestCode){
            if (permissions.length == 3){
                btnphoto.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== CODE){
            Bitmap img = (Bitmap)data.getExtras().get("data");
            IMG.setImageBitmap(img);
        }


    }


    private void sendData() {

        if (editName.getText().toString().equals("") ||editNit.getText().toString().equals("") ||editProperty.getText().toString().equals("") ||editStreet.getText().toString().equals("") || editPhone.getText().toString().equals("")){
            Toast.makeText(this, "Los campos no pueden estar vacios", Toast.LENGTH_SHORT).show();
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        //params.put("restaurant","5bf705dc9825331b589ab82a");//idRestaurant
        params.put("name", editName.getText());
        params.put("price", editNit.getText());
        params.put("property", editPhone.getText());
        params.put("description", editProperty.getText());
        params.put("description", editStreet.getText());
        // client.addHeader("authorization", Data.TOKEN);

        client.post(Data.URL_RESTAURANTE,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    //String message = response.getString("message");
                    String id = response.getString("_id");

                    if (id != null) {
                        Toast.makeText(RestauranteActivity.this, id, Toast.LENGTH_SHORT).show();

                        editName.getText().clear();
                        editNit.getText().clear();
                        editProperty.getText().clear();
                        editPhone.getText().clear();
                        editStreet.getText().clear();
                        //getData();
                        //otra activy...... do stuff
                        //finish();
                    } else {
                        Toast.makeText(RestauranteActivity.this, "ERROR", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(RestauranteActivity.this, responseString, Toast.LENGTH_LONG).show();
                Log.d("message",responseString);
            }

        });

    }


}
