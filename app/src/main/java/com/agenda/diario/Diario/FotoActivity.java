package com.agenda.diario.Diario;

import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agenda.diario.Database.DbAdapterDiario;
import com.agenda.diario.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FotoActivity extends AppCompatActivity implements FotoClick {
    ImageView indietro;
    ViewPager fotopager;
    FotoAdapter adapter;
    String[] foto,tag;
    RelativeLayout fotobar;
    TextView texdata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_foto);
        fotopager= findViewById(R.id.fotopager);
        texdata= findViewById(R.id.datafoto);
        indietro= findViewById(R.id.indietro);
        fotobar= findViewById(R.id.fotobar);

        Intent i = getIntent();
        String data=i.getExtras().getString("data");
        int pos=i.getIntExtra("pos",0);
        DbAdapterDiario db=new DbAdapterDiario(this);
        db.open();
        Cursor c = db.fetchFotoByData(data);
            tag=new String[c.getCount()];
            foto= new String[c.getCount()];
            if (c.moveToFirst()){
                for (int a = 0;a < c.getCount();a++){
                    foto[a]=c.getString(2);
                    tag[a]=c.getString(3);
                    c.moveToNext();
                }
            }
         c.close();
        db.close();


        adapter= new FotoAdapter(this,foto,tag);
        fotopager.setAdapter(adapter);
        fotopager.setCurrentItem(pos);
        adapter.setFotoClick(this);
        Dettagli(foto[pos]);

        fotopager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (tag[position].equals("image")){
                    Dettagli(foto[position]);
                }else {
                    texdata.setText(null);
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        indietro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void Dettagli(String foto){
        try {
            /*
            Uri uri = Uri.parse(foto);
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = managedQuery(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String phat= cursor.getString(column_index);
            Log.d("dett",phat);
            */

          //  ExifInterface exif = new ExifInterface(phat);
            Log.d("dett",foto);
            ExifInterface exif = new ExifInterface(foto);
            String data= exif.getAttribute(ExifInterface.TAG_DATETIME);
           SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd kk:mm:ss", Locale.getDefault());
           SimpleDateFormat convertDate = new SimpleDateFormat("dd MMMM yyyy",Locale.getDefault());
           SimpleDateFormat convertTime = new SimpleDateFormat("kk:mm",Locale.getDefault());
            Date d,d2;
            d = simpleDateFormat.parse(data);
            d2 = simpleDateFormat.parse(data);
           String dateFormat = convertDate.format(d);
           String timeFormat = convertTime.format(d2);
           String setdata= dateFormat+"   "+timeFormat;
            texdata.setText(setdata);

        }catch (Exception e){
            texdata.setText(null);
        }

    }

    @Override
    public void onFotoClick() {
        if (fotobar.getTranslationY() == 0){
            fotobar.animate().translationY(-200).setDuration(200).start();
        }else{
            fotobar.animate().translationYBy(-200).translationY(0).setDuration(200).start();
        }
    }
}
