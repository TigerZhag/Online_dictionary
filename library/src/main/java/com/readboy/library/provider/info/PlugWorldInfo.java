package com.readboy.library.provider.info;

import android.os.Parcel;
import android.os.Parcelable;

import com.readboy.depict.model.HanziInfo;

import java.util.List;

/**
 * Created by Administrator on 15-4-15.
 */
public class PlugWorldInfo implements Comparable<PlugWorldInfo> {

    public int order;
    public int primaryID = 0;
    public int dictID = -1;
    public String world;
    /**
     * 如果是多音字，不用设置
     */
    public int index;
    /**
     *
     * 多音字
     *
     */
    public List<HanziInfo> polyphone;
    public byte[] data;

    @Override
    public int compareTo(PlugWorldInfo another) {
        if (this.order > another.order) {
            return -1;
        } else if (this.order < another.order) {
            return 1;
        }
        return 0;
    }
}
