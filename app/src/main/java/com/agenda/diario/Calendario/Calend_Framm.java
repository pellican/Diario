package com.agenda.diario.Calendario;





import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.agenda.diario.Diario.Frammento_Diario;
import com.agenda.diario.R;
import com.agenda.diario.Singleton;

import java.util.Calendar;


public class Calend_Framm extends Fragment {
  private   Calendar calend;
    CalendarioAdapter adapter;
    TextView dataT;
    String dataS;
    public static final Calend_Framm newInstance(int mese){
        Calend_Framm c= new Calend_Framm();

        return c;
    }
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View calView = inflater.inflate(R.layout.griglia_calendario, container, false);
        calend = Calendar.getInstance();
        dataT= calView.findViewById(R.id.data);
        GridView gridview = calView.findViewById(R.id.gridview);
        adapter = new CalendarioAdapter(getActivity(), calend);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView gioT = view.findViewById(R.id.date);
                ImageView diar = view.findViewById(R.id.imdiar);
                int day = Integer.parseInt(gioT.getText().toString());
                FragmentManager di = getChildFragmentManager();
                FragmentManager diari=getActivity().getSupportFragmentManager();
                //  view.setBackgroundResource(R.drawable.selezione);
                /*
                if (diar.getVisibility() == View.VISIBLE ) {
                    Frammento_Diario diario = new Frammento_Diario();
                    diari.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.frameContainer, diario).commit();
                }else {
                */
                    Dialog_Calendario dialog = new Dialog_Calendario();
                    dialog.show(di, "data");
              //  }
                calend.set(Calendar.DAY_OF_MONTH, day);
                Singleton.getInstance().setData(calend);

            }
        });
        return  calView;
    }



    public void updateUI(Calendar monthYear) {

        View v = getView();
        if (v == null)
            return;


        calend.set(monthYear.get(Calendar.YEAR),monthYear.get(Calendar.MONTH),1);
        dataS= (String)DateFormat.format("MMMM yyyy", monthYear);
        dataT.setText(dataS);
        adapter.refreshDays();
        adapter.icone(dataS,monthYear);
        adapter.notifyDataSetChanged();
    }
}
