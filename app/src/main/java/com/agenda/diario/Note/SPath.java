package com.agenda.diario.Note;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;


import java.io.Serializable;

/**
 * Created by portatile on 17/08/2016.
 */
public class SPath extends Path implements Serializable {

    public SPath() {
    }

    public SPath(Path src) {
        super(src);
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    public void moveTo(float x, float y) {
        super.moveTo(x, y);
    }

    @Override
    public void lineTo(float x, float y) {
        super.lineTo(x, y);
    }

    @Override
    public void quadTo(float x1, float y1, float x2, float y2) {
        super.quadTo(x1, y1, x2, y2);
    }
}
