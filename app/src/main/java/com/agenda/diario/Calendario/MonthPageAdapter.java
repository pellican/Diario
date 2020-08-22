package com.agenda.diario.Calendario;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;


public class MonthPageAdapter extends FragmentPagerAdapter {
    Calend_Framm[] fragList;
    Calend_Framm temp;
    int fragNumber = 3;

    public MonthPageAdapter(FragmentManager fm, Calend_Framm[] fragList) {
        super(fm);
        this.fragList = fragList;

    }

    @Override
    public int getItemPosition(Object object) {
        // TODO Auto-generated method stub
        return POSITION_NONE;
    }
    @Override
    public Fragment getItem(int position) {
        return fragList[position];
    }

    @Override
    public int getCount() {
        return 3;
    }



    public void setCalendar(Calendar currentMonth) {

        Calendar prevMonth = Calendar.getInstance();
        prevMonth.setTime(currentMonth.getTime());
        prevMonth.add(Calendar.MONTH, -1);

        Calendar nextMonth = Calendar.getInstance();
        nextMonth.setTime(currentMonth.getTime());
        nextMonth.add(Calendar.MONTH, 1);
        //update all 3 fragments
        fragList[0].updateUI(prevMonth);
        fragList[1].updateUI(currentMonth);
        fragList[2].updateUI(nextMonth);

    }
}
