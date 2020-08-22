package com.agenda.diario;

import java.util.Calendar;


public class Singleton {
    private static Singleton mInstance;
    public Calendar data=null;
    private String dataS=null;
    private String dataTitolo=null;


    private Singleton() {
    }
    static {
        mInstance = null;
    }
    public static Singleton getInstance() {
        if (mInstance == null) {
            mInstance = new Singleton();
        }
        return mInstance;
    }

    public String getDataS() {
        return dataS;
    }

    public void setDataS(String dataS) {
        this.dataS = dataS;
    }




    public Calendar getData() {
        return data;
    }

    public void setData(Calendar data) {
        this.data = data;
    }

    public String getDataTitolo() {
        return dataTitolo;
    }

    public void setDataTitolo(String dataTitolo) {
        this.dataTitolo = dataTitolo;
    }

}
