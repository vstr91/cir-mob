package br.com.vostre.circular.model.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.Mensagem;

/**
 * Created by Cefet on 27/08/2015.
 */
public class MensagemDBHelper extends SQLiteOpenHelper {

    private static final int DBVERSION = CircularDBHelper.DBVERSION;
    private static final String DBNAME = CircularDBHelper.DBNAME;
    public static final String TABELA = "mensagem";
    public static final String ID = "_id";
    public static final String TITULO = "titulo";
    public static final String DESCRICAO = "descricao";
    public static final String DATA_ENVIO = "data_envio";
    public static final String DATA_LEITURA = "data_leitura";
    public static final String STATUS = "status";
    public static final String DBCREATE = "CREATE TABLE "+TABELA+"( "+ID+" integer primary key, "
            +TITULO+" text NOT NULL, "+DESCRICAO+" text NOT NULL, "+DATA_ENVIO+" text NOT NULL, "+DATA_LEITURA+" text, "
            +STATUS+" integer NOT NULL);";
    CircularDBHelper circularDBHelper;

    public MensagemDBHelper(Context context){
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

    }

    public List<Mensagem> listarTodos(Context context){
        MensagemDBAdapter adapter = new MensagemDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarTodos();
    }

    public List<Mensagem> listarTodosNaoLidas(Context context){
        MensagemDBAdapter adapter = new MensagemDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarTodosNaoLidas();
    }

    public long salvarOuAtualizar(Context context, Mensagem mensagem){
        MensagemDBAdapter adapter = new MensagemDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.salvarOuAtualizar(mensagem);
    }

    public int marcarLida(Context context, Mensagem mensagem){
        MensagemDBAdapter adapter = new MensagemDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.marcarLida(mensagem);
    }

    public long deletar(Context context, Mensagem mensagem){
        MensagemDBAdapter adapter = new MensagemDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.deletar(mensagem);
    }

    public long deletarInativos(Context context){
        MensagemDBAdapter adapter = new MensagemDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.deletarInativos();
    }

    public Mensagem carregar(Context context, Mensagem mensagem){
        MensagemDBAdapter adapter = new MensagemDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.carregar(mensagem);
    }

}
