package br.com.vostre.circular.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Local;
import br.com.vostre.circular.model.Parada;

/**
 * Created by Almir on 14/04/2014.
 */
public class ParadaDBAdapter {

    private SQLiteDatabase database;
    private ParadaDBHelper paradaDBHelper;
    private String[] colunas = {paradaDBHelper.ID, paradaDBHelper.BAIRRO, paradaDBHelper.REFERENCIA, paradaDBHelper.LATITUDE,
            paradaDBHelper.LONGITUDE, paradaDBHelper.STATUS};
    private Context context;

    public ParadaDBAdapter(Context context, SQLiteDatabase database){
        paradaDBHelper = new ParadaDBHelper(context);
        this.database = database;
        this.context = context;
    }

    public long salvarOuAtualizar(Parada parada){
        long retorno;
        ContentValues cv = new ContentValues();
        cv.put(paradaDBHelper.ID, parada.getId());
        cv.put(paradaDBHelper.BAIRRO, parada.getBairro().getId());
        cv.put(paradaDBHelper.REFERENCIA, parada.getReferencia());
        cv.put(paradaDBHelper.LATITUDE, parada.getLatitude());
        cv.put(paradaDBHelper.LONGITUDE, parada.getLongitude());
        cv.put(paradaDBHelper.STATUS, parada.getStatus());
        cv.put("taxa_de_embarque", parada.getTaxaDeEmbarque());
        cv.put("slug", parada.getSlug());

        if(database.update(ParadaDBHelper.TABELA, cv,  paradaDBHelper.ID+" = "+parada.getId(), null) < 1){
            retorno = database.insert(ParadaDBHelper.TABELA, null, cv);
            database.close();
            return retorno;
        } else{
            database.close();
            return -1;
        }

    }

    public int deletarInativos(){
        int retorno = database.delete(paradaDBHelper.TABELA, paradaDBHelper.STATUS+" = "+2, null);
        database.close();
        return retorno;
    }

    public List<Parada> listarTodos(){
        Cursor cursor = database.rawQuery("SELECT _id, referencia, latitude, longitude, status, id_bairro, taxa_de_embarque FROM "+paradaDBHelper.TABELA, null);
        BairroDBHelper bairroDBHelper = new BairroDBHelper(context);
        List<Parada> paradas = new ArrayList<Parada>();

        if(cursor.moveToFirst()){
            do{
                Parada umaParada = new Parada();
                umaParada.setId(cursor.getInt(0));
                umaParada.setReferencia(cursor.getString(1));
                umaParada.setLatitude(cursor.getString(2));
                umaParada.setLongitude(cursor.getString(3));
                umaParada.setStatus(cursor.getInt(4));
                Bairro umBairro = new Bairro();
                umBairro.setId(cursor.getInt(5));
                umBairro = bairroDBHelper.carregar(context, umBairro);

                umaParada.setBairro(umBairro);
                umaParada.setTaxaDeEmbarque(cursor.getDouble(6));
               paradas.add(umaParada);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return paradas;
    }

    public List<Parada> listarTodosComItinerario(){
        Cursor cursor = database.rawQuery("SELECT DISTINCT p._id, p.referencia, p.latitude, p.longitude, p.status, p.id_bairro, p.taxa_de_embarque FROM "
                +paradaDBHelper.TABELA+" p INNER JOIN "+ParadaItinerarioDBHelper.TABELA+" pi ON pi.id_parada = p._id INNER JOIN "
                +HorarioItinerarioDBHelper.TABELA+" hi ON hi.id_itinerario = pi.id_itinerario", null);
        BairroDBHelper bairroDBHelper = new BairroDBHelper(context);
        List<Parada> paradas = new ArrayList<Parada>();

        if(cursor.moveToFirst()){
            do{
                Parada umaParada = new Parada();
                umaParada.setId(cursor.getInt(0));
                umaParada.setReferencia(cursor.getString(1));
                umaParada.setLatitude(cursor.getString(2));
                umaParada.setLongitude(cursor.getString(3));
                umaParada.setStatus(cursor.getInt(4));
                Bairro umBairro = new Bairro();
                umBairro.setId(cursor.getInt(5));
                umBairro = bairroDBHelper.carregar(context, umBairro);

                umaParada.setBairro(umBairro);
                umaParada.setTaxaDeEmbarque(cursor.getDouble(6));
                paradas.add(umaParada);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return paradas;
    }

    // FUNCAO COS NAO EXISTE SQLITE
//    public List<Parada> listarTodosProximosComItinerario(Double distancia, Location location){
//        Cursor cursor = database.rawQuery("SELECT DISTINCT p._id, p.referencia, p.latitude, p.longitude, p.status, p.id_bairro, " +
//                "( 6371 * acos( cos( radians("+location.getLatitude()+") ) * cos( radians( latitude ) ) * cos( radians( longitude ) - radians("+location.getLongitude()+") ) + " +
//                "sin( radians("+location.getLatitude()+") ) * sin( radians( latitude ) ) ) ) AS distance  FROM "
//                +paradaDBHelper.TABELA+" p INNER JOIN "+ParadaItinerarioDBHelper.TABELA+" pi ON pi.id_parada = p._id INNER JOIN "
//                +HorarioItinerarioDBHelper.TABELA+" hi ON hi.id_itinerario = pi.id_itinerario HAVING distance < "+distancia+" ORDER BY distance", null);
//        BairroDBHelper bairroDBHelper = new BairroDBHelper(context);
//        List<Parada> paradas = new ArrayList<Parada>();
//
//        if(cursor.moveToFirst()){
//            do{
//                Parada umaParada = new Parada();
//                umaParada.setId(cursor.getInt(0));
//                umaParada.setReferencia(cursor.getString(1));
//                umaParada.setLatitude(cursor.getString(2));
//                umaParada.setLongitude(cursor.getString(3));
//                umaParada.setStatus(cursor.getInt(4));
//                Bairro umBairro = new Bairro();
//                umBairro.setId(cursor.getInt(5));
//                umBairro = bairroDBHelper.carregar(context, umBairro);
//
//                umaParada.setBairro(umBairro);
//                paradas.add(umaParada);
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        database.close();
//
//        return paradas;
//    }

    public Parada carregar(Parada parada){
        Cursor cursor = database.rawQuery("SELECT _id, referencia, latitude, longitude, status, id_bairro, taxa_de_embarque FROM "+paradaDBHelper.TABELA
                +" WHERE _id = ?", new String[]{String.valueOf(parada.getId())});
        BairroDBHelper bairroDBHelper = new BairroDBHelper(context);

        Parada umaParada = null;

        if(cursor.moveToFirst()){
            do{
                umaParada = new Parada();
                umaParada.setId(cursor.getInt(0));
                umaParada.setReferencia(cursor.getString(1));
                umaParada.setLatitude(cursor.getString(2));
                umaParada.setLongitude(cursor.getString(3));
                umaParada.setStatus(cursor.getInt(4));
                Bairro umBairro = new Bairro();
                umBairro.setId(cursor.getInt(5));
                umBairro = bairroDBHelper.carregar(context, umBairro);

                umaParada.setBairro(umBairro);
                umaParada.setTaxaDeEmbarque(cursor.getDouble(6));
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return umaParada;
    }

    public Parada carregar(String uf, String local, String bairro, String slug){
        Cursor cursor = database.rawQuery("SELECT p._id, referencia, latitude, longitude, p.status, id_bairro, taxa_de_embarque " +
                "FROM parada p " +
                "WHERE p.slug = ?", new String[]{slug});
//        Cursor cursor = database.rawQuery("SELECT p._id, referencia, latitude, longitude, p.status, id_bairro, taxa_de_embarque " +
//                "FROM parada p INNER JOIN " +
//                "bairro b ON b._id = p.id_bairro INNER JOIN " +
//                "local l ON l._id = b.id_local INNER JOIN " +
//                "estado e ON e._id = l.id_estado " +
//                "WHERE e.sigla = ? AND l.slug = ? AND b.slug = ? AND p.slug = ?", new String[]{uf, local, bairro, slug});
        BairroDBHelper bairroDBHelper = new BairroDBHelper(context);

        Parada umaParada = null;

        if(cursor.moveToFirst()){
            do{
                umaParada = new Parada();
                umaParada.setId(cursor.getInt(0));
                umaParada.setReferencia(cursor.getString(1));
                umaParada.setLatitude(cursor.getString(2));
                umaParada.setLongitude(cursor.getString(3));
                umaParada.setStatus(cursor.getInt(4));
                Bairro umBairro = new Bairro();
                umBairro.setId(cursor.getInt(5));
                umBairro = bairroDBHelper.carregar(context, umBairro);

                umaParada.setBairro(umBairro);
                umaParada.setTaxaDeEmbarque(cursor.getDouble(6));
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return umaParada;
    }

    public List<Parada> listarTodosPorBairro(Bairro bairro){
        Cursor cursor = database.rawQuery("SELECT _id, referencia, latitude, longitude, status, id_bairro, taxa_de_embarque FROM "
                +paradaDBHelper.TABELA+" WHERE id_bairro = ?", new String[]{String.valueOf(bairro.getId())});
        BairroDBHelper bairroDBHelper = new BairroDBHelper(context);
        List<Parada> paradas = new ArrayList<Parada>();

        if(cursor.moveToFirst()){
            do{
                Parada umaParada = new Parada();
                umaParada.setId(cursor.getInt(0));
                umaParada.setReferencia(cursor.getString(1));
                umaParada.setLatitude(cursor.getString(2));
                umaParada.setLongitude(cursor.getString(3));
                umaParada.setStatus(cursor.getInt(4));
                Bairro umBairro = new Bairro();
                umBairro.setId(cursor.getInt(5));
                umBairro = bairroDBHelper.carregar(context, umBairro);

                umaParada.setBairro(umBairro);
                umaParada.setTaxaDeEmbarque(cursor.getDouble(6));
                paradas.add(umaParada);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return paradas;
    }

    public List<Parada> listarTodosComItinerarioPorBairro(Bairro bairro){
        Cursor cursor = database.rawQuery("SELECT DISTINCT p._id, p.referencia, p.latitude, p.longitude, p.status, p.id_bairro, p.taxa_de_embarque FROM "
                +paradaDBHelper.TABELA+" p INNER JOIN "+ParadaItinerarioDBHelper.TABELA+" pi ON pi.id_parada = p._id INNER JOIN "
                +HorarioItinerarioDBHelper.TABELA+" hi ON hi.id_itinerario = pi.id_itinerario WHERE id_bairro = ? ORDER BY p.referencia", new String[]{String.valueOf(bairro.getId())});
        BairroDBHelper bairroDBHelper = new BairroDBHelper(context);
        List<Parada> paradas = new ArrayList<Parada>();

        if(cursor.moveToFirst()){
            do{
                Parada umaParada = new Parada();
                umaParada.setId(cursor.getInt(0));
                umaParada.setReferencia(cursor.getString(1));
                umaParada.setLatitude(cursor.getString(2));
                umaParada.setLongitude(cursor.getString(3));
                umaParada.setStatus(cursor.getInt(4));
                Bairro umBairro = new Bairro();
                umBairro.setId(cursor.getInt(5));
                umBairro = bairroDBHelper.carregar(context, umBairro);

                umaParada.setBairro(umBairro);
                umaParada.setTaxaDeEmbarque(cursor.getDouble(6));
                paradas.add(umaParada);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return paradas;
    }

}
