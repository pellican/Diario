package com.agenda.diario.Diario;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;

import com.agenda.diario.Database.DbAdapterDiario;
import com.agenda.diario.R;
import com.agenda.diario.Singleton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by portatile on 03/08/2016.
 */
public class RecordActivity  extends AppCompatActivity {
    Chronometer chronometer;
    ImageView rec_stop;
    MediaRecorder recorder;
    String outputFile,data;
    boolean record=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        chronometer= findViewById(R.id.crono);
        rec_stop= findViewById(R.id.rec_stop);
        data= Singleton.getInstance().getDataS();
        if (data != null){
            Singleton.getInstance().setDataS(null);
        }
        recorder=new MediaRecorder();
        outputFile= Environment.getExternalStorageDirectory().getAbsolutePath();
        String cartella="/AgenDiario";
        File f=new File(Environment.getExternalStorageDirectory(),cartella);
        if (!f.exists()){
            f.mkdir();
        }
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss", Locale.getDefault()).format(new Date());
        outputFile += cartella+"/"+timeStamp+".3gp";
        Log.d(outputFile,timeStamp);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioEncodingBitRate(96000);
        recorder.setAudioSamplingRate(44100);
        recorder.setOutputFile(outputFile);
        rec_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!record) {
                    try {
                        recorder.prepare();
                        recorder.start();

                    } catch (Exception e) {

                    }
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    rec_stop.setImageResource(R.drawable.stop);
                    record=true;
                 }else {
                    recorder.stop();
                    recorder.release();
                    chronometer.stop();
                   // rec_stop.setImageResource(R.drawable.ic_record_grey600_24dp);

                    DbAdapterDiario db = new DbAdapterDiario(getApplicationContext());
                    db.open();
                    db.createContactAudio(data,outputFile,data,"recorder",R.drawable.microphone,null);
                    db.close();
                    finish();
                }
            }
        });

    }
}
