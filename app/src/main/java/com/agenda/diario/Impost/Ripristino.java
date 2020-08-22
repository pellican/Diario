package com.agenda.diario.Impost;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Comparator;


class Ripristino {

    Ripristino() {
    }

    String[] getlista(){
        File root=new File(Environment.getExternalStorageDirectory()+"/AgenDiario/Backup");
        File files[] = root.listFiles(backupFilter);
        Arrays.sort( files,comparator);
        String lista[]=new String[files.length];
        for (int i=0;i<files.length;i++){
            lista[i]=files[i].getName();
        }
        return lista;
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

    private static void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inChannel.close();
            outChannel.close();
        } catch (Throwable th) {
            if (inChannel != null) {
                inChannel.close();
            }
            outChannel.close();
        }
    }
    boolean restoreDatabase(String backup) {
        String dbPath="data/data/com.agenda.diario/databases/database_diario.db";
        String fileBackup="/AgenDiario/Backup/"+backup;
        boolean ret=false;
        try {
            if (Environment.getExternalStorageDirectory().canRead()) {
                File currentDB = new File(dbPath);
                File backupDB = new File(Environment.getExternalStorageDirectory(),fileBackup);
                copyFile(backupDB, currentDB);
                ret= true;
            }

        } catch (IOException e) {
            Log.e("Permission denied", "Can't read from SD card, add permission");
            }
        return ret ;
    }
}
