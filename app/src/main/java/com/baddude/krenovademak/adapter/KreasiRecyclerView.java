package com.baddude.krenovademak.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baddude.krenovademak.R;
import com.baddude.krenovademak.model.KreasiModel;
import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.utils.EventHookUtil;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;

/**
 * Created by zaki on 20/11/17.
 */

public class KreasiRecyclerView extends AbstractItem<KreasiRecyclerView, KreasiRecyclerView.ViewHolder> {

    public KreasiModel kreasiModel;

    public KreasiRecyclerView create(KreasiModel kreasiModel){
       this.kreasiModel = kreasiModel;
       return this;
    }
    
    @Override
    public int getType() {
        return kreasiModel.idkreasi;
    }


    @Override
    public int getLayoutRes() {
        return R.layout.kreasi_item;
    }


    @Override
    public void bindView(KreasiRecyclerView.ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);

        //get the context
        Context ctx = viewHolder.itemView.getContext();

        //define our data for the view
        viewHolder.imageName.setText(kreasiModel.judul);
        viewHolder.imageDescription.setText(kreasiModel.tanggalbuat);
        viewHolder.imageView.setImageBitmap(null);
        viewHolder.numlikes.setText(String.valueOf(kreasiModel.likes));

        style(viewHolder.imageLovedOn, kreasiModel.isulike ? 1 : 0);
        style(viewHolder.imageLovedOff, kreasiModel.isulike ? 0 : 1);
        //load glide
        Glide.with(ctx).load(kreasiModel.fotothumb).into(viewHolder.imageView);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.imageView.setImageDrawable(null);
        holder.imageDescription.setText(null);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }


    private void style(View view, int value) {
        view.setScaleX(value);
        view.setScaleY(value);
        view.setAlpha(value);
    }

    public void setlike(TextView numlikes,int likes){
        numlikes.setText(String.valueOf(likes));
    }

    public void animateHeart(View imageLovedOn, View imageLovedOff, boolean on) {
        imageLovedOn.setVisibility(View.VISIBLE);
        imageLovedOff.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            viewPropertyStartCompat(imageLovedOff.animate().scaleX(on ? 0 : 1).scaleY(on ? 0 : 1).alpha(on ? 0 : 1));
            viewPropertyStartCompat(imageLovedOn.animate().scaleX(on ? 1 : 0).scaleY(on ? 1 : 0).alpha(on ? 1 : 0));
        }
    }

    public static void viewPropertyStartCompat(ViewPropertyAnimator animator) {
        if (Build.VERSION.SDK_INT >= 14) {
            animator.start();
        }
    }

    public static class ImageItemHeartClickEvent extends ClickEventHook<KreasiRecyclerView> {
        @Nullable
        @Override
        public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof KreasiRecyclerView.ViewHolder) {
                return EventHookUtil.toList(((ViewHolder) viewHolder).imageLovedContainer);
            }
            return super.onBindMany(viewHolder);
        }

        @Override
        public void onClick(View v, int position, FastAdapter<KreasiRecyclerView> fastAdapter, KreasiRecyclerView item) {
            item.kreasiModel.isulike=!item.kreasiModel.isulike;
            if(item.kreasiModel.isulike)item.kreasiModel.likes++;
            else item.kreasiModel.likes--;
            item.setlike((TextView)((ViewGroup) v).getChildAt(2),item.kreasiModel.likes);
            //we animate the heart
            item.animateHeart(((ViewGroup) v).getChildAt(0), ((ViewGroup) v).getChildAt(1), item.kreasiModel.isulike);

        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected View view;
        protected ImageView imageView;
        protected TextView imageName,imageDescription,numlikes;
        public RelativeLayout imageLovedContainer;
        protected IconicsImageView imageLovedOn;
        protected IconicsImageView imageLovedOff;

        public ViewHolder(View view) {
            super(view);
            imageLovedOff = view.findViewById(R.id.item_image_loved_no);
            imageLovedOn = view.findViewById(R.id.item_image_loved_yes);
            imageLovedContainer = view.findViewById(R.id.item_image_loved_container);
            imageDescription = view.findViewById(R.id.item_image_description);
            imageName = view.findViewById(R.id.item_image_name);
            imageView = view.findViewById(R.id.item_image_img);
            numlikes = view.findViewById(R.id.numlikes);
            this.view = view;

            //optimization to preset the correct height for our device
            int columns = 1;
            int screenWidth = view.getContext().getResources().getDisplayMetrics().widthPixels;
            int finalHeight = (int) (screenWidth / 1.5);
            imageView.setMinimumHeight(finalHeight / columns);
            imageView.setMaxHeight(finalHeight / columns);
            imageView.setAdjustViewBounds(false);
            //set height as layoutParameter too
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) imageView.getLayoutParams();
            lp.height = finalHeight / columns;
            imageView.setLayoutParams(lp);
        }
    }
}