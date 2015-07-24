package br.com.vostre.circular.model.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Almir on 07/08/2014.
 */
public class CircularDBHelper extends SQLiteOpenHelper {

    public static final int DBVERSION = 1;
    public static final String DBNAME = "circular.db";

    public CircularDBHelper(Context context){
        super(context, DBNAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PaisDBHelper.DBCREATE);
        db.execSQL(HorarioDBHelper.DBCREATE);
        db.execSQL(EstadoDBHelper.DBCREATE);
        db.execSQL(LocalDBHelper.DBCREATE);
        db.execSQL(BairroDBHelper.DBCREATE);
        db.execSQL(ParadaDBHelper.DBCREATE);
        db.execSQL(EmpresaDBHelper.DBCREATE);
        db.execSQL(ItinerarioDBHelper.DBCREATE);
        db.execSQL(ParadaItinerarioDBHelper.DBCREATE);
        db.execSQL(HorarioItinerarioDBHelper.DBCREATE);

        db.execSQL(ParadaColetaDBHelper.DBCREATE);
        db.execSQL(ItinerarioLogDBHelper.DBCREATE);

        db.execSQL(ParametroDBHelper.DBCREATE);
        db.execSQL(ParametroDBHelper.DBPOPULATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*Log.w(HorarioDBHelper.class.getName(), "Atualização da Versão " + oldVersion + " para a Versão " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS "+PaisDBHelper.TABELA);
        db.execSQL("DROP TABLE IF EXISTS "+HorarioDBHelper.TABELA);
        db.execSQL("DROP TABLE IF EXISTS "+ EstadoDBHelper.TABELA);
        db.execSQL("DROP TABLE IF EXISTS "+LocalDBHelper.TABELA);
        db.execSQL("DROP TABLE IF EXISTS "+BairroDBHelper.TABELA);
        db.execSQL("DROP TABLE IF EXISTS "+ParadaDBHelper.TABELA);
        db.execSQL("DROP TABLE IF EXISTS "+EmpresaDBHelper.TABELA);
        db.execSQL("DROP TABLE IF EXISTS "+ItinerarioDBHelper.TABELA);
        db.execSQL("DROP TABLE IF EXISTS "+ParadaItinerarioDBHelper.TABELA);
        db.execSQL("DROP TABLE IF EXISTS "+HorarioItinerarioDBHelper.TABELA);

        db.execSQL("DROP TABLE IF EXISTS "+ ParadaColetaDBHelper.TABELA);
        //db.execSQL("DROP TABLE IF EXISTS "+ ItinerarioLogDBHelper.TABELA);

        onCreate(db);*/
    }
}
