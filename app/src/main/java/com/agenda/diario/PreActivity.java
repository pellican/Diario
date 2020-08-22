package com.agenda.diario;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.agenda.diario.Util.Costanti;
import com.agenda.diario.Util.CroutonStyle;
import com.agenda.diario.Util.PasswordValidator;
import com.agenda.diario.Util.Security;

import de.keyboardsurfer.android.widget.crouton.Crouton;


public class PreActivity extends AppCompatActivity {
    public SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs=getSharedPreferences(Costanti.PREFS_NAME,MODE_MULTI_PROCESS);
        super.onCreate(savedInstanceState);

    }

    public  void requestPassword(final AppCompatActivity mActivity, final PasswordValidator mPasswordValidator) {

        final Dialog dialog =new Dialog(mActivity,R.style.AppTheme);
        dialog.setContentView(R.layout.password);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final EditText passwordEditText = dialog.findViewById(R.id.editPassword);
        final Button sblocca= dialog.findViewById(R.id.textVentra);
        final TextView diment= dialog.findViewById(R.id.textpassdim);
        sblocca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String oldPassword = mActivity.getSharedPreferences(Costanti.PREFS_NAME, MODE_MULTI_PROCESS)
                        .getString(Costanti.PREF_PASSWORD, "");
                String password = passwordEditText.getText().toString();
                // The check is done on password's hash stored in preferences
                boolean result = Security.md5(password).equals(oldPassword);

                // In case password is ok dialog is dismissed and result sent to callback
                if (result) {
                   // hideKeyboard(passwordEditText);
                    mPasswordValidator.onPasswordValidated(true);
                    dialog.dismiss();
                    // If password is wrong the auth flow is not interrupted and simply a message is shown
                } else {
                    passwordEditText.setError(mActivity.getString(R.string.errore));
                    diment.setVisibility(View.VISIBLE);
                }

            }
        });
        diment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 passDimenticata(mActivity, new PasswordValidator() {
                    @Override
                    public void onPasswordValidated(boolean passwordConfirmed) {
                        if (passwordConfirmed){
                            mPasswordValidator.onPasswordValidated(true);
                            dialog.dismiss();
                        }
                    }
                });


            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                dialog.dismiss();
                mPasswordValidator.onPasswordValidated(false);
            }
        });

    dialog.show();
    }

    public void passDimenticata(final AppCompatActivity mActivity, final PasswordValidator passwordValidator){

        final Dialog dialog =new Dialog(mActivity);
        dialog.setContentView(R.layout.dialog_re_password);
        final EditText editText= dialog.findViewById(R.id.editrerisposta);
        Button ok= dialog.findViewById(R.id.butrepass);
        final ViewGroup viewGroup= dialog.findViewById(R.id.frame);
        TextView titolo= dialog.findViewById(R.id.titolo);
        dialog.setTitle(R.string.per_resettare);
        titolo.setText(prefs.getString(Costanti.PREF_PASSWORD_DOMANDA,""));

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldAnswer = prefs.getString(Costanti.PREF_PASSWORD_RISPOSTA, "");
                String answer1 = editText.getText().toString();
                // The check is done on password's hash stored in preferences
                boolean result = Security.md5(answer1).equals(oldAnswer);

                if (result) {
                    Crouton.makeText(mActivity,getString(R.string.resettato), CroutonStyle.WARN,viewGroup).show();

                    prefs.edit()
                            .putBoolean(Costanti.PREF_SWITCHPASS,false)
                            .remove(Costanti.PREF_PASSWORD)
                            .remove(Costanti.PREF_PASSWORD_DOMANDA)
                            .remove(Costanti.PREF_PASSWORD_RISPOSTA)
                            .apply();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            passwordValidator.onPasswordValidated(true);
                            dialog.dismiss();
                        }
                    },2000);

                } else {
                    editText.setError(getString(R.string.errore));
                }

            }
        });
        dialog.show();
    }

}
