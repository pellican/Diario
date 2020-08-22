package com.agenda.diario.Impost;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import com.agenda.diario.R;
import com.agenda.diario.Util.Costanti;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;

public class Backup {
    private SharedPreferences preferences;
    private Calendar calendar;
    private long giorno= 86400000;
    private long settimana=giorno*7;
    private long mese=giorno*30;
    private long [] scelta={giorno,settimana,mese};
    private Context context;

    public Backup(Context mContext) {
        context=mContext;
        preferences=mContext.getSharedPreferences(Costanti.PREFS_NAME,Context.MODE_MULTI_PROCESS);
        calendar=Calendar.getInstance();

    }
    public void autoBackup(){
        long ultimoBackup = preferences.getLong("ultimoBackup", 0);
        if (!preferences.contains("ultimoBackup")){
            preferences.edit().putLong("ultimoBackup",calendar.getTimeInMillis()).apply();
        }
        if (ultimoBackup != 0) {
            long diff = calendar.getTimeInMillis() - ultimoBackup;
            int prefere = preferences.getInt("frequenza", 0);
            if (diff >= scelta[prefere]) {
                backup();
            }
        }
    }
    private void filebackup(){
     File root=new File(Environment.getExternalStorageDirectory()+"/AgenDiario/Backup");
     File files[] = root.listFiles(backupFilter);
     Arrays.sort( files,comparator);
     int numsalv=preferences.getInt("numerobackup",0);
         if (files.length >numsalv){
             for (int a=0;a<files.length;a++){
                 if (a>numsalv){
                    boolean delete= files[a].delete();
                 }
             }
         }
     }



    private Comparator comparator=new Comparator(){
        public int compare(Object o1, Object o2) {
            if (((File)o1).lastModified() > ((File)o2).lastModified()) {
                return -1;
            } else if (((File)o1).lastModified() < ((File)o2).lastModified()) {
                return +1;
            } else {
                return 0;
            }
        }
    };

    private FilenameFilter backupFilter = new FilenameFilter() {
        File f;
        public boolean accept(File dir, String name) {
            if(name.endsWith(".db") ) {
                return true;
            }
            f = new File(dir.getAbsolutePath()+"/"+name);
                return f.isDirectory();
        }
    };

    void backup(){

        String dbPath=context.getResources().getString(R.string.db_path);
        String data=(String) DateFormat.format("dd.MM.yyyy", calendar);
        File f=new File(Environment.getExternalStorageDirectory(),"/AgenDiario");
        if (!f.exists()){
          boolean folder = f.mkdir();
        }
        File f1=new File(Environment.getExternalStorageDirectory()+"/AgenDiario","/Backup");
        if (!f1.exists()){
          boolean sub=f1.mkdirs();
        }
        if (f1.exists()) {
            String nomeFileBackup = "/AgenDiario/Backup/backup_" + data + ".db";
            File currentDB = new File(dbPath);
            if (currentDB.exists()) {
                try {
                    File sd = Environment.getExternalStorageDirectory();
                    if (sd.canWrite()) {
                        File backupDB = new File(sd, nomeFileBackup);
                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                        preferences.edit().putLong("ultimoBackup",calendar.getTimeInMillis()).apply();
                    }
                } catch (IOException e) {
                    Log.e("Permission denied", "Can't write to SD card, add permission");
                }
                filebackup();
            }
        }
    }



}
