package com.agenda.diario.SlideIntro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agenda.diario.GoogleDrive.DriveLogIn;
import com.agenda.diario.GoogleDrive.DriveSyncdb;
import com.agenda.diario.Impost.Impost_Activity;
import com.agenda.diario.MainActivity;
import com.agenda.diario.R;
import com.agenda.diario.Util.Costanti;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SlideIntro extends AppCompatActivity {
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final int REQUEST_CODE_SIGN_IN = 4;
    private RelativeLayout relbott;
    private boolean start = false;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MyViewPagerAdapter pagerAdapter;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(Costanti.PREFS_NAME, MODE_MULTI_PROCESS);
        editor = prefs.edit();

        if (!isFirstTimeLaunch()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_slide);

        viewPager = findViewById(R.id.pager_slide);
        relbott = findViewById(R.id.relbotto);
        btnSkip = findViewById(R.id.salta_slide);
        btnNext = findViewById(R.id.avanti_slide);
        tabLayout = findViewById(R.id.tabslaid);
        layouts = new int[]{
                R.layout.slide_1,
                R.layout.slide_2,
                R.layout.slide_3,
                R.layout.slide_4,
                R.layout.slide_5,
        };
        pagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager, true);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 4) {
                    btnNext.setText("Login");
                    btnSkip.setVisibility(View.VISIBLE);
                    start = true;
                } else {
                    btnNext.setText("Avanti");
                    btnSkip.setVisibility(View.INVISIBLE);
                    start = false;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avvio();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!start) {
                    int pagina = viewPager.getCurrentItem();
                    viewPager.setCurrentItem(pagina + 1);
                } else {
                    // avvio();
                    new DriveLogIn(SlideIntro.this).signVerIn();
                }
            }
        });

    }
    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return prefs.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    private void avvio(){
        setFirstTimeLaunch(false);
        startActivity(new Intent(SlideIntro.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN ){
            if (resultCode == RESULT_OK){
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                GoogleSignInAccount account = result.getSignInAccount();
                prefs.edit().putString("user", account.getDisplayName())
                        .putString("email", account.getEmail())
                        .putBoolean("accaunt",true)
                        .putBoolean("disc", true).apply();
                if (account.getPhotoUrl() != null) {
                    prefs.edit().putString("logo", account.getPhotoUrl().toString()).apply();
                }
               // new DriveSyncdb(this,account).cercaDriveFile();
                avvio();

            }else{
                prefs.edit().putBoolean("disc",false).apply();
                if (!isConnected()){
                    float heigt = 55 * getResources().getDisplayMetrics().density;
                    Style style = new Style.Builder()
                            .setBackgroundColor(R.color.alert)
                            .setHeight((int)heigt).build();
                    Crouton.makeText(this,R.string.errore_connessione, style).show();
                }


            }
        }
    }

    public boolean isConnected() {
        ConnectivityManager cm =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

    }

}
