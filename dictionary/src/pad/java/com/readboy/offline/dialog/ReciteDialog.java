package com.readboy.offline.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ListView;
import android.widget.Toast;

import com.readboy.Dictionary.R;

public class ReciteDialog extends Dialog
        implements AdapterView.OnItemClickListener, View.OnClickListener
{
    private String[] mWords;
    private ListView mWordList;
    private CheckBox mSelectAll;

    public ReciteDialog(Context context, String[] words)
    {
        super(context, R.style.dialog);
        mWords = words;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_recite);

        mWordList = (ListView) findViewById(R.id.recite_list);
        mWordList.setAdapter(new ArrayAdapter<String>(getContext(),
                R.layout.item_list_recite_word, mWords));
        mWordList.setOnItemClickListener(this);

        mSelectAll = (CheckBox) findViewById(R.id.select_all);
        mSelectAll.setOnClickListener(this);
        findViewById(R.id.start_recite).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.cancel)
        {
            dismiss();
        }
        else if (id == R.id.start_recite)
        {
            @SuppressWarnings("deprecation")
            long checkedIds[] = mWordList.getCheckItemIds(); // 只有这个方法有效

            if (checkedIds.length == 0)
            {
                Toast.makeText(getContext(),
                        R.string.null_word_select, Toast.LENGTH_SHORT).show();
                return;
            }
            String[] words = new String[checkedIds.length];
            for (int i = 0; i < words.length; i++)
            {
                words[i] = mWords[(int) checkedIds[i]];
            }

            getContext().startActivity(new Intent("readboy.intent.action.RECITE")
                    .putExtra("words", words)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        else if (id == R.id.select_all)
        {
            @SuppressWarnings("deprecation")
            boolean isChecked = mWordList.getCheckItemIds().length < mWords.length;
            for (int i = 0; i < mWords.length; i++)
            {
                mWordList.setItemChecked(i, isChecked);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (view instanceof Checkable)
        {
            if (((Checkable) view).isChecked())
            {

                if (mWordList.getCheckItemIds().length == mWords.length)
                {
                    mSelectAll.setChecked(true);
                }
            }
            else
            {
                mSelectAll.setChecked(false);
            }
        }
    }
}
