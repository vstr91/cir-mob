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

/**
 * Created by Almir on 14/04/2014.
 */
public class HorarioItinerarioDBHelper extends SQLiteOpenHelper {

    private static final int DBVERSION = CircularDBHelper.DBVERSION;
    private static final String DBNAME = CircularDBHelper.DBNAME;
    public static final String TABELA = "horario_itinerario";
    public static final String ID = "_id";
    public static final String HORARIO = "id_horario";
    public static final String ITINERARIO = "id_itinerario";
    public static final String DOMINGO = "domingo";
    public static final String SEGUNDA = "segunda";
    public static final String TERCA = "terca";
    public static final String QUARTA = "quarta";
    public static final String QUINTA = "quinta";
    public static final String SEXTA = "sexta";
    public static final String SABADO = "sabado";
    public static final String STATUS = "status";
    public static final String OBS = "obs";
    public static final String DBCREATE = "CREATE TABLE "+TABELA+"( "+ID+" integer primary key, "
            +HORARIO+" integer NOT NULL, "+ITINERARIO+" integer NOT NULL, "+DOMINGO+" integer NOT NULL, "
            +SEGUNDA+" integer NOT NULL, "+TERCA+" integer NOT NULL, "+QUARTA+" integer NOT NULL, "
            +QUINTA+" integer NOT NULL, "+SEXTA+" integer NOT NULL, "+SABADO+" integer NOT NULL, "+STATUS+" integer NOT NULL, "+OBS+" text);";

    public static final String DBALTER_1 = "ALTER TABLE "+TABELA+" ADD obs text;";

    CircularDBHelper circularDBHelper;

    public HorarioItinerarioDBHelper(Context context){
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
        Log.w(HorarioItinerarioDBHelper.class.getName(), "Atualização da Versão "+oldVersion+" para a Versão "+newVersion);
        //db.execSQL("DROP TABLE IF EXISTS "+TABELA);
        //onCreate(db);
    }

    public List<HorarioItinerario> listarTodos(Context context){
        HorarioItinerarioDBAdapter adapter = new HorarioItinerarioDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarTodos();
    }

    public long salvarOuAtualizar(Context context, HorarioItinerario horarioItinerario){
        HorarioItinerarioDBAdapter adapter = new HorarioItinerarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.salvarOuAtualizar(horarioItinerario);
    }

    public long deletarInativos(Context context){
        HorarioItinerarioDBAdapter adapter = new HorarioItinerarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.deletarInativos();
    }

    public HorarioItinerario carregar(Context context, HorarioItinerario horarioItinerario){
        HorarioItinerarioDBAdapter adapter = new HorarioItinerarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.carregar(horarioItinerario);
    }

    public List<HorarioItinerario> listarTodosHorariosPorItinerario(Context context, Bairro bairroPartida, Bairro bairroDestino){
        HorarioItinerarioDBAdapter adapter = new HorarioItinerarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.listarTodosHorariosPorItinerario(bairroPartida, bairroDestino);
    }

    public List<HorarioItinerario> listarTodosHorariosPorItinerario(Context context, Itinerario itinerario){
        HorarioItinerarioDBAdapter adapter = new HorarioItinerarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.listarTodosHorariosPorItinerario(itinerario);
    }

    public HorarioItinerario listarProximoHorarioItinerario(Context context, Bairro partida, Bairro destino, String hora){
        HorarioItinerarioDBAdapter adapter = new HorarioItinerarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.listarProximoHorarioItinerario(partida, destino, hora);
    }

    public HorarioItinerario listarPrimeiroHorarioItinerario(Context context, Bairro partida, Bairro destino, Calendar dia){
        HorarioItinerarioDBAdapter adapter = new HorarioItinerarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.listarPrimeiroHorarioItinerario(partida, destino, dia);
    }

}
