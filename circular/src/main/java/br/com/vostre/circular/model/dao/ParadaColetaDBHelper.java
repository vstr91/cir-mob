package br.com.vostre.circular.model.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.ParadaColeta;

/**
 * Created by Almir on 14/04/2014.
 */
public class ParadaColetaDBHelper extends SQLiteOpenHelper {

    private static final int DBVERSION = CircularDBHelper.DBVERSION;
    private static final String DBNAME = CircularDBHelper.DBNAME;
    public static final String TABELA = "parada_coleta";
    public static final String ID = "_id";
    public static final String REFERENCIA = "referencia";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String STATUS = "status";
    public static final String BAIRRO = "id_bairro";
    public static final String DATA_COLETA = "data_coleta";
    public static final String ENVIADO = "enviado";
    public static final String DBCREATE = "CREATE TABLE "+TABELA+"( "+ID+" integer primary key, "
            +REFERENCIA+" text NOT NULL, "+LATITUDE+" text NOT NULL, "+LONGITUDE+" text NOT NULL, "+STATUS+" integer NOT NULL, "
            +BAIRRO+" integer NOT NULL, "+DATA_COLETA+" text NOT NULL, "+ENVIADO+" integer NOT NULL);";
    CircularDBHelper circularDBHelper;

    public ParadaColetaDBHelper(Context context){
        super(context, DBNAME, null, DBVERSION);
        circularDBHelper = new CircularDBHelper(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
     //db.execSQL(DBCREATE);
     //db.execSQL("INSERT INTO pais (nome, iso3, status) VALUES ('Brasil', 'BRA', 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.w(ParadaColetaDBHelper.class.getName(), "Atualização da Versão "+oldVersion+" para a Versão "+newVersion);
        //db.execSQL("DROP TABLE IF EXISTS "+TABELA);
        //onCreate(db);
    }

    public List<ParadaColeta> listarTodos(Context context) throws ParseException {
        ParadaColetaDBAdapter adapter = new ParadaColetaDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarTodos();
    }

    public List<ParadaColeta> listarTodosPorBairro(Context context, Bairro bairro) throws ParseException {
        ParadaColetaDBAdapter adapter = new ParadaColetaDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarTodosPorBairro(bairro);
    }

    public long salvarOuAtualizar(Context context, ParadaColeta paradaColeta){
        ParadaColetaDBAdapter adapter = new ParadaColetaDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.salvarOuAtualizar(paradaColeta);
    }

    public ParadaColeta carregar(Context context, ParadaColeta paradaColeta) throws ParseException {
        ParadaColetaDBAdapter adapter = new ParadaColetaDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.carregar(paradaColeta);
    }

    public int excluir(Context context, ParadaColeta paradaColeta){
        ParadaColetaDBAdapter adapter = new ParadaColetaDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.excluir(paradaColeta);
    }

}
