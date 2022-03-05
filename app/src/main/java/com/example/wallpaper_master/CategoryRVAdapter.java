package com.example.wallpaper_master;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CategoryRVAdapter extends RecyclerView.Adapter<CategoryRVAdapter.viewHolder> {
    private final ArrayList<CategoryRVModel> categoryRVModels;
    private final Context context;
    private final CategoryClickInterface categoryClickInterface;

    public CategoryRVAdapter(ArrayList<CategoryRVModel> categoryRVModels, Context context, CategoryClickInterface categoryClickInterface) {
        this.categoryRVModels = categoryRVModels;
        this.context = context;
        this.categoryClickInterface = categoryClickInterface;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_rv_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        CategoryRVModel model = categoryRVModels.get(position);
        holder.categoryTV.setText(model.getCategory());
//        holder.recent.setText(model.getCategory());
        if (!model.getImgUrl().isEmpty()) {
            Glide.with(context).load(model.getImgUrl()).placeholder(R.color.black_shade_1).into(holder.categoryIV);
        } else {
            holder.categoryIV.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryClickInterface.onCategoryClick(position);
//                holder.recent.setText(model.getCategory());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryRVModels.size();
    }

    public interface CategoryClickInterface {
        void onCategoryClick(int position);
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryTV;
        private final TextView recent;
        private final ImageView categoryIV;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            categoryIV = itemView.findViewById(R.id.idIVCategory);
            categoryTV = itemView.findViewById(R.id.idTVCategory);
            recent = itemView.findViewById(R.id.idTVRecent);
        }
    }
}
