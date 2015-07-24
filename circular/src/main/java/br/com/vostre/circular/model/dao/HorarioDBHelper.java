package br.com.vostre.circular.model.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Horario;

/**
 * Created by Almir on 14/04/2014.
 */
public class HorarioDBHelper extends SQLiteOpenHelper {

    private static final int DBVERSION = CircularDBHelper.DBVERSION;
    private static final String DBNAME = CircularDBHelper.DBNAME;
    public static final String TABELA = "horario";
    public static final String ID = "_id";
    public static final String NOME = "nome";
    public static final String STATUS = "status";
    public static final String DBCREATE = "CREATE TABLE "+TABELA+"( "+ID+" integer primary key, "
            +NOME+" text NOT NULL, "+STATUS+" integer NOT NULL);";
    CircularDBHelper circularDBHelper;

    public HorarioDBHelper(Context context){
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
        Log.w(HorarioDBHelper.class.getName(), "Atualização da Versão "+oldVersion+" para a Versão "+newVersion);
        //db.execSQL("DROP TABLE IF EXISTS "+TABELA);
        //onCreate(db);
    }

    public List<Horario> listarTodos(Context context){
        HorarioDBAdapter adapter = new HorarioDBAdapter(context, circularDBHelper.getReadableDatabase());
        return adapter.listarTodos();
    }

    public long salvarOuAtualizar(Context context, Horario horario){
        HorarioDBAdapter adapter = new HorarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.salvarOuAtualizar(horario);
    }

    public long deletar(Context context, Horario horario){
        HorarioDBAdapter adapter = new HorarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.deletar(horario);
    }

    public long deletarInativos(Context context){
        HorarioDBAdapter adapter = new HorarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.deletarInativos();
    }

    public Horario carregar(Context context, Horario horario){
        HorarioDBAdapter adapter = new HorarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.carregar(horario);
    }

    public Horario listarProximoHorarioItinerario(Context context, Bairro partida, Bairro destino, String hora){
        HorarioDBAdapter adapter = new HorarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.listarProximoHorarioItinerario(partida, destino, hora);
    }

    public Horario listarPrimeiroHorarioItinerario(Context context, Bairro partida, Bairro destino, Calendar dia){
        HorarioDBAdapter adapter = new HorarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.listarPrimeiroHorarioItinerario(partida, destino, dia);
    }

    public List<Horario> listarTodosHorariosPorItinerario(Context context, Bairro bairroPartida, Bairro bairroDestino){
        HorarioDBAdapter adapter = new HorarioDBAdapter(context, circularDBHelper.getWritableDatabase());
        return adapter.listarTodosHorariosPorItinerario(bairroPartida, bairroDestino);
    }

}
