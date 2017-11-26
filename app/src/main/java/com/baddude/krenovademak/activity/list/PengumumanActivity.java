package com.baddude.krenovademak.activity.list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.baddude.krenovademak.activity.edit.EditPengumumanActivity;
import com.baddude.krenovademak.adapter.PengumumanRecyclerView;
import com.baddude.krenovademak.R;
import com.baddude.krenovademak.model.PengumumanModel;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PengumumanActivity extends AppCompatActivity {

    private RecyclerView rcpengumuman;
    private FastItemAdapter<PengumumanRecyclerView> mFastAdapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getResources().getString(R.string.font_default))
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_pengumuman);
        rcpengumuman = findViewById(R.id.rcpengumuman);
        toolbar = findViewById(R.id.toolbar);

        settoolbar();
        setlist();
        setdata();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pengumuman, menu);
        menu.findItem(R.id.item_filter).setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_filter).color(Color.WHITE).actionBar());
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.item_filter:
                filterclick();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void filterclick() {
        startActivity(new Intent(PengumumanActivity.this,EditPengumumanActivity.class));
    }

    private void setdata() {
        PengumumanModel pengumumanModel = null;
        for(int i=0;i<10;i++){
            pengumumanModel = new PengumumanModel();
            pengumumanModel.idpengumuman=i;
            pengumumanModel.deskripsi="It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).";
            pengumumanModel.judul="coba";
            pengumumanModel.link="ewfefw";
            pengumumanModel.mediaModels=null;
            pengumumanModel.tanggalbuat="12/12";
            mFastAdapter.add(new PengumumanRecyclerView().create(pengumumanModel));
        }

    }

    private void setlist() {
        mFastAdapter = new FastItemAdapter<>();
        rcpengumuman.setLayoutManager(new LinearLayoutManager(this));
        rcpengumuman.setItemAnimator(new SlideDownAlphaAnimator());
        rcpengumuman.getItemAnimator().setAddDuration(500);
        rcpengumuman.getItemAnimator().setRemoveDuration(500);
        rcpengumuman.setAdapter(mFastAdapter);
    }

}
