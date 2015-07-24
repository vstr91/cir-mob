package br.com.vostre.circular.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Pais;

/**
 * Created by Almir on 14/04/2014.
 */
public class EmpresaDBAdapter {

    private SQLiteDatabase database;
    private EmpresaDBHelper empresaDBHelper;
    private String[] colunas = {empresaDBHelper.ID, empresaDBHelper.RAZAO, empresaDBHelper.FANTASIA,
            empresaDBHelper.CNPJ, empresaDBHelper.SITE, empresaDBHelper.TELEFONE, empresaDBHelper.STATUS};
    private Context context;

    public EmpresaDBAdapter(Context context, SQLiteDatabase database){
        empresaDBHelper = new EmpresaDBHelper(context);
        this.database = database;
        this.context = context;
    }

    public long salvarOuAtualizar(Empresa empresa){
        Long retorno;
        ContentValues cv = new ContentValues();
        cv.put(empresaDBHelper.ID, empresa.getId());
        cv.put(empresaDBHelper.RAZAO, empresa.getRazaoSocial());
        cv.put(empresaDBHelper.FANTASIA, empresa.getFantasia());
        cv.put(empresaDBHelper.CNPJ, empresa.getCnpj());
        cv.put(empresaDBHelper.SITE, empresa.getSite());
        cv.put(empresaDBHelper.TELEFONE, empresa.getTelefone());
        cv.put(empresaDBHelper.STATUS, empresa.getStatus());

        if(database.update(EmpresaDBHelper.TABELA, cv,  empresaDBHelper.ID+" = "+empresa.getId(), null) < 1){
            retorno = database.insert(EmpresaDBHelper.TABELA, null, cv);
            database.close();
            return retorno;
        } else{
            database.close();
            return -1;
        }

    }

    public int deletarInativos(){
        int retorno = database.delete(EmpresaDBHelper.TABELA, empresaDBHelper.STATUS+" = "+2, null);
        database.close();
        return retorno;
    }

    public List<Empresa> listarTodos(){
        Cursor cursor = database.rawQuery("SELECT _id, razao_social, fantasia, cnpj, site, telefone, status FROM "+empresaDBHelper.TABELA, null);
        EmpresaDBHelper empresaDBHelper = new EmpresaDBHelper(context);
        List<Empresa> empresas = new ArrayList<Empresa>();

        if(cursor.moveToFirst()){
            do{
                Empresa umaEmpresa = new Empresa();
                umaEmpresa.setId(cursor.getInt(0));

                umaEmpresa.setRazaoSocial(cursor.getString(1));
                umaEmpresa.setFantasia(cursor.getString(2));
                umaEmpresa.setCnpj(cursor.getString(3));
                umaEmpresa.setSite(cursor.getString(4));
                umaEmpresa.setTelefone(cursor.getString(5));
                umaEmpresa.setStatus(cursor.getInt(6));

                empresas.add(umaEmpresa);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return empresas;
    }

    public Empresa carregar(Empresa empresa){
        Cursor cursor = database.rawQuery("SELECT _id, razao_social, fantasia, cnpj, site, telefone, status FROM "+empresaDBHelper.TABELA
                +" WHERE _id = ?", new String[]{String.valueOf(empresa.getId())});
        EmpresaDBHelper empresaDBHelper = new EmpresaDBHelper(context);

        Empresa umaEmpresa = null;

        if(cursor.moveToFirst()){
            do{
                umaEmpresa = new Empresa();
                umaEmpresa.setId(cursor.getInt(0));

                umaEmpresa.setRazaoSocial(cursor.getString(1));
                umaEmpresa.setFantasia(cursor.getString(2));
                umaEmpresa.setCnpj(cursor.getString(3));
                umaEmpresa.setSite(cursor.getString(4));
                umaEmpresa.setTelefone(cursor.getString(5));
                umaEmpresa.setStatus(cursor.getInt(6));

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return umaEmpresa;
    }

}
