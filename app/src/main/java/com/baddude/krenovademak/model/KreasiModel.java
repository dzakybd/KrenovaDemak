package com.baddude.krenovademak.model;

import org.parceler.Parcel;

/**
 * Created by zaki on 20/11/17.
 */

@Parcel
public class KreasiModel {
    public int idkreasi;
    public String judul;
    public String deskripsi;
    public int fkidakun;
    public String namaakun;
    public String foto;
    public String fotothumb;
    public String tanggalbuat;
    public int likes;
    public boolean isulike;
    public int status; //-1 block, 0 blm ver, 1 ver
    public MediaModel[] mediaModels;
    public int kategori;
}
