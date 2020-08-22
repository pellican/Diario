package com.agenda.diario.Diario;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agenda.diario.Agenda.Frammento_Agenda;
import com.agenda.diario.CustomView.CustomEditText;
import com.agenda.diario.CustomView.CustomGridView;
import com.agenda.diario.Database.DbAdapterDiario;
import com.agenda.diario.Diario.Impostazioni.ImpostaActivity;
import com.agenda.diario.Diario.Testo.TestoActivity;
import com.agenda.diario.MainActivity;
import com.agenda.diario.Note.Frammento_Note;
import com.agenda.diario.R;
import com.agenda.diario.Singleton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

class DiarioPageAdapter extends PagerAdapter {
    static int DEFAULT = 0;
    private static int IMAGINE = 1;
    public static int DISEGNO = 2;
    private static int TESTO = 3;
    private static int AUDIO = 4;
    private int mode=DEFAULT;

    Calendar calendar;
    String[] date ,titoli;
    int[] intemoti,intcat;
    int[] intsfondo;

    long idaudio,idgrid;
    String phat,dispath,data;
    Context mcontext;
    LayoutInflater inflater;

  //  DbAdapterDiario dbgrid,dblist;
    Animation shake,in,out,fadein;
    View vista,vilist,viaudio;
  //  int posigrid,posilist,posaudio;

    ListView listView;

    CustomGridView gridView,audioGrid;
    Funzioni scroll;
    Funzioni fab_click;
    Funzioni fad_ann;

    private int mLastFirstVisibleItem;

    public DiarioPageAdapter(Context context, String[] date,int[]intsfondo,int[] intemoti,int[] intcat,String[] titoli) {
        this.intcat=intcat;
        this.mcontext = context;
        this.intsfondo=intsfondo;
        this.intemoti=intemoti;
        inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.date = date;
        this.titoli=titoli;
        calendar=Calendar.getInstance();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {

            return POSITION_NONE;

    }


    @Override
    public int getCount() {
        return date.length;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View v = inflater.inflate(R.layout.diario_framm, container, false);
        final TextView textView = v.findViewById(R.id.textVgio);
        TextView tebarann= v.findViewById(R.id.texbarann);
        final TextView tebarmodif= v.findViewById(R.id.texbarmodif);
        ImageView sett= v.findViewById(R.id.imagsett);
        ImageView agen= v.findViewById(R.id.imagage);
        DbAdapterDiario db = new DbAdapterDiario(mcontext);
        db.open();
        if (db.fetchAgendaByData(date[position]).getCount() != 0){
            agen.setVisibility(View.VISIBLE);
        }
        db.close();
        final FrameLayout sfondo= v.findViewById(R.id.laysfondodiario);
        final RelativeLayout bar= v.findViewById(R.id.actionbar);
        final RelativeLayout relbar = v.findViewById(R.id.bar);

        textView.setText(date[position]);
        listView= v.findViewById(R.id.listView);
        View em=inflater.inflate(R.layout.item_intesta_diario,null);
        View lv = inflater.inflate(R.layout.foto_griglia, null);
        View au=inflater.inflate(R.layout.audio_griglia,null);

        listView.addHeaderView(em,null,false);
        listView.addHeaderView(lv,null,false);
        listView.addHeaderView(au,null,false);

        final AdapterListview adapterListview= new AdapterListview(mcontext,date[position]);
        listView.setAdapter(adapterListview);

        ImageView emoti= em.findViewById(R.id.imageemotidiar);
        emoti.setImageResource(intemoti[position]);
        final CustomEditText  editTitolo= em.findViewById(R.id.edit_titolo);
        RelativeLayout cat= em.findViewById(R.id.relativecat);
        TextView textcat= em.findViewById(R.id.texcatdiario);
        textcat.setText(intcat[position]);

        gridView = lv.findViewById(R.id.gridviewDiario);
        final AdapterFotoview adapterFotoview = new AdapterFotoview(mcontext,date[position]);
        gridView.setAdapter(adapterFotoview);

        audioGrid= au.findViewById(R.id.gridviewAudio);
        final AdapterAudioview adapterAudioview = new AdapterAudioview(mcontext,date[position]);
        audioGrid.setAdapter(adapterAudioview);

        in=AnimationUtils.loadAnimation(mcontext,R.anim.abc_slide_in_top);
        out=AnimationUtils.loadAnimation(mcontext,R.anim.abc_slide_out_top);
        shake = AnimationUtils.loadAnimation(mcontext, R.anim.shake);
        fadein=AnimationUtils.loadAnimation(mcontext,R.anim.fade_in);

        sfondo.setBackgroundResource(intsfondo[position]);
        audioGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mode == DEFAULT) {
                    DbAdapterDiario db = new DbAdapterDiario(mcontext);
                    db.open();
                    Cursor c = db.fetchAudioByData(date[position]);
                    if (c.moveToPosition(i)) {

                        Intent intent = new Intent(mcontext, PlayerActivity.class);
                        intent.putExtra("canzone", c.getString(2));
                        intent.putExtra("titolo", c.getString(3));
                        intent.putExtra("artista", c.getString(4));
                        mcontext.startActivity(intent);
                    }
                    c.close();
                    db.close();
                } else  {
                    annulla();
                    relbar.setVisibility(View.VISIBLE);

                    bar.setVisibility(View.INVISIBLE);
                    fad_ann.fab_ann();

                }
            }
        });
        audioGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(mode == DEFAULT){
                    view.startAnimation(shake);
                    viaudio=view;


                    DbAdapterDiario db = new DbAdapterDiario(mcontext);
                    db.open();
                    Cursor c =db.fetchAudioByData(date[position]);
                    if (c.moveToPosition(i)){
                        idaudio=c.getLong(0);
                        if (c.getString(3).equals(date[position])){
                            phat=c.getString(2);
                        }

                    }
                    c.close();
                    db.close();
                    bar.setVisibility(View.VISIBLE);

                    relbar.setVisibility(View.INVISIBLE);
                    mode=AUDIO;

                    fab_click.fab_clik("audio",idaudio,phat);
                    return true;
                }else
                    return false;



            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view1, int pos, long l) {

                if (mode == DEFAULT){
                    mode=IMAGINE;
                    view1.startAnimation(shake);
                    vista = view1;

                    DbAdapterDiario dbgrid = new DbAdapterDiario(mcontext);
                    dbgrid.open();
                    Cursor c = dbgrid.fetchFotoByData(date[position]);


                    if (c.moveToPosition(pos)) {
                        idgrid=c.getLong(0);
                        if (c.getString(3).equals("disegno")){
                            tebarmodif.setVisibility(View.VISIBLE);
                            dispath=c.getString(2);
                            data=c.getString(1);
                        }else tebarmodif.setVisibility(View.INVISIBLE);

                    }
                    c.close();
                    dbgrid.close();
                    bar.setVisibility(View.VISIBLE);
                    relbar.setVisibility(View.INVISIBLE);
                    fab_click.fab_clik("grid",idgrid,null);
                    return true;
                }else
                    return false;
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int positionClick, long id) {
                if (mode == DEFAULT) {
                    Intent i = new Intent(mcontext, FotoActivity.class);
                    i.putExtra("data", date[position]);
                    i.putExtra("pos", positionClick);
                    mcontext.startActivity(i);

                } else {

                    annulla();
                    relbar.setVisibility(View.VISIBLE);

                    bar.setVisibility(View.INVISIBLE);
                    fad_ann.fab_ann();

                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (i>2 && mode==DEFAULT) {
                    Intent te = new Intent(mcontext, TestoActivity.class);
                    te.putExtra("datatesto", date[position]);
                    te.putExtra("posilist", i - 3);

                    mcontext.startActivity(te);

                }
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long l) {

                if (pos >2 && mode==DEFAULT) {
                    int posilist=pos-3;
                    DbAdapterDiario dblist=new DbAdapterDiario(mcontext);
                    dblist.open();
                    Cursor cu = dblist.fetchTestoByData(date[position]);
                    long  id= 0;
                    if (cu.moveToPosition(posilist)) {
                            id= cu.getLong(0);
                    }
                    cu.close();
                    dblist.close();
                    vilist=view;
                    vilist.setBackgroundColor(Color.LTGRAY);
                    bar.setVisibility(View.VISIBLE);

                    relbar.setVisibility(View.INVISIBLE);
                    mode=TESTO;
                    fab_click.fab_clik("list",id,null);
                    return true;
                }else
                    return false;
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (mLastFirstVisibleItem < i) {
                    scroll.on_Scroll(false);

                } else if (mLastFirstVisibleItem > i) {
                    scroll.on_Scroll(true);

                }
                mLastFirstVisibleItem = i;
            }
        });
        tebarmodif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=new Frammento_Note();
                Bundle b=new Bundle();
                b.putString("disegno",dispath);
                b.putString("data",data);
                b.putLong("id",idgrid);
                fragment.setArguments(b);
                MainActivity main= (MainActivity)mcontext;
                main.switchContent(R.id.frameContainer,fragment,"note");
            }
        });

        tebarann.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                annulla();
                relbar.setVisibility(View.VISIBLE);

                bar.setVisibility(View.INVISIBLE);
                fad_ann.fab_ann();

            }
        });
        agen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                try {
                    calendar.setTime(dateFormat.parse(date[position]));
                } catch (ParseException e) {
                }
                Singleton.getInstance().setData(calendar);
                if (mcontext instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) mcontext;
                    Fragment frag =new  Frammento_Agenda();
                    mainActivity.switchContent(R.id.frameContainer, frag,"diario");
                }
            }
        });
        emoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext,ImpostaActivity.class);
                intent.putExtra("pos",1);
                intent.putExtra("data",date[position]);
                mcontext.startActivity(intent);

            }
        });

        if (titoli[position] != null){
            editTitolo.setText(titoli[position]);
        }
        editTitolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Singleton.getInstance().setDataTitolo(date[position]);
                if (!editTitolo.isCursorVisible()){
                    editTitolo.setCursorVisible(true);
                }
            }
        });
        editTitolo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE )
                {
                    if (editTitolo.getText().toString().equals("")){
                        SetTitolo(date[position],null,position);
                    }else
                        SetTitolo(date[position],editTitolo.getText().toString(),position);

                   editTitolo.setCursorVisible(false);
                }
                return false;
            }
        });
        editTitolo.setBack(new CustomEditText.Back() {
            @Override
            public void BackPress() {
                if (editTitolo.getText().toString().equals("")){
                    SetTitolo(date[position],null,position);
                }else
                SetTitolo(date[position],editTitolo.getText().toString(),position);
            }
        });




        cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext,ImpostaActivity.class);
                intent.putExtra("pos",2);
                intent.putExtra("data",date[position]);
                mcontext.startActivity(intent);
            }
        });
        sett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext,ImpostaActivity.class);
                intent.putExtra("pos",0);
                intent.putExtra("data",date[position]);
                mcontext.startActivity(intent);

            }
        });
        container.addView(v);
        return v;
    }


    public  void annulla(){

        if (mode==IMAGINE) {
            vista.clearAnimation();
        }else if (mode==TESTO){
            vilist.setBackgroundColor(Color.TRANSPARENT);
        }else if (mode==AUDIO){
            viaudio.clearAnimation();
        }
        mode=DEFAULT;
    }
    public void setScroll(Funzioni scroll, Funzioni fab_click, Funzioni fad_ann){

        this.scroll=scroll;
        this.fab_click=fab_click;
        this.fad_ann=fad_ann;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void SetTitolo(String data,String titolo,int posizione){

        DbAdapterDiario db= new DbAdapterDiario(mcontext);
        db.open();Cursor c;
        c=db.fetchPaginaByData(data);
        if (c.getCount() != 0){
            if (c.moveToFirst()){
                db.updateContactPagina(c.getLong(0), c.getString(1), c.getString(2), c.getString(3),c.getInt(4),
                        c.getInt(5), c.getString(6),titolo);
                titoli[posizione]=titolo;

            }
        }
    }
    public void setMode(int mode){
        this.mode=mode;
    }


}
