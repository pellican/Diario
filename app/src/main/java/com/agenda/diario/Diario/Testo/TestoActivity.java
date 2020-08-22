package com.agenda.diario.Diario.Testo;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.agenda.diario.Database.DbAdapterDiario;
import com.agenda.diario.Diario.Frammento_Diario;
import com.agenda.diario.MainActivity;
import com.agenda.diario.R;
import com.agenda.diario.Singleton;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;



public class TestoActivity extends AppCompatActivity {
    ImageView ind, ok,texsett,imcolore;
    CheckBox cornice,grassetto,corsivo;
    String[] fonts ={
            "font/f1.ttf",
            "font/f2.ttf",
            "font/f3.ttf",
            "font/f4.ttf",
            "font/f5.ttf",
            "font/f6.ttf",
            "font/f7.ttf",
            "font/f8.ttf",
            "font/f9.ttf",
            "font/f10.ttf",
            "font/f11.ttf",
    };




    SeekBar seekBar;
    Spinner spinner;
    String[] testo;
    String data;
    int tipo=0;
    long[] id;
    int pos;
    TextView tdim;
    EditText editext;
    Bundle intent;
    RelativeLayout vista_sett;
    Animation in,out;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_testo);
        tdim= findViewById(R.id.agrande);
        ind = findViewById(R.id.imageVtexind);
        ok = findViewById(R.id.imageVtexok);
        editext = findViewById(R.id.editText);
        texsett= findViewById(R.id.ima_taxt_sett);
        vista_sett= findViewById(R.id.vista_sett_text);
        in= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.enter_to_top);
        out= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.exit_to_top);
        seekBar= findViewById(R.id.seekbar_text);

        imcolore= findViewById(R.id.imacolore);
        imcolore.setBackgroundColor(Color.parseColor("#000000"));
        cornice= findViewById(R.id.checkcornice);
        grassetto= findViewById(R.id.checkgrasset);
        corsivo= findViewById(R.id.checkitalic);
        spinner= findViewById(R.id.spinfont);


        final Typeface[] typefaces = new TypeFace(this).gettypefaces();


         dialog = new dialogo(this);


        Adapter_font adapter_font = new Adapter_font(TestoActivity.this,R.layout.item_font,typefaces);
        spinner.setAdapter(adapter_font);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                editext.setTypeface(typefaces[i],tipo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        intent = getIntent().getExtras();

        if (intent != null) {


            data = intent.getString("datatesto");
            pos = intent.getInt("posilist", 0);
            DbAdapterDiario db = new DbAdapterDiario(getApplication());
            db.open();
            Cursor c = db.fetchTestoByData(data);
            testo = new String[c.getCount()];
            id = new long[c.getCount()];
            String[] color = new String[c.getCount()];
            int[] corn = new int[c.getCount()];
            int[] tip = new int[c.getCount()];
            int[] carat = new int[c.getCount()];
            int[] dim =new int[c.getCount()];
            if (c.getCount() != 0 && c.moveToFirst()) {
                for (int a = 0; a < c.getCount(); a++) {
                    id[a] = c.getLong(0);
                    testo[a] = c.getString(2);
                    color[a] = c.getString(3);
                    corn[a] = c.getInt(4);
                    tip[a] = c.getInt(5);
                    carat[a] = c.getInt(6);
                    dim[a] = c.getInt(7);
                    c.moveToNext();
                }
            }
            c.close();
            db.close();
            editext.setText(testo[pos]);
            editext.setSelection(editext.getText().length());
            editext.setTextColor(Color.parseColor(color[pos]));
            imcolore.setBackgroundColor(Color.parseColor(color[pos]));
            editext.setTextSize(dim[pos]);
            seekBar.setProgress(dim[pos]);
            tdim.setText(""+dim[pos]);
            if (corn[pos] == 0){
                editext.setBackground(null);
                cornice.setChecked(false);
            }else{
                editext.setBackground(getResources().getDrawable(R.drawable.angoli));
                editext.setPadding(6,0,6,3);
                cornice.setChecked(true);
            }
            spinner.setSelection(carat[pos]);
            switch (tip[pos]){
                case 0: editext.setTypeface(typefaces[spinner.getSelectedItemPosition()],0);
                        tipo=0;
                        break;
                case 1: editext.setTypeface(typefaces[spinner.getSelectedItemPosition()],1);
                        grassetto.setChecked(true);
                        tipo=1;
                        break;
                case 2: editext.setTypeface(typefaces[spinner.getSelectedItemPosition()],2);
                        corsivo.setChecked(true);
                        tipo=2;
                        break;
                case 3: editext.setTypeface(typefaces[spinner.getSelectedItemPosition()],3);
                        grassetto.setChecked(true);corsivo.setChecked(true);
                        tipo=3;

            }
           final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideSoftKeyboard(TestoActivity.this);
                    showVista(vista_sett);
                    editext.setCursorVisible(false);
                }
            },500);
        }



        editext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideVista(vista_sett);

                editext.setCursorVisible(true);

            }
        });

        ind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DbAdapterDiario db = new DbAdapterDiario(getApplication());
                db.open();
                ColorDrawable drawable = (ColorDrawable) imcolore.getBackground();
                String col =String.format("#%06X",drawable.getColor());
                int corn= cornice.isChecked() ? 1:0;
                if (intent == null) {

                    db.createContactTesto(Singleton.getInstance().getDataS(), editext.getText().toString(),
                           col ,corn,tipo,spinner.getSelectedItemPosition(),seekBar.getProgress());
                    db.close();
                    finish();
                } else {
                    db.updateTesto(id[pos], data, editext.getText().toString(),col,
                            corn,tipo,spinner.getSelectedItemPosition(),seekBar.getProgress());
                    db.close();
                    finish();
                }

            }
        });
        texsett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vistaSett(vista_sett);
                editext.setCursorVisible(false);
                hideSoftKeyboard(TestoActivity.this);


            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                editext.setTextSize(i);
                tdim.setText(""+i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        imcolore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  dialog();


                dialog.show();


            }
        });
        cornice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    editext.setBackground(getResources().getDrawable(R.drawable.angoli));
                    editext.setPadding(6,0,6,3);
                }else {
                    editext.setBackground(null);
                }
            }
        });
        grassetto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    if (corsivo.isChecked()){
                        editext.setTypeface(typefaces[spinner.getSelectedItemPosition()],Typeface.BOLD_ITALIC);
                        tipo=3;
                    }else{
                        editext.setTypeface(typefaces[spinner.getSelectedItemPosition()],Typeface.BOLD);
                        tipo=1;
                    }
                }else {
                    if(corsivo.isChecked()){
                        editext.setTypeface(typefaces[spinner.getSelectedItemPosition()],Typeface.ITALIC);
                        tipo=2;
                    }else{
                        editext.setTypeface(typefaces[spinner.getSelectedItemPosition()],Typeface.NORMAL);
                        tipo=0;
                    }
                }
            }
        });
        corsivo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    if (grassetto.isChecked()){
                        editext.setTypeface(typefaces[spinner.getSelectedItemPosition()],Typeface.BOLD_ITALIC);
                        tipo=3;
                    }else{
                        editext.setTypeface(typefaces[spinner.getSelectedItemPosition()],Typeface.ITALIC);
                        tipo=2;
                    }
                }else{
                    if (grassetto.isChecked()){
                        editext.setTypeface(typefaces[spinner.getSelectedItemPosition()],Typeface.BOLD);
                        tipo=1;
                    }else{
                        editext.setTypeface(typefaces[spinner.getSelectedItemPosition()],Typeface.NORMAL);
                        tipo=0;
                    }
                }
            }
        });
    }



    private void vistaSett(final View vista){

        if (vista.getVisibility()== View.INVISIBLE){
            showVista(vista);
        }else {
            hideVista(vista);
        }


    }
    private void showVista(View vista){
        if (vista.getVisibility() == View.INVISIBLE){
            vista.setVisibility(View.VISIBLE);
            vista.startAnimation(in);
        }
    }
    private void hideVista(View vista){
        if(vista.getVisibility() == View.VISIBLE) {
            vista.startAnimation(out);
            vista.setVisibility(View.INVISIBLE);
        }
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view=activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }
    public void showSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }

    }

    public class dialogo extends Dialog {
        final String[] color={
                "#ff000000",
                "#ffff00ff",
                "#ff0000ff",
                "#ffff0000",
                "#ff00ff00",
                "#ffffff00"

        };
        ColorPicker colorPicker;
        GridView gridView;
        TextView ok,ann;
        int oldColor;
        Context context;
        public dialogo(@NonNull Context context) {
            super(context);
            this.context=context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_colore_testo);
            gridView= findViewById(R.id.gridcolore);
            colorPicker= findViewById(R.id.picker_testo);
            colorPicker.addSaturationBar((SaturationBar) findViewById(R.id.svbartest));
            colorPicker.addOpacityBar((OpacityBar)findViewById(R.id.opacitybar_test));
            ColorDrawable drawable = (ColorDrawable) imcolore.getBackground();
            colorPicker.setOldCenterColor(drawable.getColor());
            oldColor = colorPicker.getColor();
            Color_adapter adapter = new Color_adapter(context,R.layout.item_colore,color);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    colorPicker.setColor(Color.parseColor(color[i]));

                }
            });
            ok= findViewById(R.id.ok_colore_testo);
            ann= findViewById(R.id.ann_colore_testo);
            ann.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (colorPicker.getColor() != oldColor){
                        colorPicker.setOldCenterColor(colorPicker.getColor());
                        oldColor=colorPicker.getColor();
                    }
                    imcolore.setBackgroundColor(colorPicker.getColor());
                    editext.setTextColor(colorPicker.getColor());
                    dialog.dismiss();
                }
            });



        }


    }












}