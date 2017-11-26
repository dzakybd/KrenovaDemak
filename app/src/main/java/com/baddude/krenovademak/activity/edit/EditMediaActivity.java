package com.baddude.krenovademak.activity.edit;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baddude.krenovademak.R;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditMediaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView media;
    private EditText fielddesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getResources().getString(R.string.font_default))
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_edit_media);
        fielddesc = findViewById(R.id.fielddesc);
        toolbar = findViewById(R.id.toolbar);
        media = findViewById(R.id.media);

        settoolbar();
        setlefticon();
    }

    public void pilihmediaclick(View view) {
    }

    public void mediaclick(View view) {
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

    private void setlefticon() {

        fielddesc.setCompoundDrawables(
                null, null, new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_format_align_justify).color(getResources().getColor(R.color.colorTextPrimary)).sizeDp(15).paddingDp(1), null
        );
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
}
