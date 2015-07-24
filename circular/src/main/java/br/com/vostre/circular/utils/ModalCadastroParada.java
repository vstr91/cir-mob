package br.com.vostre.circular.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Local;
import br.com.vostre.circular.model.ParadaColeta;
import br.com.vostre.circular.model.dao.BairroDBHelper;
import br.com.vostre.circular.model.dao.LocalDBHelper;
import br.com.vostre.circular.model.dao.ParadaColetaDBHelper;

/**
 * Created by Almir on 07/04/2015.
 */
public class ModalCadastroParada extends android.support.v4.app.DialogFragment {

    public double latitude;
    public double longitude;
    private GoogleMap map;
    EditText editTextReferencia;
    EditText editTextLatitude;
    EditText editTextLongitude;
    private CustomSpinner cmbCidade;
    private CustomSpinner cmbBairro;
    private Button btnSalvar;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modal_cadastro_parada, container, false);

        editTextReferencia = (EditText) view.findViewById(R.id.editTextReferencia);
        editTextLatitude = (EditText) view.findViewById(R.id.editTextLatitude);
        editTextLongitude = (EditText) view.findViewById(R.id.editTextLongitude);
        cmbCidade = (CustomSpinner) view.findViewById(R.id.spinnerCidade);
        cmbBairro = (CustomSpinner) view.findViewById(R.id.spinnerBairro);
        btnSalvar = (Button) view.findViewById(R.id.buttonSalvar);
        final LocalDBHelper localDBHelper = new LocalDBHelper(getActivity());
        final BairroDBHelper bairroDBHelper = new BairroDBHelper(getActivity());
        final ParadaColetaDBHelper paradaColetaDBHelper = new ParadaColetaDBHelper(getActivity());

        editTextLatitude.setText(String.valueOf(this.getLatitude()));
        editTextLongitude.setText(String.valueOf(this.getLongitude()));

        final List<Local> locaisDb = localDBHelper.listarTodos(getActivity());
        final CustomAdapter<Local> adapter = new
                CustomAdapter<Local>(getActivity(), R.layout.custom_spinner, locaisDb, "Cidade", locaisDb.size());

        adapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);

        cmbCidade.setAdapter(adapter);

        if(locaisDb.size() == 2){
            cmbCidade.setSelection(0);
        } else{
            cmbCidade.setSelection(locaisDb.size()-1, false);
        }

        cmbCidade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Bairro bairroPlaceholder = new Bairro();
                bairroPlaceholder.setNome("Selecione o Bairro");

                Local local = (Local) adapterView.getItemAtPosition(i);

                List<Bairro> bairrosDb = bairroDBHelper.listarTodosPorLocal(getActivity(), local);

                bairrosDb.add(bairroPlaceholder);

                CustomAdapter<Bairro> adapterBairro =
                        new CustomAdapter<Bairro>(getActivity(), R.layout.custom_spinner, bairrosDb, "", bairrosDb.size());
                adapterBairro.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
                cmbBairro.setAdapter(adapterBairro);

                if(bairrosDb.size() == 2){
                    cmbBairro.setSelection(0);
                } else{
                    cmbBairro.setSelection(bairrosDb.size() - 1, false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String referencia = editTextReferencia.getText().toString();
                Bairro bairro = (Bairro) cmbBairro.getSelectedItem();
                String latitude = editTextLatitude.getText().toString();
                String longitude = editTextLongitude.getText().toString();

                Toast.makeText(view.getContext(), "Referencia: "+referencia+" | Bairro: "+bairro.toString()+" - "+bairro.getId()+" | Latitude: "
                        +latitude+" | Longitude: "+longitude, Toast.LENGTH_LONG).show();

                ParadaColeta paradaColeta = new ParadaColeta();
                paradaColeta.setReferencia(referencia);
                paradaColeta.setStatus(3);
                paradaColeta.setLatitude(latitude);
                paradaColeta.setLongitude(longitude);

                Calendar cal = Calendar.getInstance();

                paradaColeta.setDataColeta(cal);
                paradaColeta.setBairro(bairro);
                paradaColeta.setEnviado(-1);

                paradaColetaDBHelper.salvarOuAtualizar(view.getContext(), paradaColeta);

                map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(paradaColeta.getLatitude()),
                        Double.parseDouble(paradaColeta.getLongitude()))).title(paradaColeta.getReferencia()).draggable(false));

                dismiss();

            }
        });

        return view;

    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public GoogleMap getMap() {
        return map;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }
}
