package com.agenda.diario.Calendario;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
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
 * Created by portatile on 25/12/2015.
 */
public class CalendarioAdapter extends BaseAdapter {

    static final int FIRST_DAY_OF_WEEK =1; // Sunday = 0, Monday = 1


     String[] days,diario,hmesi,amesi,agenda;
     int[] vdia,vage;
     String mese,data,day,day2,data2;
     Context mContext;
     DbAdapterDiario db;

     Calendar month;
     Calendar selectedDate;


    public CalendarioAdapter(Context c, Calendar monthCalendar) {
        month = monthCalendar;
        selectedDate = (Calendar)monthCalendar.clone();
        mContext = c;
        month.set(Calendar.DAY_OF_MONTH, 1);


        refreshDays();

    }


    public int getCount() {
        return days.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        TextView dayView,txdiar,txagen;
        ImageView imdiario,imagenda;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.calendario_item, null);

        }
        dayView = v.findViewById(R.id.date);
      //  txdiar = (TextView)v.findViewById(R.id.texdiar);
        imdiario= v.findViewById(R.id.imdiar);
        imagenda= v.findViewById(R.id.imagenda);
        v.setVisibility(View.VISIBLE);
        // disable empty days from the beginning
        if(days[position].equals("")) {
            dayView.setClickable(false);
            dayView.setFocusable(false);
            v.setVisibility(View.INVISIBLE);
        }
        else {
            // mark current day as focused
            if(month.get(Calendar.YEAR)== selectedDate.get(Calendar.YEAR) && month.get(Calendar.MONTH)== selectedDate.get(Calendar.MONTH) && days[position].equals(""+selectedDate.get(Calendar.DAY_OF_MONTH))) {
              //  v.setBackgroundColor(Color.parseColor("#d9dbdb"));

                if (position==6){v.setBackgroundResource(R.drawable.selezi_dom);}
                else if(position==13){v.setBackgroundResource(R.drawable.selezi_dom);}
                else if(position==20){v.setBackgroundResource(R.drawable.selezi_dom);}
                else if(position==27){v.setBackgroundResource(R.drawable.selezi_dom);}
                else if(position==34){v.setBackgroundResource(R.drawable.selezi_dom);}
                else {v.setBackgroundResource(R.drawable.selezione2);}

            }
            else {

                if (position==6){v.setBackgroundColor(Color.parseColor("#ffdcdc"));}
                else if(position==13){v.setBackgroundColor(Color.parseColor("#ffdcdc"));}
                else if(position==20){v.setBackgroundColor(Color.parseColor("#ffdcdc"));}
                else if(position==27){v.setBackgroundColor(Color.parseColor("#ffdcdc"));}
                else if(position==34){v.setBackgroundColor(Color.parseColor("#ffdcdc"));}
                else {v.setBackgroundColor(Color.WHITE);}
            }
        }

        dayView.setText(days[position]);

        //txdiar.setText(diario[position]);
        imdiario.setVisibility(vdia[position]);
        imagenda.setVisibility(vage[position]);


        return v;
    }

    public void refreshDays(){

        int lastDay = month.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDay = month.get(Calendar.DAY_OF_WEEK);
        if (firstDay == 1) {
            days = new String[lastDay + (FIRST_DAY_OF_WEEK * 6)];
        } else {
            days = new String[lastDay + firstDay - (FIRST_DAY_OF_WEEK + 1)];
        }
        int j;// = FIRST_DAY_OF_WEEK;
        if (firstDay > 1) {
            for (j = 0; j < firstDay - FIRST_DAY_OF_WEEK; j++) {
                days[j] = "";
            }
        } else {
            for (j = 0; j < FIRST_DAY_OF_WEEK * 6; j++) {
                days[j] = "";
            }
            j = FIRST_DAY_OF_WEEK * 6 + 1; // sunday => 1, monday => 7
        }
        int dayNumber = 1;



        diario= new String[days.length];
        vdia = new int[days.length];
        vage= new int[days.length];
        for (int i = j - 1; i < days.length; i++) {
            days[i] = "" + dayNumber;
            dayNumber++;
            vdia[i]= View.INVISIBLE;
            vage[i]= View.INVISIBLE;
       }



    }
    public void icone(String mesi,Calendar date) {

            db = new DbAdapterDiario(mContext);
            db.open();
            Cursor  c=db.fetchPaginaByMese((String)DateFormat.format("MM",date));

        if (c.getCount() !=0) {

            hmesi = new String[c.getCount()];
            if (c.moveToFirst()) {
                for (int a = 0; a < c.getCount(); a++) {
                    hmesi[a] = c.getString(2);
                    c.moveToNext();
                }
            }


            for (int i = 0; i < days.length; i++) {
                day = days[i];
                if (day.length() == 1) {
                    day = "0" + day;
                }
                data = day + " " + mesi;
                for (int e = 0; e < hmesi.length; e++) {
                   if (data.equals (hmesi[e])){
                        vdia[i]= View.VISIBLE;
                    }

                }

            }
        }
            c.close();


        Cursor a =db.fetchAgendaByMese((String)DateFormat.format("MMMM",date));
        if (a.getCount() != 0){
            amesi = new String[a.getCount()];
            if (a.moveToFirst()) {
                for (int x = 0; x < a.getCount(); x++) {
                    amesi[x] = a.getString(1);
                    a.moveToNext();
                }
            }
            for (int i = 0; i < days.length; i++) {
                day2 = days[i];
                if (day2.length() == 1) {
                    day2 = "0" + day2;
                }
                data2 = day2 + " " + mesi;
                for (int e = 0; e < amesi.length; e++) {
                    if (data2.equals (amesi[e])){
                        vage[i]= View.VISIBLE;
                    }

                }

            }
        }
        a.close();
        db.close();

    }
}
