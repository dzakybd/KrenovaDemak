package com.baddude.krenovademak.activity.edit;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baddude.krenovademak.AppController;
import com.baddude.krenovademak.GlideApp;
import com.baddude.krenovademak.PrefKeys;
import com.baddude.krenovademak.R;
import com.baddude.krenovademak.activity.MainActivity;
import com.baddude.krenovademak.activity.SigninActivity;
import com.baddude.krenovademak.activity.view.ViewImageActivity;
import com.baddude.krenovademak.model.AkunModel;
import com.bumptech.glide.Glide;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditAkunAdminActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView fieldemail;
    private TextView fieldktp;
    private TextView fieldnama;
    private TextView fieldalamat;
    private TextView fieldpekerjaan;
    private TextView fieldnohp;
    private ImageView foto;
    private Switch switchadmin,switchblock;
    SweetAlertDialog pDialog;
    AkunModel akunModel;
    public static final String TAG = EditAkunAdminActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getResources().getString(R.string.font_default))
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_edit_akun);

        akunModel = Parcels.unwrap(getIntent().getParcelableExtra(PrefKeys.akunmodel));

        foto = findViewById(R.id.foto);
        fieldnohp = findViewById(R.id.fieldnohp);
        fieldpekerjaan = findViewById(R.id.fieldpekerjaan);
        fieldalamat = findViewById(R.id.fieldalamat);
        fieldnama = findViewById(R.id.fieldnama);
        fieldktp = findViewById(R.id.fieldktp);
        fieldemail = findViewById(R.id.fieldemail);
        toolbar = findViewById(R.id.toolbar);
        switchadmin = findViewById(R.id.switchadmin);
        switchblock = findViewById(R.id.switchblock);

        GlideApp.with(this).load(akunModel.fotothumb).error(R.drawable.blankprofile).into(foto);
        fieldnohp.setText(akunModel.nohp);
        fieldpekerjaan.setText(akunModel.pekerjaan);
        fieldalamat.setText(akunModel.alamat);
        fieldemail.setText(akunModel.email);
        fieldktp.setText(akunModel.ktp);
        fieldnama.setText(akunModel.nama);
        switchadmin.setChecked(akunModel.isadmin);
        switchblock.setChecked(akunModel.isblock);

        settoolbar();
    }

    private void settoolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_action_back_arrow);//Image Icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void simpanclick(View view) {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected()) ubahakun();
            else nointalert();
        }else nointalert();
    }


    public void fotoclick(View view) {
        Intent i=new Intent(this, ViewImageActivity.class);
        i.putExtra(PrefKeys.uritype, PrefKeys.pp);
        i.putExtra(PrefKeys.foto,akunModel.foto);
        startActivity(i);
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


    private void ubahakun() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.primary));
        pDialog.setTitleText("Mengirim data");
        pDialog.setCancelable(false);
        pDialog.show();
        String url = PrefKeys.UBAHAKUNADMIN;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                       if (response.contentEquals("ubah_failed")) {
                            pDialog.dismiss();
                            new SweetAlertDialog(EditAkunAdminActivity.this)
                                    .setTitleText("Maaf")
                                    .setContentText("Proses gagal")
                                    .show();
                        } else {
                            pDialog.dismiss();
                            berhasilpopup();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                        new SweetAlertDialog(EditAkunAdminActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Koneksi bermasalah!")
                                .show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(PrefKeys.isadmin, switchadmin.isChecked()?"1":"0");
                params.put(PrefKeys.isblock, switchblock.isChecked()?"1":"0");
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, TAG);
    }


    private void berhasilpopup() {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Good!")
                .setContentText("Proses berhasil")
                .setConfirmText("Oke")
                .show();
    }

}
