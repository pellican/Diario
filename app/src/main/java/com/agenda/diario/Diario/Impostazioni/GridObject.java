package com.agenda.diario.Diario.Impostazioni;

/**
 * Created by portatile on 04/06/2016.
 */
public class GridObject {



    private int emot;
    private int state;

    public GridObject(int emot, int state) {
        super();
        this.emot = emot;
        this.state = state;
    }


    public int getEmot() {
        return emot;
    }

    public void setEmot(int emot) {
        this.emot = emot;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}

