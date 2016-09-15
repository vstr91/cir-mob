package br.com.vostre.circular.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.plus.model.people.Person;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Local;
import br.com.vostre.circular.model.dao.BairroDBHelper;

public class ListviewComFiltro extends android.support.v4.app.DialogFragment implements AdapterView.OnItemClickListener, TextWatcher {

    List<?> dados;
    String tipoObjeto;
    EditText editTextFiltro;
    ListView listViewDados;
    ListviewComFiltroListener listener;
    ListviewComFiltroAdapter adapter;
    Object obj;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.activity_listview_com_filtro, container, false);

        editTextFiltro = (EditText) view.findViewById(R.id.editTextFiltro);
        listViewDados = (ListView) view.findViewById(R.id.listViewDados);

        listViewDados.setOnItemClickListener(this);
        editTextFiltro.addTextChangedListener(this);

        this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        switch (getTipoObjeto()){
            case "local":
                adapter = new ListviewComFiltroAdapter(getActivity(), R.layout.custom_spinner_local_estado,
                        dados, "local");

                adapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);

                listViewDados.setAdapter(adapter);
                break;
            case "bairro":
            case "partida":
                adapter = new ListviewComFiltroAdapter(getActivity(), R.layout.custom_spinner,
                        dados, "partida");

                adapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);

                listViewDados.setAdapter(adapter);
                break;
            case "destino":
                adapter = new ListviewComFiltroAdapter(getActivity(), R.layout.custom_spinner_com_cidade,
                        dados, "destino");

                adapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);

                listViewDados.setAdapter(adapter);
                break;
        }

        return view;

    }

    public List<?> getDados() {
        return dados;
    }

    public void setDados(List<?> dados) {
        this.dados = dados;
    }

    public String getTipoObjeto() {
        return tipoObjeto;
    }

    public void setTipoObjeto(String tipoObjeto) {
        this.tipoObjeto = tipoObjeto;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        obj = adapter.getDados().get(position);

        BairroDBHelper bairroDBHelper = new BairroDBHelper(getActivity());

        if(obj instanceof Local){
            List<Bairro> dados = bairroDBHelper.listarPartidaPorItinerario(getActivity(), (Local) obj);
            listener.onListviewComFiltroDismissed(obj, getTipoObjeto(), dados);
        } else if(obj instanceof Bairro && getTipoObjeto().equals("partida")){
            List<Bairro> dados = bairroDBHelper.listarDestinoPorPartida(getActivity(), (Bairro) obj);
            listener.onListviewComFiltroDismissed(obj, getTipoObjeto(), dados);
        } else if(obj instanceof Bairro && getTipoObjeto().equals("bairro")){
            listener.onListviewComFiltroDismissed(obj, getTipoObjeto(), null);
        } else if(obj instanceof Bairro && getTipoObjeto().equals("destino")){
            listener.onListviewComFiltroDismissed(obj, getTipoObjeto(), null);
        }


        dismiss();
    }

    public void setOnDismissListener(ListviewComFiltroListener listener){
        this.listener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        adapter.getFilter().filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onDismiss(DialogInterface dialog) {

//        if(obj == null){
//            listener.onListviewComFiltroDismissed(obj, getTipoObjeto(), null);
//        }

        super.dismiss();

    }
}