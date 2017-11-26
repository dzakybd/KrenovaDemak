package com.baddude.krenovademak.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baddude.krenovademak.R;
import com.baddude.krenovademak.model.AkunModel;
import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

/**
 * Created by zaki on 20/11/17.
 */

public class AkunRecyclerView extends AbstractItem<AkunRecyclerView, AkunRecyclerView.ViewHolder> {

    public AkunModel akunModel;

    public AkunRecyclerView create(AkunModel akunModel){
       this.akunModel = akunModel;
       return this;
    }

    @Override
    public int getType() {
        return akunModel.idakun;
    }


    @Override
    public int getLayoutRes() {
        return R.layout.akun_item;
    }

    @Override
    public void bindView(AkunRecyclerView.ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);

        //get the context
        Context ctx = viewHolder.itemView.getContext();
        //define our data for the view
        viewHolder.tvemail.setText(akunModel.email);
        viewHolder.tvnama.setText(akunModel.nama);
        Glide.with(ctx).load(akunModel.foto).into(viewHolder.imageprofile);

    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.imageprofile.setImageDrawable(null);
        holder.tvemail.setText(null);
        holder.tvnama.setText(null);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    /**
     * our ViewHolder
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageprofile;
        protected TextView tvnama,tvemail;

        public ViewHolder(View view) {
            super(view);
            tvnama=view.findViewById(R.id.tvnama);
            tvemail=view.findViewById(R.id.tvemail);
            imageprofile=view.findViewById(R.id.imageprofile);
        }
    }
}