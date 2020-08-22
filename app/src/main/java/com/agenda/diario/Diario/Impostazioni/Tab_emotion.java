package com.agenda.diario.Diario.Impostazioni;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.agenda.diario.Database.DbAdapterDiario;
import com.agenda.diario.Diario.Banner.Banner;
import com.agenda.diario.R;

import java.util.ArrayList;

/**
 * Created by portatile on 01/06/2016.
 */
public class Tab_emotion extends Fragment {
    String data;
    int pre=-1;
    ArrayList<GridObject> myObjects;
    int[] intemo={

            R.raw.e1,
            R.raw.e2,
            R.raw.e3,
            R.raw.e4,
            R.raw.e5,
            R.raw.e6,
            R.raw.e7,
            R.raw.e8,
            R.raw.e9,
            R.raw.e10,
            R.raw.e11,
            R.raw.e12,
            R.raw.e13,
            R.raw.e14,
            R.raw.e15,
            R.raw.e16,
            R.raw.e17,
            R.raw.e18,
            R.raw.e19,
            R.raw.e20,
            R.raw.e21,
            R.raw.e22,
            R.drawable.ic_delete

   };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View emo= inflater.inflate(R.layout.tab_emotion, container, false);
        data=getArguments().getString("data");
        myObjects = new ArrayList<>();
        
        for (int s :intemo) {
            myObjects.add(new GridObject(s, 0));
        }
        GridView gridView= emo.findViewById(R.id.gridemotion);
        final AdapterEmoticon adapter=new AdapterEmoticon(getActivity(),myObjects);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {

                if (pre != -1){
                    myObjects.get(pre).setState(0);
                }

                myObjects.get(position).setState(1);
                adapter.notifyDataSetChanged();
                pre=position;
                Applica(position);
            }
        });


        return emo;
    }

    public void Applica(final int posizione){
        final AlertDialog.Builder dialog =new AlertDialog.Builder(getActivity());
        if (posizione== 22){
            dialog.setMessage(R.string.elim_emot);
        } else {  dialog.setMessage(R.string.aggi_emot);}
       // dialog.setCancelable(true)
        dialog.setNegativeButton(R.string.annulla, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                /*
                DbAdapterDiario db=new DbAdapterDiario(getActivity());
                db.open();
                Cursor c=db.fetchPaginaByData(data);
                if (c.getCount()!=0){
                    if (c.moveToFirst()) {
                        if (posizione == 22){
                            db.updateContactPagina(c.getLong(0),c.getString(1),c.getString(2),c.getString(3),c.getInt(4),0,c.getInt(6));
                        }else {
                            db.updateContactPagina(c.getLong(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4), intemo[posizione],c.getInt(6));
                        }
                    }
                }
                c.close();db.close();
                */

                if (posizione == 22){

                DbAdapterDiario db=new DbAdapterDiario(getActivity());
                db.open();
                Cursor c=db.fetchPaginaByData(data);
                if (c.getCount()!=0){
                    if (c.moveToFirst()) {
                            db.updateContactPagina(c.getLong(0),c.getString(1),c.getString(2),c.getString(3),c.getInt(4),0,c.getString(6),c.getString(7));
                    }
                }
                c.close();db.close();

                }else {
                    Intent intent = new Intent(getActivity(), Banner.class);
                    intent.putExtra("data",data);
                    intent.putExtra("emoti",intemo[posizione]);
                    startActivity(intent);
                }


                dialogInterface.dismiss();
                getActivity().finish();
            }
        });
        dialog.show();
    }

}
