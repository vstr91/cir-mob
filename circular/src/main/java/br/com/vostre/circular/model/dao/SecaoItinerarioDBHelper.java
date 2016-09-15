package br.com.vostre.circular.model.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.SecaoItinerario;

/**
 * Created by Almir on 14/04/2014.
 */
public class SecaoItinerarioDBHelper extends SQLiteOpenHelper {

    private static final int DBVERSION = CircularDBHelper.DBVERSION;
    private static final String DBNAME = CircularDBHelper.DBNAME;
    public static final String DBCREATE = "CREATE TABLE secao_itinerario( _id integer primary key, " +
            "ordem integer NOT NULL, nome text NOT NULL, valor float NOT NULL, id_itinerario integer NOT NULL, status integer NOT NULL);";
    CircularDBHelper circularDBHelper;

    public SecaoItinerarioDBHelper(Context context){
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
        Log.w(SecaoItinerarioDBHelper.class.getName(), "Atualização da Versão "+oldVersion+" para a Versão "+newVersion);
        //db.execSQL("DROP TABLE IF EXISTS "+TABELA);
        //onCreate(db);
    }

    public List<SecaoItinerario> listarTodos(Context context){
        SecaoItinerarioDBAdapter adapter = new SecaoItinerarioDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarTodos();
    }

    public long salvarOuAtualizar(Context context, SecaoItinerario secaoItinerario){
        SecaoItinerarioDBAdapter adapter = new SecaoItinerarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.salvarOuAtualizar(secaoItinerario);
    }

    public long deletarInativos(Context context){
        SecaoItinerarioDBAdapter adapter = new SecaoItinerarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.deletarInativos();
    }

    public SecaoItinerario carregar(Context context, SecaoItinerario secaoItinerario){
        SecaoItinerarioDBAdapter adapter = new SecaoItinerarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.carregar(secaoItinerario);
    }

    public List<SecaoItinerario> listarTodasSecoesPorItinerario(Context context, Itinerario itinerario){
        SecaoItinerarioDBAdapter adapter = new SecaoItinerarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.listarTodasSecoesPorItinerario(itinerario);
    }

}
