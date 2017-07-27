package com.readboy.library.widget;

/**
 * Created by Sgc on 2015/6/17.
 */
class ParaseWorker extends Thread implements ISpanParaseObtainer<Object> {

    private TextScrollView.TextHandler textHandler;
    private IParaser spanParase;
    private Object tag;

    public ParaseWorker(IParaser spanParase) {
        this.spanParase = spanParase;
    }

    public IParaser getParase() {
        return spanParase;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Object getTag() {
        return tag;
    }

    @Override
    public void setListener(Object obj) {
        if (obj instanceof TextScrollView.TextHandler) {
            this.textHandler = (TextScrollView.TextHandler)obj;
        } else {
            this.textHandler = null;
        }
    }

    @Override
    public void getParaseResult(Object... objects) {
        if (textHandler != null && objects != null) {
            if (objects.length == 4 && objects[0] instanceof String
                    && objects[2] instanceof Float && objects[3] instanceof Float) {
                textHandler.sendMessages(TextScrollView.TextHandler.PARARSE_CHARS,
                        (String)objects[0], objects[1], ((Float)objects[2]).intValue(), ((Float)objects[3]).intValue(), 0);
            }
        }
    }

    @Override
    public boolean isStop() {
        return false;
    }

    @Override
    public void run() {
        if (spanParase != null) {
            spanParase.setCharsParaseObtainer(this);
            spanParase.start();
            if (textHandler != null) {
                textHandler.sendMessages(TextScrollView.TextHandler.PARARSE_END, this.toString(), null, 0, 0, 0);
            }
            spanParase.setCharsParaseObtainer(null);
        }
        textHandler = null;
    }
}
