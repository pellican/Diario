package com.agenda.diario.Agenda;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.Calendar;

/**
 * Created by portatile on 30/12/2015.
 */
public class AgendaPageAdapter extends FragmentStatePagerAdapter {
        Agenda_Framm[] agenda_framms;


    public AgendaPageAdapter(FragmentManager fm, Agenda_Framm[] agenda_framms) {
        super(fm);
        this.agenda_framms=agenda_framms;


    }
    @Override
    public int getItemPosition(Object object) {
        // TODO Auto-generated method stub
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return agenda_framms[position];
    }

    @Override
    public int getCount() {
        return 3;
    }
    public void setAgenda(Calendar currentDay) {

        Calendar prevday = Calendar.getInstance();
        prevday.setTime(currentDay.getTime());
        prevday.add(Calendar.DAY_OF_MONTH, -1);

        Calendar nextday = Calendar.getInstance();
        nextday.setTime(currentDay.getTime());
        nextday.add(Calendar.DAY_OF_MONTH, 1);
       //update all 3 fragments
        agenda_framms[0].updateUI(prevday);
        agenda_framms[1].updateUI(currentDay);
        agenda_framms[2].updateUI(nextday);

    }
}
