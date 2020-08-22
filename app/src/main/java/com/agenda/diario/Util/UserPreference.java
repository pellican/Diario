package com.agenda.diario.Util;

import android.content.Context;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.agenda.diario.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import static android.content.Context.MODE_MULTI_PROCESS;
import static android.content.Context.MODE_PRIVATE;


public class UserPreference extends Preference {
    public SharedPreferences prefs;



    public UserPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public UserPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserPreference(Context context) {
        super(context);

    }

    @Override
    protected View onCreateView(ViewGroup parent) {
            super.onCreateView(parent);
            LayoutInflater li = (LayoutInflater)getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        prefs=getContext().getSharedPreferences(Costanti.PREFS_NAME,MODE_PRIVATE);
        return li.inflate( R.layout.user_layout,  parent,false);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

       final TextView usertext = view.findViewById(R.id.usertext);
       final TextView emailtext = view.findViewById(R.id.emailtext);
       final ImageView logo = view.findViewById(R.id.logouser);
       if (logo != null){
           String url = prefs.getString("logo",null);
           if (url != null){
               Glide.with(getContext()).load(url)
                       .apply(RequestOptions.circleCropTransform())
                       .into(logo);
           }

       }
        if (usertext != null) {
            usertext.setText(prefs.getString("user","user"));
        }
        if(emailtext != null){
            emailtext.setText(prefs.getString("email","email"));
        }


    }
}
