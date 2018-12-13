package com.example.frank.proyectores;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.frank.proyectores.utils.Data;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MenuActivity extends AppCompatActivity {
    private final int CODE = 100;
    private final int CODE_PERMISSIONS = 101;
    private ImageView IMG;
    private Button btnphoto;
    private Button btnguardar;
    private EditText editName,editPrice,editProperty,editDescription;
    RecyclerView recyclerMenu;
    ArrayList<MenuResItem> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        btnphoto = findViewById(R.id.sacar_foto);
        IMG = findViewById(R.id.foto);
        btnphoto.setVisibility(View.INVISIBLE);
        btnguardar = findViewById(R.id.guardar);
        editName = findViewById(R.id.name);
        editPrice = findViewById(R.id.price);
        editProperty = findViewById(R.id.property);
        editDescription = findViewById(R.id.description);
        recyclerMenu = findViewById(R.id.recyclerMenu);
        if (reviewPermissions()){
            btnphoto.setVisibility(View.VISIBLE);
        }

        btnphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                MenuActivity.this.startActivityForResult(camera,CODE);
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


        getData();
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
    public void getData() {
        //cargar datos de la bd
        listData.clear();
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Data.URL_MENUS,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray data = response.getJSONArray("result");

                    for (int i = 0; i<data.length();i++ ){
                        JSONObject item = data.getJSONObject(i);
                        Double price = item.getDouble("price");
                        String name = item.getString("name");
                        String description = item.getString("description");
                        String id = item.getString("_id");
                        String property = item.getString("property");
                        String foto = "";


                        //Log.i("IMG",item.getString("foto"));


                        MenuResItem menu = new MenuResItem(property,name, description,foto,id,price);
                        listData.add(menu);
                    }

                    loadData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        private void loadData() {

            recyclerMenu.setLayoutManager(
                    new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true));
            MenusAdapter adapter = new MenusAdapter(this, listData);
            recyclerMenu.setAdapter(adapter);

        }
        private void sendData() {

            if (editPrice.getText().toString().equals("") || editName.getText().toString().equals("")){
                Toast.makeText(this, "Los campos no pueden estar vacios", Toast.LENGTH_SHORT).show();
                return;
            }
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();

            //params.put("restaurant","5bf705dc9825331b589ab82a");//idRestaurant
            params.put("name", editName.getText());
            params.put("price", editPrice.getText());
            params.put("property", editProperty.getText());
            params.put("description", editDescription.getText());
            client.addHeader("authorization",Data.TOKEN);

            client.post(Data.URL_MENUS,params,new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        //String message = response.getString("message");
                        String id = response.getString("_id");

                        if (id != null) {
                            Toast.makeText(MenuActivity.this, id, Toast.LENGTH_SHORT).show();

                            editName.getText().clear();
                            editPrice.getText().clear();
                            //getData();
                            //otra activy...... do stuff
                            //finish();
                            getData();
                        } else {
                            Toast.makeText(MenuActivity.this, "ERROR", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(MenuActivity.this, responseString, Toast.LENGTH_LONG).show();
                    Log.d("message",responseString);
                }

            });



        }
