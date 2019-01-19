package com.android.moven.Fragments;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.moven.NetworkConnectivityClass;
import com.android.moven.R;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    EditText et_user_name_reg, et_email_id_reg, et_user_phone_no_reg, et_user_password_reg, et_user_confirm_password_reg;
    String name, password, confirm_password, email, phone, etype;
    Button bt_user_next_reg, bt_driver_next_reg;
    LinearLayout l_user, l_driver;

    EditText et_driver_name_reg, et_driver_phone_no_reg, et_driver_password_reg, et_driver_confirm_password_reg;
    String compare_pass;


    public RegisterFragment() {
        // Required empty public constructor
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        l_user = rootView.findViewById(R.id.layout_user_register);
        l_driver = rootView.findViewById(R.id.layout_worker_register);

        Bundle b = getArguments();
        String s = null;
        if (b != null) {
            s = b.getString("message");
        }
        if (s.equals("user")) {
            etype = "CUSTOMER";
            l_user.setVisibility(View.VISIBLE);
            user_register_click_listener(rootView);
            bt_user_next_reg.setOnClickListener(this);

        } else if (s.equals("driver")) {
            etype = "DRIVER";
            l_driver.setVisibility(View.VISIBLE);
            driver_register_click_listener(rootView);
            bt_driver_next_reg.setOnClickListener(this);
            Log.d("aaaaaaaaaaa", "aya");

        }


        return rootView;
    }

    public void user_register_click_listener(View rootView) {
        et_user_name_reg = rootView.findViewById(R.id.et_user_name_reg);
        et_user_phone_no_reg = rootView.findViewById(R.id.et_user_phone_no_reg);
        et_user_password_reg = rootView.findViewById(R.id.et_user_password_reg);
        et_user_confirm_password_reg = rootView.findViewById(R.id.et_user_confirm_password_reg);
        bt_user_next_reg = rootView.findViewById(R.id.bt_user_next_reg);
    }

    public void driver_register_click_listener(View rootView) {
        et_driver_name_reg = rootView.findViewById(R.id.et_driver_name_reg);
        et_driver_phone_no_reg = rootView.findViewById(R.id.et_driver_phone_no_reg);
        et_driver_password_reg = rootView.findViewById(R.id.et_driver_password_reg);
        et_driver_confirm_password_reg = rootView.findViewById(R.id.et_driver_confirm_password_reg);
        bt_driver_next_reg = rootView.findViewById(R.id.bt_driver_next_reg);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_user_next_reg:
                submitUserForm();
                break;

            case R.id.bt_driver_next_reg:
                Log.d("aaaaaaaaaaa", "aya");
                submitDriverForm();
                break;
        }
    }

    /**
     * Validating form
     */
    private void submitUserForm() {
        if (!validateFirstName(et_user_name_reg)) {
            return;
        }

        if (!validatePhone(et_user_phone_no_reg)) {
            return;
        }
        if (!validatePassword(et_user_password_reg)) {
            return;
        }
        if (!validateConfirmPassword(et_user_password_reg, et_user_confirm_password_reg)) {
            return;
        }


        //Sending Values to next form
        name = et_user_name_reg.getText().toString();
        password = et_user_password_reg.getText().toString();
        confirm_password = et_user_confirm_password_reg.getText().toString();
        phone = et_user_phone_no_reg.getText().toString();
        if (phone.length() == 10) {
            phone = "+92" + phone;
        } else {
            phone = "+92" + phone.substring(1);
        }
        //for keyboard hide
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (NetworkConnectivityClass.isNetworkAvailable(getActivity())) {
            senddata(et_user_phone_no_reg);
        } else {

            Snackbar.make(getActivity().findViewById(android.R.id.content), "Internet not connected", Snackbar.LENGTH_SHORT).show();

        }
    }

    private void submitDriverForm() {
        if (!validateFirstName(et_driver_name_reg)) {
            return;
        }

        if (!validatePhone(et_driver_phone_no_reg)) {
            return;
        }
        if (!validatePassword(et_driver_password_reg)) {
            return;
        }
        if (!validateConfirmPassword(et_driver_password_reg, et_driver_confirm_password_reg)) {
            return;
        }


        //Sending Values to next form
        name = et_driver_name_reg.getText().toString();
        password = et_driver_password_reg.getText().toString();
        confirm_password = et_driver_confirm_password_reg.getText().toString();
        phone = et_driver_phone_no_reg.getText().toString();
        if (phone.length() == 10) {
            phone = "+92" + phone;
        } else {
            phone = "+92" + phone.substring(1);
        }
        //for keyboard hide
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (NetworkConnectivityClass.isNetworkAvailable(getActivity())) {
            senddata(et_driver_phone_no_reg);
        } else {

            Snackbar.make(getActivity().findViewById(android.R.id.content), "Internet not connected", Snackbar.LENGTH_SHORT).show();

        }
    }

    public void senddata(final EditText et_isregistered) {
        //***********post user data********

        ProgressDialogClass.showRoundProgress(getActivity(), "Please wait...");

        /*AndroidNetworking.post(ConfigURL.URL_IS_REGISTERED)
                .addBodyParameter("uMobile", phone)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ProgressDialogClass.dismissRoundProgress();
                        Toast.makeText(getActivity(), "" + response, Toast.LENGTH_LONG).show();

                        try {
                            if (response.getBoolean("error")) {

                                Fragment fragmentName = null;
                                Fragment signupfrag = new SignUpVerification();
                                fragmentName = signupfrag;

                                Bundle args = new Bundle();
                                args.putString("full_name", name);
                                args.putString("phone", phone);
                                args.putString("pass", password);
                                args.putString("type", etype);
                                fragmentName.setArguments(args);
                                replaceFragment(fragmentName);

                            } else if (!response.getBoolean("error")) {
                                ProgressDialogClass.dismissRoundProgress();
                                et_isregistered.setError("Your number already registered");
                            }
                        } catch (JSONException e) {
                            ProgressDialogClass.dismissRoundProgress();
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError error) {
                        ProgressDialogClass.dismissRoundProgress();
                        Toast.makeText(getActivity(), "" + error, Toast.LENGTH_LONG).show();
                    }
                });*/
        ////

        AndroidNetworking.post("https://itehadmotors.com/Dndchatter/v1/ispersonregistered")
                .addBodyParameter("uMobile", phone)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ProgressDialogClass.dismissRoundProgress();
                        Toast.makeText(getActivity(), "" + response, Toast.LENGTH_LONG).show();

                        try {
                            if (response.getBoolean("error")) {
                                //Intent apply data transfer
                                Fragment fragmentName = null;
                                Fragment signupfrag = new SignUpVerification();
                                fragmentName = signupfrag;

                                Bundle args = new Bundle();
                                args.putString("full_name", name);
                                args.putString("phone", phone);
                                args.putString("pass", password);
                                args.putString("type", etype);
                                fragmentName.setArguments(args);
                                replaceFragment(fragmentName);

                            } else if (!response.getBoolean("error")) {
                                et_isregistered.setError("Your number already registered");
                            }
                        } catch (JSONException e) {
                            ProgressDialogClass.dismissRoundProgress();
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError error) {
                        ProgressDialogClass.dismissRoundProgress();
                        Toast.makeText(getActivity(), "" + error, Toast.LENGTH_LONG).show();
                    }
                });

        /////
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateFirstName(EditText et_name) {
        if (et_name.getText().toString().trim().isEmpty()) {

            et_name.setError(getString(R.string.err_msg_firstName));

            return false;
        }

        return true;
    }

    private boolean validatePassword(EditText et_password) {
        if (et_password.getText().toString().trim().isEmpty()) {

            et_password.setError("Please enter password");

            return false;
        }

        if (et_password.getText().toString().length() <= 7) {
            et_password.setError("Please enter atleast 8 alphabets");

            return false;
        }
        return true;
    }

    private boolean validateConfirmPassword(EditText et_pass, EditText et_confirm_pass) {
        if (et_confirm_pass.getText().toString().trim().isEmpty()) {

            et_confirm_pass.setError("Please confirm password");
            requestFocus(et_confirm_pass);
            return false;
        } else if (et_confirm_pass.getText().toString().length() <= 7) {
            et_confirm_pass.setError("Please enter atleast 8 alphabets");

            return false;
        } else if (!et_pass.getText().toString().equals(et_confirm_pass.getText().toString())) {
            et_confirm_pass.setError("Password are not match");
            requestFocus(et_confirm_pass);
            return false;
        }

        return true;
    }

    private boolean validateEmail() {
        String email = et_email_id_reg.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            et_email_id_reg.setError(getString(R.string.err_msg_email));
            return false;
        }

        return true;
    }

    private boolean validatePhone(EditText et_phone_no) {

        if (et_phone_no.getText().toString().trim().isEmpty()) {
            et_phone_no.setError(getString(R.string.err_msg_phone));

            return false;
        } else if (!et_phone_no.getText().toString().matches("[0][3][0-9]{9}|[3][0-9]{9}")) {
            et_phone_no.setError(getString(R.string.err_regex_msg_phone));

            return false;
        } else if (et_phone_no.getText().toString().length() < 10) {
            et_phone_no.setError(getString(R.string.err_regex_msg_phone));

            return false;
        }

        return true;
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
