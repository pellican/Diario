package com.agenda.diario.Diario.Testo;

import android.content.Context;
import android.graphics.Typeface;

public class TypeFace {
    Context context;
    public TypeFace(Context context) {
        this.context=context;

    }



   public   Typeface[] gettypefaces() {
       Typeface[] typeface = new Typeface[]{
               Typeface.DEFAULT,
               new Type_font(context, "font/f1.ttf").getTypeface(),
               new Type_font(context, "font/f2.ttf").getTypeface(),
               new Type_font(context, "font/f3.ttf").getTypeface(),
               new Type_font(context, "font/f4.ttf").getTypeface(),
               new Type_font(context, "font/f5.ttf").getTypeface(),
               new Type_font(context, "font/f6.ttf").getTypeface(),
               new Type_font(context, "font/f7.ttf").getTypeface(),
               new Type_font(context, "font/f8.ttf").getTypeface(),
               new Type_font(context, "font/f9.ttf").getTypeface(),
               new Type_font(context, "font/f10.ttf").getTypeface(),
               new Type_font(context, "font/f11.ttf").getTypeface(),
       };
       return typeface;
    }
}
