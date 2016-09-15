package br.com.vostre.circular.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Parada;

/**
 * Created by Almir on 27/09/2014.
 */
public class ParadaFavoritaList extends ArrayAdapter<Parada> {

    private final Activity context;
    private final List<Parada> paradas;


    public ParadaFavoritaList(Activity context, int resource, List<Parada> objects) {
        super(context, R.layout.listview_paradas, objects);
        this.context = context;
        this.paradas = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_paradas_favoritas, null, true);
        TextView textViewParada = (TextView) rowView.findViewById(R.id.textViewParadasParada);
        TextView textViewParadaLocal = (TextView) rowView.findViewById(R.id.textViewParadasLocal);

        Parada parada = paradas.get(position);

        textViewParada.setText(parada.getReferencia());
        textViewParadaLocal.setText(parada.getBairro().getNome()+" - "+parada.getBairro().getLocal().getNome());

        return rowView;
    }

}
