package com.agenda.diario.Diario;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.agenda.diario.CustomView.CustomEditText;
import com.agenda.diario.Database.DbAdapterDiario;
import com.agenda.diario.Diario.Testo.TestoActivity;
import com.agenda.diario.Diario.Testo.TypeFace;
import com.agenda.diario.MainActivity;
import com.agenda.diario.R;

/**
 * Created by portatile on 07/02/2016.
 */
public class AdapterListview extends BaseAdapter {
    private String[] testi;
    private String[] color;
    private int[] corn;
    private int[] tip ;
    private int[] carat ;
    private int[] dim;
    private Typeface[] typeface;
    private Context mcontext;
    private String data;

    public AdapterListview(Context context,String data) {
      mcontext=context;
        this.data=data;
        typeface = new TypeFace(context).gettypefaces();
        getData();
    }

    @Override
    public int getCount() {
        return testi.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        TextView textView;

        if (view == null) {
            LayoutInflater vi = (LayoutInflater)mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           v = vi.inflate(R.layout.item_testo, null);

        }
        textView= v.findViewById(R.id.textVitemDiario);
        textView.setText(testi[i]);
        textView.setTextColor(Color.parseColor(color[i]));
        if (corn[i] == 0){
            textView.setBackground(null);
        }else{
            textView.setBackground(mcontext.getResources().getDrawable(R.drawable.angoli));
            textView.setPadding(6,0,6,3);
        }
        textView.setTypeface(typeface[carat[i]],tip[i]);
        textView.setTextSize(dim[i]);

        return v;
    }
    public void getData(){
        DbAdapterDiario db = new DbAdapterDiario(mcontext);
        db.open();
        Cursor c =db.fetchTestoByData(data);
        if (c.getCount() != 0 && c.moveToFirst()){
            testi= new String[c.getCount()];
            color = new String[c.getCount()];
            corn = new int[c.getCount()];
            tip = new int[c.getCount()];
            carat = new int[c.getCount()];
            dim =new int[c.getCount()];
            for (int i=0;i<c.getCount();i++){
                testi[i]=c.getString(2);
                color[i] = c.getString(3);
                corn[i] = c.getInt(4);
                tip[i] = c.getInt(5);
                carat[i] = c.getInt(6);
                dim[i] = c.getInt(7);
                c.moveToNext();
            }
        }else {
            testi=new String[0];
            color=new String[0];
            corn=new int[0];
            tip=new int[0];
            carat=new int[0];
            dim=new int[0];
        }
        c.close();
        db.close();

    }


}
