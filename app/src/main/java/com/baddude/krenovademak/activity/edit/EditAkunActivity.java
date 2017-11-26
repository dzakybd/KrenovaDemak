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
import android.widget.EditText;
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

public class EditAkunActivity extends AppCompatActivity implements ImagePickerCallback, Validator.ValidationListener {

    private Toolbar toolbar;
    @NotEmpty(message = "harap diisi")
    @Email(message = "email salah")
    private EditText fieldemail;
    @NotEmpty(message = "harap diisi")
    @Password(min = 6,message = "karakter minimal 6")
    private EditText fieldpass1;
    @NotEmpty(message = "harap diisi")
    @ConfirmPassword(message = "password tidak cocok")
    private EditText fieldpass2;
    @NotEmpty(message = "harap diisi")
    @Password(min = 16, message = "ktp terdiri 16 digit")
    private EditText fieldktp;
    @NotEmpty(message = "harap diisi")
    private EditText fieldnama;
    @NotEmpty(message = "harap diisi")
    private EditText fieldalamat;
    @NotEmpty(message = "harap diisi")
    private EditText fieldpekerjaan;
    @NotEmpty(message = "harap diisi")
    private EditText fieldnohp;
    private TextView hapusakunbutton;
    private ImagePicker imagePicker;
    private String pickerPath;
    private CameraImagePicker cameraPicker;
    private ImageView foto;
    private String uribig,urismall;
    Validator validator;
    SweetAlertDialog pDialog;
    int idakun;
    boolean isdaftar;
    boolean isgantifoto=false;
    public static final String TAG = EditAkunActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getResources().getString(R.string.font_default))
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_edit_akun);
        validator = new Validator(this);
        validator.setValidationListener(this);

        foto = findViewById(R.id.foto);
        fieldnohp = findViewById(R.id.fieldnohp);
        fieldpekerjaan = findViewById(R.id.fieldpekerjaan);
        fieldalamat = findViewById(R.id.fieldalamat);
        fieldnama = findViewById(R.id.fieldnama);
        fieldktp = findViewById(R.id.fieldktp);
        fieldpass2 = findViewById(R.id.fieldpass2);
        fieldpass1 = findViewById(R.id.fieldpass1);
        fieldemail = findViewById(R.id.fieldemail);
        hapusakunbutton = findViewById(R.id.hapusakun);
        toolbar = findViewById(R.id.toolbar);


        isdaftar=getIntent().getExtras().getBoolean(PrefKeys.isdaftar);
        if(isdaftar){
            hapusakunbutton.setVisibility(View.GONE);
        } else{
            GlideApp.with(this).load(Prefs.getString(PrefKeys.fotothumb,"")).error(R.drawable.blankprofile).into(foto);
            fieldnohp.setText(Prefs.getString(PrefKeys.nohp,""));
            fieldpekerjaan.setText(Prefs.getString(PrefKeys.pekerjaan,""));
            fieldalamat.setText(Prefs.getString(PrefKeys.alamat,""));
            fieldemail.setText(Prefs.getString(PrefKeys.email,""));
            fieldktp.setText(Prefs.getString(PrefKeys.ktp,""));
            fieldnama.setText(Prefs.getString(PrefKeys.nama,""));
            fieldpass1.setText(Prefs.getString(PrefKeys.password,""));
            fieldpass2.setText(Prefs.getString(PrefKeys.password,""));
            idakun=Prefs.getInt(PrefKeys.idakun,0);
        }

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
        validator.validate();
    }


    public void gantifotoclick(View view) {
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
                    isgantifoto=true;
                }
                imagePicker.submit(data);
            } else if (requestCode == Picker.PICK_IMAGE_CAMERA) {
                if (cameraPicker == null) {
                    cameraPicker = new CameraImagePicker(this);
                    cameraPicker.setImagePickerCallback(this);
                    cameraPicker.reinitialize(pickerPath);
                    isgantifoto=true;
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


    public void fotoclick(View view) {
        Intent i=new Intent(this, ViewImageActivity.class);
        if(isgantifoto){
            i.putExtra(PrefKeys.foto,uribig);
        }
        startActivity(i);
    }

    @Override
    public void onValidationSucceeded() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected()) posting();
            else nointalert();
        }else nointalert();
    }

    private void posting() {
        if(isdaftar)daftarakun();
        else ubahakun();
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

    private void daftarakun() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.primary));
        pDialog.setTitleText("Mengirim data");
        pDialog.setCancelable(false);
        pDialog.show();
        String url = PrefKeys.DAFTARAKUN;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        if (response.contentEquals("daftar_same_email")) {
                            pDialog.dismiss();
                            new SweetAlertDialog(EditAkunActivity.this)
                                    .setTitleText("Maaf")
                                    .setContentText("Email ada yang sama")
                                    .show();
                        } else if (response.contentEquals("daftar_failed")) {
                            pDialog.dismiss();
                            new SweetAlertDialog(EditAkunActivity.this)
                                    .setTitleText("Maaf")
                                    .setContentText("Proses gagal")
                                    .show();
                        } else {
                            try {
                                JSONObject jObject = new JSONObject(response);
                                idakun=Integer.parseInt(jObject.getString(PrefKeys.idakun));
                                Prefs.putInt(PrefKeys.idakun, idakun);
                                Prefs.putBoolean(PrefKeys.isblock, jObject.getString(PrefKeys.isblock).contentEquals("1") );
                                Prefs.putBoolean(PrefKeys.isadmin, jObject.getString(PrefKeys.isadmin).contentEquals("1") );
                                Prefs.putString(PrefKeys.tanggalbuat, jObject.getString(PrefKeys.tanggalbuat));

                                Prefs.putString(PrefKeys.email, fieldemail.getText().toString());
                                Prefs.putString(PrefKeys.nohp, fieldnohp.getText().toString());
                                Prefs.putString(PrefKeys.nama, fieldnama.getText().toString());
                                Prefs.putString(PrefKeys.ktp, fieldktp.getText().toString());
                                Prefs.putString(PrefKeys.password, fieldpass1.getText().toString());
                                Prefs.putString(PrefKeys.alamat, fieldalamat.getText().toString());
                                Prefs.putString(PrefKeys.pekerjaan, fieldpekerjaan.getText().toString());
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
//                        Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                        new SweetAlertDialog(EditAkunActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Koneksi bermasalah!")
                                .show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(PrefKeys.alamat, fieldalamat.getText().toString());
                params.put(PrefKeys.email, fieldemail.getText().toString());
                params.put(PrefKeys.ktp, fieldktp.getText().toString());
                params.put(PrefKeys.nama, fieldnama.getText().toString());
                params.put(PrefKeys.nohp, fieldnohp.getText().toString());
                params.put(PrefKeys.password, fieldpass1.getText().toString());
                params.put(PrefKeys.pekerjaan, fieldpekerjaan.getText().toString());
                if(isgantifoto){
                    params.put(PrefKeys.foto, getStringImage(Uri.fromFile(new File(uribig))));
                    params.put(PrefKeys.fotothumb, getStringImage(Uri.fromFile(new File(urismall))));
                }
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, TAG);
    }

    private void ubahakun() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.primary));
        pDialog.setTitleText("Mengirim data");
        pDialog.setCancelable(false);
        pDialog.show();
        String url = PrefKeys.UBAHAKUN;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        if (response.contentEquals("ubah_failed")) {
                            pDialog.dismiss();
                            new SweetAlertDialog(EditAkunActivity.this)
                                    .setTitleText("Maaf")
                                    .setContentText("Proses gagal")
                                    .show();
                        } else {
                            try {
                                JSONObject jObject = new JSONObject(response);
                                Prefs.putBoolean(PrefKeys.isblock, jObject.getString(PrefKeys.isblock).contentEquals("1") );
                                Prefs.putBoolean(PrefKeys.isadmin, jObject.getString(PrefKeys.isadmin).contentEquals("1") );

                                Prefs.putString(PrefKeys.email, fieldemail.getText().toString());
                                Prefs.putString(PrefKeys.nohp, fieldnohp.getText().toString());
                                Prefs.putString(PrefKeys.nama, fieldnama.getText().toString());
                                Prefs.putString(PrefKeys.ktp, fieldktp.getText().toString());
                                Prefs.putString(PrefKeys.password, fieldpass1.getText().toString());
                                Prefs.putString(PrefKeys.alamat, fieldalamat.getText().toString());
                                Prefs.putString(PrefKeys.pekerjaan, fieldpekerjaan.getText().toString());
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
//                        Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                        new SweetAlertDialog(EditAkunActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Koneksi bermasalah!")
                                .show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if(!fieldalamat.getText().toString().contentEquals(Prefs.getString(PrefKeys.alamat,""))){
                    params.put(PrefKeys.alamat, fieldalamat.getText().toString());
                }
                if(!fieldemail.getText().toString().contentEquals(Prefs.getString(PrefKeys.email,""))){
                    params.put(PrefKeys.email, fieldemail.getText().toString());
                }
                if(!fieldktp.getText().toString().contentEquals(Prefs.getString(PrefKeys.ktp,""))){
                    params.put(PrefKeys.ktp, fieldktp.getText().toString());
                }
                if(!fieldnama.getText().toString().contentEquals(Prefs.getString(PrefKeys.nama,""))){
                    params.put(PrefKeys.nama, fieldnama.getText().toString());
                }
                if(!fieldpass1.getText().toString().contentEquals(Prefs.getString(PrefKeys.password,""))){
                    params.put(PrefKeys.password, fieldpass1.getText().toString());
                }
                if(!fieldpekerjaan.getText().toString().contentEquals(Prefs.getString(PrefKeys.pekerjaan,""))){
                    params.put(PrefKeys.pekerjaan, fieldpekerjaan.getText().toString());
                }
                if(isgantifoto){
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
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        if(isdaftar){
                            Intent homeIntent = new Intent(EditAkunActivity.this, MainActivity.class);
                            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(homeIntent);
                        }
                    }
                })
                .show();
    }


    public String getStringImage(Uri link) {
        try {
            InputStream image_stream = getContentResolver().openInputStream(link);
            Bitmap bmp= BitmapFactory.decodeStream(image_stream );
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 75, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void hapusakunclick(View view) {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Anda yakin?")
                .setContentText("Menghapus akun dan seluruh kreasi Anda")
                .setConfirmText("Ya")
                .setCancelText("Tidak")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        if (connectivity.getActiveNetworkInfo() != null) {
                            if (connectivity.getActiveNetworkInfo().isConnected()) hapusakun();
                            else nointalert();
                        }else nointalert();
                    }
                })
                .show();
    }

    private void hapusakun(){
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.primary));
        pDialog.setTitleText("Menghapus");
        pDialog.setCancelable(false);
        pDialog.show();
        String url = PrefKeys.HAPUSAKUN;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        if (response.contentEquals("hapus_failed")) {
                            new SweetAlertDialog(EditAkunActivity.this)
                                    .setTitleText("Maaf")
                                    .setContentText("Proses gagal")
                                    .show();
                        } else {
                            new SweetAlertDialog(EditAkunActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Terhapus")
                                    .setConfirmText("Oke")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            Prefs.clear();
                                            Intent homeIntent = new Intent(EditAkunActivity.this, SigninActivity.class);
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
                        new SweetAlertDialog(EditAkunActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Koneksi bermasalah!")
                                .show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(PrefKeys.idakun, String.valueOf(idakun));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, TAG);
    }
}
