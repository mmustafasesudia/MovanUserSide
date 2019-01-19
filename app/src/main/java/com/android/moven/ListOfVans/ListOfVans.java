package com.android.moven.ListOfVans;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.moven.ConfigURL;
import com.android.moven.Fragments.ProgressDialogClass;
import com.android.moven.R;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListOfVans extends Fragment implements AdapterListOfVans.ItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    ArrayList<Moven> data;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView rv;
    String plat, plng, dLat, dLng;

    public ListOfVans() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list_of_vans, container, false);
        rv = rootView.findViewById(R.id.rv_list_of_vans);
        rv.setHasFixedSize(true);


        plat = ConfigURL.PICK_UP_LAT;
        plng = ConfigURL.PICK_UP_LNG;
        dLat = ConfigURL.DROP_OF_LAT;
        dLng = ConfigURL.DROP_OF_LNG;
        loadData(plat, plng, dLat, dLng);


        mSwipeRefreshLayout = rootView.findViewById(R.id.contentView);
        mSwipeRefreshLayout.setOnRefreshListener(this);


        //loadData();

        return rootView;
    }

    public void loadData(String pLat, String pLng, String dLat, String dLng) {
        ProgressDialogClass.showRoundProgress(getActivity(), "Please Wait..");
        AndroidNetworking.get(ConfigURL.URL_LIST_OF_DRIVERS)
                .addQueryParameter("latitude", pLat)
                .addQueryParameter("longitude", pLng)
                .addQueryParameter("drop_lat", dLat)
                .addQueryParameter("drop_long", dLng)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        refreshItems();
                        ProgressDialogClass.dismissRoundProgress();
                        data = new ArrayList<>();
                        Log.d("AA Response", "" + response);

                        try {
                            JSONArray jsonArray = response.getJSONArray("Vans");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                String driver_name, driver_mobile, van_number, dest_dist, current_dist, fare, journey_id;

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                driver_name = jsonObject.getString("DriverName");
                                driver_mobile = jsonObject.getString("DriverMobileNo");
                                van_number = jsonObject.getString("Van_Number");
                                dest_dist = jsonObject.getString("disFare");
                                current_dist = jsonObject.getString("DistanceFromSrouce");
                                fare = jsonObject.getString("Fare");
                                journey_id = jsonObject.getString("Journy_id");

                                Moven obj = new Moven(driver_name, van_number, dest_dist, driver_mobile, current_dist, fare, journey_id);
                                data.add(obj);

                            }
                            if (!(data.toArray().length > 0)) {
                                Toast.makeText(getActivity(), "No Van Available \n Try Again Later", Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
                            }
                            AdapterListOfVans adapter = new AdapterListOfVans(getActivity(), data);
                            rv.setAdapter(adapter);
                            adapter.setClickListener(ListOfVans.this);


                        } catch (JSONException e) {
                            refreshItems();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        refreshItems();
                        ProgressDialogClass.dismissRoundProgress();

                    }
                });

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
    }

    @Override
    public void onRefresh() {
        loadData(plat, plng, dLat, dLng);
    }

    @Override
    public void onClick(View view, int position) {

    }

    public void refreshItems() {
        onItemsLoadComplete();
    }

    public void onItemsLoadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;

        FragmentManager manager = getActivity().getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}