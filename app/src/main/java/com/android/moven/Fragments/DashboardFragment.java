package com.android.moven.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.moven.ChooseLocation;
import com.android.moven.ConfigURL;
import com.android.moven.ListOfVans.ListOfVans;
import com.android.moven.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class DashboardFragment extends Fragment {


    private static final String FINE_LOCATION =
            Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION =
            Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    TextView tv_your_location, choose_location;
    //MapView mMapView;
    View rootView;
    GoogleMap mGoogleMap;
    Marker mCurrLocationMarker, mDestLocationMarker;
    Button bt_startride;
    private Boolean mLocationPermissionsGranted = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        bt_startride = rootView.findViewById(R.id.bt_startride);

        tv_your_location = rootView.findViewById(R.id.your_location);
        tv_your_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), ChooseLocation.class);
                i.putExtra("key", "START");
                startActivity(i);
                getActivity().finish();

            }
        });
        choose_location = rootView.findViewById(R.id.choose_location);
        choose_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ChooseLocation.class);
                i.putExtra("key", "DROP");
                startActivity(i);
                getActivity().finish();
            }
        });

        bt_startride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragmentName = null;
                Fragment ListOfVans = new ListOfVans();
                fragmentName = ListOfVans;
                replaceFragment(fragmentName);
            }
        });

        if (ConfigURL.DROP_OF_LAT.isEmpty() && ConfigURL.DROP_OF_LNG.isEmpty() && ConfigURL.PICK_UP_LAT.isEmpty() && ConfigURL.PICK_UP_LNG.isEmpty()) {
            if (mCurrLocationMarker != null && mDestLocationMarker != null) {
                mCurrLocationMarker.remove();
                mDestLocationMarker.remove();
            }
        }

        return rootView;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;


                mSetUpMap();

            }
        });

    }

    public void setOriginMarkerOfCustomer(Double mLat, Double mLng) {
        /*mLat = 24.902577553876107;
        mLng = 67.05668918788434;*/
        Log.v("Hello    " + mLat, "" + mLng);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.add_marker);

        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        LatLng latLng = new LatLng(mLat, mLng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Origin");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
        mCurrLocationMarker.showInfoWindow();

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

    }

    public void setDestMarkerOfCustomer(Double mLat, Double mLng) {
        /*mLat = 24.902577553876107;
        mLng = 67.05668918788434;*/
        Log.v("Hello    " + mLat, "" + mLng);
        // BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.add_marker);

        if (mDestLocationMarker != null) {
            mDestLocationMarker.remove();
        }
        LatLng latLng = new LatLng(mLat, mLng);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Destination");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mDestLocationMarker = mGoogleMap.addMarker(markerOptions);
        mDestLocationMarker.showInfoWindow();

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

    }

    private void mSetUpMap() {
        // your method code
        MapsInitializer.initialize(getContext());

        if (!ConfigURL.PICK_UP_LAT.equals("") || !ConfigURL.PICK_UP_LNG.equals("")) {
            //Toast.makeText(getActivity(), "" + ConfigURL.PICK_UP_LAT, Toast.LENGTH_SHORT).show();
            setOriginMarkerOfCustomer(Double.parseDouble(ConfigURL.PICK_UP_LAT), Double.parseDouble(ConfigURL.PICK_UP_LNG));
        }
        if (!ConfigURL.DROP_OF_LAT.equals("") || !ConfigURL.DROP_OF_LNG.equals("")) {
            //Toast.makeText(getActivity(), "" + ConfigURL.PICK_UP_LAT, Toast.LENGTH_SHORT).show();
            setDestMarkerOfCustomer(Double.parseDouble(ConfigURL.DROP_OF_LAT), Double.parseDouble(ConfigURL.DROP_OF_LNG));

            if (!ConfigURL.PICK_UP_LAT.equals("") || !ConfigURL.PICK_UP_LNG.equals("")) {

                bt_startride.setVisibility(View.VISIBLE);

            }
        }
    }

    private void getLocationPermission() {
        Log.d("", "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getActivity(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull
            String[] permissions, @NonNull int[] grantResults) {
        Log.d("", "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {

            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
                    // initMap();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }

    }

    private void initMap() {

        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;


                mSetUpMap();
            }
        });

    }

    private void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;


        FragmentManager manager = getActivity().getSupportFragmentManager();
        boolean fragmentPopped = false;
        try {

            fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        } catch (IllegalStateException ignored) {
            // There's no way to avoid getting this if saveInstanceState has already been called.
        }
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(backStateName);
            ft.commitAllowingStateLoss();
        }

    }

}
