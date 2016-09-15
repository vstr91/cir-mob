package br.com.vostre.circular;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.com.vostre.circular.utils.ScreenUtils;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScreenUtils.modificaHeaderBandeja(this);

    }
}
