package com.agenda.diario.CustomView;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;

/**
 * Created by portatile on 26/06/2016.
 */
public class CustomEditText extends AppCompatEditText {

    Back back;
    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setBack(Back back){
        this.back=back;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back.BackPress();
            setCursorVisible(false);
        }

        return false;
    }

    public interface Back{
        void BackPress();
    }
}