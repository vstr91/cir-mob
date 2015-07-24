package br.com.vostre.circular.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Pais;

/**
 * Created by Almir on 14/04/2014.
 */
public class EstadoDBAdapter {

    private SQLiteDatabase database;
    private EstadoDBHelper estadoDBHelper;
    private String[] colunas = {estadoDBHelper.ID, estadoDBHelper.NOME, estadoDBHelper.SIGLA, estadoDBHelper.STATUS, estadoDBHelper.PAIS};
    private Context context;

    public EstadoDBAdapter(Context context, SQLiteDatabase database){
        estadoDBHelper = new EstadoDBHelper(context);
        this.database = database;
        this.context = context;
    }

    public long cadastrarEstado(Estado estado){
        ContentValues cv = new ContentValues();
        cv.put(estadoDBHelper.ID, estado.getId());
        cv.put(estadoDBHelper.NOME, estado.getNome());
        cv.put(estadoDBHelper.SIGLA, estado.getSigla());
        cv.put(estadoDBHelper.STATUS, estado.getStatus());
        cv.put(estadoDBHelper.PAIS, estado.getPais().getId());
        long insertId = database.insert(EstadoDBHelper.TABELA, null, cv);

        database.close();

        return insertId;
    }

    public long salvarOuAtualizar(Estado estado){
        Long retorno;
        ContentValues cv = new ContentValues();
        cv.put(estadoDBHelper.ID, estado.getId());
        cv.put(estadoDBHelper.NOME, estado.getNome());
        cv.put(estadoDBHelper.SIGLA, estado.getSigla());
        cv.put(estadoDBHelper.STATUS, estado.getStatus());
        cv.put(estadoDBHelper.PAIS, estado.getPais().getId());

        if(database.update(EstadoDBHelper.TABELA, cv,  estadoDBHelper.ID+" = "+estado.getId(), null) < 1){
            retorno = database.insert(EstadoDBHelper.TABELA, null, cv);
            database.close();
            return retorno;
        } else{
            database.close();
            return -1;
        }

    }

    public int deletarEstado(Estado estado){
        int retorno = database.delete(estadoDBHelper.TABELA, estadoDBHelper.ID+" = "+estado.getId(), null);
        database.close();
        return retorno;
    }

    public int deletarInativos(){
        int retorno = database.delete(estadoDBHelper.TABELA, estadoDBHelper.STATUS+" = "+2, null);
        database.close();
        return retorno;
    }

    public List<Estado> listarTodos(){
        Cursor cursor = database.rawQuery("SELECT _id, nome, sigla, status, id_pais FROM "+estadoDBHelper.TABELA, null);
        PaisDBHelper paisDBHelper = new PaisDBHelper(context);
        List<Estado> estados = new ArrayList<Estado>();

        if(cursor.moveToFirst()){
            do{
                Estado umEstado = new Estado();
                umEstado.setId(cursor.getInt(0));
                umEstado.setNome(cursor.getString(1));
                umEstado.setSigla(cursor.getString(2));
                umEstado.setStatus(cursor.getInt(3));
                Pais umPais = new Pais();
                umPais.setId(cursor.getInt(4));
                umPais = paisDBHelper.carregar(context, umPais);

                umEstado.setPais(umPais);
               estados.add(umEstado);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return estados;
    }

    public List<Estado> listarTodosComVinculo(){
        Cursor cursor = database.rawQuery("SELECT DISTINCT e._id, e.nome, e.sigla, e.status, e.id_pais " +
                        "FROM estado e INNER JOIN " +
                        "local l ON l.id_estado = e._id INNER JOIN " +
                        "bairro bp ON bp.id_local = l._id INNER JOIN " +
                        "bairro bd ON bd.id_local = l._id INNER JOIN " +
                        "itinerario i ON i.id_partida = bp._id OR " +
                        "id_destino = bp._id OR " +
                        "i.id_partida = bd._id OR " +
                        "id_destino = bd._id", null);
        PaisDBHelper paisDBHelper = new PaisDBHelper(context);
        List<Estado> estados = new ArrayList<Estado>();

        if(cursor.moveToFirst()){
            do{
                Estado umEstado = new Estado();
                umEstado.setId(cursor.getInt(0));
                umEstado.setNome(cursor.getString(1));
                umEstado.setSigla(cursor.getString(2));
                umEstado.setStatus(cursor.getInt(3));
                Pais umPais = new Pais();
                umPais.setId(cursor.getInt(4));
                umPais = paisDBHelper.carregar(context, umPais);

                umEstado.setPais(umPais);
                estados.add(umEstado);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return estados;
    }

    public Estado carregar(Estado estado){
        Cursor cursor = database.rawQuery("SELECT _id, nome, sigla, status, id_pais FROM "+estadoDBHelper.TABELA
                +" WHERE _id = ?", new String[]{String.valueOf(estado.getId())});
        PaisDBHelper paisDBHelper = new PaisDBHelper(context);

        Estado umEstado = null;

        if(cursor.moveToFirst()){
            do{
                umEstado = new Estado();
                umEstado.setId(cursor.getInt(0));
                umEstado.setNome(cursor.getString(1));
                umEstado.setSigla(cursor.getString(2));
                umEstado.setStatus(cursor.getInt(3));

                Pais umPais = new Pais();
                umPais.setId(cursor.getInt(4));
                umPais = paisDBHelper.carregar(context, umPais);

                umEstado.setPais(umPais);
                //paises.add(umPais);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return umEstado;
    }

}
