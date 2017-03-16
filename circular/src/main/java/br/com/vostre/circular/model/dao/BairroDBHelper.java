package br.com.vostre.circular.model.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Local;

/**
 * Created by Almir on 14/04/2014.
 */
public class BairroDBHelper extends SQLiteOpenHelper {

    private static final int DBVERSION = CircularDBHelper.DBVERSION;
    private static final String DBNAME = CircularDBHelper.DBNAME;
    public static final String TABELA = "bairro";
    public static final String ID = "_id";
    public static final String NOME = "nome";
    public static final String STATUS = "status";
    public static final String LOCAL = "id_local";
    public static final String DBCREATE = "CREATE TABLE "+TABELA+"( "+ID+" integer primary key, "
            +NOME+" text NOT NULL, "+STATUS+" integer NOT NULL, "+LOCAL+" integer NOT NULL, slug text);";
    public static final String DBALTER_1 = "ALTER TABLE "+TABELA+" ADD slug text;";
    CircularDBHelper circularDBHelper;

    public BairroDBHelper(Context context){
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
        Log.w(BairroDBHelper.class.getName(), "Atualização da Versão "+oldVersion+" para a Versão "+newVersion);
        //db.execSQL("DROP TABLE IF EXISTS "+TABELA);
        //onCreate(db);
    }

    public List<Bairro> listarTodos(Context context){
        BairroDBAdapter adapter = new BairroDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarTodos();
    }

    public List<Bairro> listarTodosPorLocal(Context context, Local local){
        BairroDBAdapter adapter = new BairroDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarTodosPorLocal(local);
    }

    public List<Bairro> listarTodosVinculadosPorLocal(Context context, Local local){
        BairroDBAdapter adapter = new BairroDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarTodosVinculadosPorLocal(local);
    }

    public List<Bairro> listarPartidaPorItinerario(Context context, Local local){
        BairroDBAdapter adapter = new BairroDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarPartidaPorItinerario(local);
    }

    public List<Bairro> listarDestinoPorPartida(Context context, Bairro bairro){
        BairroDBAdapter adapter = new BairroDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarDestinoPorPartida(bairro);
    }

    public long salvarOuAtualizar(Context context, Bairro bairro){
        BairroDBAdapter adapter = new BairroDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.salvarOuAtualizar(bairro);
    }

    public long deletarInativos(Context context){
        BairroDBAdapter adapter = new BairroDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.deletarInativos();
    }

    public Bairro carregar(Context context, Bairro bairro){
        BairroDBAdapter adapter = new BairroDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.carregar(bairro);
    }

}
