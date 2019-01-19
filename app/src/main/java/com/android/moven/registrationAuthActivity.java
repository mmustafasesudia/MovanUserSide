package com.android.moven;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.TimeUnit;

public class registrationAuthActivity extends Activity {


    private static final String TAG = "PhoneAuthActivity";
    public String r_name, r_confirm_password, r_email, r_password, r_phone;
    String LOGIN = "active";
    TextView countDown;
    Button verify, resend;
    Context context;
    //SMS Authentication Using FireBase
    EditText mVerificationField;
    Button sendVerificationCode, mVerifyButton, mResendButton;
    String mVerificationId;
    TextView textView;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    public registrationAuthActivity() {
        context = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_pin_code);

        //give the context
        AndroidNetworking.initialize(getApplicationContext());

        //Get Data Using Bundle Of Previous Registration Process
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            r_name = (String) bundle.get("r_name");
            r_phone = (String) bundle.get("r_phone");
            r_password = (String) bundle.get("r_password");
            r_confirm_password = (String) bundle.get("r_confirm_password");

        }

        mVerificationField = findViewById(R.id.input_pin_code);
        countDown = findViewById(R.id.countDown);
        verify = findViewById(R.id.btn_verify);
        resend = findViewById(R.id.btn_resend);
        textView = findViewById(R.id.textView);

        textView.setText("To Complete Registration \n Enter Pin Code \n which you recived on " + r_phone);

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

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = mVerificationField.getText().toString();

                resendVerificationCode(r_phone, mResendToken);
            }
        });


        //SMS Authentication Using FireBase

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
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.", Snackbar.LENGTH_SHORT).show();
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
        Log.d("auth", r_phone);

        if (NetworkConnectivityClass.isNetworkAvailable(registrationAuthActivity.this)) {
            startPhoneNumberVerification(r_phone);
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Internet not connected", Snackbar.LENGTH_SHORT).show();

        }
    }

    //Firebase Methods

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            //*******network call
                            Log.d("FCM Key", "" + FirebaseInstanceId.getInstance().getToken() + "r_name:" + r_name + "r_email:" + r_email + "r_password:" + r_password + "r_phone:" + r_phone + "FirebaseInstanceId.getInstance().getToken(): " + FirebaseInstanceId.getInstance().getToken());
                            /*if(new Wifi_check(registrationAuthActivity.this).isNetworkAvailable()) {
                                /*//***********post user data********
                             *//*     AndroidNetworking.post(ConfigURL.URL_REGISTER)
                                        .addBodyParameter("pName", r_name)
                                        .addBodyParameter("pEmail", r_email)
                                        .addBodyParameter("pPass", r_password)
                                        .addBodyParameter("pMobile", r_phone)
                                        .addBodyParameter("customerFcmKey", FirebaseInstanceId.getInstance().getToken())
                                        .setTag("test")
                                        .setPriority(Priority.MEDIUM)
                                        .build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                Intent i = new Intent(registrationAuthActivity.this, CategoryActivity.class);
                                                SharedPreferences preferences = getSharedPreferences("PREFRENCE", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString("LOGIN", LOGIN);
                                                editor.putString("PHONE", r_phone);
                                                editor.putString("NAME", r_name+"");
                                                editor.putString("EMAIL", r_email+"");
                                                editor.commit();
                                                startActivity(i);
                                                mAuth.signOut();
                                                finish();
                                            }

                                            @Override
                                            public void onError(ANError error) {
                                                Toast.makeText(registrationAuthActivity.this, "Error " + error, Toast.LENGTH_LONG).show();
                                            }
                                        });*//*

                            }
                            else {
                                Snackbar.make(findViewById(android.R.id.content), "Internet not connected", Snackbar.LENGTH_SHORT).show();

                            }*/
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
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

        Timer(resend);
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
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks

        resend.setVisibility(View.GONE);
        countDown.setVisibility(View.VISIBLE);
        Timer(resend);
    }

    void Timer(final Button button) {
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

   /* @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(this, "On start",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(registrationAuthActivity.this, Nav_drawer_user.class));
            finish();
        }
    }*/

}
