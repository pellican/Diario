package com.agenda.diario.GoogleDrive;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class DeleteFile {
    private Activity activity;
    private DriveResourceClient mDriveResourceClient;

    public DeleteFile(Activity activity) {
        this.activity = activity;
        GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(activity);
        if (account != null) {
            mDriveResourceClient = Drive.getDriveResourceClient(activity, account);
        }

    }
    public Boolean deleteDriveFile(String driveId){
        final boolean[] delete = {false};
        if (isConnected()) {
            DriveFile file = DriveId.decodeFromString(driveId).asDriveFile();
            mDriveResourceClient.delete(file)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("deletedrivefile", "success");
                            delete[0] = true;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("deletedrivefile", "fallito :", e);
                            delete[0] = false;
                        }
                    });
        }
        return delete[0];
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
