package com.agenda.diario.Agenda;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.agenda.diario.Database.DbAdapterDiario;
import com.agenda.diario.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by portatile on 17/03/2016.
 */
public class Dialog_agenda extends AppCompatActivity {

    EditText edit;
    String datas,oras,oredb,notifs="0";
    int ora;
    long id;
    ImageView notif,conf,ind,canc;
    TextView datat;
    Calendar calendar,calVer;
    TimePicker timePicker;
    Boolean clik= false,clicanc=false, verif = false;
    Bundle intent;
    SimpleDateFormat df;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_agenda);


        intent=getIntent().getExtras();
        datas=intent.getString("data");
        ora=intent.getInt("ora");
        calendar=Calendar.getInstance();
        calVer=Calendar.getInstance();
        df = new SimpleDateFormat("dd MMMM yyyy",Locale.getDefault());
        canc= findViewById(R.id.imagdelete);
        edit= findViewById(R.id.editTextdialage);
        conf= findViewById(R.id.imageconfeag);
        timePicker= findViewById(R.id.timePicker);

        datat = findViewById(R.id.textVdataAg);

        ind= findViewById(R.id.imageinag);
        notif= findViewById(R.id.imagenot);
        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             if (verif) {
                 if (!clik) {

                     Toast.makeText(getApplication(),R.string.notifica_on, Toast.LENGTH_SHORT).show();
                     clik = true;
                     notifs = "1";
                     notif.setImageResource(R.drawable.ic_notifications_active_black_24dp);
                 } else {
                     Toast.makeText(getApplication(), R.string.notifica_off, Toast.LENGTH_SHORT).show();
                     notif.setImageResource(R.drawable.ic_notif);
                     clik = false;
                     notifs = "0";

                 }
             }else{
                 Toast.makeText(getApplication(), R.string.notifica_non, Toast.LENGTH_SHORT).show();
             }

            }
        });
        ind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        DbAdapterDiario db = new DbAdapterDiario(getApplication());
        db.open();Cursor c;
        c=db.fetchAgendaByData(datas);

        if (c.getCount() != 0){

            if (ora != -1 && c.moveToPosition(ora)){
                  id=c.getLong(0);
                  oredb=c.getString(4);
                  edit.setText(c.getString(5));
                  notifs=c.getString(6);


            }


        }
        db.close();

        datat.setText(datas);
        timePicker.setIs24HourView(true);

        if (id ==0){
            timePicker.setCurrentHour(0);
            timePicker.setCurrentMinute(0);
        }else {
            canc.setColorFilter(Color.BLACK);
            int ore1= Integer.parseInt(oredb.substring(0,2));
            int min0= Integer.parseInt(oredb.substring(3,5));
            timePicker.setCurrentHour(ore1);
            timePicker.setCurrentMinute(min0);
            if (notifs.equals("0")){
                notif.setImageResource(R.drawable.ic_notif);
                clik = false;
            }else if (notifs.equals("1")){
                notif.setImageResource(R.drawable.ic_notifications_active_black_24dp);
                clik = true;
            }

            canc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!clicanc){
                        canc.setColorFilter(Color.RED);
                        Toast.makeText(getApplication(), R.string.cancellare,Toast.LENGTH_SHORT).show();
                        clicanc=true;
                    }else {
                        DbAdapterDiario db = new DbAdapterDiario(getApplication());
                        db.open();
                        db.deleteAgenda(id);
                        db.close();
                        finish();

                    }
                }
            });
        }
        try {
            calVer.setTime(df.parse(datas));

        }catch (Exception e){

        }

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hore, int minu) {
                calVer.set(Calendar.HOUR_OF_DAY,hore);
                calVer.set(Calendar.MINUTE,minu);
                if (System.currentTimeMillis() < calVer.getTimeInMillis()){
                    notif.setImageResource(R.drawable.ic_notifications_active_black_24dp);
                    clik = true;
                    notifs = "1";
                    verif= true;
                }else{
                    notif.setImageResource(R.drawable.ic_notif);
                    clik = false;
                    notifs = "0";
                    verif=false;
                }
            }
        });




        conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oras,minus;
                String testo = edit.getText().toString();
                int or=timePicker.getCurrentHour();
                int mi=timePicker.getCurrentMinute();
                if (or < 10) {
                    oras = "0" + or;
                }else oras =""+or;
                if (mi< 10){
                    minus = "0" + mi;
                }else minus = ""+mi ;
                String oradb=oras+":"+minus;
                String[] mese = datas.split(" ");
                DbAdapterDiario db = new DbAdapterDiario(getApplication());
                db.open();
                int flag;
                if (id == 0) {
                    db.createContactAgenda(datas,mese[1],or, oradb,testo , notifs);
                    flag= PendingIntent.FLAG_ONE_SHOT;
                } else {
                    db.updateContactAgenda(id, datas,mese[1], or, oradb, testo, notifs);
                    flag= PendingIntent.FLAG_UPDATE_CURRENT;
                }

                db.close();
                finish();
                if (clik){
                    Notifica(or,mi,testo,flag);
                }
            }
        });


    }

    public  void Notifica(int ora,int minuti,String testo,int flag){
            try {
              //  SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy",Locale.getDefault());

                calendar.setTime(df.parse(datas));
                calendar.set(Calendar.HOUR_OF_DAY,ora);
                calendar.set(Calendar.MINUTE,minuti);
            }catch (ParseException e){

            }


               // long when = System.currentTimeMillis() + 60000L;
                int id= (int)calendar.getTimeInMillis();
                AlarmManager am = (AlarmManager) getApplication().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getApplication(), MyReceiver.class);
                intent.putExtra("notifica", testo);
                intent.putExtra("ora",ora);
                intent.putExtra("data",datas);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplication(),id , intent, flag);
                am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

    }




}
