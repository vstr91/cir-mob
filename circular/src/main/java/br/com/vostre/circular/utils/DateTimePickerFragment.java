package br.com.vostre.circular.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.com.vostre.circular.R;

/**
 * Created by Almir on 05/10/2016.
 */
public class DateTimePickerFragment extends DialogFragment implements View.OnClickListener, TimePicker.OnTimeChangedListener {

    Button btnConsultar;
    Button btnFechar;
    RadioGroup radioGroup;

    public TimePickerListener listener;

    String hora;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_time_picker, container, false);

        TimePicker tp = (TimePicker) view.findViewById(R.id.timePicker);
        tp.setIs24HourView(true);
        tp.setOnTimeChangedListener(this);

        btnConsultar = (Button) view.findViewById(R.id.btnConsultar);
        btnFechar = (Button) view.findViewById(R.id.btnFechar);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);

        btnConsultar.setOnClickListener(this);
        btnFechar.setOnClickListener(this);

        RadioButton radio = null;
        hora = DateUtils.getHoraAtual();

        switch(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)){
            case Calendar.SUNDAY:
                radio = (RadioButton) view.findViewById(R.id.radioDomingo);
                break;
            case Calendar.MONDAY:
                radio = (RadioButton) view.findViewById(R.id.radioSegunda);
                break;
            case Calendar.TUESDAY:
                radio = (RadioButton) view.findViewById(R.id.radioTerca);
                break;
            case Calendar.WEDNESDAY:
                radio = (RadioButton) view.findViewById(R.id.radioQuarta);
                break;
            case Calendar.THURSDAY:
                radio = (RadioButton) view.findViewById(R.id.radioQuinta);
                break;
            case Calendar.FRIDAY:
                radio = (RadioButton) view.findViewById(R.id.radioSexta);
                break;
            case Calendar.SATURDAY:
                radio = (RadioButton) view.findViewById(R.id.radioSabado);
                break;
        }

        if(radio != null){
            radio.setChecked(true);
        }

        return view;
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.btnConsultar:

                int selecionado = radioGroup.getCheckedRadioButtonId();
                int diaDaSemana = -1;

                switch(selecionado){
                    case R.id.radioDomingo:
                        diaDaSemana = Calendar.SUNDAY;
                        break;
                    case R.id.radioSegunda:
                        diaDaSemana = Calendar.MONDAY;
                        break;
                    case R.id.radioTerca:
                        diaDaSemana = Calendar.TUESDAY;
                        break;
                    case R.id.radioQuarta:
                        diaDaSemana = Calendar.WEDNESDAY;
                        break;
                    case R.id.radioQuinta:
                        diaDaSemana = Calendar.THURSDAY;
                        break;
                    case R.id.radioSexta:
                        diaDaSemana = Calendar.FRIDAY;
                        break;
                    case R.id.radioSabado:
                        diaDaSemana = Calendar.SATURDAY;
                        break;
                }

                if(hora != null && diaDaSemana > -1){
                    listener.onTimeSet(hora, diaDaSemana);
                    dismiss();
                } else{
                    Toast.makeText(getContext(), "Informe dia e horário para continuar", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btnFechar:
                dismiss();
                break;
        }

    }

    public TimePickerListener getListener() {
        return listener;
    }

    public void setListener(TimePickerListener listener) {
        this.listener = listener;
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        java.text.DateFormat df = new SimpleDateFormat("HH:mm");
        Calendar horaEscolhida = Calendar.getInstance();
        horaEscolhida.set(Calendar.HOUR_OF_DAY, hourOfDay);
        horaEscolhida.set(Calendar.MINUTE, minute);

        hora = df.format(horaEscolhida.getTime());
    }

}
