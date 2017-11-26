package com.baddude.krenovademak.activity.edit;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.baddude.krenovademak.adapter.MediaRecyclerView;
import com.baddude.krenovademak.R;
import com.baddude.krenovademak.model.MediaModel;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditPengumumanActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText fieldjudul,fielddesc,fieldlink;
    private RecyclerView rcmedia;
    private FastItemAdapter<MediaRecyclerView> mFastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getResources().getString(R.string.font_default))
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_edit_pengumuman);
        rcmedia = findViewById(R.id.rcmedia);
        fielddesc = findViewById(R.id.fielddesc);
        fieldjudul = findViewById(R.id.fieldjudul);
        fieldlink = findViewById(R.id.fieldlink);
        toolbar = findViewById(R.id.toolbar);

        settoolbar();
        setlefticon();
        setlist();
        setdata();
    }

    private void setdata() {
        MediaModel mediaModel = null;
        for(int i=0;i<10;i++){
            mediaModel = new MediaModel();
            mediaModel.urlthumb="https://www.w3schools.com/w3css/img_fjords.jpg";
            mediaModel.url="https://www.w3schools.com/w3css/img_fjords.jpg";
            mediaModel.deskripsi="grrtrgegregre";
            mediaModel.fkid=i;
            mediaModel.idmedia=i;
            mediaModel.iskreasi=true;
            mediaModel.tanggalbuat="4t4g";
            mediaModel.tipe="file/pdf";
            mediaModel.ukuran="5mb";
            mFastAdapter.add(new MediaRecyclerView().create(mediaModel));
        }

    }

    private void setlist() {
        mFastAdapter = new FastItemAdapter<>();
        rcmedia.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        rcmedia.setItemAnimator(new SlideDownAlphaAnimator());
        rcmedia.getItemAnimator().setAddDuration(500);
        rcmedia.getItemAnimator().setRemoveDuration(500);
        rcmedia.setAdapter(mFastAdapter);
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

        fieldjudul.setCompoundDrawables(
                null, null, new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_format_title).color(getResources().getColor(R.color.colorTextPrimary)).sizeDp(15).paddingDp(1), null
        );

        fielddesc.setCompoundDrawables(
                null, null, new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_format_align_justify).color(getResources().getColor(R.color.colorTextPrimary)).sizeDp(15).paddingDp(1), null
        );

        fieldlink.setCompoundDrawables(
                null, null, new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_link).color(getResources().getColor(R.color.colorTextPrimary)).sizeDp(15).paddingDp(1), null
        );
    }

    public void tambahmediaclick(View view) {
        startActivity(new Intent(EditPengumumanActivity.this,EditMediaActivity.class));
    }

    public void simpanclick(View view) {
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

}