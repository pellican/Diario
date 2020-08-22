package com.agenda.diario.Database;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.util.Log;

import com.agenda.diario.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by portatile on 12/07/2016.
 */
public class GetDatabase {
    Context mcontext;
    DbAdapterDiario db;
    ArrayList<Database> databases;
    ArrayList<String> dataInsieme;
    Calendar mese;


    int[] categorie={
            R.string.altro,
            R.string.famiglia,
            R.string.amici,
            R.string.studio,
            R.string.amore,
            R.string.lavoro,
            R.string.attivita,
            R.string.hobby,
            R.string.idee,
            R.string.vacanza,
            R.string.festa
    };
    public GetDatabase(Context mcontext) {
        this.mcontext=mcontext;
        mese=Calendar.getInstance();
    }


    public ArrayList<Database> getDati(){
        databases = new ArrayList<>();
        dataInsieme = new ArrayList<>();

        db = new DbAdapterDiario(mcontext);
        db.open();
        Cursor f;

        f=db.fetchfiltro();f.moveToFirst();
        if (f.getCount() != 0) {
            switch (f.getInt(1)) {
                case 0:

                    Cursor a = db.fetchPaginaByMese((String) DateFormat.format("MM", mese));
                    casi(a, f.getInt(3));
                    break;
                case 1:

                    Cursor c = db.fetchAllContactsPaginaAsc();
                    casi(c, f.getInt(3));
                    break;
                case 2:
                    Cursor d = db.fetchAllContactsPaginaDisc();
                    casi(d, f.getInt(3));
                    break;
                case 3:
                    Cursor e = db.fetchPaginaByCategoria(Integer.toString(f.getInt(2)));
                    casi(e, f.getInt(3));
                    break;
            }

        }

        return databases;
    }
    private void casi(Cursor c,int vista) {

        if ((vista == 0 || vista == 2) && c.getCount() != 0 && c.moveToFirst()) {

            for (int i = 0; i < c.getCount(); i++) {

                Database ogg = new Database();
                Cursor te = db.fetchTestoByData(c.getString(2));
                if (te.getCount() != 0 && te.moveToFirst()) {

                    ogg.setTesto(te.getString(2));
                }
                te.close();
                Cursor im = db.fetchFotoByData(c.getString(2));
                if (im.getCount() != 0 && im.moveToFirst()) {

                    ogg.setFoto(im.getString(2));
                  //  ogg.setFotoDrive(im.getString(4));
                }
                im.close();

                Cursor ag = db.fetchAgendaByData(c.getString(2));
                if (vista == 2 && ag.getCount() != 0 && ag.moveToFirst()) {
                    ogg.setOra(ag.getString(4));
                    ogg.setTestoAg(ag.getString(5));
                    ogg.setNotifica(ag.getString(6));
                     ogg.setVista(Database.INSIEME);
                    dataInsieme.add(c.getString(2));
                }else {
                    ogg.setVista(Database.DIARIO);
                }
                ogg.setData(c.getString(2));
                ogg.setEmoti(c.getInt(5));
                ogg.setCategoria(categorie[c.getInt(6)]);
                ogg.setTitolo(c.getString(7));
                databases.add(ogg);

                c.moveToNext();
            }

        }
        Cursor ca=db.fetchContactsAgendaOrd();
        if (vista == 2  && ca.getCount() != 0 && ca.moveToFirst()){

                for (int i = 0; i < ca.getCount(); i++) {
                    if ( dataInsieme.size() !=0){
                        Boolean trovato = false;
                        for (int x= 0;x<dataInsieme.size();x++) {
                            //compara le date
                            if (dataInsieme.get(x).equals(ca.getString(1))) {

                              //  dataInsieme.remove(x);
                                trovato = true;

                            }
                        }
                            if (!trovato){

                                Database data = new Database();

                                data.setData(ca.getString(1));
                                data.setOra(ca.getString(4));
                                data.setTestoAg(ca.getString(5));
                                data.setNotifica(ca.getString(6));
                                data.setVista(Database.AGENDA);
                                databases.add(data);
                            }


                    }else {
                        Database data = new Database();
                        data.setData(ca.getString(1));
                        data.setOra(ca.getString(4));
                        data.setTestoAg(ca.getString(5));
                        data.setNotifica(ca.getString(6));
                        data.setVista(Database.AGENDA);
                        databases.add(data);
                    }
                    ca.moveToNext();
                }
        }
        if (vista == 1 && ca.getCount() != 0 && ca.moveToFirst()){

                for (int i = 0; i < ca.getCount(); i++) {
                    Database data = new Database();

                    data.setData(ca.getString(1));
                    data.setOra(ca.getString(4));
                    data.setTestoAg(ca.getString(5));
                    data.setNotifica(ca.getString(6));
                    data.setVista(Database.AGENDA);
                    databases.add(data);

                    ca.moveToNext();
                }
        }
        ca.close();
        c.close();
        db.close();
    }

}
