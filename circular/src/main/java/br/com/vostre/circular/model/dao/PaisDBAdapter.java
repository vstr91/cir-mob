package br.com.vostre.circular.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.Pais;

/**
 * Created by Almir on 14/04/2014.
 */
public class PaisDBAdapter {

    private SQLiteDatabase database;
    private PaisDBHelper paisDBHelper;
    private String[] colunas = {paisDBHelper.ID, paisDBHelper.NOME, paisDBHelper.ISO3, paisDBHelper.STATUS};

    public PaisDBAdapter(Context context, SQLiteDatabase database){
        paisDBHelper = new PaisDBHelper(context);
        this.database = database;
    }

    public long cadastrarPais(Pais pais){
        ContentValues cv = new ContentValues();
        cv.put(paisDBHelper.ID, pais.getId());
        cv.put(paisDBHelper.NOME, pais.getNome());
        cv.put(paisDBHelper.ISO3, pais.getIso3());
        cv.put(paisDBHelper.STATUS, pais.getStatus());
        long insertId = database.insert(PaisDBHelper.TABELA, null, cv);

        database.close();

        return insertId;
    }

    public long salvarOuAtualizar(Pais pais){
        long retorno;
        ContentValues cv = new ContentValues();
        cv.put(paisDBHelper.ID, pais.getId());
        cv.put(paisDBHelper.NOME, pais.getNome());
        cv.put(paisDBHelper.ISO3, pais.getIso3());
        cv.put(paisDBHelper.STATUS, pais.getStatus());

        if(database.update(PaisDBHelper.TABELA, cv,  paisDBHelper.ID+" = "+pais.getId(), null) < 1){
            retorno = database.insert(PaisDBHelper.TABELA, null, cv);
            database.close();
            return retorno;
        } else{
            database.close();
            return -1;
        }

    }

    public int deletarPais(Pais pais){
        int retorno = database.delete(paisDBHelper.TABELA, paisDBHelper.ID+" = "+pais.getId(), null);
        database.close();
        return retorno;
    }

    public int deletarInativos(){
        int retorno = database.delete(paisDBHelper.TABELA, paisDBHelper.STATUS+" = "+2, null);
        database.close();
        return retorno;
    }

    public List<Pais> listarTodos(){
        Cursor cursor = database.rawQuery("SELECT _id, nome, iso3, status FROM "+paisDBHelper.TABELA, null);

        List<Pais> paises = new ArrayList<Pais>();

        if(cursor.moveToFirst()){
            do{
                Pais umPais = new Pais();
                umPais.setId(cursor.getInt(0));
                umPais.setNome(cursor.getString(1));
                umPais.setIso3(cursor.getString(2));
                umPais.setStatus(cursor.getInt(3));
               paises.add(umPais);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return paises;
    }

    public Pais carregar(Pais pais){
        Cursor cursor = database.rawQuery("SELECT _id, nome, iso3, status FROM "+paisDBHelper.TABELA
                +" WHERE _id = ?", new String[]{String.valueOf(pais.getId())});

        List<Pais> paises = new ArrayList<Pais>();
        Pais umPais = null;

        if(cursor.moveToFirst()){
            do{
                umPais = new Pais();
                umPais.setId(cursor.getInt(0));
                umPais.setNome(cursor.getString(1));
                umPais.setIso3(cursor.getString(2));
                umPais.setStatus(cursor.getInt(3));
                //paises.add(umPais);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return umPais;
    }

}
