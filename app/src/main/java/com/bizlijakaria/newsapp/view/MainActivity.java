package com.bizlijakaria.newsapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.icu.text.CaseMap;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bizlijakaria.newsapp.Adapter.Adapter;

import com.bizlijakaria.newsapp.Model.model;
import com.bizlijakaria.newsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    CarouselRecyclerview recyclerView;
    ArrayList<model> modelList;
    Adapter adapter;

    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=findViewById(R.id.carouselRecyclerview);
        firebaseFirestore=FirebaseFirestore.getInstance();

        firebaseFirestore.collection("img").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            modelList = new ArrayList<>();
                            for(QueryDocumentSnapshot snapshot:task.getResult())
                            {
                                String Image=snapshot.getString("i");
                                String Title=snapshot.getString("title");
                                String date=snapshot.getString("date");
                                model m=new model(Image,Title,date);
                                modelList.add(m);
                            }
                            adapter = new Adapter(modelList);
                            recyclerView.setAdapter(adapter);
                            recyclerView.set3DItem(true);
                            recyclerView.setAlpha(true);
                            recyclerView.setInfinite(true);



                        }
                    }
                });










    }
}
