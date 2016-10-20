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
import java.util.Date;
import java.util.List;

import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.Mensagem;

/**
 * Created by Almir on 27/08/2015.
 */
public class MensagemDBAdapter {

    private SQLiteDatabase database;
    private MensagemDBHelper mensagemDBHelper;
    private Context context;

    public MensagemDBAdapter(Context context, SQLiteDatabase database){
        mensagemDBHelper = new MensagemDBHelper(context);
        this.database = database;
        this.context = context;
    }

    public long salvarOuAtualizar(Mensagem mensagem){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long retorno = null;
        ContentValues cv = new ContentValues();

        if(mensagem.getId() > 0){
            cv.put(mensagemDBHelper.ID, mensagem.getId());
        }

        cv.put(mensagemDBHelper.TITULO, mensagem.getTitulo());
        cv.put(mensagemDBHelper.DESCRICAO, mensagem.getDescricao());
        cv.put(mensagemDBHelper.DATA_ENVIO, df.format(mensagem.getDataEnvio().getTime()));
        cv.put(mensagemDBHelper.STATUS, mensagem.getStatus());

        if(database.update(MensagemDBHelper.TABELA, cv,  mensagemDBHelper.ID+" = "+mensagem.getId(), null) < 1){
            retorno = database.insert(MensagemDBHelper.TABELA, null, cv);
            database.close();
            return retorno;
        } else{
            database.close();
            return -1;
        }

    }

    public int marcarLida(Mensagem mensagem){

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ContentValues cv = new ContentValues();
        cv.put(mensagemDBHelper.ID, mensagem.getId());
        cv.put(mensagemDBHelper.TITULO, mensagem.getTitulo());
        cv.put(mensagemDBHelper.DESCRICAO, mensagem.getDescricao());
        cv.put(mensagemDBHelper.DATA_ENVIO, df.format(mensagem.getDataEnvio().getTime()));
        cv.put(mensagemDBHelper.DATA_LEITURA, df.format(new Date()));
        cv.put(mensagemDBHelper.STATUS, 5);

        int retorno = database.update(mensagemDBHelper.TABELA, cv, mensagemDBHelper.ID + " = " + mensagem.getId(), null);
        database.close();
        return retorno;
    }

    public int deletar(Mensagem mensagem){
        int retorno = database.delete(mensagemDBHelper.TABELA, mensagemDBHelper.ID+" = "+mensagem.getId(), null);
        database.close();
        return retorno;
    }

    public int deletarInativos(){
        int retorno = database.delete(mensagemDBHelper.TABELA, mensagemDBHelper.STATUS+" = "+2, null);
        database.close();
        return retorno;
    }

    public int deletarCadastrados(){
        int retorno = database.delete(mensagemDBHelper.TABELA, mensagemDBHelper.STATUS+" IN (3,4)", null);
        database.close();
        return retorno;
    }

    public List<Mensagem> listarTodos(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Cursor cursor = database.rawQuery("SELECT _id, titulo, descricao, data_envio, data_leitura, status FROM "+mensagemDBHelper.TABELA, null);
        List<Mensagem> mensagens = new ArrayList<Mensagem>();

        if(cursor.moveToFirst()){
            do{
                Mensagem umaMensagem = new Mensagem();
                umaMensagem.setId(cursor.getInt(0));
                umaMensagem.setTitulo(cursor.getString(1));
                umaMensagem.setDescricao(cursor.getString(2));
                try {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(df.parse(cursor.getString(3)));
                    umaMensagem.setDataEnvio(cal);

                    if(cursor.getString(4) != null){
                        Calendar calLeitura = Calendar.getInstance();
                        calLeitura.setTime(df.parse(cursor.getString(4)));
                        umaMensagem.setDataLeitura(calLeitura);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                umaMensagem.setStatus(cursor.getInt(5));
                mensagens.add(umaMensagem);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return mensagens;
    }

    public List<Mensagem> listarTodosRecebidas(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Cursor cursor = database.rawQuery("SELECT _id, titulo, descricao, data_envio, data_leitura, status FROM "
                +mensagemDBHelper.TABELA+" WHERE status IN (0, 5)", null);
        List<Mensagem> mensagens = new ArrayList<Mensagem>();

        if(cursor.moveToFirst()){
            do{
                Mensagem umaMensagem = new Mensagem();
                umaMensagem.setId(cursor.getInt(0));
                umaMensagem.setTitulo(cursor.getString(1));
                umaMensagem.setDescricao(cursor.getString(2));
                try {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(df.parse(cursor.getString(3)));
                    umaMensagem.setDataEnvio(cal);

                    Calendar calLeitura = Calendar.getInstance();

                    if(cursor.getString(4) != null){
                        calLeitura.setTime(df.parse(cursor.getString(4)));
                        umaMensagem.setDataLeitura(calLeitura);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                umaMensagem.setStatus(cursor.getInt(5));
                mensagens.add(umaMensagem);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return mensagens;
    }

    public List<Mensagem> listarTodosNaoLidas(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Cursor cursor = database.rawQuery("SELECT _id, titulo, descricao, data_envio, data_leitura, status FROM "
                +mensagemDBHelper.TABELA+" WHERE status = 0", null);
        List<Mensagem> mensagens = new ArrayList<Mensagem>();

        if(cursor.moveToFirst()){
            do{
                Mensagem umaMensagem = new Mensagem();
                umaMensagem.setId(cursor.getInt(0));
                umaMensagem.setTitulo(cursor.getString(1));
                umaMensagem.setDescricao(cursor.getString(2));
                try {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(df.parse(cursor.getString(3)));
                    umaMensagem.setDataEnvio(cal);

                    Calendar calLeitura = Calendar.getInstance();

                    if(cursor.getString(4) != null){
                        calLeitura.setTime(df.parse(cursor.getString(4)));
                        umaMensagem.setDataLeitura(calLeitura);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                umaMensagem.setStatus(cursor.getInt(5));
                mensagens.add(umaMensagem);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return mensagens;
    }

    public List<Mensagem> listarTodosAEnviar(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Cursor cursor = database.rawQuery("SELECT _id, titulo, descricao, data_envio, data_leitura, status FROM "
                +mensagemDBHelper.TABELA+" WHERE status = 3", null);
        List<Mensagem> mensagens = new ArrayList<Mensagem>();

        if(cursor.moveToFirst()){
            do{
                Mensagem umaMensagem = new Mensagem();
                umaMensagem.setId(cursor.getInt(0));
                umaMensagem.setTitulo(cursor.getString(1));
                umaMensagem.setDescricao(cursor.getString(2));
                try {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(df.parse(cursor.getString(3)));
                    umaMensagem.setDataEnvio(cal);

                    Calendar calLeitura = Calendar.getInstance();

                    if(cursor.getString(4) != null){
                        calLeitura.setTime(df.parse(cursor.getString(4)));
                        umaMensagem.setDataLeitura(calLeitura);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                umaMensagem.setStatus(cursor.getInt(5));
                mensagens.add(umaMensagem);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return mensagens;
    }

    public List<Mensagem> listarTodosCadastrados(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Cursor cursor = database.rawQuery("SELECT _id, titulo, descricao, data_envio, data_leitura, status FROM "
                +mensagemDBHelper.TABELA+" WHERE status IN (3,4)", null);
        List<Mensagem> mensagens = new ArrayList<Mensagem>();

        if(cursor.moveToFirst()){
            do{
                Mensagem umaMensagem = new Mensagem();
                umaMensagem.setId(cursor.getInt(0));
                umaMensagem.setTitulo(cursor.getString(1));
                umaMensagem.setDescricao(cursor.getString(2));
                try {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(df.parse(cursor.getString(3)));
                    umaMensagem.setDataEnvio(cal);

                    Calendar calLeitura = Calendar.getInstance();

                    if(cursor.getString(4) != null){
                        calLeitura.setTime(df.parse(cursor.getString(4)));
                        umaMensagem.setDataLeitura(calLeitura);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                umaMensagem.setStatus(cursor.getInt(5));
                mensagens.add(umaMensagem);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return mensagens;
    }

    public Mensagem carregar(Mensagem mensagem){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Cursor cursor = database.rawQuery("SELECT _id, titulo, descricao, data_envio, data_leitura, status FROM "
                +mensagemDBHelper.TABELA+" WHERE _id = ?", new String[]{String.valueOf(mensagem.getId())});

        Mensagem umaMensagem = null;

        if(cursor.moveToFirst()){
            do{
                umaMensagem = new Mensagem();
                umaMensagem.setId(cursor.getInt(0));
                umaMensagem.setTitulo(cursor.getString(1));
                umaMensagem.setDescricao(cursor.getString(2));
                try {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(df.parse(cursor.getString(3)));
                    umaMensagem.setDataEnvio(cal);

                    if(cursor.getString(4) != null){
                        Calendar calLeitura = Calendar.getInstance();
                        calLeitura.setTime(df.parse(cursor.getString(4)));
                        umaMensagem.setDataLeitura(calLeitura);
                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }
                umaMensagem.setStatus(cursor.getInt(5));
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return umaMensagem;
    }

}
