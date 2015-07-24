package br.com.vostre.circular.model;

import android.app.ProgressDialog;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.vostre.circular.model.dao.EmpresaDBHelper;

/**
 * Created by Almir on 15/09/2014.
 */
public class Empresa {

    private int id;
    private String razaoSocial;
    private String fantasia;
    private String cnpj;
    private String telefone;
    private String site;
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getFantasia() {
        return fantasia;
    }

    public void setFantasia(String fantasia) {
        this.fantasia = fantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void atualizarDados(JSONArray dados, int qtdDados, ProgressDialog progressDialog, Context context) throws JSONException {

        EmpresaDBHelper empresaDBHelper = new EmpresaDBHelper(context);

        for(int i = 0; i < qtdDados; i++){

            progressDialog.setProgress(i+1);

            JSONObject empresaObject =  dados.getJSONObject(i);
            Empresa umaEmpresa = new Empresa();
            umaEmpresa.setId(empresaObject.getInt("id"));
            umaEmpresa.setRazaoSocial(empresaObject.getString("razao_social"));
            umaEmpresa.setFantasia(empresaObject.getString("fantasia"));
            umaEmpresa.setCnpj(empresaObject.getString("cnpj"));
            umaEmpresa.setTelefone(empresaObject.getString("telefone"));
            umaEmpresa.setSite(empresaObject.getString("site"));
            umaEmpresa.setStatus(empresaObject.getInt("status"));

            empresaDBHelper.salvarOuAtualizar(context, umaEmpresa);

        }

        empresaDBHelper.deletarInativos(context);

    }

}
