package com.example.borja.mapas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Menu actionMenu;

    private List<Place> places;
    LocationManager locManager;

    DBPlaces dbPlaces;


    public enum gpsStatus {
        ENABLE, OFFLINE, LOCATING
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dbPlaces = new DBPlaces(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        places = dbPlaces.getPlaces();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflamos el action bar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.actionMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Podemos manejar la opcion elegida por el usuario
        switch (item.getItemId()) {

            case R.id.action_locate:
                updateLocalization();
                return true;
            case R.id.action_clear:
                deletePlaces();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMarkerClickListener(this);

        for (Place place : places) {
            mMap.addMarker(new MarkerOptions().position(place.getCoordinates()).title(place.getName()));
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                InsertPlaceDialogFragment placeDialog = new InsertPlaceDialogFragment().newInstance(
                        new InsertPlaceDialogFragment.OnOKInsertPlaceListener() {
                            @Override
                            public void operationAccept(String name, String description, LatLng latLng) {

                                Place place = new Place();
                                place.setCoordinates(latLng);
                                place.setName(name);
                                place.setDescription(description);
                                place.setAddres(getAddres(latLng));
                                places.add(place);

                                dbPlaces.addPlace(place);
                                mMap.addMarker(new MarkerOptions().position(latLng).title(name));
                            }
                        }, null, latLng);

                placeDialog.show(getSupportFragmentManager(), "MapsActivity");
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

            } else {
                startLocalization();
            }
        } else {
            startLocalization();
        }
    }

    public void startLocalization() {

        changeLocateIcon(gpsStatus.LOCATING);
        locManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        LocationListener locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                changeLocateIcon(gpsStatus.ENABLE);
                showLocation(location.getLatitude(), location.getLongitude());

            }

            //OUT_OF_SERVICE (0), TEMPORARILY_UNAVAILABLE(1) o AVAILABLE (2)
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                switch (i) {
                    case 0:
                    case 1:
                        changeLocateIcon(gpsStatus.OFFLINE);
                        break;
                    case 2:
                        changeLocateIcon(gpsStatus.ENABLE);
                        break;
                }
            }

            @Override
            public void onProviderEnabled(String s) {
                changeLocateIcon(gpsStatus.ENABLE);
            }

            @Override
            public void onProviderDisabled(String s) {
                changeLocateIcon(gpsStatus.OFFLINE);
            }
        };


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            /**
             * Providers:
             * gps: This provider determines location using satellites. Depending on conditions, this provider may take a while to return a location fix. Requires the permission android.permission.ACCESS_FINE_LOCATION.
             * network: This provider determines location based on availability of cell tower and WiFi access points. Results are retrieved by means of a network lookup. Requires either of the permissions android.permission.ACCESS_COARSE_LOCATION or android.permission.ACCESS_FINE_LOCATION.
             * A special location provider for receiving locations without actually initiating a location fix. This provider can be used to passively receive location updates when other applications or services request them without actually requesting the locations yourself. This provider will return locations generated by other providers. Requires the permission android.permission.ACCESS_FINE_LOCATION
             */
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, locListener);
        }

    }

    public void updateLocalization() {

        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setAltitudeRequired(true);
        c.setSpeedRequired(true);
        c.setPowerRequirement(Criteria.POWER_HIGH);

        LocationProvider highAccuracy = locManager.getProvider(locManager.getBestProvider(c, true));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc != null) {
                showLocation(loc.getLatitude(), loc.getLongitude());
                changeLocateIcon(gpsStatus.ENABLE);
            }
        }



    }

    public void showLocation(double lat, double lon) {
        LatLng coordinate = new LatLng(lat,lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate,19.0f));
    }

    public void changeLocateIcon(gpsStatus status) {

        if (actionMenu != null) {
            MenuItem locateItem = actionMenu.findItem(R.id.action_locate);

            switch (status) {

                case ENABLE:
                    locateItem.setIcon(R.drawable.ic_gps_fixed_white_24dp);
                    locateItem.setEnabled(true);
                    break;

                case OFFLINE:
                    locateItem.setIcon(R.drawable.ic_gps_off_white_24dp);
                    locateItem.setEnabled(false);
                    break;

                case LOCATING:
                    locateItem.setIcon(R.drawable.ic_location_searching_white_24dp);
                    locateItem.setEnabled(false);
                    break;
            }
        }
    }

    public String getAddres(LatLng latLng) {

        Geocoder geoCoder = new Geocoder(getApplicationContext(),Locale.getDefault());
        List<Address> matches = null;

        try {
            matches = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

        } catch (IOException e) {
            return "Not found";
        }

        if (matches.isEmpty()) {
            return "Not found";
        }

        Address bestMatch = matches.get(0);
        return bestMatch.getAddressLine(0);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Place place = findPlaceByLocation(marker.getPosition());
        if (place != null) {
            Intent intent = new Intent(this,PlaceDeailActivity.class);
            intent.putExtra("place",place);
            startActivity(intent);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocalization();
            }
        }
    }


    public void deletePlaces() {
        mMap.clear();
        places.clear();
        dbPlaces.delete();
    }


    public Place findPlaceByLocation(LatLng latLng) {
        for (Place place : places) {
            if (place.getCoordinates().equals(latLng)) {
                return place;
            }
        }

        return null; //TODO: throw exception
    }
}
