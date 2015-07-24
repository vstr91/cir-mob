package br.com.vostre.circular.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Local;
import br.com.vostre.circular.model.Pais;

/**
 * Created by Almir on 14/04/2014.
 */
public class BairroDBAdapter {

    private SQLiteDatabase database;
    private BairroDBHelper bairroDBHelper;
    private String[] colunas = {bairroDBHelper.ID, bairroDBHelper.NOME, bairroDBHelper.STATUS, bairroDBHelper.LOCAL};
    private Context context;

    public BairroDBAdapter(Context context, SQLiteDatabase database){
        bairroDBHelper = new BairroDBHelper(context);
        this.database = database;
        this.context = context;
    }

    public long salvarOuAtualizar(Bairro bairro){
        Long retorno;
        ContentValues cv = new ContentValues();
        cv.put(bairroDBHelper.ID, bairro.getId());
        cv.put(bairroDBHelper.NOME, bairro.getNome());
        cv.put(bairroDBHelper.STATUS, bairro.getStatus());
        cv.put(bairroDBHelper.LOCAL, bairro.getLocal().getId());

        if(database.update(BairroDBHelper.TABELA, cv,  bairroDBHelper.ID+" = "+bairro.getId(), null) < 1){
            retorno = database.insert(BairroDBHelper.TABELA, null, cv);
            database.close();
            return retorno;
        } else{
            database.close();
            return -1;
        }

    }

    public int deletarInativos(){
        int retorno = database.delete(bairroDBHelper.TABELA, bairroDBHelper.STATUS+" = "+2, null);
        database.close();
        return retorno;
    }

    public List<Bairro> listarTodos(){
        Cursor cursor = database.rawQuery("SELECT _id, nome, status, id_local FROM "+bairroDBHelper.TABELA, null);
        LocalDBHelper localDBHelper = new LocalDBHelper(context);
        List<Bairro> bairros = new ArrayList<Bairro>();

        if(cursor.moveToFirst()){
            do{
                Bairro umBairro = new Bairro();
                umBairro.setId(cursor.getInt(0));
                umBairro.setNome(cursor.getString(1));
                umBairro.setStatus(cursor.getInt(2));
                Local umLocal = new Local();
                umLocal.setId(cursor.getInt(3));
                umLocal = localDBHelper.carregar(context, umLocal);

                umBairro.setLocal(umLocal);
               bairros.add(umBairro);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return bairros;
    }

    public List<Bairro> listarTodosPorLocal(Local local){
        Cursor cursor = database.rawQuery("SELECT _id, nome, status, id_local FROM "+bairroDBHelper.TABELA
                +" WHERE id_local = ? ORDER BY nome", new String[]{String.valueOf(local.getId())});
        LocalDBHelper localDBHelper = new LocalDBHelper(context);
        List<Bairro> bairros = new ArrayList<Bairro>();

        if(cursor.moveToFirst()){
            do{
                Bairro umBairro = new Bairro();
                umBairro.setId(cursor.getInt(0));
                umBairro.setNome(cursor.getString(1));
                umBairro.setStatus(cursor.getInt(2));
                Local umLocal = new Local();
                umLocal.setId(cursor.getInt(3));
                umLocal = localDBHelper.carregar(context, umLocal);

                umBairro.setLocal(umLocal);
                bairros.add(umBairro);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return bairros;
    }

    public List<Bairro> listarTodosVinculadosPorLocal(Local local){
        Cursor cursor = database.rawQuery("SELECT DISTINCT b._id, b.nome, b.status, b.id_local " +
                "FROM bairro b INNER JOIN" +
                "     parada p ON p.id_bairro = b._id INNER JOIN" +
                "     parada_itinerario pit ON pit.id_parada = p._id" +
                " WHERE id_local = ?" +
                " ORDER BY nome", new String[]{String.valueOf(local.getId())});
        LocalDBHelper localDBHelper = new LocalDBHelper(context);
        List<Bairro> bairros = new ArrayList<Bairro>();

        if(cursor.moveToFirst()){
            do{
                Bairro umBairro = new Bairro();
                umBairro.setId(cursor.getInt(0));
                umBairro.setNome(cursor.getString(1));
                umBairro.setStatus(cursor.getInt(2));
                Local umLocal = new Local();
                umLocal.setId(cursor.getInt(3));
                umLocal = localDBHelper.carregar(context, umLocal);

                umBairro.setLocal(umLocal);
                bairros.add(umBairro);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return bairros;
    }

    public List<Bairro> listarPartidaPorItinerario(Local local){
        Cursor cursor = database.rawQuery("SELECT DISTINCT bp._id, bp.nome, bp.status, bp.id_local FROM "+ItinerarioDBHelper.TABELA
                +" i LEFT JOIN "+bairroDBHelper.TABELA+" bp ON bp._id = i.id_partida WHERE bp.id_local = ? ORDER BY bp.nome",
                new String[]{String.valueOf(local.getId())});

        /*Cursor cursor = database.rawQuery("SELECT DISTINCT bp._id, bp.nome, bp.status, bp.id_local " +
                        "FROM itinerario i LEFT JOIN " +
                        "parada_itinerario pit ON pit.id_itinerario = i._id LEFT JOIN" +
                        "     parada p ON p._id = pit.id_parada LEFT JOIN " +
                        "bairro bp ON bp._id = i.id_partida OR (bp._id = p.id_bairro AND pit.destaque = -1) WHERE bp.id_local = ?",
                new String[]{String.valueOf(local.getId())});*/

        LocalDBHelper localDBHelper = new LocalDBHelper(context);
        List<Bairro> bairros = new ArrayList<Bairro>();

        if(cursor.moveToFirst()){
            do{
                Bairro umBairro = new Bairro();
                umBairro.setId(cursor.getInt(0));
                umBairro.setNome(cursor.getString(1));
                umBairro.setStatus(cursor.getInt(2));
                Local umLocal = new Local();
                umLocal.setId(cursor.getInt(3));
                umLocal = localDBHelper.carregar(context, umLocal);

                umBairro.setLocal(umLocal);
                bairros.add(umBairro);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return bairros;
    }

    public List<Bairro> listarDestinoPorPartida(Bairro bairro){
        /*Cursor cursor = database.rawQuery("SELECT DISTINCT bd._id, bd.nome, bd.status, bd.id_local FROM "+ItinerarioDBHelper.TABELA
                        +" i LEFT JOIN "+bairroDBHelper.TABELA+" bd ON bd._id = i.id_destino INNER JOIN "
                        +HorarioItinerarioDBHelper.TABELA+" hi ON hi.id_itinerario = i._id WHERE id_partida = ? ORDER BY bd.nome",
                new String[]{String.valueOf(bairro.getId())});*/

        Cursor cursor = database.rawQuery("SELECT DISTINCT bd._id, bd.nome, bd.status, bd.id_local " +
                        "FROM itinerario i LEFT JOIN " +
                        "     parada_itinerario pit ON pit.id_itinerario = i._id LEFT JOIN" +
                        "     parada p ON p._id = pit.id_parada LEFT JOIN" +
                        "     bairro bd ON (bd._id = i.id_destino) OR (bd._id = p.id_bairro AND pit.destaque = -1) INNER JOIN" +
                        "     horario_itinerario hi ON hi.id_itinerario = i._id INNER JOIN" +
                        "     local l ON l._id = bd.id_local" +
                        " WHERE id_partida = ? " +
                        "ORDER BY bd.nome, l.nome",
                new String[]{String.valueOf(bairro.getId())});

        LocalDBHelper localDBHelper = new LocalDBHelper(context);
        List<Bairro> bairros = new ArrayList<Bairro>();

        if(cursor.moveToFirst()){
            do{
                Bairro umBairro = new Bairro();
                umBairro.setId(cursor.getInt(0));
                umBairro.setNome(cursor.getString(1));
                umBairro.setStatus(cursor.getInt(2));
                Local umLocal = new Local();
                umLocal.setId(cursor.getInt(3));
                umLocal = localDBHelper.carregar(context, umLocal);

                umBairro.setLocal(umLocal);
                bairros.add(umBairro);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return bairros;
    }

    public Bairro carregar(Bairro bairro){
        Cursor cursor = database.rawQuery("SELECT _id, nome, status, id_local FROM "+bairroDBHelper.TABELA
                +" WHERE _id = ?", new String[]{String.valueOf(bairro.getId())});
        LocalDBHelper localDBHelper = new LocalDBHelper(context);

        Bairro umBairro = null;

        if(cursor.moveToFirst()){
            do{
                umBairro = new Bairro();
                umBairro.setId(cursor.getInt(0));
                umBairro.setNome(cursor.getString(1));
                umBairro.setStatus(cursor.getInt(3));

                Local umLocal = new Local();
                umLocal.setId(cursor.getInt(3));
                umLocal = localDBHelper.carregar(context, umLocal);

                umBairro.setLocal(umLocal);
                //paises.add(umPais);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return umBairro;
    }

}
