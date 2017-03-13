package br.com.vostre.circular.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;

/**
 * Created by Almir on 14/04/2014.
 */
public class ParadaItinerarioDBAdapter {

    private SQLiteDatabase database;
    private ParadaItinerarioDBHelper paradaItinerarioDBHelper;
    private String[] colunas = {paradaItinerarioDBHelper.ID, paradaItinerarioDBHelper.PARADA, paradaItinerarioDBHelper.ITINERARIO,
            paradaItinerarioDBHelper.ORDEM, paradaItinerarioDBHelper.STATUS, paradaItinerarioDBHelper.DESTAQUE};
    private Context context;

    public ParadaItinerarioDBAdapter(Context context, SQLiteDatabase database){
        paradaItinerarioDBHelper = new ParadaItinerarioDBHelper(context);
        this.database = database;
        this.context = context;
    }

    public long salvarOuAtualizar(ParadaItinerario paradaItinerario){
        long retorno;
        ContentValues cv = new ContentValues();
        cv.put(paradaItinerarioDBHelper.ID, paradaItinerario.getId());
        cv.put(paradaItinerarioDBHelper.PARADA, paradaItinerario.getParada().getId());
        cv.put(paradaItinerarioDBHelper.ITINERARIO, paradaItinerario.getItinerario().getId());
        cv.put(paradaItinerarioDBHelper.ORDEM, paradaItinerario.getOrdem());
        cv.put(paradaItinerarioDBHelper.STATUS, paradaItinerario.getStatus());
        cv.put(paradaItinerarioDBHelper.DESTAQUE, paradaItinerario.getDestaque());
        cv.put(paradaItinerarioDBHelper.VALOR, paradaItinerario.getValor());

        if(database.update(ParadaItinerarioDBHelper.TABELA, cv,  paradaItinerarioDBHelper.ID+" = "+paradaItinerario.getId(), null) < 1){
            retorno = database.insert(ParadaItinerarioDBHelper.TABELA, null, cv);
            database.close();
            return retorno;
        } else{
            database.close();
            return -1;
        }

    }

    public int deletarInativos(){
        int retorno = database.delete(ParadaItinerarioDBHelper.TABELA, paradaItinerarioDBHelper.STATUS+" = "+2, null);
        database.close();
        return retorno;
    }

    public List<ParadaItinerario> listarTodos(){
        Cursor cursor = database.rawQuery("SELECT _id, id_parada, id_itinerario, ordem, status, destaque, valor FROM "+ParadaItinerarioDBHelper.TABELA, null);
        ParadaDBHelper paradaDBHelper = new ParadaDBHelper(context);
        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(context);
        List<ParadaItinerario> paradasItinerarios = new ArrayList<ParadaItinerario>();

        if(cursor.moveToFirst()){
            do{
                ParadaItinerario umaParadaItinerario = new ParadaItinerario();
                umaParadaItinerario.setId(cursor.getInt(0));

                Parada umaParada = new Parada();
                umaParada.setId(cursor.getInt(1));
                umaParada = paradaDBHelper.carregar(context, umaParada);

                umaParadaItinerario.setParada(umaParada);

                Itinerario umItinerario = new Itinerario();
                umItinerario.setId(cursor.getInt(2));
                umItinerario = itinerarioDBHelper.carregar(context, umItinerario);

                umaParadaItinerario.setItinerario(umItinerario);

                umaParadaItinerario.setOrdem(cursor.getInt(3));
                umaParadaItinerario.setStatus(cursor.getInt(4));
                umaParadaItinerario.setDestaque(cursor.getInt(5));
                umaParadaItinerario.setValor(cursor.getDouble(6));

               paradasItinerarios.add(umaParadaItinerario);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return paradasItinerarios;
    }

    public List<ParadaItinerario> listaParadasDestaquePorItinerario(Bairro partida, Bairro destino){
        Cursor cursor = database.rawQuery("SELECT pit._id, pit.id_parada, pit.id_itinerario, pit.ordem, pit.status, pit.destaque, valor FROM "
                +ParadaItinerarioDBHelper.TABELA+" pit INNER JOIN "
                +ItinerarioDBHelper.TABELA+" i ON i._id = pit.id_itinerario AND i.id_partida = ? AND i.id_destino = ? " +
                        "WHERE pit.destaque = -1",
                new String[]{String.valueOf(partida.getId()), String.valueOf(destino.getId())});
        ParadaDBHelper paradaDBHelper = new ParadaDBHelper(context);
        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(context);
        List<ParadaItinerario> paradasItinerarios = new ArrayList<ParadaItinerario>();

        if(cursor.moveToFirst()){
            do{
                ParadaItinerario umaParadaItinerario = new ParadaItinerario();
                umaParadaItinerario.setId(cursor.getInt(0));

                Parada umaParada = new Parada();
                umaParada.setId(cursor.getInt(1));
                umaParada = paradaDBHelper.carregar(context, umaParada);

                umaParadaItinerario.setParada(umaParada);

                Itinerario umItinerario = new Itinerario();
                umItinerario.setId(cursor.getInt(2));
                umItinerario = itinerarioDBHelper.carregar(context, umItinerario);

                umaParadaItinerario.setItinerario(umItinerario);

                umaParadaItinerario.setOrdem(cursor.getInt(3));
                umaParadaItinerario.setStatus(cursor.getInt(4));
                umaParadaItinerario.setDestaque(cursor.getInt(5));
                umaParadaItinerario.setValor(cursor.getDouble(6));

                paradasItinerarios.add(umaParadaItinerario);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return paradasItinerarios;
    }

    public List<ParadaItinerario> listaParadasDestaquePorItinerario(Itinerario itinerario){

        Cursor cursor = database.rawQuery("SELECT pit._id, pit.id_parada, pit.id_itinerario, pit.ordem, pit.status, pit.destaque, pit.valor FROM "
                        +ParadaItinerarioDBHelper.TABELA+" pit INNER JOIN "
                        +ItinerarioDBHelper.TABELA+" i ON i._id = pit.id_itinerario AND i._id = ?" +
                        "WHERE pit.destaque = -1",
                new String[]{String.valueOf(itinerario.getId())});
        ParadaDBHelper paradaDBHelper = new ParadaDBHelper(context);
        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(context);
        List<ParadaItinerario> paradasItinerarios = new ArrayList<ParadaItinerario>();

        if(cursor.moveToFirst()){
            do{
                ParadaItinerario umaParadaItinerario = new ParadaItinerario();
                umaParadaItinerario.setId(cursor.getInt(0));

                Parada umaParada = new Parada();
                umaParada.setId(cursor.getInt(1));
                umaParada = paradaDBHelper.carregar(context, umaParada);

                umaParadaItinerario.setParada(umaParada);

                Itinerario umItinerario = new Itinerario();
                umItinerario.setId(cursor.getInt(2));
                umItinerario = itinerarioDBHelper.carregar(context, umItinerario);

                umaParadaItinerario.setItinerario(umItinerario);

                umaParadaItinerario.setOrdem(cursor.getInt(3));
                umaParadaItinerario.setStatus(cursor.getInt(4));
                umaParadaItinerario.setDestaque(cursor.getInt(5));
                umaParadaItinerario.setValor(cursor.getDouble(6));

                paradasItinerarios.add(umaParadaItinerario);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return paradasItinerarios;
    }

    public ParadaItinerario carregar(ParadaItinerario paradaItinerario){
        Cursor cursor = database.rawQuery("SELECT _id, id_parada, id_itinerario, ordem, status, destaque, valor FROM "+paradaItinerarioDBHelper.TABELA
                +" WHERE _id = ?", new String[]{String.valueOf(paradaItinerario.getId())});
        ParadaDBHelper paradaDBHelper = new ParadaDBHelper(context);
        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(context);

        ParadaItinerario umaParadaItinerario = null;

        if(cursor.moveToFirst()){
            do{
                umaParadaItinerario = new ParadaItinerario();
                umaParadaItinerario.setId(cursor.getInt(0));

                Parada umaParada = new Parada();
                umaParada.setId(cursor.getInt(1));
                umaParada = paradaDBHelper.carregar(context, umaParada);

                umaParadaItinerario.setParada(umaParada);

                Itinerario umItinerario = new Itinerario();
                umItinerario.setId(cursor.getInt(2));
                umItinerario = itinerarioDBHelper.carregar(context, umItinerario);

                umaParadaItinerario.setItinerario(umItinerario);

                umaParadaItinerario.setOrdem(cursor.getInt(3));
                umaParadaItinerario.setStatus(cursor.getInt(4));
                umaParadaItinerario.setDestaque(cursor.getInt(5));
                umaParadaItinerario.setValor(cursor.getDouble(6));

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return umaParadaItinerario;
    }

    public Parada carregarParadaEmbarque(Itinerario itinerario){
        Cursor cursor = database.rawQuery("SELECT _id, id_parada, id_itinerario, ordem, status, destaque, valor FROM "+paradaItinerarioDBHelper.TABELA
                +" WHERE id_itinerario = ? AND ordem = 1", new String[]{String.valueOf(itinerario.getId())});
        ParadaDBHelper paradaDBHelper = new ParadaDBHelper(context);
        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(context);

        ParadaItinerario umaParadaItinerario = null;

        if(cursor.moveToFirst()){
            do{
                umaParadaItinerario = new ParadaItinerario();
                umaParadaItinerario.setId(cursor.getInt(0));

                Parada umaParada = new Parada();
                umaParada.setId(cursor.getInt(1));
                umaParada = paradaDBHelper.carregar(context, umaParada);

                umaParadaItinerario.setParada(umaParada);

                Itinerario umItinerario = new Itinerario();
                umItinerario.setId(cursor.getInt(2));
                umItinerario = itinerarioDBHelper.carregar(context, umItinerario);

                umaParadaItinerario.setItinerario(umItinerario);

                umaParadaItinerario.setOrdem(cursor.getInt(3));
                umaParadaItinerario.setStatus(cursor.getInt(4));
                umaParadaItinerario.setDestaque(cursor.getInt(5));
                umaParadaItinerario.setValor(cursor.getDouble(6));

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return umaParadaItinerario.getParada();
    }

    public Double verificarTarifaTrecho(Itinerario itinerario, Parada parada){
        Cursor cursor = database.rawQuery("SELECT pit.valor FROM "+paradaItinerarioDBHelper.TABELA
                +" pit INNER JOIN "+ItinerarioDBHelper.TABELA+" i INNER JOIN "+ParadaDBHelper.TABELA+" p WHERE i.id_destino = ? " +
                "AND   p.id_bairro = ? " +
                "AND   pit.destaque = -1", new String[]{String.valueOf(itinerario.getDestino().getId()), String.valueOf(parada.getBairro().getId())});

        Double valor = null;

        int qtd = cursor.getColumnCount();

        if(cursor.moveToFirst()){
            do{
                valor = cursor.getDouble(0);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return valor;
    }

}
