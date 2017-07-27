package com.readboy.online.model.data;

/**
 * Created by liujiawei on 16-7-11.
 */
public class StroksResultData {

    private String bushou;
    private boolean isGroup;
    private PinYinAndZi point;

    public String getBushou() {
        return bushou;
    }

    public void setBushou(String bushou) {
        this.bushou = bushou;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public PinYinAndZi getPoint() {
        return point;
    }

    public void setPoint(PinYinAndZi point) {
        this.point = point;
    }

}
