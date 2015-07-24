package br.com.vostre.circular.model.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

import br.com.vostre.circular.model.ItinerarioLog;

/**
 * Created by Almir on 27/05/2015.
 */
public class ItinerarioLogDBHelper extends SQLiteOpenHelper {

    private static final int DBVERSION = CircularDBHelper.DBVERSION;
    private static final String DBNAME = CircularDBHelper.DBNAME;
    public static final String TABELA = "itinerario_log";
    public static final String ID_ITINERARIO = "id_itinerario";
    public static final String DATA_CONSULTA = "data_consulta";
    public static final String DBCREATE = "CREATE TABLE "+TABELA+"( "
            +ID_ITINERARIO+" integer NOT NULL, "+DATA_CONSULTA+" text NOT NULL);";
    CircularDBHelper circularDBHelper;

    public ItinerarioLogDBHelper(Context context){
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
        Log.w(PaisDBHelper.class.getName(), "Atualização da Versão " + oldVersion + " para a Versão " + newVersion);
        //db.execSQL("DROP TABLE IF EXISTS "+TABELA);
        //onCreate(db);
    }

    public List<ItinerarioLog> listarTodos(Context context){
        ItinerarioLogDBAdapter adapter = new ItinerarioLogDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarTodos();
    }

    public long salvar(Context context, ItinerarioLog itinerarioLog){
        ItinerarioLogDBAdapter adapter = new ItinerarioLogDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.cadastrar(itinerarioLog);
    }

}
