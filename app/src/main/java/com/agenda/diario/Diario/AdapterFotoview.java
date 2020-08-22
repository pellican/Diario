package com.agenda.diario.Diario;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.agenda.diario.Database.DbAdapterDiario;
import com.agenda.diario.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;


/**
 * Created by portatile on 15/01/2016.
 */
public class AdapterFotoview extends BaseAdapter {
    String data;
    String[] foto;
    String[] fotoDrive;

    Context mcontext;
    public AdapterFotoview(Context c, String data) {
        mcontext=c;

        this.data=data;
        getData();
    }

    @Override
    public int getCount() {
        return foto.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

            final ImageView fotoView;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.item_image, null);

            }
            fotoView = v.findViewById(R.id.imageVitem);
            Glide.with(mcontext)
                    .load(foto[position])
                    .into(fotoView);



        return v;
    }

    public void getData(){
        DbAdapterDiario db = new DbAdapterDiario(mcontext);
        db.open();
        Cursor c =db.fetchFotoByData(data);
        if (c.getCount() != 0){
            foto= new String[c.getCount()];
            fotoDrive = new String[c.getCount()];
            if (c.moveToFirst()){
                for (int i=0;i<c.getCount();i++){
                    foto[i]=c.getString(2);
                    fotoDrive[i]=c.getString(4);
                    c.moveToNext();
                }
            }

        }else{
            foto=new String[0];
            fotoDrive= new String[0];
        }
        c.close();
        db.close();
    }

}
