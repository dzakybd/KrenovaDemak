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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baddude.krenovademak.AppController;
import com.baddude.krenovademak.GlideApp;
import com.baddude.krenovademak.PrefKeys;
import com.baddude.krenovademak.R;
import com.baddude.krenovademak.activity.list.KreasikuActivity;
import com.baddude.krenovademak.activity.view.ViewImageActivity;
import com.baddude.krenovademak.adapter.MediaRecyclerView;
import com.baddude.krenovademak.model.KreasiModel;
import com.baddude.krenovademak.model.MediaModel;
import com.bumptech.glide.Glide;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
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

public class EditKreasiActivity extends AppCompatActivity implements ImagePickerCallback, Validator.ValidationListener {

    private Toolbar toolbar;
    private ImageView foto;
    @NotEmpty(message = "harap diisi")
    private EditText fieldjudul;
    @NotEmpty(message = "harap diisi")
    private EditText fielddesc;
    private RecyclerView rcmedia;
    private FastItemAdapter<MediaRecyclerView> mFastAdapter;
    private TextView fieldstatus;
    private Spinner spinnerkategori;
    private ImagePicker imagePicker;
    private String pickerPath;
    private CameraImagePicker cameraPicker;
    private String uribig, urismall;
    Validator validator;
    boolean isgantifoto = false;
    SweetAlertDialog pDialog;
    KreasiModel kreasiModel;
    MediaRecyclerView mediaRecyclerView;
    public static final String TAG = EditKreasiActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getResources().getString(R.string.font_default))
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_edit_kreasi);
        spinnerkategori = findViewById(R.id.spinnerkategori);
        fieldstatus = findViewById(R.id.fieldstatus);
        rcmedia = findViewById(R.id.rcmedia);
        fielddesc = findViewById(R.id.fielddesc);
        fieldjudul = findViewById(R.id.fieldjudul);
        foto = findViewById(R.id.foto);
        toolbar = findViewById(R.id.toolbar);

        kreasiModel = Parcels.unwrap(getIntent().getParcelableExtra(PrefKeys.kreasimodel));

        settoolbar();
        setlist();
        setdata();

        validator = new Validator(this);
        validator.setValidationListener(this);
        String[] kategori = PrefKeys.mapkategori.values().toArray(new String[0]);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,kategori);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerkategori.setAdapter(dataAdapter);
        spinnerkategori.setSelection(kreasiModel.kategori-1);

        fieldstatus.setText(PrefKeys.mapstatus.get(kreasiModel.status));
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
        if (isgantifoto) {
            i.putExtra(PrefKeys.uritype, PrefKeys.pp);
            i.putExtra(PrefKeys.foto, uribig);
        }else{
            i.putExtra(PrefKeys.uritype, PrefKeys.image);
            i.putExtra(PrefKeys.foto, kreasiModel.foto);
        }
        startActivity(i);
    }

    public void gantifotoutamaclick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ganti foto");
        builder.setMessage("Pilih dari mana foto anda");
        builder.setPositiveButton("Galeri",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        pickImageSingle();
                    }
                });
        builder.setNegativeButton("Kamera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        takePicture();
                    }
                });
        builder.show();
    }

    public void simpanclick(View view) {
        validator.validate();
    }

    public void pickImageSingle() {
        imagePicker = new ImagePicker(this);
        imagePicker.shouldGenerateMetadata(true);
        imagePicker.shouldGenerateThumbnails(true);
        imagePicker.setImagePickerCallback(this);
        imagePicker.pickImage();
    }

    public void takePicture() {
        cameraPicker = new CameraImagePicker(this);
        cameraPicker.shouldGenerateMetadata(true);
        cameraPicker.shouldGenerateThumbnails(true);
        cameraPicker.setImagePickerCallback(this);
        pickerPath = cameraPicker.pickImage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_OK) {
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                if (imagePicker == null) {
                    imagePicker = new ImagePicker(this);
                    imagePicker.setImagePickerCallback(this);
                    isgantifoto = true;
                }
                imagePicker.submit(data);
            } else if (requestCode == Picker.PICK_IMAGE_CAMERA) {
                if (cameraPicker == null) {
                    cameraPicker = new CameraImagePicker(this);
                    cameraPicker.setImagePickerCallback(this);
                    cameraPicker.reinitialize(pickerPath);
                    isgantifoto = true;
                }
                cameraPicker.submit(data);
            }
        }
    }

    @Override
    public void onImagesChosen(List<ChosenImage> images) {
        if (images.get(0).getThumbnailSmallPath() != null) {
            Glide.with(this).load(Uri.fromFile(new File(images.get(0).getThumbnailSmallPath()))).into(foto);
            uribig = images.get(0).getOriginalPath();
            urismall = images.get(0).getThumbnailSmallPath();
        }
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // You have to save path in case your activity is killed.
        // In such a scenario, you will need to re-initialize the CameraImagePicker
        outState.putString("picker_path", pickerPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // After Activity recreate, you need to re-intialize these
        // two values to be able to re-intialize CameraImagePicker
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("picker_path")) {
                pickerPath = savedInstanceState.getString("picker_path");
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onValidationSucceeded() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected()) ubahkreasi();
            else nointalert();
        } else nointalert();
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


    public void tambahmediaclick(View view) {
        Intent homeIntent = new Intent(EditKreasiActivity.this, EditMediaActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(PrefKeys.kreasimodel, Parcels.wrap(kreasiModel));
        homeIntent.putExtras(bundle);
        startActivity(homeIntent);
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
                new SweetAlertDialog(EditKreasiActivity.this, SweetAlertDialog.ERROR_TYPE)
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
        String url = PrefKeys.UBAHKREASI;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contentEquals("ubah_failed")) {
                            pDialog.dismiss();
                            new SweetAlertDialog(EditKreasiActivity.this)
                                    .setTitleText("Maaf")
                                    .setContentText("Proses gagal")
                                    .show();
                        } else {
                            try {
                                JSONObject jObject = new JSONObject(response);
                                kreasiModel = new KreasiModel();
                                kreasiModel.deskripsi = fielddesc.getText().toString();
                                kreasiModel.judul = fieldjudul.getText().toString();
                                kreasiModel.fkidakun = Prefs.getInt(PrefKeys.idakun,0);
                                kreasiModel.kategori=spinnerkategori.getSelectedItemPosition()+1;
                                kreasiModel.namaakun = Prefs.getString(PrefKeys.nama,"");
                                kreasiModel.tanggalbuat=jObject.getString(PrefKeys.tanggalbuat);
                                kreasiModel.foto=jObject.getString(PrefKeys.foto);
                                kreasiModel.fotothumb=jObject.getString(PrefKeys.fotothumb);
                                kreasiModel.idkreasi=Integer.parseInt(jObject.getString(PrefKeys.idkreasi));
                                kreasiModel.status=Integer.parseInt(jObject.getString(PrefKeys.status));
                                pDialog.dismiss();
                                berhasilpopup();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        new SweetAlertDialog(EditKreasiActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Koneksi bermasalah!")
                                .show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(PrefKeys.idkreasi, String.valueOf(kreasiModel.idkreasi));
                params.put(PrefKeys.judul, fieldjudul.getText().toString());
                params.put(PrefKeys.deskripsi, fielddesc.getText().toString());
                params.put(PrefKeys.kategori, PrefKeys.mapkategori.get(spinnerkategori.getSelectedItemPosition()+1));
                if (isgantifoto) {
                    params.put(PrefKeys.foto, getStringImage(Uri.fromFile(new File(uribig))));
                    params.put(PrefKeys.fotothumb, getStringImage(Uri.fromFile(new File(urismall))));
                }
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


    public String getStringImage(Uri link) {
        try {
            InputStream image_stream = getContentResolver().openInputStream(link);
            Bitmap bmp = BitmapFactory.decodeStream(image_stream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 75, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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
                Intent homeIntent = new Intent(EditKreasiActivity.this, EditMediaActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(PrefKeys.mediamodel, Parcels.wrap(item.mediaModel));
                homeIntent.putExtras(bundle);
                startActivity(homeIntent);
                return true;
            }
        });
    }

    public void hapuskreasiclick(View view) {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Anda yakin?")
                .setContentText("Menghapus kreasi ini")
                .setConfirmText("Ya")
                .setCancelText("Tidak")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        if (connectivity.getActiveNetworkInfo() != null) {
                            if (connectivity.getActiveNetworkInfo().isConnected()) hapuskreasi();
                            else nointalert();
                        }else nointalert();
                    }
                })
                .show();
    }

    private void hapuskreasi(){
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.primary));
        pDialog.setTitleText("Menghapus");
        pDialog.setCancelable(false);
        pDialog.show();
        String url = PrefKeys.HAPUSKREASI;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        if (response.contentEquals("hapus_failed")) {
                            new SweetAlertDialog(EditKreasiActivity.this)
                                    .setTitleText("Maaf")
                                    .setContentText("Proses gagal")
                                    .show();
                        } else {
                            new SweetAlertDialog(EditKreasiActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Terhapus")
                                    .setConfirmText("Oke")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            Prefs.clear();
                                            Intent homeIntent = new Intent(EditKreasiActivity.this, KreasikuActivity.class);
                                            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(homeIntent);
                                        }
                                    })
                                    .show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        new SweetAlertDialog(EditKreasiActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Koneksi bermasalah!")
                                .show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(PrefKeys.idkreasi, String.valueOf(kreasiModel.idkreasi));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, TAG);
    }
}
