package com.agenda.diario.Diario.Impostazioni;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.agenda.diario.Database.DbAdapterDiario;
import com.agenda.diario.Diario.Banner.Banner;
import com.agenda.diario.R;

/**
 * Created by portatile on 01/06/2016.
 */
public class Tab_tag extends Fragment {
    int pos1;
    String data;
    RadioGroup radioGroup;
    RadioButton radioButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View tag= inflater.inflate(R.layout.tab_tag, container, false);
        radioGroup= tag.findViewById(R.id.radiogrup);
        data=getArguments().getString("data");
        DbAdapterDiario db= new DbAdapterDiario(getActivity());
        db.open();
        Cursor c=db.fetchPaginaByData(data);
        if (c.getCount()!=0){
            if (c.moveToFirst()) {
                ((RadioButton)radioGroup.getChildAt(c.getInt(6))).setChecked(true);

            }
        }
        c.close();db.close();


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                pos1=radioGroup.indexOfChild(tag.findViewById(i));
                Dialog(pos1);
            }
        });

        return tag;
    }
    public void Dialog(final int posizione){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage(R.string.applica_cat);
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
                DbAdapterDiario db = new DbAdapterDiario(getActivity());
                db.open();
                Cursor c=db.fetchPaginaByData(data);
                if (c.getCount()!=0){
                    if (c.moveToFirst()) {
                       db.updateContactPagina(c.getLong(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4),c.getInt(5),posizione);

                    }
                }
                c.close();db.close();
                */
                Intent intent = new Intent(getActivity(), Banner.class);
                intent.putExtra("data",data);
                intent.putExtra("cat",posizione);
                startActivity(intent);
                dialogInterface.dismiss();
                getActivity().finish();
            }
        });
        dialog.show();
    }
}
