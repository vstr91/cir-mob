package br.com.vostre.circular.model;

import java.util.Calendar;

import br.com.vostre.circular.utils.DateUtils;

/**
 * Created by Almir on 09/04/2015.
 */
public class ParadaColeta extends Parada {

    private Calendar dataColeta;
    private int enviado;

    public ParadaColeta(){
        this.setEnviado(0);
    }

    public Calendar getDataColeta() {
        return dataColeta;
    }

    public void setDataColeta(Calendar dataColeta) {
        this.dataColeta = dataColeta;
    }

    public int getEnviado() {
        return enviado;
    }

    public void setEnviado(int enviado) {
        this.enviado = enviado;
    }

    public String toJson(){

        String resultado = "";

        resultado = "{\"id\": "+this.getId()+", \"referencia\": \""+this.getReferencia()+"\", " +
                "\"latitude\": \""+this.getLatitude()+"\", \"longitude\": \""+this.getLongitude()+"\", " +
                "\"data\": \""+ DateUtils.converteDataParaPadraoBanco(this.getDataColeta().getTime())+"\"}";


        return resultado;
    }

}