package com.bizlijakaria.newsapp.view;

import static android.content.ContentValues.TAG;
import static com.google.android.material.color.MaterialColors.isColorLight;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.CaseMap;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
    LottieAnimationView lottieAnimationView;
    TextView Nama;
    Adapter adapter;
    String Nammme;
   public SharedPreferences sharedPreferences;

    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=findViewById(R.id.carouselRecyclerview);
        firebaseFirestore=FirebaseFirestore.getInstance();
        Nama=findViewById(R.id.Nameee);
        lottieAnimationView=findViewById(R.id.addday);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // Set status bar color to white
            window.setStatusBarColor(getResources().getColor(R.color.white));

            // If the status bar color is light, set system UI to dark theme
            if (isColorLight(getResources().getColor(R.color.white))) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                // If the status bar color is dark, set system UI to light theme
                getWindow().getDecorView().setSystemUiVisibility(0);
            }
        }

        sharedPreferences=getSharedPreferences("name",MODE_PRIVATE);
        boolean isNameSet = sharedPreferences.getBoolean("isNameSet", false);
        if(!isNameSet)
        {
            showDialog();

        }
        Nammme=sharedPreferences.getString("Name","");
        Nama.setText(Nammme);

        lottieAnimationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AddDayActivity.class));


            }
        });




        firebaseFirestore.collection("img").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (querySnapshot != null) {
                    modelList = new ArrayList<>();
                    for (QueryDocumentSnapshot snapshot : querySnapshot) {
                        String Image = snapshot.getString("i");
                        String Title = snapshot.getString("title");
                        String date = snapshot.getString("date");
                        model m = new model(Image, Title, date);
                        modelList.add(m);
                    }
                    adapter = new Adapter(modelList);
                    recyclerView.setAdapter(adapter);
                    recyclerView.set3DItem(true);
                    recyclerView.setAlpha(true);
                    recyclerView.setInfinite(true);
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });











    }

    public void showDialog() {
        Dialog dialog=new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialogitem);
        dialog.setCancelable(true);
        EditText editText=dialog.findViewById(R.id.name);
        Button button=dialog.findViewById(R.id.save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=editText.getText().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isNameSet", true);
                editor.putString("Name",name);
                editor.apply();
                Toast.makeText(MainActivity.this, "Name add sucess fully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });
        dialog.show();
    }


}
