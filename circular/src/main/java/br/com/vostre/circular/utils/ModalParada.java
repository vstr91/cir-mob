package br.com.vostre.circular.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import br.com.vostre.circular.R;
import br.com.vostre.circular.TodosHorarios;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaColeta;
import br.com.vostre.circular.model.dao.ParadaColetaDBHelper;

/**
 * Created by Almir on 07/04/2015.
 */
public class ModalParada extends android.support.v4.app.DialogFragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    ListView listViewItinerarios;
    Button btnFechar;
    TextView textViewParada;
    TextView textViewLocal;

    ItinerarioList adapter;
    Parada parada;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modal_parada, container, false);

        listViewItinerarios = (ListView) view.findViewById(R.id.listViewItinerarios);
        textViewParada = (TextView) view.findViewById(R.id.textViewParada);
        textViewLocal = (TextView) view.findViewById(R.id.textViewLocal);
        btnFechar = (Button) view.findViewById(R.id.btnFechar);

        if(this.getDialog() != null){
            this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        listViewItinerarios.setAdapter(getAdapter());

        textViewParada.setText(getParada().getReferencia());
        textViewLocal.setText(getParada().getBairro().getNome()+" - "+getParada().getBairro().getLocal().getNome());

        listViewItinerarios.setOnItemClickListener(this);

        btnFechar.setOnClickListener(this);

        return view;

    }

    public ItinerarioList getAdapter() {
        return adapter;
    }

    public void setAdapter(ItinerarioList adapter) {
        this.adapter = adapter;
    }

    public Parada getParada() {
        return parada;
    }

    public void setParada(Parada parada) {
        this.parada = parada;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HorarioItinerario itinerario = adapter.getItem(position);

        Intent intent = new Intent(getContext(), TodosHorarios.class);
        intent.putExtra("id_partida", itinerario.getItinerario().getPartida().getId());
        intent.putExtra("id_destino", itinerario.getItinerario().getDestino().getId());
        intent.putExtra("itinerario", itinerario.getItinerario().getId());
        intent.putExtra("hora", itinerario.getHorario().toString());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnFechar:
                dismiss();
        }
    }
}
