package com.android.moven.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.moven.ConfigURL;
import com.android.moven.Drawer;
import com.android.moven.NetworkConnectivityClass;
import com.android.moven.R;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
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
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpVerification extends Fragment implements View.OnClickListener {

    private static final String TAG = "PhoneAuthActivity";
    String fname, language, password, phone, etype;
    TextView countDown;
    Button verify, resend;
    //SMS Authentication Using FireBase
    EditText mVerificationField;
    String mVerificationId;
    TextView textView;
    Context context;
    String LOGIN = "active";
    Bitmap bitmap;
    byte[] byteArray;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    public SignUpVerification() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_signup_password_pin, container, false);

        // ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        bitmap = BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.ic_current_location);

        Bundle bundle = getArguments();

        if (bundle != null) {
            if (bundle != null) {

                phone = (String) bundle.get("phone");
                fname = (String) bundle.get("full_name");
                password = (String) bundle.get("pass");
                etype = (String) bundle.get("type");

            }
        }

        //  phone = "+923342220502";
        mVerificationField = rootView.findViewById(R.id.input_pin_code);
        countDown = rootView.findViewById(R.id.countDown);
        verify = rootView.findViewById(R.id.btn_verify);
        resend = rootView.findViewById(R.id.btn_resend);
//        textView = (TextView) rootView.findViewById(R.id.textViewPin);
//
//        textView.setText("To Complete Process \n Enter Pin Code \n which you recived on " + phone);


        verify.setOnClickListener(this);
        resend.setOnClickListener(this);

       /* verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Cannot be empty.");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
            }
        });*/

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
                    /*Snackbar.make(getActivity().findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();*/
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
//        Toast.makeText(this, "Phone no: " + phone,
//                Toast.LENGTH_LONG).show();
        countDown.setVisibility(View.VISIBLE);
        verify.setVisibility(View.VISIBLE);
        startPhoneNumberVerification(phone);
        Log.d(TAG, phone);

        return rootView;
    }


    //Firebase Methods

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();

                            if (NetworkConnectivityClass.isNetworkAvailable(getActivity())) {
                                try {
                                    sendData();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                /*Snackbar.make(getActivity().findViewById(android.R.id.content), "Internet Not Connected",
                                        Snackbar.LENGTH_SHORT).show();*/
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

        timerStart(resend, verify);

    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
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
            /*startActivity(new Intent(getActivity(), DrawerMainActivity.class));
            getActivity().finish();*/
        }
    }

    public void timerStart(final Button visible, final Button gone) {
        new CountDownTimer(120000, 1000) {

            public void onTick(long millisUntilFinished) {
                countDown.setText("Didn't Received Code Resend in: " + String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                countDown.setVisibility(View.GONE);
                gone.setVisibility(View.GONE);
                countDown.setVisibility(View.GONE);
                visible.setVisibility(View.VISIBLE);
            }
        }.start();
    }


    public void sendData() throws IOException {

        ProgressDialogClass.showRoundProgress(getActivity(), "Please wait...");


        if (NetworkConnectivityClass.isNetworkAvailable(getActivity())) {

            AndroidNetworking.post(ConfigURL.URL_REGISTER_PERSON)
                    .addBodyParameter("uName", fname)
                    .addBodyParameter("uPass", password)
                    .addBodyParameter("uMobile", phone)
                    .addBodyParameter("uEmail", "ex@gmail.com")
                    .addBodyParameter("uType", etype)
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
                                //Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_LONG).show();
                                if (!error) {

                                    Intent intent = new Intent(getActivity(), Drawer.class);
                                    SharedPreferences preferences = getActivity().getSharedPreferences("PREFRENCE", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("LOGIN", LOGIN);
                                    editor.putString("PHONE", phone);
                                    editor.putString("NAME", fname + "");
                                    editor.commit();
                                    startActivity(intent);
                                    mAuth.signOut();
                                    getActivity().finish();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError error) {
                            ProgressDialogClass.dismissRoundProgress();
                            Log.v("Sign In", "" + error);
//                        Toast.makeText(WorkerSignInPage.this, "" + error, Toast.LENGTH_LONG).show();
                        }
                    });
            //***********post user data********
            AndroidNetworking.upload(ConfigURL.URL_REGISTER_PERSON)
                    .addMultipartParameter("uName", fname)
                    .addMultipartParameter("uPass", phone)
                    .addMultipartParameter("uLanguage", "English")
                    .addMultipartParameter("uPass", password)
                    .addMultipartParameter("uFcmKey", FirebaseInstanceId.getInstance().getToken())
                    .addMultipartFile("photo", returnFile(bitmap))
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ProgressDialogClass.dismissRoundProgress();
                            try {
                                String msg = response.getString("message");
                                Log.d("error", "error ni ay");

                                boolean error = false;

                                error = response.getBoolean("error");
                                //Toast.makeText(getActivity(), "agya" + msg, Toast.LENGTH_LONG).show();
                                if (!error) {


                                    Intent intent = new Intent(getActivity(), Drawer.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            } catch (JSONException e) {
                                Log.d("error", "errorrr");
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            ProgressDialogClass.dismissRoundProgress();
                            Log.v("Sign In", "" + error);
//                        Toast.makeText(WorkerSignInPage.this, "" + error, Toast.LENGTH_LONG).show();
                        }
                    });

            ///

            ///

        } else {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Internet not connected", Snackbar.LENGTH_SHORT).show();

        }
    }

    public File returnFile(Bitmap bmp) throws IOException {
        File f = new File(getActivity().getCacheDir(), phone + ".png");
        f.createNewFile();
        //Convert bitmap to byte array
        Bitmap bitmap = bmp;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();
        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        return f;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verify:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Cannot be empty.");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);

                break;
            case R.id.btn_resend:
                countDown.setVisibility(View.VISIBLE);
                verify.setVisibility(View.VISIBLE);
                resend.setVisibility(View.GONE);
                timerStart(resend, verify);
                resendVerificationCode(phone, mResendToken);
                break;
        }
    }
}
