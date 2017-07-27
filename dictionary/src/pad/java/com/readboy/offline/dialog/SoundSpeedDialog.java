package com.readboy.offline.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.readboy.Dictionary.R;

/**
 * Created by Administrator on 14-7-29.
 */
public class SoundSpeedDialog extends Dialog {

    private Context mContext = null;
    private SeekBar mSeekBar = null;
    private int mAudioRate = 0;

    public SoundSpeedDialog(Context context) {
        super(context);
    }

    public SoundSpeedDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    protected SoundSpeedDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void init(final Context mContext) {
        this.mContext = mContext;
        View audioSpeedView = View.inflate(this.mContext, R.layout.dialog_audio_speed, null);
        mSeekBar = (SeekBar) audioSpeedView.findViewById(R.id.audio_speed_progress);
//        mSeekBar.setOnSeekBarChangeListener(this);
        this.addContentView(audioSpeedView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true);

        ((ImageButton)audioSpeedView.findViewById(R.id.audio_speed_sure)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mAudioRate = mSeekBar.getProgress()*10-100;
                SoundSpeedDialog.this.dismiss();
            }
        });
        ((ImageButton)audioSpeedView.findViewById(R.id.audio_speed_cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                SoundSpeedDialog.this.dismiss();
            }
        });
    }

    public int getAudioRate() {
        return mAudioRate;
    }
}
