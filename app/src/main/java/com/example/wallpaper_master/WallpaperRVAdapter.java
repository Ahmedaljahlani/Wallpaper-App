package com.example.wallpaper_master;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class WallpaperRVAdapter extends RecyclerView.Adapter<WallpaperRVAdapter.viewHolder> {
    private final ArrayList<String> wallpaperList;
    private final Context context;

    public WallpaperRVAdapter(ArrayList<String> wallpaperList, Context context) {
        this.wallpaperList = wallpaperList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.wallpaper_rv_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Glide.with(context).load(wallpaperList.get(position)).placeholder(R.color.black_shade_1).into(holder.wallpaper);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WallpaperActivity.class);
                intent.putExtra("imgUrl", wallpaperList.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wallpaperList.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private final CardView imgCV;
        private final ImageView wallpaper;

        public viewHolder(View itemView) {
            super(itemView);
            imgCV = itemView.findViewById(R.id.idCVWallpaper);
            wallpaper = itemView.findViewById(R.id.idIVWallpaper);

        }
    }
}
