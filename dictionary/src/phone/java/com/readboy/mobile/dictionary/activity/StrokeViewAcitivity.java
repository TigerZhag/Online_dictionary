package com.readboy.mobile.dictionary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.readboy.Dictionary.R;
import com.readboy.depict.model.HanziInfo;
import com.readboy.library.io.DictIOFile;
import com.readboy.library.search.DictWordSearch;
import com.readboy.mobile.dictionary.control.DictManagerControler;
import com.readboy.mobile.dictionary.fragment.DictStrokeViewFragment;

/**
 * Created by 123 on 2016/8/23.
 */

public class StrokeViewAcitivity extends FragmentActivity {

    public final static String EXTRA_WORLD = "world";
    public final static String EXTRA_SOUND_BYTES = "sound_bytes";
    public final static String EXTRA_PHONETIC = "phonetic";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_acitivity_stroke_view);

        Intent intent = getIntent();
        if (intent != null) {
            String world = intent.getStringExtra(EXTRA_WORLD);
            byte[] soundBytes = intent.getByteArrayExtra(EXTRA_SOUND_BYTES);
            String phonetic = intent.getStringExtra(EXTRA_PHONETIC);
            if (world != null && soundBytes != null && phonetic != null) {
                DictWordSearch dictWordSearch = new DictWordSearch();
                if (dictWordSearch.open(DictIOFile.ID_BHDICT, this, null)) {
                    DictStrokeViewFragment dictStrokeViewFragment = new DictStrokeViewFragment();
                    dictWordSearch.getHanziPackage().setHanzi(world, soundBytes);
                    HanziInfo hanziDataInfo = dictWordSearch.getHanziPackage().getHanziInfo();
                    hanziDataInfo.phonetic = phonetic;
                    dictStrokeViewFragment.setStrokePackage(dictWordSearch.getHanziPackage());
                    DictManagerControler.replaceFragment(getSupportFragmentManager().beginTransaction(),
                            R.id.activity_stroke_view,
                            DictStrokeViewFragment.class.getSimpleName(),
                            DictStrokeViewFragment.class.getSimpleName(),
                            dictStrokeViewFragment);
                }
            }
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.right_out);
    }
}
