package com.app.doorpin.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Annotation;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.doorpin.Adapters.Utils;
import com.app.doorpin.R;
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.LoginRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public class Login extends AppCompatActivity implements View.OnClickListener {

    SessionManager sessionManager;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;

    TextView tv_forgotpassword;
    EditText et_login_id, et_password;
    Button btn_login;
    ImageView ivHidePass, ivShowPass;

    private String str_login_id;
    private String str_password;
    private String str_device_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionManager = new SessionManager(Login.this);
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setMessage("Please Wait...");

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        tv_forgotpassword = findViewById(R.id.tv_forgotpassword);
        btn_login = findViewById(R.id.btn_login);
        et_login_id = findViewById(R.id.et_login_id);
        et_password = findViewById(R.id.et_password);

        ivHidePass = findViewById(R.id.ivHidePass);
        ivShowPass = findViewById(R.id.ivShowPass);

        tv_forgotpassword.setOnClickListener(this);
        btn_login.setOnClickListener(this);

        ivShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivShowPass.setVisibility(View.GONE);
                ivHidePass.setVisibility(View.VISIBLE);
                et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        });

        ivHidePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivHidePass.setVisibility(View.GONE);
                ivShowPass.setVisibility(View.VISIBLE);
                et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });
        //get device id
        // str_device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        str_device_id = UUID.randomUUID().toString();//android_id is not safe
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_forgotpassword:
                startActivity(new Intent(Login.this, ForgotPassword.class));
                finish();
                break;
            case R.id.btn_login:
                str_login_id = et_login_id.getText().toString().trim();
                str_password = et_password.getText().toString().trim();
                if (Utils.CheckInternetConnection(getApplicationContext())) {
                    if (validateLogin(str_login_id, str_password)) {
                        loginsuccess(str_login_id, str_password);
                    } else {
                        //Toast.makeText(this, "Please Enter Correct Information", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private boolean validateLogin(String loginid, String passwd) {

        if (loginid == null || loginid.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Invalid User Id", Toast.LENGTH_SHORT).show();
            return false;

        } else {
            if (loginid.matches("[0-9]+")) {
                if (loginid.length() < 7 || loginid.length() > 15) {
                    et_login_id.setError("Please Enter valid phone number");
                    et_login_id.requestFocus();
                    return false;
                } else {
                    if (passwd == null || passwd.trim().length() == 0) {
                        et_password.requestFocus();
                        et_password.setError("Please enter password");
                        return false;
                    } else {
                        if (passwd.trim().length() < 4) {
                            et_password.requestFocus();
                            et_password.setError("Password should not be less than 4 digit");
                            return false;
                        } else {
                            return true;
                        }
                    }

                }
            } else {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(loginid).matches()) {
                    et_login_id.setError("Please Enter valid email");
                    et_login_id.requestFocus();
                    return false;
                } else {
                    if (passwd == null || passwd.trim().length() == 0) {
                        et_password.requestFocus();
                        et_password.setError("Please enter password");
                        return false;
                    } else {
                        if (passwd.trim().length() < 4) {
                            et_password.requestFocus();
                            et_password.setError("Password should not be less than 4 digit");
                            return false;
                        } else {
                            return true;
                        }
                    }

                }
            }
        }
    }

    public void loginsuccess(String loginId, String password) {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final LoginRequest user = new LoginRequest(sessionManager.getDoctorNurseId(), loginId, password, str_device_id, "NA");
        Call<LoginRequest> call = apiInterface.getLogin(user);
        call.enqueue(new Callback<LoginRequest>() {
            @Override
            public void onResponse(Call<LoginRequest> call, Response<LoginRequest> response) {
                LoginRequest loginRequest = response.body();
                if (response.isSuccessful()) {
                    if (loginRequest.status.equals("success")) {
                        List<LoginRequest.Login_Datum> login_datum = loginRequest.response;
                        if (login_datum.size() <= 0) {
                            //when response array is empty
                            progressDialog.dismiss();
                            Toast.makeText(Login.this, loginRequest.message, Toast.LENGTH_SHORT).show();
                        } else {
                            for (LoginRequest.Login_Datum data : login_datum) {
                                if (!data.hospital_id.equals("null") && !data.hospital_id.equals(null) && !data.hospital_id.isEmpty()) {
                                    sessionManager.saveLoginData(data.user_id, data.login_id, data.hospital_id);
                                } else {
                                    sessionManager.saveLoginData(data.user_id, data.login_id, "NA");
                                }
                                sessionManager.saveDoctorNurseId(data.user_type);
                                if (str_device_id.equals(null) || str_device_id.isEmpty()) {
                                    sessionManager.saveDeviceId("NA");
                                } else {
                                    sessionManager.saveDeviceId(str_device_id);
                                }
                                if (sessionManager.getDoctorNurseId().equals("1")) {
                                    Toast.makeText(Login.this, loginRequest.message, Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    startActivity(new Intent(Login.this, HomePage_Doctor.class));
                                    finish();
                                } else {
                                    Toast.makeText(Login.this, loginRequest.message, Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    startActivity(new Intent(Login.this, HomePage_Nurse.class));
                                    finish();
                                }
                            }

                        }
                    } else {
                        //failure response
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, loginRequest.message, Toast.LENGTH_SHORT).show();
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
                                new AlertDialog.Builder(Login.this)
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
            public void onFailure(Call<LoginRequest> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
                new AlertDialog.Builder(Login.this)
                        .setMessage("Server error! Please try again later")
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
        Intent intentMainAct = new Intent(Login.this, MainActivity.class);
        startActivity(intentMainAct);
        finish();
    }

}
