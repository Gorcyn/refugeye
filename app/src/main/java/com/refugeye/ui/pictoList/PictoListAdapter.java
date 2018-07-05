package com.refugeye.ui.pictoList;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.refugeye.R;
import com.refugeye.data.model.Picto;

public class PictoListAdapter extends ArrayAdapter<Picto> {

    private final LayoutInflater inflater;
    private int selectedPosition = -1;

    @Nullable
    public Picto getSelectedItem() {
        if (selectedPosition < 0 || selectedPosition > getCount()) {
            return null;
        }
        return getItem(selectedPosition);
    }

    public PictoListAdapter(Context context) {
        super(context, 0);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.r_picto, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();

        Picto picto = getItem(position);
        if (picto != null) {

            holder.image.setImageResource(picto.getResId());
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    PictoListAdapter.this.selectedPosition = position;
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    return true;
                }
            });
        }

        return convertView;
    }

    private class ViewHolder {
        public final ImageView image;

        ViewHolder(View view) {
            image = view.findViewById(R.id.picto_image);
        }
    }
}
