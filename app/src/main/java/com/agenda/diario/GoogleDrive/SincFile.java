package com.agenda.diario.GoogleDrive;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.agenda.diario.Database.DbAdapterDiario;
import com.agenda.diario.Diario_list.Frag_Diario_list;
import com.agenda.diario.MainActivity;
import com.agenda.diario.R;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class SincFile {
    private DbAdapterDiario db;
    private DriveResourceClient mDriveResourceClient;
    private Activity activity;

    public SincFile(Activity activity,DriveResourceClient mDriveResourceClient) {
        this.activity = activity;
        this.mDriveResourceClient = mDriveResourceClient;
        db = new DbAdapterDiario(activity);
        sincFile();
    }
    private void sincFile(){
        db.open();
        Cursor c = db.fetchAllFoto();
        if (c.getCount() != 0 && c.moveToFirst()){
            final File[] file = new File[c.getCount()];
            for (int i=0;i<c.getCount();i++){

                file[i]= new File(c.getString(2));

                if (!file[i].exists()){
                    ripristinaDaDrive(c.getLong(0),c.getString(2),c.getString(4));
                }
                c.moveToNext();
            }
        }
        c.close();
        Cursor ca = db.fetchAllAudio();
        if (ca.getCount() != 0 && ca.moveToFirst()){
            final File[] file = new File[ca.getCount()];
            for (int i=0;i<ca.getCount();i++){

                file[i]= new File(ca.getString(2));

                if (!file[i].exists()){
                    ripristinaDaDrive(c.getLong(0),c.getString(2),c.getString(4));
                }
                ca.moveToNext();
            }
        }
        ca.close();
        MainActivity mainActivity = (MainActivity)activity;
        mainActivity.switchContent(R.id.frameContainer, new Frag_Diario_list(), "list");
    }
    private void ripristinaDaDrive(final long id, final String path, final String driveId ) {
        if (driveId != null && isConnected()) {
            final ProgressDialog progressDialog = new ProgressDialog(activity);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle(R.string.ripristino);
            progressDialog.setMessage("loading..File..");
            progressDialog.setCancelable(false);
            progressDialog.show();

            DriveFile driveFile = DriveId.decodeFromString(driveId).asDriveFile();
            final Task<DriveContents> openFileTask = mDriveResourceClient.openFile(driveFile, DriveFile.MODE_READ_ONLY);
            openFileTask.continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                @Override
                public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {

                    DriveContents driveContents = task.getResult();
                    InputStream inputStream = driveContents.getInputStream();
                    String cercafolder = path.substring(0, path.lastIndexOf("/"));
                    File cerca = new File(cercafolder);
                    File outFile = null;
                    if (!cerca.exists()) {
                        File f = new File(Environment.getExternalStorageDirectory(), "/AgenDiario");
                        if (!f.exists()) {
                            boolean folder = f.mkdir();
                        }
                        File f1 = new File(Environment.getExternalStorageDirectory() + "/AgenDiario", "/FileRipristino");
                        if (!f1.exists()) {
                            boolean sub = f1.mkdirs();
                        }
                        if (f1.exists()) {
                            File sd = Environment.getExternalStorageDirectory();
                            String file = path.substring(path.lastIndexOf("/"), path.length());
                            String nomefile = "/AgenDiario/FileRipristino/" + file;
                            outFile = new File(sd, nomefile);
                            Cursor c = db.fetchFotoById(id);
                            if (c.getCount() != 0 && c.moveToFirst()) {
                                db.updateFoto(id, c.getString(1), nomefile, c.getString(3), driveId);
                            }
                        }
                    } else {
                        outFile = new File(path);
                    }

                    byte[] buf = new byte[8192];
                    int c = 0;
                    OutputStream outputStream = new FileOutputStream(outFile);
                    while ((c = inputStream.read(buf, 0, buf.length)) > 0) {
                        outputStream.write(buf, 0, c);
                        outputStream.flush();
                    }
                    outputStream.close();


                    return mDriveResourceClient.discardContents(driveContents);
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    progressDialog.dismiss();
                }
            });

        }
    }
    private boolean isConnected() {
        ConnectivityManager cm =(ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
