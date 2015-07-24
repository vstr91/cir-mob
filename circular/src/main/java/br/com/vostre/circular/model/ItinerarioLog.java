package br.com.vostre.circular.model;

import java.util.Calendar;

/**
 * Created by Almir on 27/05/2015.
 */
public class ItinerarioLog {

    private Itinerario itinerario;
    private Calendar dataConsulta;

    public Itinerario getItinerario() {
        return itinerario;
    }

    public void setItinerario(Itinerario itinerario) {
        this.itinerario = itinerario;
    }

    public Calendar getDataConsulta() {
        return dataConsulta;
    }

    public void setDataConsulta(Calendar dataConsulta) {
        this.dataConsulta = dataConsulta;
    }

}
