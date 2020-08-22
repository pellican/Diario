package com.agenda.diario.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by portatile on 03/01/2016.
 */
public class DatabaseHelperDiario extends SQLiteOpenHelper {
        private static final String DATABASE_PAGINA = "create table pagina (_id integer primary key autoincrement," +
                " date text not null, data text not null,mese text not null, sfondo integer,emoticon integer,categoria text,titolo text);";

        private static final String DATABASE_FOTO="create table foto(_id integer primary key autoincrement, pagina_id text, foto text,tag text,driveid text);";
        private static final String DATABASE_TESTO="create table testo(_id integer primary key autoincrement, pagina_id text, testo text, colore text,cornice integer,tipo integer,carattere integer,dim integer );";
        private static final String DATABASE_AUDIO="create table audio(_id integer primary key autoincrement, pagina_id text, audio text,titolo text,artista text,tag integer,driveid text);";
        private static final String DATABASE_AGENDA="create table agenda (_id integer primary key autoincrement," +
            " data text ,mese text , ora integer, oratesto text , testo text ,notifica text );";
        private static final String DATABASE_FILTRO="create table filtro(_id integer primary key autoincrement,filtro integer,categoria integer,vista integer);";
    private static final String DATABASE_CESTINO="create table cestino(_id integer primary key autoincrement,elimina text);";

        private static final String DATABASE_NAME = "database_diario.db";
        private static final int DATABASE_VERSION = 1;

        public DatabaseHelperDiario(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase database) {
            database.execSQL(DATABASE_PAGINA);
            database.execSQL(DATABASE_FOTO);
            database.execSQL(DATABASE_TESTO);
            database.execSQL(DATABASE_AUDIO);
            database.execSQL(DATABASE_AGENDA);
            database.execSQL(DATABASE_FILTRO);
            database.execSQL(DATABASE_CESTINO);
        }

        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            database.execSQL("DROP TABLE IF EXISTS pagina");
            database.execSQL("DROP TABLE IF EXISTS foto");
            database.execSQL("DROP TABLE IF EXISTS testo");
            database.execSQL("DROP TABLE IF EXISTS audio");
            database.execSQL("DROP TABLE IF EXISTS agenda");
            database.execSQL("DROP TABLE IF EXISTS filtro");
            database.execSQL("DROP TABLE IF EXISTS cestino");
            onCreate(database);
        }
    }

