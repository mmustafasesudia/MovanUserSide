package com.android.moven.ForgotFragmets;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.moven.ConfigURL;
import com.android.moven.Fragments.ProgressDialogClass;
import com.android.moven.NetworkConnectivityClass;
import com.android.moven.R;
import com.android.moven.SignInSignUpForgotActivity;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotInputNewPasswordFragment extends Fragment implements View.OnClickListener {

    Button btn_done;
    EditText et_input_pass, et_input_confirm_pass;
    String phone, pass;
    ImageView img_back;

    public ForgotInputNewPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.forgot_input_new_password_fragment, container, false);

        // ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        Bundle bundle = getArguments();

        if (bundle != null) {
            phone = (String) bundle.get("phone");
        }
        img_back = rootView.findViewById(R.id.img_back);
        final TextInputLayout passwordWrapper = rootView.findViewById(R.id.pass);
        passwordWrapper.setHint("Password");
        final TextInputLayout cpasswordWrapper = rootView.findViewById(R.id.c_pass);
        cpasswordWrapper.setHint("Confirm Password");
        btn_done = rootView.findViewById(R.id.btn_done);
        et_input_pass = rootView.findViewById(R.id.et_input_pass);
        et_input_confirm_pass = rootView.findViewById(R.id.et_input_confirm_pass);
        btn_done.setOnClickListener(this);
        img_back.setOnClickListener(this);

        return rootView;
    }


    public void submit() {

        if (!validatePassword()) {
            return;
        }
        if (!validatePasswordConfirmation()) {
            return;
        }
        if (NetworkConnectivityClass.isNetworkAvailable(getActivity())) {
            //TODO send data here
            sendData();
        } else {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Internet Not Connected",
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean validatePassword() {
        if (et_input_pass.getText().toString().trim().isEmpty()) {
            et_input_pass.setError("Password Cannot Be Empty");
            requestFocus(et_input_pass);
            return false;
        }
        return true;
    }

    private boolean validatePasswordConfirmation() {
        if (et_input_confirm_pass.getText().toString().trim().isEmpty()) {
            et_input_confirm_pass.setError("Confirm Password Cannot Be Empty");
            requestFocus(et_input_confirm_pass);
            return false;
        } else if (!et_input_pass.getText().toString().equals(et_input_confirm_pass.getText().toString())) {
            et_input_pass.setError("Password Not Match");
            requestFocus(et_input_pass);
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

        pass = et_input_pass.getText().toString();

        ProgressDialogClass.showRoundProgress(getActivity(), "Resetting Password ");
        AndroidNetworking.post(ConfigURL.URL_FORGOT_PASS)
                .addBodyParameter("pMobile", phone)
                .addBodyParameter("pPass", pass)
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

                            if (!error) {
                                Toast.makeText(getActivity(), "Password Updated", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getActivity(), "Now You can Sign In from here", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getActivity(), SignInSignUpForgotActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                getActivity().finish();
                            }
                            if (error) {
                                Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(getActivity(), "" + error, Toast.LENGTH_LONG).show();
                        ProgressDialogClass.dismissRoundProgress();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_done:
                submit();
                break;
            case R.id.img_back:
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    getActivity().finish();
                } else {
                    getActivity().onBackPressed();
                }
                break;
        }
    }
}