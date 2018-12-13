package com.example.frank.proyectores;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.frank.proyectores.util.Data;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ClienteActivity extends AppCompatActivity {
    private Button btnguardar;
    private EditText editName,editEmail,editPhone,editCi;
    private final int CODE = 100;
    private final int CODE_PERMISSIONS = 101;
    private ImageView IMG;
    private Button btnphoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);
        btnguardar = findViewById(R.id.guardar);
        editName = findViewById(R.id.name);
        editEmail = findViewById(R.id.email);
        editPhone = findViewById(R.id.phone);
        editCi = findViewById(R.id.ci);

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

        if (editName.getText().toString().equals("") ||editEmail.getText().toString().equals("") ||editPhone.getText().toString().equals("") || editCi.getText().toString().equals("")){
            Toast.makeText(this, "Los campos no pueden estar vacios", Toast.LENGTH_SHORT).show();
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        //params.put("restaurant","5bf705dc9825331b589ab82a");//idRestaurant
        params.put("name", editName.getText());
        params.put("price", editCi.getText());
        params.put("property", editPhone.getText());
        params.put("description", editEmail.getText());
       // client.addHeader("authorization", Data.TOKEN);

        client.post(Data.URL_CLIENT,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    //String message = response.getString("message");
                    String id = response.getString("_id");

                    if (id != null) {
                        Toast.makeText(ClienteActivity.this, id, Toast.LENGTH_SHORT).show();

                        editName.getText().clear();
                        editCi.getText().clear();
                        editEmail.getText().clear();
                        editPhone.getText().clear();
                        //getData();
                        //otra activy...... do stuff
                        //finish();
                    } else {
                        Toast.makeText(ClienteActivity.this, "ERROR", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(ClienteActivity.this, responseString, Toast.LENGTH_LONG).show();
                Log.d("message",responseString);
            }

        });

    }
}


