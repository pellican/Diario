package com.agenda.diario.Diario.Testo;

import android.content.Context;
import android.graphics.Typeface;

public class Type_font {

    public Type_font(Context context,String font) {
        typeface= Typeface.createFromAsset(context.getAssets(),font);
    }
    public Typeface getTypeface() {
        return typeface;
    }
    Typeface typeface;
}
