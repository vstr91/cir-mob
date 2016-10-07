package br.com.vostre.circular.model.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.LocalColeta;
import br.com.vostre.circular.model.LocalColeta;

/**
 * Created by Almir on 14/04/2014.
 */
public class LocalColetaDBHelper extends SQLiteOpenHelper {

    private static final int DBVERSION = CircularDBHelper.DBVERSION;
    private static final String DBNAME = CircularDBHelper.DBNAME;

    public static final String DBCREATE = "CREATE TABLE local_coleta ( _id integer primary key, nome text NOT NULL, id_estado integer NOT NULL, " +
            "id_cidade integer NOT NULL, status integer NOT NULL, data_coleta text NOT NULL);";
    CircularDBHelper circularDBHelper;

    public LocalColetaDBHelper(Context context){
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
        Log.w(LocalColetaDBHelper.class.getName(), "Atualização da Versão "+oldVersion+" para a Versão "+newVersion);
        //db.execSQL("DROP TABLE IF EXISTS "+TABELA);
        //onCreate(db);
    }

    public List<LocalColeta> listarTodos(Context context) {
        LocalColetaDBAdapter adapter = new LocalColetaDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarTodos();
    }

    public List<LocalColeta> listarTodosNaoEnviados(Context context) {
        LocalColetaDBAdapter adapter = new LocalColetaDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarTodosNaoEnviados();
    }

    public List<LocalColeta> listarTodosEnviados(Context context) {
        LocalColetaDBAdapter adapter = new LocalColetaDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarTodosEnviados();
    }

    public long salvarOuAtualizar(Context context, LocalColeta localColeta){
        LocalColetaDBAdapter adapter = new LocalColetaDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.salvarOuAtualizar(localColeta);
    }

    public LocalColeta carregar(Context context, LocalColeta localColeta) {
        LocalColetaDBAdapter adapter = new LocalColetaDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.carregar(localColeta);
    }

    public int excluir(Context context, LocalColeta localColeta){
        LocalColetaDBAdapter adapter = new LocalColetaDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.excluir(localColeta);
    }

    public long deletarCadastrados(Context context){
        LocalColetaDBAdapter adapter = new LocalColetaDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.deletarCadastrados();
    }

}
