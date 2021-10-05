package com.ziziapps.firebaseotp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.ziziapps.firebaseotp.databinding.ActivityVerifyOtpactivityBinding;

import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity {

    private ActivityVerifyOtpactivityBinding binding;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.activityVerifyOtpTvNumber.setText(String.format(
                "+20-%s", getIntent().getStringExtra("mobile")
        ));

        setupOTPInput();


        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                binding.activityVerifyOtpTvResendOtp.setText(getString(R.string.wait ) + ": " + millisUntilFinished / 1000);
                binding.activityVerifyOtpTvResendOtp.setEnabled(false);
            }
            public void onFinish() {
                binding.activityVerifyOtpTvResendOtp.setText(R.string.resend_otp);
                binding.activityVerifyOtpTvResendOtp.setEnabled(true);
            }
        }.start();


        verificationId = getIntent().getStringExtra("verificationCode");
        binding.activitySendOtpBtnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.activityVerifyOtpEtCode1.getText().toString().trim().isEmpty()
                        || binding.activityVerifyOtpEtCode2.getText().toString().trim().isEmpty()
                        || binding.activityVerifyOtpEtCode3.getText().toString().trim().isEmpty()
                        || binding.activityVerifyOtpEtCode4.getText().toString().trim().isEmpty()
                        || binding.activityVerifyOtpEtCode5.getText().toString().trim().isEmpty()
                        || binding.activityVerifyOtpEtCode6.getText().toString().trim().isEmpty()){
                    Toast.makeText(VerifyOTPActivity.this, "Please enter valid code", Toast.LENGTH_SHORT).show();
                    return;
                }

                String code =
                        binding.activityVerifyOtpEtCode1.getText().toString() +
                                binding.activityVerifyOtpEtCode2.getText().toString() +
                                binding.activityVerifyOtpEtCode3.getText().toString() +
                                binding.activityVerifyOtpEtCode4.getText().toString() +
                                binding.activityVerifyOtpEtCode5.getText().toString() +
                                binding.activityVerifyOtpEtCode6.getText().toString();

                if (verificationId != null) {
                    binding.activityVerifyOtpPbLoading.setVisibility(View.VISIBLE);
                    binding.activitySendOtpBtnVerify.setVisibility(View.INVISIBLE);
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            verificationId,
                            code
                    );

                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    binding.activityVerifyOtpPbLoading.setVisibility(View.GONE);
                                    binding.activitySendOtpBtnVerify.setVisibility(View.VISIBLE);
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(VerifyOTPActivity.this, "The verification code entered was invalid", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });
        binding.activityVerifyOtpTvResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+20" + getIntent().getStringExtra("mobile"),
                        60,
                        TimeUnit.SECONDS,
                        VerifyOTPActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(VerifyOTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                verificationId = s;
                                Toast.makeText(VerifyOTPActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                                new CountDownTimer(60000, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                        binding.activityVerifyOtpTvResendOtp.setText(getString(R.string.wait ) + ": " + millisUntilFinished / 1000);
                                        binding.activityVerifyOtpTvResendOtp.setEnabled(false);
                                    }
                                    public void onFinish() {
                                        binding.activityVerifyOtpTvResendOtp.setText(R.string.resend_otp);
                                        binding.activityVerifyOtpTvResendOtp.setEnabled(true);
                                    }
                                }.start();
                            }
                        }
                );
            }
        });
    }

    private void setupOTPInput(){
        binding.activityVerifyOtpEtCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    binding.activityVerifyOtpEtCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.activityVerifyOtpEtCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    binding.activityVerifyOtpEtCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.activityVerifyOtpEtCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    binding.activityVerifyOtpEtCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.activityVerifyOtpEtCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    binding.activityVerifyOtpEtCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.activityVerifyOtpEtCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    binding.activityVerifyOtpEtCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}