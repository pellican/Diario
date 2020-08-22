package com.agenda.diario.Note;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.widget.LinearLayout.LayoutParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.agenda.diario.CustomView.DisegnoView;
import com.agenda.diario.Database.DbAdapterDiario;
import com.agenda.diario.Diario.Frammento_Diario;
import com.agenda.diario.Diario_list.Frag_Diario_list;
import com.agenda.diario.GoogleDrive.DriveLogIn;
import com.agenda.diario.GoogleDrive.DriveSyncdb;
import com.agenda.diario.MainActivity;
import com.agenda.diario.R;
import com.agenda.diario.Singleton;
import com.agenda.diario.Util.AlphaManager;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by portatile on 27/12/2015.
 */
public class Frammento_Note extends Fragment implements OnDrawChangedListener {
    ImageView indietro,fill,pennello,gomma,undo,redo,lente,delete;
    String datamod, data,path,dispath;
    String misura,newmisura;
    long id;
    ImageView strokeImageView,eraseImageView,contagocce;
    DisegnoView disegnoView;
    private ColorPicker mColorPicker;
    private int oldColor,size;
    int newSize;
    private int seekBarStrokeProgress = 10, seekBarEraserProgress = 50;
    String base= Environment.getExternalStorageDirectory().getAbsolutePath();
    String cartella="/AgenDiario";
    String timeStamp = new SimpleDateFormat("dd-MM hhmmss", Locale.getDefault()).format(new Date());
    boolean click=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.tolbar_note));
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View not = inflater.inflate(R.layout.frammento_note, container, false);
        data = Singleton.getInstance().getDataS();
        if (data != null){
            Singleton.getInstance().setDataS(null);
        }
        Bundle bundle=getArguments();

        misura=getResources().getString(R.string.tratto);
        indietro= not.findViewById(R.id.indietro_note);
        fill= not.findViewById(R.id.fill);
        pennello= not.findViewById(R.id.pennello);
        gomma= not.findViewById(R.id.gomma);
        undo= not.findViewById(R.id.undo);
        redo= not.findViewById(R.id.redo);
        lente= not.findViewById(R.id.lente);
        delete= not.findViewById(R.id.delete);
        disegnoView= not.findViewById(R.id.pagina);
        disegnoView.setOnDrawChangedListener(this);
        indietro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                indietro();
            }
        });
        pennello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (disegnoView.getMode() == DisegnoView.STROKE) {
                    showPopup(view, DisegnoView.STROKE);

                } else {
                    disegnoView.setMode(DisegnoView.STROKE);
                    AlphaManager.setAlpha(fill, 0.4f);
                    AlphaManager.setAlpha(gomma, 0.4f);
                    AlphaManager.setAlpha(lente,0.4f);
                    AlphaManager.setAlpha(pennello, 1f);
                }
            }
        });
        fill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (disegnoView.getMode() == DisegnoView.FILL) {
                    showPopup(view, DisegnoView.FILL);

                } else {
                    disegnoView.setMode(DisegnoView.FILL);
                    AlphaManager.setAlpha(pennello, 0.4f);
                    AlphaManager.setAlpha(gomma, 0.4f);
                    AlphaManager.setAlpha(lente,0.4f);
                    AlphaManager.setAlpha(fill, 1f);
                }
            }
        });

        AlphaManager.setAlpha(gomma, 0.4f);
        AlphaManager.setAlpha(fill, 0.4f);
        AlphaManager.setAlpha(lente,0.4f);
        gomma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (disegnoView.getMode() == DisegnoView.ERASER) {
                    showPopup(view, DisegnoView.ERASER);
                } else {
                    disegnoView.setMode(DisegnoView.ERASER);
                    AlphaManager.setAlpha(fill, 0.4f);
                    AlphaManager.setAlpha(pennello, 0.4f);
                    AlphaManager.setAlpha(lente,0.4f);
                    AlphaManager.setAlpha(gomma, 1f);
                }
            }
        });
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disegnoView.undo();
            }
        });
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disegnoView.redo();
            }
        });

       lente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (disegnoView.getMode() == DisegnoView.ZOOM_MODE){
                    disegnoView.resetZoom();
                }else {
                    disegnoView.setMode(DisegnoView.ZOOM_MODE);
                    AlphaManager.setAlpha(fill, 0.4f);
                    AlphaManager.setAlpha(pennello, 0.4f);
                    AlphaManager.setAlpha(gomma, 0.4f);
                    AlphaManager.setAlpha(lente,1f);
                }

            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!click){
                    Toast.makeText(getActivity(), R.string.cancellare,Toast.LENGTH_SHORT).show();
                    delete.setColorFilter(Color.RED);
                    click=true;
                }else {
                    delete.setColorFilter(Color.BLACK);
                    disegnoView.erase();
                    click=false;
                }
            }
        });
        if (bundle != null){
            dispath=getArguments().getString("disegno");
            datamod=getArguments().getString("data");
            id=getArguments().getLong("id");
            Bitmap bmp;
            try {
                FileInputStream fileInputStream = new FileInputStream(dispath);
                bmp= BitmapFactory.decodeStream(fileInputStream);
                disegnoView.setBackgroundBitmap( bmp);

            } catch (Exception e) {
                 Log.e("AD", "Errore bitmap background", e);
            }
        }
        return  not;

    }

    public void indietro(){

        if (data != null){
            save();
            goDiario(data);
        }else if (datamod != null){
            update();
            goDiario(datamod);
        }
        else {
            Fragment list = new Frag_Diario_list();
            getMainActivity().switchContent(R.id.frameContainer,list,"list");
        }
    }
    public void goDiario(String sdata){

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        try {
            calendar.setTime(dateFormat.parse(sdata));
        } catch (ParseException e) {
        }
        Singleton.getInstance().setData(calendar);
        Fragment diario = new Frammento_Diario();
        getMainActivity().switchContent(R.id.frameContainer,diario,"diario");
    }
    public void update(){
        // salva file modifica
        Bitmap bitmap = disegnoView.getBitmap();
        if (bitmap != null) {
            try {
                File file = new File(dispath);
                boolean del = file.delete();
                dispath = base + cartella + "/disegno" + timeStamp + ".png";
                File bitmapFile = new File(dispath);
                FileOutputStream out = new FileOutputStream(bitmapFile, false);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
                DbAdapterDiario db = new DbAdapterDiario(getActivity());
                db.open();
                Cursor c = db.fetchFotoById(id);
                if (c.getCount() != 0 && c.moveToFirst()) {


                    db.updateFoto(id, c.getString(1), dispath, c.getString(3),null);
                }
                c.close();
                db.close();

            } catch (Exception e) {
                Log.e("AD", "update errore", e);
            }
        }
    }
    public void save(){
        // salva file nuovo
        Bitmap bitmap = disegnoView.getBitmap();
        if (bitmap != null) {
            try {
                File f=new File(Environment.getExternalStorageDirectory(),cartella);
                if (!f.exists()){
                    f.mkdir();
                }
                path =base+cartella+"/disegno"+timeStamp+".png";
                File bitmapFile = new File(path);
                FileOutputStream out = new FileOutputStream(bitmapFile,false);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
                if (bitmapFile.exists() && data != null) {
                    DbAdapterDiario db= new DbAdapterDiario(getActivity());
                   // new DriveSyncdb(getActivity()).creaDriveIdDb(data,path,"image",-1);
                    db.open();
                    db.createContactFoto(data, path,"image",null);
                    db.close();

                } else {
                  // getActivity().showMessage(R.string.error, ONStyle.ALERT);
                }

            } catch (Exception e) {
                Log.e("AD","salva  errore",e);
            }
        }

    }

    private void showPopup(View view, final int eraserOrStroke) {

        final boolean isErasing = eraserOrStroke == DisegnoView.ERASER;
        final boolean isFill = eraserOrStroke == DisegnoView.FILL;
        LayoutInflater inflaterEraser = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupEraserLayout = inflaterEraser.inflate(R.layout.popup_gomma, null);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_pennello, null);
        final PopupWindow popup = new PopupWindow(getActivity());
        popup.setContentView(isErasing ? popupEraserLayout : layout);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);
        popup.setBackgroundDrawable(new BitmapDrawable());

        contagocce= layout.findViewById(R.id.contagocce);
       // AlphaManager.setAlpha(contagocce, 0.4f);
        strokeImageView = layout.findViewById(R.id.stroke_circle);
        Drawable circleDrawable = getResources().getDrawable(R.drawable.tratto);
        size = circleDrawable.getIntrinsicWidth();
        eraseImageView = popupEraserLayout.findViewById(R.id.stroke_circle);
        size = circleDrawable.getIntrinsicWidth();
        final TextView tratto= layout.findViewById(R.id.textratto);

        mColorPicker = layout.findViewById(R.id.stroke_color_picker);
        mColorPicker.addSVBar((SVBar) layout.findViewById(R.id.svbar));
        mColorPicker.addOpacityBar((OpacityBar) layout.findViewById(R.id.opacitybar));
        if (isFill){
            tratto.setText(R.string.riempimento);
            mColorPicker.setColor(disegnoView.getFillColor());
            mColorPicker.setOldCenterColor(disegnoView.getFillColor());
        }else{

            mColorPicker.setColor(disegnoView.getStrokeColor());
            mColorPicker.setOldCenterColor(disegnoView.getStrokeColor());
        }
        mColorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                if (isFill){
                    disegnoView.setFillColor(color);
                }else disegnoView.setStrokeColor(color);
            }
        });

        oldColor = mColorPicker.getColor();
        contagocce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFill){
                    disegnoView.setMode(DisegnoView.CONTAGOCCE_F);
                    AlphaManager.setAlpha(fill, 0.4f);
                }else {
                    disegnoView.setMode(DisegnoView.CONTAGOCCE_S);
                    AlphaManager.setAlpha(pennello, 0.4f);
                }
                Toast.makeText(getActivity(), R.string.cotagoccie,Toast.LENGTH_SHORT).show();
                popup.dismiss();
            }
        });

        if (!isFill) {

        SeekBar mSeekBar = (SeekBar) (isErasing ? popupEraserLayout
                .findViewById(R.id.stroke_seekbar) : layout
                .findViewById(R.id.stroke_seekbar));
        mSeekBar.setVisibility(View.VISIBLE);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setSeekbarProgress(i, eraserOrStroke);
                tratto.setText(newmisura);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        int progress = isErasing ? seekBarEraserProgress : seekBarStrokeProgress;
        mSeekBar.setProgress(progress);
    }


        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mColorPicker.getColor() != oldColor)
                    mColorPicker.setOldCenterColor(oldColor);
            }
        });
        popup.showAsDropDown(view,-10,10);
    }



    protected void setSeekbarProgress(int progress, int eraserOrStroke) {

        int calcProgress = progress > 1 ? progress : 1;
         newSize = Math.round((size / 100f) * calcProgress);
        int offset = (size - newSize) / 2;
        newmisura=misura+": "+newSize;

        LayoutParams lp = new LayoutParams(newSize, newSize);
        lp.setMargins(offset, offset, offset, offset);
        if (eraserOrStroke == DisegnoView.STROKE) {
            strokeImageView.setLayoutParams(lp);
            strokeImageView.setVisibility(View.VISIBLE);
            seekBarStrokeProgress = progress;
        } else if (eraserOrStroke == DisegnoView.ERASER){
            eraseImageView.setLayoutParams(lp);
            eraseImageView.setVisibility(View.VISIBLE);
            seekBarEraserProgress = progress;
        }
        disegnoView.setSize(newSize, eraserOrStroke);
    }



    @Override
    public void onDrawChanged() {
        if (disegnoView.getPaths().size() > 0)
            AlphaManager.setAlpha(undo, 1f);
        else
            AlphaManager.setAlpha(undo, 0.4f);
        // Redo
        if (disegnoView.getUndoneCount() > 0)
            AlphaManager.setAlpha(redo, 1f);
        else
            AlphaManager.setAlpha(redo, 0.4f);
    }

    public MainActivity getMainActivity(){
        return (MainActivity)getActivity();
    }
}
