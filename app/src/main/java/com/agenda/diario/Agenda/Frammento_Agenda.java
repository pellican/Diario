package com.agenda.diario.Agenda;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.agenda.diario.Database.DbAdapterDiario;
import com.agenda.diario.R;
import com.agenda.diario.Singleton;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

/**
 * Created by portatile on 27/12/2015.
 */
public class Frammento_Agenda extends Fragment {
    private static int RESULT_LOAD_IMAGE = 1;

    FloatingActionButton fab;
    private Handler mHandler = new Handler();
    public Calendar currentDay;
    public ViewPager agendaPager;
    public Agenda_Framm[] agenda_framms;
    public AgendaPageAdapter adapter;
    public int preday,today,nextday;
    public int focusPage;
    private static final int PAGE_CENTER=1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.tolbar_agenda));
        }
    }
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View ageView = inflater.inflate(R.layout.frammento_agenda, container, false);
        currentDay=Calendar.getInstance();

        if (Singleton.getInstance().getData() != null){
            currentDay=Singleton.getInstance().getData();
            Singleton.getInstance().setData(null);
        }







        fab = ageView.findViewById(R.id.fabag);
        Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.bounce);
        fab.startAnimation(animation);
        agendaPager = ageView.findViewById(R.id.paperAgenda);
        agenda_framms= new Agenda_Framm[3];
        agenda_framms[0]= Agenda_Framm.newInstance(preday);
        agenda_framms[1]= Agenda_Framm.newInstance(today);
        agenda_framms[2]= Agenda_Framm.newInstance(nextday);
        adapter=new AgendaPageAdapter(getChildFragmentManager(),agenda_framms);
        agendaPager.setAdapter(adapter);
        agendaPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
                        currentDay.add(Calendar.DAY_OF_MONTH, -1);
                    } else if (focusPage > PAGE_CENTER) {
                        currentDay.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    adapter.setAgenda(currentDay);
                    agendaPager.setCurrentItem(1, false);
                }

            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.setAgenda(currentDay);
            }
        }, 200);

        agendaPager.setCurrentItem(1, false);

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),Dialog_agenda.class);

                intent.putExtra("data",agenda_framms[1].dataS);
                intent.putExtra("ora",-1);
                getActivity().startActivity(intent);
            }

        });

        return  ageView;
    }


    @Override
    public void onResume() {
        super.onResume();
        adapter.setAgenda(currentDay);

    }

}
