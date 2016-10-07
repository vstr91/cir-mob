package br.com.vostre.circular.utils;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Almir on 04/10/2016.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public TimePickerListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        java.text.DateFormat df = new SimpleDateFormat("HH:mm");
        Calendar horaEscolhida = Calendar.getInstance();
        horaEscolhida.set(Calendar.HOUR_OF_DAY, hourOfDay);
        horaEscolhida.set(Calendar.MINUTE, minute);

        //listener.onTimeSet(df.format(horaEscolhida.getTime()));
    }

    public TimePickerListener getListener() {
        return listener;
    }

    public void setListener(TimePickerListener listener) {
        this.listener = listener;
    }
}
