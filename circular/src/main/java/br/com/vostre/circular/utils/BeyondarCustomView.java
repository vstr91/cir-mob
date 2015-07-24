package br.com.vostre.circular.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.beyondar.android.view.BeyondarViewAdapter;
import com.beyondar.android.world.BeyondarObject;

import br.com.vostre.circular.R;

/**
 * Created by Almir on 25/12/2014.
 */
public class BeyondarCustomView extends BeyondarViewAdapter {

    LayoutInflater inflater;

    public BeyondarCustomView(Context context) {
        super(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(BeyondarObject beyondarObject, View recycledView, ViewGroup viewGroup) {

        if (recycledView == null) {
            recycledView = inflater.inflate(R.layout.beyondar_custom_view, null);
        }

        TextView textViewReferencia = (TextView) recycledView.findViewById(R.id.textViewReferencia);
        textViewReferencia.setText(beyondarObject.getName());

        // Once the view is ready we specify the position
        setPosition(beyondarObject.getScreenPositionTopLeft());

        return recycledView;
    }

}
