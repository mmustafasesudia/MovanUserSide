package com.android.moven.Fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.moven.ConfigURL;
import com.android.moven.Drawer;
import com.android.moven.ForgotFragmets.ForgotPasswordFragment;
import com.android.moven.NetworkConnectivityClass;
import com.android.moven.R;
import com.android.moven.SignUpFragment;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment implements View.OnClickListener {

    Dialog dialog;
    TextView tv_new_account, tv_forgot_pass;
    EditText et_input_phone, et_input_password;
    String phone, pass;
    Button btn_signin;
    String LOGIN = "active";
    Bundle bundle;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sign_in, container, false);

        /*if (ConfigURL.getEmail(getActivity()).length() > 0) {
            Intent intent = new Intent(getActivity(), Drawer.class);
            startActivity(intent);
            getActivity().finish();
        }*/
        //CustomDialog();

        et_input_phone = rootView.findViewById(R.id.et_input_phone);
        et_input_password = rootView.findViewById(R.id.et_input_password);

        tv_new_account = rootView.findViewById(R.id.tv_new_account);
        tv_forgot_pass = rootView.findViewById(R.id.tv_forgot_pass);
        btn_signin = rootView.findViewById(R.id.btn_signin);

        tv_new_account.setOnClickListener(this);
        tv_forgot_pass.setOnClickListener(this);
        btn_signin.setOnClickListener(this);

        return rootView;
    }

    /* public void CustomDialog() {
         final android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
         View mView = getLayoutInflater().inflate(R.layout.custom_dialogbox, null);
         mBuilder.setCancelable(false);


         Button btn_select_user = (Button) mView.findViewById(R.id.dialogButton_user);
         Button btn_select_driver = (Button) mView.findViewById(R.id.dialogButton_driver);

         final Fragment RegisterFragment = new RegisterFragment();

         mBuilder.setView(mView);

         dialog = mBuilder.create();
         dialog.show();
         btn_select_user.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 dialog.dismiss();
                 bundle = new Bundle();
                 String myMessage = "user";
                 bundle.putString("message", myMessage);
                 RegisterFragment.setArguments(bundle);
                 replaceFragment(RegisterFragment);
             }
         });

         btn_select_driver.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 dialog.dismiss();
                 bundle = new Bundle();
                 String myMessage = "driver";
                 bundle.putString("message", myMessage);
                 RegisterFragment.setArguments(bundle);
                 replaceFragment(RegisterFragment);
             }
         });


         //aa
     }
 */
    @Override
    public void onClick(View view) {

        Fragment fragmentName = null;
        //Fragment ForgotPasswordFragment = new ForgotPasswordFragment();


        switch (view.getId()) {
            case R.id.btn_signin:
                submit();
                break;
            case R.id.tv_forgot_pass:
                Fragment ForgotPasswordFragment = new ForgotPasswordFragment();
                replaceFragment(ForgotPasswordFragment);
                break;
            case R.id.tv_new_account:
                //CustomDialog();
                //Fragment ForgotPasswordFragment = new ForgotPasswordFragment();
                Fragment SignUpInfoFragment = new SignUpFragment();
                replaceFragment(SignUpInfoFragment);
                break;
        }
    }

    public void submit() {
        if (!validatePhone()) {
            return;
        }
        if (et_input_password.getText().toString().isEmpty()) {
            et_input_password.setError("Password Cannot Be Empty");
            requestFocus(et_input_password);
            return;
        }

        if (NetworkConnectivityClass.isNetworkAvailable(getActivity())) {
            sendData();
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

        ProgressDialogClass.showRoundProgress(getActivity(), "Please wait...");

        phone = et_input_phone.getText().toString();
        pass = et_input_password.getText().toString();

        if (phone.length() == 10) {
            phone = "+92" + phone;
        } else {
            phone = "+92" + phone.substring(1);
        }

        AndroidNetworking.post(ConfigURL.URL_LOGIN_PERSON)
                .addBodyParameter("password", pass)
                .addBodyParameter("mobileNo", phone)
                .addBodyParameter("fcmKey", FirebaseInstanceId.getInstance().getToken())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String name = "", email = "", mobile = "", parent = "";
                        ProgressDialogClass.dismissRoundProgress();
                        try {
                            if (!response.getBoolean("error")) {

                                JSONArray jsonArray = response.getJSONArray("user");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    User workerDetail = new User(jsonArray.getJSONObject(i));
                                    name = workerDetail.getUser_name();
                                    email = workerDetail.getUser_email();
                                    mobile = workerDetail.getUser_mobileNo();
                                    parent = workerDetail.getParent_mobileNo();
                                }


                                ConfigURL.PICK_UP_LAT = "";
                                ConfigURL.PICK_UP_LNG = "";
                                ConfigURL.DROP_OF_LAT = "";
                                ConfigURL.DROP_OF_LNG = "";

                                Intent intent = new Intent(getActivity(), Drawer.class);
                                SharedPreferences preferences = getActivity().getSharedPreferences("PREFRENCE", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("EMAIL", email);
                                editor.putString("PHONE", mobile);
                                editor.putString("NAME", name);
                                editor.putString("PARENT", parent);
                                editor.commit();
                                startActivity(intent);
                                getActivity().finish();

                            } else if (response.getBoolean("error")) {
                                Toast.makeText(getActivity(), "Wrong phone/password", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                        ProgressDialogClass.dismissRoundProgress();
                        Log.v("Sign In", "" + anError);
                        Toast.makeText(getActivity(), "" + anError, Toast.LENGTH_LONG).show();
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
