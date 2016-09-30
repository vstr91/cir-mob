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

import java.util.List;

import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.dao.MensagemDBHelper;
import br.com.vostre.circular.utils.MensagemList;

/**
 * Created by Almir on 17/06/2015.
 */
public class FragmentMensagensEnviadas extends Fragment implements TextWatcher, AdapterView.OnItemClickListener {

    private ListView lista;
    MensagemList adapterMensagens;
    List<Mensagem> mensagens;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_listview_mensagens_enviadas, container, false);

        lista = (ListView) rootView.findViewById(R.id.listViewMensagensEnviadas);
//        editTextFiltro = (EditText) rootView.findViewById(R.id.editTextFiltro);
//        editTextFiltro.addTextChangedListener(this);

        mensagens = carregaMensagensEnviadas();

        adapterMensagens =
                new MensagemList(getActivity(), android.R.layout.simple_spinner_dropdown_item, mensagens);

        adapterMensagens.setDropDownViewResource(android.R.layout.simple_selectable_list_item);

        lista.setAdapter(adapterMensagens);
        lista.setOnItemClickListener(this);
        lista.setEmptyView(rootView.findViewById(R.id.textViewListaVazia));

        return rootView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        adapterMensagens.getFilter().filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Mensagem mensagem = adapterMensagens.getItem(position);

        Intent intent = new Intent(getContext(), DetalheMensagem.class);
        intent.putExtra("mensagem", mensagem.getId());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        atualizaLista();
    }

    private List<Mensagem> carregaMensagensEnviadas(){
        MensagemDBHelper mensagemDBHelper = new MensagemDBHelper(getContext());
        mensagens = mensagemDBHelper.listarTodosCadastrados(getContext());

        return mensagens;

    }

    public void atualizaLista(){
        mensagens = carregaMensagensEnviadas();
        adapterMensagens =
                new MensagemList(getActivity(), android.R.layout.simple_spinner_dropdown_item, mensagens);
        adapterMensagens.notifyDataSetChanged();

        if(lista != null){
            lista.setAdapter(adapterMensagens);
        }
    }

}
