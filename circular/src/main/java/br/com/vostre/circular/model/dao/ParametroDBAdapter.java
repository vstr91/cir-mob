package br.com.vostre.circular.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;

/**
 * Created by Almir on 11/06/2015.
 */
public class ParametroDBAdapter {

    private SQLiteDatabase database;
    private ParametroDBHelper parametroDBHelper;
    private Context context;

    public ParametroDBAdapter(Context context, SQLiteDatabase database){
        parametroDBHelper = new ParametroDBHelper(context);
        this.database = database;
        this.context = context;
    }

    public String carregarUltimoAcesso(){
        Cursor cursor = database.rawQuery("SELECT ultimo_acesso FROM "+parametroDBHelper.TABELA, null);

        String ultimoAcesso = null;

        if(cursor.moveToFirst()){
            do{
                ultimoAcesso = cursor.getString(0);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return ultimoAcesso;
    }

    public void gravarUltimoAcesso(String acesso){

        Cursor cursor = database.rawQuery("UPDATE "+parametroDBHelper.TABELA+" SET "+parametroDBHelper.ULTIMO_ACESSO+" = ?", new String[]{acesso});
        cursor.moveToFirst();
        cursor.close();

        database.close();

    }

    public String carregarUltimoAcessoMensagem(){
        Cursor cursor = database.rawQuery("SELECT ultimo_acesso_mensagem FROM "+parametroDBHelper.TABELA, null);

        String ultimoAcesso = null;

        if(cursor.moveToFirst()){
            do{
                ultimoAcesso = cursor.getString(0);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return ultimoAcesso;
    }

    public void gravarUltimoAcessoMensagem(String acesso){

        Cursor cursor = database.rawQuery("UPDATE "+parametroDBHelper.TABELA+" SET "
                +parametroDBHelper.ULTIMO_ACESSO_MENSAGEM+" = ?", new String[]{acesso});
        cursor.moveToFirst();
        cursor.close();

        database.close();

    }

}
