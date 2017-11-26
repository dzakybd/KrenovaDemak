package com.baddude.krenovademak.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baddude.krenovademak.R;
import com.baddude.krenovademak.model.PengumumanModel;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

/**
 * Created by zaki on 20/11/17.
 */

public class PengumumanRecyclerView extends AbstractItem<PengumumanRecyclerView, PengumumanRecyclerView.ViewHolder> {

    public PengumumanModel pengumumanModel;

    public PengumumanRecyclerView create(PengumumanModel pengumumanModel){
       this.pengumumanModel = pengumumanModel;
       return this;
    }

    @Override
    public int getType() {
        return pengumumanModel.idpengumuman;
    }


    @Override
    public int getLayoutRes() {
        return R.layout.pengumuman_item;
    }

    @Override
    public void bindView(PengumumanRecyclerView.ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);

        //get the context
        Context ctx = viewHolder.itemView.getContext();

        //define our data for the view
        viewHolder.tvjudul.setText(pengumumanModel.judul);
        viewHolder.tvdeskripsi.setText(pengumumanModel.deskripsi);
        viewHolder.tvtanggal.setText("{cmd-calendar-plus} "+pengumumanModel.tanggalbuat);
        viewHolder.buttonbaca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.tvdeskripsi.setText(null);
        holder.tvjudul.setText(null);
        holder.tvtanggal.setText(null);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    /**
     * our ViewHolder
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvjudul,tvdeskripsi,tvtanggal;
        protected Button buttonbaca;

        public ViewHolder(View view) {
            super(view);
            tvjudul=view.findViewById(R.id.tvjudul);
            tvdeskripsi=view.findViewById(R.id.tvdeskripsi);
            tvtanggal=view.findViewById(R.id.tvtanggal);
            buttonbaca=view.findViewById(R.id.buttonbaca);
        }
    }
}