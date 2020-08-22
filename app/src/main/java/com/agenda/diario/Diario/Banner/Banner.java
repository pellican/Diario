package com.agenda.diario.Diario.Banner;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.agenda.diario.Database.DbAdapterDiario;
import com.agenda.diario.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;


/**
 * Created by portatile on 12/06/2016.
 */
public class Banner extends AppCompatActivity {
    private AdView adView;
    String data;
    int sfondo,emoti,cat;
    String tutte;
    TextView err;
    Button annulla;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.banner_layout);
        err= findViewById(R.id.texterr);
        Intent intent=getIntent();
        tutte=intent.getStringExtra("tutti");
        data=intent.getStringExtra("data");
        sfondo=intent.getIntExtra("sfondo",0);
        emoti=intent.getIntExtra("emoti",0);
        cat=intent.getIntExtra("cat",-1);
        MobileAds.initialize(getApplicationContext(),"ca-app-pub-8361211670031398~2956205263");
        adView= findViewById(R.id.banner);
        AdRequest adRequest = new AdRequest.Builder()

                .addTestDevice("0ACF3B4D4A213B7F2C87CF5C979471A2")
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();


                DbAdapterDiario db=new DbAdapterDiario(getApplicationContext());
                db.open();
                Cursor c=db.fetchPaginaByData(data);
                if (c.getCount()!=0){
                    if (c.moveToFirst()) {
                        if (sfondo != 0) {
                            if (tutte != null){
                                db.updateAllSfondo(sfondo);
                            }else {
                                db.updateContactPagina(c.getLong(0), c.getString(1), c.getString(2), c.getString(3), sfondo, c.getInt(5), c.getString(6), c.getString(7));
                            }
                        }else if (emoti != 0){
                            db.updateContactPagina(c.getLong(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4), emoti,c.getString(6),c.getString(7));
                        }else  if (cat != -1){
                            db.updateContactPagina(c.getLong(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4),c.getInt(5),Integer.toString(cat),c.getString(7));
                        }
                    }
                }
                c.close();db.close();
                finish();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                err.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                Toast.makeText(getApplication(),"left",Toast.LENGTH_SHORT).show();
            }





        });
        adView.loadAd(adRequest);
        annulla= findViewById(R.id.buttann);
        annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }


    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

}
