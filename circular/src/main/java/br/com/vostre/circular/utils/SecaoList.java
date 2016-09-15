package br.com.vostre.circular.utils;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.SecaoItinerario;

/**
 * Created by Almir on 27/09/2014.
 */
public class SecaoList extends ArrayAdapter<SecaoItinerario> {

    private final Activity context;
    private final List<SecaoItinerario> secoes;


    public SecaoList(Activity context, int resource, List<SecaoItinerario> objects) {
        super(context, R.layout.listview_todas_secoes, objects);
        this.context = context;
        this.secoes = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_todas_secoes, null, true);

        TextView textViewNomeSecao = (TextView) rowView.findViewById(R.id.textViewNomeSecao);
        TextView textViewValorSecao = (TextView) rowView.findViewById(R.id.textViewValorSecao);

        SecaoItinerario secao = secoes.get(position);
        DecimalFormat nf = (DecimalFormat) NumberFormat.getCurrencyInstance();

        DecimalFormatSymbols symbols = nf.getDecimalFormatSymbols();
        symbols.setCurrencySymbol("");
        nf.setDecimalFormatSymbols(symbols);

        if(secao.getNome() == null){
            textViewNomeSecao.setText("N/D");
        } else{
            textViewNomeSecao.setText(secao.getNome());
            textViewValorSecao.setText("R$ "+nf.format(secao.getValor()));

        }


        return rowView;
    }

}
