package com.android.moven.CompletedJob;

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

import com.android.moven.ConfigURL;
import com.android.moven.Fragments.ProgressDialogClass;
import com.android.moven.ListOfVans.AdapterListOfVans;
import com.android.moven.R;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListOfCompletedRides extends Fragment implements AdapterListOfVans.ItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    ArrayList<CompletedRides> data;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView rv;
    String plat, plng, dLat, dLng;

    public ListOfCompletedRides() {
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
        View rootView = inflater.inflate(R.layout.fragment_list_of_completed_rides, container, false);
        rv = rootView.findViewById(R.id.rv_list_of_vans);
        rv.setHasFixedSize(true);

        /*Bundle bundle = getArguments();
        if (bundle != null) {
            plat = (String) bundle.get("pLat");
            plng = (String) bundle.get("pLng");
            dLat = (String) bundle.get("dLat");
            dLng = (String) bundle.get("dLng");
*/
        loadData();


        mSwipeRefreshLayout = rootView.findViewById(R.id.contentView);
        mSwipeRefreshLayout.setOnRefreshListener(this);


        //loadData();

        return rootView;
    }

    public void loadData() {
        ProgressDialogClass.showRoundProgress(getActivity(), "Please Wait..");
        AndroidNetworking.get(ConfigURL.URL_LIST_OF_COMPLETED_RIDES)
                .addQueryParameter("customer_num", ConfigURL.getMobileNumber(getActivity()))
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

                                CompletedRides moven = new CompletedRides(jsonArray.getJSONObject(i));
                                driver_name = moven.getDriver_name();
                                driver_mobile = moven.getDriver_mobile();
                                van_number = moven.getVan_number();
                                dest_dist = moven.getDist_from_destination();
                                current_dist = moven.getDist_from_current();
                                fare = moven.getFixed_fare();
                                journey_id = moven.getJourney_id();

                                CompletedRides obj = new CompletedRides(driver_name, van_number, dest_dist, driver_mobile, current_dist, fare, journey_id);
                                data.add(obj);

                            }

                            AdapterListOfCompletedRides adapter = new AdapterListOfCompletedRides(getActivity(), data);
                            rv.setAdapter(adapter);
                            //adapter.setClickListener(ListOfCompletedRides.this);


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
        loadData();
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