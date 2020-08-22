package com.agenda.diario.Calendario;




import android.os.Build;
import android.os.Bundle;


import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.agenda.diario.Diario.Frammento_Diario;
import com.agenda.diario.R;

import java.util.Calendar;


public class Frammento_Calendario extends Fragment {
    private Calendar currentMonth;
    private Handler mHandler = new Handler();
    int month;
    int prevMonth;
    int nextMonth;
    Calend_Framm[] fragList;
    int focusPage;
    static final int PAGE_CENTER=1;
    ViewPager monthPage;
    ImageView ind;
    MonthPageAdapter monthPageAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.tolbar_calendario));
        }
    }
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View calView = inflater.inflate(R.layout.frammento_calendario, container, false);
        currentMonth=Calendar.getInstance();
        /*
        ind=(ImageView)calView.findViewById(R.id.imagecalind);
        ind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager=getFragmentManager();
                Fragment diar=new Frammento_Diario();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.abc_fade_in,R.anim.abc_fade_out)
                        .replace(R.id.frameContainer, diar).commit();
            }
        });
*/

        fragList = new Calend_Framm[3];
        fragList[0] = Calend_Framm.newInstance(prevMonth);
        fragList[1] = Calend_Framm.newInstance(month);
        fragList[2] = Calend_Framm.newInstance(nextMonth);

        monthPage = calView.findViewById(R.id.paper);
        monthPageAdapter = new MonthPageAdapter(getChildFragmentManager(), fragList);
        monthPage.setAdapter(monthPageAdapter);

        monthPage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                focusPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {

                    if (focusPage < PAGE_CENTER) {
                        currentMonth.add(Calendar.MONTH, -1);
                    } else if (focusPage > PAGE_CENTER) {
                        currentMonth.add(Calendar.MONTH, 1);
                    }
                    monthPageAdapter.setCalendar(currentMonth);
                    monthPage.setCurrentItem(1, false);
                }

            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                monthPageAdapter.setCalendar(currentMonth);
            }
        }, 200);

        monthPage.setCurrentItem(1, false);
        return calView;
    }


}
