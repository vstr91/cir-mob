package br.com.vostre.circular.model.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import br.com.vostre.circular.model.Pais;

/**
 * Created by Almir on 11/06/2015.
 */
public class ParametroDBHelper extends SQLiteOpenHelper {

    private static final int DBVERSION = CircularDBHelper.DBVERSION;
    private static final String DBNAME = CircularDBHelper.DBNAME;
    public static final String TABELA = "parametro";
    public static final String ULTIMO_ACESSO = "ultimo_acesso";
    public static final String DBCREATE = "CREATE TABLE "+TABELA+" ("+ULTIMO_ACESSO+" text);";
    public static final String DBPOPULATE = "INSERT INTO "+TABELA+" ("+ULTIMO_ACESSO+") VALUES ('-');";
    CircularDBHelper circularDBHelper;

    public ParametroDBHelper(Context context){
        super(context, DBNAME, null, DBVERSION);
        circularDBHelper = new CircularDBHelper(context);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    public String carregarUltimoAcesso(Context context){
        ParametroDBAdapter adapter = new ParametroDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.carregarUltimoAcesso();
    }

    public void gravarUltimoAcesso(Context context, String ultimoAcesso){
        ParametroDBAdapter adapter = new ParametroDBAdapter(context, circularDBHelper.getReadableDatabase());
        adapter.gravarUltimoAcesso(ultimoAcesso);
    }

}
