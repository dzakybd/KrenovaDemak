package com.baddude.krenovademak.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.baddude.krenovademak.R;
import com.baddude.krenovademak.model.MediaModel;
import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

/**
 * Created by zaki on 20/11/17.
 */

public class MediaRecyclerView extends AbstractItem<MediaRecyclerView, MediaRecyclerView.ViewHolder> {

    public MediaModel mediaModel;

    public MediaRecyclerView create(MediaModel mediaModel){
       this.mediaModel = mediaModel;
       return this;
    }

    @Override
    public int getType() {
        return mediaModel.idmedia;
    }


    @Override
    public int getLayoutRes() {
        return R.layout.media_item;
    }

    @Override
    public void bindView(MediaRecyclerView.ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);

        //get the context
        Context ctx = viewHolder.itemView.getContext();
        Glide.with(ctx).load(mediaModel.urlthumb).into(viewHolder.thumbnail);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.thumbnail.setImageDrawable(null);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    /**
     * our ViewHolder
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView thumbnail;

        public ViewHolder(View view) {
            super(view);
            thumbnail=view.findViewById(R.id.thumbnail);
        }
    }
}