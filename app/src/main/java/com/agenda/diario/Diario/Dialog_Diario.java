package com.agenda.diario.Diario;



import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.agenda.diario.Diario.Testo.TestoActivity;
import com.agenda.diario.MainActivity;
import com.agenda.diario.Note.Frammento_Note;
import com.agenda.diario.R;

/**
 * Created by portatile on 31/12/2015.
 */
public class Dialog_Diario extends DialogFragment {
    public static int RESULT_LOAD_IMAGE = 1;
    public static int RESULT_LOAD_AUDIO = 2;
    public static int RESULT_LOAD_VIDEO =3;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(Dialog_Diario.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_diario, container);

        TextView textT = view.findViewById(R.id.textdiatesto);
        TextView immT = view.findViewById(R.id.textdiaimag);
        TextView vidT= view.findViewById(R.id.textdiavideo);
        TextView disegno= view.findViewById(R.id.textdiadisegno);
        TextView audio= view.findViewById(R.id.textdiaaudio);
        TextView rec= view.findViewById(R.id.textdiarecord);
        TextView annT = view.findViewById(R.id.textdiaann);


        annT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        immT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // final Fragment homeFragment = (Frammento_Diario) getActivity().getFragmentManager().findFragmentById(R.id.frameContainer);
              try{
                  Intent in = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                  getActivity().startActivityForResult(in, RESULT_LOAD_IMAGE);
              }catch (Exception e){
                  Toast.makeText(getContext(),R.string.errore, Toast.LENGTH_SHORT).show();
              }

                dismiss();
            }
        });
        vidT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent vi=new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    getActivity().startActivityForResult(vi,RESULT_LOAD_VIDEO);
                }catch (Exception e){
                    Toast.makeText(getContext(),R.string.errore, Toast.LENGTH_SHORT).show();
                }
                dismiss();
            }
        });
        disegno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity)getActivity();
                Fragment note = new Frammento_Note();
                mainActivity.switchContent(R.id.frameContainer,note,"note");
            }
        });
        textT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent te = new Intent(getActivity(),TestoActivity.class);
                getActivity().startActivity(te);
                dismiss();
            }
        });

        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent au = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    getActivity().startActivityForResult(au,RESULT_LOAD_AUDIO);
                }catch (Exception e){
                    Toast.makeText(getContext(),R.string.errore, Toast.LENGTH_SHORT).show();
                }

                dismiss();
            }
        });
        rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent re =new Intent(getActivity(),RecordActivity.class);
                getActivity().startActivity(re);
                dismiss();
            }
        });
        return view;
    }


}
