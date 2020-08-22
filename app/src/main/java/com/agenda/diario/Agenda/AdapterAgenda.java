package com.agenda.diario.Agenda;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.agenda.diario.Database.DbAdapterDiario;
import com.agenda.diario.R;

import java.util.Calendar;

/**
 * Created by portatile on 31/12/2015.
 */
public class AdapterAgenda extends BaseAdapter {

    static final int FIRST_DAY_OF_WEEK =1; // Sunday = 0, Monday = 1

    // references to our items
    public String[] ore,oredb,mindb,testodb;
    public int[] noti;
    public String dataS;
    private Context mContext;
    DbAdapterDiario db;
    Cursor c;
    private Calendar month;


    public AdapterAgenda(Context c, Calendar monthCalendar) {
      //  month = monthCalendar;
        dataS=(String)DateFormat.format("dd MMMM yyyy", monthCalendar);
        mContext = c;
      //  month.set(Calendar.DAY_OF_MONTH, 1);

        refreshDays();
        Rigne("");
    }


    public int getCount() {
        return 24;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }



    public View getView(int position, View v, ViewGroup parent) {

        Holder holder;

        if (v == null) {
            holder = new Holder();
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_testo_age, null);

           holder.oreView = v.findViewById(R.id.textVitemAgenda);
           holder.oreDB= v.findViewById(R.id.textoraagitem);
            holder.minDB= v.findViewById(R.id.textminagitem);
            holder.testo= v.findViewById(R.id.texttestiagitem);
            holder.notif= v.findViewById(R.id.imagenotitem);
            v.setTag(holder);
        }else {holder = (Holder) v.getTag();}



        holder.oreView.setText(oredb[position]);
       // holder.oreDB.setText(oredb[position]);
       // holder.minDB.setText(mindb[position]);
        holder.testo.setText(testodb[position]);
        holder.notif.setImageResource(noti[position]);
      //  holder.testo.setText(dataS);


        return v;
    }
private  class Holder{
    TextView oreView,oreDB,minDB,testo;
    ImageView notif;
}

    public void refreshDays()
    {

     //   int lastDay = month.getActualMaximum(Calendar.DAY_OF_MONTH);
      //  int firstDay = month.get(Calendar.DAY_OF_WEEK);

/*
        if(firstDay==1){
            days = new String[lastDay+(FIRST_DAY_OF_WEEK*6)];
        }
        else {
            days = new String[lastDay+firstDay-(FIRST_DAY_OF_WEEK+1)];
        }
        */

        ore = new String[24];

        for (int i=0;i<24;i++){
            if (i <10) {
                ore[i] = "0" + i + ":00";
            }else ore[i] = "" + i + ":00";
        }


    }
    public void Rigne(String data){
        oredb= new String[24];
        mindb=new String[24];
        testodb=new String[24];
        noti= new int[24];
        db = new DbAdapterDiario(mContext);
        db.open();
        c=db.fetchAgendaByData(data);
        if (c.getCount() !=0){

                if (c.moveToFirst()) {

                    for (int i=1;i< 1+c.getCount();i++){
                      //  int ore=c.getInt(2);
                        oredb[i]= c.getString(4);

                        testodb[i]=c.getString(5);
                        String not=c.getString(6);
                        if (not.equals("1")){
                              noti[i]=R.drawable.ic_notifications_active_black_24dp;
                        }

                        c.moveToNext();
                     }
                }


        }
        c.close();
        db.close();

    }
}
