package br.com.vostre.circular.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Parada;

/**
 * Created by Almir on 08/11/2014.
 */
public class ItinerarioDestinoSpinner extends ArrayAdapter<Bairro> {

    private final Activity context;
    private final List<Bairro> bairros;
    int position = 0;

    public ItinerarioDestinoSpinner(Activity context, int resource, List<Bairro> objects, int position) {
        super(context, resource, objects);
        this.context = context;
        this.bairros = objects;
        this.position = position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.itinerario_destino_spinner, null, true);
        TextView textViewBairroParada = (TextView) rowView.findViewById(R.id.bairroParada);
        TextView textViewCidadeBairro = (TextView) rowView.findViewById(R.id.cidadeBairro);

        Bairro bairro = bairros.get(position);

        textViewBairroParada.setText(bairro.getNome());

        String local = "";

        if(null != bairro.getLocal()){
            local = bairro.getLocal().getNome();
        } else{
            textViewCidadeBairro.setVisibility(View.GONE);
        }

        textViewCidadeBairro.setText(local);

        textViewBairroParada.setTextColor(Color.WHITE);

        return rowView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        View row = null;

        if(position == this.position-1){
            TextView tv = new TextView(getContext());
            tv.setVisibility(View.GONE);
            row = tv;
        } else{

            if(row == null){
                LayoutInflater inflater = context.getLayoutInflater();
                row = inflater.inflate(R.layout.itinerario_destino_spinner, parent, false);
            }

            TextView textViewBairroParada = (TextView) row.findViewById(R.id.bairroParada);
            TextView textViewCidadeBairro = (TextView) row.findViewById(R.id.cidadeBairro);

            Bairro bairro = bairros.get(position);

            if(textViewBairroParada != null){
                textViewBairroParada.setText(bairro.getNome());
            }

            if(textViewCidadeBairro != null){

                if(bairro.getLocal() != null){
                    textViewCidadeBairro.setText(bairro.getLocal().getNome());
                }

                textViewBairroParada.setTextColor(Color.BLACK);
            }

            //row = super.getDropDownView(position, null, parent);
        }
/*
        if(row == null){
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.itinerario_destino_spinner, parent, false);
        }

        TextView textViewBairroParada = (TextView) row.findViewById(R.id.bairroParada);
        TextView textViewCidadeBairro = (TextView) row.findViewById(R.id.cidadeBairro);

        Bairro bairro = bairros.get(position);

        textViewBairroParada.setText(bairro.getNome());

        String local = "";

        if(null != bairro.getLocal()){
            local = bairro.getLocal().getNome();
        }

        textViewCidadeBairro.setText(local);

        textViewBairroParada.setTextColor(Color.BLACK);
*/
        return row;

        //return super.getDropDownView(position, convertView, parent);
    }
}
