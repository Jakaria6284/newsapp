package com.bizlijakaria.newsapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bizlijakaria.newsapp.Model.model;
import com.bizlijakaria.newsapp.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    List<model>modelList;

    public Adapter(List<model> modelList) {
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.newsitem,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
       String Image=modelList.get(position).getImage();
       String Title=modelList.get(position).getTitle();
       String publish=modelList.get(position).getPublishat();
       String url=modelList.get(position).getUrl();

       holder.setData(Image,Title,publish);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title,publishat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            title=itemView.findViewById(R.id.title);
            publishat=itemView.findViewById(R.id.publishat);
        }
        public void setData(String img,String tit,String publish)
        {
            Glide.with(imageView.getContext()).load(img).into(imageView);
            title.setText(tit);
            publishat.setText(publish);
        }
    }
}
