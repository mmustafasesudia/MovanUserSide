package com.android.moven;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class StartrideFragment extends Fragment {

    Handler handler = new Handler();
    int delay = 5000; //milliseconds
    Runnable runnable;

    Button btn_finish;

    Marker mCurrLocationMarker;
    GoogleMap mGoogleMap;
    String van;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //do other stuff here
            String message = intent.getStringExtra("message");
            String type = intent.getStringExtra("type");
            if (type.equals("PICKEDUP")) {
                handler.removeCallbacks(runnable);

                Fragment fragmentName = null;
                Fragment OnTheWay = new OnTheWay();
                fragmentName = OnTheWay;
                replaceFragment(fragmentName);

            } else if (type.equals("FINISHED")) {

            }

        }
    };

    public StartrideFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_startride, container, false);
        btn_finish = view.findViewById(R.id.btn_finish);
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(runnable);
            }
        });
        Bundle bundle = getArguments();
        if (bundle != null) {
            van = (String) bundle.get("van");
            //Toast.makeText(getActivity(), "" + van, Toast.LENGTH_SHORT).show();
            callURL(van);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mGoogleMap.setMyLocationEnabled(true);
                MapsInitializer.initialize(getContext());
            }
        });

    }

    public void callURL(final String van_number) {

        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                getLastDriverLocation(van_number);
                handler.postDelayed(runnable, delay);
            }
        }, delay);

    }

    public void getLastDriverLocation(final String van_number) {
        AndroidNetworking.get(ConfigURL.URL_DRIVER_LAST_LOC)
                .addQueryParameter("van_num", van_number)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {


                            JSONArray jsonArray = response.getJSONArray("Vans");
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String driverName = jsonObject.getString("DriverName");
                            String driverLat = jsonObject.getString("Latitude");
                            String driverLng = jsonObject.getString("Longitude");
                            setDriverMarker(Double.parseDouble(driverLat), Double.parseDouble(driverLng), driverName, van_number);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    public void setDriverMarker(Double mLat, Double mLng, String driverName, String van_num) {
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        LatLng latLng = new LatLng(mLat, mLng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Drive Name : " + driverName);
        markerOptions.snippet("Van Number : " + van_num);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
        mCurrLocationMarker.showInfoWindow();
        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        //Toast.makeText(getActivity(), " ", Toast.LENGTH_SHORT).show();

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

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mMessageReceiver, new IntentFilter(ConfigURL.PUSH_NOTIFICATION));

    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mMessageReceiver);

    }

}
