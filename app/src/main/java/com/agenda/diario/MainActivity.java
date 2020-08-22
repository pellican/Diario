package com.agenda.diario;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.agenda.diario.Agenda.Frammento_Agenda;
import com.agenda.diario.Calendario.Frammento_Calendario;
import com.agenda.diario.Database.DbAdapterDiario;
import com.agenda.diario.Diario.Frammento_Diario;
import com.agenda.diario.Diario_list.Frag_Diario_list;
import com.agenda.diario.GoogleDrive.DriveLogIn;
import com.agenda.diario.GoogleDrive.DriveSyncdb;
import com.agenda.diario.GoogleDrive.SincGoogleDrive;
import com.agenda.diario.Impost.Backup;
import com.agenda.diario.Impost.Impost_Activity;
import com.agenda.diario.Note.Frammento_Note;
import com.agenda.diario.Util.Costanti;
import com.agenda.diario.Util.PasswordValidator;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MainActivity extends PreActivity {
    public static final int RESULT_LOAD_IMAGE = 1;
    public static final int RESULT_LOAD_AUDIO = 2;
    public static final int RESULT_LOAD_VIDEO = 3;
    private static final int REQUEST_CODE_SIGN_IN = 4;
    public static String TAG_DIARIO="diario";





    private Handler mHandler = new Handler();
    DrawerLayout drawerLayout;
    DbAdapterDiario db;
    DriveLogIn driveLogIn;
    SimpleDateFormat convertDate;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        if(getResources().getBoolean(R.bool.port_solo)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        convertDate = new SimpleDateFormat("dd MMMM yyyy",Locale.getDefault());
        drawerLayout = findViewById(R.id.drawerLayout);
        fragmentManager = getSupportFragmentManager();
        db= new DbAdapterDiario(this);
        driveLogIn = new DriveLogIn(MainActivity.this);

        Intent intent= getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (action != null && action.equals("notifica")){
            String data=intent.getStringExtra("data");
            int ora= intent.getIntExtra("ora",-1);
            if (data != null && ora >= 0){
                DbAdapterDiario db = new DbAdapterDiario(getApplicationContext());
                db.open();
                Cursor c=db.fetchAgendaByData(data);
                if (c.getCount() != 0 && c.moveToFirst()){
                    for (int i=0;i<c.getCount();i++) {
                        int a=c.getInt(3);
                        if (a == ora) {
                            db.updateContactAgenda(c.getLong(0), c.getString(1), c.getString(2), c.getInt(3),
                                    c.getString(4), c.getString(5), "0");
                            c.moveToNext();
                        }
                        c.moveToNext();
                    }
                }
            }
            fragmentManager.beginTransaction().replace(R.id.frameContainer, new Frammento_Agenda()).commit();
        }else if (Intent.ACTION_SEND.equals(action) && type != null){
                riceviImage(intent);

        }
        else {
            if (prefs.getBoolean("accaunt",true)){

                driveLogIn.signVerIn();
            }
            if (prefs.getString(Costanti.PREF_PASSWORD,null) != null
                    && prefs.getBoolean(Costanti.PREF_SWITCHPASS,false)) {

                requestPassword(this, new PasswordValidator() {
                    @Override
                    public void onPasswordValidated(boolean passwordConfirmed) {
                        if (passwordConfirmed) {

                            fragmentManager.beginTransaction().replace(R.id.frameContainer, new Frag_Diario_list()).commit();
                        } else {
                            finish();
                        }
                    }
                });
            }else {
                fragmentManager.beginTransaction().replace(R.id.frameContainer, new Frag_Diario_list()).commit();

            }

       }



    }


    public void riceviImage(Intent intent){

        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        String schame =imageUri.getScheme();
        String phat= null;

        if (schame.equals("file")){
            phat=imageUri.getPath();
            String datafoto=getDataFoto(phat);
            salvaevai(datafoto,phat);
        }
        else if (schame.equals("content")) {
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(imageUri, proj, null, null, null);
            if (cursor != null && cursor.moveToFirst()){
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                phat= cursor.getString(column_index);
                cursor.close();
            }
            String datafoto=getDataFoto(phat);
            salvaevai(datafoto,phat);
        }

    }



    public void Calendario(View v) {

        fragmentManager.beginTransaction().replace(R.id.frameContainer, new Frammento_Calendario(),TAG_DIARIO).commit();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        }, 200);
    }
    public void Diario(View v) {

        fragmentManager.beginTransaction().replace(R.id.frameContainer, new Frag_Diario_list()).commit();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        }, 200);
    }
    public void Agenda(View v) {

        fragmentManager.beginTransaction().replace(R.id.frameContainer, new Frammento_Agenda(),TAG_DIARIO).commit();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        }, 200);
    }
    public void Note(View v) {

        fragmentManager.beginTransaction().replace(R.id.frameContainer, new Frammento_Note()).commit();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        }, 200);
    }
    public void Impost(View v) {
        drawerLayout.closeDrawer(Gravity.LEFT);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, Impost_Activity.class);
                MainActivity.this.startActivity(intent);
            }
        }, 200);

    }
    public void clickcal(View v){

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.frameContainer, new Frammento_Calendario()).commit();

    }
    public void clicklist(View v){

        fragmentManager.beginTransaction()
               // .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.frameContainer,new  Frag_Diario_list()).commit();
    }
    public void switchContent(int id, Fragment fragment,String tag) {
        fragmentManager.beginTransaction()
           // .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(id, fragment,tag)
            .commit();

    }
    public void clikdrawer(View v){
        drawerLayout.openDrawer(Gravity.LEFT);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent dati) {
        super.onActivityResult(requestCode, resultCode, dati);
        try {
            String setdata=Singleton.getInstance().getDataS() ;
            switch (requestCode){
                case  RESULT_LOAD_IMAGE:
                    if (resultCode == RESULT_OK && null != dati) {

                        String foto = dati.getData().toString();
                        Uri uri = Uri.parse(foto);
                        String[] proj = {MediaStore.Images.Media.DATA};
                        Cursor cursor = new CursorLoader(getApplicationContext(), uri, proj, null, null, null).loadInBackground();
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        String phat = cursor.getString(column_index);
                        String dateFormat = getDataFoto(phat);
                        if (dateFormat != null){
                            if (dateFormat.equals(setdata)) {
                               // new DriveSyncdb(this).creaDriveIdDb(setdata,phat,"image",-1);
                                db.open();
                                db.createContactFoto(setdata, phat, "image",null);
                                db.close();


                            } else {
                                dialog(dateFormat, setdata, phat);
                            }
                        }else{
                            db.open();
                            db.createContactFoto(setdata, phat, "image",null);
                            db.close();
                            //new DriveSyncdb(this).creaDriveIdDb(setdata,phat,"image",-1);
                        }

                    }
                        break;
                case RESULT_LOAD_AUDIO :
                    if ( resultCode == RESULT_OK && null != dati) {
                        Uri uri = dati.getData();
                        String schame = uri.getScheme();
                        String phat = null;

                        if (schame.equals("file")) {
                            phat = uri.getPath();
                            if (phat.contains("%20")) {
                                phat = phat.replaceAll(" ", "%20");
                            }
                        } else if (schame.equals("content")) {

                            String[] proj = {MediaStore.Audio.Media.DATA};
                            Cursor cursor = new CursorLoader(getApplicationContext(), uri, proj, null, null, null).loadInBackground();
                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                            cursor.moveToFirst();
                            phat = cursor.getString(column_index);
                        }
                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        mmr.setDataSource(this, uri);
                        String titolo = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                        String artista = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

                        db.open();
                        db.createContactAudio(setdata, phat, titolo, artista, R.drawable.music,null);
                        db.close();
                    }
                    break;
                case RESULT_LOAD_VIDEO :
                    if (resultCode == RESULT_OK && null != dati){
                        Uri uri = dati.getData();
                        String phat=null;
                        if (uri.getScheme().equals("file")){
                            phat=uri.getPath();
                            Log.d("video file",phat);
                            if (phat.contains("%20")){
                                phat=phat.replaceAll(" ","%20");
                            }
                        }else if (uri.getScheme().equals("content")){
                            String[] proj = { MediaStore.Video.Media.DATA};
                            Cursor cursor = new CursorLoader(getApplicationContext(),uri, proj, null, null, null).loadInBackground();
                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                            cursor.moveToFirst();
                            phat= cursor.getString(column_index);
                            Log.d("video content",phat);
                        }
                        // String dateFormat=getDataFoto(phat);
                       // new DriveSyncdb(this).creaDriveIdDb(setdata,phat,"video",-1);
                        db.open();
                        db.createContactFoto(setdata, phat, "video",null);
                        db.close();
                    }
                    break;
                case REQUEST_CODE_SIGN_IN:
                    if (resultCode == RESULT_OK) {
                        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(dati);
                        GoogleSignInAccount account = result.getSignInAccount();

                        prefs.edit().putString("user", account.getDisplayName())
                                .putString("email", account.getEmail())
                                .putBoolean("accaunt",true)
                                .putBoolean("disc", true).apply();
                        if (account.getPhotoUrl() != null) {
                            prefs.edit().putString("logo", account.getPhotoUrl().toString()).apply();
                        }
                        new DriveSyncdb(this,account).cercaDriveFile();


                    }else{
                       // prefs.edit().putBoolean("disc",false).apply();
                        if (!driveLogIn.isConnected()){
                            float heigt = 55 * getResources().getDisplayMetrics().density;
                            Style style = new Style.Builder()
                                    .setBackgroundColor(R.color.alert)
                                    .setHeight((int)heigt).build();
                            Crouton.makeText(this,R.string.errore_connessione, style).show();
                        }

                    }
                    break;
                default:
                    {
                        Toast.makeText(this, R.string.riprova, Toast.LENGTH_SHORT).show();
                    }
            }

            Singleton.getInstance().setDataS(null);
        } catch (Exception e) {
            Log.d("ricevi", "onActivityResult: ",e);
                Toast.makeText(this,R.string.errore, Toast.LENGTH_SHORT)
                        .show();
            }
    }

    public String getDataFoto(String phat){
        String dateFormat= null;
        try {
            ExifInterface exif = new ExifInterface(phat);
            String data= exif.getAttribute(ExifInterface.TAG_DATETIME);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd kk:mm:ss", Locale.getDefault());
            Date d= simpleDateFormat.parse(data);
             dateFormat = convertDate.format(d);
        }catch (Exception e){
            Log.d("mio","getdatafoto: ",e);
            Toast.makeText(this, "errore", Toast.LENGTH_LONG).show();
        }
       return  dateFormat;
    }

    public void dialog(final String datafoto, final String setdata, final String foto){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.la_foto)+ datafoto);
        dialog.setMessage(getString(R.string.usa_la_data));
        dialog.setNeutralButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                salvaevai(setdata,foto);
                dialogInterface.dismiss();
            }
        });
        dialog.setNegativeButton(R.string.annulla, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                salvaevai(datafoto,foto);
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }


    public void salvaevai(String data,String foto){
        Calendar calendar =Calendar.getInstance();
        Date date=null;
        try {
            date = convertDate.parse(data);
        }catch (Exception e){
            Toast.makeText(this, "errore", Toast.LENGTH_LONG).show();
        }
        Log.d("salvaevai",data);
        calendar.setTime(date);
        Singleton.getInstance().setData(calendar);
      //  new DriveSyncdb(this).creaDriveIdDb(data,foto,"image",-1);
        db.open();
        db.createContactFoto(data, foto, "image",null);
        db.close();
        Fragment fragment = new Frammento_Diario();
        switchContent(R.id.frameContainer,fragment,"diario");
    }


    @Override
    protected void onStop() {
        if(prefs.getBoolean(Costanti.PREF_SWITCHBACKUP,false)) {
            new Backup(getApplicationContext()).autoBackup();
        }
     //   new DriveLogIn(this).databaseToDrive();
        super.onStop();

    }



    @Override
    public void onBackPressed() {

        Fragment f;
        f = checkFragmentInstance(R.id.frameContainer, Frammento_Note.class);
        if (f != null) {

            ((Frammento_Note)f).indietro();
            return;
        }
        if (getSupportFragmentManager().findFragmentByTag("diario") != null) {

            fragmentManager.beginTransaction()
                   // .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.frameContainer, new Frag_Diario_list()).commit();

        }else {

            super.onBackPressed();
        }
    }
    public Fragment checkFragmentInstance(int id, Object instanceClass) {
        Fragment result = null;
        if (fragmentManager != null) {
            Fragment fragment = fragmentManager.findFragmentById(id);
            if (instanceClass.equals(fragment.getClass())) {
                result = fragment;
            }
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this,SincGoogleDrive.class);
        startService(intent);
    }
}
