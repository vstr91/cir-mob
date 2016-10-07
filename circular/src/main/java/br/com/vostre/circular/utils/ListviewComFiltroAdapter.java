package br.com.vostre.circular.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Local;

/**
 * Created by Almir on 23/09/2015.
 */
public class ListviewComFiltroAdapter extends ArrayAdapter<Object> implements Filterable {

    private final Activity context;
    private List<?> dados;
    private List<?> dadosOriginais;
    private List<Local> listaFiltradaLocal;
    private List<Bairro> listaFiltradaBairro;
    private final String tipoObjeto;
    int position = 0;

    public ListviewComFiltroAdapter(Activity context, int resource, List<?> objects, String tipoObjeto) {
        super(context, resource, (List<Object>) objects);
        this.context = context;
        this.dados = objects;
        this.tipoObjeto = tipoObjeto;
        listaFiltradaLocal = null;
        listaFiltradaBairro = null;
        dadosOriginais = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View rowView = null;

        switch (getTipoObjeto()){
            case "estado":

                rowView = inflater.inflate(R.layout.custom_spinner_local_estado, null, true);
                TextView textViewEstadoNome = (TextView) rowView.findViewById(R.id.textViewLocal);

                Estado umEstado = (Estado) dados.get(position);

                textViewEstadoNome.setText(umEstado.getNome());

                break;
            case "local":

                rowView = inflater.inflate(R.layout.custom_spinner_local_estado, null, true);
                TextView textViewLocal = (TextView) rowView.findViewById(R.id.textViewLocal);
                TextView textViewEstado = (TextView) rowView.findViewById(R.id.textViewEstado);

                Local umLocal = (Local) dados.get(position);

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

                break;
            case "partida":
                rowView = inflater.inflate(R.layout.custom_spinner, null, true);
                TextView textViewBairro = (TextView) rowView.findViewById(R.id.textViewNomeCustom);

                Bairro umBairro = (Bairro) dados.get(position);

                textViewBairro.setText(umBairro.getNome());
                break;
            case "destino":
                rowView = inflater.inflate(R.layout.itinerario_destino_spinner, null, true);
                TextView textViewBairroDestino = (TextView) rowView.findViewById(R.id.bairroParada);
                TextView textViewCidadeBairroDestino = (TextView) rowView.findViewById(R.id.cidadeBairro);

                Bairro umBairroDestino = (Bairro) dados.get(position);

                textViewBairroDestino.setText(umBairroDestino.getNome());
                textViewCidadeBairroDestino.setText(umBairroDestino.getLocal().getNome());
                break;
        }

        return rowView;
    }

    public List<?> getDados() {
        return dados;
    }

    public String getTipoObjeto() {
        return tipoObjeto;
    }

    @Override
    public Filter getFilter() {
        return new ListviewFilter();
    }

    @Override
    public int getCount() {
        return dados.size();
    }

    private class ListviewFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            switch (getTipoObjeto()){
                case "local":
                    listaFiltradaLocal = new ArrayList();
                    break;
                case "partida":
                    listaFiltradaBairro = new ArrayList();
                    break;
                case "destino":
                    listaFiltradaBairro = new ArrayList();
                    break;
            }

            if (constraint != null && dados != null && constraint.length() > 0) {

                constraint = constraint.toString().toLowerCase();
                int length = dados.size();
                int i = 0;

                switch (tipoObjeto){
                    case "local":
                        while (i < length) {

                            Local obj = (Local) dados.get(i);
                            String data = obj.getNome()+" "+obj.getEstado().getNome();
                            if (data.toLowerCase().contains(constraint.toString())) {
                                listaFiltradaLocal.add(obj);
                            }

                            i++;
                        }

                        filterResults.values = listaFiltradaLocal;
                        filterResults.count = listaFiltradaLocal.size();

                        break;
                    case "partida":
                        while (i < length) {

                            Bairro obj = (Bairro) dados.get(i);
                            String data = obj.getNome()+" "+obj.getLocal().getNome();
                            if (data.toLowerCase().contains(constraint.toString())) {
                                listaFiltradaBairro.add(obj);
                            }

                            i++;
                        }

                        filterResults.values = listaFiltradaBairro;
                        filterResults.count = listaFiltradaBairro.size();

                        break;
                    case "destino":
                        while (i < length) {

                            Bairro obj = (Bairro) dados.get(i);
                            String data = obj.getNome()+" "+obj.getLocal().getNome();
                            if (data.toLowerCase().contains(constraint.toString())) {
                                listaFiltradaBairro.add(obj);
                            }

                            i++;
                        }

                        filterResults.values = listaFiltradaBairro;
                        filterResults.count = listaFiltradaBairro.size();
                        break;
                }

            } else{
                filterResults.values = dadosOriginais;
                filterResults.count = dadosOriginais.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //listaFiltrada = (ArrayList<Local>) results.values;
            if (results.count > 0) {

                switch (getTipoObjeto()){
                    case "local":
                        dados = (List<Local>) results.values;
                        break;
                    case "partida":
                        dados = (List<Bairro>) results.values;
                        break;
                    case "destino":
                        dados = (List<Bairro>) results.values;
                        break;
                }


                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

}
