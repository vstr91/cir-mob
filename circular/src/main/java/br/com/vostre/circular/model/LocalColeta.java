package br.com.vostre.circular.model;

import java.util.Calendar;

import br.com.vostre.circular.utils.DateUtils;

/**
 * Created by Almir on 04/10/2016.
 */
public class LocalColeta extends Local {

    private Calendar dataColeta;

    public Calendar getDataColeta() {
        return dataColeta;
    }

    public void setDataColeta(Calendar dataColeta) {
        this.dataColeta = dataColeta;
    }

    public String toJson(){

        String resultado = "";

        resultado = "{\"id\": "+this.getId()+", \"estado\": \""+this.getEstado().getId()+"\", " +
                "\"cidade\": \""+this.getCidade().getId()+"\", \"nome\": \""+this.getNome()+"\", " +
                "\"data\": \""+ DateUtils.converteDataParaPadraoBanco(this.getDataColeta().getTime())+"\"}";


        return resultado;
    }

}
