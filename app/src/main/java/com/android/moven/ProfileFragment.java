package com.android.moven;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    TextView tv_name, tv_email, tv_mobile, tv_signout, tv_mobile_parent;
    LinearLayout ll_change_name, ll_email, ll_pass;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tv_name = view.findViewById(R.id.tv_name);
        tv_email = view.findViewById(R.id.tv_email);
        tv_mobile = view.findViewById(R.id.tv_mobile);
        tv_mobile_parent = view.findViewById(R.id.tv_mobile_parent);
        tv_signout = view.findViewById(R.id.tv_signout);


        ll_pass = view.findViewById(R.id.ll_pass);
        ll_pass.setOnClickListener(this);

        tv_name.setText("" + ConfigURL.getName(getActivity()));
        tv_email.setText("" + ConfigURL.getEmail(getActivity()));
        tv_mobile.setText("" + ConfigURL.getMobileNumber(getActivity()));
        tv_mobile_parent.setText("" + ConfigURL.getParent(getActivity()));

        tv_signout.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragmentName = null;
        switch (v.getId()) {
            case R.id.tv_signout:
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getActivity(), SignInSignUpForgotActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                ConfigURL.clearshareprefrence(getActivity());
                getActivity().finish();
                break;
        }
    }


}
