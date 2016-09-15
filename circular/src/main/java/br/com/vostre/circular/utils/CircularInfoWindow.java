package br.com.vostre.circular.utils;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import br.com.vostre.circular.R;

/**
 * Created by Almir on 17/11/2015.
 */
public class CircularInfoWindow implements GoogleMap.InfoWindowAdapter {

    private final View infowindow;
    TextView txtNome;
    TextView txtBairro;

    public CircularInfoWindow(Activity activity) {
        infowindow = activity.getLayoutInflater().inflate(R.layout.circular_infowindow, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        render(marker, infowindow);
        return infowindow;
    }

    private void render(Marker marker, View view) {
        txtNome = (TextView) infowindow.findViewById(R.id.txtNome);
        txtBairro = (TextView) infowindow.findViewById(R.id.txtBairro);

        txtNome.setText(marker.getTitle());
        txtBairro.setText(marker.getSnippet());
    }

}
