package com.agenda.diario.Diario;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.agenda.diario.CustomView.Display;
import com.agenda.diario.CustomView.TouchImageView;
import com.agenda.diario.R;
import com.agenda.diario.Singleton;
import com.bumptech.glide.Glide;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by portatile on 19/01/2016.
 */
public class FotoAdapter extends PagerAdapter {
    LayoutInflater inflater;
    String data;
    String[] foto,tag;
    Context mcontext;
    VideoView videovista;
    FotoClick fotoClick;
    public FotoAdapter(Context context,String[] foto,String[] tag) {
        this.mcontext=context;
        this.foto=foto;
        this.tag=tag;
       inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);



    }


    @Override
    public int getItemPosition(Object object) {

        return POSITION_NONE;

    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    @Override
    public int getCount() {
        return foto.length;
    }
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        if (tag[position].equals("image") ||tag[position].equals("disegno")) {
            TouchImageView rootView = new TouchImageView(mcontext);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                    .LayoutParams.MATCH_PARENT));
            Point dimensions = Display.getUsableSize(mcontext);

            Glide.with(mcontext)
                    .load(foto[position])
                    .apply(RequestOptions.fitCenterTransform()
                            .override(dimensions.x, dimensions.y)
                            .error(R.drawable.diario2))
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(rootView);
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fotoClick.onFotoClick();
                }
            });
            container.addView(rootView);
            return rootView;
        }else if (tag[position].equals("video")){
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rootView =inflater.inflate(R.layout.item_video,container,false);

            final VideoView videoView = rootView.findViewById(R.id.video);
            final ImageView play= rootView.findViewById(R.id.videoplay);
            final ImageView sfondo= rootView.findViewById(R.id.playsfondo);
            Glide.with(mcontext).load(foto[position]).into(sfondo);

            MediaController media =new MediaController(mcontext,false);
            media.setAnchorView(videoView);
            videoView.setMediaController(media);
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    videoView.setVideoURI(Uri.parse(foto[position]));
                    videoView.requestFocus();
                    videoView.start();

                     //   Play(position);
                        play.setVisibility(View.INVISIBLE);
                        sfondo.setVisibility(View.INVISIBLE);

                }
            });
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fotoClick.onFotoClick();
                }
            });
            container.addView(rootView);
            return rootView;
        }

        return null;
    }
    public void setFotoClick(FotoClick fotoClick){
        this.fotoClick=fotoClick;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
