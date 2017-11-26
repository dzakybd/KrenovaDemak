package com.baddude.krenovademak.activity.list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.baddude.krenovademak.activity.edit.EditKreasiActivity;
import com.baddude.krenovademak.adapter.KreasiRecyclerView;
import com.baddude.krenovademak.R;
import com.baddude.krenovademak.model.KreasiModel;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class KreasikuActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rckreasiku;
    private FastItemAdapter<KreasiRecyclerView> mFastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getResources().getString(R.string.font_default))
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_kreasiku);
        rckreasiku = findViewById(R.id.rckreasiku);
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

    private void setdata() {
        KreasiModel kreasiModel = null;
        for(int i=0;i<10;i++){
            kreasiModel = new KreasiModel();
            kreasiModel.idkreasi=i;
            kreasiModel.deskripsi="Coba";
            kreasiModel.judul="gwergrreg";
            kreasiModel.fkidakun=i;
            kreasiModel.namaakun="yayak";
            kreasiModel.foto="https://www.w3schools.com/w3css/img_fjords.jpg";
            kreasiModel.status=1;
            kreasiModel.isulike=false;
            kreasiModel.likes=55;
            kreasiModel.mediaModels =null;
            kreasiModel.tanggalbuat="12/12";
            mFastAdapter.add(new KreasiRecyclerView().create(kreasiModel));
        }

    }

    private void setlist() {
        mFastAdapter = new FastItemAdapter<>();
        rckreasiku.setLayoutManager(new LinearLayoutManager(this));
        rckreasiku.setItemAnimator(new SlideDownAlphaAnimator());
        rckreasiku.getItemAnimator().setAddDuration(500);
        rckreasiku.getItemAnimator().setRemoveDuration(500);
        rckreasiku.setAdapter(mFastAdapter);
        mFastAdapter.withEventHook(new KreasiRecyclerView.ImageItemHeartClickEvent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.kreasiku, menu);
        menu.findItem(R.id.item_add).setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_plus).color(Color.WHITE).actionBar());
        menu.findItem(R.id.item_filter).setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_filter).color(Color.WHITE).actionBar());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.item_add:
                tambahclick();
                break;
            case R.id.item_filter:
                filterclick();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void filterclick() {
    }

    private void tambahclick() {
        startActivity(new Intent(this,EditKreasiActivity.class));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
