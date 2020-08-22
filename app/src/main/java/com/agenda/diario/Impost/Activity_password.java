package com.agenda.diario.Impost;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.agenda.diario.PreActivity;
import com.agenda.diario.R;
import com.agenda.diario.Util.Costanti;
import com.agenda.diario.Util.CroutonStyle;
import com.agenda.diario.Util.Security;



import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by portatile on 10/05/2016.
 */
public class Activity_password extends AppCompatActivity {
    Button ok;
    SharedPreferences prefs;
    EditText passins,passre,domanda,risposta;

    ViewGroup viewGroup;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_password);
        prefs=getSharedPreferences(Costanti.PREFS_NAME,MODE_MULTI_PROCESS);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        viewGroup=  findViewById(R.id.snackbarpos);
        passins= findViewById(R.id.editinspass);
        passre= findViewById(R.id.editrepass);
        domanda= findViewById(R.id.editdomanda);
        risposta= findViewById(R.id.editrisposta);

        ok= findViewById(R.id.butpass);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkData()){
                    if (passins.getText().toString().length() == 0 ){
                        Crouton.makeText(Activity_password.this,R.string.password_vuota, CroutonStyle.ALERT,viewGroup).show();
                    }else {
                        Crouton.makeText(Activity_password.this,R.string.password_salvata,CroutonStyle.CONFIRM,viewGroup).show();
                        prefs.edit()
                                .putString(Costanti.PREF_PASSWORD, Security.md5(passins.getText().toString()))
                                .putString(Costanti.PREF_PASSWORD_DOMANDA, domanda.getText().toString())
                                .putString(Costanti.PREF_PASSWORD_RISPOSTA, Security.md5(risposta.getText().toString()))
                                .apply();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },2500);
                    }
                }
            }
        });

    }
    private boolean checkData() {
        boolean res = true;

        if (passins.getText().length() == passre.getText().length()
                && passre.getText().length() == 0) {
            return true;
        }
        boolean passwordOk = passins.getText().toString().length() > 0;
        boolean passwordCheckOk = passre.getText().toString().length() > 0 && passins.getText().toString()
                .equals(passre.getText().toString());
        boolean questionOk = domanda.getText().toString().length() > 0;
        boolean answerOk = risposta.getText().toString().length() > 0;

        if (!passwordOk || !passwordCheckOk || !questionOk || !answerOk) {
            res = false;
            if (!passwordOk) {
                passins.setError(getString(R.string.errore_password));
            }
            if (!passwordCheckOk) {
                passre.setError(getString(R.string.errore_password));
            }
            if (!questionOk) {
                domanda.setError(getString(R.string.domanda));
            }
            if (!answerOk) {
                risposta.setError(getString(R.string.risposta_vuota));
            }
        }
        return res;
    }
}
