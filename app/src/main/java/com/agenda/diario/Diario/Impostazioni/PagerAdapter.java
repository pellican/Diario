package com.agenda.diario.Diario.Impostazioni;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by portatile on 01/06/2016.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    String data;
    public PagerAdapter(FragmentManager fm, int NumOfTabs,String data) {
        super(fm);
        this.data=data;
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Tab_sfondo tab1 = new Tab_sfondo();
                Bundle sfo=new Bundle();
                sfo.putString("data",data);
                tab1.setArguments(sfo);
                return tab1;
            case 1:
                Tab_emotion tab2 = new Tab_emotion();
                Bundle emo=new Bundle();
                emo.putString("data",data);
                tab2.setArguments(emo);
                return tab2;
            case 2:
                Tab_tag tab3 = new Tab_tag();
                Bundle tag= new Bundle();
                tag.putString("data",data);
                tab3.setArguments(tag);
                return tab3;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
