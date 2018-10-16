package com.bobby.coding.detail;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bobby.coding.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.wang.avi.AVLoadingIndicatorView;



public class ImageFragment extends Fragment {
    private SubsamplingScaleImageView collapsingImage;
    private AVLoadingIndicatorView progressBar;
    public ImageFragment() { }
    Listener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_image, container, false);
        renderView(v);
        return  v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int position=listener.ImageSelected();
        setImage(position);

    }

    private void renderView(View v){
        progressBar = v.findViewById(R.id.progressimage);
        collapsingImage = v.findViewById(R.id.collapsingimage);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (DetailActivity) activity;
        } catch (ClassCastException e) { }
    }

    @Override
    public void onDetach() {
        listener = null; // => avoid leaking, thanks @Deepscorn
        super.onDetach();
    }

    interface Listener {
        int ImageSelected();
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    public void removeWait() {
        progressBar.setVisibility(View.GONE);
    }

    public void setImage(int position){

        if(getActivity()!=null)
        Glide.with(this)
                .asBitmap()
                .load(((DetailActivity)getActivity()).data.get(position).getUrl())
                .apply(new RequestOptions().error(R.drawable.no_image))
                 .into(new SimpleTarget<Bitmap>() {

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                  removeWait();
                  Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_image);
                  collapsingImage.setImage(ImageSource.bitmap(bitmap));
            }

            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                collapsingImage.setImage(ImageSource.bitmap(resource));
                removeWait();

            }
        });
    }
}
