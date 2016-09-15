package br.com.vostre.circular.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Itinerario;

/**
 * Created by Almir on 27/09/2014.
 */
public class ItinerarioFavoritoList extends ArrayAdapter<Itinerario> {

    private final Activity context;
    private final List<Itinerario> itinerarios;


    public ItinerarioFavoritoList(Activity context, int resource, List<Itinerario> objects) {
        super(context, R.layout.listview_itinerarios, objects);
        this.context = context;
        this.itinerarios = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_itinerarios_favoritos, null, true);
        TextView textViewItinerarioBairroPartida = (TextView) rowView.findViewById(R.id.textViewItinerarioBairroPartida);
        TextView textViewItinerarioLocalPartida = (TextView) rowView.findViewById(R.id.textViewItinerarioLocalPartida);

        TextView textViewItinerarioBairroDestino = (TextView) rowView.findViewById(R.id.textViewItinerarioBairroDestino);
        TextView textViewItinerarioLocalDestino = (TextView) rowView.findViewById(R.id.textViewItinerarioLocalDestino);

        Itinerario itinerario = itinerarios.get(position);

        textViewItinerarioBairroPartida.setText(itinerario.getPartida().getNome());
        textViewItinerarioLocalPartida.setText(itinerario.getPartida().getLocal().getNome());

        textViewItinerarioBairroDestino.setText(itinerario.getDestino().getNome());
        textViewItinerarioLocalDestino.setText(itinerario.getDestino().getLocal().getNome());

        return rowView;
    }

}
