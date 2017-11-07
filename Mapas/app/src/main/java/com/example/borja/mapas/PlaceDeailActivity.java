package com.example.borja.mapas;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class PlaceDeailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_deail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Place place = (Place)intent.getParcelableExtra("place");

        TextView nameTextView = (TextView)findViewById(R.id.detail_place_name);
        TextView descriptionTextView = (TextView)findViewById(R.id.detail_place_description);
        TextView addressTextView = (TextView)findViewById(R.id.detail_place_address);

        nameTextView.setText(place.getName());
        descriptionTextView.setText(place.getDescription());
        addressTextView.setText(place.getAddres());
    }

}
