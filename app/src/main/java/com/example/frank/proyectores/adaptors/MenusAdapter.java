package com.example.frank.proyectores.adaptors;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.frank.proyectores.MenuActivity;
import com.example.frank.proyectores.R;
import com.example.frank.proyectores.items.MenuResItem;
import com.example.frank.proyectores.util.Data;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class MenusAdapter extends RecyclerView.Adapter<MenusAdapter.MenuViewHolder> {


    private Context context;
    private ArrayList<MenuResItem> listData;



    public MenusAdapter(Context context, ArrayList<MenuResItem> listData) {
        this.context = context;
        this.listData = listData;

    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu,parent,false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, final int position) {
        holder.setData(listData.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,listData.get(position),Toast.LENGTH_SHORT).show();
                //context.startActivity(new Intent(context,Main2Activity.class));
            }
        });


    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nombre,precio,description;
        ImageView foto;
        Button btnBorrar;
        String id,idRestaurant;
        private ConstraintLayout parentLayout;

        public MenuViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.textNombre);
            precio = itemView.findViewById(R.id.textPrecio);
            foto = itemView.findViewById(R.id.imageFoto);
            btnBorrar = itemView.findViewById(R.id.btnBorrar);
            btnBorrar.setOnClickListener(this);
            //nombre = itemView.findViewById(R.id.textNombre);

            parentLayout = itemView.findViewById(R.id.parent_layout);

        }

        public void setData(MenuResItem item) {
            nombre.setText(item.getName());
            precio.setText(item.getPrice().toString());
            //property.setText(item.getProperty().toString());
            //Glide.with(context).load(Data.HOST + item.getFoto()).into(foto);
            id = item.getId();

        }

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            // Add the buttons
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    deleteMenu();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            builder.setTitle("Esta seguro de eliminar el menu");

            AlertDialog dialog = builder.create();
            dialog.show();




            //Toast.makeText(context,id,Toast.LENGTH_LONG).show();
        }

        private void deleteMenu() {
            AsyncHttpClient client = new AsyncHttpClient();

            client.delete(Data.URL_MENUS + id,new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Toast.makeText(context, "Boorrad", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, MenuActivity.class);
                    context.startActivity(intent);
                }

            });
        }
    }

}
