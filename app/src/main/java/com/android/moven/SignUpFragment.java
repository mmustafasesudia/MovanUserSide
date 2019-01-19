package com.android.moven;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.moven.Fragments.ProgressDialogClass;
import com.android.moven.Fragments.SignInFragment;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener {

    TextView tv_sign_in;
    Button btn_next;
    String phone, p_phone;
    EditText et_input_full_name, et_input_email, et_input_phone, et_input_password, et_input_phone_parent;
    // RadioButton rb_input_gender_male, rb_input_gender_female;

    public SignUpFragment() {
        // Required empty public constructor
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        //((AppCompatActivity) getActivity()).getSupportActionBar().show();


        et_input_full_name = rootView.findViewById(R.id.et_input_full_name);
        et_input_password = rootView.findViewById(R.id.et_input_password);
        et_input_email = rootView.findViewById(R.id.et_input_email);
        et_input_phone = rootView.findViewById(R.id.et_input_phone);
        et_input_phone_parent = rootView.findViewById(R.id.et_input_phone_parent);


        tv_sign_in = rootView.findViewById(R.id.tv_sign_in);
        btn_next = rootView.findViewById(R.id.btn_next);

        tv_sign_in.setOnClickListener(this);
        btn_next.setOnClickListener(this);

        return rootView;
    }

    public void submit() {

        if (et_input_full_name.getText().toString().isEmpty()) {
            et_input_full_name.setError("Full Name Cannot Be Empty");
            requestFocus(et_input_full_name);
            return;
        }
        if (et_input_full_name.getText().toString().startsWith(" ")) {
            et_input_full_name.setError("Space In Start And End Is Not Allowed");
            requestFocus(et_input_full_name);
            return;
        }

        if (et_input_email.getText().toString().isEmpty() || !isValidEmail(et_input_email.getText().toString())) {
            et_input_email.setError("Invalid Email");
            requestFocus(et_input_email);
            return;
        }
        if (!validatePhone(et_input_phone)) {
            return;
        }
        if (!validatePhone(et_input_phone_parent)) {
            return;
        }
        if (et_input_password.getText().toString().isEmpty()) {
            et_input_password.setError("Password Cannot Be Empty");
            requestFocus(et_input_password);
            return;
        }

        if (NetworkConnectivityClass.isNetworkAvailable(getActivity())) {
            checkIfNumberExist();
        } else {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Internet Not Connected",
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean validatePhone(EditText editText) {
        if (!editText.getText().toString().matches("[0][3][0-9]{9}|[3][0-9]{9}")) {
            editText.setError("Invalid Phone Number");
            requestFocus(editText);
            return false;
        } else if (editText.getText().toString().length() < 10) {
            editText.setError("Invalid Length");
            requestFocus(editText);
            return false;
        } else if (editText.getText().toString().trim().isEmpty()) {
            editText.setError("Feild Cannot Be Empty");
            requestFocus(editText);
            return false;
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void checkIfNumberExist() {
        ProgressDialogClass.showRoundProgress(getActivity(), "Please Wait ..");

        phone = et_input_phone.getText().toString();
        if (phone.length() == 10) {
            phone = "+92" + phone;
        } else {
            phone = "+92" + phone.substring(1);
        }

        p_phone = et_input_phone_parent.getText().toString();
        if (p_phone.length() == 10) {
            p_phone = "+92" + p_phone;
        } else {
            p_phone = "+92" + p_phone.substring(1);
        }

        AndroidNetworking.post(ConfigURL.URL_REGISTER_PERSON)
                .addBodyParameter("name", et_input_full_name.getText().toString())
                .addBodyParameter("mobile_num", phone)
                .addBodyParameter("fcm", FirebaseInstanceId.getInstance().getToken())
                .addBodyParameter("password", et_input_password.getText().toString())
                .addBodyParameter("email", et_input_email.getText().toString())
                .addBodyParameter("parent_num", p_phone)
                .setPriority(Priority.MEDIUM)
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
                                Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_LONG).show();
                            }
                            if (!error) {
                                Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_LONG).show();

                                ConfigURL.PICK_UP_LAT = "";
                                ConfigURL.PICK_UP_LNG = "";
                                ConfigURL.DROP_OF_LAT = "";
                                ConfigURL.DROP_OF_LNG = "";

                                Intent intent = new Intent(getActivity(), Drawer.class);
                                SharedPreferences preferences = getActivity().getSharedPreferences("PREFRENCE", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("EMAIL", et_input_email.getText().toString());
                                editor.putString("PHONE", phone);
                                editor.putString("NAME", et_input_full_name.getText().toString());
                                editor.putString("PARENT", et_input_phone_parent.getText().toString());
                                editor.commit();
                                startActivity(intent);
                                getActivity().finish();
//                                Toast.makeText(getActivity(), "Full Name" + fullname + "Email" + email + "Phone" + phone + "Gender" + gender, Toast.LENGTH_LONG).show();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sign_in:
                Fragment fragmentName = null;
                Fragment SignInFragment = new SignInFragment();
                fragmentName = SignInFragment;
                replaceFragment(fragmentName);
                break;
            case R.id.btn_next:
                submit();
                break;
        }
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

