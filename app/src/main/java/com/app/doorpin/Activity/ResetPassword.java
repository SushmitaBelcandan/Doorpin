package com.app.doorpin.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.se.omapi.Session;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.doorpin.Adapters.Utils;
import com.app.doorpin.R;
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.FP_Verify;
import com.app.doorpin.retrofit.RP_Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassword extends AppCompatActivity implements View.OnClickListener {

    Button btn_reset;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;
    SessionManager sessionManager;

    ImageView ivConfirmShowPass, ivConfirmHidePass, ivCreateShowPass, ivCreateHidePass;
    EditText et_createpassword, et_confirmpassword;

    String createpassword, confirmpassword;
    String str_login_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Intent oIntent = getIntent();//call from fpverify
        str_login_id = oIntent.getExtras().getString("LOGIN_ID_RESET");
        et_createpassword = findViewById(R.id.et_cp);
        et_confirmpassword = findViewById(R.id.et_cfrmp);
        ivConfirmShowPass = findViewById(R.id.ivConfirmShowPass);
        ivConfirmHidePass = findViewById(R.id.ivConfirmHidePass);
        ivCreateShowPass = findViewById(R.id.ivCreateShowPass);
        ivCreateHidePass = findViewById(R.id.ivCreateHidePass);

        progressDialog = new ProgressDialog(ResetPassword.this);
        sessionManager = new SessionManager(ResetPassword.this);
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
                        resetPassword(sessionManager.getDoctorNurseId(), str_login_id, confirmpassword);
                    } else {
                        Toast.makeText(this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Toast.makeText(this, "Please Enter Required Information", Toast.LENGTH_SHORT).show();
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
        }
        if (!setpassword.equals(confirmpassword)) {
            Toast.makeText(getApplicationContext(), "Password mismatch", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public void resetPassword(String str_usr_type, String str_login_id, String str_password) {

        try {
            progressDialog.show();
            progressDialog.setMessage("Please Wait...");
        } catch (Exception e) {
            e.printStackTrace();
        }

        final RP_Model user = new RP_Model(str_usr_type, str_login_id, str_password);
        Call<RP_Model> call = apiInterface.resetPassword(user);
        call.enqueue(new Callback<RP_Model>() {
            @Override
            public void onResponse(Call<RP_Model> call, Response<RP_Model> response) {
                RP_Model rp_model = response.body();
                if (response.isSuccessful()) {
                    if (rp_model.status.equals("success")) {
                        List<RP_Model.RPModel> reset_pass_list = rp_model.response;
                        for (RP_Model.RPModel reset_pass_data : reset_pass_list) {
                            sessionManager.saveDoctorNurseId(reset_pass_data.user_type);
                        }
                        Toast.makeText(ResetPassword.this, rp_model.message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        startActivity(new Intent(ResetPassword.this, PasswordChanged.class));
                        finish();
                    } else {
                        Toast.makeText(ResetPassword.this, rp_model.message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } else {
                    if (response.code() == 400) {
                        if (!response.isSuccessful()) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response.errorBody().string());
                                String userMessage = jsonObject.getString("status");
                                String internalMessage = jsonObject.getString("message");
                                progressDialog.dismiss();
                                new AlertDialog.Builder(ResetPassword.this)
                                        .setMessage(internalMessage)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RP_Model> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
                new AlertDialog.Builder(ResetPassword.this)
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
        Intent intentForgotPassword = new Intent(ResetPassword.this, ForgotPassword.class);
        startActivity(intentForgotPassword);
        finish();
    }


}