package com.app.doorpin.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.doorpin.Adapters.Utils;
import com.app.doorpin.R;
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.FP_Model;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    SessionManager session;

    TextView back_to_login;
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

        back_to_login = findViewById(R.id.tv_b_t_login);
        btn_submit = findViewById(R.id.btn_submit);
        et_mail_or_phone = findViewById(R.id.et_mn_or_email_Fp);

        progressDialog = new ProgressDialog(ForgotPassword.this);


        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        back_to_login.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_b_t_login:
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
                String login_id = null;
                if (fp_model.status.equals("success")) {
                    List<FP_Model.FPModelDatum> fp_model_list_data = fp_model.response;
                    for (FP_Model.FPModelDatum fp_data : fp_model_list_data) {
                        login_id = fp_data.loginId;
                    }
                    Toast.makeText(ForgotPassword.this, fp_model.message, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    startActivity(new Intent(ForgotPassword.this, FPVerify.class).putExtra("LOGIN_ID", str_login_id_fp));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<FP_Model> call, Throwable t) {

                call.cancel();
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


}