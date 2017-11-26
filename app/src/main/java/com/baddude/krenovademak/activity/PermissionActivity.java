package com.baddude.krenovademak.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.baddude.krenovademak.PrefKeys;
import com.baddude.krenovademak.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PermissionActivity extends AppCompatActivity {
    private MultiplePermissionsListener allPermissionsListener;
    private PermissionRequestErrorListener errorListener;
    View contentView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getResources().getString(R.string.font_default))
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_permission);
        if(Prefs.getBoolean(PrefKeys.permissions,false)){
            moveon();
        }
        contentView=findViewById(android.R.id.content);
        createPermissionListeners();
    }

    private void moveon() {
        Intent i = new Intent(PermissionActivity.this, SigninActivity.class);
        startActivity(i);
        finish();
    }

    private void createPermissionListeners() {
        MultiplePermissionsListener feedbackViewMultiplePermissionListener = new SampleMultiplePermissionListener(this);

        allPermissionsListener =
                new CompositeMultiplePermissionsListener(feedbackViewMultiplePermissionListener,
                        SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(contentView,
                                R.string.all_permissions_denied_feedback)
                                .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                                .build());


        errorListener = new SampleErrorListener();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void izinclick(View view) {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(allPermissionsListener)
                .withErrorListener(errorListener)
                .check();
    }

    private class SampleMultiplePermissionListener implements MultiplePermissionsListener {
        public SampleMultiplePermissionListener(PermissionActivity permissionActivity) {
            
        }

        @Override
        public void onPermissionsChecked(MultiplePermissionsReport report) {
            if(report.areAllPermissionsGranted()){
                Prefs.putBoolean(PrefKeys.permissions,true);
                moveon();
            }
        }


        @Override
        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
            showPermissionRationale(token);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        public void showPermissionRationale(final PermissionToken token) {
            new AlertDialog.Builder(PermissionActivity.this).setTitle(R.string.permission_rationale_title)
                    .setMessage(R.string.permission_rationale_message)
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            token.cancelPermissionRequest();
                        }
                    })
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            token.continuePermissionRequest();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override public void onDismiss(DialogInterface dialog) {
                            token.cancelPermissionRequest();
                        }
                    })
                    .show();
        }
    }

    private class SampleErrorListener implements PermissionRequestErrorListener {
        @Override
        public void onError(DexterError error) {
            Log.e("Dexter", "There was an error: " + error.toString());
        }
    }
}
