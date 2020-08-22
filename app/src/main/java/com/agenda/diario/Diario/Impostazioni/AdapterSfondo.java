package com.agenda.diario.Diario.Impostazioni;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.agenda.diario.R;
import com.bumptech.glide.Glide;

import java.util.Arrays;

/**
 * Created by portatile on 01/06/2016.
 */
public class AdapterSfondo extends BaseAdapter {

    Context mcontext;
    int[] idsfondo;
    boolean[] checkarr;


    ClickSfondo clickSfondo;
    public AdapterSfondo(Context context, int[] idsfondo) {
        mcontext=context;

        this.idsfondo=idsfondo;
        checkarr= new boolean[idsfondo.length];
    }

    @Override
    public int getCount() {
        return idsfondo.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView fotoView;
        final CheckBox checkBox;

        if (view == null) {
            LayoutInflater vi = (LayoutInflater)mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_sfondo, null);

        }

        checkBox= v.findViewById(R.id.checkboxsfo);
        checkBox.setChecked(checkarr[position]);
        fotoView= v.findViewById(R.id.imagesfondo);
       // String as = "assets://a1ass.png";
       // Uri uri= Uri.parse(as);
       // Glide.with(mcontext).load(as).into(fotoView);
       // fotoView.setImageResource(idsfondo[position]);
        Glide.with(mcontext).load(idsfondo[position]).into(fotoView);
        fotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkarr[position]){
                    Arrays.fill(checkarr,false);
                    checkarr[position]=true;

                    clickSfondo.clicksfondo(position);
                    notifyDataSetChanged();
                }else if (checkarr[position]){
                    checkarr[position]=false;

                    notifyDataSetChanged();
                }
            }
        });

        return v;
    }
    public void setClickSfondo(ClickSfondo click){
        clickSfondo=click;
    }

}
