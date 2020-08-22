package com.agenda.diario.Diario.Impostazioni;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.agenda.diario.R;

import java.util.ArrayList;

/**
 * Created by portatile on 03/06/2016.
 */
public class AdapterEmoticon extends BaseAdapter{
    ArrayList<GridObject> intemo;
    Context mcontext;
    public AdapterEmoticon(Context context,ArrayList<GridObject> intemo) {
        mcontext=context;
        this.intemo=intemo;
    }

    @Override
    public int getCount() {
        return intemo.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView emoticon;
        GridObject object= intemo.get(position);

        if (view == null) {
            LayoutInflater vi = (LayoutInflater)mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_emoticon, null);
        }
        emoticon= v.findViewById(R.id.imageemoti);
        emoticon.setImageResource(object.getEmot());

        if (object.getState() == 1) {
           v.setBackgroundColor(Color.GREEN);
        } else {
           v.setBackgroundColor(Color.TRANSPARENT);
        }
        return v;
    }
}
