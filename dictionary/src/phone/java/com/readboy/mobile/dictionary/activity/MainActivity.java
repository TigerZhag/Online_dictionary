package com.readboy.mobile.dictionary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.readboy.Dictionary.R;
import com.readboy.HanziManager.HanziDataManager;
import com.readboy.depict.data.DictData;
import com.readboy.mobile.dictionary.utils.CheckHanziData;

/**
 * Created by Senny on 2015/10/26.
 */
public class MainActivity extends FragmentActivity {

    private final int STATE_START = 0x01;
    private final int STATE_START_ANIMATION = STATE_START + 1;
    private final int STATE_FINISH = STATE_START_ANIMATION + 2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            if (msg != null) {
                switch (msg.what) {

                    case STATE_START:
                        startActivity(intent);
                        finish();
                        break;

                    case STATE_START_ANIMATION:
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in, R.anim.fake_anim);
                        sendEmptyMessageDelayed(STATE_FINISH, 400);
                        break;

                    case STATE_FINISH:
                        finish();
                        break;
                }
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final DictData dictData = new DictData(this);
        String hanziversion = dictData.getHanziversion();
        if (hanziversion == null ||
                HanziDataManager.getVersion(this) == null ||
                !hanziversion.equals(HanziDataManager.getVersion(this))) {

            /*Intent intent = new Intent();
            intent.setAction(HanziDataService.ACTION_UPDATE);
            intent.setPackage(getPackageName());
            startService(intent);*/
            if (CheckHanziData.getInstance() != null) {
                CheckHanziData.getInstance().check();
            }

            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(STATE_START_ANIMATION, 1500);
            }
            setContentView(R.layout.activity_start_up);
        } else {
            if (mHandler != null) {
                mHandler.sendEmptyMessage(STATE_START);
            }
        }
    }

    @Override
    public void finish() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        super.finish();
    }
}
