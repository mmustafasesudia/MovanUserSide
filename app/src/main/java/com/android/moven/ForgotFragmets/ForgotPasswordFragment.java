package com.android.moven.ForgotFragmets;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.moven.ConfigURL;
import com.android.moven.Fragments.ProgressDialogClass;
import com.android.moven.NetworkConnectivityClass;
import com.android.moven.R;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {

    Button btn_forgot_password_next;
    EditText et_input_phone;
    String phone;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.forgot_password_fragment, container, false);

        // ((AppCompatActivity) getActivity()).getSupportActionBar().show();


        et_input_phone = rootView.findViewById(R.id.et_input_phone);
        btn_forgot_password_next = rootView.findViewById(R.id.btn_forgot_password_next);
        btn_forgot_password_next.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_forgot_password_next:
                submit();
                break;
        }
    }

    public void submit() {
        if (!validatePhone()) {
            return;
        }
        if (NetworkConnectivityClass.isNetworkAvailable(getActivity())) {
            //TODO send data from here
            sendData();

           /* Fragment fragmentName = null;
            Fragment ForgotPasswordPinFragment = new ForgotPasswordPinFragment();
            fragmentName = ForgotPasswordPinFragment;

            Bundle args = new Bundle();
            args.putString("phone", phone);
            fragmentName.setArguments(args);
            replaceFragment(fragmentName);*/

        } else {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Internet Not Connected",
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean validatePhone() {
        if (!et_input_phone.getText().toString().matches("[0][3][0-9]{9}|[3][0-9]{9}")) {
            et_input_phone.setError("Invalid Phone Number");
            requestFocus(et_input_phone);
            return false;
        } else if (et_input_phone.getText().toString().length() < 10) {
            et_input_phone.setError("Invalid Length");
            requestFocus(et_input_phone);
            return false;
        } else if (et_input_phone.getText().toString().trim().isEmpty()) {
            et_input_phone.setError("Phone Number Cannot Be Empty");
            requestFocus(et_input_phone);
            return false;
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void sendData() {
        ProgressDialogClass.showRoundProgress(getActivity(), "Please Wait");
        phone = et_input_phone.getText().toString();

        if (phone.length() == 10) {
            phone = "+92" + phone;
        } else {
            phone = "+92" + phone.substring(1);
        }

        AndroidNetworking.post(ConfigURL.URL_IS_PERSON_EXIST)
                .addBodyParameter("pMobile", phone)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ProgressDialogClass.dismissRoundProgress();
                        try {
                            String msg = response.getString("message");
                            boolean error = false;

                            error = response.getBoolean("error");

                            if (error) {

                                /*Fragment fragmentName = null;
                                Fragment ForgotPasswordPinFragment = new ForgotPasswordPinFragment();
                                fragmentName = ForgotPasswordPinFragment;

                                Bundle args = new Bundle();
                                args.putString("phone", phone);
                                fragmentName.setArguments(args);
                                replaceFragment(fragmentName);*/

                                Fragment fragmentName = null;
                                Fragment ForgotInputNewPasswordFragment = new ForgotInputNewPasswordFragment();
                                fragmentName = ForgotInputNewPasswordFragment;
                                Bundle args = new Bundle();
                                args.putString("phone", phone);
                                fragmentName.setArguments(args);
                                replaceFragment(fragmentName);


                            }
                            if (!error) {
                                Toast.makeText(getActivity(), "No Account Associated With This Number", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        ProgressDialogClass.dismissRoundProgress();
                        Toast.makeText(getActivity(), "" + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;

        FragmentManager manager = getActivity().getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.content_frame_dashboard, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }
}
