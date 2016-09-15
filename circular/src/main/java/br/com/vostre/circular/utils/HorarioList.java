package br.com.vostre.circular.utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;

/**
 * Created by Almir on 27/09/2014.
 */
public class HorarioList extends ArrayAdapter<HorarioItinerario> {

    private final Activity context;
    private final List<HorarioItinerario> horarios;
    private final String proximoHorario;
    private int posicaoProximoHorario;


    public HorarioList(Activity context, int resource, List<HorarioItinerario> objects, String proximoHorario) {
        super(context, R.layout.listview_todos_horarios, objects);
        this.context = context;
        this.horarios = objects;
        this.proximoHorario = proximoHorario;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_todos_horarios, null, true);
        TextView textViewHorario = (TextView) rowView.findViewById(R.id.textViewTodosHorariosHorario);
        TextView textViewObs = (TextView) rowView.findViewById(R.id.textViewObs);

        ImageView imgDomingo = (ImageView) rowView.findViewById(R.id.imgDomingo);
        ImageView imgSegunda = (ImageView) rowView.findViewById(R.id.imgSegunda);
        ImageView imgTerca = (ImageView) rowView.findViewById(R.id.imgTerca);
        ImageView imgQuarta = (ImageView) rowView.findViewById(R.id.imgQuarta);
        ImageView imgQuinta = (ImageView) rowView.findViewById(R.id.imgQuinta);
        ImageView imgSexta = (ImageView) rowView.findViewById(R.id.imgSexta);
        ImageView imgSabado = (ImageView) rowView.findViewById(R.id.imgSabado);

        Horario horario = horarios.get(position).getHorario();
        HorarioItinerario horarioItinerario = horarios.get(position);

        if(horario.getNome() == null){
            textViewHorario.setText("N/D");
        } else{
            textViewHorario.setText(horarios.get(position).getHorario().toString());

            if(proximoHorario.equals(horarios.get(position).getHorario().toString())){
                textViewHorario.setTextColor(Color.RED);
                rowView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                textViewObs.setTextColor(Color.parseColor("#1E1F1C"));
                setPosicaoProximoHorario(position);
            }

            if(horarioItinerario.getObs() != null && !horarioItinerario.getObs().equals("") && !horarioItinerario.getObs().equals("null")){
                textViewObs.setText(horarioItinerario.getObs());
            } else{
                textViewObs.setVisibility(View.GONE);
            }

            if(horarios.get(position).getDomingo() != -1){
                imgDomingo.setBackgroundResource(R.drawable.domingo_gc);
            }

            if(horarios.get(position).getSegunda() != -1){
                imgSegunda.setBackgroundResource(R.drawable.segunda_gc);
            }

            if(horarios.get(position).getTerca() != -1){
                imgTerca.setBackgroundResource(R.drawable.terca_gc);
            }

            if(horarios.get(position).getQuarta() != -1){
                imgQuarta.setBackgroundResource(R.drawable.quarta_gc);
            }

            if(horarios.get(position).getQuinta() != -1){
                imgQuinta.setBackgroundResource(R.drawable.quinta_gc);
            }

            if(horarios.get(position).getSexta() != -1){
                imgSexta.setBackgroundResource(R.drawable.sexta_gc);
            }

            if(horarios.get(position).getSabado() != -1){
                imgSabado.setBackgroundResource(R.drawable.sabado_gc);
            }

        }


        return rowView;
    }

    public int getPosicaoProximoHorario() {
        return this.posicaoProximoHorario;
    }

    public void setPosicaoProximoHorario(int posicaoProximoHorario) {
        this.posicaoProximoHorario = posicaoProximoHorario;
    }

}
