package com.agenda.diario.Diario;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.agenda.diario.Database.DbAdapterDiario;
import com.agenda.diario.R;
import com.bumptech.glide.Glide;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by portatile on 15/01/2016.
 */
public class AdapterAudioview extends BaseAdapter {
    String data;
    String[] audio;
    int[] tag;
    Context mcontext;

    public AdapterAudioview(Context c, String data) {
        mcontext=c;

        this.data=data;
        getData();
    }

    @Override
    public int getCount() {
        return audio.length;

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
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View v = convertView;
        final ImageView audioView;
        TextView titolo;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_audio, null);

        }

        audioView= v.findViewById(R.id.audioplay);

        titolo= v.findViewById(R.id.titoloaudio);
        audioView.setImageResource(tag[position]);
        titolo.setText(audio[position]);

        return v;
    }


    public void getData(){

        DbAdapterDiario db = new DbAdapterDiario(mcontext);
        db.open();
        Cursor c =db.fetchAudioByData(data);
        if (c.getCount() != 0){
            audio= new String[c.getCount()];
            tag= new int [c.getCount()];
            if (c.moveToFirst()){
                for (int i=0;i<c.getCount();i++){
                    String file=c.getString(2).substring(c.getString(2).lastIndexOf('/')+1);
                    tag[i]=c.getInt(5);
                    audio[i]=file;

                    c.moveToNext();
                }
            }

        }else audio=new String[0];
        c.close();
        db.close();

    }

}
