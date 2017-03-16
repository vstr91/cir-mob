package br.com.vostre.circular.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.dao.ParadaItinerarioDBHelper;

/**
 * Created by Almir on 04/09/2014.
 */
public class ItinerarioList extends ArrayAdapter<HorarioItinerario> {

    private final Activity context;
    private final List<HorarioItinerario> itinerarios;
    DecimalFormat format = (DecimalFormat) NumberFormat.getCurrencyInstance();
    ParadaItinerarioDBHelper paradaItinerarioDBHelper = new ParadaItinerarioDBHelper(getContext());
    Parada parada;


    public ItinerarioList(Activity context, int resource, List<HorarioItinerario> objects, Parada parada) {
        super(context, R.layout.listview_itinerarios, objects);
        this.context = context;
        this.itinerarios = objects;
        this.parada = parada;

        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        symbols.setCurrencySymbol("");
        format.setDecimalFormatSymbols(symbols);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_itinerarios, null, true);
        TextView textViewItinerario = (TextView) rowView.findViewById(R.id.textViewParadaDetalheItinerario);
        TextView textViewObs = (TextView) rowView.findViewById(R.id.textViewObs);
        TextView textViewHorario = (TextView) rowView.findViewById(R.id.textViewParadaDetalheHorario);
        TextView textViewTarifa = (TextView) rowView.findViewById(R.id.textViewParadaDetalheTarifa);
        TextView textViewObsHorario = (TextView) rowView.findViewById(R.id.textViewObsHorario);
        textViewItinerario.setText(itinerarios.get(position).getItinerario().toString());

        textViewObsHorario.setVisibility(View.GONE);

        Horario horario = itinerarios.get(position).getHorario();

        if(horario.getNome() == null){
            textViewHorario.setText("N/D");
        } else{
            textViewHorario.setText(itinerarios.get(position).getHorario().toString());
        }

        HorarioItinerario umHorarioItinerario = itinerarios.get(position);

        if(umHorarioItinerario.getItinerario().getObservacao() != null &&
                !umHorarioItinerario.getItinerario().getObservacao().equals("") &&
                !umHorarioItinerario.getItinerario().getObservacao().equalsIgnoreCase("null")){
            textViewObs.setText("("+itinerarios.get(position).getItinerario().getObservacao()+")");
        } else{
            textViewObs.setVisibility(View.GONE);
        }

        String obsHorario = umHorarioItinerario.getObs();

        if(obsHorario != null &&
                !obsHorario.equals("") &&
                !obsHorario.equalsIgnoreCase("null")){
            textViewObsHorario.setText(obsHorario);
            textViewObsHorario.setVisibility(View.VISIBLE);
        } else{
            textViewObsHorario.setVisibility(View.GONE);
        }

        if(parada != null){
            Double valor = paradaItinerarioDBHelper.verificarTarifaTrecho(context, itinerarios.get(position).getItinerario(), parada);

            if(valor != null){
                textViewTarifa.setText("R$ " + format.format(valor));
            } else{
                textViewTarifa.setText("R$ " + format.format(umHorarioItinerario.getItinerario().getValor()));
            }

        } else{
            textViewTarifa.setText("R$ " + format.format(umHorarioItinerario.getItinerario().getValor()));
        }

        return rowView;
    }
}
