package br.com.vostre.circular.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;

/**
 * Created by Almir on 04/09/2014.
 */
public class ItinerarioList extends ArrayAdapter<HorarioItinerario> {

    private final Activity context;
    private final List<HorarioItinerario> itinerarios;


    public ItinerarioList(Activity context, int resource, List<HorarioItinerario> objects) {
        super(context, R.layout.listview_itinerarios, objects);
        this.context = context;
        this.itinerarios = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_itinerarios, null, true);
        TextView textViewItinerario = (TextView) rowView.findViewById(R.id.textViewParadaDetalheItinerario);
        TextView textViewObs = (TextView) rowView.findViewById(R.id.textViewObs);
        TextView textViewHorario = (TextView) rowView.findViewById(R.id.textViewParadaDetalheHorario);
        textViewItinerario.setText(itinerarios.get(position).getItinerario().toString());

        Horario horario = itinerarios.get(position).getHorario();

        if(horario.getNome() == null){
            textViewHorario.setText("N/D");
        } else{
            textViewHorario.setText(itinerarios.get(position).getHorario().toString());
        }

        HorarioItinerario umHorarioItinerario = itinerarios.get(position);

        if(umHorarioItinerario.getItinerario().getObservacao() != null &&
                !umHorarioItinerario.getItinerario().getObservacao().equals("") &&
                !umHorarioItinerario.getItinerario().getObservacao().equals("null")){
            textViewObs.setText("("+itinerarios.get(position).getItinerario().getObservacao()+")");
        } else{
            textViewObs.setVisibility(View.GONE);
        }

        return rowView;
    }
}
