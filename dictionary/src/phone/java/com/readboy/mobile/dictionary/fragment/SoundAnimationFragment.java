package com.readboy.mobile.dictionary.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.readboy.Dictionary.R;
import com.readboy.mobile.dictionary.control.DictManagerControler;
import com.readboy.mobile.dictionary.view.CustomAnimationDrawable;

/**
 * Created by Senny on 2015/11/11.
 */
public class SoundAnimationFragment extends Fragment {

    public static void show(FragmentManager fragmentManager) {
        DictManagerControler.addStackFragmentWithAnim(fragmentManager.beginTransaction(),
                R.id.fragment_main_extend_sound_animation,
                SoundAnimationFragment.class.getSimpleName(),
                SoundAnimationFragment.class.getSimpleName(), new SoundAnimationFragment(),
                0, 0, 0, R.anim.drop_mini_down);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sound_animation, null);
        final View animationView = root.findViewById(R.id.fragment_sound_animation_view);
        animationView.post(new Runnable() {
            @Override
            public void run() {
                final CustomAnimationDrawable customAnimationDrawable = new CustomAnimationDrawable((AnimationDrawable) getResources().getDrawable(R.drawable.animation_playing_sound));
                customAnimationDrawable.setOnEndCustomAnimatoinListener(new CustomAnimationDrawable.OnEndCustomAnimatoinListener() {
                    @Override
                    public void endFrame() {
//                        animationView.setVisibility(View.GONE);
                        customAnimationDrawable.stop();
                        getFragmentManager().popBackStack();

                    }
                });
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    animationView.setBackground(customAnimationDrawable);
                } else {
                    animationView.setBackgroundDrawable(customAnimationDrawable);
                }
                customAnimationDrawable.start();
            }
        });
        return root;
    }
}
