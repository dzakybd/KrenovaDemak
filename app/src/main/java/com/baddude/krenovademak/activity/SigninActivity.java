package com.baddude.krenovademak.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baddude.krenovademak.AppController;
import com.baddude.krenovademak.PrefKeys;
import com.baddude.krenovademak.R;
import com.baddude.krenovademak.activity.edit.EditAkunActivity;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SigninActivity extends AppCompatActivity implements Validator.ValidationListener {

    @NotEmpty(message = "harap diisi")
    @Password(min = 6,message = "karakter minimal 6")
    EditText fieldpassword;

    @NotEmpty(message = "harap diisi")
    @Email(message = "email salah")
    EditText fieldemail;
    Validator validator;
    SweetAlertDialog pDialog;
    public static final String TAG = SigninActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!Prefs.getString(PrefKeys.email, "").isEmpty()) {
            move();
        }
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getResources().getString(R.string.font_default))
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_signin);
        fieldpassword = findViewById(R.id.fieldpassword);
        fieldemail = findViewById(R.id.fieldemail);

        validator = new Validator(this);
        validator.setValidationListener(this);

    }

    private void move() {
        Intent i = new Intent(SigninActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void masukclick(View view) {
        validator.validate();
    }


    public void daftarclick(View view) {
        Intent i = new Intent(SigninActivity.this, EditAkunActivity.class);
        i.putExtra(PrefKeys.isdaftar,true);
        startActivity(i);
    }

    private void login() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.primary));
        pDialog.setTitleText("Masuk");
        pDialog.setCancelable(false);
        pDialog.show();
        String url = PrefKeys.LOGIN;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        try {
                            JSONObject jObject = new JSONObject(response);
                            if(jObject.getString(PrefKeys.idakun).contentEquals("null")) {
                                new SweetAlertDialog(SigninActivity.this)
                                        .setTitleText("Login salah!")
                                        .show();
                            }else{
                                Prefs.putInt(PrefKeys.idakun, Integer.parseInt(jObject.getString(PrefKeys.idakun)));
                                Prefs.putString(PrefKeys.email, jObject.getString(PrefKeys.email));
                                Prefs.putBoolean(PrefKeys.isblock, jObject.getString(PrefKeys.isblock).contentEquals("1") );
                                Prefs.putBoolean(PrefKeys.isadmin, jObject.getString(PrefKeys.isadmin).contentEquals("1") );
                                Prefs.putString(PrefKeys.nohp, jObject.getString(PrefKeys.nohp));
                                Prefs.putString(PrefKeys.nama, jObject.getString(PrefKeys.nama));
                                Prefs.putString(PrefKeys.foto, jObject.getString(PrefKeys.foto));
                                Prefs.putString(PrefKeys.ktp, jObject.getString(PrefKeys.ktp));
                                Prefs.putString(PrefKeys.password, jObject.getString(PrefKeys.password));
                                Prefs.putString(PrefKeys.alamat, jObject.getString(PrefKeys.alamat));
                                Prefs.putString(PrefKeys.pekerjaan, jObject.getString(PrefKeys.pekerjaan));
                                Prefs.putString(PrefKeys.tanggalbuat, jObject.getString(PrefKeys.tanggalbuat));
                                Prefs.putString(PrefKeys.fotothumb, jObject.getString(PrefKeys.fotothumb));
                                move();
                            }
                        } catch (JSONException e) {
                            Log.d(TAG,e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                        new SweetAlertDialog(SigninActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Maaf")
                                .setContentText("Koneksi bermasalah!")
                                .show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put(PrefKeys.email,fieldemail.getText().toString());
                params.put(PrefKeys.password,fieldpassword.getText().toString());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, TAG);
    }

    @Override
    public void onValidationSucceeded() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected()) login();
            else nointalert();
        }else nointalert();
    }

    private void nointalert() {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Internet mati")
                .setContentText("Mohon menghidupkan internet")
                .setConfirmText("Ya")
                .setCancelText("Tidak")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                        startActivity(intent);
                    }
                })
                .show();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

