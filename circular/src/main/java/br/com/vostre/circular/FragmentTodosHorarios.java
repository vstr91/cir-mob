package br.com.vostre.circular;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.dao.BairroDBHelper;
import br.com.vostre.circular.model.dao.HorarioDBHelper;
import br.com.vostre.circular.model.dao.HorarioItinerarioDBHelper;
import br.com.vostre.circular.model.dao.ItinerarioDBHelper;
import br.com.vostre.circular.model.dao.ParadaItinerarioDBHelper;
import br.com.vostre.circular.utils.HorarioList;

/**
 * Created by Almir on 17/06/2015.
 */
public class FragmentTodosHorarios extends Fragment {

    private TextView txtVia;
    private TextView txtObs;
    private Itinerario itinerario;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_todos_horarios, container, false);

        Bundle valores = getArguments();

        String proximoHorario = valores.getString("proximoHorario");

        itinerario = new Itinerario();
        itinerario.setId(valores.getInt("itinerario"));

        ListView lista = (ListView) rootView.findViewById(R.id.listViewHorarios);
        HorarioItinerarioDBHelper horarioItinerarioDBHelper = new HorarioItinerarioDBHelper(getActivity());
        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(getActivity());
        ParadaItinerarioDBHelper paradaItinerarioDBHelper = new ParadaItinerarioDBHelper(getActivity());

        itinerario = itinerarioDBHelper.carregar(getActivity(), itinerario);

        TextView txtPartida = (TextView) rootView.findViewById(R.id.textViewTodosHorarios);
        TextView txtDestino = (TextView) rootView.findViewById(R.id.textViewItinerario);

        txtVia = (TextView) rootView.findViewById(R.id.textViewVia);
        txtObs = (TextView) rootView.findViewById(R.id.textViewObs);

        List<ParadaItinerario> paradasItinerario = paradaItinerarioDBHelper
                .listaParadasDestaquePorItinerario(getActivity(), itinerario);

        if(paradasItinerario.size() > 0){

            int cont = 0;
            String via = "";

            for(ParadaItinerario umaParada : paradasItinerario){

                if(cont == 0){
                    via = via.concat("Via "+umaParada.getParada().getBairro().getLocal().getNome());
                    cont++;
                } else{
                    via = via.concat(", "+umaParada.getParada().getBairro().getLocal().getNome());
                }

            }

            txtVia.setText(via);

        } else{
            txtVia.setVisibility(View.GONE);
        }

        if(itinerario.getObservacao() != null &&
                !itinerario.getObservacao().equals("") &&
                !itinerario.getObservacao().equals("null")){
            txtObs.setText("("+itinerario.getObservacao()+")");
        } else{
            txtObs.setVisibility(View.GONE);
        }

        if(itinerario.getPartida().getLocal().getId() !=
                itinerario.getDestino().getLocal().getId()){
            txtDestino.setText(itinerario.getPartida().getLocal().getNome()+" X "
                    +itinerario.getDestino().getLocal().getNome());
        } else{
            txtDestino.setText(itinerario.getPartida().getNome()+" X "
                    +itinerario.getDestino().getNome());
        }

        List<HorarioItinerario> todosHorariosItinerario = horarioItinerarioDBHelper
                .listarTodosHorariosPorItinerario(getActivity(), itinerario);

        HorarioList adapterHorarios =
                new HorarioList(getActivity(), android.R.layout.simple_spinner_dropdown_item, todosHorariosItinerario, proximoHorario);

        adapterHorarios.setDropDownViewResource(android.R.layout.simple_selectable_list_item);

        lista.setAdapter(adapterHorarios);

        return rootView;
    }

}
