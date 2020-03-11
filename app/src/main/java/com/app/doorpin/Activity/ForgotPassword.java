package com.app.doorpin.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.doorpin.Adapters.Utils;
import com.app.doorpin.R;
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.FP_Model;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    SessionManager session;

    LinearLayout ll_back_to_login;
    Button btn_submit;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;
    EditText et_mail_or_phone;

    public String str_login_id_fp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        session = new SessionManager(ForgotPassword.this);

        ll_back_to_login = findViewById(R.id.ll_back_to_login);
        btn_submit = findViewById(R.id.btn_submit);
        et_mail_or_phone = findViewById(R.id.et_mn_or_email_Fp);

        progressDialog = new ProgressDialog(ForgotPassword.this);


        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        ll_back_to_login.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_back_to_login:
                startActivity(new Intent(ForgotPassword.this, Login.class));
                finish();
                break;
            case R.id.btn_submit:
                str_login_id_fp = et_mail_or_phone.getText().toString().trim();

                if (validateMobileNumberorEmailId(str_login_id_fp)) {
                    if (Utils.CheckInternetConnection(getApplicationContext())) {
                        setPassword(session.getDoctorNurseId(), str_login_id_fp);
                    } else {
                        Toast.makeText(this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //empty
                }
            default:
                break;

        }
    }

    public void setPassword(String usrType, String strLoginId) {

        try {
            progressDialog.show();
            progressDialog.setMessage("Please Wait...");
        } catch (Exception e) {
            e.printStackTrace();
        }

        final FP_Model user = new FP_Model(usrType, strLoginId);
        Call<FP_Model> call = apiInterface.getPassword(user);
        call.enqueue(new Callback<FP_Model>() {
            @Override
            public void onResponse(Call<FP_Model> call, Response<FP_Model> response) {
                FP_Model fp_model = response.body();
                if (response.isSuccessful()) {
                    String login_id = "NA";
                    if (fp_model.status.equals("success")) {
                        List<FP_Model.FPModelDatum> fp_model_list_data = fp_model.response;
                        for (FP_Model.FPModelDatum fp_data : fp_model_list_data) {
                            login_id = fp_data.loginId;
                            session.saveDoctorNurseId(fp_data.userType);
                        }
                        Toast.makeText(ForgotPassword.this, fp_model.message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        startSMSListener();
                        startActivity(new Intent(ForgotPassword.this, FPVerify.class).putExtra("LOGIN_ID", login_id));
                        finish();
                    } else {
                        Toast.makeText(ForgotPassword.this, fp_model.message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(ForgotPassword.this)
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
            public void onFailure(Call<FP_Model> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
                new AlertDialog.Builder(ForgotPassword.this)
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

    private boolean validateMobileNumberorEmailId(String loginid) {

        if (loginid == null || loginid.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Invalid User Id", Toast.LENGTH_SHORT).show();
            return false;

        } else {
            if (loginid.matches("[0-9]+")) {
                if (loginid.length() < 7 || loginid.length() > 15) {
                    et_mail_or_phone.setError("Please Enter valid phone number");
                    et_mail_or_phone.requestFocus();
                    return false;
                }
            } else {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(loginid).matches()) {
                    et_mail_or_phone.setError("Please Enter valid email");
                    et_mail_or_phone.requestFocus();
                    return false;
                }
            }
        }
        return true;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentLogin = new Intent(ForgotPassword.this, Login.class);
        startActivity(intentLogin);
        finish();
    }

    //-----start sms retriver service-----
    public void startSMSListener() {
        SmsRetrieverClient mClient = SmsRetriever.getClient(this);
        Task<Void> mTask = mClient.startSmsRetriever();
        mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ForgotPassword.this, "SMS Retriever starts", Toast.LENGTH_LONG).show();
            }
        });
        mTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgotPassword.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

}