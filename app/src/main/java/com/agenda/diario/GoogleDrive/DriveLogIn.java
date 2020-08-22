package com.agenda.diario.GoogleDrive;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.agenda.diario.Impost.Impost_Activity;
import com.agenda.diario.R;
import com.agenda.diario.Util.Costanti;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import static android.content.Context.MODE_MULTI_PROCESS;

public class DriveLogIn {
    private static final int REQUEST_CODE_SIGN_IN = 4;
    private SharedPreferences prefs;
    private GoogleSignInClient mGoogleSignInClient;
    private Activity activity;


    public  void signVerIn(){
      if (!isSignedIn()) {
        signIn();
    }
}
    public DriveLogIn(Activity activity) {
        this.activity=activity;



    }
    public void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    private void signIn() {
        mGoogleSignInClient = getGoogleSignInClient();
        mGoogleSignInClient.silentSignIn()
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        new DriveSyncdb(activity,googleSignInAccount).cercaDriveFile();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        activity.startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
                    }
        });
    }
    public void revokeAccess() {
        prefs =activity.getSharedPreferences(Costanti.PREFS_NAME,MODE_MULTI_PROCESS);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
        if (account != null) {
            mGoogleSignInClient = getGoogleSignInClient();
            mGoogleSignInClient.revokeAccess()
                    .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            prefs.edit()
                                    .remove("user")
                                    .remove("email")
                                    .remove("logo")
                                    .putBoolean("accaunt",false)
                                    .putBoolean("disc",false).apply();
                            Toast.makeText(activity,"revocato",Toast.LENGTH_LONG).show();
                            activity.getFragmentManager().beginTransaction().replace(R.id.content,new Impost_Activity.Pref()).commit();
                        }
                    });
        }
    }

    private boolean isSignedIn() {
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (signInAccount != null){
            new DriveSyncdb(activity,signInAccount).cercaDriveFile();
        }
        return (signInAccount != null && signInAccount.getGrantedScopes().contains(Drive.SCOPE_APPFOLDER));
    }


    private GoogleSignInClient getGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_APPFOLDER)
                        .requestEmail()
                        .requestProfile()
                        .build();
        return GoogleSignIn.getClient(activity, signInOptions);
    }

/*
    public void createDriveClients(GoogleSignInAccount googleSignInAccount) {
        // Build a drive client.
        mDriveClient = Drive.getDriveClient(activity, googleSignInAccount);
        // Build a drive resource client.
        mDriveResourceClient =
                Drive.getDriveResourceClient(activity, googleSignInAccount);
        Toast.makeText(activity,"login drive act",Toast.LENGTH_LONG).show();

        new DriveSyncdb(activity).cercaDriveFile();
      //  Intent intent = new Intent(activity, SincGoogleDrive.class);
      //  activity.startService(intent);
    }
*/
    public boolean isConnected() {
        ConnectivityManager cm =(ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        return activeNetwork != null && activeNetwork.isConnected();
    }




































}
