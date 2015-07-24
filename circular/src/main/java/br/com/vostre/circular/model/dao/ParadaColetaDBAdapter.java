package br.com.vostre.circular.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaColeta;
import br.com.vostre.circular.utils.DateUtils;

/**
 * Created by Almir on 10/04/2015.
 */
public class ParadaColetaDBAdapter {

    private SQLiteDatabase database;
    private ParadaColetaDBHelper paradaColetaDBHelper;
    private String[] colunas = {paradaColetaDBHelper.ID, paradaColetaDBHelper.BAIRRO, paradaColetaDBHelper.REFERENCIA,
            paradaColetaDBHelper.LATITUDE,
            paradaColetaDBHelper.LONGITUDE, paradaColetaDBHelper.STATUS, paradaColetaDBHelper.DATA_COLETA, paradaColetaDBHelper.ENVIADO};
    private Context context;

    public ParadaColetaDBAdapter(Context context, SQLiteDatabase database){
        paradaColetaDBHelper = new ParadaColetaDBHelper(context);
        this.database = database;
        this.context = context;
    }

    public long salvarOuAtualizar(ParadaColeta paradaColeta){
        long retorno;
        ContentValues cv = new ContentValues();

        if(paradaColeta.getId() > 0){
            cv.put(paradaColetaDBHelper.ID, paradaColeta.getId());
        }

        cv.put(paradaColetaDBHelper.BAIRRO, paradaColeta.getBairro().getId());
        cv.put(paradaColetaDBHelper.REFERENCIA, paradaColeta.getReferencia());
        cv.put(paradaColetaDBHelper.LATITUDE, paradaColeta.getLatitude());
        cv.put(paradaColetaDBHelper.LONGITUDE, paradaColeta.getLongitude());
        cv.put(paradaColetaDBHelper.STATUS, paradaColeta.getStatus());
        cv.put(paradaColetaDBHelper.DATA_COLETA, DateUtils.converteDataParaPadraoBanco(paradaColeta.getDataColeta().getTime()));
        cv.put(paradaColetaDBHelper.ENVIADO, paradaColeta.getEnviado());

        if(paradaColeta.getId() > 0){
            database.update(ParadaColetaDBHelper.TABELA, cv,  paradaColetaDBHelper.ID+" = "+paradaColeta.getId(), null);
            database.close();
            return -1;
        } else{
            retorno = database.insert(ParadaColetaDBHelper.TABELA, null, cv);
            database.close();
            return retorno;
        }

    }

    public List<ParadaColeta> listarTodos() throws ParseException {
        Cursor cursor = database.rawQuery("SELECT _id, referencia, latitude, longitude, status, id_bairro, data_coleta, enviado FROM "+paradaColetaDBHelper.TABELA, null);
        BairroDBHelper bairroDBHelper = new BairroDBHelper(context);
        List<ParadaColeta> paradas = new ArrayList<ParadaColeta>();

        if(cursor.moveToFirst()){
            do{
                ParadaColeta umaParada = new ParadaColeta();
                umaParada.setId(cursor.getInt(0));
                umaParada.setReferencia(cursor.getString(1));
                umaParada.setLatitude(cursor.getString(2));
                umaParada.setLongitude(cursor.getString(3));
                umaParada.setStatus(cursor.getInt(4));
                Bairro umBairro = new Bairro();
                umBairro.setId(cursor.getInt(5));
                umBairro = bairroDBHelper.carregar(context, umBairro);

                umaParada.setBairro(umBairro);

                Date date = DateUtils.convertePadraoBancoParaDate(cursor.getString(6));
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                umaParada.setDataColeta(cal);
                umaParada.setEnviado(cursor.getInt(7));
                paradas.add(umaParada);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return paradas;
    }

    public ParadaColeta carregar(ParadaColeta parada) throws ParseException {
        Cursor cursor = database.rawQuery("SELECT _id, referencia, latitude, longitude, status, id_bairro, data_coleta, enviado FROM "+
                paradaColetaDBHelper.TABELA
                +" WHERE _id = ?", new String[]{String.valueOf(parada.getId())});
        BairroDBHelper bairroDBHelper = new BairroDBHelper(context);

        ParadaColeta umaParada = null;

        if(cursor.moveToFirst()){
            do{
                umaParada.setId(cursor.getInt(0));
                umaParada.setReferencia(cursor.getString(1));
                umaParada.setLatitude(cursor.getString(2));
                umaParada.setLongitude(cursor.getString(3));
                umaParada.setStatus(cursor.getInt(4));
                Bairro umBairro = new Bairro();
                umBairro.setId(cursor.getInt(5));
                umBairro = bairroDBHelper.carregar(context, umBairro);

                umaParada.setBairro(umBairro);

                Date date = DateUtils.convertePadraoBancoParaDate(cursor.getString(6));
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                umaParada.setDataColeta(cal);
                umaParada.setEnviado(cursor.getInt(7));
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return umaParada;
    }

    public List<ParadaColeta> listarTodosPorBairro(Bairro bairro) throws ParseException {
        Cursor cursor = database.rawQuery("SELECT _id, referencia, latitude, longitude, status, id_bairro, data_coleta, enviado FROM "
                +paradaColetaDBHelper.TABELA+" WHERE id_bairro = ?", new String[]{String.valueOf(bairro.getId())});
        BairroDBHelper bairroDBHelper = new BairroDBHelper(context);
        List<ParadaColeta> paradas = new ArrayList<ParadaColeta>();

        if(cursor.moveToFirst()){
            do{
                ParadaColeta umaParada = new ParadaColeta();
                umaParada.setId(cursor.getInt(0));
                umaParada.setReferencia(cursor.getString(1));
                umaParada.setLatitude(cursor.getString(2));
                umaParada.setLongitude(cursor.getString(3));
                umaParada.setStatus(cursor.getInt(4));
                Bairro umBairro = new Bairro();
                umBairro.setId(cursor.getInt(5));
                umBairro = bairroDBHelper.carregar(context, umBairro);

                umaParada.setBairro(umBairro);

                Date date = DateUtils.convertePadraoBancoParaDate(cursor.getString(6));
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                umaParada.setDataColeta(cal);
                umaParada.setEnviado(cursor.getInt(7));

                paradas.add(umaParada);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return paradas;
    }

}
