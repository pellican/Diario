package com.agenda.diario.Agenda;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.agenda.diario.R;
import com.agenda.diario.Singleton;

import java.util.Calendar;

/**
 * Created by portatile on 30/12/2015.
 */
public class Agenda_Framm extends Fragment {
    Calendar calendar;
    AdapterAgenda adapter;
    TextView dataT;
    String dataS;

    public static final Agenda_Framm newInstance(int mese){
        Agenda_Framm c= new Agenda_Framm();

        return c;
    }
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View diaView = inflater.inflate(R.layout.agenda_framm, container, false);
        calendar=Calendar.getInstance();
        dataT= diaView.findViewById(R.id.textVagenda);
        final ListView cont= diaView.findViewById(R.id.list_agenda);
        adapter =new AdapterAgenda(getActivity(),calendar);
        cont.setAdapter(adapter);
        cont.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (i>0 && adapter.oredb[i] != null){
                Intent intent = new Intent(getActivity(),Dialog_agenda.class);
                //  calendar.set(Calendar.HOUR_OF_DAY, i);
                //  calendar.set(Calendar.MINUTE, 0);
                //  Singleton.getInstance().setData(calendar);
                String ora;
                if (i < 10) {
                    ora = "0" + i + ":00";
                } else ora = i + ":00";

                intent.putExtra("data",dataS);
                intent.putExtra("ora",i-1);
                getActivity().startActivity(intent);
            }


            }
        });

        return diaView;
    }


    public void updateUI(Calendar day) {
        View v = getView();
        if (v == null)
            return;

        calendar.set(day.get(Calendar.YEAR),day.get(Calendar.MONTH),day.get(Calendar.DAY_OF_MONTH));
        dataS= (String) DateFormat.format("dd MMMM yyyy", day);
        dataT.setText(dataS);
        adapter.Rigne(dataS);

        adapter.notifyDataSetChanged();
    }


}
