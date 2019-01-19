package com.android.moven.ListOfVans;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.moven.ConfigURL;
import com.android.moven.Fragments.ProgressDialogClass;
import com.android.moven.R;
import com.android.moven.StartrideFragment;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AdapterListOfVans extends RecyclerView.Adapter<AdapterListOfVans.MyViewHolder> {

    private Context acontext;
    private ArrayList<Moven> arrayList;
    private ItemClickListener clickListener;


    public AdapterListOfVans(Context context, ArrayList<Moven> arrayList) {
        this.arrayList = arrayList;
        acontext = context;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Moven current = arrayList.get(position);
        holder.tv_driver_name.setText("Driver Name : " + current.getDriver_name());
        holder.tv_van_number.setText("Van Number Plate : " + current.getVan_number());
        holder.tv_distance_from_dest.setText("Distance  : " + current.getDist_from_destination());
        holder.tv_driver_mobile_number.setText("Contact : " + current.getDriver_mobile());
        holder.tv_distance_from_cureent.setText("Driver From Your Location : " + current.getDist_from_current());
        holder.tv_fixed_fare.setText("Fixed Fare : " + current.getFixed_fare());

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list_of_vans, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    public void replaceFragment(Fragment fragment, int frame) {
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;

        FragmentManager manager = ((FragmentActivity) acontext).getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(frame, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void createRequest(Double pickLat, Double pickLng, Double dropLat, Double dropLng, final String journy_id, final String van_number, String fare) {
        ProgressDialogClass.showRoundProgress(acontext, "Sending Request..");
        AndroidNetworking.post(ConfigURL.URL_JOURNEY_REQUEST)
                .addBodyParameter("student_num", ConfigURL.getMobileNumber(acontext))
                .addBodyParameter("journy_id", journy_id)
                .addBodyParameter("pick_lat", String.valueOf(pickLat))
                .addBodyParameter("pick_long", String.valueOf(pickLng))
                .addBodyParameter("drop_lat", String.valueOf(dropLat))
                .addBodyParameter("drop_long", String.valueOf(dropLng))
                .addBodyParameter("fare", fare)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ProgressDialogClass.dismissRoundProgress();
                        try {
                            if (!response.getBoolean("error")) {

                                ConfigURL.PICK_UP_LAT = "";
                                ConfigURL.PICK_UP_LNG = "";
                                ConfigURL.DROP_OF_LAT = "";
                                ConfigURL.DROP_OF_LNG = "";

                                Toast.makeText(acontext, "Request Sent..!", Toast.LENGTH_LONG).show();

                                Fragment fragmentName = null;
                                Fragment SignInFragment = new StartrideFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("van", "" + van_number);
                                fragmentName = SignInFragment;
                                fragmentName.setArguments(bundle);
                                replaceFragment(fragmentName, R.id.fragment_container);

                            } else if (response.getBoolean("error")) {
                                Toast.makeText(acontext, "Request Failed", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        ProgressDialogClass.dismissRoundProgress();
                        Log.v("Sign In", "" + anError);
                        Toast.makeText(acontext, "" + anError, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CardView mCardView;
        public TextView tv_driver_name, tv_van_number, tv_distance_from_dest, tv_driver_mobile_number, tv_distance_from_cureent, tv_fixed_fare;
        public Button btn_book_van;

        public MyViewHolder(View v) {
            super(v);

            mCardView = v.findViewById(R.id.card_view_tuition_completed);
            tv_driver_name = v.findViewById(R.id.tv_driver_name);
            tv_van_number = v.findViewById(R.id.tv_van_number);
            tv_distance_from_dest = v.findViewById(R.id.tv_distance_from_dest);
            tv_driver_mobile_number = v.findViewById(R.id.tv_driver_mobile_number);
            tv_distance_from_cureent = v.findViewById(R.id.tv_distance_from_cureent);
            tv_fixed_fare = v.findViewById(R.id.tv_fixed_fare);

            btn_book_van = v.findViewById(R.id.btn_book_van);
            btn_book_van.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Moven moven = arrayList.get(getAdapterPosition());
                    Double pickLat = Double.valueOf(ConfigURL.PICK_UP_LAT);
                    Double pickLng = Double.valueOf(ConfigURL.PICK_UP_LNG);
                    Double dropLat = Double.valueOf(ConfigURL.DROP_OF_LAT);
                    Double dropLng = Double.valueOf(ConfigURL.DROP_OF_LNG);
                    createRequest(pickLat, pickLng, dropLat, dropLng, moven.getJourney_id(), moven.getVan_number(), moven.getFixed_fare());
                }
            });

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }

}
