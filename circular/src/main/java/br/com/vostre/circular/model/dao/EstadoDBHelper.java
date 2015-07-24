package br.com.vostre.circular.model.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

import br.com.vostre.circular.model.Estado;

/**
 * Created by Almir on 14/04/2014.
 */
public class EstadoDBHelper extends SQLiteOpenHelper {

    private static final int DBVERSION = CircularDBHelper.DBVERSION;
    private static final String DBNAME = CircularDBHelper.DBNAME;
    public static final String TABELA = "estado";
    public static final String ID = "_id";
    public static final String NOME = "nome";
    public static final String SIGLA = "sigla";
    public static final String STATUS = "status";
    public static final String PAIS = "id_pais";
    public static final String DBCREATE = "CREATE TABLE "+TABELA+"( "+ID+" integer primary key, "
            +NOME+" text NOT NULL, "+SIGLA+" text NOT NULL, "+STATUS+" integer NOT NULL, "+PAIS+" integer NOT NULL);";
    CircularDBHelper circularDBHelper;

    public EstadoDBHelper(Context context){
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
        Log.w(EstadoDBHelper.class.getName(), "Atualização da Versão "+oldVersion+" para a Versão "+newVersion);
        //db.execSQL("DROP TABLE IF EXISTS "+TABELA);
        onCreate(db);
    }

    public List<Estado> listarTodos(Context context){
        EstadoDBAdapter adapter = new EstadoDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarTodos();
    }

    public List<Estado> listarTodosComVinculo(Context context){
        EstadoDBAdapter adapter = new EstadoDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarTodosComVinculo();
    }

    public long salvar(Context context, Estado estado){
        EstadoDBAdapter adapter = new EstadoDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.cadastrarEstado(estado);
    }

    public long salvarOuAtualizar(Context context, Estado estado){
        EstadoDBAdapter adapter = new EstadoDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.salvarOuAtualizar(estado);
    }

    public long deletar(Context context, Estado estado){
        EstadoDBAdapter adapter = new EstadoDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.deletarEstado(estado);
    }

    public long deletarInativos(Context context){
        EstadoDBAdapter adapter = new EstadoDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.deletarInativos();
    }

    public Estado carregar(Context context, Estado estado){
        EstadoDBAdapter adapter = new EstadoDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.carregar(estado);
    }

}
