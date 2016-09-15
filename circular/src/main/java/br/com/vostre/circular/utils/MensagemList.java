package br.com.vostre.circular.utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Mensagem;

/**
 * Created by Cefet on 27/08/2015.
 */
public class MensagemList extends ArrayAdapter<Mensagem> {

    private final Activity context;
    private final List<Mensagem> mensagens;


    public MensagemList(Activity context, int resource, List<Mensagem> objects) {
        super(context, R.layout.listview_mensagens, objects);
        this.context = context;
        this.mensagens = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_mensagens, null, true);
        TextView textViewTitulo = (TextView) rowView.findViewById(R.id.textViewTitulo);

        Mensagem mensagem = mensagens.get(position);

        textViewTitulo.setText(mensagem.getTitulo());

        if(mensagem.getStatus() != 0){
            textViewTitulo.setTypeface(null, Typeface.NORMAL);
            textViewTitulo.setBackgroundColor(Color.TRANSPARENT);
            textViewTitulo.setTextColor(Color.parseColor("#FFFFFF"));
        }

        return rowView;
    }

}
