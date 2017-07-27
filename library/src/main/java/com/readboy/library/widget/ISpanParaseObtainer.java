package com.readboy.library.widget;

/**
 * Created by Sen on 2015/6/17.
 */
interface ISpanParaseObtainer<K> {

    public void setListener(K k);
    public void getParaseResult(K... k);
    public boolean isStop();

}
