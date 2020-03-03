package com.app.doorpin.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.doorpin.Adapters.Utils;
import com.app.doorpin.R;
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.FP_Model;
import com.app.doorpin.retrofit.FP_Verify;
import com.app.doorpin.retrofit.ResendOtp;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FPVerify extends AppCompatActivity implements View.OnClickListener {

    SessionManager session;
    ProgressDialog progressDialog;
    public ProgressBar progressBarCircle;
    public CountDownTimer countDownTimer;
    ApiInterface apiInterface;

    TextView textTimer, tv_resend_otp;
    Button btn_verify;
    EditText editTextone, editTexttwo, editTextthree, editTextfour;
    TextView tv_text_info;

    int i = 0;
    String str_login_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        btn_verify = findViewById(R.id.btn_verify);
        textTimer = findViewById(R.id.tv_timer);
        tv_resend_otp = findViewById(R.id.tv_resend_otp);
        String text = "<font color=#969696>Didn't Receive OTP?</font> <font color=#7D297E>Resend</font>";
        tv_resend_otp.setText(Html.fromHtml(text));
        // tv_resend_otp.setVisibility(View.GONE);

        //  progressBarCircle = findViewById(R.id.progressBarCircle);
        Intent oIntent = getIntent();//call from forgotpassword
        str_login_id = oIntent.getExtras().getString("LOGIN_ID");
        tv_text_info = findViewById(R.id.tv_text_info);
        tv_text_info.setText("Please enter the OTP sent to" + " " + str_login_id);

        session = new SessionManager(FPVerify.this);
        progressDialog = new ProgressDialog(FPVerify.this);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        btn_verify.setOnClickListener(this);

        progressBarCircle = findViewById(R.id.progressBarCircle);

        editTextone = findViewById(R.id.editTextone);
        editTexttwo = findViewById(R.id.editTexttwo);
        editTextthree = findViewById(R.id.editTextthree);
        editTextfour = findViewById(R.id.editTextfour);

        editTextone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editTextone.getText().toString().length() == 1) {
                    editTexttwo.requestFocus();
                }
            }
        });

        editTexttwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editTexttwo.getText().toString().length() == 0) {
                    editTextone.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editTexttwo.getText().toString().length() == 1) {
                    editTextthree.requestFocus();
                }
            }
        });

        editTextthree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editTextthree.getText().toString().length() == 0) {
                    editTexttwo.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editTextthree.getText().toString().length() == 1) {
                    editTextfour.requestFocus();
                }
            }
        });

        editTextfour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editTextfour.getText().toString().length() == 0) {
                    editTextthree.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // We can call api to verify the OTP here or on an explicit button click
            }
        });

        startTimer();


        tv_resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
                if (Utils.CheckInternetConnection(getApplicationContext())) {
                    if (str_login_id.equals("NA") || str_login_id.equals(null)) {
                        new AlertDialog.Builder(FPVerify.this)
                                .setMessage("Invalid User Id")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    } else {
                        resendOtp(session.getDoctorNurseId(), str_login_id);//recent saved user_type from forgot password
                    }
                } else {
                    new AlertDialog.Builder(FPVerify.this)
                            .setMessage("Please Check Internet Connection!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }

            }
        });

    }

    private void startTimer() {

        //whenever timer is getting started button will be enable
        btn_verify.setEnabled(true);
        btn_verify.setClickable(true);
        btn_verify.setBackgroundResource(R.drawable.button_shape);
        editTextone.setText("");
        editTexttwo.setText("");
        editTextthree.setText("");
        editTextfour.setText("");

        progressBarCircle.setProgress(i);
        tv_resend_otp.setVisibility(View.GONE);


        countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                i++;
                progressBarCircle.setProgress((int) i * 100 / (100000 / 1000));
                textTimer.setText("00: " + millisUntilFinished / 1000 + " " + "s");
            }

            public void onFinish() {
                i = 0;
                textTimer.setText("Time Up!");
                btn_verify.setEnabled(false);
                btn_verify.setClickable(false);
                btn_verify.setBackgroundResource(R.drawable.disable_btn_verify);
                tv_resend_otp.setVisibility(View.VISIBLE);
            }
        }.start();

    }

    public void resendOtp(String str_user_type, String str_login_id) {

        try {
            progressDialog.show();
            progressDialog.setMessage("Please Wait...");
        } catch (Exception e) {
            e.printStackTrace();
        }

        final ResendOtp resend_model = new ResendOtp(str_user_type, str_login_id);
        Call<ResendOtp> resend_call = apiInterface.resendOtp(resend_model);
        resend_call.enqueue(new Callback<ResendOtp>() {
            @Override
            public void onResponse(Call<ResendOtp> call, Response<ResendOtp> response) {
                ResendOtp resend_resources = response.body();
                if (response.isSuccessful()) {
                    if (resend_resources.status.equals("success")) {
                        Toast.makeText(FPVerify.this, resend_resources.message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(FPVerify.this, resend_resources.message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(FPVerify.this)
                            .setMessage("Network Connection error! Please try again later")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ResendOtp> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
                new AlertDialog.Builder(FPVerify.this)
                        .setMessage("Network Connection error! Please try again later")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_verify:
                String otp1 = editTextone.getText().toString().trim();
                String otp2 = editTexttwo.getText().toString().trim();
                String otp3 = editTextthree.getText().toString().trim();
                String otp4 = editTextfour.getText().toString().trim();
                String otp = otp1 + otp2 + otp3 + otp4;
                if (Utils.CheckInternetConnection(getApplicationContext())) {
                    if (otp.length() != 4 || otp.isEmpty()) {
                        Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                    } else {
                        if (str_login_id.equals("NA") || str_login_id.equals(null)) {
                            Toast.makeText(this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show();
                        } else {
                            fpverify(session.getDoctorNurseId(), str_login_id, otp);
                        }
                    }
                } else {
                    Toast.makeText(this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
                }

                // startActivity(new Intent(FPVerify.this,ResetPassword.class));
        }

    }

    public void fpverify(String strUsrType, String strLoginId, String strOtp) {

        try {
            progressDialog.show();
            progressDialog.setMessage("Please Wait...");
        } catch (Exception e) {
            e.printStackTrace();
        }

        final FP_Verify user = new FP_Verify(strUsrType, strLoginId, strOtp);
        Call<FP_Verify> call = apiInterface.verifyForgotPassword(user);
        call.enqueue(new Callback<FP_Verify>() {
            @Override
            public void onResponse(Call<FP_Verify> call, Response<FP_Verify> response) {
                FP_Verify fpv_model = response.body();
                if (response.isSuccessful()) {
                    if (fpv_model.status.equals("success")) {
                        String login_id = "NA";
                        List<FP_Verify.FPVerify> fp_verify_list = fpv_model.response;
                        for (FP_Verify.FPVerify fp_data : fp_verify_list) {
                            login_id = fp_data.login_id; //pass login id for reset api input params
                        }
                        Toast.makeText(FPVerify.this, fpv_model.message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        startActivity(new Intent(FPVerify.this, ResetPassword.class).putExtra("LOGIN_ID_RESET", login_id));
                        finish();
                    } else {
                        Toast.makeText(FPVerify.this, fpv_model.message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(FPVerify.this)
                            .setMessage("Network Connection error! Please try again later")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<FP_Verify> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
                new AlertDialog.Builder(FPVerify.this)
                        .setMessage("Network Connection error! Please try again later")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentForgotPassword = new Intent(FPVerify.this, ForgotPassword.class);
        startActivity(intentForgotPassword);
        finish();
    }


}