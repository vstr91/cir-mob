package br.com.vostre.circular.model;

import java.util.Calendar;

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
}
