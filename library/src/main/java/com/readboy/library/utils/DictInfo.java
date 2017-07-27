package com.readboy.library.utils;


/**
 * Created by Administrator on 14-7-22.
 */
public class DictInfo {

    /*---------------------------------------------
    * 每本词典ID
    * ---------------------------------------------
    */
    public final static int DICT_BIHUA             = 0; // 笔画
    public final static int DICT_HANCHENG          = 1; // 汉成
    public final static int DICT_LANGWEN           = 2; // 朗文
    public final static int DICT_DONGMAN           = 3; // 动漫
    public final static int DICT_GUHAN             = 4; // 古汉
    public final static int DICT_HANYING           = 5; // 汉英
    public final static int DICT_XIANHAN           = 6; // 现汉
    public final static int DICT_JIANHAN           = 7; // 简汉
    public final static int DICT_XUEHAN            = 8; // 学汉
    public final static int DICT_YINGHAN           = 9; // 英汉
    public final static int DICT_YINGYING          = 10;// 英英
    public final static int DICT_SHENGCI           = 11;// 生词

    public final static int[] EXPLAIN_COPY_KEY_ID = {		// 显示解释时需要复制关键字的字典
            DictInfo.DICT_GUHAN, DictInfo.DICT_XIANHAN, DictInfo.DICT_HANCHENG, DictInfo.DICT_YINGHAN
    };
}
