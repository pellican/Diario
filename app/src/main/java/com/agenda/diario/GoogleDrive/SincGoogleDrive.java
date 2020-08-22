package com.agenda.diario.GoogleDrive;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.agenda.diario.Database.DbAdapterDiario;
import com.agenda.diario.R;
import com.agenda.diario.Util.Costanti;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class SincGoogleDrive extends IntentService {
    private DbAdapterDiario db;
    private DriveSyncdb syncdb;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private SharedPreferences prefs;

    public SincGoogleDrive() {
        super("SincGoogleDrive");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db= new DbAdapterDiario(getApplicationContext());

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (isConnected()){
            prefs =getSharedPreferences(Costanti.PREFS_NAME,MODE_MULTI_PROCESS);
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
            if (account != null) {
                mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(),account);
                updateDbtoDrive();
            }

        }

    }



    private void sincFile(){
        db.open();
        Cursor c = db.fetchAllFoto();
        if (c.getCount() != 0 && c.moveToFirst()){
            final File[] file = new File[c.getCount()];
            for (int i=0;i<c.getCount();i++){
                if (c.getString(4)== null){
                    // colunn id 0 ,data 1, path 2,tipo 3,driveid 4
                    creaNuovoDrive(c.getLong(0),c.getString(1),c.getString(2),c.getString(3));
                }else{
                    updateToDrive(c.getString(4),c.getString(2));
                }

                c.moveToNext();
            }
        }
        c.close();
        Cursor ca = db.fetchAllAudio();
        if (ca.getCount() != 0 && ca.moveToFirst()){
            final File[] file = new File[ca.getCount()];
            for (int i=0;i<ca.getCount();i++){
                if (ca.getString(4)== null){
                    creaNuovoDrive(ca.getLong(0),ca.getString(1),c.getString(2),"audio");
                }else{
                    updateToDrive(c.getString(4),c.getString(2));
                }

                ca.moveToNext();
            }
        }
        ca.close();
    }

    private void updateDbtoDrive(){
        final String driveId = prefs.getString("driveId", null);
        if (isConnected() && driveId != null) {

            DriveFile file = DriveId.decodeFromString(driveId).asDriveFile();
            Task<DriveContents> openFileTask = mDriveResourceClient.openFile(file, DriveFile.MODE_WRITE_ONLY);
            openFileTask.continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                @Override
                public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                    DriveContents driveContents = task.getResult();
                    OutputStream outputStream = driveContents.getOutputStream();
                    String input = "data/data/com.agenda.diario/databases/database_diario.db";
                    File inputFile = new File(input);

                    byte[] buf = new byte[8192];

                    int c = 0;
                    InputStream inputStream = new FileInputStream(inputFile);
                    while ((c = inputStream.read(buf, 0, buf.length)) > 0) {
                        outputStream.write(buf, 0, c);
                        outputStream.flush();
                    }
                    outputStream.close();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setStarred(true)
                            .setLastViewedByMeDate(new Date())
                            .build();
                    return mDriveResourceClient.commitContents(driveContents, changeSet);

                }
            })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                         sincFile();
                        }
                    });
        }
    }


    private void creaNuovoDrive(final long id, final String data, final String path, final String tipo){
        final File file = new File(path);
        final Task<DriveFolder> appFolderTask = mDriveResourceClient.getAppFolder();
        final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
        Tasks.whenAll(appFolderTask, createContentsTask)
                .continueWithTask(new Continuation<Void, Task<DriveFile>>() {
                    @Override
                    public Task<DriveFile> then(@NonNull Task<Void> task) throws Exception {
                        DriveFolder parent = appFolderTask.getResult();
                        DriveContents contents = createContentsTask.getResult();
                        OutputStream outputStream = contents.getOutputStream();
                        InputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        int c;
                        byte[] buf = new byte[8192];

                        while ((c = inputStream.read(buf, 0, buf.length)) > 0) {
                            outputStream.write(buf, 0, c);
                            outputStream.flush();
                        }
                        outputStream.close();
                       // String title = file.getName();
                        String extension = MimeTypeMap.getFileExtensionFromUrl(file.toString());

                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle(file.getName())
                                .setMimeType(tipo + "/" + extension)
                                .setStarred(true)
                                .build();

                        return mDriveResourceClient.createFile(parent, changeSet, contents);
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DriveFile>() {
                    @Override
                    public void onSuccess(DriveFile driveFile) {
                        if (tipo.equals("image")  || tipo.equals("video")){
                            db.updateFoto(id,data,path,tipo,driveFile.getDriveId().encodeToString());
                        }else if (tipo.equals("audio")){
                            Cursor c = db.fetchAudioById(id);
                            if (c.getCount() != 0 && c.moveToFirst()){
                                db.updateAudio(id,c.getString(1),c.getString(2),c.getString(3)
                                ,c.getString(4),c.getInt(5),driveFile.getDriveId().encodeToString());
                                c.close();
                            }

                        }

                    }
                });
    }

    public void updateToDrive(String driveId,final String input){
        //   final String driveId = prefs.getString("driveId", null);
        if ( driveId != null) {

            DriveFile file = DriveId.decodeFromString(driveId).asDriveFile();
            Task<DriveContents> openFileTask = mDriveResourceClient.openFile(file, DriveFile.MODE_WRITE_ONLY);
            openFileTask.continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                @Override
                public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                    DriveContents driveContents = task.getResult();
                    OutputStream outputStream = driveContents.getOutputStream();
                    //   String input = "data/data/com.agenda.diario/databases/database_diario.db";
                    File inputFile = new File(input);

                    byte[] buf = new byte[8192];

                    int c = 0;
                    InputStream inputStream = new FileInputStream(inputFile);
                    while ((c = inputStream.read(buf, 0, buf.length)) > 0) {
                        outputStream.write(buf, 0, c);
                        outputStream.flush();
                    }
                    outputStream.close();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setStarred(true)
                            .setLastViewedByMeDate(new Date())
                            .build();
                    return mDriveResourceClient.commitContents(driveContents, changeSet);

                }
            })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                          //  Toast.makeText(getApplicationContext(), "database to drive", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    public boolean isConnected() {
        ConnectivityManager cm =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        return activeNetwork != null && activeNetwork.isConnected();
    }


}
