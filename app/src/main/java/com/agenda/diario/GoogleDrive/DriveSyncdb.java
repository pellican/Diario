package com.agenda.diario.GoogleDrive;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.agenda.diario.Database.DbAdapterDiario;
import com.agenda.diario.Diario_list.Frag_Diario_list;
import com.agenda.diario.MainActivity;
import com.agenda.diario.R;
import com.agenda.diario.Util.Costanti;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
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

import static android.content.Context.MODE_MULTI_PROCESS;

public class DriveSyncdb {
    private SharedPreferences prefs;
    private GoogleSignInAccount account;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private DriveId mDriveId;
    private Activity activity;

    public DriveSyncdb(Activity activity,GoogleSignInAccount account) {
        this.activity=activity;
        this.account=account;
        init();
    }
    private void init(){
        prefs =activity.getSharedPreferences(Costanti.PREFS_NAME,MODE_MULTI_PROCESS);

        mDriveClient = Drive.getDriveClient(activity,account);

        mDriveResourceClient = Drive.getDriveResourceClient(activity, account);

    }

    // cerca se gia installato
    public void cercaDriveFile(){

        final String driveId = prefs.getString("driveId", null);
        if (driveId == null && isConnected()) {
            mDriveClient.requestSync().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    final Query query = new Query.Builder()
                            .addFilter(Filters.eq(SearchableField.TITLE, "ultimo.db"))
                            .build();
                    final Task<DriveFolder> appFolderTask = mDriveResourceClient.getAppFolder();
                    appFolderTask
                            .addOnSuccessListener(new OnSuccessListener<DriveFolder>() {
                                @Override
                                public void onSuccess(DriveFolder driveFolder) {
                                    final Task<MetadataBuffer> querytask = mDriveResourceClient.queryChildren(driveFolder, query);
                                    querytask
                                            .addOnSuccessListener(new OnSuccessListener<MetadataBuffer>() {
                                                @Override
                                                public void onSuccess(MetadataBuffer metadata) {

                                                    Toast.makeText(activity, "" + metadata.getCount(), Toast.LENGTH_LONG).show();
                                    /*
                                    for (int a= 0;a<metadata.getCount();a++){
                                        DriveFile driveFile = DriveId.decodeFromString(metadata.get(a).getDriveId().encodeToString()).asDriveFile();
                                        mDriveResourceClient.delete(driveFile);

                                    } */
                                                    if (metadata.getCount() > 0) {
                                                        ripristinaDaDrive(metadata.get(0).getDriveId().encodeToString());
                                                        prefs.edit().putString("driveId", metadata.get(0).getDriveId().encodeToString()).apply();

                                                    } else {
                                                        creaNuovoInDrive();
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(activity, "file non trovato in drive", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(activity, "folder non trovato in drive", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            });
        }else if (driveId !=null && isConnected() ){
          //  updateToDrive(driveId);
        }
    }


    // ripristina da google drive (dialog)
    private void ripristinaDaDrive(String driveId){
        if (isConnected()) {
            final ProgressDialog progressDialog = new ProgressDialog(activity);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle(R.string.ripristino);
            progressDialog.setMessage("loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            DriveFile file = DriveId.decodeFromString(driveId).asDriveFile();
            Task<DriveContents> openFileTask = mDriveResourceClient.openFile(file, DriveFile.MODE_READ_ONLY);
            openFileTask.continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                @Override
                public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                    DriveContents driveContents = task.getResult();
                    InputStream inputStream = driveContents.getInputStream();
                    String out = "data/data/com.agenda.diario/databases/database_diario.db";

                    File outFile = new File(out);
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
            })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            final MainActivity mainActivity = (MainActivity) activity;
                            Fragment f = mainActivity.checkFragmentInstance(R.id.frameContainer, Frag_Diario_list.class);
                            if (f != null) {
                                ((Frag_Diario_list) f).removeTutorial();
                            }
                            new SincFile(activity,mDriveResourceClient);
                            progressDialog.dismiss();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(activity, "errore drive to database", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    private void creaNuovoInDrive() {
        final String driveId = prefs.getString("driveId", null);
        if (driveId == null && isConnected()) {

            String dbPath = activity.getResources().getString(R.string.db_path);
            final File currentDB = new File(dbPath);
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
                                inputStream = new FileInputStream(currentDB);
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

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle("ultimo.db")
                                    .setMimeType("application/x-sqlite3")
                                    .setStarred(true)
                                    .build();

                            return mDriveResourceClient.createFile(parent, changeSet, contents);
                        }

                    })
                    .addOnSuccessListener(new OnSuccessListener<DriveFile>() {
                        @Override
                        public void onSuccess(DriveFile driveFile) {

                            Toast.makeText(activity, "db creato in drive", Toast.LENGTH_LONG).show();
                            prefs.edit().putString("driveId", driveFile.getDriveId().encodeToString()).apply();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }












    public boolean isConnected() {
        ConnectivityManager cm =(ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        return activeNetwork != null && activeNetwork.isConnected();
    }










}
