package br.com.vostre.circular.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.ItinerarioLog;
import br.com.vostre.circular.model.Pais;

/**
 * Created by Almir on 27/05/2015.
 */
public class ItinerarioLogDBAdapter {

    private SQLiteDatabase database;
    private ItinerarioLogDBHelper itinerarioLogDBHelper;
    private Context context;

    public ItinerarioLogDBAdapter(Context context, SQLiteDatabase database){
        itinerarioLogDBHelper = new ItinerarioLogDBHelper(context);
        this.database = database;
        this.context = context;
    }

    public long cadastrar(ItinerarioLog itinerarioLog){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ContentValues cv = new ContentValues();
        cv.put("id_itinerario", itinerarioLog.getItinerario().getId());
        cv.put("data_consulta", df.format(itinerarioLog.getDataConsulta().getTime()));
        long insertId = database.insert(ItinerarioLogDBHelper.TABELA, null, cv);

        database.close();

        return insertId;
    }

    public List<ItinerarioLog> listarTodos(){
        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(context);
        Cursor cursor = database.rawQuery("SELECT DISTINCT id_itinerario FROM "+ItinerarioLogDBHelper.TABELA
                +" ORDER BY data_consulta DESC", null);

        List<ItinerarioLog> itinerarios = new ArrayList<ItinerarioLog>();

        if(cursor.moveToFirst()){
            do{
                ItinerarioLog itinerarioLog = new ItinerarioLog();

                Itinerario umItinerario = new Itinerario();
                umItinerario.setId(cursor.getInt(0));
                umItinerario = itinerarioDBHelper.carregar(context, umItinerario);

                itinerarioLog.setItinerario(umItinerario);

                itinerarios.add(itinerarioLog);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return itinerarios;
    }

}
