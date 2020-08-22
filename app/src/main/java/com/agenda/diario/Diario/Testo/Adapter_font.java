package com.agenda.diario.Diario.Testo;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.agenda.diario.R;



public class Adapter_font extends ArrayAdapter<Typeface> {
    private Typeface[] typeface;
    private LayoutInflater mflater;

    private int mResource;

    Adapter_font(Context context, int resource, Typeface[] font) {
        super(context, resource);

        mflater = LayoutInflater.from(context);
        mResource = resource;
        typeface=font;
    }

    @Override
    public int getCount() {
        return typeface.length;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View view, ViewGroup parent){
        if (view == null ){
            view = mflater.inflate(mResource, parent, false);
        }
        TextView textView = view.findViewById(R.id.tex_item_font);
        textView.setTypeface(typeface[position]);

        return view;
    }

}
