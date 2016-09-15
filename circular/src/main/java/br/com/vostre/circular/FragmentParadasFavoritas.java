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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Local;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.dao.HorarioItinerarioDBHelper;
import br.com.vostre.circular.model.dao.ItinerarioDBHelper;
import br.com.vostre.circular.model.dao.LocalDBHelper;
import br.com.vostre.circular.model.dao.ParadaDBHelper;
import br.com.vostre.circular.model.dao.ParadaItinerarioDBHelper;
import br.com.vostre.circular.utils.AnimaUtils;
import br.com.vostre.circular.utils.HorarioList;
import br.com.vostre.circular.utils.ListviewComFiltro;
import br.com.vostre.circular.utils.ListviewComFiltroListener;
import br.com.vostre.circular.utils.ParadaFavoritaList;
import br.com.vostre.circular.utils.ParadaList;
import br.com.vostre.circular.utils.PreferencesUtils;
import br.com.vostre.circular.utils.ToolbarUtils;

/**
 * Created by Almir on 17/06/2015.
 */
public class FragmentParadasFavoritas extends Fragment implements TextWatcher, AdapterView.OnItemClickListener {

//    private EditText editTextFiltro;
    private ListView lista;
    ParadaFavoritaList adapterParadasFavoritas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_listview_com_filtro_paradas_favoritas, container, false);

        lista = (ListView) rootView.findViewById(R.id.listViewParadasFavoritas);
//        editTextFiltro = (EditText) rootView.findViewById(R.id.editTextFiltro);
//        editTextFiltro.addTextChangedListener(this);

        List<Parada> paradasFavoritas = carregaParadasFavoritas();

        adapterParadasFavoritas =
                new ParadaFavoritaList(getActivity(), android.R.layout.simple_spinner_dropdown_item, paradasFavoritas);

        adapterParadasFavoritas.setDropDownViewResource(android.R.layout.simple_selectable_list_item);

        lista.setAdapter(adapterParadasFavoritas);
        lista.setOnItemClickListener(this);
        lista.setEmptyView(rootView.findViewById(R.id.textViewListaVazia));

        return rootView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        adapterParadasFavoritas.getFilter().filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Parada parada = adapterParadasFavoritas.getItem(position);

        Intent intent = new Intent(getContext(), ParadaDetalhe.class);
        intent.putExtra("id_parada", parada.getId());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Parada> paradasFavoritas = carregaParadasFavoritas();
        adapterParadasFavoritas =
                new ParadaFavoritaList(getActivity(), android.R.layout.simple_spinner_dropdown_item, paradasFavoritas);
        adapterParadasFavoritas.notifyDataSetChanged();

        if(lista != null){
            lista.setAdapter(adapterParadasFavoritas);
        }

    }

    private List<Parada> carregaParadasFavoritas(){
        List<String> lstParadas = PreferencesUtils.carregaParadasFavoritas(getContext());

        List<Parada> paradasFavoritas = new ArrayList<>();
        ParadaDBHelper paradaDBHelper = new ParadaDBHelper(getContext());

        for(String id : lstParadas){
            Parada parada = new Parada();
            parada.setId(Integer.parseInt(id));
            parada = paradaDBHelper.carregar(getContext(), parada);

            paradasFavoritas.add(parada);

        }

        return paradasFavoritas;

    }

}
