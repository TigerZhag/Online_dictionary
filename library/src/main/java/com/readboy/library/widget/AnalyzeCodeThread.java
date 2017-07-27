package com.readboy.library.widget;

import com.readboy.library.widget.analy.ImlAnalyze;

class AnalyzeCodeThread extends Thread {

    /**
     * 线程是否开始
     */
    public boolean isHasStart = false;

    /**
     * 监听数据解析的监听器
     */
    private ImlAnalyze imlAnalyze = null;

    /**
     * 等待锁
     */
    private byte[] obj = new byte[1];
    private boolean isWait = false;

    public void onPause() {
        this.isWait = true;
    }

    /**
     * 唤醒线程
     */
    public void onResume() {
        this.isWait = false;
        if (obj != null) {
            synchronized (obj) {
                obj.notify();
            }
        }
    }

    public AnalyzeCodeThread(ImlAnalyze imlAnalyze){
        this.imlAnalyze = imlAnalyze;
    }

    public void setStop() {
        if (imlAnalyze != null) {
            imlAnalyze.setObtainer(null);
            imlAnalyze.stop();
        }
    }

    public ImlAnalyze getImlAnalyze() {
        return imlAnalyze;
    }

    @Override
    public void start() {
        this.isHasStart = true;
        super.start();
    }

    @Override
    public void run() {
//        Log.i("Dictionary", "Analyze start !");
        if (imlAnalyze != null) {
            imlAnalyze.start();
            imlAnalyze.stop();
            imlAnalyze.clear();
        }
        imlAnalyze = null;
        obj = null;
//        System.out.println("NewAnalyzeDictThread ... end "+isStop);
//        Log.i("Dictionary", "Analyze end !");
    }

    public void haveARest() {
        try {
            Thread.currentThread().sleep(200);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (isWait) {
            synchronized (obj) {
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
