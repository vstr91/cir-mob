package br.com.vostre.circular.model.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Parada;

/**
 * Created by Almir on 14/04/2014.
 */
public class ParadaDBHelper extends SQLiteOpenHelper {

    private static final int DBVERSION = CircularDBHelper.DBVERSION;
    private static final String DBNAME = CircularDBHelper.DBNAME;
    public static final String TABELA = "parada";
    public static final String ID = "_id";
    public static final String REFERENCIA = "referencia";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String STATUS = "status";
    public static final String BAIRRO = "id_bairro";
    public static final String DBCREATE = "CREATE TABLE "+TABELA+"( "+ID+" integer primary key, "
            +REFERENCIA+" text NOT NULL, "+LATITUDE+" text NOT NULL, "+LONGITUDE+" text NOT NULL, "+STATUS+" integer NOT NULL, "
            +BAIRRO+" integer NOT NULL, taxa_de_embarque real);";
    public static final String DBALTER_1 = "ALTER TABLE "+TABELA+" ADD taxa_de_embarque real;";
    CircularDBHelper circularDBHelper;

    public ParadaDBHelper(Context context){
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
        Log.w(ParadaDBHelper.class.getName(), "Atualização da Versão "+oldVersion+" para a Versão "+newVersion);
        //db.execSQL("DROP TABLE IF EXISTS "+TABELA);
        //onCreate(db);
    }

    public List<Parada> listarTodos(Context context){
        ParadaDBAdapter adapter = new ParadaDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarTodos();
    }

    public List<Parada> listarTodosComItinerario(Context context){
        ParadaDBAdapter adapter = new ParadaDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarTodosComItinerario();
    }

//    public List<Parada> listarTodosProximosComItinerario(Context context, Double distancia, Location location){
//        ParadaDBAdapter adapter = new ParadaDBAdapter(context, circularDBHelper.getReadableDatabase());
//        return adapter.listarTodosProximosComItinerario(distancia, location);
//    }

    public List<Parada> listarTodosPorBairro(Context context, Bairro bairro){
        ParadaDBAdapter adapter = new ParadaDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarTodosPorBairro(bairro);
    }

    public List<Parada> listarTodosComItinerarioPorBairro(Context context, Bairro bairro){
        ParadaDBAdapter adapter = new ParadaDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarTodosComItinerarioPorBairro(bairro);
    }

    public long salvarOuAtualizar(Context context, Parada parada){
        ParadaDBAdapter adapter = new ParadaDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.salvarOuAtualizar(parada);
    }

    public long deletarInativos(Context context){
        ParadaDBAdapter adapter = new ParadaDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.deletarInativos();
    }

    public Parada carregar(Context context, Parada parada){
        ParadaDBAdapter adapter = new ParadaDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.carregar(parada);
    }

}
