package com.ziziapps.firebaseotp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.ziziapps.firebaseotp.databinding.ActivitySendOtpactivityBinding;

import java.util.concurrent.TimeUnit;

public class SendOTPActivity extends AppCompatActivity {

    ActivitySendOtpactivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        String locale = getApplicationContext().getResources().getConfiguration().locale.getCountry();
//        TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
//        String countryCodeValue = tm.getSimCountryIso();
//        binding.activitySendOtpTvCountryCode.setText("+" + countryCodeValue + "-");

        binding.activitySendOtpBtnGetOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.activitySendOtpEtNumber.getText().toString().trim().isEmpty()){
                    Toast.makeText(SendOTPActivity.this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                binding.activitySendOtpPbLoading.setVisibility(View.VISIBLE);
                binding.activitySendOtpBtnGetOtp.setVisibility(View.INVISIBLE);
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+20" + binding.activitySendOtpEtNumber.getText().toString(),
                        60,
                        TimeUnit.SECONDS,
                        SendOTPActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                binding.activitySendOtpPbLoading.setVisibility(View.GONE);
                                binding.activitySendOtpBtnGetOtp.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                binding.activitySendOtpPbLoading.setVisibility(View.GONE);
                                binding.activitySendOtpBtnGetOtp.setVisibility(View.VISIBLE);
                                Toast.makeText(SendOTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                binding.activitySendOtpPbLoading.setVisibility(View.GONE);
                                binding.activitySendOtpBtnGetOtp.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(getApplicationContext(), VerifyOTPActivity.class);
                                intent.putExtra("mobile", binding.activitySendOtpEtNumber.getText().toString());
                                intent.putExtra("verificationCode", s);
                                startActivity(intent);
                            }
                        }
                );

            }
        });
    }
}