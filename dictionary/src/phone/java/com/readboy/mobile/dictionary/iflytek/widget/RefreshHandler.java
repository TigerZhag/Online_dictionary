/*
package com.readboy.lee.api;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

*/
/**
 * Created by lee on 3/20/14.
 *//*

public class RefreshHandler extends Handler {
    public RefreshHandler(Looper looper){
        super(looper);
    }

    public RefreshHandler(){
    }

    public void sendMessage(int what,int arg1){
        this.removeMessages(what);
        Message msg = this.obtainMessage(what);
        msg.arg1 = arg1;
        this.sendMessage(msg);
    }

    public void sendMessage(int what){
        this.removeMessages(what);
        Message msg = this.obtainMessage(what);
        this.sendMessage(msg);
    }

    public void sendMessage(int what,Object obj){
        this.removeMessages(what);
        Message msg = this.obtainMessage(what);
        msg.obj= obj;
        this.sendMessage(msg);
    }

    public void sendMessageDelayed(int what,Object obj,int delay){
        this.removeMessages(what);
        Message msg = this.obtainMessage(what);
        msg.obj= obj;
        this.sendMessageDelayed(msg,delay);
    }

    public void cancelAll(){
        this.removeCallbacksAndMessages(null);
    }
}
*/
package com.readboy.mobile.dictionary.iflytek.widget;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by lee on 3/20/14.
 */
public class RefreshHandler extends Handler {
    public RefreshHandler(Looper looper){
        super(looper);
    }

    public RefreshHandler(){

    }

    public void sendMessage(int what,int arg1){
        this.removeMessages(what);
        Message msg = this.obtainMessage(what);
        msg.arg1 = arg1;
        this.sendMessage(msg);
    }

    public void sendMessage(int what,int arg1,int arg2){
        this.removeMessages(what);
        Message msg = this.obtainMessage(what);
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        this.sendMessage(msg);
    }

    public void sendMessage(int what,int arg1,Object obj){
        this.removeMessages(what);
        Message msg = this.obtainMessage(what);
        msg.arg1 = arg1;
        msg.obj = obj;
        this.sendMessage(msg);
    }

    public void sendMessage(int what,int arg1,int arg2,Object o){
        this.removeMessages(what);
        Message msg = this.obtainMessage(what);
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = o;
        this.sendMessage(msg);
    }


    public void sendMessage(int what){
        this.removeMessages(what);
        Message msg = this.obtainMessage(what);
        this.sendMessage(msg);
    }

    public void sendMessage(int what,Object obj){
        this.removeMessages(what);
        Message msg = this.obtainMessage(what);
        msg.obj= obj;
        this.sendMessage(msg);
    }

    public void sendMessageDelayed(int what,Object obj,int delay){
        this.removeMessages(what);
        Message msg = this.obtainMessage(what);
        msg.obj= obj;
        this.sendMessageDelayed(msg,delay);
    }

    public void cancelAll(){
        this.removeCallbacksAndMessages(null);
    }
}
