package com.example.wallpaper_master;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telecom.Connection;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CategoryRVAdapter.CategoryClickInterface, ConnectionReceiver.ReceiverListener {

    ArrayList<CategoryRVModel> categoryRVModels;
    private RecyclerView categoryRV, wallpaperRv;
    private ProgressBar loadingPB;
    private ArrayList<String> wallpaperArrayList;
    private CategoryRVAdapter categoryRVAdapter;
    private WallpaperRVAdapter wallpaperRVAdapter;
    private EditText searchEdit;
    private ImageView searchIV;
    private TextView recent, noConnection;

    DBAdapter db;
    long result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.black));

        checkConnection();
        categoryRV = findViewById(R.id.idRVCategories);
        wallpaperRv = findViewById(R.id.idRVWallpapers);
        searchEdit = findViewById(R.id.idEdtSearch);
        searchIV = findViewById(R.id.idIVSearch);
        loadingPB = findViewById(R.id.idPBLoading);
        recent = findViewById(R.id.idTVRecent);

        wallpaperArrayList = new ArrayList<>();
        categoryRVModels = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this, RecyclerView.HORIZONTAL, false);
        wallpaperRVAdapter = new WallpaperRVAdapter(wallpaperArrayList, this);
        categoryRVAdapter = new CategoryRVAdapter(categoryRVModels, this, this);

        categoryRV.setLayoutManager(manager);
        categoryRV.setAdapter(categoryRVAdapter);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        wallpaperRv.setLayoutManager(layoutManager);
        wallpaperRv.setAdapter(wallpaperRVAdapter);

        db = new DBAdapter(this);
        db.openDB();

        getCategories();

        getWallpapers();

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchStr = searchEdit.getText().toString();
                if (searchStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter something to search", Toast.LENGTH_SHORT).show();
                } else {
                    getWallpapersByCategory(searchStr);
                }
            }
        });
    }

    private void getWallpapersByCategory(String category) {
        wallpaperArrayList.clear();
        loadingPB.setVisibility(View.VISIBLE);
        String url = "https://api.pexels.com/v1/search?query=" + category + "&per_page=80&page=1";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    loadingPB.setVisibility(View.GONE);
                    JSONArray photos = response.getJSONArray("photos");
                    for (int i = 0; i < photos.length(); i++) {
                        JSONObject photoObject = photos.getJSONObject(i);
                        String imgUrl = photoObject.getJSONObject("src").getString("portrait");

                        wallpaperArrayList.add(imgUrl);
                    }
                    wallpaperRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Fail to get data..", Toast.LENGTH_SHORT).show();
            }
        }) {
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();

                headers.put("Authorization", "563492ad6f917000010000017b9ee65d7af6494b92933d0defa18c1c");

                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    private void getWallpapers() {
        wallpaperArrayList.clear();
        loadingPB.setVisibility(View.VISIBLE);
        String url = "https://api.pexels.com/v1/curated?per_page=80&page=1";


        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                try {
                    JSONArray photos = response.getJSONArray("photos");
                    for (int i = 0; i < photos.length(); i++) {
                        JSONObject photoObj = photos.getJSONObject(i);
                        String imgUrl = photoObj.getJSONObject("src").getString("portrait");
                        db.delete(imgUrl);
                        save(imgUrl);
//                        db.update(imgUrl);
                        wallpaperArrayList.add(imgUrl);
                    }
                    wallpaperRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingPB.setVisibility(View.GONE);
                retrieveDataOffline();
                Toast.makeText(MainActivity.this, "Network error occurred", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                // in this method passing headers as
                // key along with value as API keys.
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "563492ad6f917000010000017b9ee65d7af6494b92933d0defa18c1c");
                // at last returning headers.
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    private void getCategories() {
        categoryRVModels.add(new CategoryRVModel("Tropical Plants", "https://images.pexels.com/photos/3722570/pexels-photo-3722570.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"));
        categoryRVModels.add(new CategoryRVModel("Flowers", "https://images.pexels.com/photos/1086178/pexels-photo-1086178.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"));
        categoryRVModels.add(new CategoryRVModel("Trees", "https://images.pexels.com/photos/1423600/pexels-photo-1423600.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"));
        categoryRVModels.add(new CategoryRVModel("Travel", "https://images.pexels.com/photos/672358/pexels-photo-672358.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"));
        categoryRVModels.add(new CategoryRVModel("Nature", "https://images.pexels.com/photos/2387873/pexels-photo-2387873.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"));
        categoryRVModels.add(new CategoryRVModel("Abstract", "https://images.pexels.com/photos/2110951/pexels-photo-2110951.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"));
        categoryRVModels.add(new CategoryRVModel("Music", "https://images.pexels.com/photos/4348093/pexels-photo-4348093.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"));
        categoryRVModels.add(new CategoryRVModel("Programming", "https://images.unsplash.com/photo-1542831371-29b0f74f9713?ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8cHJvZ3JhbW1pbmd8ZW58MHx8MHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"));
        categoryRVModels.add(new CategoryRVModel("Architecture", "https://images.pexels.com/photos/256150/pexels-photo-256150.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"));
        categoryRVModels.add(new CategoryRVModel("Arts", "https://images.pexels.com/photos/1194420/pexels-photo-1194420.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"));
        categoryRVModels.add(new CategoryRVModel("Cars", "https://images.pexels.com/photos/3802510/pexels-photo-3802510.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"));
        categoryRVModels.add(new CategoryRVModel("Technology", "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?ixid=MnwxMjA3fDB8MHxzZWFyY2h8MTJ8fHRlY2hub2xvZ3l8ZW58MHx8MHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"));
        categoryRVModels.add(new CategoryRVModel("Beach", "https://images.pexels.com/photos/853199/pexels-photo-853199.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"));
        categoryRVModels.add(new CategoryRVModel("Mountain", "https://images.pexels.com/photos/1261728/pexels-photo-1261728.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"));
        categoryRVModels.add(new CategoryRVModel("Field", "https://images.pexels.com/photos/35857/amazing-beautiful-breathtaking-clouds.jpg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"));
        categoryRVModels.add(new CategoryRVModel("Clouds", "https://images.pexels.com/photos/2088205/pexels-photo-2088205.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"));
        categoryRVModels.add(new CategoryRVModel("Condensation", "https://images.pexels.com/photos/891030/pexels-photo-891030.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"));
    }

    @Override
    public void onCategoryClick(int position) {
        String category = categoryRVModels.get(position).getCategory();
        recent.setText(category);
        getWallpapersByCategory(category);
    }

    private void checkConnection() {
        // initialize intent filter
        IntentFilter intentFilter = new IntentFilter();

        // add action
        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");

        // register receiver
        registerReceiver(new ConnectionReceiver(), intentFilter);

        // Initialize listener
        ConnectionReceiver.Listener = this;

        // Initialize connectivity manager
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Initialize network info
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        // get connection status
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        // display snack bar
//        showSnackBar(isConnected);
    }

//    private void showSnackBar(boolean isConnected) {
//
//        // initialize color and message
//        String message;
//        int color;
//
//
//        // check condition
//        if (isConnected) {
//
//            // when internet is connected
//            // set message
//            message = "Connected to Internet";
//
//            // set text color
//            color = Color.WHITE;
//
//        } else {
//
//            // when internet
//            // is disconnected
//            // set message
//            message = "Not Connected to Internet";
//
//            // set text color
//            color = Color.RED;
//        }
//
//        // initialize snack bar
//        Snackbar snackbar = Snackbar.make(findViewById(R.id.idRVWallpapers), message, Snackbar.LENGTH_LONG);
//
//        // initialize view
//        View view = snackbar.getView();
//
//        // Assign variable
//        TextView textView = view.findViewById(R.id.snackbar_text);
//
//        // set text color
//        textView.setTextColor(color);
//        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//
//        // show snack bar
//        snackbar.show();
//    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        // display snack bar
//        showSnackBar(isConnected);
    }

    private void save(String url) {
        DBAdapter db = new DBAdapter(this);
        db.openDB();
        result = db.add(url);
        if (result == 1) {
            Toast.makeText(this, "Wallpapers updated successfully", Toast.LENGTH_SHORT).show();
        } else {

        }
    }

    public void retrieveDataOffline() {

        DBAdapter db = new DBAdapter(this);
        db.openDB();
        Cursor c = db.getTvshow();
        while (c.moveToNext()) {
            String url = c.getString(1);
            wallpaperArrayList.add(url);
        }
        db.closeDB();
    }
}