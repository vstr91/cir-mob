package br.com.vostre.circular.model;

import android.app.ProgressDialog;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.vostre.circular.model.dao.ItinerarioDBHelper;

/**
 * Created by Almir on 14/04/2014.
 */
public class Itinerario {

    private int id;
    private Bairro partida;
    private Bairro destino;
    private Empresa empresa;
    private double valor;
    private int status;
    private String observacao;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bairro getPartida() {
        return partida;
    }

    public void setPartida(Bairro partida) {
        this.partida = partida;
    }

    public Bairro getDestino() {
        return destino;
    }

    public void setDestino(Bairro destino) {
        this.destino = destino;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public String toString() {

        if(this.getPartida().getLocal().getId() != this.getDestino().getLocal().getId()){
            return this.getPartida().getLocal().getNome()+" x "+this.getDestino().getLocal().getNome();
        } else{
            return this.getPartida().getNome()+" x "+this.getDestino().getNome();
        }


    }

    public void atualizarDados(JSONArray dados, int qtdDados, ProgressDialog progressDialog, Context context) throws JSONException {

        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(context);

        for(int i = 0; i < qtdDados; i++){

            progressDialog.setProgress(i+1);

            JSONObject itinerarioObject =  dados.getJSONObject(i);
            Itinerario umItinerario = new Itinerario();
            umItinerario.setId(itinerarioObject.getInt("id"));

            Bairro bairroPartida = new Bairro();
            bairroPartida.setId(itinerarioObject.getInt("partida"));
            umItinerario.setPartida(bairroPartida);

            Bairro bairroDestino = new Bairro();
            bairroDestino.setId(itinerarioObject.getInt("destino"));
            umItinerario.setDestino(bairroDestino);

            umItinerario.setValor(itinerarioObject.getDouble("valor"));
            umItinerario.setStatus(itinerarioObject.getInt("status"));

            Empresa empresaItinerario = new Empresa();
            empresaItinerario.setId(itinerarioObject.getInt("empresa"));
            umItinerario.setEmpresa(empresaItinerario);

            umItinerario.setObservacao(itinerarioObject.getString("observacao"));

            itinerarioDBHelper.salvarOuAtualizar(context, umItinerario);

        }

        itinerarioDBHelper.deletarInativos(context);

    }

}
