package com.agenda.diario.Note;

import android.util.Pair;

import java.io.Serializable;

/**
 * Created by portatile on 17/08/2016.
 */
public class Pari <F,S> implements Serializable{
    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    private final F first;
     private final S second;

    public Pari( F first, S second) {
        this.first=first;
        this.second=second;
    }
    @Override
    public int hashCode() { return first.hashCode() ^ second.hashCode(); }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pari pairo = (Pari) o;
        return this.first.equals(pairo.getFirst()) &&
                this.second.equals(pairo.getSecond());
    }
}
