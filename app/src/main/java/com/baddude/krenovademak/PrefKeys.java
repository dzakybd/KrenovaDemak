package com.baddude.krenovademak;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zaki on 21/11/17.
 */

 public class PrefKeys {

    public static final String idakun="idakun";
    public static final String nohp="nohp";
    public static final String nama="nama";
    public static final String foto="foto";
    public static final String ktp="ktp";
    public static final String email="email";
    public static final String password="password";
    public static final String alamat="alamat";
    public static final String pekerjaan="pekerjaan";
    public static final String tanggalbuat="tanggalbuat";
    public static final String isblock="isblock";
    public static final String isadmin="isadmin";
    public static final String fotothumb = "fotothumb";
    public static final String isdaftar="isdaftar";

    public static final String idkreasi="idkreasi";
    public static final String judul="judul";
    public static final String deskripsi="deskripsi";
    public static final String status="status";
    public static final String kategori="kategori";
    public static final String fkidakun="fkidakun";

    public static final String fkidkreasi="fkidkreasi";
    public static final String idmedia="fkidakun";
    public static final String fkid="fkid";
    public static final String iskreasi="iskreasi";
    public static final String url="url";
    public static final String urlthumb="urlthumb";
    public static final String ukuran="ukuran";
    public static final String tipe="tipe";

    public static final String idpengumuman="idpengumuman";
    public static final String link="link";

   public static final String uritype="uritype";
   public static final String video="video";
   public static final String image="image";
   public static final String pp="pp";
   public static final String file="file";

    public static final String permissions="permissions";
    public static final String akunmodel = "akunmodel";
    public static final String kreasimodel = "kreasimodel";
    public static final String mediamodel = "mediamodel";
    public static final String pengumumanmodel = "pengumumanmodel";

    public static final String BASE_URL = "http://128.199.177.26/krenova/";
    public static final String LOGIN = BASE_URL+"login.php";
    public static final String DAFTARAKUN = BASE_URL+"daftarakun.php";
    public static final String UBAHAKUN = BASE_URL+"ubahakun.php";
    public static final String UBAHAKUNADMIN = BASE_URL+"ubahakunadmin.php";
    public static final String HAPUSAKUN = BASE_URL+"hapusakun.php";
    public static final String MEDIAUPLOAD = BASE_URL+"mediaupload.php";
    public static final String PPUPLOAD = BASE_URL+"ppupload.php";
    public static final String BUATKREASI = BASE_URL+"buatkreasi.php";
    public static final String UBAHKREASI = BASE_URL+"ubahkreasi.php";
    public static final String UBAHKREASIADMIN = BASE_URL+"ubahkreasiadmin.php";
    public static final String HAPUSKREASI = BASE_URL+"hapuskreasi.php";
    public static final String GETKREASIMAIN = BASE_URL+"getkreasimain.php";

    public static final Map<Integer, String> mapkategori;
    static
    {
        mapkategori = new HashMap<Integer, String>();
        mapkategori.put(1, "Agribisnis dan pangan");
        mapkategori.put(2, "Rekayasa teknologi dan manufaktur");
        mapkategori.put(3, "Kesehatan, obat-obatan dan kosmetik");
        mapkategori.put(4, "Kerajinan dan industri rumah tangga");
        mapkategori.put(5, "Kehutanan dan lingkungan hidup");
        mapkategori.put(6, "Sosial dan budaya");
        mapkategori.put(7, "Kelautan dan perikanan");
        mapkategori.put(8, "Pendidikan");
        mapkategori.put(9, "Energi");
        mapkategori.put(10, "Pariwisata");
        mapkategori.put(11, "OPD");
    }

    public static final Map<Integer, Integer> mapkategoriimage;
    static
    {
        mapkategoriimage = new HashMap<Integer, Integer>();
        mapkategoriimage.put(1, R.drawable.back1);
        mapkategoriimage.put(2, R.drawable.back2);
        mapkategoriimage.put(3, R.drawable.back3);
        mapkategoriimage.put(4, R.drawable.back4);
        mapkategoriimage.put(5, R.drawable.back5);
        mapkategoriimage.put(6, R.drawable.back6);
        mapkategoriimage.put(7, R.drawable.back7);
        mapkategoriimage.put(8, R.drawable.back8);
        mapkategoriimage.put(9, R.drawable.back9);
        mapkategoriimage.put(10, R.drawable.back10);
        mapkategoriimage.put(11, R.drawable.back11);
    }

    public static final Map<Integer, String> mapstatus;
    static
    {
        mapstatus = new HashMap<Integer, String>();
        mapstatus.put(1, "Belum diverifikasi");
        mapstatus.put(2, "Terverifikasi");
        mapstatus.put(3, "Ditolak");
    }
 }
