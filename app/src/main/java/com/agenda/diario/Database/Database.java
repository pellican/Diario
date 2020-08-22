package com.agenda.diario.Database;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by portatile on 11/07/2016.
 */
public class Database implements Parcelable {


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getFotoDrive() {
        return fotoDrive;
    }

    public void setFotoDrive(String fotoDrive) {
        this.fotoDrive = fotoDrive;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    public int getEmoti() {
        return emoti;
    }

    public void setEmoti(int emoti) {
        this.emoti = emoti;
    }

    public int getVista() {
        return vista;
    }

    public void setVista(int vista) {
        this.vista = vista;
    }
    public String getOra() {
        return ora;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public String getNotifica() {
        return notifica;
    }

    public void setNotifica(String notifica) {
        this.notifica = notifica;
    }

    public String getTestoAg() {
        return testoAg;
    }

    public void setTestoAg(String testoAg) {
        this.testoAg = testoAg;
    }

    public static final int DIARIO=0;
    public static final int AGENDA=1;
    public static final int INSIEME=2;
        private String data;
        private String testo;
        private String foto;



        private String fotoDrive;
        private String titolo;
        private String ora;
        private String notifica;
        private String testoAg;
        private int categoria;
        private int emoti;
        private int vista;

    public Database() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.data);
        dest.writeString(this.testo);
        dest.writeString(this.foto);
        dest.writeString(this.fotoDrive);
        dest.writeString(this.titolo);
        dest.writeString(this.ora);
        dest.writeString(this.notifica);
        dest.writeString(this.testoAg);
        dest.writeInt(this.categoria);
        dest.writeInt(this.emoti);
        dest.writeInt(this.vista);
    }

    protected Database(Parcel in) {
        this.data = in.readString();
        this.testo = in.readString();
        this.foto = in.readString();
        this.fotoDrive = in.readString();
        this.titolo = in.readString();
        this.ora = in.readString();
        this.notifica = in.readString();
        this.testoAg = in.readString();
        this.categoria = in.readInt();
        this.emoti = in.readInt();
        this.vista = in.readInt();
    }

    public static final Creator<Database> CREATOR = new Creator<Database>() {
        @Override
        public Database createFromParcel(Parcel source) {
            return new Database(source);
        }

        @Override
        public Database[] newArray(int size) {
            return new Database[size];
        }
    };
}
