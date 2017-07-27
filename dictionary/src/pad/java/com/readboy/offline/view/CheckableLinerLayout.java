package com.readboy.offline.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class CheckableLinerLayout extends LinearLayout implements Checkable
{
    private static final int[] STATE_CHECKABLE = {android.R.attr.state_checked};
    private boolean mChecked;

    @SuppressWarnings("unused")
    public CheckableLinerLayout(Context context)
    {
        super(context);
    }

    @SuppressWarnings("unused")
    public CheckableLinerLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("unused")
    public CheckableLinerLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public void toggle()
    {
        setChecked(!mChecked);
    }

    @Override
    public boolean isChecked()
    {
        return mChecked;
    }

    @Override
    public void setChecked(boolean checked)
    {
        if (mChecked != checked)
        {
            mChecked = checked;
        }
        refreshDrawableState();

        View v;
        for (int i = 0; i < getChildCount(); i++)
        {
            v = getChildAt(i);
            if (v instanceof Checkable)
            {
                ((Checkable) v).setChecked(mChecked);
                v.setSelected(mChecked);
            }
        }
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace)
    {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked())
        {
            mergeDrawableStates(drawableState, STATE_CHECKABLE);
        }
        return drawableState;
    }
}
