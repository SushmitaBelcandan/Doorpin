package com.app.doorpin.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.doorpin.Adapters.Utils;
import com.app.doorpin.R;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.RP_Model;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassword extends AppCompatActivity implements View.OnClickListener {

    Button btn_reset;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;

    EditText et_createpassword, et_confirmpassword;
    String createpassword, confirmpassword;
    ImageView ivConfirmShowPass, ivConfirmHidePass, ivCreateShowPass, ivCreateHidePass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        et_createpassword = findViewById(R.id.et_cp);
        et_confirmpassword = findViewById(R.id.et_cfrmp);
        ivConfirmShowPass = findViewById(R.id.ivConfirmShowPass);
        ivConfirmHidePass = findViewById(R.id.ivConfirmHidePass);
        ivCreateShowPass = findViewById(R.id.ivCreateShowPass);
        ivCreateHidePass = findViewById(R.id.ivCreateHidePass);

        progressDialog = new ProgressDialog(ResetPassword.this);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);


        btn_reset = findViewById(R.id.btn_reset);
        btn_reset.setOnClickListener(this);
        //
        //Create password show and hide
        ivCreateShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivCreateShowPass.setVisibility(View.GONE);
                ivCreateHidePass.setVisibility(View.VISIBLE);
                et_createpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        });

        ivCreateHidePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivCreateHidePass.setVisibility(View.GONE);
                ivCreateShowPass.setVisibility(View.VISIBLE);
                et_createpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });
        //Confirm password show and hide
        ivConfirmShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivConfirmShowPass.setVisibility(View.GONE);
                ivConfirmHidePass.setVisibility(View.VISIBLE);
                et_confirmpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        });

        ivConfirmHidePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivConfirmHidePass.setVisibility(View.GONE);
                ivConfirmShowPass.setVisibility(View.VISIBLE);
                et_confirmpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });
        //--------------------------------------------------------------------------------------------------------

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reset:
                createpassword = et_createpassword.getText().toString();
                confirmpassword = et_confirmpassword.getText().toString();
                if (validatePassword(createpassword, confirmpassword)) {
                    if (Utils.CheckInternetConnection(getApplicationContext())) {
                        resetPassword();
                    } else {
                        Toast.makeText(this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Please Enter Required Information", Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
    }


    private boolean validatePassword(String setpassword, String confirmpassword) {
        if (setpassword == null || setpassword.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Invalid Password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (setpassword.equals(confirmpassword)) {
            Toast.makeText(getApplicationContext(), "Password entered validated successfully", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }


    public void resetPassword() {

        try {
            progressDialog.show();
            progressDialog.setMessage("Please Wait...");
        } catch (Exception e) {
            e.printStackTrace();
        }

        final RP_Model user = new RP_Model("1", "9535980054", "123456");
        Call<RP_Model> call = apiInterface.resetPassword(user);
        call.enqueue(new Callback<RP_Model>() {
            @Override
            public void onResponse(Call<RP_Model> call, Response<RP_Model> response) {
                RP_Model rp_model = response.body();
                if (rp_model.status.equals("success")) {
                    Toast.makeText(ResetPassword.this, rp_model.message, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    startActivity(new Intent(ResetPassword.this, PasswordChanged.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<RP_Model> call, Throwable t) {

                call.cancel();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentForgotPassword = new Intent(ResetPassword.this, ForgotPassword.class);
        startActivity(intentForgotPassword);
        finish();
    }


}