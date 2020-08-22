package com.agenda.diario.Calendario;



import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agenda.diario.Agenda.Frammento_Agenda;
import com.agenda.diario.Diario.Frammento_Diario;
import com.agenda.diario.R;
import com.agenda.diario.Singleton;


/**
 * Created by portatile on 31/12/2015.
 */
public class Dialog_Calendario extends DialogFragment {
    TextView diarT,agenT,annT,titoloT;
    int mNum;
    FragmentManager fragmentManager;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(Dialog_Calendario.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_calendario, container);

        diarT= view.findViewById(R.id.textVdiar);
        agenT= view.findViewById(R.id.textVage);
        annT= view.findViewById(R.id.textVann);

        diarT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager = getActivity().getSupportFragmentManager();
                Fragment diario = new Frammento_Diario();
               fragmentManager.beginTransaction().replace(R.id.frameContainer, diario,"diario").commit();
            }
        });
        agenT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager = getActivity().getSupportFragmentManager();
                Fragment agenda = new Frammento_Agenda();
                fragmentManager.beginTransaction().replace(R.id.frameContainer, agenda).commit();
            }
        });
        annT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Singleton.getInstance().setData(null);
    }
}
