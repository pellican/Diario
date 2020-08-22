package com.agenda.diario.Impost;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.agenda.diario.GoogleDrive.DriveLogIn;
import com.agenda.diario.GoogleDrive.DriveSyncdb;
import com.agenda.diario.MainActivity;
import com.agenda.diario.R;
import com.agenda.diario.Util.Costanti;
import com.agenda.diario.Util.CroutonStyle;
import com.agenda.diario.Util.Security;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class Impost_Activity extends AppCompatActivity {
    private static final int REQUEST_CODE_SIGN_IN = 4;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.tolbar_impost));
        }
        setContentView(R.layout.frammento_impost);
        getFragmentManager().beginTransaction().replace(R.id.content,new Pref()).commit();
        prefs= PreferenceManager.getDefaultSharedPreferences(this);
    }
    public void indietro(View view){
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN ){
            if (resultCode == RESULT_OK){
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                GoogleSignInAccount account = result.getSignInAccount();
                prefs.edit().putString("user", account.getDisplayName())
                        .putString("email", account.getEmail())
                        .putBoolean("accaunt",true)
                        .putBoolean("disc", true).apply();
                if (account.getPhotoUrl() != null) {
                    prefs.edit().putString("logo", account.getPhotoUrl().toString()).apply();
                }
                Toast.makeText(this, account.getDisplayName(), Toast.LENGTH_SHORT).show();
                getFragmentManager().beginTransaction().replace(R.id.content,new Pref()).commit();
               // new DriveLogIn(this).createDriveClients(account);
                new DriveSyncdb(this,account).cercaDriveFile();
            }else{
              //  prefs.edit().putBoolean("disc",false).apply();
                getFragmentManager().beginTransaction().replace(R.id.content,new Pref()).commit();
                if (!new DriveLogIn(this).isConnected()){
                    float heigt = 55 * getResources().getDisplayMetrics().density;
                    Style style = new Style.Builder()
                            .setBackgroundColor(R.color.alert)
                            .setHeight((int)heigt).build();
                    Crouton.makeText(this,R.string.errore_connessione, style).show();
                }
            }
        }
    }

    public static class Pref extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        SharedPreferences preferences;
        SwitchPreference swiPass,disc;
        Preference imppass,backup,ripristino,user,email;
        boolean password= false;
        DriveLogIn drive;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting);
            preferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
            swiPass=(SwitchPreference) findPreference("switchpass");
            imppass=findPreference("impass");
            backup=findPreference("backup");
            ripristino=findPreference("ripristino");
            user=findPreference("user");
            email=findPreference("email");
            drive = new DriveLogIn(getActivity());
            disc=(SwitchPreference) findPreference("disc");

            disc.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean checked = Boolean.valueOf(newValue.toString());
                    if (checked){

                        drive.signVerIn();
                        preferences.edit().putBoolean("accaunt",true).apply();

                    }else{

                        drive.revokeAccess();

                    }
                    return true;
                }
            });
            if (disc.isChecked()){
                disc.setSummary(R.string.disconnetti);
                backup.setEnabled(false);
                ripristino.setEnabled(false);
            }else{
                disc.setSummary(R.string.connetti);
                backup.setEnabled(true);
                ripristino.setEnabled(true);
            }

            // swich password
            swiPass.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean checked = Boolean.valueOf(newValue.toString());
                    if (checked) {
                        preference.setSummary(R.string.dispassword);
                        imppass.setEnabled(true);
                        if (!password){
                            Intent intent = new Intent(getActivity(), Activity_password.class);
                            getActivity().startActivity(intent);
                        }

                    } else {
                        preference.setSummary(R.string.impassword);

                        AlertDialog.Builder dialog =new AlertDialog.Builder(getActivity());
                        dialog.setTitle(R.string.dispassword);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                imppass.setEnabled(false);
                                // password disattivata
                            }
                        });
                        dialog.setNegativeButton(R.string.annulla, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                swiPass.setChecked(true);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                    return true;
                }
            });
            if (swiPass.isChecked()){
                imppass.setEnabled(true);
            }
            if (preferences.getString(Costanti.PREF_PASSWORD,null) != null){
                password=true;
                imppass.setSummary("Modifica la Password");
            }
            // imposta password
            imppass.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (password){
                        final Dialog dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.dialog_password);
                        final EditText editText = dialog.findViewById(R.id.editmodpass);

                        Button ok= dialog.findViewById(R.id.butmodpass);
                        Button ann= dialog.findViewById(R.id.butannpass);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String oldPassword = preferences.getString(Costanti.PREF_PASSWORD, "");
                                String password = editText.getText().toString();
                                // The check is done on password's hash stored in preferences
                                boolean result = Security.md5(password).equals(oldPassword);

                                // In case password is ok dialog is dismissed and result sent to callback
                                if (result) {
                                    // hideKeyboard(passwordEditText);
                                    Intent intent = new Intent(getActivity(), Activity_password.class);
                                    getActivity().startActivity(intent);
                                    dialog.dismiss();
                                    // If password is wrong the auth flow is not interrupted and simply a message is shown
                                } else {
                                    editText.setError(getActivity().getString(R.string.errore));

                                }
                            }
                        });

                        ann.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }else {
                        Intent intent = new Intent(getActivity(), Activity_password.class);
                        getActivity().startActivity(intent);
                    }
                    return true;
                }
            });
            backup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final Dialog dialog=new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_backup);
                    Switch swibackup= dialog.findViewById(R.id.switchbaclup);
                    final TextView imposta= dialog.findViewById(R.id.textimposta);
                    final TextView numero= dialog.findViewById(R.id.textnumbac);
                    TextView esegui= dialog.findViewById(R.id.textesebackup);
                    TextView ok= dialog.findViewById(R.id.okbackup);
                    final Spinner frequenza= dialog.findViewById(R.id.spinnerbackup);
                    final Spinner numerobackup= dialog.findViewById(R.id.spinnernumbackup);
                    final ViewGroup viewGroup= dialog.findViewById(R.id.posmessagio);
                    swibackup.setChecked(preferences.getBoolean("swibackup",false));
                    frequenza.setSelection(preferences.getInt("frequenza",0));
                    numerobackup.setSelection(preferences.getInt("numerobackup",2));
                    swibackup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (!isChecked){
                                imposta.setAlpha(0.4f);frequenza.setVisibility(View.INVISIBLE);
                                numero.setAlpha(0.4f);numerobackup.setVisibility(View.INVISIBLE);
                                preferences.edit().putBoolean("swibackup",false).apply();
                            }else{
                                imposta.setAlpha(1f);frequenza.setVisibility(View.VISIBLE);
                                numero.setAlpha(1f);numerobackup.setVisibility(View.VISIBLE);
                                preferences.edit().putBoolean("swibackup",true).apply();
                            }
                        }
                    });
                    frequenza.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            preferences.edit().putInt("frequenza",position).apply();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    numerobackup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            preferences.edit().putInt("numerobackup",position).apply();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    esegui.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Backup backup=new Backup(getActivity());
                            backup.backup();
                            Crouton.makeText(getActivity(),getString(R.string.backup_eseguito), CroutonStyle.CONFIRM,viewGroup).show();
                        }
                    });

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    if(swibackup.isChecked()){
                        imposta.setAlpha(1f);frequenza.setVisibility(View.VISIBLE);
                        numero.setAlpha(1f);numerobackup.setVisibility(View.VISIBLE);
                    }else{
                        imposta.setAlpha(0.4f);frequenza.setVisibility(View.INVISIBLE);
                        numero.setAlpha(0.4f);numerobackup.setVisibility(View.INVISIBLE);
                    }

                    dialog.show();

                    return true;
                }
            });
            ripristino.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final Ripristino ripristino =new Ripristino();
                    if (ripristino.getlista().length !=0) {

                        final Dialog dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.dialog_ripristno);
                        final Spinner spinner = dialog.findViewById(R.id.spinneripristno);
                        TextView ann = dialog.findViewById(R.id.textannrip);
                        TextView ok = dialog.findViewById(R.id.textokrip);
                        final ViewGroup viewGroup= dialog.findViewById(R.id.message);
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity()
                                , android.R.layout.simple_spinner_item, ripristino.getlista());
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                        ann.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (ripristino.restoreDatabase(spinner.getSelectedItem().toString())) {
                                    Crouton.makeText(getActivity(), "Ripristino in corso..", CroutonStyle.CONFIRM, viewGroup).show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                          //  getActivity().finish();
                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            getActivity().finish();
                                            getActivity().startActivity(intent);
                                        }
                                    }, 2000);

                                }else{
                                    Crouton.makeText(getActivity(), "Errore", CroutonStyle.CONFIRM, viewGroup).show();
                                }
                            }
                        });
                        dialog.show();
                    }else {
                        Toast.makeText(getActivity(),"nessun backup trovato",Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }

        @Override
        public void onResume() {
            super.onResume();
            if (preferences.getString(Costanti.PREF_PASSWORD,null) != null){
                password=true;
                imppass.setSummary("Modifica la Password");
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
           /*
            if(key.equals("disc")){
                if (disc.isEnabled()){
                    backup.setEnabled(false);
                    ripristino.setEnabled(false);
                }else{
                    backup.setEnabled(true);
                    ripristino.setEnabled(true);
                }
            }
            */
        }

    }
}
