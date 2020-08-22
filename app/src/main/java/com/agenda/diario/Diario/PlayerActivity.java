package com.agenda.diario.Diario;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.agenda.diario.R;

import java.util.concurrent.TimeUnit;

/**
 * Created by portatile on 31/07/2016.
 */
public class PlayerActivity extends AppCompatActivity {
    private MediaPlayer mp;
    private SeekBar seekBar;
    private ImageView play_stop;
    private Handler durationHandler = new Handler();
    private double timeElapsed = 0, finalTime = 0;


    TextView titoloT,artistaT,timeT;
    String canzone,titolo,artista;
    String minuti,secondi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        seekBar= findViewById(R.id.seekbar);
        play_stop= findViewById(R.id.play_stop);
        titoloT= findViewById(R.id.playertitolo);
        artistaT= findViewById(R.id.playercantante);
        timeT= findViewById(R.id.playertime);
        Intent intent=getIntent();
        canzone=intent.getStringExtra("canzone");
        titolo=intent.getStringExtra("titolo");
        artista=intent.getStringExtra("artista");



        titoloT.setText(titolo);
        artistaT.setText(artista);
        if(mp == null){
            mp=new MediaPlayer();
            Uri uri= Uri.parse(canzone);
            Log.d(canzone,titolo+artista+uri.toString());
            try {

                mp.setDataSource(getApplicationContext(),uri);
                mp.prepare();
            }catch (Exception e){

            }

          //  mp=MediaPlayer.create(getApplicationContext(),uri);
            finalTime=mp.getDuration();
            seekBar.setEnabled(true);
            mp.start();
            play_stop.setImageResource(R.drawable.ic_pause_circle_outline_grey600_24dp);
            seekBar.setMax((int)finalTime);
            durationHandler.postDelayed(updateSeekBarTime, 100);

        }
        play_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mp.isPlaying()) {
                    mp.pause();
                    play_stop.setImageResource(R.drawable.ic_play_circle_outline_grey600_24dp);

                } else {
                    mp.start();
                    play_stop.setImageResource(R.drawable.ic_pause_circle_outline_grey600_24dp);
                    seekBar.setMax(mp.getDuration());
                    durationHandler.postDelayed(updateSeekBarTime, 100);

                }




            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                try {
                    if (mp.isPlaying() || mp != null) {
                        if (b)
                            mp.seekTo(i);
                    }
                } catch (Exception e) {
                    Log.e("seek bar", "" + e);
                    seekBar.setEnabled(false);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                play_stop.setImageResource(R.drawable.ic_play_circle_outline_grey600_24dp);
            }
        });
    }
    private Runnable updateSeekBarTime = new Runnable() {

        public void run() {
            timeElapsed = mp.getCurrentPosition();
            seekBar.setProgress((int)timeElapsed);
            double timeRemain = finalTime-timeElapsed;
            int minute=(int)TimeUnit.MILLISECONDS.toMinutes((long)timeRemain);
            if (minute<10){
                 minuti="0"+minute;
            }else minuti=""+minute;
            int secons=(int)TimeUnit.MILLISECONDS.toSeconds((long)timeRemain)- (int)TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemain));
            if (secons<10){
                secondi="0"+secons;
            }else secondi=""+secons;
            /*
            timeT.setText(String.format(Locale.getDefault(),"%d : %d",TimeUnit.MILLISECONDS.toMinutes((long)timeRemain)
                    ,TimeUnit.MILLISECONDS.toSeconds((long)timeRemain)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemain))));
            */
            timeT.setText(minuti+":"+secondi);

            durationHandler.postDelayed(this, 100);

        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null && mp.isPlaying()){
            mp.stop();
        }
    }
}
