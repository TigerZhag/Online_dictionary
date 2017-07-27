package com.readboy.mobile.dictionary.iflytek.widget;


import android.os.Handler;

/**
 * Created by adapter on 2014/5/28.
 */
public class LoopHandler extends Handler {

    private int MSG_CYCLE= 0x255;
    private boolean loop = true;
    public static int DEFAULT_DELAY = 50;
    private int msg_delay = DEFAULT_DELAY;

    public LoopHandler(boolean loop){
        this(loop,DEFAULT_DELAY);
    }

    public LoopHandler(boolean loop, int delay){
        this.loop = loop;
        msg_delay = delay;
        start();
    }

    public void start(){
        update();
    }

    public void startLoop(){
        loop = true;
        update();
    }

    public void stop(){
        loop = false;
        this.removeMessages(MSG_CYCLE);
    }

    protected void update(){
        if(loop) {
            this.removeMessages(MSG_CYCLE);
            this.sendMessageDelayed(obtainMessage(MSG_CYCLE), msg_delay);
        }
    }

    @Override
    public void handleMessage(android.os.Message msg){
        super.handleMessage(msg);
        if(msg.what == MSG_CYCLE){
            update();
        }
    }
}
