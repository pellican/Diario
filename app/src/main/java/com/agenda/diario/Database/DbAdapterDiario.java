package com.agenda.diario.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by portatile on 03/01/2016.
 */
public class DbAdapterDiario {
    private static final String DATABASE_TABLE_pagina = "pagina";
    private static final String DATABASE_TABLE_foto="foto";
    private static final String DATABASE_TABLE_testo="testo";
    private static final String DATABASE_TABLE_audio="audio";
    private static final String DATABASE_TABLE_agenda="agenda";
    private static final String DATABASE_TABLE_filtro="filtro";
    private static final String DATABASE_TABLE_cestino="cestino";
    private static final String KEY_CONTACTID = "_id";
    private static final String KEY_DATA = "data";
    private static final String KEY_DATE = "date";
    private static final String KEY_MESE = "mese";
    private static final String KEY_SFONDO = "sfondo";
    private static final String KEY_EMOTICON = "emoticon";
    private static final String KEY_PAGINA_ID = "pagina_id";
    private static final String KEY_TESTO = "testo";
    private static final String KEY_COLORE = "colore";
    private static final String KEY_CORNICE = "cornice";
    public static final String KEY_TIPO = "tipo";
    public static final String KEY_CARATTERE = "carattere";
    public static final String KEY_DIM = "dim";
    public static final String KEY_FOTO = "foto";
    public static final String KEY_AUDIO = "audio";
    public static final String KEY_CATEGORIA="categoria";
    public static final String KEY_TITOLO="titolo";
    public static final String KEY_ORA="ora";
    public static final String KEY_ORATESTO="oratesto";
    public static final String KEY_NOTIFICA="notifica";
    public static final String KEY_FILTRO="filtro";
    public static final String KEY_VISTA="vista";
    public static final String KEY_ARTISTA="artista";
    public static final String KEY_TAG="tag";
    private static final String KEY_DRIVEID="driveid";
    private static final String KEY_ELIMINA="elimina";

    private Context context;
    private SQLiteDatabase database;
    private DatabaseHelperDiario dbHelper;

    static {
        DbAdapterDiario.class.getSimpleName();
    }

    public DbAdapterDiario(Context context) {
        this.context = context;
    }

    public DbAdapterDiario open() throws SQLException {
        this.dbHelper = new DatabaseHelperDiario(this.context);
        this.database = this.dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        this.dbHelper.close();
    }

    private ContentValues createContentValuesPagina(String date,String data,String mese, int sfondo,int emoticon,String categoria,String titolo) {
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, date);
        values.put(KEY_DATA, data);
        values.put(KEY_MESE,mese);
        values.put(KEY_SFONDO, sfondo);
        values.put(KEY_EMOTICON,emoticon);
        values.put(KEY_CATEGORIA,categoria);
        values.put(KEY_TITOLO,titolo);

        return values;
    }



    private ContentValues createValuesFoto(String pagina_id, String foto,String tag,String driveid) {
        ContentValues values = new ContentValues();
        values.put(KEY_PAGINA_ID, pagina_id);
        values.put(KEY_FOTO, foto);
        values.put(KEY_TAG,tag);
        values.put(KEY_DRIVEID,driveid);
        return values;
    }

    private ContentValues createValuesTesto(String pagina_id,String testo,String colore,int cornice, int tipo,int carattre,int dim) {
        ContentValues values = new ContentValues();
        values.put(KEY_PAGINA_ID, pagina_id);
        values.put(KEY_TESTO, testo);
        values.put(KEY_COLORE,colore);
        values.put(KEY_CORNICE,cornice);
        values.put(KEY_TIPO,tipo);
        values.put(KEY_CARATTERE,carattre);
        values.put(KEY_DIM,dim);
        return values;
    }

    private ContentValues createValuesAudio(String pagina_id,String audio,String titolo,String artista,int tag,String driveid) {
        ContentValues values = new ContentValues();
        values.put(KEY_PAGINA_ID, pagina_id);
        values.put(KEY_AUDIO, audio);
        values.put(KEY_TITOLO,titolo);
        values.put(KEY_ARTISTA,artista);
        values.put(KEY_TAG,tag);
        values.put(KEY_DRIVEID,driveid);
        return values;
    }
    private ContentValues createValueAgenda(String data,String mese,int ora,String oratesto,String testo,String notifica){
        ContentValues values = new ContentValues();
        values.put(KEY_DATA,data);
        values.put(KEY_MESE,mese);
        values.put(KEY_ORA,ora);
        values.put(KEY_ORATESTO,oratesto);
        values.put(KEY_TESTO,testo);
        values.put(KEY_NOTIFICA, notifica);

        return  values;
    }
    private ContentValues createValueFiltro(int filtro,int categoria,int vista){
        ContentValues values = new ContentValues();
        values.put(KEY_FILTRO,filtro);
        values.put(KEY_CATEGORIA,categoria);
        values.put(KEY_VISTA,vista);
        return values;
    }
    private ContentValues createValueCestino(String elimina) {
        ContentValues values = new ContentValues();
        values.put(KEY_ELIMINA,elimina);
        return values;
    }

    public long createContactPagina(String date,String data,String mese, int sfondo,int emoticon,String categoria,String titolo) {
        return this.database.insertOrThrow(DATABASE_TABLE_pagina, null, createContentValuesPagina(date,data,mese,sfondo,emoticon,categoria,titolo));
    }
    public long createContactFoto(String pagina_id,String foto,String tag,String driveid) {
        return this.database.insertOrThrow(DATABASE_TABLE_foto, null, createValuesFoto(pagina_id, foto,tag,driveid));
    }
    public long createContactTesto(String pagina_id,String testo,String colore,int cornice, int tipo,int carattre,int dim) {
        return this.database.insertOrThrow(DATABASE_TABLE_testo, null, createValuesTesto(pagina_id,testo, colore, cornice, tipo, carattre,dim));
    }
    public long createContactAudio(String pagina_id,String audio,String titolo,String artista,int tag,String driveid) {
        return this.database.insertOrThrow(DATABASE_TABLE_audio, null, createValuesAudio(pagina_id, audio,titolo,artista,tag,driveid));
    }
    public long createContactFiltro(int filtro,int categoria,int vista) {
        return this.database.insertOrThrow(DATABASE_TABLE_filtro, null, createValueFiltro(filtro,categoria,vista));
    }

    public long createContactCestino(String elimina) {
        return this.database.insertOrThrow(DATABASE_TABLE_cestino, null, createValueCestino(elimina));
    }
    public Cursor fethAllCestino(){
        return this.database.query(DATABASE_TABLE_cestino, new String[]{KEY_CONTACTID,KEY_ELIMINA},
                null, null, null,null,null);
    }
    public boolean deleteCestino(long contactID) {
        return this.database.delete(DATABASE_TABLE_cestino, new StringBuilder("_id=").append(contactID).toString(), null) > 0;
    }



    public boolean updateContactPagina(long contactID,String date,String data,String mese, int sfondo,int emoticon,String categoria,String titolo) {
        return this.database.update(DATABASE_TABLE_pagina, createContentValuesPagina(date,data,mese, sfondo,emoticon,categoria,titolo),
                new StringBuilder("_id=").append(contactID).toString(), null) > 0;
    }
    public boolean updateAllSfondo(int sfondo){
        ContentValues values=new ContentValues();
        values.put(KEY_SFONDO,sfondo);
        return  database.update(DATABASE_TABLE_pagina,values,null,null) >0;

    }
    public boolean updateFoto(long contactID,String pagina_id, String foto,String tag,String driveid) {
        return this.database.update(DATABASE_TABLE_foto, createValuesFoto(pagina_id, foto,tag,driveid), "_id=" + contactID, null) > 0;
    }
    public boolean updateIDFoto(long contactID,String pagina_id, String foto,String tag,String driveid) {
        return this.database.update(DATABASE_TABLE_foto, createValuesFoto(pagina_id, foto,tag,driveid), "_id=" + contactID, null) > 0;
    }
    public boolean updateTesto(long contactID,String pagina_id,String testo,String colore,int cornice,int tipo,int carattre,int dim) {
        return this.database.update(DATABASE_TABLE_testo, createValuesTesto(pagina_id,testo,colore,cornice, tipo,carattre,dim), "_id=" + contactID, null) > 0;
    }
    public boolean updateAudio(long contactID,String pagina_id,String audio,String titilo,String artista,int tag,String driveid) {
        return this.database.update(DATABASE_TABLE_audio, createValuesAudio(pagina_id, audio,titilo,artista,tag,driveid), new StringBuilder("_id=").append(contactID).toString(), null) > 0;
    }
    public boolean updateFiltro(long contactID,int filtro,int categoria,int vista) {
        return this.database.update(DATABASE_TABLE_filtro, createValueFiltro(filtro,categoria,vista), new StringBuilder("_id=").append(contactID).toString(), null) > 0;
    }

    public boolean deleteContactPagina(String data) {
        return this.database.delete(DATABASE_TABLE_pagina, KEY_DATA + "=?", new String[]{data}) >0;
    }
    public boolean updatePaginaData(String date,String data,String mese, int sfondo,int emoticon,String categoria,String titolo) {
        return this.database.update(DATABASE_TABLE_pagina, createContentValuesPagina(date,data,mese, sfondo,emoticon,categoria,titolo),
                new StringBuilder("data=").append(data).toString(), null) > 0;
    }

    public boolean deleteFoto(long contactID) {
        return this.database.delete(DATABASE_TABLE_foto, new StringBuilder("_id=").append(contactID).toString(), null) > 0;
    }


    public boolean deleteTesto(long contactID) {
        return this.database.delete(DATABASE_TABLE_testo, new StringBuilder("_id=").append(contactID).toString(), null) > 0;
    }
    public boolean deleteAudio(long contactID) {
        return this.database.delete(DATABASE_TABLE_audio, new StringBuilder("_id=").append(contactID).toString(), null) > 0;
    }
    public boolean deleteFiltro(long contactID) {
        return this.database.delete(DATABASE_TABLE_filtro, new StringBuilder("_id=").append(contactID).toString(), null) > 0;
    }


    public void removeAllbyData(String data) {
        this.database.delete(DATABASE_TABLE_pagina,"data = '"+data+"'", null);
        this.database.delete(DATABASE_TABLE_foto,"pagina_id = '"+data+"'", null);
        this.database.delete(DATABASE_TABLE_testo, "pagina_id = '"+data+"'", null);
        this.database.delete(DATABASE_TABLE_audio, "pagina_id = '"+data+"'", null);
    }

    public Cursor fetchAllContactsPaginaAsc() {
        return this.database.query(DATABASE_TABLE_pagina, new String[]{KEY_CONTACTID,KEY_DATE,KEY_DATA,KEY_MESE,KEY_SFONDO,KEY_EMOTICON,KEY_CATEGORIA,KEY_TITOLO},
                null, null, null,null,"date ASC");
    }
    public Cursor fetchAllContactsPaginaDisc() {
        return this.database.query(DATABASE_TABLE_pagina, new String[]{KEY_CONTACTID,KEY_DATE,KEY_DATA,KEY_MESE,KEY_SFONDO,KEY_EMOTICON,KEY_CATEGORIA,KEY_TITOLO},
                null, null, null,null,"date DESC");
    }
    public Cursor fetchAllContactsPagina() {
        return this.database.query(DATABASE_TABLE_pagina, new String[]{KEY_CONTACTID,KEY_DATE,KEY_DATA,KEY_MESE,KEY_SFONDO,KEY_EMOTICON,KEY_CATEGORIA,KEY_TITOLO},
                null, null, null,null,null);
    }

    public Cursor fetchPaginaByData(String filter) {
        String where = KEY_DATA+"=?" ;
        return this.database.query(DATABASE_TABLE_pagina, new String[]{KEY_CONTACTID,KEY_DATE,KEY_DATA,KEY_MESE, KEY_SFONDO,KEY_EMOTICON,KEY_CATEGORIA,KEY_TITOLO}, where,new String[]{filter}, null, null, null);
    }
    public Cursor fetchPaginaByMese(String filter) {
        String where = KEY_MESE+"=?" ;
        return this.database.query(DATABASE_TABLE_pagina, new String[]{KEY_CONTACTID,KEY_DATE,KEY_DATA,KEY_MESE, KEY_SFONDO,KEY_EMOTICON,KEY_CATEGORIA,KEY_TITOLO}, where,new String[]{filter}, null, null, null);
    }
    public Cursor fetchPaginaByCategoria(String filter) {
        String where =KEY_CATEGORIA+"=?" ;
        return this.database.query(DATABASE_TABLE_pagina, new String[]{KEY_CONTACTID,KEY_DATE,KEY_DATA,KEY_MESE, KEY_SFONDO,KEY_EMOTICON,KEY_CATEGORIA,KEY_TITOLO}, where,new String[]{filter}, null, null, "date ASC");
    }

    public Cursor fetchfiltro(){
        return  this.database.query(DATABASE_TABLE_filtro,new String[]{KEY_CONTACTID,KEY_FILTRO,KEY_CATEGORIA,KEY_VISTA},null,null,null,null,null);
    }


    public Cursor fetchFotoById(long filter) {
        String where = KEY_CONTACTID+"='"+filter+"'" ;
        return this.database.query(DATABASE_TABLE_foto, new String[]{KEY_CONTACTID,KEY_PAGINA_ID,KEY_FOTO,KEY_TAG,KEY_DRIVEID}, where,null, null, null, null);
    }
    public  Cursor fetchAllFoto(){
        return  this.database.query(DATABASE_TABLE_foto,new String[]{KEY_CONTACTID,KEY_PAGINA_ID,KEY_FOTO,KEY_TAG,KEY_DRIVEID},null,null,null,null,null);
    }
    public Cursor fetchFotoByData(String filter) {
        String where = KEY_PAGINA_ID+"=?" ;
        return this.database.query(DATABASE_TABLE_foto, new String[]{KEY_CONTACTID,KEY_PAGINA_ID,KEY_FOTO,KEY_TAG,KEY_DRIVEID}, where,new String[]{filter}, null, null, null);
    }

    public Cursor fetchTestoByData(String filter) {
        String where = KEY_PAGINA_ID+"=?" ;
        return this.database.query(DATABASE_TABLE_testo, new String[]{KEY_CONTACTID,KEY_PAGINA_ID,KEY_TESTO,KEY_COLORE,KEY_CORNICE,KEY_TIPO,KEY_CARATTERE,KEY_DIM}, where,new String[]{filter}, null, null, null);
    }
    public Cursor fetchAudioById(long filter) {
        String where = KEY_PAGINA_ID+"='"+filter+"'" ;
        return this.database.query(DATABASE_TABLE_audio, new String[]{KEY_CONTACTID,KEY_PAGINA_ID,KEY_AUDIO,KEY_TITOLO,KEY_ARTISTA,KEY_TAG,KEY_DRIVEID}, where,null, null, null, null);
    }

    public Cursor fetchAudioByData(String filter) {
        String where = KEY_PAGINA_ID+"=?" ;
        return this.database.query(DATABASE_TABLE_audio, new String[]{KEY_CONTACTID,KEY_PAGINA_ID,KEY_AUDIO,KEY_TITOLO,KEY_ARTISTA,KEY_TAG,KEY_DRIVEID}, where,new String[]{filter}, null, null, null);
    }
    public  Cursor fetchAllAudio(){
        return  this.database.query(DATABASE_TABLE_audio,new String[]{KEY_CONTACTID,KEY_PAGINA_ID,KEY_AUDIO,KEY_TITOLO,KEY_ARTISTA,KEY_TAG,KEY_DRIVEID},null,null,null,null,null);
    }


    public long createContactAgenda(String data,String mese,int ora,String oratesto,String testo, String notifica) {
        return this.database.insertOrThrow(DATABASE_TABLE_agenda, null, createValueAgenda(data,mese,ora,oratesto,testo,notifica));
    }
    public boolean updateContactAgenda(long contactID,String data,String mese,int ora,String oratesto,String testo, String notifica) {
        return this.database.update(DATABASE_TABLE_agenda, createValueAgenda(data,mese, ora,oratesto, testo, notifica), new StringBuilder("_id=").append(contactID).toString(), null) > 0;
    }
    public boolean deleteAgenda(long contactID) {
        return this.database.delete(DATABASE_TABLE_agenda, new StringBuilder("_id=").append(contactID).toString(), null) > 0;
    }
    public Cursor fetchAgendaByData(String data) {
        String where = KEY_DATA+"=?";
        return this.database.query(DATABASE_TABLE_agenda, new String[]{KEY_CONTACTID,KEY_DATA,KEY_MESE,KEY_ORA,KEY_ORATESTO,KEY_TESTO, KEY_NOTIFICA}, where,new String[]{data}, null, null,"oratesto ASC");
    }
    public Cursor fetchAgendaByMese(String mese) {
        String where = KEY_MESE+"=?";
        return this.database.query(DATABASE_TABLE_agenda, new String[]{KEY_CONTACTID,KEY_DATA,KEY_MESE,KEY_ORA,KEY_ORATESTO,KEY_TESTO, KEY_NOTIFICA}, where,new String[]{mese}, null, null,"oratesto ASC");
    }
    public Cursor fetchContactsAgendaOrd() {

        return this.database.query(DATABASE_TABLE_agenda, new String[]{KEY_CONTACTID,KEY_DATA,KEY_MESE,KEY_ORA,KEY_ORATESTO,KEY_TESTO, KEY_NOTIFICA}, null,null, null, null,null);
    }
    public Cursor fetchPaginaByData_Ora(String data,String ora) {
        String where = KEY_DATA+"=? and"+KEY_ORA+"=?" ;
        return this.database.query(DATABASE_TABLE_agenda, new String[]{KEY_CONTACTID,KEY_DATA,KEY_MESE,KEY_ORA,KEY_ORATESTO,KEY_TESTO, KEY_NOTIFICA}, where,new String[]{data,ora}, null, null, null);
    }


}
