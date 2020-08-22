package com.agenda.diario.Diario_list;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.agenda.diario.Agenda.Frammento_Agenda;
import com.agenda.diario.Database.Database;
import com.agenda.diario.Database.DbAdapterDiario;
import com.agenda.diario.Database.GetDatabase;
import com.agenda.diario.Diario.Frammento_Diario;
import com.agenda.diario.MainActivity;
import com.agenda.diario.R;
import com.agenda.diario.Singleton;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import me.toptas.fancyshowcase.FancyShowCaseView;


public class Frag_Diario_list extends Fragment implements ItemLongClickListener {

    FloatingActionsMenu actionsMenu;
    FloatingActionButton actionDell,actionDiario,actionAgenda;

    ImageView draw,cerca,filtro;
    View view;
    EditText edit;
    RecyclerView list;
    RelativeLayout actinbar,bar;
    InputMethodManager input;
    TextView annulla,agedia;
    AdapterList adapter;
    FancyShowCaseView caseView;
    ArrayList<Database> dati;

    Context mcontext;
    Boolean selez=false;
    DbAdapterDiario db;
    String data;
    View dia,sel,overlay;
    int posizione,cursel;
    Animation in,out,bounce,fan_menu_bounce,search;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.tolbar_diario_list));
        }

        input = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
      //  getDatabase = new GetDatabase(getActivity());

      //  dati=getDatabase.getDati();
       // getListDati();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dati= new GetDatabase(getActivity()).getDati();
        adapter.update(dati);
        caseView= new FancyShowCaseView.Builder(getActivity())
                .focusOn(actionDell)
                .title(getResources().getString(R.string.per_iniziare))
                .backgroundColor(Color.parseColor("#90000120"))
                .delay(1000)
                .build();
        if (dati.size() == 0){
            // tutorial();
            caseView.show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        dia = inflater.inflate(R.layout.frammento_diario_list, container, false);

        mcontext=getActivity();


        in=AnimationUtils.loadAnimation(mcontext,R.anim.abc_slide_in_top);
        out=AnimationUtils.loadAnimation(mcontext,R.anim.abc_slide_out_top);
        bounce = AnimationUtils.loadAnimation(mcontext,R.anim.bounce);
        fan_menu_bounce=AnimationUtils.loadAnimation(mcontext,R.anim.fab_menu_bounce);
        actionsMenu= dia.findViewById(R.id.fabmenu);
        actionsMenu.startAnimation(fan_menu_bounce);
        actionsMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                overlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                overlay.setVisibility(View.INVISIBLE);

            }
        });
        setOverlay(R.color.black_overlay);


        actionDell= dia.findViewById(R.id.fab_cestino);
        actionDell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elimina();
            }
        });
        actionDiario= dia.findViewById(R.id.fab_diario);
        actionDiario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionsMenu.collapse();
               // dialog();
                setData();
            }
        });
        actionAgenda= dia.findViewById(R.id.fab_agenda);
        actionAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionsMenu.collapse();
                Fragment fragment = new Frammento_Agenda();
                switchContent(R.id.frameContainer,fragment,"agenda");
            }
        });
        actinbar= dia.findViewById(R.id.actionbarlist);
        bar= dia.findViewById(R.id.bar2);
        annulla= dia.findViewById(R.id.annulla_list);
        list= dia.findViewById(R.id.recicle);
        draw= dia.findViewById(R.id.imagedraw);
        cerca= dia.findViewById(R.id.imagecerca);
        filtro= dia.findViewById(R.id.imagefiltro);
        edit= dia.findViewById(R.id.editcerca);

        agedia= dia.findViewById(R.id.texagendiario);
        view=dia.findViewById(R.id.viewcerca);
        adapter=new AdapterList(getActivity(),dati);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator itemAnimator=new DefaultItemAnimator();
        itemAnimator.setAddDuration(700);
        itemAnimator.setRemoveDuration(700);
        list.setItemAnimator(itemAnimator);
        list.setAdapter(adapter);


        adapter.setClickListener(this);
        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                draw_ind();
            }
        });
        cerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCerca();
            }
        });
        filtro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popfiltro();
            }
        });
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ArrayList<Database> fil = Cerca(charSequence.toString());
                adapter.update(fil);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actinbar.getVisibility() == View.VISIBLE){
                    ripristino();
                }
            }
        });
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy >0){

                    actionsMenu.animate().translationY(200).setDuration(100).start();

                }else if (dy <0){

                    actionsMenu.animate().translationYBy(200).translationY(0).setDuration(100).start();

                }
            }
        });
        switch (getfiltro()){
            case 0:agedia.setText(R.string.diario);
                break;
            case 1:agedia.setText(R.string.agenda);
                break;
            case 2:agedia.setText(R.string.agendiario);
                break;
            default:agedia.setText("");
        }

        return  dia;
    }
    // TUTORIAL

    public void removeTutorial(){
        if (caseView != null){
            caseView.hide();
        }

    }


    private ArrayList<Database> Cerca(String cerca){

        cerca=cerca.toLowerCase();
        ArrayList<Database> database = new ArrayList<>();
        for (int i=0;i<dati.size();i++){
            String titolo= null,testo= null;
            if (dati.get(i).getTitolo() != null) {
                titolo=dati.get(i).getTitolo().toLowerCase();
            }
            if (dati.get(i).getTesto() != null){
                testo=dati.get(i).getTesto().toLowerCase();
            }


            Log.d("filtro",testo+titolo);
            if ((titolo != null && titolo.contains(cerca)) ||
                    (testo != null && testo.contains(cerca))){
                database.add(dati.get(i));
            }
        }


        return database;
    }


    private void draw_ind() {

        if (edit.getVisibility() == View.VISIBLE){
            draw.setImageResource(R.drawable.ic_menu_grey600_24dp);
            edit.setVisibility(View.INVISIBLE);
            agedia.setVisibility(View.VISIBLE);
            cerca.setVisibility(View.VISIBLE);
            filtro.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
            adapter.update(dati);
            input.hideSoftInputFromWindow(edit.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }else {
            if (mcontext instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) mcontext;
                mainActivity.clikdrawer(draw);
            }
        }
    }

    private void setCerca(){

        edit.setVisibility(View.VISIBLE);
        search = AnimationUtils.loadAnimation(mcontext,R.anim.cerca);
        edit.startAnimation(search);
        edit.requestFocus();
        input.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT );
        draw.setImageResource(R.drawable.ic_arrow_back);
        agedia.setVisibility(View.INVISIBLE);
        cerca.setVisibility(View.INVISIBLE);
        filtro.setVisibility(View.INVISIBLE);
        view.setVisibility(View.INVISIBLE);

    }

    private void elimina() {

        db = new DbAdapterDiario(getActivity());
        db.open();
        db.removeAllbyData(data);

        db.close();
        adapter.list.remove(posizione);
        adapter.remove(posizione);
        update();
        ripristino();

    }
    void setData(){
        DatePickerDialog dialog =new DatePickerDialog();
        dialog.setTitle(getString(R.string.aggiungi));
        dialog.setAccentColor(getResources().getColor(R.color.bar_diario_list));


        dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar=Calendar.getInstance();
                calendar.set(year,monthOfYear,dayOfMonth);
                Singleton.getInstance().setData(calendar);
                switchContent(R.id.frameContainer,new Frammento_Diario(),"diario");

            }
        });
        dialog.show(getActivity().getFragmentManager(),"dialog");
    }

    public void dialog(){

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_diario_list);
        final DatePicker datePicker= dialog.findViewById(R.id.calendario);
        TextView ok= dialog.findViewById(R.id.okagg);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                calendar.set(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth());
                Singleton.getInstance().setData(calendar);
                switchContent(R.id.frameContainer,new Frammento_Diario(),"diario");
                dialog.dismiss();
            }
        });

        TextView ann= dialog.findViewById(R.id.textann);
        ann.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    public void popfiltro(){

        final Dialog pop = new Dialog(getActivity());
        pop.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pop.setContentView(R.layout.popfiltro);
        final RadioGroup rad1= pop.findViewById(R.id.radiofiltro);
        RadioGroup rad2= pop.findViewById(R.id.radiofiltro_di_ag);
        final Spinner spinner= pop.findViewById(R.id.spincat);

        final ArrayAdapter adapter =ArrayAdapter.createFromResource(mcontext,R.array.categor,android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        final DbAdapterDiario db =new DbAdapterDiario(mcontext);
        db.open();

        if (db.fetchfiltro().getCount() ==0){
            db.createContactFiltro(0,0,2);
        }
        final Cursor c=db.fetchfiltro();
        c.moveToFirst();

        cursel=c.getInt(2);
        spinner.setSelection(cursel);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (cursel != i){

                   if ( ((RadioButton) rad1.getChildAt(3)).isChecked()){
                       db.updateFiltro(1,c.getInt(1),i,c.getInt(3));
                       update();
                       pop.dismiss();
                   } //else rad1.check((rad1.getChildAt(3)).getId());
                }
                cursel=i;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rad1.check((rad1.getChildAt(c.getInt(1))).getId());
        rad1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rdiar_mese:
                        db.updateFiltro(1,0,c.getInt(2),c.getInt(3));

                        break;
                    case R.id.rdiar_vecchi:
                        db.updateFiltro(1,1,c.getInt(2),c.getInt(3));

                        break;
                    case R.id.rdiar_nuovi:
                        db.updateFiltro(1,2,c.getInt(2),c.getInt(3));

                        break;
                    case R.id.rdiar_cat:
                        db.updateFiltro(1,3,spinner.getSelectedItemPosition(),c.getInt(3));

                        break;
                }
                update();
                c.close();db.close();
                pop.dismiss();
            }
        });
        rad2.check((rad2.getChildAt(c.getInt(3))).getId());
        rad2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rad_di:
                        db.updateFiltro(1,c.getInt(1),c.getInt(2),0);
                        break;
                    case R.id.rad_ag:
                        db.updateFiltro(1,c.getInt(1),c.getInt(2),1);

                        break;
                    case R.id.rad_di_ag:
                        db.updateFiltro(1,c.getInt(1),c.getInt(2),2);

                        break;
                }
                update();
                c.close();db.close();
                pop.dismiss();
            }
        });

        pop.show();
    }
    private int getfiltro(){
        int a=-1;
        DbAdapterDiario db =new DbAdapterDiario(mcontext);
        db.open();
        Cursor c = db.fetchfiltro();
        if (c.moveToFirst()){
            a=c.getInt(3);
        }
        c.close();db.close();
        return a;

    }

    public void update(){
        switchContent(R.id.frameContainer,new Frag_Diario_list(),"list");
        /*
        dati.clear();
        dati=new GetDatabase(getActivity()).getDati();
        adapter.update(dati);
        */

    }

    public void switchContent(int id, Fragment fragment,String tag) {

        if (mcontext == null)
            return;
        if (mcontext instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) mcontext;
            mainActivity.switchContent(id, fragment,tag);
        }

    }
    public void ripristino(){

        if (selez) {
            actionDell.setVisibility(View.INVISIBLE);
            actionsMenu.setVisibility(View.VISIBLE);
            actionsMenu.startAnimation(fan_menu_bounce);
            actinbar.setAnimation(out);
            actinbar.setVisibility(View.INVISIBLE);
            sel.setSelected(false);
            selez = false;
            adapter.selezi = false;
        }
    }

    @Override
    public void onLongClick(View view,int position, String data) {

            if (!selez) {
                posizione=position;
                sel=view;
                actionsMenu.setVisibility(View.INVISIBLE);
                actionDell.setVisibility(View.VISIBLE);
                actionDell.startAnimation(bounce);
                actinbar.setVisibility(View.VISIBLE);
                actinbar.setAnimation(in);
                view.setSelected(true);
                selez = true;
                adapter.selezi=true;
                this.data=data;

            }

        }

    public void setOverlay(int colorResurce) {

        View overlayView = new View(getContext());
        overlayView.setBackgroundResource(colorResurce);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        overlayView.setLayoutParams(params);
        overlayView.setVisibility(View.GONE);
        overlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay.setVisibility(View.GONE);
                actionsMenu.toggle();
            }
        });
        ViewGroup parent = ((ViewGroup) actionsMenu.getParent());
        parent.addView(overlayView, parent.indexOfChild(actionsMenu));
        this.overlay = overlayView;
    }

    @Override
    public void onPause() {

        super.onPause();
        if (input.showSoftInput(edit,InputMethodManager.SHOW_IMPLICIT)){
            input.hideSoftInputFromWindow(edit.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


}
