package com.agenda.diario.Diario_list;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.agenda.diario.Agenda.Frammento_Agenda;
import com.agenda.diario.Database.Database;
import com.agenda.diario.Diario.Frammento_Diario;
import com.agenda.diario.MainActivity;
import com.agenda.diario.R;
import com.agenda.diario.Singleton;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by portatile on 15/05/2016.
 */
public class AdapterList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mcontext;

    ArrayList<Database> list;


    private Calendar calendar;
    private int lastPosition=0;
    private ItemLongClickListener listener;
    boolean selezi=false;


     AdapterList(Context c,ArrayList<Database> list) {
        this.list=list;
        calendar = Calendar.getInstance();
        mcontext=c;

    }
    public void update(ArrayList<Database> list){

        this.list=list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if (list == null){
            return 0;
        }
        return  list.size();
        // return date.length;
    }

    @Override
    public int getItemViewType(int position) {
        if (list != null){
            Database oggeto = list.get(position);
            if (oggeto != null){
                return oggeto.getVista();
            }
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        switch (viewType){

            case Database.DIARIO:
               itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_recicle_diario, parent, false);
                setAnimation(itemView, lastPosition,3);
                return new DiarioHolder(itemView);
            case Database.AGENDA:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_recicle_agenda,parent,false);
                setAnimation(itemView, lastPosition,5);
                return new AgendaHolder(itemView);
            case Database.INSIEME:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_recicle_insieme,parent,false);
                setAnimation(itemView, lastPosition,3);
                return new InsiemeHolder(itemView);
        }

        return null;
    }



    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final Database oggeto = list.get(position);
        lastPosition=holder.getAdapterPosition();
        if (oggeto != null){

            switch (oggeto.getVista()){
                case Database.DIARIO:

                   // ((DiarioHolder) holder).dataT.setText(oggeto.getData());
                    ((DiarioHolder) holder).testoT.setText(oggeto.getTesto());

                    Glide.with(mcontext)
                            .load(oggeto.getFoto())
                            .error(null)
                            .into(((DiarioHolder) holder).image);

        /*
        if (foto[position] != null) {
            Uri uri = Uri.parse(foto[position]);
           Glide.with(mcontext).load(foto[position]).into(holder.image);

        }
        */
                    ((DiarioHolder) holder).iemoti.setImageResource(oggeto.getEmoti());
                    ((DiarioHolder) holder).catT.setText(oggeto.getCategoria());
                    if (oggeto.getTitolo() == null ){
                        ((DiarioHolder) holder).data_T.setText(oggeto.getData());
                    }else {
                        ((DiarioHolder) holder).titolo.setText(oggeto.getTitolo());
                        ((DiarioHolder) holder).data_T.setText(oggeto.getData());
                    }
                    break;
                case Database.AGENDA:
//                        int pos=position-date.length;
                        ((AgendaHolder) holder).dataA.setText(oggeto.getData());
                         ((AgendaHolder) holder).oraA.setText(oggeto.getOra());
                          ((AgendaHolder) holder).testoA.setText(oggeto.getTestoAg());
                    if (oggeto.getNotifica()!= null && oggeto.getNotifica().equals("0")){
                        ((AgendaHolder) holder).notA.setImageResource(R.drawable.ic_notif);
                    }

                    break;
                case Database.INSIEME:
                    ((InsiemeHolder) holder).testoT.setText(oggeto.getTesto());
                    Glide.with(mcontext).load(oggeto.getFoto()).error(null).into(((InsiemeHolder) holder).image);

        /*
        if (foto[position] != null) {
            Uri uri = Uri.parse(foto[position]);
           Glide.with(mcontext).load(foto[position]).into(holder.image);

        }
        */
                    ((InsiemeHolder) holder).iemoti.setImageResource(oggeto.getEmoti());
                    ((InsiemeHolder) holder).catT.setText(oggeto.getCategoria());
                    if (oggeto.getTitolo() == null ){
                        ((InsiemeHolder) holder).data_T.setText(oggeto.getData());
                    }else {
                        ((InsiemeHolder) holder).titolo.setText(oggeto.getTitolo());
                        ((InsiemeHolder) holder).data_T.setText(oggeto.getData());
                    }
                    ((InsiemeHolder) holder).oraA.setText(oggeto.getOra());
                    ((InsiemeHolder) holder).testoA.setText(oggeto.getTestoAg());
                    if (oggeto.getNotifica().equals("0")){
                        ((InsiemeHolder) holder).notA.setImageResource(R.drawable.ic_notif);
                    }
                    break;
            }
        }

    }



    public void setClickListener(ItemLongClickListener itemClickListener) {
        this.listener = itemClickListener;
    }

    private void switchContent(int id, Fragment fragment, String tag) {
        if (mcontext == null)
            return;
        if (mcontext instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) mcontext;
            mainActivity.switchContent(id, fragment,tag);
        }

    }
    public void remove(int posizione){
        notifyItemRemoved(posizione);
        notifyItemRangeChanged(posizione,getItemCount());
    }
    public void setAnimation(View view, int position,int n)
    {if (position < n){
        view.animate().cancel();
        view.setTranslationY(100);
        view.setAlpha(0);
        view.animate().alpha(1.0f).translationY(0).setDuration(300).setStartDelay(position * 100);
    }

    }
    public class InsiemeHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        TextView titolo,testoT,catT,data_T,testoA,oraA;
        ImageView image,iemoti,notA;

        public InsiemeHolder(View view){
            super(view);
            titolo= view.findViewById(R.id.texdatalistins);
            testoT= view.findViewById(R.id.textestolistins);
            image= view.findViewById(R.id.imagelistitemins);
            iemoti= view.findViewById(R.id.emotilistins);
            catT= view.findViewById(R.id.textcatlistins);
            data_T= view.findViewById(R.id.textdata_ins);
            oraA= view.findViewById(R.id.textoraagdi);
            notA= view.findViewById(R.id.imagenotins);
            testoA= view.findViewById(R.id.texttestoag);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (!selezi) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                try {
                    Database oggeto = list.get(getAdapterPosition());
                    calendar.setTime(dateFormat.parse(oggeto.getData()));
                } catch (ParseException e) {
                }
                Singleton.getInstance().setData(calendar);
                Fragment mFragment = new Frammento_Diario();
                switchContent(R.id.frameContainer, mFragment,"diario");
            }
        }

        @Override
        public boolean onLongClick(View view) {
            Database oggeto = list.get(getAdapterPosition());
            if (listener != null){listener.onLongClick(view,getAdapterPosition(),oggeto.getData());}
            return true;
        }
    }

    public class AgendaHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView dataA,oraA,testoA;
        ImageView notA;
        public AgendaHolder(View itemView) {
            super(itemView);
            dataA= itemView.findViewById(R.id.texdatalistage);
            oraA= itemView.findViewById(R.id.textoraag);
            testoA= itemView.findViewById(R.id.texttestoagen);
            notA= itemView.findViewById(R.id.imagenotagen);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            try {
                Database oggeto = list.get(getAdapterPosition());
                calendar.setTime(dateFormat.parse(oggeto.getData()));
            } catch (ParseException e) {
            }
            Singleton.getInstance().setData(calendar);
            Fragment mFragment = new Frammento_Agenda();
            switchContent(R.id.frameContainer, mFragment,"diario");
        }
    }


    public   class DiarioHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        TextView titolo,testoT,catT,data_T;
        ImageView image,iemoti;
        View container;


        public DiarioHolder(View view) {
            super(view);
            container=view;
            titolo= view.findViewById(R.id.texdatalist);
            testoT= view.findViewById(R.id.textestolist);
            image= view.findViewById(R.id.imagelistitem);
            iemoti= view.findViewById(R.id.emotilist);
            catT= view.findViewById(R.id.textcatlist);
            data_T= view.findViewById(R.id.textdata_);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (!selezi) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                try {
                    Database oggeto = list.get(getAdapterPosition());
                    calendar.setTime(dateFormat.parse(oggeto.getData()));
                } catch (ParseException e) {
                }
                Singleton.getInstance().setData(calendar);
                Fragment mFragment = new Frammento_Diario();
                switchContent(R.id.frameContainer, mFragment,"diario");
            }
        }
        @Override
        public boolean onLongClick(View view) {
            Database oggeto = list.get(getAdapterPosition());
            if (listener != null){listener.onLongClick(view,getAdapterPosition(),oggeto.getData());}
            return true;
        }
    }



}