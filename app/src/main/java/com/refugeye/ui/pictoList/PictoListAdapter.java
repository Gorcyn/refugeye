package com.refugeye.ui.pictoList;

import java.util.List;

import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.refugeye.R;
import com.refugeye.data.model.Picto;

public class PictoListAdapter extends RecyclerView.Adapter<PictoListAdapter.ViewHolder> {

    private List<Picto> pictoList;
    private int selectedPosition = -1;

    public void setPictoList(List<Picto> pictoList) {
        this.pictoList = pictoList;
    }

    @Nullable
    public Picto getSelectedItem() {
        if (selectedPosition < 0 || selectedPosition > getItemCount()) {
            return null;
        }
        return pictoList.get(selectedPosition);
    }

    @Override
    public int getItemCount() {
        return pictoList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_picto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull ViewHolder holder, int position) {

        Picto picto = pictoList.get(position);

        Drawable icon = holder.itemView.getResources().getDrawable(picto.getResId());

        Glide.with(holder.itemView.getContext())
                .load(icon)
                .into(holder.image);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PictoListAdapter.this.selectedPosition = holder.getAdapterPosition();
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                return true;
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView image;

        ViewHolder(View view) {
            super(view);
            this.image = view.findViewById(R.id.picto_image);
        }
    }
}
