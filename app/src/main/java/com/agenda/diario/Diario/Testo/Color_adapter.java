package com.agenda.diario.Diario.Testo;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.agenda.diario.R;


public class Color_adapter extends ArrayAdapter<String> {
   private String[] color;
   private Context context;
   private int resource;
    public Color_adapter(@NonNull Context context, int resource,String[] colore) {
        super(context, resource,colore);
        this.context=context;
        this.color=colore;
        this.resource=resource;
    }

    @Override
    public int getCount() {
        return color.length;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView=inflater.inflate(resource,parent,false);
            ImageView imcolore = convertView.findViewById(R.id.im_item_colore);
            imcolore.setBackgroundColor(Color.parseColor(color[position]));
        }
        return convertView;
    }
}
