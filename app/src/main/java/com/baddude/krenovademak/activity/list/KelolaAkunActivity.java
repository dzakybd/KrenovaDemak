package com.baddude.krenovademak.activity.list;

import android.content.Context;
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

import com.baddude.krenovademak.adapter.AkunRecyclerView;
import com.baddude.krenovademak.R;
import com.baddude.krenovademak.model.AkunModel;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class KelolaAkunActivity extends AppCompatActivity {
    private RecyclerView rcakun;
    private FastItemAdapter<AkunRecyclerView> mFastAdapter;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getResources().getString(R.string.font_default))
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_kelola_akun);
        rcakun = findViewById(R.id.rcakun);
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
    }

    private void setdata() {
        AkunModel akunModel = null;
        for(int i=0;i<10;i++){
            akunModel = new AkunModel();
            akunModel.idakun=i;
            akunModel.alamat="ew";
            akunModel.email="abc@def.com";
            akunModel.foto="https://www.w3schools.com/w3css/img_fjords.jpg";
            akunModel.isadmin=false;
            akunModel.isblock=false;
            akunModel.ktp="12fdff";
            akunModel.nama="zaki";
            akunModel.nohp="32442";
            akunModel.password="erew";
            akunModel.pekerjaan="ewewf2";
            akunModel.tanggalbuat="3fkffk";
            mFastAdapter.add(new AkunRecyclerView().create(akunModel));
        }

    }

    private void setlist() {
        mFastAdapter = new FastItemAdapter<>();
        rcakun.setLayoutManager(new LinearLayoutManager(this));
        rcakun.setItemAnimator(new SlideDownAlphaAnimator());
        rcakun.getItemAnimator().setAddDuration(500);
        rcakun.getItemAnimator().setRemoveDuration(500);
        rcakun.setAdapter(mFastAdapter);
    }

}

