package br.com.vostre.circular.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.SecaoItinerario;

/**
 * Created by Almir on 14/04/2014.
 */
public class SecaoItinerarioDBAdapter {

    private SQLiteDatabase database;
    private SecaoItinerarioDBHelper secaoItinerarioDBHelper;
    private Context context;

    public SecaoItinerarioDBAdapter(Context context, SQLiteDatabase database){
        secaoItinerarioDBHelper = new SecaoItinerarioDBHelper(context);
        this.database = database;
        this.context = context;
    }

    public long salvarOuAtualizar(SecaoItinerario secaoItinerario){
        Long retorno;
        ContentValues cv = new ContentValues();
        cv.put("_id", secaoItinerario.getId());
        cv.put("ordem", secaoItinerario.getOrdem());
        cv.put("id_itinerario", secaoItinerario.getItinerario().getId());
        cv.put("nome", secaoItinerario.getNome());
        cv.put("valor", secaoItinerario.getValor());
        cv.put("status", secaoItinerario.getStatus());

        if(database.update("secao_itinerario", cv, "_id = "+secaoItinerario.getId(), null) < 1){
            retorno = database.insert("secao_itinerario", null, cv);
            database.close();
            return retorno;
        } else{
            database.close();
            return -1;
        }

    }

    public int deletarInativos(){
        int retorno = database.delete("secao_itinerario", "status = "+2, null);
        database.close();
        return retorno;
    }

    public List<SecaoItinerario> listarTodos(){
        Cursor cursor = database.rawQuery("SELECT _id, id_itinerario, " +
                "ordem, valor, nome, status FROM secao_itinerario", null);
        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(context);
        List<SecaoItinerario> secoesItinerarios = new ArrayList<SecaoItinerario>();

        if(cursor.moveToFirst()){
            do{
                SecaoItinerario umaSI = new SecaoItinerario();
                umaSI.setId(cursor.getInt(0));

                Itinerario umItinerario = new Itinerario();
                umItinerario.setId(cursor.getInt(1));
                umItinerario = itinerarioDBHelper.carregar(context, umItinerario);

                umaSI.setItinerario(umItinerario);

                umaSI.setOrdem(cursor.getInt(2));
                umaSI.setValor(cursor.getDouble(3));
                umaSI.setNome(cursor.getString(4));
                umaSI.setStatus(cursor.getInt(5));

               secoesItinerarios.add(umaSI);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return secoesItinerarios;
    }

    public SecaoItinerario carregar(SecaoItinerario secaoItinerario){
        Cursor cursor = database.rawQuery("SELECT _id, id_itinerario, " +
                "ordem, valor, nome, status FROM secao_itinerario WHERE _id = ?",
                new String[]{String.valueOf(secaoItinerario.getId())});
        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(context);

        SecaoItinerario umaSI = null;

        if(cursor.moveToFirst()){
            do{
                umaSI.setId(cursor.getInt(0));

                Itinerario umItinerario = new Itinerario();
                umItinerario.setId(cursor.getInt(1));
                umItinerario = itinerarioDBHelper.carregar(context, umItinerario);

                umaSI.setItinerario(umItinerario);

                umaSI.setOrdem(cursor.getInt(2));
                umaSI.setValor(cursor.getDouble(3));
                umaSI.setNome(cursor.getString(4));
                umaSI.setStatus(cursor.getInt(5));

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return umaSI;
    }

    public List<SecaoItinerario> listarTodasSecoesPorItinerario(Itinerario itinerario){
        Cursor cursor = database.rawQuery("SELECT _id, id_itinerario, ordem, valor, nome, status " +
                        "FROM secao_itinerario WHERE id_itinerario = ?",
                new String[]{String.valueOf(itinerario.getId())});
        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(context);
        List<SecaoItinerario> secoes = new ArrayList<SecaoItinerario>();

        if(cursor.moveToFirst()){
            do{
                SecaoItinerario umaSI = new SecaoItinerario();
                umaSI.setId(cursor.getInt(0));

                Itinerario umItinerario = new Itinerario();
                umItinerario.setId(cursor.getInt(1));
                umItinerario = itinerarioDBHelper.carregar(context, umItinerario);

                umaSI.setItinerario(umItinerario);

                umaSI.setOrdem(cursor.getInt(2));
                umaSI.setValor(cursor.getDouble(3));
                umaSI.setNome(cursor.getString(4));
                umaSI.setStatus(cursor.getInt(5));

                secoes.add(umaSI);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return secoes;
    }

}
