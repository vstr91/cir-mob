package br.com.vostre.circular.utils;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import java.util.Calendar;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Local;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaColeta;
import br.com.vostre.circular.model.dao.BairroDBHelper;
import br.com.vostre.circular.model.dao.LocalDBHelper;
import br.com.vostre.circular.model.dao.MensagemDBHelper;
import br.com.vostre.circular.model.dao.ParadaColetaDBHelper;

/**
 * Created by Almir on 07/04/2015.
 */
public class ModalCadastroMensagem extends android.support.v4.app.DialogFragment implements View.OnClickListener {

    EditText editTextTitulo;
    EditText editTextDescricao;
    private Button btnEnviar;
    private Button btnFechar;

    ModalCadastroListener listener;

    MensagemDBHelper mensagemDBHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modal_cadastro_mensagem, container, false);

        view.setMinimumWidth(700);

        editTextTitulo = (EditText) view.findViewById(R.id.editTextTitulo);
        editTextDescricao = (EditText) view.findViewById(R.id.editTextDescricao);
        btnEnviar = (Button) view.findViewById(R.id.btnEnviar);
        btnFechar = (Button) view.findViewById(R.id.btnFechar);

        mensagemDBHelper = new MensagemDBHelper(getContext());

        btnEnviar.setOnClickListener(this);
        btnFechar.setOnClickListener(this);

        return view;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
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
            case R.id.btnEnviar:
                String titulo = editTextTitulo.getText().toString();
                String descricao = editTextDescricao.getText().toString();

                if(titulo.trim().isEmpty() || descricao.trim().isEmpty()){
                    Toast.makeText(getActivity(), "Todos os dados são obrigatórios.", Toast.LENGTH_LONG).show();
                } else{
                    Mensagem mensagem = new Mensagem();
                    mensagem.setId(NumberUtils.geraIdMensagem(getContext(), 10000));
                    mensagem.setTitulo(titulo);
                    mensagem.setDescricao(descricao);
                    mensagem.setStatus(3);
                    mensagem.setDataEnvio(Calendar.getInstance());

                    Long resultado = mensagemDBHelper.salvarOuAtualizar(getActivity(), mensagem);

                    listener.onModalCadastroDismissed(resultado.intValue());

                    Toast.makeText(getActivity(), "Mensagem salva com sucesso. Ela será enviada em breve!", Toast.LENGTH_LONG).show();

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

        DisplayMetrics metrics = new DisplayMetrics();
        getDialog().getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int dialogWidth = (int) (metrics.widthPixels * 0.9);

        getDialog().getWindow().setLayout(dialogWidth, WindowManager.LayoutParams.WRAP_CONTENT);
    }

}
