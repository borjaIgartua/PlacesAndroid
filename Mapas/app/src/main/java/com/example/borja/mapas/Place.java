package com.example.borja.mapas;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Borja on 20/10/17.
 */

public class Place implements Parcelable {

    private LatLng coordinates;
    private String name;
    private String description;
    private String addres;

    public Place() {
    }

    public Place(float lat, float lon, String name, String description, String addres) {

        coordinates = new LatLng(lat,lon);
        this.name = name;
        this.description = description;
        this.addres = addres;
    }

    public Place(Parcel parcel){
        readFromParcel(parcel);
    }

    public void setAddres(String addres) { this.addres = addres; }

    public String getAddres() { return addres; }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(coordinates,i);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(addres);
    }

    private void readFromParcel(Parcel in) {
        coordinates = in.readParcelable(LatLng.class.getClassLoader());
        name = in.readString();
        description = in.readString();
        addres = in.readString();
    }

    public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {

        @Override
        public Place createFromParcel(Parcel source) {
            return new Place(source);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
}
