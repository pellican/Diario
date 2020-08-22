package com.agenda.diario.Diario;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.agenda.diario.CustomView.CustomViewPager;
import com.agenda.diario.Database.DbAdapterDiario;
import com.agenda.diario.Diario.Testo.Type_font;
import com.agenda.diario.GoogleDrive.DeleteFile;
import com.agenda.diario.GoogleDrive.DriveSyncdb;
import com.agenda.diario.R;
import com.agenda.diario.Singleton;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.Calendar;


public class Frammento_Diario extends Fragment implements Funzioni {


    Context mcontext;
    String contenuto;
    long id;
    int[] categorie ={
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

    Calendar currentDay;
    String dataCorrente,phat;
    String[] date,titoli;
    int[] intsfondo;
    int[] intemoti,intcat;

    CustomViewPager diarioPager;
    int pos=0;
    DiarioPageAdapter adapter;
    DbAdapterDiario db;
    int focusPage;

    FloatingActionButton fab;
    FloatingActionButton fab_x;

    public Frammento_Diario() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.tolbar_diario));
        }
    }
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View diaView = inflater.inflate(R.layout.frammento_diario, container, false);
        currentDay=Calendar.getInstance();
        dataCorrente=(String)DateFormat.format("dd MMMM yyyy", currentDay);
        if (Singleton.getInstance().getData() != null){
            currentDay=Singleton.getInstance().getData();
            dataCorrente=(String)DateFormat.format("dd MMMM yyyy", currentDay);
            Singleton.getInstance().setData(null);
        }
        mcontext=getActivity();
        diarioPager = diaView.findViewById(R.id.paperDiario);
        fab = diaView.findViewById(R.id.fab1);
        fab_x = diaView.findViewById(R.id.fab_canc);

        Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.bounce);
        fab.startAnimation(animation);

        db= new DbAdapterDiario(getActivity());
        db.open();
        if (db.fetchPaginaByData(dataCorrente).getCount()==0){
            db.createContactPagina((String) DateFormat.format("yyyy-MM-dd",currentDay)
                    ,dataCorrente,(String)DateFormat.format("MM",currentDay),R.raw.s1,0,"0",null);
            if (db.fetchfiltro().getCount() ==0){
                db.createContactFiltro(0,0,2);
            }
        }
        Pagina();
        /*
        else {
            db.createContactPagina((String) DateFormat.format("yyyy-MM-dd", currentDay)
                    , dataCorrente,(String)DateFormat.format("MM",currentDay),0);
            date=new String[1];
            date[0]=dataCorrente;
                }

                */

        adapter=new DiarioPageAdapter(getActivity(),date,intsfondo,intemoti,intcat,titoli);
        diarioPager.setAdapter(adapter);
        adapter.setScroll(this,this,this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    FragmentManager di = getChildFragmentManager();
                    Dialog_Diario dialog = new Dialog_Diario();
                    dialog.show(di, "inser");
                    Singleton.getInstance().setDataS(dataCorrente);

            }
        });
        fab_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contenuto == "grid"){
                    Cursor c = db.fetchFotoById(id);
                    if (c.getCount() != 0 && c.moveToFirst()){
                        if (c.getString(4)!= null){

                          DeleteFile deleteFile =  new DeleteFile(getActivity());
                          if (!deleteFile.deleteDriveFile(c.getString(4))){
                              db.createContactCestino(c.getString(4));
                          }
                        }
                        c.close();
                        db.deleteFoto(id);
                    }



                }else if (contenuto == "list"){
                    db.deleteTesto(id);
                }else if (contenuto == "audio"){
                    Cursor c = db.fetchAudioById(id);
                    if (c.getCount() != 0 && c.moveToFirst()){
                        if (c.getString(4)!= null){

                            DeleteFile deleteFile =  new DeleteFile(getActivity());
                            if (!deleteFile.deleteDriveFile(c.getString(4))){
                                db.createContactCestino(c.getString(4));
                            }
                        }
                        c.close();

                    }
                    if (phat != null){

                        File file= new File(phat);
                        file.delete();
                    }
                    db.deleteAudio(id);
                }
                adapter.annulla();
                adapter.notifyDataSetChanged();
                fab_ann();
            }
        });

        diarioPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                final int width = diarioPager.getWidth();
                if (position == focusPage) {
                     int translationX = (int) ((+(width + fab.getWidth()) / 2f) * positionOffset);
                    fab.setTranslationX(translationX);
                }else if (position < focusPage){
                    fab.setTranslationX((fab.getRight() * (1 - positionOffset)));
                }
            }
            @Override
            public void onPageSelected(int position) {
                focusPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                dataCorrente = date[focusPage];
            }
        });
        diarioPager.setCurrentItem(pos);
        return  diaView;
    }



    @Override
       public void onResume() {
        super.onResume();

        Pagina();
        adapter.intemoti=intemoti;
        adapter.intsfondo=intsfondo;
        adapter.intcat=intcat;
        adapter.titoli=titoli;
        adapter.setMode(DiarioPageAdapter.DEFAULT);
        adapter.notifyDataSetChanged();
    }
    public void Pagina(){

        Cursor cur= db.fetchAllContactsPaginaAsc();

        if (cur.getCount()!=0){
            date = new String[cur.getCount()];
            intsfondo = new int[cur.getCount()];
            intemoti = new int[cur.getCount()];
            intcat = new int[cur.getCount()];
            titoli = new String[cur.getCount()];
            if (cur.moveToFirst()) {
                for (int i = 0; i < cur.getCount(); i++) {
                    date[i] = cur.getString(2);
                    intsfondo[i]= cur.getInt(4);
                    intemoti[i]= cur.getInt(5);
                    intcat[i]= categorie[cur.getInt(6)];
                    titoli[i] = cur.getString(7);
                    if (date[i].equals(dataCorrente)){
                        pos=i;
                    }
                    cur.moveToNext();
                }
            }

        }
        cur.close();
    }

    @Override
    public void on_Scroll(Boolean direzione) {
        if (!direzione){
            fab.animate().scaleX(0).scaleY(0).setDuration(100).start();
            fab.setClickable(false);
        }else if (direzione){
            fab.animate().scaleX(1).scaleY(1).setDuration(100).start();
            fab.setClickable(true);
        }
    }

    @Override
    public void fab_clik(String contenuto,long id,String phat) {

        this.contenuto=contenuto;
        this.id=id;
        this.phat=phat;
        fab.setVisibility(View.INVISIBLE);
        fab_x.setVisibility(View.VISIBLE);
        Animation animfab = AnimationUtils.loadAnimation(getActivity(),R.anim.bounce);
        fab_x.startAnimation(animfab);
        diarioPager.setPagingEnabled(false);
    }

    @Override
    public void fab_ann() {

        fab_x.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.VISIBLE);
        Animation animfab = AnimationUtils.loadAnimation(getActivity(),R.anim.bounce);
        fab.startAnimation(animfab);
        diarioPager.setPagingEnabled(true);
    }



}
