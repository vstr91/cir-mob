package br.com.vostre.circular.utils;

import android.content.Context;

import java.util.List;

import br.com.vostre.circular.model.dao.MensagemDBHelper;

/**
 * Created by Almir on 29/09/2016.
 */
public class NumberUtils {

    public static int geraIdMensagem(Context ctx, int numeroInicial){

        MensagemDBHelper mensagemDBHelper = new MensagemDBHelper(ctx);
        List mensagens = mensagemDBHelper.listarTodosCadastrados(ctx);

        return numeroInicial+mensagens.size()+1;

    }

}
