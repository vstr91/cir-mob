package br.com.vostre.circular;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.dao.ItinerarioDBHelper;
import br.com.vostre.circular.model.dao.ParadaDBHelper;
import br.com.vostre.circular.utils.ItinerarioFavoritoList;
import br.com.vostre.circular.utils.ParadaFavoritaList;
import br.com.vostre.circular.utils.PreferencesUtils;

/**
 * Created by Almir on 17/06/2015.
 */
public class FragmentItinerariosFavoritos extends Fragment implements TextWatcher, AdapterView.OnItemClickListener {

//    private EditText editTextFiltro;
    private ListView lista;
    ItinerarioFavoritoList adapterItinerariosFavoritos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_listview_com_filtro_itinerarios_favoritos, container, false);

        lista = (ListView) rootView.findViewById(R.id.listViewItinerariosFavoritos);
//        editTextFiltro = (EditText) rootView.findViewById(R.id.editTextFiltro);
//        editTextFiltro.addTextChangedListener(this);

        List<Itinerario> itinerariosFavoritos = carregaItinerariosFavoritos();

        adapterItinerariosFavoritos =
                new ItinerarioFavoritoList(getActivity(), android.R.layout.simple_spinner_dropdown_item, itinerariosFavoritos);

        adapterItinerariosFavoritos.setDropDownViewResource(android.R.layout.simple_selectable_list_item);

        lista.setAdapter(adapterItinerariosFavoritos);
        lista.setOnItemClickListener(this);
        lista.setEmptyView(rootView.findViewById(R.id.textViewListaVazia));

        return rootView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        adapterItinerariosFavoritos.getFilter().filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Itinerario itinerario = adapterItinerariosFavoritos.getItem(position);

        Intent intent = new Intent(getContext(), TodosHorariosNovo.class);
        intent.putExtra("id_partida", itinerario.getPartida().getId());
        intent.putExtra("id_destino", itinerario.getDestino().getId());
        intent.putExtra("itinerario", itinerario.getId());

        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Itinerario> itinerariosFavoritos = carregaItinerariosFavoritos();
        adapterItinerariosFavoritos =
                new ItinerarioFavoritoList(getActivity(), android.R.layout.simple_spinner_dropdown_item, itinerariosFavoritos);
        adapterItinerariosFavoritos.notifyDataSetChanged();

        if(lista != null){
            lista.setAdapter(adapterItinerariosFavoritos);
        }

    }

    private List<Itinerario> carregaItinerariosFavoritos(){
        List<String> lstItinerarios = PreferencesUtils.carregaItinerariosFavoritos(getContext());

        List<Itinerario> itinerariosFavoritos = new ArrayList<>();
        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(getContext());

        for(String id : lstItinerarios){
            Itinerario itinerario = new Itinerario();

            String[] dados = id.split("\\|");

            Bairro partida = new Bairro();
            Bairro destino = new Bairro();

            partida.setId(Integer.parseInt(dados[0]));
            destino.setId(Integer.parseInt(dados[1]));

            itinerario.setPartida(partida);
            itinerario.setDestino(destino);

            itinerario = itinerarioDBHelper.carregarPorPartidaEDestino(getContext(), itinerario);

            itinerariosFavoritos.add(itinerario);

        }

        return itinerariosFavoritos;

    }

}
