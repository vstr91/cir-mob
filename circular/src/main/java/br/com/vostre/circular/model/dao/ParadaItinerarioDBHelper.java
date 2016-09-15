package br.com.vostre.circular.model.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;

/**
 * Created by Almir on 14/04/2014.
 */
public class ParadaItinerarioDBHelper extends SQLiteOpenHelper {

    private static final int DBVERSION = CircularDBHelper.DBVERSION;
    private static final String DBNAME = CircularDBHelper.DBNAME;
    public static final String TABELA = "parada_itinerario";
    public static final String ID = "_id";
    public static final String PARADA = "id_parada";
    public static final String ITINERARIO = "id_itinerario";
    public static final String ORDEM = "ordem";
    public static final String STATUS = "status";
    public static final String DESTAQUE = "destaque";
    public static final String VALOR = "valor";
    public static final String DBCREATE = "CREATE TABLE "+TABELA+"( "+ID+" integer primary key, "
            +PARADA+" integer NOT NULL, "+ITINERARIO+" integer NOT NULL, "+ORDEM+" integer NOT NULL, "
            +STATUS+" integer NOT NULL, "+DESTAQUE+" integer NOT NULL, "+VALOR+" real);";
    CircularDBHelper circularDBHelper;

    public ParadaItinerarioDBHelper(Context context){
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
        Log.w(ParadaItinerarioDBHelper.class.getName(), "Atualização da Versão "+oldVersion+" para a Versão "+newVersion);
        //db.execSQL("DROP TABLE IF EXISTS "+TABELA);
        //onCreate(db);
    }

    public List<ParadaItinerario> listarTodos(Context context){
        ParadaItinerarioDBAdapter adapter = new ParadaItinerarioDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarTodos();
    }

    public List<ParadaItinerario> listaParadasDestaquePorItinerario(Context context, Bairro partida, Bairro destino){
        ParadaItinerarioDBAdapter adapter = new ParadaItinerarioDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listaParadasDestaquePorItinerario(partida, destino);
    }

    public List<ParadaItinerario> listaParadasDestaquePorItinerario(Context context, Itinerario itinerario){
        ParadaItinerarioDBAdapter adapter = new ParadaItinerarioDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listaParadasDestaquePorItinerario(itinerario);
    }

    public long salvarOuAtualizar(Context context, ParadaItinerario paradaItinerario){
        ParadaItinerarioDBAdapter adapter = new ParadaItinerarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.salvarOuAtualizar(paradaItinerario);
    }

    public long deletarInativos(Context context){
        ParadaItinerarioDBAdapter adapter = new ParadaItinerarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.deletarInativos();
    }

    public ParadaItinerario carregar(Context context, ParadaItinerario paradaItinerario){
        ParadaItinerarioDBAdapter adapter = new ParadaItinerarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.carregar(paradaItinerario);
    }

    public Parada carregarParadaEmbarque(Context context, Itinerario itinerario){
        ParadaItinerarioDBAdapter adapter = new ParadaItinerarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.carregarParadaEmbarque(itinerario);
    }

}
