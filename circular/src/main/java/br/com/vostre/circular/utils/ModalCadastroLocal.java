package br.com.vostre.circular.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Local;
import br.com.vostre.circular.model.LocalColeta;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.dao.BairroDBHelper;
import br.com.vostre.circular.model.dao.EstadoDBHelper;
import br.com.vostre.circular.model.dao.LocalColetaDBHelper;
import br.com.vostre.circular.model.dao.LocalDBHelper;
import br.com.vostre.circular.model.dao.MensagemDBHelper;

/**
 * Created by Almir on 07/04/2015.
 */
public class ModalCadastroLocal extends android.support.v4.app.DialogFragment implements View.OnClickListener, ListviewComFiltroListener {

    EditText editTextNome;
    private Button btnEstado;
    private Button btnCidade;
    private Button btnSalvar;
    private Button btnFechar;

    ModalCadastroListener listener;

    LocalDBHelper localDBHelper;
    LocalColetaDBHelper localColetaDBHelper;
    EstadoDBHelper estadoDBHelper;

    Estado estadoEscolhido;
    Local cidadeEscolhida;

    CheckBox checkCidade;
    boolean checkMarcado = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modal_cadastro_local, container, false);

        view.setMinimumWidth(700);

        editTextNome = (EditText) view.findViewById(R.id.editTextNome);
        btnEstado = (Button) view.findViewById(R.id.btnEstado);
        btnCidade = (Button) view.findViewById(R.id.btnCidade);

        btnSalvar = (Button) view.findViewById(R.id.btnSalvar);
        btnFechar = (Button) view.findViewById(R.id.btnFechar);
        checkCidade = (CheckBox) view.findViewById(R.id.checkCidade);

        localColetaDBHelper = new LocalColetaDBHelper(getContext());
        estadoDBHelper = new EstadoDBHelper(getContext());

        btnEstado.setOnClickListener(this);
        btnCidade.setOnClickListener(this);

        btnSalvar.setOnClickListener(this);
        btnFechar.setOnClickListener(this);

        checkCidade.setOnClickListener(this);

        btnCidade.setEnabled(false);

        final List<Estado> estadosDb = estadoDBHelper.listarTodos(getActivity());
        final CustomAdapter<Estado> adapter = new
                CustomAdapter<Estado>(getActivity(), R.layout.custom_spinner, estadosDb, "Estado", estadosDb.size());

        adapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);

        return view;

    }

    public ModalCadastroListener getListener() {
        return listener;
    }

    public void setListener(ModalCadastroListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.checkCidade:

                if(checkMarcado){
                    checkMarcado = false;

                    if(estadoEscolhido != null){
                        btnCidade.setEnabled(true);
                    }

                } else{
                    checkMarcado = true;
                    btnCidade.setEnabled(false);
                }

                break;
            case R.id.btnEstado:
                EstadoDBHelper estadoDBHelper = new EstadoDBHelper(v.getContext());

                List<Estado> estados = estadoDBHelper.listarTodos(v.getContext());

                ListviewComFiltro lista = new ListviewComFiltro();
                lista.setDados(estados);
                lista.setTipoObjeto("estado");
                lista.setOnDismissListener(this);
                lista.show(getFragmentManager(), "lista");
                break;
            case R.id.btnCidade:
                List<Local> cidades = listarLocaisPorEstado(estadoEscolhido);

                ListviewComFiltro listaCidades = new ListviewComFiltro();
                listaCidades.setDados(cidades);
                listaCidades.setTipoObjeto("local");
                listaCidades.setOnDismissListener(this);
                listaCidades.show(getFragmentManager(), "listaCidades");
                break;
            case R.id.btnSalvar:
                String nome = editTextNome.getText().toString();
                LocalColetaDBHelper localColetaDBHelper = new LocalColetaDBHelper(getContext());

                if(nome.trim().isEmpty() || estadoEscolhido == null || (cidadeEscolhida == null && !checkMarcado)){
                    Toast.makeText(getActivity(), "Todos os dados são obrigatórios.", Toast.LENGTH_LONG).show();
                } else{
                    LocalColeta local = new LocalColeta();
                    local.setNome(nome.trim());
                    local.setStatus(3);
                    local.setDataColeta(Calendar.getInstance());

                    local.setEstado(estadoEscolhido);

                    if(!checkMarcado){
                        local.setCidade(cidadeEscolhida);
                    } else{
                        local.setCidade(new Local());
                    }

                    Long resultado = localColetaDBHelper.salvarOuAtualizar(getActivity(), local);

                    if(checkMarcado && local.getCidade() == null){
                        LocalColeta cidade = new LocalColeta();
                        cidade.setId(resultado.intValue());
                        cidade = localColetaDBHelper.carregar(getContext(), cidade);
                        local.setCidade(cidade);
                        localColetaDBHelper.salvarOuAtualizar(getContext(), local);
                    }



                    listener.onModalCadastroDismissed(resultado.intValue());

                    Toast.makeText(getActivity(), "Local salvo com sucesso.", Toast.LENGTH_LONG).show();

                    dismiss();
                }

                break;
            case R.id.btnFechar:
                dismiss();
                break;
        }
    }

    public void onStart()
    {
        super.onStart();

        // safety check
        if (getDialog() == null)
            return;

        int dialogWidth = 1000;
        int dialogHeight = 650;

        getDialog().getWindow().setLayout(dialogWidth, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onListviewComFiltroDismissed(Object result, String tipoObjeto, List<?> dados) {

        switch (tipoObjeto){
            case "estado":
                estadoEscolhido = (Estado) result;

                btnEstado.setText(estadoEscolhido.getNome());

                // Testa se retornou apenas um resultado. Em caso positivo, ja segue o preenchimento do restante do formulario,
                // tanto partida quanto destino
                if(dados.size() == 1){
                    Local local = (Local) dados.get(0);
                    cidadeEscolhida = local;
                    btnCidade.setText(local.getNome());

                    if(!checkMarcado){
                        btnCidade.setEnabled(true);
                    }


                } else{

                    if(!checkMarcado){
                        btnCidade.setEnabled(true);
                        btnCidade.setText("Escolha a Cidade");
                        AnimaUtils.animaBotao(btnCidade);
                    }

                    cidadeEscolhida = null;

                }
                break;
            case "cidade":
                Local cidade = (Local) result;
                cidadeEscolhida = cidade;
                btnCidade.setText(cidade.getNome());

                if(!checkMarcado){
                    btnCidade.setEnabled(true);
                }

                break;
        }

    }

    private List<Local> listarLocaisPorEstado(Estado estado){
        localDBHelper = new LocalDBHelper(getActivity());
        return localDBHelper.listarTodosPorEstado(getActivity(), estado);
    }

}
