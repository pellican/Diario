package com.agenda.diario.Diario.Impostazioni;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;

import com.agenda.diario.CustomView.CustomGridView;
import com.agenda.diario.Database.DbAdapterDiario;
import com.agenda.diario.Diario.Banner.Banner;
import com.agenda.diario.Diario.FotoActivity;
import com.agenda.diario.R;

/**
 * Created by portatile on 01/06/2016.
 */
public class Tab_sfondo extends Fragment implements ClickSfondo{
    String data;



    CustomGridView gridView;
    int[] idsfondo= {

            R.raw.s1,
            R.raw.s2,
            R.raw.s3,
            R.raw.s4,
            R.raw.s5,
            R.raw.s6,
            R.raw.s7,
            R.raw.s8,
            R.raw.s9

    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View sfo= inflater.inflate(R.layout.tab_sfondo, container, false);
        data=getArguments().getString("data");
        gridView= sfo.findViewById(R.id.gridviewsfo);
        AdapterSfondo adapter= new AdapterSfondo(getActivity(),idsfondo);
        gridView.setAdapter(adapter);
        adapter.setClickSfondo(this);



        return sfo;
    }

    @Override
    public void clicksfondo(final int posizione) {
        final Dialog dialog=new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sfondo);

        final RadioButton corr= dialog.findViewById(R.id.radiocorre);
        final RadioButton tutte= dialog.findViewById(R.id.radiotutte);
        Button ann= dialog.findViewById(R.id.ann_sfond);
        ann.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        Button ok= dialog.findViewById(R.id.ok_sfondo);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (corr.isChecked()|| tutte.isChecked()){

                    Intent intent = new Intent(getActivity(), Banner.class);
                    if (tutte.isChecked()){

                        intent.putExtra("tutti","tutti");
                    }
                    intent.putExtra("data",data);
                    intent.putExtra("sfondo",idsfondo[posizione]);
                    startActivity(intent);
                    dialog.dismiss();
                    getActivity().finish();
                }
            }
        });

        dialog.show();
    }
}
