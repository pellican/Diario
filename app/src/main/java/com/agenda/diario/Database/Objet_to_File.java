package com.agenda.diario.Database;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.Pair;

import com.agenda.diario.Note.Pari;
import com.agenda.diario.Note.SPaint;
import com.agenda.diario.Note.SPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by portatile on 16/08/2016.
 */
public class Objet_to_File {
    String FileName="disegno.dat";
   // String outputFile;
    Context mContext;
    public Objet_to_File(Context mContext) {
        this.mContext=mContext;
    }

    public void scriviListToFile(ArrayList<Pari<SPath, SPaint>> paths){
      /*
        outputFile= Environment.getExternalStorageDirectory().getAbsolutePath();

        String cartella="/AgenDiario";
        File f=new File(Environment.getExternalStorageDirectory(),cartella);
        if (!f.exists()){
            f.mkdir();
        }
        outputFile += cartella+"/"+FileName+".dat";
        */
        File myfile =  mContext.getFileStreamPath(FileName);
        if(null == FileName)
            throw new RuntimeException ("FileName is null!");
        try {
            if(myfile.exists() || myfile.createNewFile()){
                FileOutputStream fos = mContext.openFileOutput(FileName,Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(paths);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ArrayList<Pari<SPath, SPaint>> leggiListDaFile() {
        ArrayList<Pari<SPath, SPaint>> masterlistrev=new ArrayList<>();
            if(null == FileName)
                return null;

            File myfile = mContext.getFileStreamPath(FileName);
            try {
                if(myfile.exists()){
                    FileInputStream fis = mContext.openFileInput(FileName);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    masterlistrev = (ArrayList<Pari<SPath, SPaint>>) ois.readObject();
                    fis.close();
                }else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return masterlistrev;
    }
}
