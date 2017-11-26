package com.baddude.krenovademak.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baddude.krenovademak.AppController;
import com.baddude.krenovademak.PrefKeys;
import com.baddude.krenovademak.activity.edit.EditAkunActivity;
import com.baddude.krenovademak.activity.edit.EditKreasiActivity;
import com.baddude.krenovademak.activity.edit.EditKreasiAdminActivity;
import com.baddude.krenovademak.activity.list.KelolaAkunActivity;
import com.baddude.krenovademak.activity.list.KreasikuActivity;
import com.baddude.krenovademak.activity.list.PengumumanActivity;
import com.baddude.krenovademak.adapter.KreasiRecyclerView;
import com.baddude.krenovademak.R;
import com.baddude.krenovademak.model.KreasiModel;
import com.baddude.krenovademak.model.MediaModel;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    IProfile profile;
    boolean isadmin,isblock;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    private RecyclerView rckreasian;
    private boolean appBarExpanded = true;
    private Menu collapsedMenu;
    private FastItemAdapter<KreasiRecyclerView> mFastAdapter;
    FloatingActionButton fab;
    SweetAlertDialog pDialog;
    KreasiRecyclerView kreasiRecyclerView;
    public static final String TAG = MainActivity.class.getSimpleName();
    KreasiModel kreasiModel;
    MaterialDialog dialog;
    ImageView header;
    KreasiRecyclerView.ImageItemHeartClickEvent imageItemHeartClickEvent;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getResources().getString(R.string.font_default))
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_main);
        isadmin=Prefs.getBoolean(PrefKeys.isadmin,false);
        isblock=Prefs.getBoolean(PrefKeys.isblock,false);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbar);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        header=findViewById(R.id.header);
        rckreasian = findViewById(R.id.rckreasian);
        rckreasian.setLayoutManager(new LinearLayoutManager(this));
        rckreasian.setItemAnimator(new SlideDownAlphaAnimator());
        rckreasian.getItemAnimator().setAddDuration(500);
        rckreasian.getItemAnimator().setRemoveDuration(500);

        fab = findViewById(R.id.fab);
        setSupportActionBar(toolbar);
        setnavbar();
        if(isadmin)filterclickadmin(); else filterclick();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

    }



    private void settoolbar(int kategori) {

        bitmap = BitmapFactory.decodeResource(getResources(), PrefKeys.mapkategoriimage.get(kategori));
        header.setImageResource(PrefKeys.mapkategoriimage.get(kategori));
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @SuppressWarnings("ResourceType")
            @Override
            public void onGenerated(Palette palette) {
                int vibrantColor = palette.getVibrantColor(R.color.colorPrimary);
                collapsingToolbar.setContentScrimColor(vibrantColor);
                collapsingToolbar.setStatusBarScrimColor(R.color.colorPrimaryDark);
            }
        });
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //  Vertical offset == 0 indicates appBar is fully expanded.
                if (Math.abs(verticalOffset) > 200) {
                    appBarExpanded = false;
                    invalidateOptionsMenu();
                } else {
                    appBarExpanded = true;
                    invalidateOptionsMenu();
                }
            }
        });

    }

    private void setnavbar() {
        profile = new ProfileDrawerItem().withIdentifier(11).
                withName(Prefs.getString(PrefKeys.nama,"")).
                withEmail(Prefs.getString(PrefKeys.email,"")).
                withIcon(Prefs.getString(PrefKeys.foto,""));

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.gethelp)
                .addProfiles(
                        profile,
                        new ProfileSettingDrawerItem().withName("Ubah akun").withIcon(CommunityMaterial.Icon.cmd_account_edit).withIdentifier(12),
                        new ProfileSettingDrawerItem().withName("Keluar").withIcon(CommunityMaterial.Icon.cmd_logout).withIdentifier(13)
                ).withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        Intent i=null;
                        switch ((int)profile.getIdentifier()){
                            case 12:
                                i=new Intent(MainActivity.this,EditAkunActivity.class);
                                i.putExtra(PrefKeys.isdaftar,false);
                                startActivity(i);
                                break;
                            case 13:
                                Prefs.clear();
                                i=new Intent(MainActivity.this,SigninActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                                break;
                        }
                        return false;
                    }
                })
                .build();

//create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(21).withName((isadmin) ? "Kelola akun" : "Kreasiku").withIcon((isadmin) ? CommunityMaterial.Icon.cmd_view_list : CommunityMaterial.Icon.cmd_lightbulb_on),
                        new PrimaryDrawerItem().withIdentifier(22).withName("Pengumuman").withIcon(CommunityMaterial.Icon.cmd_newspaper),
                        new SectionDrawerItem().withName("Kategori kreasi"),
                        new SecondaryDrawerItem().withIdentifier(1).withName(PrefKeys.mapkategori.get(1)).withIcon(CommunityMaterial.Icon.cmd_corn),
                        new SecondaryDrawerItem().withIdentifier(2).withName(PrefKeys.mapkategori.get(2)).withIcon(CommunityMaterial.Icon.cmd_factory),
                        new SecondaryDrawerItem().withIdentifier(3).withName(PrefKeys.mapkategori.get(3)).withIcon(CommunityMaterial.Icon.cmd_pill),
                        new SecondaryDrawerItem().withIdentifier(4).withName(PrefKeys.mapkategori.get(4)).withIcon(CommunityMaterial.Icon.cmd_shopping),
                        new SecondaryDrawerItem().withIdentifier(5).withName(PrefKeys.mapkategori.get(5)).withIcon(CommunityMaterial.Icon.cmd_pine_tree),
                        new SecondaryDrawerItem().withIdentifier(6).withName(PrefKeys.mapkategori.get(6)).withIcon(CommunityMaterial.Icon.cmd_account_multiple),
                        new SecondaryDrawerItem().withIdentifier(7).withName(PrefKeys.mapkategori.get(7)).withIcon(CommunityMaterial.Icon.cmd_fish),
                        new SecondaryDrawerItem().withIdentifier(8).withName(PrefKeys.mapkategori.get(8)).withIcon(CommunityMaterial.Icon.cmd_school),
                        new SecondaryDrawerItem().withIdentifier(9).withName(PrefKeys.mapkategori.get(9)).withIcon(CommunityMaterial.Icon.cmd_flash),
                        new SecondaryDrawerItem().withIdentifier(10).withName(PrefKeys.mapkategori.get(10)).withIcon(CommunityMaterial.Icon.cmd_beach),
                        new SecondaryDrawerItem().withIdentifier(11).withName(PrefKeys.mapkategori.get(11)).withIcon(CommunityMaterial.Icon.cmd_domain)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Intent i=null;
                        int val = (int) drawerItem.getIdentifier();
                        switch (val){
                            case 21:
                                i=new Intent(MainActivity.this,(isadmin) ? KelolaAkunActivity.class : KreasikuActivity.class);
                                break;
                            case 22:
                                i=new Intent(MainActivity.this,PengumumanActivity.class);
                                break;
                            case 23:
                                i=new Intent(MainActivity.this,TentangActivity.class);
                                break;
                            default:
                                setlist(val);
                                settoolbar(val);
                                break;

                        }
                        if(isblock){
                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Maaf akun anda diblokir")
                                    .show();
                        }else if(i!=null){
                            startActivity(i);
                        }

                        return false;
                    }
                }).addStickyDrawerItems(new SecondaryDrawerItem().withIdentifier(23).withName("Tentang"))
                .build();
        result.setSelection(1);
        settoolbar(1);
    }

    private void setdata(final int kategori) {
        String url = PrefKeys.GETKREASIMAIN;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray;
                        JSONObject jObject;
                        try {
                            jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jObject = (JSONObject) jsonArray.get(i);
                                kreasiModel = new KreasiModel();
                                kreasiModel.deskripsi=jObject.getString(PrefKeys.deskripsi);
                                kreasiModel.namaakun=jObject.getString(PrefKeys.deskripsi);
                                kreasiModel.deskripsi=jObject.getString(PrefKeys.deskripsi);
                                kreasiModel.deskripsi=jObject.getString(PrefKeys.deskripsi);
                                kreasiModel.deskripsi=jObject.getString(PrefKeys.deskripsi);
                                kreasiModel.deskripsi=jObject.getString(PrefKeys.deskripsi);
                                mFastAdapter.add(kreasiRecyclerView.create(kreasiModel));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Koneksi bermasalah!")
                                .show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(PrefKeys.kategori, String.valueOf(kategori));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, TAG);
    }

    private boolean checkint(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected()) return true;
            else {
                nointalert();
                return false;
            }
        }else {
            nointalert();
            return false;
        }
    }

    private void setlist(int kategori) {
        mFastAdapter = new FastItemAdapter<>();
        kreasiRecyclerView = new KreasiRecyclerView();
        imageItemHeartClickEvent = new KreasiRecyclerView.ImageItemHeartClickEvent();
        mFastAdapter.withEventHook(imageItemHeartClickEvent);
        if(checkint())setdata(kategori);
        rckreasian.setAdapter(mFastAdapter);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (collapsedMenu != null
                && (!appBarExpanded || collapsedMenu.size() != 0)) {
            //collapsed
            collapsedMenu.add("Filter")
                    .setIcon(R.drawable.ic_action_filter)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        } else {
            //expanded
        }
        return super.onPrepareOptionsMenu(collapsedMenu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        collapsedMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        if (item.getTitle() == "Filter") {
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void filterclick() {
        dialog= new MaterialDialog.Builder(this)
                .title("Filter kreasi")
                .customView(R.layout.filter_kreasi, true)
                .positiveText("Filter")
                .build();

        EditText fieldjudul = dialog.getCustomView().findViewById(R.id.fieldjudul);
        Spinner spinnerurut = dialog.getCustomView().findViewById(R.id.spinnerurut);
        Spinner spinnerstatus = dialog.getCustomView().findViewById(R.id.spinnerstatus);

    }

    private void filterclickadmin() {
        dialog= new MaterialDialog.Builder(this)
                .title("Filter kreasi")
                .customView(R.layout.filter_kreasi_admin, true)
                .positiveText("Filter")
                .build();
        EditText fieldjudul = dialog.getCustomView().findViewById(R.id.fieldjudul);
        Spinner spinnerurut = dialog.getCustomView().findViewById(R.id.spinnerurut);
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
}
