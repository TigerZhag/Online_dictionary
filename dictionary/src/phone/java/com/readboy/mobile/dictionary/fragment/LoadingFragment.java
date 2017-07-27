package com.readboy.mobile.dictionary.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.readboy.Dictionary.R;

/**
 * Created by 123 on 2016/8/24.
 */

public class LoadingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_loading, null);
        ImageView loading = (ImageView) root.findViewById(R.id.fragment_loading_image);
        AnimationDrawable animationDrawable = (AnimationDrawable) loading.getBackground();
        animationDrawable.start();
        return root;
    }

}
