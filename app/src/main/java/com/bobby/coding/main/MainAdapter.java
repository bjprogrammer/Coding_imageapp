package com.bobby.coding.main;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewGroupCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bobby.coding.model.Upload;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bobby.coding.R;
import com.wang.avi.AVLoadingIndicatorView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private final OnItemClickListener listener;
    private final List<Upload> data;
    private final Context context;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    public MainAdapter(Context context, List<Upload> data, OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MainAdapter.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, null);
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            vh = new MainAdapter.ViewHolder(view);

        }
        else  {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store3, null);
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            vh = new MainAdapter.ViewHolder(view);
        }
        ViewGroupCompat.setTransitionGroup(parent,false);

            ((MainActivity) context).startPostponedEnterTransition();
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public void onBindViewHolder(final MainAdapter.ViewHolder holder, int position) {
        if( holder.getItemViewType()== VIEW_ITEM) {
            holder.click(data.get(position), listener);
            String images = data.get(position).getUrl();

            holder.spinner.setIndicatorColor(Color.WHITE);
            holder.spinner.show();
            holder.spinner.setVisibility(View.VISIBLE);
            holder.setIsRecyclable(false);

            Glide.with(context)
                    .load(images)
                    .thumbnail(0.1f)
                    .apply(new RequestOptions().error(R.drawable.no_image))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.spinner.hide();
                            holder.spinner.setVisibility(View.GONE);
                            holder.background.setScaleType(ImageView.ScaleType.FIT_XY);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.spinner.hide();
                            holder.spinner.setVisibility(View.GONE);
                            holder.background.setScaleType(ImageView.ScaleType.CENTER);

                            return false;
                        }
                    })
                    .into(holder.background);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.background.setTransitionName("transition" + position);
            }

        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnItemClickListener {
        void onClick(Upload Item, View v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView background;
        private AVLoadingIndicatorView spinner;

        public ViewHolder(View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.img_card_main);
            spinner = itemView.findViewById(R.id.card_spinner);
        }

        public void click(final Upload storeListData, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(storeListData,v);
                }
            });
        }
    }
}