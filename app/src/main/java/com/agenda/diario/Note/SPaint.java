package com.agenda.diario.Note;

import android.graphics.Paint;

import java.io.Serializable;

/**
 * Created by portatile on 17/08/2016.
 */
public class SPaint extends Paint implements Serializable {

    public SPaint(Paint paint) {
        super(paint);
    }

    public SPaint() {
    }
}
