package br.com.vostre.circular.utils;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Local;

/**
 * Created by Almir on 08/11/2014.
 */
public class LocalEstadoSpinner extends ArrayAdapter<Local> {

    private final Activity context;
    private final List<Local> locais;
    int position = 0;

    public LocalEstadoSpinner(Activity context, int resource, List<Local> objects, int position) {
        super(context, resource, objects);
        this.context = context;
        this.locais = objects;
        this.position = position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_spinner_local_estado, null, true);
        TextView textViewLocal = (TextView) rowView.findViewById(R.id.textViewLocal);
        TextView textViewEstado = (TextView) rowView.findViewById(R.id.textViewEstado);

        Local umLocal = locais.get(position);

        textViewLocal.setText(umLocal.getNome());

        if(umLocal.getEstado() != null){

            String estado = "";

            if(umLocal.getCidade() != null){
                estado = umLocal.getCidade().getNome()+" - ";
            }

            textViewEstado.setText(estado+umLocal.getEstado().getNome());
        } else{
            textViewEstado.setVisibility(View.GONE);
        }

        //textViewEstado.setTextColor(Color.WHITE);

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
                row = inflater.inflate(R.layout.custom_spinner_local_estado, parent, false);
            }

            TextView textViewLocal = (TextView) row.findViewById(R.id.textViewLocal);
            TextView textViewEstado = (TextView) row.findViewById(R.id.textViewEstado);

            Local umLocal = locais.get(position);

            if(textViewLocal != null){
                textViewLocal.setText(umLocal.getNome());
            }

            if(textViewEstado != null){

                if(umLocal.getEstado().getNome() != null){

                    String estado = "";

                    if(umLocal.getCidade() != null){
                        estado = umLocal.getCidade().getNome()+" - ";
                    }

                    textViewEstado.setText(estado+umLocal.getEstado().getNome());
                }

                textViewLocal.setTextColor(Color.BLACK);
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
