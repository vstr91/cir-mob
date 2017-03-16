package br.com.vostre.circular.model;

import android.app.ProgressDialog;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import br.com.vostre.circular.model.dao.ParadaDBHelper;

/**
 * Created by Almir on 14/04/2014.
 */
public class Parada {

    private int id;
    private Bairro bairro;
    private String referencia;
    private String latitude;
    private String longitude;
    private int status;

    private Double taxaDeEmbarque;
    private String slug;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Double getTaxaDeEmbarque() {
        return taxaDeEmbarque;
    }

    public void setTaxaDeEmbarque(Double taxaDeEmbarque) {
        this.taxaDeEmbarque = taxaDeEmbarque;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Override
    public String toString() {
        return this.getReferencia();
    }

    public void atualizarDados(JSONArray dados, int qtdDados, ProgressDialog progressDialog, Context context) throws JSONException {

        ParadaDBHelper paradaDBHelper = new ParadaDBHelper(context);

        for(int i = 0; i < qtdDados; i++){

            progressDialog.setProgress(i+1);

            JSONObject paradaObject =  dados.getJSONObject(i);
            Parada umaParada = new Parada();
            umaParada.setId(paradaObject.getInt("id"));
            umaParada.setReferencia(paradaObject.getString("referencia"));
            umaParada.setLatitude(paradaObject.getString("latitude"));
            umaParada.setLongitude(paradaObject.getString("longitude"));
            umaParada.setStatus(paradaObject.getInt("status"));

            Bairro umBairro = new Bairro();
            umBairro.setId(paradaObject.getInt("bairro"));

            umaParada.setBairro(umBairro);

            if(!paradaObject.getString("taxaDeEmbarque").equals("null") && paradaObject.get("taxaDeEmbarque") != null){
                umaParada.setTaxaDeEmbarque(paradaObject.getDouble("taxaDeEmbarque"));
            }

            umaParada.setSlug(paradaObject.getString("slug"));

            paradaDBHelper.salvarOuAtualizar(context, umaParada);

        }

        paradaDBHelper.deletarInativos(context);

    }

}
