package com.example.okmac.greymap.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.okmac.greymap.Database.GeoTagDatabase;
import com.example.okmac.greymap.R;
import com.example.okmac.greymap.Utils.AppFileManager;
import com.example.okmac.greymap.activities.MainActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, View.OnClickListener, LocationListener {
    private static final int VIBRATOR_REQUEST_CODE = 102;
    private static final int LOCATION_REQUEST_CODE = 103;


    private PlaceAutocompleteFragment autocompleteFragment;
    private LatLng latLng;
    private String address = "temp";

    private GeoTagDatabase geoTagDatabase;
    private ArrayList<GeoTag> geoTags;
    private GoogleMap googleMap;
    private MainActivity mainActivity;
    private AppFileManager appFileManager;
    private LocationManager locationManager;
    private Location currentLocation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        geoTagDatabase = GeoTagDatabase.getDatabase(getContext());
        mainActivity = (MainActivity) getActivity();
        appFileManager = new AppFileManager(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.fab_current_location).setOnClickListener(this);
        view.findViewById(R.id.btn_add_location).setOnClickListener(this);
        //view.findViewById(R.id.btn_search_location).setOnClickListener(this);
        SupportMapFragment supportMapFragment = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map));
        supportMapFragment.getMapAsync(this);


        autocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                latLng = place.getLatLng();
                address = getAddress(latLng);
                googleMap.addMarker(new MarkerOptions().position(latLng));
                moveCameraToTag(place.getLatLng());
            }

            @Override
            public void onError(Status status) {
                Log.e("##error", "" + status.getStatusMessage());
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        this.latLng = latLng;
        if (checkSelfPermission(getContext(), Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.VIBRATE}, VIBRATOR_REQUEST_CODE);
        } else {
            vibrate();
        }
        address = getAddress(latLng);
        googleMap.addMarker(new MarkerOptions().position(latLng));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPermissionAccepted;
        switch (requestCode) {

            case VIBRATOR_REQUEST_CODE:

                isPermissionAccepted = true;
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        isPermissionAccepted = false;
                        break;
                    }
                }
                if (isPermissionAccepted) {
                    vibrate();
                }
            case LOCATION_REQUEST_CODE:

                isPermissionAccepted = true;
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        isPermissionAccepted = false;
                        break;
                    }
                }
                if (isPermissionAccepted) {
                    getCurrentLocation();
                }
                break;

        }
    }


    private void vibrate() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(70);
            }
        }
    }

    /*private void getTags() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ArrayList<GeoTag>> future = executor.submit(new GetTagsTask(geoTagDatabase));
        try {
            geoTags = future.get();
            ArrayList<LatLng> latLngs = new ArrayList<>();
            if (geoTags != null) {
                for (GeoTag geoTag : geoTags) {
                    LatLng latLng = new LatLng(geoTag.getLatitude(), geoTag.getLongitude());
                    latLngs.add(latLng);
                    googleMap.addMarker(new MarkerOptions().position(latLng));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }*/

    public String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addressList;
        String addressString = "";
        StringBuilder deviceAddress = new StringBuilder();
        try {
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                if (address != null) {
                    deviceAddress = new StringBuilder();
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        deviceAddress.append(address.getAddressLine(i)).append(",");
                    }
                }
            }

            addressString = deviceAddress.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressString;
    }

    private void showSnackBar(int message) {
        Snackbar.make(getActivity().findViewById(android.R.id.content),
                message, Snackbar.LENGTH_LONG).show();
    }

    private void getCurrentLocation() {

        if (checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                onLocationChanged(location);
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_current_location:
                getCurrentLocation();
                break;
            case R.id.btn_add_location:
                if (latLng == null) {
                    showSnackBar(R.string.no_location_selected);
                    return;
                }
                GeoTag geoTag = new GeoTag();
                geoTag.setAddress(this.address);
                geoTag.setLatitude(this.latLng.latitude);
                geoTag.setLongitude(this.latLng.longitude);
                geoTagDatabase.insertTag(geoTag);
                mainActivity.notifyDbUpdated();
                googleMap.clear();
                autocompleteFragment.setText("");
                Toast.makeText(getContext(), R.string.location_added, Toast.LENGTH_LONG).show();
                break;
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d("##LATTITUDE", location.getLatitude() + "");
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        this.latLng = latLng;
        address = getAddress(latLng);
        googleMap.addMarker(new MarkerOptions().position(latLng));
        moveCameraToTag(latLng);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void moveCameraToTag(LatLng latLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
    }

}
