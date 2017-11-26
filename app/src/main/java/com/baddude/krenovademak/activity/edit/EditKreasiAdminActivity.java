package com.baddude.krenovademak.activity.edit;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baddude.krenovademak.AppController;
import com.baddude.krenovademak.GlideApp;
import com.baddude.krenovademak.PrefKeys;
import com.baddude.krenovademak.R;
import com.baddude.krenovademak.activity.view.ViewImageActivity;
import com.baddude.krenovademak.activity.view.ViewMediaActivity;
import com.baddude.krenovademak.adapter.MediaRecyclerView;
import com.baddude.krenovademak.model.KreasiModel;
import com.baddude.krenovademak.model.MediaModel;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditKreasiAdminActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView foto;
    private TextView fieldjudul,fielddesc, fieldkategori;
    private Spinner spinnerstatus;
    private RecyclerView rcmedia;
    MediaRecyclerView mediaRecyclerView;
    SweetAlertDialog pDialog;
    private FastItemAdapter<MediaRecyclerView> mFastAdapter;
    public static final String TAG = EditKreasiAdminActivity.class.getSimpleName();
    KreasiModel kreasiModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getResources().getString(R.string.font_default))
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_edit_kreasi);
        rcmedia = findViewById(R.id.rcmedia);
        fielddesc = findViewById(R.id.fielddesc);
        fieldjudul = findViewById(R.id.fieldjudul);
        fieldkategori = findViewById(R.id.fieldkategori);
        spinnerstatus = findViewById(R.id.spinnerstatus);
        foto = findViewById(R.id.foto);
        toolbar = findViewById(R.id.toolbar);

        kreasiModel = Parcels.unwrap(getIntent().getParcelableExtra(PrefKeys.kreasimodel));

        settoolbar();
        setlist();
        setdata();

        String[] status = PrefKeys.mapstatus.values().toArray(new String[0]);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,status);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerstatus.setAdapter(dataAdapter);
        spinnerstatus.setSelection(kreasiModel.status-1);

        fieldkategori.setText(PrefKeys.mapkategori.get(kreasiModel.kategori));
        fieldjudul.setText(kreasiModel.judul);
        fielddesc.setText(kreasiModel.deskripsi);
        GlideApp.with(this).load(kreasiModel.fotothumb).error(R.drawable.blankprofile).into(foto);

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


    public void fotoutamaclick(View view) {
        Intent i = new Intent(this, ViewImageActivity.class);
        i.putExtra(PrefKeys.uritype, PrefKeys.image);
        i.putExtra(PrefKeys.foto, kreasiModel.foto);
        startActivity(i);
    }


    public void simpanclick(View view) {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected()) ubahkreasi();
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

    private void setdata() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.primary));
        pDialog.setTitleText("Memuat data");
        pDialog.setCancelable(false);
        pDialog.show();
        String url=PrefKeys.UBAHKREASI;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MediaModel mediaModel;
                        JSONArray jsonArray;
                        JSONObject jObject;
                        try {
                            jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jObject = (JSONObject) jsonArray.get(i);
                                mediaModel = new MediaModel();
                                mediaModel.deskripsi=jObject.getString(PrefKeys.deskripsi);
                                mediaModel.fkid=Integer.parseInt(jObject.getString(PrefKeys.fkid));
                                mediaModel.idmedia=Integer.parseInt(jObject.getString(PrefKeys.idmedia));
                                mediaModel.iskreasi=jObject.getString(PrefKeys.iskreasi).contentEquals("1");
                                mediaModel.tanggalbuat=jObject.getString(PrefKeys.tanggalbuat);
                                mediaModel.tipe=jObject.getString(PrefKeys.tipe);
                                mediaModel.ukuran=jObject.getString(PrefKeys.ukuran);
                                mediaModel.url=jObject.getString(PrefKeys.url);
                                mediaModel.urlthumb=jObject.getString(PrefKeys.urlthumb);
                                mFastAdapter.add(mediaRecyclerView.create(mediaModel));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                new SweetAlertDialog(EditKreasiAdminActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Koneksi bermasalah!")
                        .show();
            }
        });
        AppController.getInstance().addToRequestQueue(stringRequest, TAG);
    }


    private void ubahkreasi() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.primary));
        pDialog.setTitleText("Mengirim data");
        pDialog.setCancelable(false);
        pDialog.show();
        String url = PrefKeys.UBAHKREASIADMIN;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contentEquals("ubah_failed")) {
                            pDialog.dismiss();
                            new SweetAlertDialog(EditKreasiAdminActivity.this)
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
                        pDialog.dismiss();
                        new SweetAlertDialog(EditKreasiAdminActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Koneksi bermasalah!")
                                .show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(PrefKeys.idkreasi, String.valueOf(kreasiModel.idkreasi));
                params.put(PrefKeys.status, PrefKeys.mapstatus.get(spinnerstatus.getSelectedItemPosition()+1));
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
    private void setlist() {
        mFastAdapter = new FastItemAdapter<>();
        rcmedia.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcmedia.setItemAnimator(new SlideDownAlphaAnimator());
        rcmedia.getItemAnimator().setAddDuration(500);
        rcmedia.getItemAnimator().setRemoveDuration(500);
        rcmedia.setAdapter(mFastAdapter);

        mFastAdapter.withOnClickListener(new OnClickListener<MediaRecyclerView>() {
            @Override
            public boolean onClick(View v, IAdapter<MediaRecyclerView> adapter, MediaRecyclerView item, int position) {
                Intent homeIntent = new Intent(EditKreasiAdminActivity.this, ViewMediaActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(PrefKeys.mediamodel, Parcels.wrap(item.mediaModel));
                homeIntent.putExtras(bundle);
                startActivity(homeIntent);
                return true;
            }
        });
    }
}
