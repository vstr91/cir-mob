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
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Local;
import br.com.vostre.circular.model.LocalColeta;
import br.com.vostre.circular.utils.DateUtils;

/**
 * Created by Almir on 10/04/2015.
 */
public class LocalColetaDBAdapter {

    private SQLiteDatabase database;
    private LocalColetaDBHelper localColetaDBHelper;
    private Context context;

    public LocalColetaDBAdapter(Context context, SQLiteDatabase database){
        localColetaDBHelper = new LocalColetaDBHelper(context);
        this.database = database;
        this.context = context;
    }

    public long salvarOuAtualizar(LocalColeta localColeta){
        long retorno;
        ContentValues cv = new ContentValues();

        if(localColeta.getId() > 0){
            cv.put("_id", localColeta.getId());
        }

        cv.put("id_estado", localColeta.getEstado().getId());
        cv.put("id_cidade", localColeta.getCidade().getId());
        cv.put("nome", localColeta.getNome());
        cv.put("status", localColeta.getStatus());
        cv.put("data_coleta", DateUtils.converteDataParaPadraoBanco(localColeta.getDataColeta().getTime()));

        if(localColeta.getId() > 0){
            database.update("local_coleta", cv,  "_id = "+localColeta.getId(), null);
            database.close();
            return -1;
        } else{
            retorno = database.insert("local_coleta", null, cv);
            database.close();
            return retorno;
        }

    }

    public List<LocalColeta> listarTodos() {
        Cursor cursor = database.rawQuery("SELECT _id, nome, id_estado, id_cidade, status, data_coleta FROM local_coleta", null);
        EstadoDBHelper estadoDBHelper = new EstadoDBHelper(context);
        LocalDBHelper localDBHelper = new LocalDBHelper(context);

        List<LocalColeta> locais = new ArrayList<LocalColeta>();

        if(cursor.moveToFirst()){
            do{
                LocalColeta umLocal = new LocalColeta();
                umLocal.setId(cursor.getInt(0));
                umLocal.setNome(cursor.getString(1));

                Estado estado = new Estado();
                estado.setId(cursor.getInt(2));
                estado = estadoDBHelper.carregar(context, estado);
                umLocal.setEstado(estado);

                if(umLocal.getId() != cursor.getInt(3)){
                    Local cidade = new Local();
                    cidade.setId(cursor.getInt(3));
                    cidade = localDBHelper.carregar(context, cidade);
                    umLocal.setCidade(cidade);
                }

                umLocal.setStatus(cursor.getInt(4));

                Calendar cal = Calendar.getInstance();

                try{
                    Date date = DateUtils.convertePadraoBancoParaDate(cursor.getString(5));
                    cal.setTime(date);
                } catch(ParseException e){
                    e.printStackTrace();
                }

                umLocal.setDataColeta(cal);
                locais.add(umLocal);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return locais;
    }

    public List<LocalColeta> listarTodosNaoEnviados() {
        Cursor cursor = database.rawQuery("SELECT _id, nome, id_estado, id_cidade, status, data_coleta FROM local_coleta WHERE status = 3", null);
        EstadoDBHelper estadoDBHelper = new EstadoDBHelper(context);
        LocalDBHelper localDBHelper = new LocalDBHelper(context);

        List<LocalColeta> locais = new ArrayList<LocalColeta>();

        if(cursor.moveToFirst()){
            do{
                LocalColeta umLocal = new LocalColeta();
                umLocal.setId(cursor.getInt(0));
                umLocal.setNome(cursor.getString(1));

                Estado estado = new Estado();
                estado.setId(cursor.getInt(2));
                estado = estadoDBHelper.carregar(context, estado);
                umLocal.setEstado(estado);

                if(umLocal.getId() != cursor.getInt(3)){
                    Local cidade = new Local();
                    cidade.setId(cursor.getInt(3));
                    cidade = localDBHelper.carregar(context, cidade);
                    umLocal.setCidade(cidade);
                }

                umLocal.setStatus(cursor.getInt(4));

                Calendar cal = Calendar.getInstance();

                try{
                    Date date = DateUtils.convertePadraoBancoParaDate(cursor.getString(5));
                    cal.setTime(date);
                } catch(ParseException e){
                    e.printStackTrace();
                }

                umLocal.setDataColeta(cal);
                locais.add(umLocal);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return locais;
    }

    public List<LocalColeta> listarTodosEnviados() {
        Cursor cursor = database.rawQuery("SELECT _id, nome, id_estado, id_cidade, status, data_coleta FROM local_coleta WHERE status = 4", null);
        EstadoDBHelper estadoDBHelper = new EstadoDBHelper(context);
        LocalDBHelper localDBHelper = new LocalDBHelper(context);

        List<LocalColeta> locais = new ArrayList<LocalColeta>();

        if(cursor.moveToFirst()){
            do{
                LocalColeta umLocal = new LocalColeta();
                umLocal.setId(cursor.getInt(0));
                umLocal.setNome(cursor.getString(1));

                Estado estado = new Estado();
                estado.setId(cursor.getInt(2));
                estado = estadoDBHelper.carregar(context, estado);
                umLocal.setEstado(estado);

                if(umLocal.getId() != cursor.getInt(3)){
                    Local cidade = new Local();
                    cidade.setId(cursor.getInt(3));
                    cidade = localDBHelper.carregar(context, cidade);
                    umLocal.setCidade(cidade);
                }

                umLocal.setStatus(cursor.getInt(4));

                Calendar cal = Calendar.getInstance();

                try{
                    Date date = DateUtils.convertePadraoBancoParaDate(cursor.getString(5));
                    cal.setTime(date);
                } catch(ParseException e){
                    e.printStackTrace();
                }

                umLocal.setDataColeta(cal);
                locais.add(umLocal);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return locais;
    }

    public LocalColeta carregar(LocalColeta local) {
        Cursor cursor = database.rawQuery("SELECT _id, nome, id_estado, id_cidade, status, data_coleta FROM local_coleta WHERE _id = ?", new String[]{String.valueOf(local.getId())});
        EstadoDBHelper estadoDBHelper = new EstadoDBHelper(context);
        LocalDBHelper localDBHelper = new LocalDBHelper(context);

        LocalColeta umLocal = null;

        if(cursor.moveToFirst()){
            do{
                umLocal = new LocalColeta();
                umLocal.setId(cursor.getInt(0));
                umLocal.setNome(cursor.getString(1));

                Estado estado = new Estado();
                estado.setId(cursor.getInt(2));
                estado = estadoDBHelper.carregar(context, estado);
                umLocal.setEstado(estado);

                if(umLocal.getId() != cursor.getInt(3)){
                    Local cidade = new Local();
                    cidade.setId(cursor.getInt(3));
                    cidade = localDBHelper.carregar(context, cidade);
                    umLocal.setCidade(cidade);
                }

                umLocal.setStatus(cursor.getInt(4));

                Calendar cal = Calendar.getInstance();

                try{
                    Date date = DateUtils.convertePadraoBancoParaDate(cursor.getString(5));
                    cal.setTime(date);
                } catch(ParseException e){
                    e.printStackTrace();
                }

                umLocal.setDataColeta(cal);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return umLocal;
    }

    public int excluir(LocalColeta localColeta){
        return database.delete("local_coleta", "_id = "+localColeta.getId(), null);
    }

    public int deletarCadastrados(){
        int retorno = database.delete("local_coleta", "status IN (3,4)", null);
        database.close();
        return retorno;
    }

}
