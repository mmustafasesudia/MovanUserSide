package com.android.moven.ForgotFragmets;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.moven.NetworkConnectivityClass;
import com.android.moven.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ForgotPasswordPinFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "Forgot Password Pin";
    String phone;
    TextView countDown;
    Button verify, btn_verify, resend;
    EditText mVerificationField;
    String mVerificationId;
    TextView textView;
    Context context;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    public ForgotPasswordPinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.forgot_password_pin_fragment, container, false);


        FirebaseApp.initializeApp(getActivity());
        Bundle bundle = getArguments();

        if (bundle != null) {
            phone = (String) bundle.get("phone");
        }

        mVerificationField = rootView.findViewById(R.id.input_pin_code_forgot_password);
        countDown = rootView.findViewById(R.id.countDown);
        verify = rootView.findViewById(R.id.btn_reset_password);
        resend = rootView.findViewById(R.id.btn_resend);
        //textView = (TextView) rootView.findViewById(R.id.textViewPin);

        //textView.setText("To Complete Process \n Enter Pin Code \n which you recived on " + phone);


        verify.setOnClickListener(this);
        resend.setOnClickListener(this);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Cannot be empty.");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
            }
        });


        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    //Invalid Phone Number
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
        Log.d("auth", phone);
//        Toast.makeText(getActivity(), "Phone no: " + phone,
//                Toast.LENGTH_LONG).show();
        startPhoneNumberVerification(phone);
        Log.d(TAG, phone);

        return rootView;
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();

                            if (NetworkConnectivityClass.isNetworkAvailable(getActivity())) {

                                Fragment fragmentName = null;
                                Fragment ForgotInputNewPasswordFragment = new ForgotInputNewPasswordFragment();
                                fragmentName = ForgotInputNewPasswordFragment;
                                Bundle args = new Bundle();
                                args.putString("phone", phone);
                                fragmentName.setArguments(args);
                                replaceFragment(fragmentName);


                            } else {
                                Snackbar.make(getActivity().findViewById(android.R.id.content), "Internet Not Connected",
                                        Snackbar.LENGTH_SHORT).show();
                            }

                            FirebaseAuth.getInstance().signOut();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                mVerificationField.setError("Invalid code.");
                            }
                        }
                    }
                });
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

        timerStart(resend);

    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        Toast.makeText(getActivity(), "Verify Method" + code, Toast.LENGTH_LONG).show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseApp.initializeApp(getActivity());
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //TODO below fragment call if needed
            /*startActivity(new Intent(WorkerForgotPasswordPin.this, WorkerForgotInputNewPassword.class));
            finish();*/
        }
    }

    public void timerStart(final Button button) {
        new CountDownTimer(120000, 1000) {

            public void onTick(long millisUntilFinished) {
                countDown.setText("Didn't Received Code Resend in: " + String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                countDown.setVisibility(View.GONE);
                button.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_resend:
                timerStart(resend);
                resendVerificationCode(phone, mResendToken);
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
