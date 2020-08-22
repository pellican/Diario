package com.agenda.diario.Diario.Impostazioni;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.agenda.diario.CustomView.CustomViewPager;
import com.agenda.diario.R;

/**
 * Created by portatile on 01/06/2016.
 */
public class ImpostaActivity extends AppCompatActivity {
    CustomViewPager impostapager;
    TextView titolo;
    String data;
    int pos;
    Bundle intent;
    ImageView ind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impost_pagina);
        intent = getIntent().getExtras();
        if (intent!=null){
            data=intent.getString("data");
            pos=intent.getInt("pos");
        }
        titolo= findViewById(R.id.textsfondo);
        ind= findViewById(R.id.imageVsfoind);

        impostapager = findViewById(R.id.sfondopaper);
        TabLayout tabLayout = findViewById(R.id.tab);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_crop));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_emotion));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_folder));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(),data);
        impostapager.setAdapter(adapter);
        impostapager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                impostapager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        impostapager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
               switch (position){
                   case 0:{
                       titolo.setText(R.string.scegli_sfondo);
                       break;
                   }
                   case 1:{
                       titolo.setText(R.string.emotion);
                       break;
                   }
                   case 2:{
                       titolo.setText(R.string.categoria);
                       break;
                   }

                   default:
               }


            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        impostapager.setCurrentItem(pos);

        ind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

}
