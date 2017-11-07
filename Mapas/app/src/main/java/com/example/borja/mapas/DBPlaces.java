package com.example.borja.mapas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Borja on 20/10/17.
 */

public class DBPlaces {

    private static final String TABLA = "places";
    private SQLiteDatabase dataBase = null;
    private DataBaseHelper dbHelper = null;

    Context context;

    public DBPlaces(Context context) {
        this.context = context;

        dbHelper = new DataBaseHelper(context);
        dataBase = dbHelper.getWritableDatabase();
    }

    public void addPlace(Place place) {

        ContentValues initialValues=new ContentValues();
        initialValues.put("latitud",place.getCoordinates().latitude);
        initialValues.put("longitud",place.getCoordinates().longitude);
        initialValues.put("titulo",place.getName());
        initialValues.put("descripcion",place.getDescription());
        initialValues.put("addres", place.getAddres());

        dataBase.insert(TABLA,null,initialValues);
    }

    public List<Place> getPlaces() {

        String[] columnasABuscar = {"_id", "latitud","longitud","titulo","descripcion", "addres"};
        Cursor c = dataBase.query(TABLA,columnasABuscar,null,null , null,null,null);

        List<Place> places = new ArrayList<Place>();

        if (c.moveToNext()) {

            do {

                float latitud = c.getFloat(1);
                float longitud = c.getFloat(2);
                String titulo = c.getString(3);
                String desc = c.getString(4);
                String addres = c.getString(5);

                places.add(new Place(latitud,longitud,titulo,desc,addres));

            } while(c.moveToNext());
        }

        return places;
    }

    public void delete() {
        dataBase.delete(TABLA,null,null);
    }
}
