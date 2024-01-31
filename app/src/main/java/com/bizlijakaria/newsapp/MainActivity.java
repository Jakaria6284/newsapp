package com.bizlijakaria.newsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bizlijakaria.newsapp.Adapter.Adapter;
import com.bizlijakaria.newsapp.Model.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<model>modelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=findViewById(R.id.recyclerview);
        modelList=new ArrayList<>();

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        String apiLink="https://newsapi.org/v2/everything?q=tesla&from=2023-12-31&sortBy=publishedAt&apiKey=474e8af6f7c4481ba2ed02d09a667807";

        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiLink,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("API Response", response);
                        try {
                            // Parse JSON response
                           // JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("articles");

                           // JSONArray listArray = jsonObject.getJSONArray("list");
                           for(int i=0;i<jsonArray.length();i++)
                           {
                               JSONObject object=jsonArray.getJSONObject(i);
                               model md=new model(
                                object.getString("title"),
                                       object.getString("url"),
                                       object.getString("urlToImage"),
                                       object.getString("publishedAt")
                               );
                               modelList.add(md);
                               Adapter adapter=new Adapter(modelList);
                               recyclerView.setAdapter(adapter);
                               adapter.notifyDataSetChanged();
                           }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("API Error", "Error parsing JSON response");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Log.e("API Error", "TimeoutError: Unable to connect to the server");
                } else {
                    Log.e("API Error", "VolleyError: " + error.toString());
                }
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue
        queue.add(stringRequest);
    }
}